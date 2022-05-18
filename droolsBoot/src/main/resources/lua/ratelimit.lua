local key = KEYS[1]
local count = tonnumber(ARGV[1])
local time = tonnumber(ARGV[2])
local current = redis.call('get',key)
if current and tonnumber(current) > count then
    return tonnumber(current)
end

current = redis.call("incr",key)

if tonnumber(current) == 1 then
    redis.call("expire",key,time)
end

return tonnumber(current)