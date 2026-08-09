package authsystem.auth.authentication;

import authsystem.security.core.port.SecurityUserPort;
import authsystem.security.core.port.SecurityUserView;
import authsystem.security.core.principal.CustomUserDetails;
import authsystem.temppassword.registry.TempPasswordRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@RequiredArgsConstructor
public class TempPasswordAuthenticationProvider implements AuthenticationProvider {

  private final SecurityUserPort securityUserPort;
  private final TempPasswordRegistry tempPasswordRegistry;


  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String email = authentication.getName();
    String rawPassword = (String) authentication.getCredentials();

    SecurityUserView user = securityUserPort.findByEmail(email)
        .orElseThrow(() -> new BadCredentialsException("자격 증명이 올바르지 않습니다."));

    if (!tempPasswordRegistry.matches(user.id(), rawPassword)) {
      throw new BadCredentialsException("자격 증명이 올바르지 않습니다.");
    }

    if (user.locked()) {
      throw new LockedException("계정이 잠겨 있습니다.");
    }

    CustomUserDetails principal = new CustomUserDetails(user, null);
    return UsernamePasswordAuthenticationToken.authenticated(principal, null,
        principal.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
