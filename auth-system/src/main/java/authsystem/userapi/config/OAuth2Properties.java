package authsystem.userapi.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "auth-system.oauth2")
public record OAuth2Properties(
    @NotBlank String successRedirectUri,
    @NotBlank String failureRedirectUri
) {

}
