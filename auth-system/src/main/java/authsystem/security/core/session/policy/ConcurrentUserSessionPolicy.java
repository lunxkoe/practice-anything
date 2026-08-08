package authsystem.security.core.session.policy;

import authsystem.security.core.session.dto.UserSession;
import authsystem.security.core.session.registry.UserSessionRegistry;
import java.time.Instant;
import java.util.UUID;

public interface ConcurrentUserSessionPolicy {

  UserSession issue(UUID userId, Instant now, UserSessionRegistry registry);
}
