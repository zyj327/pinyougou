package com.pyg.core.service.spec;

import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.specification.Specification;
import com.pyg.core.vo.SpecVo;

import java.util.List;
import java.util.Map;

public interface SpecService {

    /**
     * 条件查询所有规格
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    public PageResult search(Integer page, Integer rows, Specification specification);

    /**
     * 添加规格
     * @param specVo
     */
    void add(SpecVo specVo);

    /**
     * 修改规格之数据回显
     * @param id
     * @return
     */
    SpecVo findOne(Long id);

    /**
     * 批量删除
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 修改规格之保存
     * @param specVo
     */
    void update(SpecVo specVo);

    /**
     * 删除单个规格
     * @param id
     */
    void delOne(Long id);

    /**
     * 新增模板时获取规格结果集
     * @return
     */
    List<Map> selectOptionList();
}
