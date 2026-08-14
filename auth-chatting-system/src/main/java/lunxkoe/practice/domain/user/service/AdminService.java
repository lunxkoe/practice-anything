package lunxkoe.practice.domain.user.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lunxkoe.practice.domain.user.dto.request.UserListParams;
import lunxkoe.practice.domain.user.dto.request.UserLockUpdateRequest;
import lunxkoe.practice.domain.user.dto.request.UserRoleUpdateRequest;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.entity.User;
import lunxkoe.practice.domain.user.entity.enums.LockReason;
import lunxkoe.practice.domain.user.exception.UserNotFoundException;
import lunxkoe.practice.domain.user.mapper.UserMapper;
import lunxkoe.practice.domain.user.repository.UserRepository;
import lunxkoe.practice.global.dto.CursorPageResponse;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {

  private final UserRepository userRepository;
  private final UserSessionRegistry userSessionRegistry;
  private final UserMapper userMapper;

  public CursorPageResponse<UserDto> searchUserList(UserListParams condition) {
    return userRepository.searchUserList(condition);
  }

  @Transactional
  public UserDto changeRole(UUID userId, UserRoleUpdateRequest request) {
    User foundUser = getFoundUser(userId);

    foundUser.changeRole(request.role());

    userSessionRegistry.revokeAll(foundUser.getId());

    return userMapper.userDtoFrom(foundUser);
  }

  @Transactional
  public UserDto changeLock(UUID userId, UserLockUpdateRequest request) {
    User foundUser = getFoundUser(userId);

    if (request.locked()) {
      foundUser.lock(LockReason.ADMIN_ACTION);
    } else {
      foundUser.unlock();
    }

    userSessionRegistry.revokeAll(foundUser.getId());

    return userMapper.userDtoFrom(foundUser);
  }

  private User getFoundUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::withNone);
  }
}
