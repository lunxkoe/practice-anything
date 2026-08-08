package authsystem.security.session.config;

import authsystem.security.core.session.policy.ConcurrentUserSessionPolicy;
import authsystem.security.core.session.policy.UserSessionExpirationPolicy;
import authsystem.security.core.session.registry.UserSessionRegistry;
import authsystem.security.core.session.registry.decorator.ConcurrentPolicyUserSessionRegistry;
import authsystem.security.session.registry.UserSessionRedisRegistry;
import java.time.Clock;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class UserSessionRegistryConfig {

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.user-session.impl", havingValue = "redis", matchIfMissing = true)
  public UserSessionRegistry userSessionRedisRegistry(StringRedisTemplate redisTemplate,
      UserSessionExpirationPolicy userSessionExpirationPolicy,
      ConcurrentUserSessionPolicy concurrentUserSessionPolicy, Clock clock) {
    UserSessionRegistry delegate =
        new UserSessionRedisRegistry(redisTemplate, userSessionExpirationPolicy, clock);
    return new ConcurrentPolicyUserSessionRegistry(delegate, concurrentUserSessionPolicy);
  }
}
