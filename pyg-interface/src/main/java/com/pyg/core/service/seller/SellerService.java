package com.pyg.core.service.seller;

import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.seller.Seller;

public interface SellerService {

    /**
     * 添加商家
     */
    public void add(Seller seller);

    /**
     * 条件查询所有商家
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    public PageResult search(Integer page,Integer rows,Seller seller);

    /**
     * 审核商家之数据回显
     * @param sellerId
     * @return
     */
    public Seller findOne(String sellerId);

    /**
     * 商家审核
     * @param sellerId
     * @param status
     */
    public void updateStatus(String sellerId, String status);
}
