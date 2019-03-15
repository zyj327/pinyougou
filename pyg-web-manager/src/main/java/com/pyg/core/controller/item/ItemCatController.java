package com.pyg.core.controller.item;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.PageResult;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.service.item.ItemCatService;
import com.pyg.core.vo.ItemVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    /**
     *  商品分类列表查询
     * @param parentId
     * @return
     */
    @RequestMapping("/findByParentId.do")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    /**
     * 商品分类修改之数据回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }

    /**
     * 新增商品分类
     * @param itemCat
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody ItemCat itemCat){
        try {
            itemCatService.add(itemCat);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 条件查询
     * @param page
     * @param rows
     * @param itemCat
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page,Integer rows,@RequestBody ItemCat itemCat){
        return itemCatService.search(page,rows,itemCat);
    }

    /**
     * 更新商品分类
     * @param itemCat
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody ItemCat itemCat){
        try {
            itemCatService.update(itemCat);
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
    public Result dele(Long[] ids){
        try {
            itemCatService.dele(ids);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }


    /**
     * 查询商品所有分类
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }
}
