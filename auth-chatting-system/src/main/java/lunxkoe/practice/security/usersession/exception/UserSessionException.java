package lunxkoe.practice.security.usersession.exception;

import java.util.Map;
import lunxkoe.practice.global.exception.AppException;
import org.springframework.http.HttpStatus;

public abstract class UserSessionException extends AppException {

  protected UserSessionException(String message, Throwable cause, HttpStatus status, Map<String, Object> details) {
    super(message, cause, status, details);
  }
}
