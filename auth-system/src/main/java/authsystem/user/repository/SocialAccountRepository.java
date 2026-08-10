package authsystem.user.repository;

import authsystem.user.entity.SocialAccount;
import authsystem.user.entity.enums.OAuthProvider;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialAccountRepository extends JpaRepository<SocialAccount, UUID> {

  Optional<SocialAccount> findByProviderAndProviderId(OAuthProvider provider, String providerId);
}
