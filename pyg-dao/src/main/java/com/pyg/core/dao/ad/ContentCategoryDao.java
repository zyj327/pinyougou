package com.pyg.core.dao.ad;

import com.pyg.core.pojo.ad.ContentCategory;
import com.pyg.core.pojo.ad.ContentCategoryQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ContentCategoryDao {
    int countByExample(ContentCategoryQuery example);

    int deleteByExample(ContentCategoryQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ContentCategory record);

    int insertSelective(ContentCategory record);

    List<ContentCategory> selectByExample(ContentCategoryQuery example);

    ContentCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ContentCategory record, @Param("example") ContentCategoryQuery example);

    int updateByExample(@Param("record") ContentCategory record, @Param("example") ContentCategoryQuery example);

    int updateByPrimaryKeySelective(ContentCategory record);

    int updateByPrimaryKey(ContentCategory record);
}