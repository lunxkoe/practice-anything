package lunxkoe.practice.security.usersession.registry;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lunxkoe.practice.security.usersession.dto.UserSession;
import lunxkoe.practice.security.usersession.exception.UserSessionExpiredException;
import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.ExpirationPolicy;

public interface UserSessionRegistry {

  ExpirationPolicy expirationPolicy();

  ConcurrentPolicy concurrentPolicy();

  default UserSession issue(UUID userId, Instant issuedAt) {
    UserSession newSession = UserSession.issue(userId, issuedAt);
    Instant expiresAt = requireFuture(expirationPolicy().expiresAt(issuedAt, issuedAt), issuedAt);
    return concurrentPolicy().apply(newSession, expiresAt, this);
  }

  default UserSession rotate(UUID userId, UUID sessionId, UUID presentedRefreshJti, Instant now) {
    UserSession storedSession = verifyUserSession(userId, sessionId);
    Instant expiresAt = requireFuture(expirationPolicy().expiresAt(storedSession.issuedAt(), now), now);
    return compareAndRotate(userId, sessionId, presentedRefreshJti, UUID.randomUUID(), storedSession.issuedAt(), expiresAt);
  }

  default UserSession verifyUserSession(UUID userId, UUID sessionId) {
    return find(userId, sessionId)
        .orElseThrow(UserSessionExpiredException::withNone);
  }

  private static Instant requireFuture(Instant expiresAt, Instant now) {
    if (!expiresAt.isAfter(now)) {
      throw new IllegalStateException(
          "만료 정책이 유효하지 않은 만료시각을 반환했습니다. expiresAt=" + expiresAt + ", now=" + now);
    }
    return expiresAt;
  }

  UserSession save(UserSession newSession, Instant expiresAt);

  UserSession replaceAll(UserSession newSession, Instant expiresAt);

  UserSession saveEvictingOldest(UserSession newSession, Instant expiresAt, int maxDevices);

  UserSession compareAndRotate(UUID userId, UUID sessionId, UUID expectedRefreshJti, UUID newRefreshJti, Instant issuedAt, Instant expiresAt);

  Optional<UserSession> find(UUID userId, UUID sessionId);

  List<UserSession> findAllByUserId(UUID userId);

  void revoke(UUID userId, UUID sessionId);

  void revokeAll(UUID userId);
}
