package lunxkoe.practice.global.temppassword.config;

import lunxkoe.practice.global.temppassword.generator.TempPasswordGenerator;
import lunxkoe.practice.global.temppassword.properties.TempPasswordProperties;
import lunxkoe.practice.global.temppassword.registry.TempPasswordRegistry;
import lunxkoe.practice.global.temppassword.registry.impl.TempPasswordRedisRegistry;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableConfigurationProperties({
    TempPasswordProperties.class
})
public class TempPasswordRegistryConfig {

  @Bean
  public TempPasswordRegistry tempPasswordRegistry(
      StringRedisTemplate redisTemplate,
      TempPasswordProperties tempPasswordProperties,
      TempPasswordGenerator tempPasswordGenerator,
      PasswordEncoder passwordEncoder
  ) {
    return switch (tempPasswordProperties.impl()) {
      case REDIS -> new TempPasswordRedisRegistry(
          redisTemplate,
          tempPasswordProperties,
          tempPasswordGenerator,
          passwordEncoder
      );
    };
  }
}
