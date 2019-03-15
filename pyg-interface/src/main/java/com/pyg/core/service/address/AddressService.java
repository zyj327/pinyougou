package com.pyg.core.service.address;

import com.pyg.core.pojo.address.Address;

import java.util.List;

public interface AddressService {

    /**
     * 用户所有收件人信息
     * @param username
     * @return
     */
    List<Address> findListByLoginUser(String username);

}
