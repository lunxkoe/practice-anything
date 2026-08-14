package lunxkoe.practice.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lunxkoe.practice.domain.user.entity.enums.LockReason;
import lunxkoe.practice.domain.user.entity.enums.Role;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(
    name = "users",
    uniqueConstraints = @UniqueConstraint(
        name = User.UK_EMAIL,
        columnNames = {"email"}
    )
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

  public static final String UK_EMAIL = "uk_users_email";

  @Id @GeneratedValue(strategy = GenerationType.UUID)
  @Column(nullable = false, updatable = false)
  private UUID id;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(nullable = false)
  private boolean locked;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private LockReason lockReason;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant updatedAt;

  private User(String email, String password, String name, Role role, boolean locked, LockReason lockReason) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.role = role;
    this.locked = locked;
    this.lockReason = lockReason;
  }

  public static User create(String email, String encodedPassword, String name) {
    return new User(email, encodedPassword, name, Role.USER, false, LockReason.NONE);
  }

  public void changeRole(Role role) {
    this.role = role;
  }

  public void lock(LockReason lockReason) {
    this.locked = true;
    this.lockReason = lockReason;
  }

  public void unlock() {
    this.locked = false;
    this.lockReason = LockReason.NONE;
  }

  public void changePassword(String encodePassword) {
    this.password = encodePassword;
  }

  public void changeName(String name) {
    this.name = name;
  }
}
