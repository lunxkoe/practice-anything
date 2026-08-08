package authsystem.user.repository;

import authsystem.user.entity.User;
import authsystem.user.repository.querydsl.UserCustomRepository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID>, UserCustomRepository {

  boolean existsByEmail(String email);

  Optional<User> findByEmail(String email);
}
