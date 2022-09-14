package com.seckill.manager.controller;

import com.seckill.manager.pojo.Admin;
import com.seckill.manager.service.AdminServer;
import com.seckill.common.util.JwtTokenUtil;
import com.seckill.common.util.Result;
import com.seckill.common.util.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private AdminServer adminServer;

    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> dataMap) throws Exception {
        Admin admin = adminServer.findByName(dataMap.get("username"));
        if (admin == null) {
            return new Result(false, StatusCode.ERROR, "账号不存在");
        }
        if (!admin.getPassword().equals(dataMap.get("password"))) {
            return new Result(false, StatusCode.ERROR, "密码错误");
        }
        // 登录成功，生成令牌
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("admin", admin.getLoginName());
        String jwt = JwtTokenUtil.generateTokenAdmin(UUID.randomUUID().toString(), payload, 900000000L);
        return new Result(true, StatusCode.OK, "登录成功", jwt);

    }
}
