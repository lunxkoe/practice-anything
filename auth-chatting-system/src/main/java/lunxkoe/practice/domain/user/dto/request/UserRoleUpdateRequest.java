package lunxkoe.practice.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lunxkoe.practice.domain.user.entity.enums.Role;

public record UserRoleUpdateRequest(
    @NotNull(message = "role은 필수입니다.")
    Role role
) {

}
