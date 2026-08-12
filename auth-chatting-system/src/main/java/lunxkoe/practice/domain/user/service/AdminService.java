package lunxkoe.practice.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lunxkoe.practice.domain.user.dto.request.UserListParams;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.repository.UserRepository;
import lunxkoe.practice.global.dto.CursorPageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;

  public CursorPageResponse<UserDto> searchUserList(UserListParams condition) {
    return userRepository.searchUserList(condition);
  }
}
