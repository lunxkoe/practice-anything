package lunxkoe.practice.domain.user.repository;

import java.util.UUID;
import lunxkoe.practice.domain.user.entity.User;
import lunxkoe.practice.domain.user.repository.querydsl.UserCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, UUID>, UserCustomRepository {

  boolean existsByEmail(String email);
}
