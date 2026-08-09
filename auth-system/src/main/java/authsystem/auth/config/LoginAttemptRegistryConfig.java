package authsystem.auth.config;

import authsystem.auth.loginattempt.properties.LoginAttemptProperties;
import authsystem.auth.loginattempt.registry.LoginAttemptRegistry;
import authsystem.auth.loginattempt.registry.impl.LoginAttemptRedisRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
@EnableConfigurationProperties({
    LoginAttemptProperties.class
})
public class LoginAttemptRegistryConfig {

  @Bean
  @ConditionalOnProperty(name = "auth-system.security.login-attempt.impl", havingValue = "redis", matchIfMissing = true)
  public LoginAttemptRegistry loginAttemptRedisRegistry(
      StringRedisTemplate redisTemplate,
      LoginAttemptProperties properties
  ) {
    return new LoginAttemptRedisRegistry(redisTemplate, properties);
  }
}
