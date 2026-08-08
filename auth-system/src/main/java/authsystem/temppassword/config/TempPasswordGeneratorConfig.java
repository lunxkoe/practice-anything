package authsystem.temppassword.config;

import authsystem.temppassword.generator.TempPasswordGenerator;
import authsystem.temppassword.generator.impl.FixedTempPasswordGenerator;
import authsystem.temppassword.generator.impl.RandomTempPasswordGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TempPasswordGeneratorConfig {

  @Bean
  @ConditionalOnProperty(name = "auth-system.temp-password.generator", havingValue = "random", matchIfMissing = true)
  public TempPasswordGenerator randomTempPasswordGenerator() {
    return new RandomTempPasswordGenerator();
  }

  @Bean
  @ConditionalOnProperty(name = "auth-system.temp-password.generator", havingValue = "fixed")
  public TempPasswordGenerator fixedTempPasswordGenerator() {
    return new FixedTempPasswordGenerator();
  }
}
