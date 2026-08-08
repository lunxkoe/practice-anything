package authsystem.auth.event.request;

public record TempPasswordRequestedEvent(
    String email,
    String rawTempPassword,
    int expireMinutes
) {

  @Override
  public String toString() {
    return email;   // 로그에 실수로 원문 비밀번호가 찍히지 않도록 toString을 오버라이드
  }
}
