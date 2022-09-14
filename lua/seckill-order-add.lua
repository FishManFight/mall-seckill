ngx.header.content_type="application/json;charset=utf8"
--引入json库
local cjson = require "cjson"
--引入jwt模块
local jwttoken = require "token"
--引入redis
local redis = require "redis-cluster"
--引入kafka
local kafka = require "kafka"

--获取请求头中的令牌数据
local auth_header = ngx.var.http_Authorization
--调用令牌校验
local result = jwttoken.check(auth_header)

--如果code==200表示令牌校验通过
if result.code==200 then
	--响应结果
	local response = {}

	--获取id
	local uri_args = ngx.req.get_uri_args()
	local id = uri_args["id"]

	--判断该商品用户是否已经在指定时间内购买过
	local username = result["body"]["payload"]["username"]
	local userKey= "USER"..username.."ID"..id
	local hasbuy = redis.get(userKey)
	
	--如果没有购买，则判断该商品是否是热点商品
	if hasbuy==nil or hasbuy[1]==nil or hasbuy[1]==ngx.null then
		--从redis中获取该商品信息
		local num = redis.hget("SKU_"..id,"num")[1]

		--如果不是热点商品，则走普通抢单流程
		if num==nil or num==ngx.null then
			--拼接url
			local url = "/api/order/add/"..id
			--执行请求
			ngx.exec(url)
			return
		else
			--热点商品
			num = tonumber(num)
			
			--如果有库存，才允许抢单
			if num<=0 then
                --库存不足，无法排队
		        response["code"]=405
                response["message"]="当前商品库存不足，无法抢购"
		        ngx.say(cjson.encode(response))
				return
			else
				--递增排队
				local incrresult = redis.hincrby("SKU_"..id,userKey,1)
				local incrcount = incrresult[1]
                -- 判断用户是否在排队，userKey自增，返回值是1，表示之前没有排队，可以排队
				if incrcount==1 then
                    --热点数据，发送MQ排队
                    local userorder = {}
                    userorder["username"]=username
                    userorder["id"]=id

					--排队抢单
					kafka.send("neworder",cjson.encode(userorder))

                    --response["incrresult"]=incrresult
                    --response["incrcount"]=incrcount
                    response["code"]=202
                    response["message"]="您正在排队抢购该商品"
					ngx.say(cjson.encode(response))
					return
				else
					--响应用户正在排队抢购该商品
                	response["code"]=202
        	        response["message"]="您正在排队抢购该商品"
	                ngx.say(cjson.encode(response))
					return
				end
			end
		end
	else
		--24小时内购买过该商品
		response["code"]=415
        response["message"]="您24小时内已经抢购了该商品，不能重复抢购"
        ngx.say(cjson.encode(response))
		return
	end
else
	-- 输出结果
	ngx.say(cjson.encode(result))
	ngx.exit(result.code)
end