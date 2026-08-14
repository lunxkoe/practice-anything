package lunxkoe.practice.domain.user.service;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lunxkoe.practice.domain.user.dto.request.ChangePasswordRequest;
import lunxkoe.practice.domain.user.dto.request.ProfileUpdateRequest;
import lunxkoe.practice.domain.user.dto.request.UserCreateRequest;
import lunxkoe.practice.domain.user.dto.request.UserListParams;
import lunxkoe.practice.domain.user.dto.response.ProfileDto;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.entity.Profile;
import lunxkoe.practice.domain.user.entity.User;
import lunxkoe.practice.domain.user.exception.AccessDeniedException;
import lunxkoe.practice.domain.user.exception.DuplicateEmailException;
import lunxkoe.practice.domain.user.exception.UserNotFoundException;
import lunxkoe.practice.domain.user.mapper.UserMapper;
import lunxkoe.practice.domain.user.repository.ProfileRepository;
import lunxkoe.practice.domain.user.repository.UserRepository;
import lunxkoe.practice.global.dto.CursorPageResponse;
import lunxkoe.practice.global.file.storage.FileStorageService;
import lunxkoe.practice.global.temppassword.registry.TempPasswordRegistry;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final ProfileRepository profileRepository;
  private final UserMapper userMapper;
  private final UserSessionRegistry userSessionRegistry;
  private final TempPasswordRegistry tempPasswordRegistry;
  private final FileStorageService fileStorageService;

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

  public ProfileDto getProfile(UUID userId) {
    Profile foundProfile = profileRepository.findByIdWithUser(userId)
        .orElseThrow(UserNotFoundException::withNone);
    return userMapper.profileDtoFrom(foundProfile);
  }

  @Transactional
  public ProfileDto changeProfile(UUID userId, ProfileUpdateRequest request, MultipartFile image, UUID requestUserId) {
    checkSelf(userId, requestUserId);

    Profile foundProfile = profileRepository.findByIdWithUser(userId)
        .orElseThrow(UserNotFoundException::withNone);

    // 프로필 정보 수정
    foundProfile.getUser().changeName(request.name());

    foundProfile.changeProfile(
        request.gender(),
        request.birthDate(),
        userMapper.locationFrom(request.location()),
        request.temperatureSensitivity()
    );

    String oldProfileImageUrl = foundProfile.getProfileImageUrl();
    String newProfileImageUrl = oldProfileImageUrl;
    if (image != null && !image.isEmpty()) {
      newProfileImageUrl = fileStorageService.store(image, "profile");
//      fileStorageService.delete(oldProfileImageUrl); // TODO 임시 비활성화: 추후 배치로 고아 삭제하는 기능 구현 예정
    }

    foundProfile.changeProfileImageUrl(newProfileImageUrl);

    return userMapper.profileDtoFrom(foundProfile);
  }

  @Transactional
  public void changePassword(UUID userId, ChangePasswordRequest request, UUID requestUserId) {
    checkSelf(userId, requestUserId);

    User foundUser = userRepository.findById(userId)
        .orElseThrow(UserNotFoundException::withNone);

    foundUser.changePassword(
        passwordEncoder.encode(request.password())
    );

    userSessionRegistry.revokeAll(userId);
    tempPasswordRegistry.revoke(userId);
  }

  private void checkSelf(UUID userId, UUID requestUserId) {
    if (!userId.equals(requestUserId)) {
      throw AccessDeniedException.withNone();
    }
  }
}
