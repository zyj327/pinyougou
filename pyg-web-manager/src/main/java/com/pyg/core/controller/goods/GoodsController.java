package com.pyg.core.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.PageResult;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.good.Goods;
import com.pyg.core.service.goods.GoodsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;

    /**
     * 待审商品列表: 运营商后台
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        return goodsService.searchByManager(page,rows,goods);
    }

    /**
     * 商品审核:运营商后台
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("/updateStatus.do")
    public Result updateStatus(Long[] ids,String status){
        try {
            goodsService.updateStatus(ids,status);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }

    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            goodsService.delete(ids);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }
    /**
     * 删除单个商品
     * @param id
     * @return
     */
    @RequestMapping("/delOne.do")
    public Result dele(Long id){
        try {
            goodsService.delOne(id);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

}
