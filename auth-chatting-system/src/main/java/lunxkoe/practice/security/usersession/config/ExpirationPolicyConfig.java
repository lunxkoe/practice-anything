package lunxkoe.practice.security.usersession.config;

import lunxkoe.practice.security.usersession.policy.ExpirationPolicy;
import lunxkoe.practice.security.usersession.policy.impl.AbsoluteExpirationPolicy;
import lunxkoe.practice.security.usersession.policy.impl.SlidingExpirationPolicy;
import lunxkoe.practice.security.usersession.properties.UserSessionProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpirationPolicyConfig {

  @Bean
  @ConditionalOnProperty(name = "app.security.user-session.expiration-policy", havingValue = "absolute", matchIfMissing = true)
  public ExpirationPolicy absoluteExpirationPolicy(UserSessionProperties properties) {
    return new AbsoluteExpirationPolicy(properties.ttl());
  }

  @Bean
  @ConditionalOnProperty(name = "app.security.user-session.expiration-policy", havingValue = "sliding")
  public ExpirationPolicy slidingExpirationPolicy(UserSessionProperties properties) {
    return new SlidingExpirationPolicy(properties.ttl());
  }
}
