package authsystem.user.dto.request;

import authsystem.user.entity.enums.Role;
import jakarta.validation.constraints.NotNull;

public record UserRoleUpdateRequest(
    @NotNull(message = "role은 필수입니다.")
    Role role
) {

}
