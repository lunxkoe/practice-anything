package authsystem.security.core.token.dto;

import java.util.UUID;

public record AccessTokenClaims(
    UUID userId,
    UUID sessionId,
    String role
) {

}
