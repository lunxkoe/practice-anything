package lunxkoe.practice.security.token.provider.impl;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
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
public class NimbusTokenProvider implements TokenProvider {

  // 알고리즘/헤더 정책
  private static final JWSAlgorithm ALGORITHM = JWSAlgorithm.HS256;
  private static final JWSHeader JWS_HEADER = new JWSHeader.Builder(ALGORITHM)
      .type(JOSEObjectType.JWT)
      .build();

  private static final String CLAIM_SESSION_ID = "sid";
  private static final String CLAIM_ROLE = "role";
  private static final String CLAIM_TOKEN_TYPE = "typ";

  private static final String TOKEN_TYPE_ACCESS = "access";
  private static final String TOKEN_TYPE_REFRESH = "refresh";

  private final TokenProperties tokenProperties;
  private final Clock clock;
  private final JWSSigner accessSigner;
  private final JWSVerifier accessVerifier;
  private final JWSSigner refreshSigner;
  private final JWSVerifier refreshVerifier;

  public NimbusTokenProvider(TokenProperties tokenProperties, Clock clock) {
    this.tokenProperties = tokenProperties;
    this.clock = clock;

    try {
      byte[] accessSecret = Base64.getDecoder().decode(tokenProperties.accessSecret());
      byte[] refreshSecret = Base64.getDecoder().decode(tokenProperties.refreshSecret());
      this.accessSigner = new MACSigner(accessSecret);
      this.accessVerifier = new MACVerifier(accessSecret);
      this.refreshSigner = new MACSigner(refreshSecret);
      this.refreshVerifier = new MACVerifier(refreshSecret);
    } catch (JOSEException e) {
      throw TokenProviderException.withMessageAndCause("토큰 키 초기화 실패", e);
    } catch (IllegalArgumentException e) {
      throw TokenProviderException.withMessageAndCause("토큰 시크릿 base64 디코딩 실패", e);
    }
  }

  @Override
  public String createAccessToken(UUID userId, UUID sessionId, String role, Instant now) {
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .issuer(tokenProperties.issuer())
        .audience(tokenProperties.audience())
        .subject(userId.toString())
        .claim(CLAIM_SESSION_ID, sessionId.toString())
        .claim(CLAIM_ROLE, role)
        .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS)
        .issueTime(Date.from(now))
        .expirationTime(Date.from(now.plus(tokenProperties.accessTokenExpiration())))
        .build();
    return sign(claims, accessSigner);
  }

  @Override
  public String createRefreshToken(UUID userId, UUID sessionId, UUID jti, Instant now) {
    JWTClaimsSet claims = new JWTClaimsSet.Builder()
        .issuer(tokenProperties.issuer())
        .audience(tokenProperties.audience())
        .subject(userId.toString())
        .claim(CLAIM_SESSION_ID, sessionId.toString())
        .claim(CLAIM_TOKEN_TYPE, TOKEN_TYPE_REFRESH)
        .jwtID(jti.toString())
        .issueTime(Date.from(now))
        .expirationTime(Date.from(now.plus(tokenProperties.refreshTokenExpiration())))
        .build();
    return sign(claims, refreshSigner);
  }

  private String sign(JWTClaimsSet claims, JWSSigner signer) {
    try {
      SignedJWT jwt = new SignedJWT(JWS_HEADER, claims);
      jwt.sign(signer);
      return jwt.serialize();
    } catch (JOSEException e) {
      throw TokenProviderException.withMessageAndCause("토큰 서명 실패", e);
    }
  }

  @Override
  public AccessTokenClaims parseAccessToken(String token) {
    JWTClaimsSet claims = verifyAndParse(
        token,
        accessVerifier,
        TOKEN_TYPE_ACCESS,
        InvalidAccessTokenException::withNone,
        InvalidAccessTokenException::withCause
    );

    try {
      String subject = claims.getSubject();
      String sid = claims.getStringClaim(CLAIM_SESSION_ID);
      String role = claims.getStringClaim(CLAIM_ROLE);

      if (subject == null || sid == null || role == null || role.isBlank()) {
        throw InvalidAccessTokenException.withNone();
      }

      return new AccessTokenClaims(
          UUID.fromString(subject),
          UUID.fromString(sid),
          role
      );

    } catch (ParseException | IllegalArgumentException e) {
      throw InvalidAccessTokenException.withCause(e);
    }
  }

  @Override
  public RefreshTokenClaims parseRefreshToken(String token) {
    JWTClaimsSet claims = verifyAndParse(
        token,
        refreshVerifier,
        TOKEN_TYPE_REFRESH,
        InvalidRefreshTokenException::withNone,
        InvalidRefreshTokenException::withCause
    );

    try {
      String subject = claims.getSubject();
      String sid = claims.getStringClaim(CLAIM_SESSION_ID);
      String jti = claims.getJWTID();

      if (subject == null || sid == null || jti == null) {
        throw InvalidRefreshTokenException.withNone();
      }

      return new RefreshTokenClaims(
          UUID.fromString(subject),
          UUID.fromString(sid),
          UUID.fromString(jti)
      );

    } catch (ParseException | IllegalArgumentException e) {
      throw InvalidRefreshTokenException.withCause(e);
    }
  }

  private JWTClaimsSet verifyAndParse(
      String token,
      JWSVerifier verifier,
      String expectedTokenType,
      Supplier<? extends TokenException> invalidTokenSupplier,
      Function<Throwable, ? extends TokenException> invalidTokenFactory
  ) {
    try {
      SignedJWT jwt = SignedJWT.parse(token);

      if (!ALGORITHM.equals(jwt.getHeader().getAlgorithm())) {
        // 허용하지 않는 알고리즘
        log.warn("token rejected: unexpected algorithm={}", jwt.getHeader().getAlgorithm());
        throw invalidTokenSupplier.get();
      }

      if (!jwt.verify(verifier)) {
        // 서명 불일치
        log.warn("token rejected: signature mismatch");
        throw invalidTokenSupplier.get();
      }

      JWTClaimsSet claims = jwt.getJWTClaimsSet();

      if (!tokenProperties.issuer().equals(claims.getIssuer())) {
        // issuer 검증
        log.warn("token rejected: issuer mismatch, actual={}", claims.getIssuer());
        throw invalidTokenSupplier.get();
      }

      if (!claims.getAudience().contains(tokenProperties.audience())) {
        // audience 검증 (aud는 List<String>으로 옴)
        log.warn("token rejected: audience mismatch, actual={}", claims.getAudience());
        throw invalidTokenSupplier.get();
      }

      String actualTokenType = claims.getStringClaim(CLAIM_TOKEN_TYPE);
      if (!expectedTokenType.equals(actualTokenType)) {
        // access <-> refresh 크로스 유즈 차단
        log.warn("token rejected: type mismatch, expected={}, actual={}", expectedTokenType, actualTokenType);
        throw invalidTokenSupplier.get();
      }

      Date expiration = claims.getExpirationTime();
      Instant now = Instant.now(clock);
      if (expiration == null || !now.isBefore(expiration.toInstant())) {
        // 정상 토큰이지만 만료 (refresh | 재로그인 대상)
        log.debug("token expired: sub={}, exp={}", claims.getSubject(), expiration);
        throw ExpiredTokenException.withNone();
      }

      return claims;
    } catch (ParseException | JOSEException e) {
      // 구조 깨짐 / 검증 오류
      log.warn("token rejected: parse/verify error", e);
      throw invalidTokenFactory.apply(e);
    }
  }
}
