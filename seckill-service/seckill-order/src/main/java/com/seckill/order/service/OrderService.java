package com.seckill.order.service;

import com.github.pagehelper.PageInfo;
import com.seckill.order.pojo.Order;

import java.io.IOException;
import java.util.Map;

public interface OrderService {

    /***
     * 热点商品下单
     */
    void hotAdd(Map<String, String> orderMap) throws IOException;

    /***
     * 添加订单
     */
    int add(Order order);

    /***
     * Order多条件分页查询
     * @param order
     * @param page
     * @param size
     * @return
     */
    PageInfo<Order> findPage(Order order, int page, int size);

    Order findById(String id);

    void pay(String id);
}
