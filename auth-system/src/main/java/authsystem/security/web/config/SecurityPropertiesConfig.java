package authsystem.security.web.config;

import authsystem.security.core.session.properties.UserSessionProperties;
import authsystem.security.core.token.properties.TokenProperties;
import authsystem.security.web.cookie.RefreshTokenCookieProperties;
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
