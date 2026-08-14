package lunxkoe.practice.global.file.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class InvalidFilePathException extends FileException {

  private static final String MESSAGE = "허용되지 않는 파일 경로입니다.";

  private InvalidFilePathException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.BAD_REQUEST, details);
  }

  public static InvalidFilePathException withNone() {
    return new InvalidFilePathException(null, Map.of());
  }
}
