package lunxkoe.practice.domain.user.repository;

import java.util.Optional;
import java.util.UUID;
import lunxkoe.practice.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

  @Query("select p from Profile p join fetch p.user where p.id = :userId")
  Optional<Profile> findByIdWithUser(UUID userId);
}
