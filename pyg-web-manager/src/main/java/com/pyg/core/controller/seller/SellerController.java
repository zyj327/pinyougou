package com.pyg.core.controller.seller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.PageResult;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.seller.Seller;
import com.pyg.core.service.seller.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller")
public class SellerController {

    @Reference
    private SellerService sellerService;

    /**
     * 商家管理之商家列表展示
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller){
        return sellerService.search(page,rows,seller);
    }

    /**
     * 商家审核之数据回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public Seller findOne(String id){
        return sellerService.findOne(id);
    }

    /**
     * 商家审核
     * @param sellerId
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(String sellerId,String status){
        try {
            sellerService.updateStatus(sellerId,status);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

}
