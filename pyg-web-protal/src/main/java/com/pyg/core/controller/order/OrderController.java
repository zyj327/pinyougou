package com.pyg.core.controller.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.order.Order;
import com.pyg.core.service.order.OrderService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    /**
     * 提交订单
     * @param order
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody Order order){
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            orderService.add(order,username);
            return new Result(true,"提交成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true,"提交失败!");
        }
    }

}
