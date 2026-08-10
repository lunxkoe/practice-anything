package authsystem.userapi.config;

import authsystem.security.web.config.SecurityAuthorizationRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class UserApiAuthorizationRules implements SecurityAuthorizationRules {

  @Override
  public void configure(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
    registry
        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/auth/sign-in", "/api/auth/sign-out",
            "/api/auth/refresh", "/api/auth/reset-password").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/auth/csrf-token").permitAll()
        .requestMatchers("/", "/index.html", "/favicon.ico", "/css/**", "/js/**", "/images/**",
            "/assets/**", "/logo_symbol.svg", "/vite.svg").permitAll()
        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
        .requestMatchers("/uploads/**").permitAll()
        .requestMatchers("/oauth2/authorization/**", "/login/oauth2/code/**").permitAll();
  }
}
