package authsystem.security.session.config;

import authsystem.security.core.session.policy.UserSessionExpirationPolicy;
import authsystem.security.core.session.policy.impl.AbsoluteExpirationPolicy;
import authsystem.security.core.session.policy.impl.SlidingExpirationPolicy;
import authsystem.security.core.session.properties.UserSessionProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserSessionExpirationPolicyConfig {

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.user-session.expiration-policy", havingValue = "absolute", matchIfMissing = true)
  public UserSessionExpirationPolicy absoluteUserSessionExpirationPolicy(
      UserSessionProperties userSessionProperties) {
    return new AbsoluteExpirationPolicy(userSessionProperties.userSessionExpiration());
  }

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.user-session.expiration-policy", havingValue = "sliding")
  public UserSessionExpirationPolicy slidingUserSessionExpirationPolicy(
      UserSessionProperties userSessionProperties) {
    return new SlidingExpirationPolicy(userSessionProperties.userSessionExpiration());
  }
}
