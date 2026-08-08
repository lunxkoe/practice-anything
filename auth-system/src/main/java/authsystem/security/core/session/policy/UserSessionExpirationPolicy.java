package authsystem.security.core.session.policy;

import authsystem.security.core.session.dto.UserSession;
import java.time.Instant;

public interface UserSessionExpirationPolicy {

  Instant expiresAtOnIssue(Instant issuedAt);

  Instant expiresAtOnRotate(UserSession current, Instant now);
}
