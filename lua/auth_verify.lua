ngx.header.content_type="application/json;charset=utf8"

--引入json库
local cjson = require "cjson"

--引入jwt模块
local jwttoken = require "token"

--获取请求头中的令牌数据
local auth_header = ngx.var.http_Authorization

--调用令牌校验
local result = jwttoken.check(auth_header)

-- 输出结果
ngx.say(cjson.encode(result))
ngx.exit(result.code)