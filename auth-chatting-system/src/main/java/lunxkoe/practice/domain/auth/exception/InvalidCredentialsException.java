package lunxkoe.practice.domain.auth.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AuthException{

  private static final String MESSAGE = "아이디 혹은 비밀번호가 잘못되었습니다.";

  private InvalidCredentialsException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.UNAUTHORIZED, details);
  }

  public static InvalidCredentialsException withNone() {
    return new InvalidCredentialsException(null, Map.of());
  }
}
