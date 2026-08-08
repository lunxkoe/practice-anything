package authsystem.security.session.config;

import authsystem.security.core.session.policy.ConcurrentUserSessionPolicy;
import authsystem.security.core.session.policy.impl.MaxDeviceConcurrentUserSessionPolicy;
import authsystem.security.core.session.policy.impl.MultiDeviceConcurrentUserSessionPolicy;
import authsystem.security.core.session.policy.impl.SingleDeviceConcurrentUserSessionPolicy;
import authsystem.security.core.session.properties.UserSessionProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcurrentSessionPolicyConfig {

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.user-session.concurrent-policy", havingValue = "single", matchIfMissing = true)
  public ConcurrentUserSessionPolicy singleDeviceConcurrentUserSession() {
    return new SingleDeviceConcurrentUserSessionPolicy();
  }

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.user-session.concurrent-policy", havingValue = "multi")
  public ConcurrentUserSessionPolicy multiDeviceConcurrentUserSessionPolicy() {
    return new MultiDeviceConcurrentUserSessionPolicy();
  }

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.user-session.concurrent-policy", havingValue = "max")
  public ConcurrentUserSessionPolicy maxDeviceConcurrentUserSessionPolicy(
      UserSessionProperties properties) {
    return new MaxDeviceConcurrentUserSessionPolicy(properties.maxDevice());
  }
}
