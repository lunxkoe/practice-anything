package lunxkoe.practice.security.usersession.registry.impl;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lunxkoe.practice.security.usersession.dto.UserSession;
import lunxkoe.practice.security.usersession.exception.RefreshTokenReusedException;
import lunxkoe.practice.security.usersession.exception.UserSessionExpiredException;
import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.ExpirationPolicy;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.RedisScript;

public class UserSessionRedisRegistry implements UserSessionRegistry {

  private static final String SESSION_KEY_PREFIX = "auth:user-session:";
  private static final String INDEX_KEY_PREFIX = "auth:user-session-index:";

  private static final String FIELD_REFRESH_JTI = "refreshJti";
  private static final String FIELD_ISSUED_AT = "issuedAt";

  private static final RedisScript<Long> REPLACE_ALL_SCRIPT = RedisScript.of(
      new ClassPathResource("scripts/replace-all-user-sessions.lua"), Long.class);
  private static final RedisScript<Long> COMPARE_AND_ROTATE_SCRIPT = RedisScript.of(
      new ClassPathResource("scripts/compare-and-rotate.lua"), Long.class);
  private static final RedisScript<Long> REVOKE_SESSION_SCRIPT = RedisScript.of(
      new ClassPathResource("scripts/revoke-session.lua"), Long.class);
  private static final RedisScript<Long> REVOKE_ALL_SESSIONS_SCRIPT = RedisScript.of(
      new ClassPathResource("scripts/revoke-all-sessions.lua"), Long.class);
  private static final RedisScript<Long> SAVE_EVICTING_OLDEST_SCRIPT = RedisScript.of(
      new ClassPathResource("scripts/save-evicting-oldest.lua"), Long.class);

  private final StringRedisTemplate redisTemplate;
  private final HashOperations<String, String, String> hashOperations;
  private final ZSetOperations<String, String> zSetOperations;
  private final ExpirationPolicy expirationPolicy;
  private final ConcurrentPolicy concurrentPolicy;
  private final Clock clock;

  public UserSessionRedisRegistry(StringRedisTemplate redisTemplate,
      ExpirationPolicy expirationPolicy, ConcurrentPolicy concurrentPolicy, Clock clock) {
    this.redisTemplate = redisTemplate;
    this.hashOperations = redisTemplate.opsForHash();
    this.zSetOperations = redisTemplate.opsForZSet();
    this.expirationPolicy = expirationPolicy;
    this.concurrentPolicy = concurrentPolicy;
    this.clock = clock;
  }

  @Override
  public ExpirationPolicy expirationPolicy() {
    return expirationPolicy;
  }

  @Override
  public ConcurrentPolicy concurrentPolicy() {
    return concurrentPolicy;
  }

  @Override
  public UserSession save(UserSession newSession, Instant expiresAt) {
    String sessionKey = sessionKey(newSession.userId(), newSession.sessionId());
    String indexKey = indexKey(newSession.userId());

    Map<String, String> fields = Map.of(
        FIELD_REFRESH_JTI, newSession.currentRefreshJti().toString(),
        FIELD_ISSUED_AT, String.valueOf(newSession.issuedAt().toEpochMilli())
    );

    redisTemplate.execute(
        new SessionCallback<Object>() {
          @Override
          public Object execute(RedisOperations operations) {
            operations.multi();
            operations.opsForHash().putAll(sessionKey, fields);
            operations.expireAt(sessionKey, expiresAt);
            operations.opsForZSet()
                .add(indexKey, newSession.sessionId().toString(), expiresAt.toEpochMilli());
            return operations.exec();
          }
        }
    );

    extendIndexExpiryIfNeeded(indexKey, expiresAt);

    return newSession;
  }

  @Override
  public UserSession replaceAll(UserSession newSession, Instant expiresAt) {
    String indexKey = indexKey(newSession.userId());
    String newSessionKey = sessionKey(newSession.userId(), newSession.sessionId());

    redisTemplate.execute(REPLACE_ALL_SCRIPT,
        List.of(indexKey, newSessionKey),
        sessionKeyPrefix(newSession.userId()),
        newSession.sessionId().toString(),
        newSession.currentRefreshJti().toString(),
        String.valueOf(newSession.issuedAt().toEpochMilli()),
        String.valueOf(expiresAt.toEpochMilli()));

    return newSession;
  }

