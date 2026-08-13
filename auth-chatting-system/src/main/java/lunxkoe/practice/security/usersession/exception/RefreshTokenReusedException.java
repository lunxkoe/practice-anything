package lunxkoe.practice.security.usersession.exception;

import java.util.Map;
import org.springframework.http.HttpStatus;

public class RefreshTokenReusedException extends UserSessionException {

  private static final String MESSAGE = "이미 사용된 리프레시 토큰입니다. 보안을 위해 모든 기기에서 로그아웃되었습니다.";

  private RefreshTokenReusedException(Throwable cause, Map<String, Object> details) {
    super(MESSAGE, cause, HttpStatus.UNAUTHORIZED, details);
  }

  public static RefreshTokenReusedException withNone() {
    return new RefreshTokenReusedException(null, Map.of());
  }
}
