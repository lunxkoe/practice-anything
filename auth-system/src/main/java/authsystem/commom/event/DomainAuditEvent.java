package authsystem.commom.event;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * 보안/운영 감사 로그의 공용 이벤트 계약. auth-domain, user-domain 등 여러 모듈이 이 타입으로만 이벤트를 발행하고, audit-core는 이 타입만
 * 구독한다 — 발행자와 구독자가 서로의 존재를 몰라도 되게 하는 것이 목적.
 */
public record DomainAuditEvent(
    String type,
    UUID actorId,
    UUID targetId,
    String ip,
    Map<String, Object> metadata,
    Instant occurredAt
) {

  public static DomainAuditEvent of(String type, UUID actorId, UUID targetId, String ip,
      Map<String, Object> metadata, Instant occurredAt) {
    return new DomainAuditEvent(type, actorId, targetId, ip, metadata, occurredAt);
  }
}
