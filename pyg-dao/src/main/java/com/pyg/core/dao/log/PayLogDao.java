package com.pyg.core.dao.log;

import com.pyg.core.pojo.log.PayLog;
import com.pyg.core.pojo.log.PayLogQuery;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PayLogDao {
    int countByExample(PayLogQuery example);

    int deleteByExample(PayLogQuery example);

    int deleteByPrimaryKey(String outTradeNo);

    int insert(PayLog record);

    int insertSelective(PayLog record);

    List<PayLog> selectByExample(PayLogQuery example);

    PayLog selectByPrimaryKey(String outTradeNo);

    int updateByExampleSelective(@Param("record") PayLog record, @Param("example") PayLogQuery example);

    int updateByExample(@Param("record") PayLog record, @Param("example") PayLogQuery example);

    int updateByPrimaryKeySelective(PayLog record);

    int updateByPrimaryKey(PayLog record);
}