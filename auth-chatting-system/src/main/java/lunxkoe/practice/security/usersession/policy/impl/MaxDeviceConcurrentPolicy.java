package lunxkoe.practice.security.usersession.policy.impl;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import lunxkoe.practice.security.usersession.dto.UserSession;
import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;

public class MaxDeviceConcurrentPolicy implements ConcurrentPolicy {

  private final int maxDevices;

  public MaxDeviceConcurrentPolicy(int maxDevices) {
    this.maxDevices = maxDevices;
  }

  @Override
  public UserSession apply(UserSession newSession, Instant expiresAt, UserSessionRegistry registry) {
    return registry.saveEvictingOldest(newSession, expiresAt, maxDevices);
  }
}
