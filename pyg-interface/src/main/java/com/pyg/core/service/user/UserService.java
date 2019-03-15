package com.pyg.core.service.user;

import com.pyg.core.pojo.user.User;

public interface UserService {

    /**
     * 获取短信验证码
     * @param phone
     */
    public void sendCode(String phone);

    /**
     * 用户注册
     * @param smscode
     * @param user
     */
    public void add(String smscode, User user);
}
