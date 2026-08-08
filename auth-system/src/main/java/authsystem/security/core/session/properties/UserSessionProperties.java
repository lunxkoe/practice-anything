package authsystem.security.core.session.properties;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.Duration;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth-system.security.user-session")
public record UserSessionProperties(

    @NotNull
    @DurationMin(seconds = 1)
    Duration userSessionExpiration,

    @Positive
    Integer maxDevice
) {

}
