package lunxkoe.practice.security.details;

import java.util.Collection;
import java.util.List;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomUserDetails implements UserDetails, CredentialsContainer {

  private UserDto userDto;
  private String password;

  public CustomUserDetails(UserDto userDto, String password) {
    this.userDto = userDto;
    this.password = password;
  }

  public UserDto getUserDto() {
    return userDto;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(userDto.role().name()));
  }

  @Override
  public String getPassword() {
    return password;
  }

  @Override
  public String getUsername() {
    return userDto.email();
  }

  @Override
  public boolean isAccountNonLocked() {
    return !userDto.locked();
  }

  @Override
  public void eraseCredentials() {
    this.password = null;
  }
}
