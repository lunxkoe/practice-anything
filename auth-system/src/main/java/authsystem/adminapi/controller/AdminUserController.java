package authsystem.adminapi.controller;

import authsystem.common.dto.CursorPageResponse;
import authsystem.user.dto.request.UserListParams;
import authsystem.user.dto.request.UserLockUpdateRequest;
import authsystem.user.dto.request.UserRoleUpdateRequest;
import authsystem.user.dto.response.UserDto;
import authsystem.user.service.AdminUserService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
public class AdminUserController {

  private final AdminUserService adminUserService;

  @GetMapping
  public ResponseEntity<CursorPageResponse<UserDto>> searchUserList(
      @Valid @ModelAttribute UserListParams condition) {
    return ResponseEntity.ok(adminUserService.searchUserList(condition));
  }

  @PatchMapping("/{userId}/role")
  public ResponseEntity<UserDto> changeRole(@PathVariable UUID userId,
      @Valid @RequestBody UserRoleUpdateRequest request) {
    return ResponseEntity.ok(adminUserService.changeRole(userId, request));
  }

  @PatchMapping("/{userId}/lock")
  public ResponseEntity<UserDto> changeLock(@PathVariable UUID userId,
      @Valid @RequestBody UserLockUpdateRequest request) {
    return ResponseEntity.ok(adminUserService.changeLock(userId, request));
  }
}
