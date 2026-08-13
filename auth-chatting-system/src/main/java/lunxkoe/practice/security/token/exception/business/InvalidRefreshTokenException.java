package lunxkoe.practice.security.token.exception.business;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class InvalidRefreshTokenException extends TokenException {

  private static final String MESSAGE = "유효하지 않은 리프레시 토큰입니다.";

  private InvalidRefreshTokenException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.UNAUTHORIZED, details);
  }

  public static InvalidRefreshTokenException withNone() {
    return new InvalidRefreshTokenException(null, Map.of());
  }

  public static InvalidRefreshTokenException withCause(Throwable cause) {
    return new InvalidRefreshTokenException(cause, Map.of());
  }
}
