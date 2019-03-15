package com.pyg.core.dao.template;

import com.pyg.core.pojo.template.TypeTemplate;
import com.pyg.core.pojo.template.TypeTemplateQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TypeTemplateDao {
    int countByExample(TypeTemplateQuery example);

    int deleteByExample(TypeTemplateQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(TypeTemplate record);

    int insertSelective(TypeTemplate record);

    List<TypeTemplate> selectByExample(TypeTemplateQuery example);

    TypeTemplate selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TypeTemplate record, @Param("example") TypeTemplateQuery example);

    int updateByExample(@Param("record") TypeTemplate record, @Param("example") TypeTemplateQuery example);

    int updateByPrimaryKeySelective(TypeTemplate record);

    int updateByPrimaryKey(TypeTemplate record);

    /**
     * 批量删除模板
     * @param ids
     */
    void deleteByPrimaryKeys(Long[] ids);

    /**
     * 新增商品分类时加载分类模板
     * @return
     */
    List<TypeTemplate> selectOptionList();
}