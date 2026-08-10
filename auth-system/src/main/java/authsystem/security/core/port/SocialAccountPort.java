package authsystem.security.core.port;

import java.util.Optional;

public interface SocialAccountPort {

  Optional<SecurityUserView> findLinkedUser(String provider, String providerId);

  SecurityUserView createSocialUser(String provider, String providerId, String email, String name);
}
