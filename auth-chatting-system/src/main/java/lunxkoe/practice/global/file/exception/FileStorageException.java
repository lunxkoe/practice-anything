package lunxkoe.practice.global.file.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class FileStorageException extends FileException {

  private static final String MESSAGE = "파일 저장에 실패했습니다.";

  private FileStorageException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.INTERNAL_SERVER_ERROR, details);
  }

  public static FileStorageException withCause(Throwable cause) {
    return new FileStorageException(cause, Map.of());
  }
}
