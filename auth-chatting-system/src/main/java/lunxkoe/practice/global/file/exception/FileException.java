package lunxkoe.practice.global.file.exception;


import java.util.Map;
import lunxkoe.practice.global.exception.AppException;
import org.springframework.http.HttpStatus;

public abstract class FileException extends AppException {

  protected FileException(String message, Throwable cause, HttpStatus status, Map<String, Object> details) {
    super(message, cause, status, details);
  }
}
