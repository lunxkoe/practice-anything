package lunxkoe.practice.global.temppassword.registry;

import java.util.UUID;
import lunxkoe.practice.global.temppassword.generator.TempPasswordGenerator;

public interface TempPasswordRegistry {

  TempPasswordGenerator generator();

  default String issue(UUID userId) {
    String rawTempPassword = generator().generate();
    save(userId, rawTempPassword);
    return rawTempPassword;
  }

  void save(UUID userId, String rawTempPassword);

  void revoke(UUID userId);

  boolean matches(UUID userId, String rawPassword);

  int getExpirationMinutes();
}
