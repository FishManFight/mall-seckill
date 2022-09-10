package com.seckill.web.framework;

import com.seckill.common.util.StatusCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "Result", value = "Result")
@Data
public class Result<T> {
    @ApiModelProperty(value = "执行是否成功,true:成功,false:失败", required = true)
    private boolean flag;// 是否成功
    @ApiModelProperty(value = "返回状态码,20000:成功,20001:失败,20002:用户名或密码错误,20003:权限不足,20004:远程调用失败,20005:重复操作,20006:没有对应的数据", required = true)
    private Integer code;// 返回码
    @ApiModelProperty(value = "提示信息", required = true)
    private String message;// 返回消息
    @ApiModelProperty(value = "逻辑数据", required = true)
    private T data;// 返回数据

    public Result(boolean flag, Integer code, String message, T data) {
        this.flag = flag;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Result(boolean flag, Integer code, String message) {
        this.flag = flag;
        this.code = code;
        this.message = message;
    }

    public Result() {
        this.flag = true;
        this.code = StatusCode.OK;
        this.message = "操作成功";
    }
}
