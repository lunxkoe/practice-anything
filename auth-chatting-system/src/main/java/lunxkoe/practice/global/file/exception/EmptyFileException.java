package lunxkoe.practice.global.file.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class EmptyFileException extends FileException {

  private static final String MESSAGE = "업로드할 파일이 없습니다.";

  private EmptyFileException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.BAD_REQUEST, details);
  }

  public static EmptyFileException withNone() {
    return new EmptyFileException(null, Map.of());
  }
}
