package lunxkoe.practice.security.usersession.policy;

import java.time.Instant;
import lunxkoe.practice.security.usersession.dto.UserSession;
import lunxkoe.practice.security.usersession.registry.UserSessionRegistry;

public interface ConcurrentPolicy {

  /** registry.issue()/rotate()를 절대 호출하면 안됨 */
  UserSession apply(UserSession newSession, Instant expiresAt, UserSessionRegistry registry);
}
