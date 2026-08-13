package lunxkoe.practice.domain.auth.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class AccountLockedException extends AuthException {

  private static final String MESSAGE = "계정이 잠겨있습니다. 관리자에게 문의해주세요.";

  private AccountLockedException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.FORBIDDEN, details);
  }

  public static AccountLockedException withNone() {
    return new AccountLockedException(null, Map.of());
  }
}
