package authsystem.auth.config;

import authsystem.auth.authentication.TempPasswordAuthenticationProvider;
import authsystem.security.core.port.SecurityUserPort;
import authsystem.temppassword.registry.TempPasswordRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;

@Configuration
public class AuthAuthenticationProviderConfig {

  @Bean
  public AuthenticationProvider tempPasswordAuthenticationProvider(
      SecurityUserPort securityUserPort, TempPasswordRegistry tempPasswordRegistry) {
    return new TempPasswordAuthenticationProvider(securityUserPort, tempPasswordRegistry);
  }
}
