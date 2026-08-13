package lunxkoe.practice.security.cookie.properties;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security.refresh-cookie")
public record RefreshTokenCookieProperties(

    @NotNull
    @DurationMin(seconds = 1)
    Duration refreshTokenExpiration,

    @NotNull
    Boolean secure
) {

}
