package authsystem.security.core.token.dto;

import java.util.UUID;

public record RefreshTokenClaims(
    UUID userId,
    UUID sessionId,
    UUID jti
) {

}
