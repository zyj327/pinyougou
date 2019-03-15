package com.pyg.core.controller.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.Result;
import com.pyg.core.service.pay.WxPayService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pay")
public class WxPayController {

    @Reference
    private WxPayService wxPayService;

    /**
     * 生成支付页面需要的数据
     * @return
     */
    @RequestMapping("/createNative.do")
    public Map<String,String> createNative(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return wxPayService.createNative(userId);
    }

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    @RequestMapping("/queryPayStatus.do")
    public Result queryPayStatus(String out_trade_no){
        int time = 0;
        while (true){
            // 查询订单
            Map<String, String> resultMap = wxPayService.queryPayStatus(out_trade_no);
            if(resultMap == null){
                return new Result(false,"支付失败!");
            }
            if(resultMap.get("trade_state").equals("SUCCESS")){
                return new Result(true,"支付成功");
            }
            // 支付中,支付失败等情况
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            time++;
            if(time>360){
                return new Result(false,"二维码超时");
            }
        }
    }
}
