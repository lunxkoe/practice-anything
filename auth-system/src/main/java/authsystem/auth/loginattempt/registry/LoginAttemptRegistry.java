package authsystem.auth.loginattempt.registry;

public interface LoginAttemptRegistry {

  int recordFailure(String email);

  void recordSuccess(String email);
}
