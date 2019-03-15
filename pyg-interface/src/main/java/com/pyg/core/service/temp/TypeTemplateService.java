package com.pyg.core.service.temp;

import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {

    /**
     * 条件查询所有模板
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);

    /**
     * 添加模板
     * @param typeTemplate
     */
    void add(TypeTemplate typeTemplate);

    /**
     * 根据id查询个模板:模板修改之数据回显
     * @param id
     */
    TypeTemplate findOne(Long id);

    /**
     * 修改模板
     * @param typeTemplate
     */
    void update(TypeTemplate typeTemplate);

    /**
     * 批量删除模板
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 删除单个模板
     * @param id
     */
    void delOne(Long id);

    /**
     * 修改商品分类时加载模板结果集
     * @return
     */
    List<TypeTemplate> selectOptionList();

    /**
     * 添加商品:回显规格选项
     * @param id
     * @return
     */
    List<Map> findBySpecList(Long id);
}
