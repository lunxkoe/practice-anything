package lunxkoe.practice.global.temppassword.registry.impl;

import java.util.UUID;
import lunxkoe.practice.global.temppassword.generator.TempPasswordGenerator;
import lunxkoe.practice.global.temppassword.properties.TempPasswordProperties;
import lunxkoe.practice.global.temppassword.registry.TempPasswordRegistry;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TempPasswordRedisRegistry implements TempPasswordRegistry {

  private static final String KEY_PREFIX = "auth:temp-password:";

  private final StringRedisTemplate redisTemplate;
  private final TempPasswordProperties properties;
  private final TempPasswordGenerator generator;
  private final PasswordEncoder passwordEncoder;

  public TempPasswordRedisRegistry(StringRedisTemplate redisTemplate, TempPasswordProperties properties, TempPasswordGenerator generator, PasswordEncoder passwordEncoder) {
    this.redisTemplate = redisTemplate;
    this.properties = properties;
    this.generator = generator;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public TempPasswordGenerator generator() {
    return generator;
  }

  @Override
  public void save(UUID userId, String rawTempPassword) {
    redisTemplate.opsForValue().set(key(userId), passwordEncoder.encode(rawTempPassword), properties.expiration());
  }

  @Override
  public void revoke(UUID userId) {
    redisTemplate.delete(key(userId));
  }

  @Override
  public boolean matches(UUID userId, String rawPassword) {
    String saved = redisTemplate.opsForValue().get(key(userId));
    return saved != null && passwordEncoder.matches(rawPassword, saved);
  }

  @Override
  public int getExpirationMinutes() {
    return (int) properties.expiration().toMinutes();
  }

  private String key(UUID userId) {
    return KEY_PREFIX + userId;
  }
}
