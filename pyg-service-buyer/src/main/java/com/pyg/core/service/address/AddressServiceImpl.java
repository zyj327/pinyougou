package com.pyg.core.service.address;

import com.alibaba.dubbo.config.annotation.Service;
import com.pyg.core.dao.address.AddressDao;
import com.pyg.core.pojo.address.Address;
import com.pyg.core.pojo.address.AddressQuery;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Resource
    private AddressDao addressDao;

    /**
     * 用户所有收件人信息
     * @param username
     * @return
     */
    @Override
    public List<Address> findListByLoginUser(String username) {
        AddressQuery query = new AddressQuery();
        query.createCriteria().andUserIdEqualTo(username);
        List<Address> addressList = addressDao.selectByExample(query);
        return addressList;
    }
}
