package lunxkoe.practice.security.usersession.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import lunxkoe.practice.security.usersession.properties.enums.ConcurrentPolicyType;
import lunxkoe.practice.security.usersession.properties.enums.ExpirationPolicyType;
import lunxkoe.practice.security.usersession.properties.enums.UserSessionRegistryType;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.security.user-session")
public record UserSessionProperties(

    @NotNull(message = "ttl은 필수 값입니다.")
    @DurationMin(days = 1, message = "ttl은 최소 1일 이상이어야 합니다.")
    @DurationMax(days = 30, message = "ttl은 30일을 초과할 수 없습니다.")
    Duration ttl,

    @Min(value = 1, message = "maxDevices는 1 이상이어야 합니다.")
    @Max(value = 20, message = "maxDevices는 20을 넘을 수 없습니다.")
    int maxDevices,

    ExpirationPolicyType expirationPolicy,

    ConcurrentPolicyType concurrentPolicy,

    UserSessionRegistryType impl
) {

  public UserSessionProperties {
    if (expirationPolicy == null) {
      expirationPolicy = ExpirationPolicyType.ABSOLUTE;
    }
    if (concurrentPolicy == null) {
      concurrentPolicy = ConcurrentPolicyType.MULTI;
    }
    if (impl == null) {
      impl = UserSessionRegistryType.REDIS;
    }
  }
}
