package lunxkoe.practice.security.token.provider.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.ProtectedHeader;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import lunxkoe.practice.security.token.dto.AccessTokenClaims;
import lunxkoe.practice.security.token.dto.RefreshTokenClaims;
import lunxkoe.practice.security.token.exception.business.ExpiredTokenException;
import lunxkoe.practice.security.token.exception.business.InvalidAccessTokenException;
import lunxkoe.practice.security.token.exception.business.InvalidRefreshTokenException;
import lunxkoe.practice.security.token.exception.business.TokenException;
import lunxkoe.practice.security.token.exception.system.TokenProviderException;
import lunxkoe.practice.security.token.properties.TokenProperties;
import lunxkoe.practice.security.token.provider.TokenProvider;

@Slf4j
public class JjwtTokenProvider implements TokenProvider {

  // 알고리즘 정책
  private static final String EXPECTED_ALGORITHM = Jwts.SIG.HS256.getId();

  private static final String CLAIM_SESSION_ID = "sid";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_TOKEN_TYPE = "typ";

  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";

  private final TokenProperties tokenProperties;
  private final Clock clock;
  private final SecretKey accessSecretKey;
  private final SecretKey refreshSecretKey;

  public JjwtTokenProvider(TokenProperties tokenProperties, Clock clock) {
    this.tokenProperties = tokenProperties;
    this.clock = clock;

    try {
      byte[] accessSecret = Base64.getDecoder().decode(tokenProperties.accessSecret());
      byte[] refreshSecret = Base64.getDecoder().decode(tokenProperties.refreshSecret());
      this.accessSecretKey = Keys.hmacShaKeyFor(accessSecret);
      this.refreshSecretKey = Keys.hmacShaKeyFor(refreshSecret);
    } catch (WeakKeyException e) {
      throw TokenProviderException.withMessageAndCause("토큰 키 초기화 실패", e);
    } catch (IllegalArgumentException e) {
      // Base64.getDecoder().decode()가 잘못된 base64 형식일 때 던짐
      throw TokenProviderException.withMessageAndCause("토큰 시크릿 base64 디코딩 실패", e);
    }
  }

  @Override
  public String createAccessToken(UUID userId, UUID sessionId, String role, Instant now) {
    return Jwts.builder()
        .issuer(tokenProperties.issuer())
        .audience().add(tokenProperties.audience()).and()
        .subject(userId.toString())
        .claim(CLAIM_SESSION_ID, sessionId.toString())
        .claim(CLAIM_ROLE, role)
        .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(tokenProperties.accessTokenExpiration())))
        .signWith(accessSecretKey, Jwts.SIG.HS256)
        .compact();
  }

  @Override
  public String createRefreshToken(UUID userId, UUID sessionId, UUID jti, Instant now) {
    return Jwts.builder()
        .issuer(tokenProperties.issuer())
        .audience().add(tokenProperties.audience()).and()
        .subject(userId.toString())
        .claim(CLAIM_SESSION_ID, sessionId.toString())
        .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
        .id(jti.toString())
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plus(tokenProperties.refreshTokenExpiration())))
        .signWith(refreshSecretKey, Jwts.SIG.HS256)
        .compact();
  }

  @Override
  public AccessTokenClaims parseAccessToken(String token) {
    Claims claims = verifyAndParse(
        token,
        accessSecretKey,
        TOKEN_TYPE_ACCESS,
        InvalidAccessTokenException::withNone,
        InvalidAccessTokenException::withCause
    );

    try {
      String subject = claims.getSubject();
      String sid = claims.get(CLAIM_SESSION_ID, String.class);
      String role = claims.get(CLAIM_ROLE, String.class);

      if (subject == null || sid == null || role == null || role.isBlank()) {
        throw InvalidAccessTokenException.withNone();
      }

      return new AccessTokenClaims(
          UUID.fromString(subject),
          UUID.fromString(sid),
          role
      );
    } catch (IllegalArgumentException e) {
      throw InvalidAccessTokenException.withCause(e);
    }
  }

  @Override
  public RefreshTokenClaims parseRefreshToken(String token) {
    Claims claims = verifyAndParse(
        token,
        refreshSecretKey,
        TOKEN_TYPE_REFRESH,
        InvalidRefreshTokenException::withNone,
        InvalidRefreshTokenException::withCause
    );

    try {
      String subject = claims.getSubject();
      String sid = claims.get(CLAIM_SESSION_ID, String.class);
      String jti = claims.getId();

      if (subject == null || sid == null || jti == null) {
        throw InvalidRefreshTokenException.withNone();
      }

      return new RefreshTokenClaims(
          UUID.fromString(subject),
          UUID.fromString(sid),
          UUID.fromString(jti)
      );
    } catch (IllegalArgumentException e) {
      throw InvalidRefreshTokenException.withCause(e);
    }
  }

  private Claims verifyAndParse(
      String token,
      SecretKey key,
      String expectedTokenType,
      Supplier<? extends TokenException> invalidTokenSupplier,
      Function<Throwable, ? extends TokenException> invalidTokenFactory
  ) {
    try {
      Claims claims = Jwts.parser()
          .clock(() -> Date.from(Instant.now(clock)))
          .keyLocator(header -> {
            // 서명 검증 전, 헤더의 alg 값을 먼저 확인 (Nimbus 버전의 사전 알고리즘 체크와 동일한 의도)
            String algorithm = (header instanceof ProtectedHeader protectedHeader)
                ? protectedHeader.getAlgorithm()
                : null;
            if (!EXPECTED_ALGORITHM.equals(algorithm)) {
              // 허용하지 않는 알고리즘
              log.warn("token rejected: unexpected algorithm={}", algorithm);
              throw invalidTokenSupplier.get();
            }
            return key;
          })
          .build()
          .parseSignedClaims(token)
          .getPayload();

      if (!tokenProperties.issuer().equals(claims.getIssuer())) {
        // issuer 검증
        log.warn("token rejected: issuer mismatch, actual={}", claims.getIssuer());
        throw invalidTokenSupplier.get();
      }

      if (claims.getAudience() == null
          || !claims.getAudience().contains(tokenProperties.audience())) {
        // audience 검증
        log.warn("token rejected: audience mismatch, actual={}", claims.getAudience());
        throw invalidTokenSupplier.get();
      }

      String actualTokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
      if (!expectedTokenType.equals(actualTokenType)) {
        // access <-> refresh 크로스 유즈 차단
        log.warn("token rejected: type mismatch, expected={}, actual={}", expectedTokenType, actualTokenType);
        throw invalidTokenSupplier.get();
      }

      return claims;

    } catch (ExpiredJwtException e) {
      // 정상 토큰이지만 만료 (refresh | 재로그인 대상)
      Claims expiredClaims = e.getClaims();
      log.debug("token expired: sub={}, exp={}", expiredClaims.getSubject(), expiredClaims.getExpiration());
      throw ExpiredTokenException.withNone();
    } catch (SignatureException e) {
      // 서명 불일치: 원인 예외 없이 던짐
      log.warn("token rejected: signature mismatch");
      throw invalidTokenSupplier.get();
    } catch (JwtException | IllegalArgumentException e) {
      // 기타 구조 깨짐/형식 오류: cause 보존
      log.warn("token rejected: parse/verify error", e);
      throw invalidTokenFactory.apply(e);
    }
  }
}
