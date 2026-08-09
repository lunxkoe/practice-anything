package authsystem.userapi.controller;

import authsystem.security.core.principal.CurrentUser;
import authsystem.security.core.principal.UserPrincipal;
import authsystem.user.dto.request.ChangePasswordRequest;
import authsystem.user.dto.request.UserCreateRequest;
import authsystem.user.dto.response.UserDto;
import authsystem.user.service.UserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<UserDto> signUp(@Valid @RequestBody UserCreateRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.signUp(request));
  }

  @PatchMapping("/{userId}/password")
  public ResponseEntity<Void> changePassword(
      @PathVariable UUID userId,
      @Valid @RequestBody ChangePasswordRequest request,
      @CurrentUser UserPrincipal principal
  ) {
    userService.changePassword(userId, request, principal.userId());
    return ResponseEntity.noContent().build();
  }
}
