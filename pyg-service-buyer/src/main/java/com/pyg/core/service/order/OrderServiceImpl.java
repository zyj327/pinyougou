package com.pyg.core.service.order;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.core.dao.item.ItemDao;
import com.pyg.core.dao.log.PayLogDao;
import com.pyg.core.dao.order.OrderDao;
import com.pyg.core.dao.order.OrderItemDao;
import com.pyg.core.pojo.cart.Cart;
import com.pyg.core.pojo.item.Item;
import com.pyg.core.pojo.log.PayLog;
import com.pyg.core.pojo.order.Order;
import com.pyg.core.pojo.order.OrderItem;
import com.pyg.core.utils.uniquekey.IdWorker;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private IdWorker idWorker;

    @Resource
    private ItemDao itemDao;

    @Resource
    private OrderItemDao orderItemDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private PayLogDao payLogDao;


    /**
     * 提交订单
     * @param order
     * @param username
     */
    @Override
    public void add(Order order, String username) {
        // 一个商家对应一个订单,取出redis中的购物车对象
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
        if(cartList != null){
            // 创建list保存订单id
            List<Long> orderIds = new ArrayList<>();
            // 支付总金额
            double totalFee = 0F;
            for (Cart cart : cartList) {
                // 保存订单信息
                long orderId = idWorker.nextId();
                order.setOrderId(orderId); // 订单id
                order.setStatus("1"); // 支付状态
                order.setCreateTime(new Date()); // 订单创建时间
                order.setUserId(username); // 提交订单用户
                order.setSourceType("2"); // 订单来源
                order.setSellerId(cart.getSellerId()); // 商家id
                double totalPrice = 0f; // 订单总价
                // 获取购物项
                List<OrderItem> orderItemList = cart.getOrderItemList();
                if(orderItemList != null && orderItemList.size() >0){
                    for (OrderItem orderItem : orderItemList) {
                        // 保存订单详细
                        long id = idWorker.nextId();
                        orderItem.setId(id); // 明细id
                        orderItem.setOrderId(orderId); // 外键
                        Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                        orderItem.setGoodsId(item.getGoodsId()); // spu ID
                        orderItem.setPicPath(item.getImage()); // 商品图片
                        orderItem.setSellerId(item.getSellerId()); // 商家ID
                        orderItem.setTitle(item.getTitle()); // spu 标题
                        orderItem.setPrice(new BigDecimal(item.getPrice().doubleValue()));  // 商品单价
                        totalPrice += item.getPrice().doubleValue() * orderItem.getNum(); // 订单总价格
                        orderItem.setTotalFee(new BigDecimal(totalPrice));  // 商品总价
                        orderItemDao.insertSelective(orderItem); // 保存订单详细
                    }
                }
                // 支付总金额
                totalFee += totalPrice;
                order.setPayment(new BigDecimal(totalPrice)); // 订单支付金额,该商家下所有商品价格
                orderDao.insertSelective(order);
            }
            // 提交订单生成支付日志
            PayLog payLog = new PayLog();
            payLog.setOutTradeNo(String.valueOf(idWorker.nextId())); // 支付订单号
            payLog.setCreateTime(new Date()); // 订单创建日期
            payLog.setOrderList(orderIds.toString().replace("[","").replace("]","")); // 订单集合
            payLog.setTotalFee((long) totalFee*100); // 订单总金额
            payLog.setUserId(username); // 订单用户
            payLog.setTradeState("2"); // 1未支付,2已支付
            payLog.setPayType("1"); // 支付类型
            payLogDao.insertSelective(payLog);
            // 调用接口时还要获取数据,因此将数据放入缓存中
            redisTemplate.boundHashOps("payLog").put(username,payLog);
        }
        // 删除redis中的购物车
        redisTemplate.boundHashOps("BUYER_CART").delete(username);
    }
}
