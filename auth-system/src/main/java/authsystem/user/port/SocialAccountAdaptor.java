package authsystem.user.port;

import authsystem.security.core.port.SecurityUserView;
import authsystem.security.core.port.SocialAccountPort;
import authsystem.user.entity.Profile;
import authsystem.user.entity.SocialAccount;
import authsystem.user.entity.User;
import authsystem.user.entity.enums.OAuthProvider;
import authsystem.user.exception.DuplicateEmailException;
import authsystem.user.repository.ProfileRepository;
import authsystem.user.repository.SocialAccountRepository;
import authsystem.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialAccountAdaptor implements SocialAccountPort {

  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  private final SocialAccountRepository socialAccountRepository;
  private final PasswordEncoder passwordEncoder;

  @Override
  public Optional<SecurityUserView> findLinkedUser(String provider, String providerId) {
    return socialAccountRepository
        .findByProviderAndProviderId(OAuthProvider.valueOf(provider), providerId)
        .map(SocialAccount::getUser)
        .map(this::toView);
  }

  @Override
  public SecurityUserView createSocialUser(String provider, String providerId, String email,
      String name) {
    User newUser = User.create(name, email, passwordEncoder.encode(UUID.randomUUID().toString()));

    User savedUser;
    try {
      savedUser = userRepository.saveAndFlush(newUser);
    } catch (DataIntegrityViolationException e) {
      throw DuplicateEmailException.withEmail(email);
    }

    profileRepository.save(Profile.create(savedUser));
    socialAccountRepository.save(
        SocialAccount.link(savedUser, OAuthProvider.valueOf(provider), providerId, email)
    );

    return toView(savedUser);
  }

  private SecurityUserView toView(User user) {
    return new SecurityUserView(user.getId(), user.getEmail(), user.getName(),
        user.getCreatedAt(), user.getPassword(), user.getRole().name(), user.isLocked());
  }
}
