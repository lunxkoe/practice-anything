package authsystem.adminapi.config;

import authsystem.security.web.config.SecurityAuthorizationRules;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.stereotype.Component;

@Component
public class AdminApiAuthorizationRules implements SecurityAuthorizationRules {

  @Override
  public void configure(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
    registry
        .requestMatchers(HttpMethod.GET, "/api/users").hasAuthority("ADMIN")
        .requestMatchers(HttpMethod.PATCH, "/api/users/{userId}/role").hasAnyAuthority("ADMIN")
        .requestMatchers(HttpMethod.PATCH, "/api/users/{userId}/lock").hasAnyAuthority("ADMIN");
  }
}
