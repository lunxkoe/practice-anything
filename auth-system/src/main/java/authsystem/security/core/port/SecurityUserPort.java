package authsystem.security.core.port;

import java.util.Optional;
import java.util.UUID;

public interface SecurityUserPort {

  Optional<SecurityUserView> findByEmail(String email);

  Optional<SecurityUserView> findById(UUID userId);
}
