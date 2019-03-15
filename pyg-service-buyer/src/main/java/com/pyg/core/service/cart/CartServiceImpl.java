package com.pyg.core.service.cart;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.core.dao.item.ItemDao;
import com.pyg.core.dao.seller.SellerDao;
import com.pyg.core.pojo.cart.Cart;
import com.pyg.core.pojo.item.Item;
import com.pyg.core.pojo.order.OrderItem;
import com.pyg.core.pojo.seller.Seller;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class CartServiceImpl implements CartService {

    @Resource
    private ItemDao itemDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 根据id获取商品(sku)
     * @param id
     * @return
     */
    @Override
    public Item findOne(Long id) {
        return itemDao.selectByPrimaryKey(id);
    }

    /**
     * 填充购物车回显需要的数据
     * @param cartList
     * @return
     */
    @Override
    public List<Cart> findCartList(List<Cart> cartList) {
        for (Cart cart : cartList) {
            // 填充商家店铺名称
            Seller seller = sellerDao.selectByPrimaryKey(cart.getSellerId());
            cart.setSellerName(seller.getName());
            // 填充购物项的数据
            List<OrderItem> list = cart.getOrderItemList();
            for (OrderItem orderItem : list) {
                Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                orderItem.setPicPath(item.getImage()); // 商品的图片
                orderItem.setPrice(item.getPrice()); // 商品的单价
                orderItem.setTitle(item.getTitle()); // 商品的标题
                // 小计 = 单价 * 数量
                BigDecimal totalFee = new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum());
                orderItem.setTotalFee(totalFee); // 商品小计
            }
        }
        return cartList;
    }

    /**
     * 登录后: 将本地购物车加入到redis中
     * @param newCartList
     * @param name
     */
    @Override
    public void mergeCartList(List<Cart> newCartList, String name) {
        // 合并购物车
        // 从reids中取出老车
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(name);
        // 将新车合并到老车中
        oldCartList = mergeNewCartListToOldCartList(newCartList,oldCartList);
        // 将合并后的购物车存入redis中
        redisTemplate.boundHashOps("BUYER_CART").put(name,oldCartList);
    }

    /**
     * 从redis中取出购物车
     * @param username
     */
    @Override
    public List<Cart> findCartListForRedis(String username) {
        //Map<String,List<Cart>> map = (Map<String, List<Cart>>) redisTemplate.boundHashOps("BUYER_CART");
        return (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);

    }

    /**
     * 合并购物车
     * @param newCartList
     * @param oldCartList
     * @return
     */
    private List<Cart> mergeNewCartListToOldCartList(List<Cart> newCartList, List<Cart> oldCartList) {
        if(newCartList != null){ // 新购物车不为空
            if(oldCartList != null){ // 旧购物车不为空
                // 合并购物车
                // 遍历新车
                for (Cart cart : newCartList) {
                    // 判断是不是同一个商家
                    int sellerIndexOf = oldCartList.indexOf(cart);
                    if(sellerIndexOf != -1){ // 同一个商家
                        // 判断是不是同一款商品
                        List<OrderItem> newOrderItemList = cart.getOrderItemList(); // 新车购物项集
                        List<OrderItem> oldOrderItemList = oldCartList.get(sellerIndexOf).getOrderItemList(); // 旧车购物项集
                        for (OrderItem newOrderItem : newOrderItemList) {
                            // 判断老车中是否有新车购物项
                            int orderItemIndexOf = oldOrderItemList.indexOf(newOrderItem);
                            if(orderItemIndexOf != -1){ // 老车中包含新车中的购物项,合并,数量相加
                                OrderItem oldOrderItem = oldOrderItemList.get(orderItemIndexOf); // 取出相同购物项
                                oldOrderItem.setNum(oldOrderItem.getNum()+newOrderItem.getNum());
                            }else{
                                // 不是同款商品：是同一个商家。将该购物项加入到该商家下的购物项集中
                                oldOrderItemList.add(newOrderItem);
                            }
                        }
                    }else{ // 不是同一个商家
                        oldCartList.add(cart);
                    }
                }

            }else{ // 老购物车为空,说明第一次登陆,直接返回新购物车
                return newCartList;
            }
        }
        // 新购物车为空,不需要做处理,直接返回老车
        return oldCartList;
    }
}
