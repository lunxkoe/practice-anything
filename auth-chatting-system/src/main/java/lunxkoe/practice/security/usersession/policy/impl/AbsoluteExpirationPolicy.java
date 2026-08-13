package lunxkoe.practice.security.usersession.policy.impl;

import java.time.Duration;
import java.time.Instant;
import lunxkoe.practice.security.usersession.policy.ExpirationPolicy;

public class AbsoluteExpirationPolicy implements ExpirationPolicy {

  private final Duration ttl;

  public AbsoluteExpirationPolicy(Duration ttl) {
    this.ttl = ttl;
  }

  @Override
  public Instant expiresAt(Instant issuedAt, Instant now) {
    return issuedAt.plus(ttl);
  }
}
