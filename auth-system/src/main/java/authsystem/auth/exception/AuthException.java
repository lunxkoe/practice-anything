package authsystem.auth.exception;

import authsystem.common.exception.AppException;
import java.util.Map;
import org.springframework.http.HttpStatus;

public abstract class AuthException extends AppException {

  protected AuthException(HttpStatus status, String message, Map<String, Object> details,
      Throwable cause) {
    super(status, message, details, cause);
  }
}
