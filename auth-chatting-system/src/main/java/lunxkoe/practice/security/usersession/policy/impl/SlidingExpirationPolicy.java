package lunxkoe.practice.security.usersession.policy.impl;

import java.time.Duration;
import java.time.Instant;
import lunxkoe.practice.security.usersession.policy.ExpirationPolicy;

public class SlidingExpirationPolicy implements ExpirationPolicy {

  private final Duration ttl;

  public SlidingExpirationPolicy(Duration ttl) {
    this.ttl = ttl;
  }

  @Override
  public Instant expiresAt(Instant issuedAt, Instant now) {
    return now.plus(ttl);
  }
}
