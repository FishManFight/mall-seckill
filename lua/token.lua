--依赖jwt库
local jwt = require("resty.jwt")
--秘钥
local secret="5pil6aOO5YaN576O5Lmf5q+U5LiN5LiK5bCP6ZuF55qE56yR"

-- 定义一个名为 jwttoken 的模块
jwttoken = {}

--令牌校验
function jwttoken.check(auth_header)
    --定义响应数据
    local response = {}

    --如果请求头中没有令牌，则直接返回401
    if auth_header == nil then
        response["code"]=401
        response["message"]="没有找到令牌数据"
        return response
    end

    --查找令牌中的Bearer前缀字符，并进行截取
    local _, _, token = string.find(auth_header, "Bearer%s+(.+)")

    --如果没有Bearer，则表示令牌无效
    if token == nil then
        response["code"]=401
        response["message"]="令牌格式不正确"
        return response
    end

    --校验令牌
	
    local jwt_obj = jwt:verify(secret,token)
    --如果校验结果中的verified==false，则表示令牌无效
    if jwt_obj.verified == false then
        response["code"]=401
        response["message"]="令牌无效"
        return response
    end

    --全部校验完成后，说明令牌有效，返回令牌数据
    response["code"]=200
    response["message"]="令牌校验通过"
    response["body"]=jwt_obj
    return response
end

return jwttoken