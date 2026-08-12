package lunxkoe.practice.domain.user.repository.querydsl;

import lunxkoe.practice.domain.user.dto.request.UserListParams;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.global.dto.CursorPageResponse;

public interface UserCustomRepository {

  CursorPageResponse<UserDto> searchUserList(UserListParams condition);
}
