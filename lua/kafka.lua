--创建对象
local kafka={}

--kafka依赖库
local client = require "resty.kafka.client"
local producer = require "resty.kafka.producer"

--配置kafka的链接地址
local broker_list = {
      { host = "192.168.200.128", port = 9092 }
}

--发送消息
--queuename:队列名字
--content:发送的内容
function kafka.send(queuename,content)
        --创建生产者
        local pro = producer:new(broker_list,{ producer_type="async"})
        --发送消息
        local offset, err = pro:send(queuename, nil, content)
        --返回结果
        return offset
end

return kafka