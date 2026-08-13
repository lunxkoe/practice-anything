package lunxkoe.practice.security.usersession.config;

import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.impl.MaxDeviceConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.impl.MultiDeviceConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.impl.SingleDeviceConcurrentPolicy;
import lunxkoe.practice.security.usersession.properties.UserSessionProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcurrentPolicyConfig {

  @Bean
  @ConditionalOnProperty(
      name = "app.security.user-session.concurrent-policy", havingValue = "multi", matchIfMissing = true)
  public ConcurrentPolicy multiDeviceConcurrentPolicy() {
    return new MultiDeviceConcurrentPolicy();
  }

  @Bean
  @ConditionalOnProperty(name = "app.security.user-session.concurrent-policy", havingValue = "single")
  public ConcurrentPolicy singleDeviceConcurrentPolicy() {
    return new SingleDeviceConcurrentPolicy();
  }

  @Bean
  @ConditionalOnProperty(name = "app.security.user-session.concurrent-policy", havingValue = "max")
  public ConcurrentPolicy maxDeviceConcurrentPolicy(UserSessionProperties properties) {
    return new MaxDeviceConcurrentPolicy(properties.maxDevices());
  }
}
