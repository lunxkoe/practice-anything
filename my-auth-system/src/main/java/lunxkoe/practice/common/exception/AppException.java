package lunxkoe.practice.common.exception;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class AppException extends RuntimeException {

  private final HttpStatus status;
  private final Map<String, Object> details;

  public AppException(String message, Throwable cause, HttpStatus status, Map<String, Object> details) {
    super(message, cause);
    this.status = status;
    this.details = details != null ? details : Map.of();
  }
}
