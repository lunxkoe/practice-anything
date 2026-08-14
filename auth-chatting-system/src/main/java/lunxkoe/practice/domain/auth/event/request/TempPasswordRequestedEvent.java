package lunxkoe.practice.domain.auth.event.request;

public record TempPasswordRequestedEvent(
    String email,
    String rawTempPassword,
    int expireMinutes
) {

  @Override
  public String toString() {
    return email;
  }
}
