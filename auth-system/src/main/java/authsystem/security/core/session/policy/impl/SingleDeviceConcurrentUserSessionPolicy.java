package authsystem.security.core.session.policy.impl;

import authsystem.security.core.session.dto.UserSession;
import authsystem.security.core.session.policy.ConcurrentUserSessionPolicy;
import authsystem.security.core.session.registry.UserSessionRegistry;
import java.time.Instant;
import java.util.UUID;

/**
 * 단일 기기 로그인: 기존 세션 회수와 새 세션 발급을 원자적으로 처리한다.
 */
public class SingleDeviceConcurrentUserSessionPolicy implements ConcurrentUserSessionPolicy {

  @Override
  public UserSession issue(UUID userId, Instant now, UserSessionRegistry registry) {
    return registry.issueExclusive(userId, now);
  }
}

