package com.seckill.manager.service;

import com.seckill.manager.pojo.Admin;

public interface AdminServer {
    Admin findByName(String name);
}
