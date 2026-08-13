package lunxkoe.practice.security.usersession.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class UserSessionExpiredException extends UserSessionException {

  private static final String MESSAGE = "세션이 만료되었습니다. 다시 로그인해주세요.";

  private UserSessionExpiredException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.UNAUTHORIZED, details);
  }

  public static UserSessionExpiredException withNone() {
    return new UserSessionExpiredException(null, Map.of());
  }
}
