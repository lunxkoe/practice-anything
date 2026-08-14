package lunxkoe.practice.domain.user.dto.request;

import jakarta.validation.constraints.NotNull;

public record UserLockUpdateRequest(
    @NotNull(message = "locked는 필수입니다.")
    Boolean locked
) {

}
