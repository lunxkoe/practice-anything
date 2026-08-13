package lunxkoe.practice.domain.auth.exception;

import java.util.Map;
import lunxkoe.practice.global.exception.AppException;
import org.springframework.http.HttpStatus;

public abstract class AuthException extends AppException {

  protected AuthException(String message, Throwable cause, HttpStatus status, Map<String, Object> details) {
    super(message, cause, status, details);
  }
}
