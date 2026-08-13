package lunxkoe.practice.security.details;

import java.util.Collection;
import java.util.List;
import lunxkoe.practice.domain.user.entity.User;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

  private final User user;
  private String password;

  public CustomUserDetails(User user, String password) {
    this.user = user;
    this.password = password;
  }

  public User getUser() {
    return user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(user.getRole().name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return user.getEmail();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !user.isLocked();
  }

  @Override
  public void eraseCredentials() {
    this.password = null;
  }
}
