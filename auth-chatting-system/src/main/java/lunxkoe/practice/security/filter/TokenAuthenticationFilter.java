package lunxkoe.practice.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import lunxkoe.practice.security.details.CustomUserPrincipal;
import lunxkoe.practice.security.token.dto.AccessTokenClaims;
import lunxkoe.practice.security.token.exception.business.ExpiredTokenException;
import lunxkoe.practice.security.token.exception.business.TokenException;
import lunxkoe.practice.security.token.provider.TokenProvider;
import lunxkoe.practice.security.usersession.exception.UserSessionException;
import lunxkoe.practice.security.usersession.exception.UserSessionExpiredException;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";
  private static final String TOKEN_EXPIRED_HEADER = "X-Token-Expired";
  private static final String USER_SESSION_EXPIRED_HEADER = "X-User-Session-Expired";

  private final TokenProvider tokenProvider;
  private final UserSessionRegistry userSessionRegistry;

  public TokenAuthenticationFilter(TokenProvider tokenProvider, UserSessionRegistry userSessionRegistry) {
    this.tokenProvider = tokenProvider;
    this.userSessionRegistry = userSessionRegistry;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    String token = resolveToken(request);
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    try {
      authenticateAccessToken(token);
    } catch (ExpiredTokenException e) {
      response.setHeader(TOKEN_EXPIRED_HEADER, "true");
      log.warn("만료된 토큰, Refresh 필요");
    } catch (UserSessionExpiredException e) {
      response.setHeader(USER_SESSION_EXPIRED_HEADER, "true");
      log.warn("사용자 세션이 만료되었습니다. 재로그인 필요");
    } catch (TokenException | UserSessionException e) {
      log.warn("인증 실패: {}", e.getMessage());
    } catch (RuntimeException e) {
      log.error("토큰 인증 처리 중 예상하지 못한 예외 발생", e);
    }

    filterChain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER_PREFIX)) {
      return authorization.substring(BEARER_PREFIX.length());
    }

    return null;
  }

  private void authenticateAccessToken(String token) {
    // 현재 토큰을 사용할 수 있는 기간인지 확인
    AccessTokenClaims claims = tokenProvider.parseAccessToken(token);

    // 현재 사용자 세션이 살아있는지 확인
    userSessionRegistry.verifyUserSession(claims.userId(), claims.sessionId());

    // 현재 로그인한 사용자의 인가 정보 추출
    CustomUserPrincipal userPrincipal = new CustomUserPrincipal(claims.userId(), claims.role());
    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(userPrincipal.role()));
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userPrincipal, null, authorities);

    SecurityContext context = SecurityContextHolder.createEmptyContext();
    context.setAuthentication(authToken);
    SecurityContextHolder.setContext(context);
  }
}
