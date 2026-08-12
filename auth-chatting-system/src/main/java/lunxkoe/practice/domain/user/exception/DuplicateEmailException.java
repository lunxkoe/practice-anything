package lunxkoe.practice.domain.user.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class DuplicateEmailException extends UserException {

  private static final String MESSAGE = "이미 사용 중인 이메일입니다.";

  private DuplicateEmailException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.CONFLICT, details);
  }

  public static DuplicateEmailException withNone() {
    return new DuplicateEmailException(null, Map.of());
  }
}
