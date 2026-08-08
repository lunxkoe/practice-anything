package authsystem.security.core.principal;

import authsystem.security.core.port.SecurityUserView;
import java.util.Collection;
import java.util.List;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

  private final SecurityUserView user;
  private String password;

  public CustomUserDetails(SecurityUserView user, String password) {
    this.user = user;
    this.password = password;
  }

  public SecurityUserView getSecurityUser() {
    return user;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(user.role()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return user.email();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !user.locked();
  }

  @Override
  public void eraseCredentials() {
    this.password = null;
  }
}
