package lunxkoe.practice.security.usersession.config;

import java.time.Clock;
import lunxkoe.practice.security.usersession.policy.ConcurrentPolicy;
import lunxkoe.practice.security.usersession.policy.ExpirationPolicy;
import lunxkoe.practice.security.usersession.properties.UserSessionProperties;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;
import lunxkoe.practice.security.usersession.registry.impl.UserSessionRedisRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties({
    UserSessionProperties.class
})
public class UserSessionRegistryConfig {

  @Bean
  @ConditionalOnProperty(name = "app.security.user-session.impl", havingValue = "redis", matchIfMissing = true)
  public UserSessionRegistry userSessionRegistry(
      StringRedisTemplate redisTemplate,
      ExpirationPolicy expirationPolicy,
      ConcurrentPolicy concurrentPolicy,
      Clock clock
  ) {
    return new UserSessionRedisRegistry(redisTemplate, expirationPolicy, concurrentPolicy, clock);
  }
}
