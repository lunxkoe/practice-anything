package authsystem.commom.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AppException extends RuntimeException {

  private final HttpStatus status;
  private final Map<String, Object> details;

  public AppException(HttpStatus status, String message, Map<String, Object> details,
      Throwable cause) {
    super(message, cause);
    this.status = status;
    this.details = details;
  }
}
