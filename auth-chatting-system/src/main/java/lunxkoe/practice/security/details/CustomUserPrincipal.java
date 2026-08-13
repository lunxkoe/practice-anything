package lunxkoe.practice.security.details;

import java.util.UUID;

public record CustomUserPrincipal(
    UUID userId,
    String role
) {

}
