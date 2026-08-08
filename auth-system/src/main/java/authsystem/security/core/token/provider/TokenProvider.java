package authsystem.security.core.token.provider;

import authsystem.security.core.token.dto.AccessTokenClaims;
import authsystem.security.core.token.dto.RefreshTokenClaims;
import java.time.Instant;
import java.util.UUID;

public interface TokenProvider {

  String createAccessToken(UUID userId, UUID sessionId, String role, Instant now);

  String createRefreshToken(UUID userId, UUID sessionId, UUID jti, Instant now);

  AccessTokenClaims parseAccessToken(String token);

  RefreshTokenClaims parseRefreshToken(String token);
}
