package authsystem.user.exception;

import authsystem.common.exception.AppException;
import java.util.Map;
import org.springframework.http.HttpStatus;

public abstract class UserException extends AppException {

  protected UserException(HttpStatus status, String message, Map<String, Object> details,
      Throwable cause) {
    super(status, message, details, cause);
  }
}
