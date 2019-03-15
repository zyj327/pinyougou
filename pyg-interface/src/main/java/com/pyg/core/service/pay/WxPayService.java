package com.pyg.core.service.pay;

import java.util.Map;

public interface WxPayService {

    /**
     * 动态生成支付二维码
     * @return
     */
    Map<String,String> createNative(String userId);

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryPayStatus(String out_trade_no);
}
