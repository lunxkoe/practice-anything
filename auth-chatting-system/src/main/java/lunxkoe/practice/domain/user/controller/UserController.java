package lunxkoe.practice.domain.user.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.domain.user.dto.request.ChangePasswordRequest;
import lunxkoe.practice.domain.user.dto.request.ProfileUpdateRequest;
import lunxkoe.practice.domain.user.dto.request.UserCreateRequest;
import lunxkoe.practice.domain.user.dto.request.UserListParams;
import lunxkoe.practice.domain.user.dto.response.ProfileDto;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.service.UserService;
import lunxkoe.practice.global.dto.CursorPageResponse;
import lunxkoe.practice.security.details.CurrentUser;
import lunxkoe.practice.security.details.CustomUserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("")
  public ResponseEntity<UserDto> signUp(@Valid @RequestBody UserCreateRequest request) {
    UserDto userDto = userService.signUp(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(userDto);
  }

  @GetMapping("/{userId}/profiles")
  public ResponseEntity<ProfileDto> getProfile(@PathVariable UUID userId) {
    ProfileDto profileDto = userService.getProfile(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(profileDto);
  }

  @PatchMapping("/{userId}/profiles")
  public ResponseEntity<ProfileDto> changeProfile(
      @PathVariable UUID userId,
      @Valid @RequestPart ProfileUpdateRequest request,
      @RequestPart(required = false) MultipartFile image,
      @CurrentUser CustomUserPrincipal principal
  ) {
    ProfileDto profileDto = userService.changeProfile(userId, request, image, principal.userId());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(profileDto);
  }

  @PatchMapping("/{userId}/password")
  public ResponseEntity<UserDto> changePassword(
      @PathVariable UUID userId,
      @Valid @RequestBody ChangePasswordRequest request,
      @CurrentUser CustomUserPrincipal principal
  ) {
    userService.changePassword(userId, request, principal.userId());
    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }
}
