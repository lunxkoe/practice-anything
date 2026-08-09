package authsystem.auth.loginattempt.service;

import authsystem.auth.loginattempt.properties.LoginAttemptProperties;
import authsystem.auth.loginattempt.registry.LoginAttemptRegistry;
import authsystem.security.core.port.SecurityUserPort;
import authsystem.security.core.session.registry.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

  private final LoginAttemptRegistry loginAttemptRegistry;
  private final LoginAttemptProperties loginAttemptProperties;
  private final SecurityUserPort securityUserPort;
  private final UserSessionRegistry userSessionRegistry;

  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public void handleFailedAttempt(String email) {
    int failureCount = loginAttemptRegistry.recordFailure(email);
    if (failureCount < loginAttemptProperties.maxAttempts()) {
      return;
    }
    securityUserPort.findByEmail(email)
        .ifPresent(user -> {
          securityUserPort.lock(user.id(), "FAILED_LOGIN_LIMIT_EXCEEDED");
          userSessionRegistry.revokeAll(user.id());
          loginAttemptRegistry.recordSuccess(email);
        });
  }

  public void handleSuccessAttempt(String email) {
    loginAttemptRegistry.recordSuccess(email);
  }
}
