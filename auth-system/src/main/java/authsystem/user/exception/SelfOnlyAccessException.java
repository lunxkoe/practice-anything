package authsystem.user.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class SelfOnlyAccessException extends UserException {

  private static final String MESSAGE = "본인만 접근할 수 있습니다.";

  private SelfOnlyAccessException(Map<String, Object> details, Throwable cause) {
    super(HttpStatus.FORBIDDEN, MESSAGE, details, cause);
  }

  public static SelfOnlyAccessException withNone() {
    return new SelfOnlyAccessException(Map.of(), null);
  }
}
