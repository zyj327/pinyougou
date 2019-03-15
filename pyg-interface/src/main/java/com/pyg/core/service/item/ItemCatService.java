package com.pyg.core.service.item;

import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.vo.ItemVo;

import java.util.List;

public interface ItemCatService {

    /**
     *商品分类
     * @param parentId
     * @return
     */
    public List<ItemCat> findByParentId(Long parentId);

    /**
     * 商品分类修改之数据回显
     * @param id
     * @return
     */
    public ItemCat findOne(Long id);

    /**
     * 添加商品分类
     * @param itemCat
     */
    public void add(ItemCat itemCat);

    /**
     * 条件查询所有商品分类
     * @param page
     * @param rows
     * @param itemCat
     * @return
     */
    public PageResult search(Integer page,Integer rows,ItemCat itemCat);

    /**
     * 修改商品分类
     * @param itemCat
     */
    public void update(ItemCat itemCat);

    /**
     * 批量删除
     */
    public void dele(Long[] ids);


    /**
     * 查询所有分类列表
     * @return
     */
    public List<ItemCat> findAll();


}
