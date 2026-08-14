package lunxkoe.practice.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lunxkoe.practice.domain.user.entity.enums.Gender;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "profiles")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

  private static final int DEFAULT_TEMPERATURE_SENSITIVITY = 3;

  @Id
  private UUID id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "fk_profiles_user_id"))
  private User user;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  private LocalDate birthDate;

  @Embedded
  private Location location;

  @Column(nullable = false)
  private int temperatureSensitivity = 3;

  private String profileImageUrl;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant updatedAt;

  protected Profile(User user, int temperatureSensitivity) {
    this.user = user;
    this.temperatureSensitivity = temperatureSensitivity;
  }

  public static Profile create(User user) {
    return new Profile(user, DEFAULT_TEMPERATURE_SENSITIVITY);
  }

  public void changeProfile(Gender newGender, LocalDate newBirthDate, Location newLocation, int newTemperatureSensitivity) {
    this.gender = newGender;
    this.birthDate = newBirthDate;
    this.location = newLocation;
    this.temperatureSensitivity = newTemperatureSensitivity;
  }

  public void changeProfileImageUrl(String newProfileImageUrl) {
    this.profileImageUrl = newProfileImageUrl;
  }
}
