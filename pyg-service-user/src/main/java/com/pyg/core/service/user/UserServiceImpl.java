package com.pyg.core.service.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.core.dao.user.UserDao;
import com.pyg.core.pojo.user.User;
import com.pyg.core.utils.md5.MD5Util;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination smsDestination;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private UserDao userDao;

    /**
     * 获取短信验证码
     * @param phone
     */
    @Override
    public void sendCode(final String phone) {
        final String code = RandomStringUtils.randomNumeric(6);
        System.out.println("code:"+code);
        // 保存验证码到redis中
        redisTemplate.boundValueOps(phone).set(code);
        // 设置过期时间: 5分钟
        redisTemplate.boundValueOps(phone).expire(5,TimeUnit.MINUTES);
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("phoneNumbers", phone);
                mapMessage.setString("signName", "阮文");
                mapMessage.setString("templateCode", "SMS_140720901");
                mapMessage.setString("templateParam", "{\"code\":\""+code+"\"}");
                return mapMessage;
            }
        });
    }

    /**
     * 用户注册
     * @param smscode
     * @param user
     */
    @Override
    public void add(String smscode, User user) {
        // 从redis中取出验证码
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        // 对比验证码
        if(code != null && code.equals(smscode)){ // 验证成功
            // 密码加密
            String password = MD5Util.MD5Encode(user.getPassword(), null);
            user.setPassword(password); // 加密后密码
            user.setCreated(new Date()); // 创建时间: 数据库不能为空
            user.setUpdated(new Date()); // 修改时间: 同上
            // 保存用户
            userDao.insertSelective(user);
        }else{
            throw new RuntimeException("验证码不正确!");
        }
    }
}
