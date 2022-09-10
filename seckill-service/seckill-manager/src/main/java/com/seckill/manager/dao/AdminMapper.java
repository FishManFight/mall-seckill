package com.seckill.manager.dao;

import com.seckill.manager.pojo.Admin;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface AdminMapper extends Mapper<Admin> {
}
