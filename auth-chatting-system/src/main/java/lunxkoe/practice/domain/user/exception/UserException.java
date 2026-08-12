package lunxkoe.practice.domain.user.exception;

import java.util.Map;
import lunxkoe.practice.global.exception.AppException;
import org.springframework.http.HttpStatus;

public abstract class UserException extends AppException {

  protected UserException(String message, Throwable cause, HttpStatus status, Map<String, Object> details) {
    super(message, cause, status, details);
  }
}
