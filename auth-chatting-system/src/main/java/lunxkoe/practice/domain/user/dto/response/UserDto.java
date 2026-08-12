package lunxkoe.practice.domain.user.dto.response;

import java.time.Instant;
import java.util.UUID;
import lunxkoe.practice.domain.user.entity.enums.Role;

public record UserDto(
    UUID id,
    Instant createdAt,
    String email,
    String name,
    Role role,
    boolean locked
) {

}
