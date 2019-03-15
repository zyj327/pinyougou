package com.pyg.core.service.brand;

import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {
    /**
     * 查询所有
     * @return
     */
    public List<Brand> findAll();

    /**
     * 条件查询所有
     * @param pagNum
     * @param pageSize
     * @param brand
     * @return
     */
    public PageResult search(Integer pagNum, Integer pageSize, Brand brand);

    /**
     * 添加品牌
     * @param brand
     */
    void add(Brand brand);

    /**
     * 根据id查询单条数据
     * @param id
     * @return
     */
    Brand findOne(Long id);

    /**
     * 修改数据之保存
     * @param brand
     */
    void update(Brand brand);

    /**
     * 批量删除
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 删除单个品牌
     * @param id
     */
    void delOne(Long id);

    /**
     * 新增模板时加载品牌结果集
     * @return
     */
    List<Map> selectOptionList();
}
