package com.pyg.core.controller.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.cart.Cart;
import com.pyg.core.pojo.item.Item;
import com.pyg.core.pojo.order.OrderItem;
import com.pyg.core.service.cart.CartService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;


    /**
     * 添加购物车
     * @param itemId
     * @param num
     * @return
     */
    // 使用CrossOrigin注解,该属性,allowCredentials = "true" 默认是true,可以不写
    @RequestMapping("/addGoodsToCartList.do")
    @CrossOrigin(origins = {"http://localhost:9003"})
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request,HttpServletResponse response){
        /*
        服务器端支持跨域
            response.setHeader("Access-Control-Allow-Origin","http://localhost:9003");
            response.setHeader("Access-Control-Allow-Credentials","true");
         */
        try {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            boolean flag = false; // 定义一个开关
            // 1,定义一个空车对象
            List<Cart> cartList = null;
            // 2,判断本地是否有购物车
            Cookie[] cookies = request.getCookies();
            if(cookies != null && cookies.length>0){
                for (Cookie cookie : cookies) {
                    if("BUYER_CART".equals(cookie.getName())){
                        // 3,本地有购物车,直接取出
                        String text = cookie.getValue();
                        String decode = URLDecoder.decode(text);
                        cartList = JSON.parseArray(decode,Cart.class);
                        // 跳出循环
                        break;
                    }
                }
            }
            // 4,本地没有购物车,创建一个购物车
            if(cartList==null){
                cartList = new ArrayList<>();
            }
            // 将商品封装cart对象中,对cookie进行瘦身
            Cart cart = new Cart();
            Item item = cartService.findOne(itemId); // 获取商品对象:sku
            cart.setSellerId(item.getSellerId()); // 封装商家id
            List<OrderItem> orderItemList = new ArrayList<>(); // 购物项集合
            // 因为cookie有大小限制(最大4k,超过保存不了),所以给该对象瘦身,不保存全部数据,封装商品对象(sku:商品id,商品数量,其他不封装),商品数量,商家id
            OrderItem orderItem = new OrderItem(); // 购物车中的购物项
            orderItem.setItemId(itemId); // 商品id
            orderItem.setNum(num); // 商品数量
            orderItemList.add(orderItem);// 购物项封装到购物项集合中
            cart.setOrderItemList(orderItemList); // 购物项集合添加到购物车
            // 5,将商品进行封装购物车
            int sellerIndexOf = cartList.indexOf(cart);// 判断商品是否属于同一个商家
            if(sellerIndexOf != -1){ // 属于同一商家
                // 判断是否有同款商品,将该商家购物项取出
                Cart oldCart = cartList.get(sellerIndexOf);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();//取出购物项
                int itemIndexOf = oldOrderItemList.indexOf(orderItem);// 判断是否是同款商品
                if(itemIndexOf != -1){ // 同款商品
                    // 合并数量
                    OrderItem orderItem1 = oldOrderItemList.get(itemIndexOf); // 之前的购物项
                    orderItem1.setNum(orderItem1.getNum()+num); // 合并数量
                }else{ // 不是同款
                    // 添加商品到购物车
                    oldOrderItemList.add(orderItem);
                }


            }else{ // 不属于同一个商家
                cartList.add(cart);
            }
            // 6,保存购物车
            if(!"anonymousUser".equals(username)){ // 已登录: anonymousUser:匿名用户
                // 购物车保存到redis中
                cartService.mergeCartList(cartList,username);
                // 6.1 若本地有,清空本地购物车
                if(flag){
                    Cookie cookie = new Cookie("BUYER_CART",null);
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }else{ // 未登录
                // 购物车保存到本地cookie中: tomcat7 不需要转码 tomcat8 需要转码
                String s = JSON.toJSONString(cartList);
                String encode = URLEncoder.encode(s);
                // 6,将商品保存到购物车
                Cookie cookie = new Cookie("BUYER_CART",encode);
                cookie.setMaxAge(60*60); // cookie过期时间1h
                cookie.setPath("/"); // 设置cookie共享
                response.addCookie(cookie);
            }
            return new Result(true,"添加购物车成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"添加购物车失败!");
        }
    }

    /**
     * 回显购物车中的列表数据
     * @return
     */
    @RequestMapping("/findCartList.do")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response){
        // 未登录 从cookie中取
        List<Cart> cartList = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length>0){
            for (Cookie cookie : cookies) {
                if("BUYER_CART".equals(cookie.getName())){
                    String text = cookie.getValue();
                    String decode = URLDecoder.decode(text);
                    cartList = JSON.parseArray(decode,Cart.class);
                    // 跳出循环
                    break;
                }
            }
        }
        // 已登录
        // 获取用户
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!"anonymousUser".equals(username)){ // 已登录
            // 将本地购物车同步到redis中
            if(cartList != null){
                cartService.mergeCartList(cartList,username);
                // 清空本地购物车
                Cookie cookie = new Cookie("BUYER_CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            cartList = cartService.findCartListForRedis(username);
        }
        // 取出的购物车需要进行数据的填充
        if(cartList != null){
            // 填充数据
            cartList = cartService.findCartList(cartList);
        }
        return cartList;
    }
}
