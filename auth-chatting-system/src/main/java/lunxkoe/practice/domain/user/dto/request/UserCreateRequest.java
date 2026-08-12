package lunxkoe.practice.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Locale;

public record UserCreateRequest(

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @Size(max = 50, message = "이메일은 50자 이하여야 합니다.")
    String email,

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하여야 합니다.")
    String password,

    @NotBlank(message = "이름은 필수입니다.")
    @Size(max = 10, message = "이름은 10자 이하여야 합니다.")
    String name
) {

  public UserCreateRequest {
    email = normalizeEmail(email);
    password = trimOrNull(password);
    name = trimOrNull(name);
  }

  private static String normalizeEmail(String email) {
    return email == null ? null : email.trim().toLowerCase(Locale.ROOT);
  }

  private static String trimOrNull(String value) {
    return value == null ? null : value.trim();
  }
}
