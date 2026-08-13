package lunxkoe.practice.domain.auth.mapper;

import lombok.RequiredArgsConstructor;
import lunxkoe.practice.domain.auth.dto.response.JwtDto;
import lunxkoe.practice.domain.auth.dto.response.SignInDto;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.entity.User;
import lunxkoe.practice.domain.user.mapper.UserMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthMapper {

  private final UserMapper userMapper;

  public SignInDto signInDtoFrom(UserDto userDto, String accessToken, String refreshToken) {
    return new SignInDto(
        jwtDtoFrom(userDto, accessToken),
        refreshToken
    );
  }

  public JwtDto jwtDtoFrom(UserDto userDto, String accessToken) {
    return new JwtDto(
        userDto,
        accessToken
    );
  }
}