  @Override
  public UserSession saveEvictingOldest(UserSession newSession, Instant expiresAt, int maxDevices) {
    String indexKey = indexKey(newSession.userId());
    String newSessionKey = sessionKey(newSession.userId(), newSession.sessionId());

    redisTemplate.execute(SAVE_EVICTING_OLDEST_SCRIPT,
        List.of(indexKey, newSessionKey),
        sessionKeyPrefix(newSession.userId()),
        String.valueOf(maxDevices),
        newSession.sessionId().toString(),
        newSession.currentRefreshJti().toString(),
        String.valueOf(newSession.issuedAt().toEpochMilli()),
        String.valueOf(expiresAt.toEpochMilli()));

    return newSession;
  }

  @Override
  public UserSession compareAndRotate(UUID userId, UUID sessionId, UUID expectedRefreshJti, UUID newRefreshJti, Instant issuedAt, Instant expiresAt) {
    String sessionKey = sessionKey(userId, sessionId);
    String indexKey = indexKey(userId);

    Long result = redisTemplate.execute(COMPARE_AND_ROTATE_SCRIPT,
        List.of(sessionKey, indexKey),
        sessionKeyPrefix(userId),
        sessionId.toString(),
        expectedRefreshJti.toString(),
        newRefreshJti.toString(),
        String.valueOf(expiresAt.toEpochMilli()));

    if (result == null || result == 0L) {
      throw UserSessionExpiredException.withNone();
    }
    if (result == -1L) {
      throw RefreshTokenReusedException.withNone();
    }

    return new UserSession(userId, sessionId, newRefreshJti, issuedAt);
  }

  @Override
  public Optional<UserSession> find(UUID userId, UUID sessionId) {
    String sessionKey = sessionKey(userId, sessionId);
    Map<String, String> fields = hashOperations.entries(sessionKey);

    if (fields.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(toUserSession(userId, sessionId, fields));
  }

  @Override
  public List<UserSession> findAllByUserId(UUID userId) {
    String indexKey = indexKey(userId);
    pruneExpiredEntries(indexKey);

    Set<String> sessionIds = zSetOperations.range(indexKey, 0, -1);
    if (sessionIds == null || sessionIds.isEmpty()) {
      return List.of();
    }

    List<UserSession> sessions = new ArrayList<>();
    for (String sessionId : sessionIds) {
      // 세션 키가 TTL로 먼저 사라진 경우 find()가 비어있는 채로 자연스럽게 걸러진다.
      find(userId, UUID.fromString(sessionId)).ifPresent(sessions::add);
    }
    return sessions;
  }

  @Override
  public void revoke(UUID userId, UUID sessionId) {
    redisTemplate.execute(REVOKE_SESSION_SCRIPT,
        List.of(sessionKey(userId, sessionId), indexKey(userId)),
        sessionId.toString()
    );
  }

  @Override
  public void revokeAll(UUID userId) {
    redisTemplate.execute(REVOKE_ALL_SESSIONS_SCRIPT,
        List.of(indexKey(userId)),
        sessionKeyPrefix(userId)
    );
  }

  private void pruneExpiredEntries(String indexKey) {
    zSetOperations.removeRangeByScore(indexKey, 0, Instant.now(clock).toEpochMilli());
  }

  private void extendIndexExpiryIfNeeded(String indexKey, Instant candidateExpiresAt) {
    // 인덱스가 지금 들고 있는 세션들 중 가장 늦게 끝나는 만료시각을 구해서,
    // 인덱스 TTL을 그 시각과 같거나 그보다 늦게 맞춘다.
    Set<TypedTuple<String>> highestScored = zSetOperations.reverseRangeWithScores(indexKey, 0, 0);

    Instant maxExpiresAt = candidateExpiresAt;
    if (highestScored != null && !highestScored.isEmpty()) {
      Double highestScore = highestScored.iterator().next().getScore();
      if (highestScore != null) {
        maxExpiresAt = Instant.ofEpochMilli(Math.max(candidateExpiresAt.toEpochMilli(), highestScore.longValue()));
      }
    }
    redisTemplate.expireAt(indexKey, maxExpiresAt);
  }

  private UserSession toUserSession(UUID userId, UUID sessionId, Map<String, String> fields) {
    UUID refreshJti = UUID.fromString(fields.get(FIELD_REFRESH_JTI));
    Instant issuedAt = Instant.ofEpochMilli(Long.parseLong(fields.get(FIELD_ISSUED_AT)));
    return new UserSession(userId, sessionId, refreshJti, issuedAt);
  }

  private String sessionKey(UUID userId, UUID sessionId) {
    return sessionKeyPrefix(userId) + sessionId;
  }

  private String sessionKeyPrefix(UUID userId) {
    return SESSION_KEY_PREFIX + userId + ":";
  }

  private String indexKey(UUID userId) {
    return INDEX_KEY_PREFIX + userId;
  }
}
