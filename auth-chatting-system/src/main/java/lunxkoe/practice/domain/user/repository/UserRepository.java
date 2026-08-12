package lunxkoe.practice.domain.user.repository;

import java.util.UUID;
import lunxkoe.practice.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID> {

  boolean existsByEmail(String email);
}
