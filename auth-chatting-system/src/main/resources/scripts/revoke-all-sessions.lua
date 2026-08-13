-- KEYS[1] = auth:user-session-index:{userId}
-- ARGV[1] = auth:user-session:{userId}: (세션 키 접두사)
--
-- Lua 스크립트는 Redis에서 통째로 원자적으로 실행되므로, 이 스크립트가 도는 동안
-- 다른 요청이 끼어들어 인덱스에 새 세션을 추가할 수 없다. 그래서 인덱스에서 읽은 멤버만
-- 골라 지우는 대신 인덱스 키 자체를 통째로 삭제해도 좀비 세션이 생기지 않는다
-- (CodeIt의 Java 버전 revokeAll이 부분 삭제를 해야 했던 이유는 원자적이지 않았기 때문).

local sessionIds = redis.call('ZRANGE', KEYS[1], 0, -1)
for _, sessionId in ipairs(sessionIds) do
  redis.call('DEL', ARGV[1] .. sessionId)
end
redis.call('DEL', KEYS[1])
return #sessionIds
