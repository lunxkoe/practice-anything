package lunxkoe.practice.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
    String password
) {

  public ChangePasswordRequest {
    password = trimOrNull(password);
  }

  private static String trimOrNull(String value) {
    return value == null ? null : value.trim();
  }
}
