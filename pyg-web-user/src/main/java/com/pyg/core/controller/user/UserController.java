package com.pyg.core.controller.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.user.User;
import com.pyg.core.service.user.UserService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    /**
     * 获取短信验证码
     * @param phone
     * @return
     */
    @RequestMapping("/sendCode.do")
    public Result sendCode(String phone){
        // todo 校验手机号
        try {
            userService.sendCode(phone);
            return new Result(true,"短信发送成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"短信发送失败!");
        }
    }

    /**
     * 用户注册
     * @param smscode
     * @param user
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(String smscode, @RequestBody User user){
        try {
            userService.add(smscode,user);
            return new Result(true,"注册成功!");
        } catch (RuntimeException e){
            e.printStackTrace();
            return new Result(false,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"注册失败!");
        }
    }
}
