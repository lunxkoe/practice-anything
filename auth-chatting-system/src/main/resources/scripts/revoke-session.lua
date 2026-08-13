-- KEYS[1] = auth:user-session:{userId}:{sessionId}
-- KEYS[2] = auth:user-session-index:{userId}
-- ARGV[1] = sessionId

redis.call('DEL', KEYS[1])
redis.call('ZREM', KEYS[2], ARGV[1])
return 1
