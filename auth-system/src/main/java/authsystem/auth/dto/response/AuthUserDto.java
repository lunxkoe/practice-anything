package authsystem.auth.dto.response;

import java.time.Instant;
import java.util.UUID;

public record AuthUserDto(
    UUID id,
    Instant createdAt,
    String email,
    String name,
    String role,
    boolean locked
) {
  
}
