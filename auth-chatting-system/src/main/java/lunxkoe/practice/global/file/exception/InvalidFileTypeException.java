package lunxkoe.practice.global.file.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class InvalidFileTypeException extends FileException {

  private static final String MESSAGE = "허용되지 않는 파일 형식입니다.";

  private InvalidFileTypeException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.BAD_REQUEST, details);
  }

  public static InvalidFileTypeException withExtension(String extension) {
    return new InvalidFileTypeException(null, Map.of("extension", extension));
  }
}
