package authsystem.temppassword.properties;

import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth-system.temp-password")
public record TempPasswordProperties(

    @NotNull(message = "expiration은 필수 값입니다.")
    @DurationMin(minutes = 3, message = "expiration은 최소 3분 이상이어야 합니다.")
    Duration expiration
) {

}
