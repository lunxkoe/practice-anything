package authsystem.security.core.port;

import java.time.Instant;
import java.util.UUID;

public record SecurityUserView(
    UUID id,
    String email,
    String name,
    Instant createdAt,
    String encodedPassword,
    String role,
    boolean locked
) {

}
