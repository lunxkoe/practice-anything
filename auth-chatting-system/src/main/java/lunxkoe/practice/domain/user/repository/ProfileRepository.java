package lunxkoe.practice.domain.user.repository;

import java.util.UUID;
import lunxkoe.practice.domain.user.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

}
