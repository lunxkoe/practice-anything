package authsystem.security.core.principal;

import java.util.UUID;

public record UserPrincipal(
    UUID userId,
    String role
) {

}
