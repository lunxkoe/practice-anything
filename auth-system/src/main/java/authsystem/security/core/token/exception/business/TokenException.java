package authsystem.security.core.token.exception.business;

import authsystem.commom.exception.AppException;
import java.util.Map;
import org.springframework.http.HttpStatus;

public class TokenException extends AppException {

  public TokenException(HttpStatus status, String message, Map<String, Object> details,
      Throwable cause) {
    super(status, message, details, cause);
  }
}
