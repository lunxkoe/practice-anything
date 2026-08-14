package lunxkoe.practice.global.temppassword.authentication;

import lunxkoe.practice.domain.user.entity.User;
import lunxkoe.practice.domain.user.mapper.UserMapper;
import lunxkoe.practice.domain.user.repository.UserRepository;
import lunxkoe.practice.global.temppassword.registry.TempPasswordRegistry;
import lunxkoe.practice.security.details.CustomUserDetails;
import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

public class TempPasswordAuthenticationProvider implements AuthenticationProvider {

  private final UserRepository userRepository;
  private final UserMapper userMapper;
  private final TempPasswordRegistry tempPasswordRegistry;

  public TempPasswordAuthenticationProvider(UserRepository userRepository, UserMapper userMapper, TempPasswordRegistry tempPasswordRegistry) {
    this.userRepository = userRepository;
    this.userMapper = userMapper;
    this.tempPasswordRegistry = tempPasswordRegistry;
  }

  @Override
  public @Nullable Authentication authenticate(Authentication authentication)
      throws AuthenticationException {

    String email = authentication.getName();
    String rawPassword = (String) authentication.getCredentials();

    User foundUser = userRepository.findByEmail(email)
        .orElseThrow(() -> new BadCredentialsException("자격 증명이 올바르지 않습니다."));

    if (foundUser.isLocked()) {
      throw new LockedException("계정이 잠겨 있습니다.");
    }

    if (!tempPasswordRegistry.matches(foundUser.getId(), rawPassword)) {
      throw new BadCredentialsException("자격 증명이 올바르지 않습니다.");
    }

    CustomUserDetails principal = new CustomUserDetails(userMapper.userDtoFrom(foundUser), null);
    return UsernamePasswordAuthenticationToken.authenticated(principal, null, principal.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
