package authsystem.security.jwt.config;

import authsystem.security.core.token.properties.TokenProperties;
import authsystem.security.core.token.provider.TokenProvider;
import authsystem.security.jwt.provider.JjwtTokenProvider;
import authsystem.security.jwt.provider.NimbusTokenProvider;
import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenProviderConfig {

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.token.impl", havingValue = "nimbus", matchIfMissing = true)
  public TokenProvider nimbusTokenProvider(TokenProperties tokenProperties, Clock clock) {
    return new NimbusTokenProvider(tokenProperties, clock);
  }

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.token.impl", havingValue = "jjwt")
  public TokenProvider jjwtTokenProvider(TokenProperties tokenProperties, Clock clock) {
    return new JjwtTokenProvider(tokenProperties, clock);
  }
}
