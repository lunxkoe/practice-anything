package lunxkoe.practice.security.config;

import lunxkoe.practice.security.cookie.properties.RefreshTokenCookieProperties;
import lunxkoe.practice.security.token.properties.TokenProperties;
import lunxkoe.practice.security.usersession.properties.UserSessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
    TokenProperties.class,
    UserSessionProperties.class,
    RefreshTokenCookieProperties.class
})
public class SecurityPropertiesConfig {

}
