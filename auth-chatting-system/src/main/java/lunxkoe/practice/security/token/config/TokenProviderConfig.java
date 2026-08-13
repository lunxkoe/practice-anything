package lunxkoe.practice.security.token.config;

import java.time.Clock;
import lunxkoe.practice.security.token.properties.TokenProperties;
import lunxkoe.practice.security.token.provider.TokenProvider;
import lunxkoe.practice.security.token.provider.impl.JjwtTokenProvider;
import lunxkoe.practice.security.token.provider.impl.NimbusTokenProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    TokenProperties.class
})
public class TokenProviderConfig {

  @Bean
  @ConditionalOnProperty(name = "app.security.token.impl", havingValue = "nimbus", matchIfMissing = true)
  public TokenProvider nimbusTokenProvider(TokenProperties tokenProperties, Clock clock) {
    return new NimbusTokenProvider(tokenProperties, clock);
  }

  @Bean
  @ConditionalOnProperty(name = "app.security.token.impl", havingValue = "jjwt")
  public TokenProvider jjwtTokenProvider(TokenProperties tokenProperties, Clock clock) {
    return new JjwtTokenProvider(tokenProperties, clock);
  }
}
