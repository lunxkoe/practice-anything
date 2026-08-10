package authsystem.auth.service;

import authsystem.auth.dto.response.SignInDto;
import authsystem.auth.exception.AccountLockedException;
import authsystem.auth.exception.EmailAlreadyRegisteredException;
import authsystem.auth.mapper.AuthMapper;
import authsystem.security.core.port.SecurityUserPort;
import authsystem.security.core.port.SecurityUserView;
import authsystem.security.core.port.SocialAccountPort;
import authsystem.security.core.session.dto.UserSession;
import authsystem.security.core.session.registry.UserSessionRegistry;
import authsystem.security.core.token.provider.TokenProvider;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OAuth2SignInService {

  private final SocialAccountPort socialAccountPort;
  private final SecurityUserPort securityUserPort;
  private final UserSessionRegistry userSessionRegistry;
  private final TokenProvider tokenProvider;
  private final AuthMapper authMapper;
  private final Clock clock;

  @Transactional
  public SignInDto signIn(String provider, String providerId, String providerEmail,
      String providerName) {

    SecurityUserView user = socialAccountPort.findLinkedUser(provider, providerId)
        .orElseGet(() -> provisionNewUser(provider, providerId, providerEmail, providerName));

    if (user.locked()) {
      throw AccountLockedException.withNone();
    }

    Instant now = Instant.now(clock);
    UserSession issued = userSessionRegistry.issue(user.id(), now);

    String accessToken = tokenProvider.createAccessToken(user.id(), issued.sessionId(),
        user.role(), now);
    String refreshToken = tokenProvider.createRefreshToken(user.id(), issued.sessionId(),
        issued.currentRefreshJti(), now);

    return authMapper.signInDtoFrom(user, accessToken, refreshToken);
  }

  private SecurityUserView provisionNewUser(String provider, String providerId, String email,
      String name) {
    if (securityUserPort.findByEmail(email).isPresent()) {
      throw EmailAlreadyRegisteredException.withEmail(email);
    }
    return socialAccountPort.createSocialUser(provider, providerId, email, name);
  }
}
