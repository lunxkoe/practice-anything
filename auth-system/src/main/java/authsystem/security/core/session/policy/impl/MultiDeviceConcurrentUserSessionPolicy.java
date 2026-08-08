package authsystem.security.core.session.policy.impl;

import authsystem.security.core.session.dto.UserSession;
import authsystem.security.core.session.policy.ConcurrentUserSessionPolicy;
import authsystem.security.core.session.registry.UserSessionRegistry;
import java.time.Instant;
import java.util.UUID;

/**
 * 다중 기기 로그인: 기존 세션 수에 제한을 두지 않는다.
 */
public class MultiDeviceConcurrentUserSessionPolicy implements ConcurrentUserSessionPolicy {

  @Override
  public UserSession issue(UUID userId, Instant now, UserSessionRegistry registry) {
    return registry.issue(userId, now);
  }
}
