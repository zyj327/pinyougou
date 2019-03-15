package com.pyg.core.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.PageResult;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.good.Goods;
import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.service.goods.GoodsService;
import com.pyg.core.service.item.ItemCatService;
import com.pyg.core.vo.GoodsVo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/goods")
public class GoodsController {
    @Reference
    private GoodsService goodsService;
    @Reference
    private ItemCatService itemCatService;

    /**
     * 添加商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody GoodsVo goodsVo){
        try {
            // 设置sellerId:商家id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(sellerId);
            goodsService.add(goodsVo);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");

        }
    }

    /**
     * 查询商品列表:商家后台
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Goods goods){
        // 设置商家id:sellerId
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(sellerId);
        return goodsService.search(page,rows,goods);
    }

    /**
     * 商品修改之数据回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public GoodsVo findOne(Long id){
        return goodsService.findOne(id);
    }

    /**
     * 更新商品
     * @param goodsVo
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody GoodsVo goodsVo){
        try {
            goodsService.update(goodsVo);
            return new Result(true,"更新成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败!");
        }
    }


}
