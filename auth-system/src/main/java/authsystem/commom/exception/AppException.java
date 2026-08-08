package authsystem.commom.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AppException extends RuntimeException {

  private final HttpStatus status;
  private final Map<String, Object> details;

  protected AppException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace, HttpStatus status, Map<String, Object> details) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.status = status;
    this.details = details;
  }
}
