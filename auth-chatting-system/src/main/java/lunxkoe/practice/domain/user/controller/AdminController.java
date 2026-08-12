package lunxkoe.practice.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.domain.user.dto.request.UserListParams;
import lunxkoe.practice.domain.user.dto.response.UserDto;
import lunxkoe.practice.domain.user.service.AdminService;
import lunxkoe.practice.global.dto.CursorPageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AdminController {

  private final AdminService adminService;

  @GetMapping("")
  public ResponseEntity<CursorPageResponse<UserDto>> searchUserList(@Valid @ModelAttribute UserListParams condition) {
    CursorPageResponse<UserDto> userDtoCursorPageResponse = adminService.searchUserList(condition);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(userDtoCursorPageResponse);
  }
}
