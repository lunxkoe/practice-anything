package authsystem.user.service;

import authsystem.security.core.session.registry.UserSessionRegistry;
import authsystem.user.dto.request.ChangePasswordRequest;
import authsystem.user.dto.request.UserCreateRequest;
import authsystem.user.dto.response.UserDto;
import authsystem.user.entity.Profile;
import authsystem.user.entity.User;
import authsystem.user.exception.DuplicateEmailException;
import authsystem.user.exception.InvalidCurrentPasswordException;
import authsystem.user.exception.SelfOnlyAccessException;
import authsystem.user.exception.UserNotFoundException;
import authsystem.user.mapper.UserMapper;
import authsystem.user.repository.ProfileRepository;
import authsystem.user.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final UserSessionRegistry userSessionRegistry;

  @Transactional
  public UserDto signUp(UserCreateRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw DuplicateEmailException.withEmail(request.email());
    }

    User newUser = User.create(request.name(), request.email(),
        passwordEncoder.encode(request.password()));

    User savedUser = null;
    try {
      savedUser = userRepository.saveAndFlush(newUser);
    } catch (DataIntegrityViolationException e) {
      throw DuplicateEmailException.withEmail(request.email());
    }

    profileRepository.save(Profile.create(savedUser));

    return userMapper.userDtoFrom(savedUser);
  }

  @Transactional
  public void changePassword(UUID userId, ChangePasswordRequest request, UUID requestUserId) {
    checkSelf(userId, requestUserId);

    User foundUser = getFoundUser(userId);

//    if (!passwordEncoder.matches(request.currentPassword(), foundUser.getPassword())) {
//      throw InvalidCurrentPasswordException.withNone();
//    }

    foundUser.changePassword(passwordEncoder.encode(request.password()));

    userSessionRegistry.revokeAll(userId);
  }

  private void checkSelf(UUID userId, UUID requestUserId) {
    if (!userId.equals(requestUserId)) {
      throw SelfOnlyAccessException.withNone();
    }
  }

  private User getFoundUser(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::withNone);
  }
}
