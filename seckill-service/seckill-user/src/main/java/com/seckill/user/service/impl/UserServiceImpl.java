package com.seckill.user.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.seckill.common.util.TimeUtil;
import com.seckill.user.dao.UserMapper;
import com.seckill.user.pojo.User;
import com.seckill.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 通过用户名查找用户
     *
     * @param username
     * @return
     */
    @Override
    public User findByUsername(String username) {
        return userMapper.selectByPrimaryKey(username);
    }

    /**
     * User条件+分页查找
     *
     * @param user
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageInfo<User> findPage(User user, int pageNum, int pageSize) {
        // 分页
        PageHelper.startPage(pageNum, pageSize);
        // 搜索条件构建
        Example example = createExample(user);
        // 执行搜索
        return new PageInfo<>(userMapper.selectByExample(example));
    }

    /**
     * User构建查询对象
     *
     * @param user
     * @return
     */
    public Example createExample(User user) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        if (user != null) {
            // 用户名
            if (!StringUtils.isEmpty(user.getUsername())) {
                criteria.andLike("username", "%" + user.getUsername() + "%");
            }
            // 密码，加密存储
            if (!StringUtils.isEmpty(user.getPassword())) {
                criteria.andEqualTo("password", user.getPassword());
            }
            // 注册手机号
            if (!StringUtils.isEmpty(user.getPhone())) {
                criteria.andEqualTo("phone", user.getPhone());
            }
            // 注册邮箱
            if (!StringUtils.isEmpty(user.getEmail())) {
                criteria.andEqualTo("email", user.getEmail());
            }
            // 创建时间
            if (!StringUtils.isEmpty(user.getCreated())) {
                criteria.andGreaterThanOrEqualTo("created", user.getCreated());
                // 当天时间
                criteria.andLessThan("created", TimeUtil.addDateHour(user.getCreated(), 24));
            }
            // 修改时间
            if (!StringUtils.isEmpty(user.getUpdated())) {
                criteria.andEqualTo("updated", user.getUpdated());
            }
            // 昵称
            if (!StringUtils.isEmpty(user.getNickName())) {
                criteria.andEqualTo("nickName", user.getNickName());
            }
            // 真实姓名
            if (!StringUtils.isEmpty(user.getName())) {
                criteria.andLike("name", "%" + user.getName() + "%");
            }
        }
        return example;
    }
}
