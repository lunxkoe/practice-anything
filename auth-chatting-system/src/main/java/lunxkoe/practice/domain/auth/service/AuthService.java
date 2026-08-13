package lunxkoe.practice.domain.auth.service;

import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.domain.auth.dto.request.SignInRequest;
import lunxkoe.practice.domain.auth.dto.response.SignInDto;
import lunxkoe.practice.domain.auth.exception.AccountLockedException;
import lunxkoe.practice.domain.auth.exception.InvalidCredentialsException;
import lunxkoe.practice.domain.auth.mapper.AuthMapper;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.security.details.CustomUserDetails;
import lunxkoe.practice.security.token.dto.RefreshTokenClaims;
import lunxkoe.practice.security.token.exception.business.TokenException;
import lunxkoe.practice.security.token.provider.TokenProvider;
import lunxkoe.practice.security.usersession.dto.UserSession;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;
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

  private final TokenProvider tokenProvider;
  private final UserSessionRegistry userSessionRegistry;
  private final AuthenticationManager authenticationManager;
  private final Clock clock;
  private final AuthMapper authMapper;

  public void signOut(String refreshToken) {
    if (!StringUtils.hasText(refreshToken)) {
      return;
    }

    RefreshTokenClaims claims = null;
    try {
      claims = tokenProvider.parseRefreshToken(refreshToken);
    } catch (TokenException e) {
      return; // 만료/위조/형식오류 토큰도 결과적으로 "로그아웃된 상태"와 같으므로 성공 처리
    }

    userSessionRegistry.revoke(claims.userId(), claims.sessionId());
  }

  public SignInDto signIn(SignInRequest request) {
    // 로그인
    CustomUserDetails principal = authenticate(request);

    // 토큰 발급
    UserDto userDto = principal.getUserDto();

    Instant issuedAt = Instant.now(clock);
    UserSession issued = userSessionRegistry.issue(
        userDto.id(),
        issuedAt
    );

    String accessToken = tokenProvider.createAccessToken(
        userDto.id(),
        issued.sessionId(),
        userDto.role().name(),
        issuedAt
    );

    String refreshToken = tokenProvider.createRefreshToken(
        userDto.id(),
        issued.sessionId(),
        issued.currentRefreshJti(),
        issuedAt
    );

    return authMapper.signInDtoFrom(userDto, accessToken, refreshToken);
  }

  private CustomUserDetails authenticate(SignInRequest request) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          UsernamePasswordAuthenticationToken.unauthenticated(
              request.username(),
              request.password()
          )
      );
      return (CustomUserDetails) authentication.getPrincipal();
    } catch (LockedException e) {
      throw AccountLockedException.withNone();
    } catch (AuthenticationException e) {
      throw InvalidCredentialsException.withNone();
    }
  }
}
