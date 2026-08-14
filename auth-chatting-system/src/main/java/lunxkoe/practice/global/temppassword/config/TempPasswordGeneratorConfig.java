package lunxkoe.practice.global.temppassword.config;

import lunxkoe.practice.global.temppassword.generator.TempPasswordGenerator;
import lunxkoe.practice.global.temppassword.generator.impl.RandomTempPasswordGenerator;
import lunxkoe.practice.global.temppassword.properties.TempPasswordProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TempPasswordGeneratorConfig {

  @Bean
  public TempPasswordGenerator tempPasswordGenerator(TempPasswordProperties tempPasswordProperties) {
    return switch (tempPasswordProperties.generator()) {
      case RANDOM -> new RandomTempPasswordGenerator();
    };
  }
}
