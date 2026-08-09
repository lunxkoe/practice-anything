package authsystem.user.port;

import authsystem.security.core.port.SecurityUserPort;
import authsystem.security.core.port.SecurityUserView;
import authsystem.user.entity.User;
import authsystem.user.entity.enums.LockReason;
import authsystem.user.repository.UserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepositorySecurityUserPort implements SecurityUserPort {

  private final UserRepository userRepository;

  @Override
  public Optional<SecurityUserView> findByEmail(String email) {
    return userRepository.findByEmail(email).map(this::toView);
  }

  @Override
  public Optional<SecurityUserView> findById(UUID userId) {
    return userRepository.findById(userId).map(this::toView);
  }

  @Override
  public void lock(UUID userId, String reason) {
    userRepository.findById(userId)
        .ifPresent(user -> user.lock(LockReason.valueOf(reason)));
  }

  private SecurityUserView toView(User user) {
    return new SecurityUserView(
        user.getId(),
        user.getEmail(),
        user.getName(),
        user.getCreatedAt(),
        user.getPassword(),
        user.getRole().name(),
        user.isLocked()
    );
  }
}
