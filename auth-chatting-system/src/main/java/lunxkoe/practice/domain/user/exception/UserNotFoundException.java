package lunxkoe.practice.domain.user.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class UserNotFoundException extends UserException{

  private static final String MESSAGE = "사용자를 찾을 수 없습니다.";

  private UserNotFoundException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.NOT_FOUND, details);
  }

  public static UserNotFoundException withNone() {
    return new UserNotFoundException(null, Map.of());
  }
}
