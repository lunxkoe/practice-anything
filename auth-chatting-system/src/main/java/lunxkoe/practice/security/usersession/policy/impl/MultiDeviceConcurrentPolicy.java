package lunxkoe.practice.security.usersession.policy.impl;

import java.time.Instant;
import lunxkoe.practice.security.usersession.dto.UserSession;
import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;

/** 기기 수 제한 없이 세션을 계속 누적 */
public class MultiDeviceConcurrentPolicy implements ConcurrentPolicy {

  @Override
  public UserSession apply(UserSession newSession, Instant expiresAt, UserSessionRegistry registry) {
    return registry.save(newSession, expiresAt);
  }
}
