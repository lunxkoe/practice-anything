package authsystem.temppassword.config;

import authsystem.temppassword.generator.TempPasswordGenerator;
import authsystem.temppassword.properties.TempPasswordProperties;
import authsystem.temppassword.registry.TempPasswordRegistry;
import authsystem.temppassword.registry.impl.TempPasswordRedisRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties({TempPasswordProperties.class})
public class TempPasswordRegistryConfig {

  @Bean
  @ConditionalOnProperty(name = "auth-system.temp-password.impl", havingValue = "redis", matchIfMissing = true)
  public TempPasswordRegistry tempPasswordRedisRegistry(StringRedisTemplate redisTemplate,
      TempPasswordProperties tempPasswordProperties, TempPasswordGenerator tempPasswordGenerator,
      PasswordEncoder passwordEncoder) {
    return new TempPasswordRedisRegistry(redisTemplate, tempPasswordProperties,
        tempPasswordGenerator, passwordEncoder);
  }
}
