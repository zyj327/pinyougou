package com.pyg.core.dao.specification;

import com.pyg.core.pojo.specification.SpecificationOption;
import com.pyg.core.pojo.specification.SpecificationOptionQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SpecificationOptionDao {
    int countByExample(SpecificationOptionQuery example);

    int deleteByExample(SpecificationOptionQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(SpecificationOption record);

    int insertSelective(SpecificationOption record);

    List<SpecificationOption> selectByExample(SpecificationOptionQuery example);

    SpecificationOption selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") SpecificationOption record, @Param("example") SpecificationOptionQuery example);

    int updateByExample(@Param("record") SpecificationOption record, @Param("example") SpecificationOptionQuery example);

    int updateByPrimaryKeySelective(SpecificationOption record);

    int updateByPrimaryKey(SpecificationOption record);

    /**
     * 批量保存
     * @param specificationOptionList
     */
    void insertSelectives(List<SpecificationOption> specificationOptionList);

    /**
     * 批量删除规格选项
     * @param ids
     */
    void deleteByPrimaryKeys(Long[] ids);
}