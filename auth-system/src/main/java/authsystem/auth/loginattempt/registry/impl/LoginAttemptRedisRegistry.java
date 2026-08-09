package authsystem.auth.loginattempt.registry.impl;

import authsystem.auth.loginattempt.properties.LoginAttemptProperties;
import authsystem.auth.loginattempt.registry.LoginAttemptRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;


public class LoginAttemptRedisRegistry implements LoginAttemptRegistry {

  private static final String KEY_PREFIX = "auth:login-attempt:";

  private final StringRedisTemplate redisTemplate;
  private final LoginAttemptProperties properties;

  public LoginAttemptRedisRegistry(StringRedisTemplate redisTemplate,
      LoginAttemptProperties properties) {
    this.redisTemplate = redisTemplate;
    this.properties = properties;
  }

  @Override
  public int recordFailure(String email) {
    String key = key(email);
    Long count = redisTemplate.opsForValue().increment(key);
    if (count != null && count == 1L) {
      redisTemplate.expire(key, properties.window());
    }
    return count == null ? 0 : count.intValue();
  }

  @Override
  public void recordSuccess(String email) {
    redisTemplate.delete(key(email));
  }

  private String key(String email) {
    return KEY_PREFIX + email;
  }
}
