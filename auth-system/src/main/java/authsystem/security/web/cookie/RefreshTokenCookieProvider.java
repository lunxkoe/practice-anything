package authsystem.security.web.cookie;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieProvider {

  public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

  private final RefreshTokenCookieProperties cookieProperties;

  public void attach(HttpServletResponse response, String refreshToken) {
    response.addHeader(HttpHeaders.SET_COOKIE,
        build(refreshToken, cookieProperties.refreshTokenExpiration()).toString());
  }

  public void clear(HttpServletResponse response) {
    response.addHeader(HttpHeaders.SET_COOKIE, build("", Duration.ZERO).toString());
  }

  private ResponseCookie build(String value, Duration maxAge) {
    return ResponseCookie.from(REFRESH_TOKEN, value)
        .httpOnly(true)
        .secure(cookieProperties.secure())
        .sameSite("Strict")
        .path("/api/auth")
        .maxAge(maxAge)
        .build();
  }
}
