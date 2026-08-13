package lunxkoe.practice.security.token.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lunxkoe.practice.security.token.dto.AccessTokenClaims;
import lunxkoe.practice.security.token.dto.RefreshTokenClaims;
import lunxkoe.practice.security.token.exception.business.ExpiredTokenException;
import lunxkoe.practice.security.token.exception.business.InvalidAccessTokenException;
import lunxkoe.practice.security.token.exception.business.InvalidRefreshTokenException;
import lunxkoe.practice.security.token.properties.TokenProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

abstract class TokenProviderContractTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private static final Instant BASE_TIME = Instant.parse("2024-01-01T00:00:00Z");
  private static final Duration ACCESS_EXPIRATION = Duration.ofMinutes(15);
  private static final Duration REFRESH_EXPIRATION = Duration.ofDays(14);
  private static final String ISSUER = "practice-auth";
  private static final String AUDIENCE = "practice-client";

  private static final UUID USER_ID = UUID.randomUUID();
  private static final UUID SESSION_ID = UUID.randomUUID();
  private static final UUID REFRESH_JTI = UUID.randomUUID();
  private static final String ROLE = "USER";

  private static final String ACCESS_SECRET = secret("access-key");
  private static final String REFRESH_SECRET = secret("refresh-key");
  private static final String OTHER_ACCESS_SECRET = secret("other-access-key");

  protected abstract TokenProvider createProvider(TokenProperties properties, Clock clock);

  private TokenProvider tokenProvider;

  @BeforeEach
  void setUp() {
    tokenProvider = providerWithClock(BASE_TIME);
  }

  @Nested
  @DisplayName("정상 발급과 파싱")
  class HappyPath {

    @Test
    @DisplayName("발급한 AccessToken을 파싱하면 발급 시 넣은 값이 그대로 복원된다")
    void accessTokenRoundTrip() {
      // given
      String token = tokenProvider.createAccessToken(USER_ID, SESSION_ID, ROLE, BASE_TIME);

      // when
      AccessTokenClaims claims = tokenProvider.parseAccessToken(token);

      // then
      assertThat(claims).isEqualTo(new AccessTokenClaims(USER_ID, SESSION_ID, ROLE));
    }

    @Test
    @DisplayName("발급한 RefreshToken을 파싱하면 발급 시 넣은 값이 그대로 복원된다")
    void refreshTokenRoundTrip() {
      // given
      String token = tokenProvider.createRefreshToken(USER_ID, SESSION_ID, REFRESH_JTI, BASE_TIME);

      // when
      RefreshTokenClaims claims = tokenProvider.parseRefreshToken(token);

      // then
      assertThat(claims).isEqualTo(new RefreshTokenClaims(USER_ID, SESSION_ID, REFRESH_JTI));
    }
  }

  @Nested
  @DisplayName("서명 검증")
  class SignatureValidation {

    @Test
    @DisplayName("페이로드가 변조된 토큰은 거부한다")
    void rejectsTamperedPayload() {
      // given
      String token = tokenProvider.createAccessToken(USER_ID, SESSION_ID, ROLE, BASE_TIME);
      String tampered = tamperPayload(token, payload -> {
        payload.put("role", "ADMIN");
        return payload;
      });

      // when // then
      assertThatThrownBy(() -> tokenProvider.parseAccessToken(tampered))
          .isInstanceOf(InvalidAccessTokenException.class);
    }

    @Test
    @DisplayName("다른 시크릿으로 서명된 토큰은 거부한다")
    void rejectsTokenSignedWithDifferentSecret() {
      // given
      TokenProvider providerWithOtherSecret = createProvider(
          properties(OTHER_ACCESS_SECRET, REFRESH_SECRET), Clock.fixed(BASE_TIME, ZoneOffset.UTC));
      String token = providerWithOtherSecret.createAccessToken(USER_ID, SESSION_ID, ROLE, BASE_TIME);

      // when // then
      assertThatThrownBy(() -> tokenProvider.parseAccessToken(token))
          .isInstanceOf(InvalidAccessTokenException.class);
    }

    @Test
    @DisplayName("헤더의 알고리즘이 기대(HS256)와 다르면 거부한다")
    void rejectsUnexpectedAlgorithm() {
      // given
      Map<String, Object> header = Map.of("alg", "HS384", "typ", "JWT");
      String token = craftToken(ACCESS_SECRET, header, validAccessPayload(BASE_TIME));

      // when // then
      assertThatThrownBy(() -> tokenProvider.parseAccessToken(token))
          .isInstanceOf(InvalidAccessTokenException.class);
    }
  }

  @Nested
  @DisplayName("만료 검증")
  class ExpirationValidation {

    @Test
    @DisplayName("만료 시각이 지난 AccessToken은 거부한다")
    void rejectsExpiredAccessToken() {
      // given
      String token = tokenProvider.createAccessToken(USER_ID, SESSION_ID, ROLE, BASE_TIME);
      TokenProvider providerAfterExpiration = providerWithClock(BASE_TIME.plus(ACCESS_EXPIRATION).plusSeconds(1));

      // when // then
      assertThatThrownBy(() -> providerAfterExpiration.parseAccessToken(token))
          .isInstanceOf(ExpiredTokenException.class);
    }

    @Test
    @DisplayName("만료 시각이 지난 RefreshToken은 거부한다")
    void rejectsExpiredRefreshToken() {
      // given
      String token = tokenProvider.createRefreshToken(USER_ID, SESSION_ID, REFRESH_JTI, BASE_TIME);
      TokenProvider providerAfterExpiration = providerWithClock(BASE_TIME.plus(REFRESH_EXPIRATION).plusSeconds(1));

      // when // then
      assertThatThrownBy(() -> providerAfterExpiration.parseRefreshToken(token))
          .isInstanceOf(ExpiredTokenException.class);
    }
  }

  @Nested
  @DisplayName("토큰 타입 교차 사용 방지")
  class TokenTypeCrossUsePrevention {

    @Test
    @DisplayName("AccessToken을 parseRefreshToken에 넣으면 거부한다")
    void rejectsAccessTokenAsRefreshToken() {
      // given
      String accessToken = tokenProvider.createAccessToken(USER_ID, SESSION_ID, ROLE, BASE_TIME);

      // when // then
      assertThatThrownBy(() -> tokenProvider.parseRefreshToken(accessToken))
          .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    @DisplayName("RefreshToken을 parseAccessToken에 넣으면 거부한다")
    void rejectsRefreshTokenAsAccessToken() {
      // given
      String refreshToken = tokenProvider.createRefreshToken(USER_ID, SESSION_ID, REFRESH_JTI, BASE_TIME);

      // when // then
      assertThatThrownBy(() -> tokenProvider.parseAccessToken(refreshToken))
          .isInstanceOf(InvalidAccessTokenException.class);
    }
  }

  @Nested
  @DisplayName("클레임/신뢰 속성 검증")
  class ClaimValidation {

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidAccessTokenCases")
    @DisplayName("AccessToken의 필수 클레임이 없거나 issuer/audience가 다르면 거부한다")
    void rejectsInvalidAccessTokenPayload(String description, UnaryOperator<Map<String, Object>> mutator) {
      // given
      Map<String, Object> payload = mutator.apply(validAccessPayload(BASE_TIME));
      String token = craftToken(ACCESS_SECRET, defaultHeader(), payload);

      // when // then
      assertThatThrownBy(() -> tokenProvider.parseAccessToken(token))
          .isInstanceOf(InvalidAccessTokenException.class);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidRefreshTokenCases")
    @DisplayName("RefreshToken의 필수 클레임이 없거나 issuer/audience가 다르면 거부한다")
    void rejectsInvalidRefreshTokenPayload(String description, UnaryOperator<Map<String, Object>> mutator) {
      // given
      Map<String, Object> payload = mutator.apply(validRefreshPayload(BASE_TIME));
      String token = craftToken(REFRESH_SECRET, defaultHeader(), payload);

      // when // then
      assertThatThrownBy(() -> tokenProvider.parseRefreshToken(token))
          .isInstanceOf(InvalidRefreshTokenException.class);
    }

    static Stream<Arguments> invalidAccessTokenCases() {
      return Stream.of(
          Arguments.of("sid 클레임이 없으면", remove("sid")),
          Arguments.of("role 클레임이 없으면", remove("role")),
          Arguments.of("role이 빈 문자열이면", put("role", "")),
          Arguments.of("sub이 UUID 형식이 아니면", put("sub", "not-a-uuid")),
          Arguments.of("issuer가 다르면", put("iss", "unexpected-issuer")),
          Arguments.of("audience가 다르면", put("aud", List.of("unexpected-audience")))
      );
    }

    static Stream<Arguments> invalidRefreshTokenCases() {
      return Stream.of(
          Arguments.of("sid 클레임이 없으면", remove("sid")),
          Arguments.of("jti 클레임이 없으면", remove("jti")),
          Arguments.of("sub이 UUID 형식이 아니면", put("sub", "not-a-uuid")),
          Arguments.of("issuer가 다르면", put("iss", "unexpected-issuer")),
          Arguments.of("audience가 다르면", put("aud", List.of("unexpected-audience")))
      );
    }
  }

  // ----- 픽스처 -----

  private TokenProperties properties(String accessSecret, String refreshSecret) {
    return new TokenProperties(
        ACCESS_EXPIRATION, REFRESH_EXPIRATION, accessSecret, refreshSecret, ISSUER, AUDIENCE);
  }

  private TokenProvider providerWithClock(Instant instant) {
    return createProvider(properties(ACCESS_SECRET, REFRESH_SECRET), Clock.fixed(instant, ZoneOffset.UTC));
  }

  private static String secret(String seed) {
    String padded = (seed + "0".repeat(32)).substring(0, 32);
    return Base64.getEncoder().encodeToString(padded.getBytes(StandardCharsets.UTF_8));
  }

  // ----- 토큰 수동 조립 (Nimbus/Jjwt API에 의존하지 않고 두 구현체에 동일하게 적용하기 위함) -----

  private static Map<String, Object> defaultHeader() {
    Map<String, Object> header = new LinkedHashMap<>();
    header.put("alg", "HS256");
    header.put("typ", "JWT");
    return header;
  }

  private static Map<String, Object> validAccessPayload(Instant now) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("iss", ISSUER);
    payload.put("aud", List.of(AUDIENCE));
    payload.put("sub", USER_ID.toString());
    payload.put("sid", SESSION_ID.toString());
    payload.put("role", ROLE);
    payload.put("typ", "access");
    payload.put("iat", now.getEpochSecond());
    payload.put("exp", now.plus(ACCESS_EXPIRATION).getEpochSecond());
    return payload;
  }

  private static Map<String, Object> validRefreshPayload(Instant now) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("iss", ISSUER);
    payload.put("aud", List.of(AUDIENCE));
    payload.put("sub", USER_ID.toString());
    payload.put("sid", SESSION_ID.toString());
    payload.put("jti", REFRESH_JTI.toString());
    payload.put("typ", "refresh");
    payload.put("iat", now.getEpochSecond());
    payload.put("exp", now.plus(REFRESH_EXPIRATION).getEpochSecond());
    return payload;
  }

  private static UnaryOperator<Map<String, Object>> remove(String claim) {
    return payload -> {
      payload.remove(claim);
      return payload;
    };
  }

  private static UnaryOperator<Map<String, Object>> put(String claim, Object value) {
    return payload -> {
      payload.put(claim, value);
      return payload;
    };
  }

  private String craftToken(String secretBase64, Map<String, Object> header, Map<String, Object> payload) {
    try {
      byte[] secretBytes = Base64.getDecoder().decode(secretBase64);
      String headerPart = base64UrlEncode(OBJECT_MAPPER.writeValueAsBytes(header));
      String payloadPart = base64UrlEncode(OBJECT_MAPPER.writeValueAsBytes(payload));
      String signingInput = headerPart + "." + payloadPart;

      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(new SecretKeySpec(secretBytes, "HmacSHA256"));
      String signaturePart = base64UrlEncode(mac.doFinal(signingInput.getBytes(StandardCharsets.UTF_8)));

      return signingInput + "." + signaturePart;
    } catch (Exception e) {
      throw new IllegalStateException("테스트용 토큰 생성 실패", e);
    }
  }

  private String tamperPayload(String token, UnaryOperator<Map<String, Object>> mutator) {
    String[] parts = token.split("\\.");
    try {
      Map<String, Object> payload = OBJECT_MAPPER.readValue(
          Base64.getUrlDecoder().decode(parts[1]), new TypeReference<Map<String, Object>>() { });
      Map<String, Object> mutated = mutator.apply(payload);
      String newPayloadPart = base64UrlEncode(OBJECT_MAPPER.writeValueAsBytes(mutated));
      return parts[0] + "." + newPayloadPart + "." + parts[2];
    } catch (Exception e) {
      throw new IllegalStateException("테스트용 토큰 변조 실패", e);
    }
  }

  private String base64UrlEncode(byte[] bytes) {
    return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
  }
}
