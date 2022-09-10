package com.seckill.goods.dao;
import com.seckill.goods.pojo.UndoLog;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.common.Mapper;

@Repository
public interface UndoLogMapper extends Mapper<UndoLog> {
}
