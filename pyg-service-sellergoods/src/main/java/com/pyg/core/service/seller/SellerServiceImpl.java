package com.pyg.core.service.seller;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.core.dao.seller.SellerDao;
import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.seller.Seller;
import com.pyg.core.pojo.seller.SellerQuery;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class SellerServiceImpl implements SellerService {
    @Resource
    private SellerDao sellerDao;

    @Transactional
    @Override
    public void add(Seller seller) {
        // 设置商家状态
        seller.setStatus("0"); // 未审核
        seller.setCreateTime(new Date()); //提交日期
        // 需要对密码进行加密:md5,BCrypt
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // 加密
        String password = bCryptPasswordEncoder.encode(seller.getPassword());
        seller.setPassword(password);
        sellerDao.insertSelective(seller);
    }

    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        // 设置分页参数
        PageHelper.startPage(page,rows);
        // 设置查询条件
        SellerQuery query = new SellerQuery();
        SellerQuery.Criteria criteria = query.createCriteria();
        // 商家状态
        query.createCriteria().andStatusEqualTo(seller.getStatus());
        if(seller.getName()!=null && !"".equals(seller.getName().trim())){
            criteria.andNameLike("%"+seller.getName()+"%");
        }
        if(seller.getNickName()!=null && !"".equals(seller.getNickName().trim())){
            criteria.andNickNameLike("%"+seller.getNickName()+"%");
        }
        Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(query);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public Seller findOne(String sellerId) {
        return sellerDao.selectByPrimaryKey(sellerId);
    }

    @Override
    public void updateStatus(String sellerId, String status) {
        Seller seller = new Seller();
        seller.setSellerId(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }
}
