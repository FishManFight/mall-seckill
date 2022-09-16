package com.seckill.user.controller;

import com.github.pagehelper.PageInfo;
import com.seckill.common.util.JwtTokenUtil;
import com.seckill.common.util.StatusCode;
import com.seckill.user.pojo.User;
import com.seckill.user.service.UserService;
import com.seckill.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     *
     * @param dataMap
     * @return
     * @throws Exception
     */
    @PostMapping(value = "/login")
    public Result login(@RequestParam Map<String, String> dataMap) throws Exception {
        // 1.根据用户名查询用户信息
        User user = userService.findByUsername(dataMap.get("username"));
        // 2.用户不存在
        if (ObjectUtils.isEmpty(user)) {
            return new Result(false, StatusCode.ERROR, "用户不存在");
        }
        // 3.密码错误
        String MD5password = DigestUtils.md5DigestAsHex(dataMap.get("password").getBytes());
        if (!user.getPassword().equals(MD5password)) {
            return new Result(false, StatusCode.ERROR, "密码错误");
        }
        // 4.登录成功
        HashMap<String, Object> payload = new HashMap<>();
        payload.put("username", user.getUsername());
        payload.put("name", user.getName());
        payload.put("phone", user.getPhone());
        String jwt = JwtTokenUtil.generateTokenUser(UUID.randomUUID().toString(), payload, 900000000L);
        return new Result(true, StatusCode.OK, jwt);
    }

    @RequestMapping(value = "/search/{pageNum}/{pageSize}")
    public Result findPage(@RequestBody(required = false) User user, @PathVariable int pageNum, @PathVariable int pageSize) {
        // 调用ParaService实现分页条件查询Para
        PageInfo<User> pageInfo = userService.findPage(user, pageNum, pageSize);
        return new Result(true, StatusCode.OK, "查询成功", pageInfo);
    }
}
