package authsystem.user.mapper;

import authsystem.user.dto.response.UserDto;
import authsystem.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

  public UserDto userDtoFrom(User user) {
    return new UserDto(
        user.getId(),
        user.getCreatedAt(),
        user.getEmail(),
        user.getName(),
        user.getRole(),
        user.isLocked()
    );
  }
}
