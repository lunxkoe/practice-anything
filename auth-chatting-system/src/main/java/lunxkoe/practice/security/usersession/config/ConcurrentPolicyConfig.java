package lunxkoe.practice.security.usersession.config;

import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.impl.MaxDeviceConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.impl.MultiDeviceConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.impl.SingleDeviceConcurrentPolicy;
import lunxkoe.practice.security.usersession.properties.UserSessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcurrentPolicyConfig {

  @Bean
  public ConcurrentPolicy concurrentPolicy(UserSessionProperties properties) {
    return switch (properties.concurrentPolicy()) {
      case MULTI -> new MultiDeviceConcurrentPolicy();
      case SINGLE -> new SingleDeviceConcurrentPolicy();
      case MAX -> new MaxDeviceConcurrentPolicy(properties.maxDevices());
    };
  }
}
