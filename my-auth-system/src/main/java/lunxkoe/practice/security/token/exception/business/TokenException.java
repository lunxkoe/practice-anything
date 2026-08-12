package lunxkoe.practice.security.token.exception.business;

import java.util.Map;
import lunxkoe.practice.common.exception.AppException;
import org.springframework.http.HttpStatus;

public abstract class TokenException extends AppException {

  public TokenException(String message, Throwable cause, HttpStatus status, Map<String, Object> details) {
    super(message, cause, status, details);
  }
}
