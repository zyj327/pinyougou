package com.pyg.core.controller.address;



import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.pojo.address.Address;
import com.pyg.core.service.address.AddressService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
@RequestMapping("/address")
public class AddressController {

    @Reference
    private AddressService addressService;

    /**
     * 用户所有收件人信息展示
     * @return
     */
    @RequestMapping("/findListByLoginUser.do")
    public List<Address> findListByLoginUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return addressService.findListByLoginUser(username);
    }

}
