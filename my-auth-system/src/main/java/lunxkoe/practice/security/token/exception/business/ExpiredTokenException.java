package lunxkoe.practice.security.token.exception.business;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class ExpiredTokenException extends TokenException {

  private static final String MESSAGE = "만료된 토큰입니다.";

  private ExpiredTokenException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.UNAUTHORIZED, details);
  }

  public static ExpiredTokenException withNone() {
    return new ExpiredTokenException(null, Map.of());
  }

  public static ExpiredTokenException withCause(Throwable cause) {
    return new ExpiredTokenException(cause, Map.of());
  }
}
