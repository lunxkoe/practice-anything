package lunxkoe.practice.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record SignInRequest(

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다.")
    String username,

    @NotBlank(message = "비밀번호는 필수입니다.")
    String password
) {

  public SignInRequest {
    username = normalizeUsername(username);
    password = trimOrNull(password);
  }

  private static String normalizeUsername(String username) {
    return username == null ? null : username.trim().toLowerCase(Locale.ROOT);
  }

  private static String trimOrNull(String value) {
    return value == null ? null : value.trim();
  }
}
