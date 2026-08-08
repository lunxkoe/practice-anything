package authsystem.security.core.session.registry;

import authsystem.security.core.session.dto.UserSession;
import authsystem.security.core.session.exception.business.UserSessionExpiredException;
import authsystem.security.core.session.policy.UserSessionExpirationPolicy;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRegistry {

  UserSessionExpirationPolicy expirationPolicy();

  default UserSession issue(UUID userId, Instant now) {
    UserSession issued = UserSession.issue(userId, now);
    Instant expiresAt = requireFuture(expirationPolicy().expiresAtOnIssue(now), now);
    return save(issued, expiresAt);
  }

  /**
   * 이 유저의 기존 세션을 전부 회수하고 새 세션을 원자적으로 발급한다 (단일 기기 로그인에서 사용).
   */
  default UserSession issueExclusive(UUID userId, Instant now) {
    UserSession issued = UserSession.issue(userId, now);
    Instant expiresAt = requireFuture(expirationPolicy().expiresAtOnIssue(now), now);
    return replaceAll(issued, expiresAt);
  }

  default UserSession rotate(UUID userId, UUID sessionId, UUID currentRefreshJti, Instant now) {
    UserSession current = verifyUserSession(userId, sessionId);
    Instant expiresAt = requireFuture(expirationPolicy().expiresAtOnRotate(current, now), now);
    return compareAndRotate(userId, sessionId, currentRefreshJti, UUID.randomUUID(),
        current.issuedAt(), expiresAt);
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

  UserSession save(UserSession session, Instant expiresAt);

  UserSession replaceAll(UserSession session, Instant expiresAt);

  UserSession compareAndRotate(UUID userId, UUID sessionId, UUID expectedRefreshJti,
      UUID newRefreshJti, Instant issuedAt, Instant expiresAt);

  Optional<UserSession> find(UUID userId, UUID sessionId);

  List<UserSession> findAllByUserId(UUID userId);

  void revoke(UUID userId, UUID sessionId);

  void revokeAll(UUID userId);
}
