package lunxkoe.practice.security.config;

import jakarta.annotation.PostConstruct;
import lunxkoe.practice.security.cookie.properties.RefreshTokenCookieProperties;
import lunxkoe.practice.security.token.properties.TokenProperties;
import lunxkoe.practice.security.usersession.properties.UserSessionProperties;
import org.springframework.stereotype.Component;

@Component
public class ExpirationConsistencyValidator {

  private final TokenProperties tokenProperties;
  private final UserSessionProperties userSessionProperties;
  private final RefreshTokenCookieProperties cookieProperties;

  public ExpirationConsistencyValidator(
      TokenProperties tokenProperties,
      UserSessionProperties userSessionProperties,
      RefreshTokenCookieProperties cookieProperties) {
    this.tokenProperties = tokenProperties;
    this.userSessionProperties = userSessionProperties;
    this.cookieProperties = cookieProperties;
  }

  @PostConstruct
  void validateExpirationConsistency() {
    if (tokenProperties.refreshTokenExpiration().compareTo(userSessionProperties.ttl()) > 0) {
      throw new IllegalStateException(
          "refreshTokenExpiration(" + tokenProperties.refreshTokenExpiration()
              + ")은 user-session ttl(" + userSessionProperties.ttl() + ")보다 클 수 없습니다.");
    }
    if (!userSessionProperties.ttl().equals(cookieProperties.refreshTokenExpiration())) {
      throw new IllegalStateException(
          "user-session ttl(" + userSessionProperties.ttl()
              + ")과 refresh-cookie refreshTokenExpiration(" + cookieProperties.refreshTokenExpiration()
              + ")은 같아야 합니다.");
    }
  }
}
