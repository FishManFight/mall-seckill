--引入json解析库
local cjson = require("cjson")
--kafka依赖库
local client = require "resty.kafka.client"
local producer = require "resty.kafka.producer"
--配置kafka的链接地址
local broker_list = {
      { host = "192.168.200.128", port = 9092 }
}
--创建生产者
local pro = producer:new(broker_list,{ producer_type="async"})

--获取IP
local headers=ngx.req.get_headers()
local ip=headers["X-REAL-IP"] or headers["X_FORWARDED_FOR"] or ngx.var.remote_addr or "0.0.0.0"

--定义消息内容
local logjson = {}
logjson["uri"]=ngx.var.uri
logjson["ip"]=ip
logjson["token"]="Bearer TEST"
logjson["actime"]=os.date("%Y-%m-%d %H:%M:%S")

--发送消息
local offset, err = pro:send("itemaccess", nil, cjson.encode(logjson))

--页面跳转
local uri = ngx.var.uri
uri = string.gsub(uri,"/web","")
ngx.exec(uri)