package com.pyg.core.service.cart;

import com.pyg.core.pojo.cart.Cart;
import com.pyg.core.pojo.item.Item;

import java.util.List;


public interface CartService {

    /**
     * 根据id获取商品(sku)
     * @param id
     * @return
     */
    public Item findOne(Long id);

    /**
     *  填充购物车回显需要的数据
     * @param cartList
     * @return
     */
    List<Cart> findCartList(List<Cart> cartList);

    /**
     * 登录后: 将本地购物车加入到redis中
     * @return
     */
    public void mergeCartList(List<Cart> cartList,String name);

    /**
     * 从reids中取出购物车
     * @param username
     */
    List<Cart> findCartListForRedis(String username);

}
