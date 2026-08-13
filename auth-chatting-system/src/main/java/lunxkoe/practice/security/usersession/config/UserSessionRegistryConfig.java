package lunxkoe.practice.security.usersession.config;

import java.time.Clock;
import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.ExpirationPolicy;
import lunxkoe.practice.security.usersession.properties.UserSessionProperties;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;
import lunxkoe.practice.security.usersession.registry.impl.UserSessionRedisRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class UserSessionRegistryConfig {

  @Bean
  public UserSessionRegistry userSessionRegistry(
      UserSessionProperties properties,
      StringRedisTemplate redisTemplate,
      ExpirationPolicy expirationPolicy,
      ConcurrentPolicy concurrentPolicy,
      Clock clock) {
    return switch (properties.impl()) {
      case REDIS -> new UserSessionRedisRegistry(redisTemplate, expirationPolicy, concurrentPolicy, clock);
    };
  }
}
