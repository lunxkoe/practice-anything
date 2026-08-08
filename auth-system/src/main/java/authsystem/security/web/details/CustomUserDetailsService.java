package authsystem.security.web.details;

import authsystem.security.core.port.SecurityUserPort;
import authsystem.security.core.port.SecurityUserView;
import authsystem.security.core.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final SecurityUserPort securityUserPort;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    SecurityUserView user = securityUserPort.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 유저를 찾을 수 없습니다: " + email));
    return new CustomUserDetails(user, user.encodedPassword());
  }
}
