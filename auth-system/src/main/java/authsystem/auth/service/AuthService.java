package authsystem.auth.service;

import authsystem.auth.dto.request.ResetPasswordRequest;
import authsystem.auth.dto.request.SignInRequest;
import authsystem.auth.dto.response.RefreshDto;
import authsystem.auth.dto.response.SignInDto;
import authsystem.auth.event.request.TempPasswordRequestedEvent;
import authsystem.auth.exception.AccountLockedException;
import authsystem.auth.exception.InvalidCredentialsException;
import authsystem.auth.mapper.AuthMapper;
import authsystem.security.core.port.SecurityUserPort;
import authsystem.security.core.port.SecurityUserView;
import authsystem.security.core.principal.CustomUserDetails;
import authsystem.security.core.session.dto.UserSession;
import authsystem.security.core.session.registry.UserSessionRegistry;
import authsystem.security.core.token.dto.RefreshTokenClaims;
import authsystem.security.core.token.exception.business.InvalidRefreshTokenException;
import authsystem.security.core.token.exception.business.TokenException;
import authsystem.security.core.token.provider.TokenProvider;
import authsystem.temppassword.generator.TempPasswordGenerator;
import authsystem.temppassword.registry.TempPasswordRegistry;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

  private final UserSessionRegistry userSessionRegistry;
  private final TokenProvider tokenProvider;
  private final AuthenticationManager authenticationManager;
  private final AuthMapper authMapper;
  private final SecurityUserPort securityUserPort;
  private final TempPasswordRegistry tempPasswordRegistry;
  private final ApplicationEventPublisher eventPublisher;
  private final Clock clock;

  public void signOut(String refreshToken) {
    if (!StringUtils.hasText(refreshToken)) {
      return;
    }

    RefreshTokenClaims claims;
    try {
      claims = tokenProvider.parseRefreshToken(refreshToken);
    } catch (TokenException e) {
      return;
    }

    userSessionRegistry.revoke(claims.userId(), claims.sessionId());
  }

  public SignInDto signIn(SignInRequest request) {
    Authentication authentication = authenticate(request);

    CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
    SecurityUserView user = principal.getSecurityUser();

    Instant now = Instant.now(clock);
    UserSession issued = userSessionRegistry.issue(user.id(), now);

    String accessToken = tokenProvider.createAccessToken(user.id(), issued.sessionId(),
        user.role(), now);

    String refreshToken = tokenProvider.createRefreshToken(user.id(), issued.sessionId(),
        issued.currentRefreshJti(), now);

    return authMapper.signInDtoFrom(user, accessToken, refreshToken);
  }

  private Authentication authenticate(SignInRequest request) {
    try {
      return authenticationManager.authenticate(
          UsernamePasswordAuthenticationToken.unauthenticated(request.username(),
              request.password())
      );
    } catch (LockedException e) {
      throw AccountLockedException.withNone();
    } catch (AuthenticationException e) {
      throw InvalidCredentialsException.withNone();
    }
  }

  public void resetPassword(ResetPasswordRequest request) {
    securityUserPort.findByEmail(request.email())
        .ifPresent(user -> {
          String rawTempPassword = tempPasswordRegistry.issue(user.id());
          eventPublisher.publishEvent(
              new TempPasswordRequestedEvent(user.email(), rawTempPassword,
                  tempPasswordRegistry.getExpirationMinutes()));
        });
  }

  public RefreshDto refresh(String refreshToken) {
    RefreshTokenClaims claims = tokenProvider.parseRefreshToken(refreshToken);

    SecurityUserView user = securityUserPort.findById(claims.userId())
        .orElseThrow(InvalidRefreshTokenException::withNone);

    if (user.locked()) {
      userSessionRegistry.revokeAll(user.id());
      throw AccountLockedException.withNone();
    }

    Instant now = Instant.now(clock);
    UserSession rotated = userSessionRegistry.rotate(user.id(), claims.sessionId(), claims.jti(),
        now);

    String newAccessToken = tokenProvider.createAccessToken(user.id(), rotated.sessionId(),
        user.role(), now);

    String newRefreshToken = tokenProvider.createRefreshToken(user.id(), rotated.sessionId(),
        rotated.currentRefreshJti(), now);

    return authMapper.refreshDtoFrom(user, newAccessToken, newRefreshToken);
  }
}
