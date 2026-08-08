package authsystem.auth.mapper;

import authsystem.auth.dto.response.AuthUserDto;
import authsystem.auth.dto.response.JwtDto;
import authsystem.auth.dto.response.RefreshDto;
import authsystem.auth.dto.response.SignInDto;
import authsystem.security.core.port.SecurityUserView;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

  public AuthUserDto authUserDtoFrom(SecurityUserView user) {
    return new AuthUserDto(
        user.id(),
        user.createdAt(),
        user.email(),
        user.name(),
        user.role(),
        user.locked()
    );
  }

  public SignInDto signInDtoFrom(SecurityUserView user, String accessToken, String refreshToken) {
    return new SignInDto(new JwtDto(authUserDtoFrom(user), accessToken), refreshToken);
  }

  public RefreshDto refreshDtoFrom(SecurityUserView user, String accessToken, String refreshToken) {
    return new RefreshDto(new JwtDto(authUserDtoFrom(user), accessToken), refreshToken);
  }
}
