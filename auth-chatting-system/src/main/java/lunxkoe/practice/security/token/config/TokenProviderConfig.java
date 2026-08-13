package lunxkoe.practice.security.token.config;

import java.time.Clock;
import lunxkoe.practice.security.token.properties.TokenProperties;
import lunxkoe.practice.security.token.provider.TokenProvider;
import lunxkoe.practice.security.token.provider.impl.JjwtTokenProvider;
import lunxkoe.practice.security.token.provider.impl.NimbusTokenProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenProviderConfig {

  @Bean
  public TokenProvider tokenProvider(TokenProperties tokenProperties, Clock clock) {
    return switch (tokenProperties.impl()) {
      case NIMBUS -> new NimbusTokenProvider(tokenProperties, clock);
      case JJWT -> new JjwtTokenProvider(tokenProperties, clock);
    };
  }
}
