package lunxkoe.practice.security.cookie.provider;

import jakarta.servlet.http.HttpServletResponse;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.security.cookie.properties.RefreshTokenCookieProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RefreshTokenCookieProvider {

  public static final String REFRESH_TOKEN = "REFRESH_TOKEN";

  private final RefreshTokenCookieProperties cookieProperties;

  public void attach(HttpServletResponse response, String refreshToken) {
    Duration maxAge = cookieProperties.refreshTokenExpiration();
    response.addHeader(HttpHeaders.SET_COOKIE, build(refreshToken, maxAge).toString());
  }

  public void clear(HttpServletResponse response) {
    response.addHeader(HttpHeaders.SET_COOKIE, build("", Duration.ZERO).toString());
  }

  private ResponseCookie build(String value, Duration maxAge) {
    return ResponseCookie.from(REFRESH_TOKEN, value)
        .httpOnly(true)
        .secure(cookieProperties.secure())
        .sameSite("Lax")
        .path("/")
        .maxAge(maxAge)
        .build();
  }
}
