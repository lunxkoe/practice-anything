package authsystem.userapi.config;


import authsystem.security.web.config.HttpSecurityCustomizer;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSecurityCustomizer implements HttpSecurityCustomizer {

  private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
  private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;

  @Override
  public void customize(HttpSecurity http) throws Exception {
    http.oauth2Login(oauth2 -> oauth2
        .successHandler(oAuth2LoginSuccessHandler)
        .failureHandler(oAuth2LoginFailureHandler));
  }
}
