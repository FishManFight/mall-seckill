package com.seckill.user.service;

import com.github.pagehelper.PageInfo;
import com.seckill.user.pojo.User;

public interface UserService {
    User findByUsername(String username);

    PageInfo<User> findPage(User user, int pageNum, int pageSize);
}
