--redis连接配置
local config = {
    name = "test",
    serv_list = {
        {ip="192.168.200.128", port = 7001},
        {ip="192.168.200.128", port = 7002},
        {ip="192.168.200.128", port = 7003},
        {ip="192.168.200.128", port = 7004},
        {ip="192.168.200.128", port = 7005},
        {ip="192.168.200.128", port = 7006},
    },
    idle_timeout    = 1000,
    pool_size       = 10000,
}

--引入redis集群配置
local redis_cluster = require "resty.rediscluster"

--定义一个对象
local lredis = {}

--根据key查询
function lredis.get(key)
        --创建链接
        local red = redis_cluster:new(config)
        red:init_pipeline()

        --根据key获取数据
        red:get(key)
        local rresult = red:commit_pipeline()

        --关闭链接
        red:close()

        return rresult
end


--添加带过期的数据
function lredis.setexp(key,value,time)
        --创建链接
        local red = redis_cluster:new(config)
        red:init_pipeline()

        --添加key，同时设置过期时间
        red:set(key,value)
        red:expire(key,time)

        local rresult = red:commit_pipeline()
end

--根据key查询hash
function lredis.hget(key1,key2)
        --创建链接
        local red = redis_cluster:new(config)
        red:init_pipeline()

        --根据key获取数据
        red:hmget(key1,key2)
        local rresult = red:commit_pipeline()

        --关闭链接
        red:close()

        return rresult[1]
end

--hash数据添加
function lredis.hset(key1,key2,value)
        --创建链接
        local red = redis_cluster:new(config)
        red:init_pipeline()

        --添加hash数据
        red:hmset(key1,key2,value)
        local rresult = red:commit_pipeline()

        --关闭链接
        red:close()
        return rresult
end

--hash中指定的key自增
function lredis.hincrby(key1,key2,value)
        --创建链接
        local red = redis_cluster:new(config)
        red:init_pipeline()

        --添加hash数据
        red:hincrby(key1,key2,value)
        local rresult = red:commit_pipeline()

        --关闭链接
        red:close()

        return rresult
end

return lredis