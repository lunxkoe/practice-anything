-- KEYS[1] = auth:user-session-index:{userId}
-- KEYS[2] = auth:user-session:{userId}:{new sessionId}
-- ARGV[1] = auth:user-session:{userId}: (세션 키 접두사)
-- ARGV[2] = maxDevices
-- ARGV[3] = new sessionId
-- ARGV[4] = new refreshJti
-- ARGV[5] = new issuedAt epoch millis
-- ARGV[6] = new expireAt epoch millis

local indexKey = KEYS[1]
local newSessionKey = KEYS[2]
local sessionPrefix = ARGV[1]
local maxDevices = tonumber(ARGV[2])
local newSessionId = ARGV[3]
local newRefreshJti = ARGV[4]
local newIssuedAt = ARGV[5]
local newExpiresAtMillis = ARGV[6]

local existingSessionIds = redis.call('ZRANGE', indexKey, 0, -1)

local sessions = {}
for _, sid in ipairs(existingSessionIds) do
  local issuedAt = redis.call('HGET', sessionPrefix .. sid, 'issuedAt')
  if issuedAt then
    table.insert(sessions, {id = sid, issuedAt = tonumber(issuedAt)})
  end
end

table.sort(sessions, function(a, b) return a.issuedAt < b.issuedAt end)

local excess = #sessions - maxDevices + 1
if excess > 0 then
  for i = 1, excess do
    local victim = sessions[i]
    redis.call('DEL', sessionPrefix .. victim.id)
    redis.call('ZREM', indexKey, victim.id)
  end
end

redis.call('HSET', newSessionKey, 'refreshJti', newRefreshJti, 'issuedAt', newIssuedAt)
redis.call('PEXPIREAT', newSessionKey, newExpiresAtMillis)
redis.call('ZADD', indexKey, newExpiresAtMillis, newSessionId)

local highest = redis.call('ZREVRANGE', indexKey, 0, 0, 'WITHSCORES')
local maxExpiresAtMillis = newExpiresAtMillis
if highest[2] and tonumber(highest[2]) > tonumber(maxExpiresAtMillis) then
  maxExpiresAtMillis = highest[2]
end
redis.call('PEXPIREAT', indexKey, maxExpiresAtMillis)

return 1
