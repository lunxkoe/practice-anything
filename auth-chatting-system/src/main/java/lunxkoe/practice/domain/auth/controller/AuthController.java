package lunxkoe.practice.domain.auth.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lunxkoe.practice.domain.auth.dto.request.ResetPasswordRequest;
import lunxkoe.practice.domain.auth.dto.request.SignInRequest;
import lunxkoe.practice.domain.auth.dto.response.JwtDto;
import lunxkoe.practice.domain.auth.dto.response.RefreshDto;
import lunxkoe.practice.domain.auth.dto.response.SignInDto;
import lunxkoe.practice.domain.auth.service.AuthService;
import lunxkoe.practice.security.cookie.provider.RefreshTokenCookieProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final RefreshTokenCookieProvider refreshTokenCookieProvider;

  @PostMapping("/sign-out")
  public ResponseEntity<Void> signOut(
      @CookieValue(name = RefreshTokenCookieProvider.REFRESH_TOKEN, required = false) String refreshToken,
      HttpServletResponse response
  ) {
    authService.signOut(refreshToken);
    refreshTokenCookieProvider.clear(response);
    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

  @PostMapping("/sign-in")
  public ResponseEntity<JwtDto> signIn(
      @Valid @RequestBody SignInRequest request,
      HttpServletResponse response
  ) {
    SignInDto signInDto = authService.signIn(request);
    refreshTokenCookieProvider.attach(response, signInDto.refreshToken());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(signInDto.jwtDto());
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(
      @CookieValue(name = RefreshTokenCookieProvider.REFRESH_TOKEN) String refreshToken,
      HttpServletResponse response
  ) {
    RefreshDto refreshDto = authService.refresh(refreshToken);
    refreshTokenCookieProvider.attach(response, refreshDto.refreshToken());
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(refreshDto.jwtDto());
  }
}
