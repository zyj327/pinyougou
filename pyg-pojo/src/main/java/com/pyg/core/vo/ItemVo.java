package com.pyg.core.vo;

import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.pojo.template.TypeTemplate;

import java.io.Serializable;
import java.util.List;

/**
 * 商品分类:封装商品分类+类型模板
 */
public class ItemVo implements Serializable {
    private ItemCat itemCat;
    private List<TypeTemplate> templateList;

    public ItemVo() {
    }

    public ItemVo(ItemCat itemCat, List<TypeTemplate> templateList) {
        this.itemCat = itemCat;
        this.templateList = templateList;
    }

    public ItemCat getItemCat() {
        return itemCat;
    }

    public void setItemCat(ItemCat itemCat) {
        this.itemCat = itemCat;
    }

    public List<TypeTemplate> getTemplateList() {
        return templateList;
    }

    public void setTemplateList(List<TypeTemplate> templateList) {
        this.templateList = templateList;
    }
}
