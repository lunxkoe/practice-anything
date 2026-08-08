package authsystem.security.web.config;

import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer.AuthorizationManagerRequestMatcherRegistry;

public interface SecurityAuthorizationRules {

  void configure(AuthorizationManagerRequestMatcherRegistry registry);
}
