package authsystem.user.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class InvalidCurrentPasswordException extends UserException {

  private static final String MESSAGE = "현재 비밀번호가 일치하지 않습니다.";

  private InvalidCurrentPasswordException(Map<String, Object> details, Throwable cause) {
    super(HttpStatus.BAD_REQUEST, MESSAGE, details, cause);
  }

  public static InvalidCurrentPasswordException withNone() {
    return new InvalidCurrentPasswordException(Map.of(), null);
  }
}
