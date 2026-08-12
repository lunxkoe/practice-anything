package lunxkoe.practice.domain.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lunxkoe.practice.domain.user.dto.request.UserCreateRequest;
import lunxkoe.practice.domain.user.dto.request.UserListParams;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.entity.Profile;
import lunxkoe.practice.domain.user.entity.User;
import lunxkoe.practice.domain.user.exception.DuplicateEmailException;
import lunxkoe.practice.domain.user.mapper.UserMapper;
import lunxkoe.practice.domain.user.repository.ProfileRepository;
import lunxkoe.practice.domain.user.repository.UserRepository;
import lunxkoe.practice.global.dto.CursorPageResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  private final UserMapper userMapper;

  @Transactional
  public UserDto signUp(UserCreateRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw DuplicateEmailException.withNone();
    }

    User newUser = User.create(
        request.email(),
        passwordEncoder.encode(request.password()),
        request.name()
    );

    try {
      User savedUser = userRepository.saveAndFlush(newUser);

      Profile newProfile = Profile.create(savedUser);
      profileRepository.save(newProfile);

      log.info("[UserService] 회원가입 성공 userId = {}", savedUser.getId());
      return userMapper.userDtoFrom(savedUser);

    } catch (DataIntegrityViolationException e) {
      if (isEmailUniqueConstraintViolation(e)) {
        throw DuplicateEmailException.withNone();
      }
      throw e;
    }
  }

  private boolean isEmailUniqueConstraintViolation(DataIntegrityViolationException e) {
    Throwable cause = e.getCause();
    while (cause != null) {
      if (cause instanceof ConstraintViolationException cve) {
        String constraintName = cve.getConstraintName();
        return constraintName != null && constraintName.endsWith(User.UK_EMAIL);
      }
      cause = cause.getCause();
    }
    return false;
  }
}
