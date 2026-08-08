package authsystem.user.dto.response;

import authsystem.user.entity.enums.Role;
import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    Instant createdAt,
    String email,
    String name,
    Role role,
    boolean locked
) {

}
