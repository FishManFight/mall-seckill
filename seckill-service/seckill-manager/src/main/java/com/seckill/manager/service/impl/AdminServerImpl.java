package com.seckill.manager.service.impl;

import com.seckill.manager.dao.AdminMapper;
import com.seckill.manager.pojo.Admin;
import com.seckill.manager.service.AdminServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminServerImpl implements AdminServer {
    @Autowired
    private AdminMapper adminMapper;
    @Override
    public Admin findByName(String name) {
        Admin admin = new Admin();
        admin.setLoginName(name);
        return adminMapper.selectOne(admin);
    }
}
