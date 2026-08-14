package lunxkoe.practice.global.file.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class FileTooLargeException extends FileException {

  private static final String MESSAGE = "파일 크기가 허용된 용량을 초과했습니다.";

  private FileTooLargeException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.CONTENT_TOO_LARGE, details);
  }

  public static FileTooLargeException withSize(long actualSize, long maxSize) {
    return new FileTooLargeException(null, Map.of("actualSize", actualSize, "maxSize", maxSize));
  }
}
