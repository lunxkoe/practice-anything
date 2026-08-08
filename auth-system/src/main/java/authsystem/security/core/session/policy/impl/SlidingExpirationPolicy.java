package authsystem.security.core.session.policy.impl;

import authsystem.security.core.session.dto.UserSession;
import authsystem.security.core.session.policy.UserSessionExpirationPolicy;
import java.time.Duration;
import java.time.Instant;

public class SlidingExpirationPolicy implements UserSessionExpirationPolicy {

  private final Duration ttl;

  public SlidingExpirationPolicy(Duration ttl) {
    this.ttl = ttl;
  }

  @Override
  public Instant expiresAtOnIssue(Instant issuedAt) {
    return issuedAt.plus(ttl);
  }

  @Override
  public Instant expiresAtOnRotate(UserSession current, Instant now) {
    return now.plus(ttl);
  }
}
