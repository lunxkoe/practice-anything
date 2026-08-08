package authsystem.user.repository.querydsl;

import authsystem.commom.dto.CursorPageResponse;
import authsystem.user.dto.request.UserListParams;
import authsystem.user.dto.response.UserDto;

public interface UserCustomRepository {

  CursorPageResponse<UserDto> search(UserListParams condition);
}