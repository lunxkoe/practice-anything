package authsystem.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SignInRequest(
    @NotBlank(message = "이메일은 필수입니다.")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다.")
    String username,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(max = 64, message = "비밀번호는 64자 이하여야 합니다.")
    String password
) {

}