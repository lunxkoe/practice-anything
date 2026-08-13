package lunxkoe.practice.security.token.exception.business;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class InvalidAccessTokenException extends TokenException {

  private static final String MESSAGE = "유효하지 않은 엑세스 토큰입니다.";

  private InvalidAccessTokenException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.UNAUTHORIZED, details);
  }

  public static InvalidAccessTokenException withNone() {
    return new InvalidAccessTokenException(null, Map.of());
  }

  public static InvalidAccessTokenException withCause(Throwable cause) {
    return new InvalidAccessTokenException(cause, Map.of());
  }
}
