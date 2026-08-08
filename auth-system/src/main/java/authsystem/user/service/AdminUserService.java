package authsystem.user.service;

import authsystem.common.dto.CursorPageResponse;
import authsystem.security.core.session.registry.UserSessionRegistry;
import authsystem.user.dto.request.UserListParams;
import authsystem.user.dto.request.UserLockUpdateRequest;
import authsystem.user.dto.request.UserRoleUpdateRequest;
import authsystem.user.dto.response.UserDto;
import authsystem.user.entity.User;
import authsystem.user.entity.enums.LockReason;
import authsystem.user.exception.UserNotFoundException;
import authsystem.user.mapper.UserMapper;
import authsystem.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminUserService {

  private final UserRepository userRepository;
  private final UserSessionRegistry userSessionRegistry;
  private final UserMapper userMapper;

  public CursorPageResponse<UserDto> searchUserList(UserListParams condition) {
    return userRepository.search(condition);
  }

  @Transactional
  public UserDto changeRole(UUID userId, UserRoleUpdateRequest request) {
    User foundUser = getFoundUser(userId);

    foundUser.changeRole(request.role());

    userSessionRegistry.revokeAll(userId);

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

    userSessionRegistry.revokeAll(userId);

    return userMapper.userDtoFrom(foundUser);
  }

  private User getFoundUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::withNone);
  }
}
