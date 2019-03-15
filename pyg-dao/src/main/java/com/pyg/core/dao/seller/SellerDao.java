package com.pyg.core.dao.seller;

import com.pyg.core.pojo.seller.Seller;
import com.pyg.core.pojo.seller.SellerQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SellerDao {
    int countByExample(SellerQuery example);

    int deleteByExample(SellerQuery example);

    int deleteByPrimaryKey(String sellerId);

    int insert(Seller record);

    int insertSelective(Seller record);

    List<Seller> selectByExample(SellerQuery example);

    Seller selectByPrimaryKey(String sellerId);

    int updateByExampleSelective(@Param("record") Seller record, @Param("example") SellerQuery example);

    int updateByExample(@Param("record") Seller record, @Param("example") SellerQuery example);

    int updateByPrimaryKeySelective(Seller record);

    int updateByPrimaryKey(Seller record);
}