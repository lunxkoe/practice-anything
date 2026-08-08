package authsystem.security.core.session.exception.business;

import authsystem.commom.exception.AppException;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class UserSessionException extends AppException {

  protected UserSessionException(HttpStatus status, String message, Map<String, Object> details,
      Throwable cause) {
    super(status, message, details, cause);
  }
}
