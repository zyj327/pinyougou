package com.pyg.core.service.goods;

import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.good.Goods;
import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.vo.GoodsVo;

import java.util.List;

public interface GoodsService {

    /**
     * 添加商品信息
     * @param goodsVo
     */
    public void add(GoodsVo goodsVo);

    /**
     * 查询商品列表:商家后台
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    public PageResult search(Integer page, Integer rows, Goods goods);

    /**
     * 查询商品列表:运营商后台
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    public PageResult searchByManager(Integer page, Integer rows, Goods goods);


    /**
     * 修改商品之商品回显
     */
    public GoodsVo findOne(Long id);

    /**
     * 修改商品
     * @param goodsVo
     */
    public void update(GoodsVo goodsVo);

    /**
     * 商品审核:运营商后台
     * @param ids
     * @param status
     */
    public void updateStatus(Long[] ids,String status);

    /**
     * 商品批量删除
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 删除单个商品
     * @param id
     */
    void delOne(Long id);
}
