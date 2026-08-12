package lunxkoe.practice.domain.user.mapper;

import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.entity.User;
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
