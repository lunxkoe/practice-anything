package lunxkoe.practice.security.usersession.policy;

import java.time.Instant;

public interface ExpirationPolicy {

  Instant expiresAt(Instant issuedAt, Instant now);
}
