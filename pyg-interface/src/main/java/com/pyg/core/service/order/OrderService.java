package com.pyg.core.service.order;

import com.pyg.core.pojo.order.Order;

public interface OrderService {

    /**
     * 提交订单
     * @param order
     * @param username
     */
    void add(Order order, String username);
}
