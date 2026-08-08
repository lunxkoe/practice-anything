package authsystem.init;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "auth-system.admin")
@Validated
public record AdminProperties(
    @NotBlank String name,
    @NotBlank String email,
    @NotBlank String password
) {

}
