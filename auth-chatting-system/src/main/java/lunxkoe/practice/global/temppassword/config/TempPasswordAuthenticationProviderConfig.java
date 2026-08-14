package lunxkoe.practice.global.temppassword.config;

import lunxkoe.practice.domain.user.mapper.UserMapper;
import lunxkoe.practice.domain.user.repository.UserRepository;
import lunxkoe.practice.global.temppassword.authentication.TempPasswordAuthenticationProvider;
import lunxkoe.practice.global.temppassword.registry.TempPasswordRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TempPasswordAuthenticationProviderConfig {

  @Bean
  public TempPasswordAuthenticationProvider tempPasswordAuthenticationProvider(
      UserRepository userRepository,
      UserMapper userMapper,
      TempPasswordRegistry tempPasswordRegistry
  ) {
    return new TempPasswordAuthenticationProvider(
        userRepository,
        userMapper,
        tempPasswordRegistry
    );
  }
}
