package com.pyg.core.controller.temp;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.PageResult;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.template.TypeTemplate;
import com.pyg.core.service.temp.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
    @Reference
    TypeTemplateService typeTemplateService;

    /**
     * 条件查询所有模板
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate typeTemplate){
        return typeTemplateService.search(page,rows,typeTemplate);
    }

    /**
     * 添加模板
     * @param typeTemplate
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 根据id查询个模板:模板修改之数据回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    /**
     * 模板更新
     * @param typeTemplate
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody TypeTemplate typeTemplate){
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 批量删除模板
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            typeTemplateService.delete(ids);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 修改商品分类时加载模板结果集
     * @return
     */
    @RequestMapping("/selectOptionList.do")
    public List<TypeTemplate> selectOptionList(){
        return typeTemplateService.selectOptionList();
    }

    /**
     * 删除单个模板
     * @param id
     * @return
     */
    @RequestMapping("/delOne.do")
    public Result delOne(Long id){
        try {
            typeTemplateService.delOne(id);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }
}
