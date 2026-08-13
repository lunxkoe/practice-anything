package lunxkoe.practice.security.usersession.config;

import lunxkoe.practice.security.usersession.policy.ExpirationPolicy;
import lunxkoe.practice.security.usersession.policy.impl.AbsoluteExpirationPolicy;
import lunxkoe.practice.security.usersession.policy.impl.SlidingExpirationPolicy;
import lunxkoe.practice.security.usersession.properties.UserSessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExpirationPolicyConfig {

  @Bean
  public ExpirationPolicy expirationPolicy(UserSessionProperties properties) {
    return switch (properties.expirationPolicy()) {
      case ABSOLUTE -> new AbsoluteExpirationPolicy(properties.ttl());
      case SLIDING -> new SlidingExpirationPolicy(properties.ttl());
    };
  }
}
