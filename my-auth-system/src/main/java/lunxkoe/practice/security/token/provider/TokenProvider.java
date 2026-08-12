package lunxkoe.practice.security.token.provider;

import java.time.Instant;
import java.util.UUID;
import lunxkoe.practice.security.token.dto.AccessTokenClaims;
import lunxkoe.practice.security.token.dto.RefreshTokenClaims;

public interface TokenProvider {

  String createAccessToken(UUID userId, UUID sessionId, String role, Instant now);

  String createRefreshToken(UUID userId, UUID sessionId, UUID jti, Instant now);

  AccessTokenClaims parseAccessToken(String token);

  RefreshTokenClaims parseRefreshToken(String token);
}
