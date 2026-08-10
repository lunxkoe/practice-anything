package authsystem.userapi.config;

import authsystem.auth.dto.response.SignInDto;
import authsystem.auth.exception.OAuth2FailureCode;
import authsystem.auth.service.OAuth2SignInService;
import authsystem.security.web.cookie.RefreshTokenCookieProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

  private final OAuth2SignInService oAuth2SignInService;
  private final RefreshTokenCookieProvider refreshTokenCookieProvider;
  private final OAuth2Properties oAuth2Properties;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
      Authentication authentication) throws IOException {

    OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) authentication;
    OAuth2User oAuth2User = token.getPrincipal();
    String registrationId = token.getAuthorizedClientRegistrationId(); // "google" | "kakao"

    OAuth2Identity identity = resolveIdentity(registrationId, oAuth2User);

    try {
      SignInDto result = oAuth2SignInService.signIn(identity.provider(), identity.providerId(),
          identity.email(), identity.name());
      refreshTokenCookieProvider.attach(response, result.refreshToken());
      response.sendRedirect(oAuth2Properties.successRedirectUri());
    } catch (RuntimeException e) {
      String errorMessage = (e instanceof OAuth2FailureCode failureCode)
          ? failureCode.errorCode()
          : "oauth_processing_failed";
      log.warn("OAuth2 로그인 처리 실패: provider={}, reason={}", registrationId, errorMessage, e);
      OAuth2RedirectSupport.redirectWithError(response, oAuth2Properties.failureRedirectUri(),
          errorMessage);
    }
  }

  private OAuth2Identity resolveIdentity(String registrationId, OAuth2User oAuth2User) {
    return switch (registrationId) {
      case "google" -> new OAuth2Identity("GOOGLE", oAuth2User.getName(), // sub 클레임
          oAuth2User.getAttribute("email"), oAuth2User.getAttribute("name"));
      case "kakao" -> resolveKakaoIdentity(oAuth2User);
      default -> throw new IllegalStateException("지원하지 않는 provider: " + registrationId);
    };
  }

  private OAuth2Identity resolveKakaoIdentity(OAuth2User oAuth2User) {
    // 카카오의 최상위 id는 JSON에서 숫자다 — getAttribute()는 <A> A를 반환하는 제네릭
    // 메서드라, 결과를 변수에 담지 않고 바로 String.valueOf(...)에 넘기면 A가 뭘로
    // 추론될지 애매해져서 컴파일러가 String.valueOf(char[]) 오버로드를 골라버릴 수 있다
    // (그러면 런타임에 Long -> char[] ClassCastException). Object로 먼저 받아서
    // 오버로드 선택을 명확하게 고정한다.
    Object rawId = oAuth2User.getAttribute("id");
    String providerId = String.valueOf(rawId);

    Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
    Map<String, Object> profile = kakaoAccount != null
        ? (Map<String, Object>) kakaoAccount.get("profile")
        : null;
    String nickname = profile != null && profile.get("nickname") != null
        ? (String) profile.get("nickname")
        : "kakao-user";

    // 카카오는 이메일을 못 받아올 수 있으므로 가상 이메일을 만든다.
    String virtualEmail = nickname + "_" + providerId + "@kakao.com";
    return new OAuth2Identity("KAKAO", providerId, virtualEmail, nickname);
  }

  private record OAuth2Identity(String provider, String providerId, String email, String name) {

  }
}
