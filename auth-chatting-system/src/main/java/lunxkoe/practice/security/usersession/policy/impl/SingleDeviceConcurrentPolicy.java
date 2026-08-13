package lunxkoe.practice.security.usersession.policy.impl;

import java.time.Instant;
import lunxkoe.practice.security.usersession.dto.UserSession;
import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;

/** 기존 세션을 전부 회수하고 새 세션 하나만 원자적으로 남김 (단일 기기 로그인). */
public class SingleDeviceConcurrentPolicy implements ConcurrentPolicy {

  @Override
  public UserSession apply(UserSession newSession, Instant expiresAt, UserSessionRegistry registry) {
    return registry.replaceAll(newSession, expiresAt);
  }
}
