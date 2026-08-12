package lunxkoe.practice.security.token.config;

import lunxkoe.practice.security.token.properties.TokenProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({TokenProperties.class})
public class TokenProviderConfig {

}
