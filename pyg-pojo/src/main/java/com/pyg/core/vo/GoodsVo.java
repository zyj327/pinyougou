package com.pyg.core.vo;

import com.pyg.core.pojo.good.Goods;
import com.pyg.core.pojo.good.GoodsDesc;
import com.pyg.core.pojo.item.Item;

import java.io.Serializable;
import java.util.List;

/**
 * 封装所有商品属性
 */
public class GoodsVo implements Serializable {
    private Goods goods;            //商品信息
    private GoodsDesc goodsDesc;    //商品详细信息
    private List<Item> itemList;    //商品对应的库存信息

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
