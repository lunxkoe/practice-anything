package lunxkoe.practice.domain.user.exception;

import java.util.Map;
import lunxkoe.practice.global.exception.AppException;
import org.springframework.http.HttpStatus;

public class AccessDeniedException extends AppException {

  private static final String MESSAGE = "권한이 없습니다.";

  private AccessDeniedException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.FORBIDDEN, details);
  }

  public static AccessDeniedException withNone() {
    return new AccessDeniedException(null, Map.of());
  }
}
