package authsystem.auth.loginattempt.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth-system.security.login-attempt")
public record LoginAttemptProperties(

    @Positive
    Integer maxAttempts,

    @NotNull
    @DurationMin(minutes = 1)
    Duration window
) {

}
