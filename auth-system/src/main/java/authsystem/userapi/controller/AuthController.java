package authsystem.userapi.controller;

import authsystem.auth.dto.request.ResetPasswordRequest;
import authsystem.auth.dto.request.SignInRequest;
import authsystem.auth.dto.response.JwtDto;
import authsystem.auth.dto.response.RefreshDto;
import authsystem.auth.dto.response.SignInDto;
import authsystem.auth.service.AuthService;
import authsystem.security.web.cookie.RefreshTokenCookieProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;
  private final RefreshTokenCookieProvider refreshTokenCookieProvider;

  @PostMapping("/sign-in")
  public ResponseEntity<JwtDto> signIn(@Valid @ModelAttribute SignInRequest request,
      HttpServletResponse response) {
    SignInDto result = authService.signIn(request);
    refreshTokenCookieProvider.attach(response, result.refreshToken());
    return ResponseEntity.ok(result.jwtDto());
  }

  @PostMapping("/sign-out")
  public ResponseEntity<Void> signOut(
      @CookieValue(name = RefreshTokenCookieProvider.REFRESH_TOKEN, defaultValue = "") String refreshToken,
      HttpServletResponse response) {
    authService.signOut(refreshToken);
    refreshTokenCookieProvider.clear(response);
    return ResponseEntity.ok().build();
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtDto> refresh(
      @CookieValue(name = RefreshTokenCookieProvider.REFRESH_TOKEN, defaultValue = "") String refreshToken,
      HttpServletResponse response) {
    RefreshDto result = authService.refresh(refreshToken);
    refreshTokenCookieProvider.attach(response, result.refreshToken());
    return ResponseEntity.ok(result.jwtDto());
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    authService.resetPassword(request);
    return ResponseEntity.ok().build();
  }

  @GetMapping("/csrf-token")
  public ResponseEntity<Void> csrfToken(CsrfToken csrfToken) {
    csrfToken.getToken();
    return ResponseEntity.noContent().build();
  }
}
