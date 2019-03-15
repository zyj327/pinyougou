package com.pyg.core.service.pay;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.wxpay.sdk.WXPayUtil;
import com.pyg.core.dao.log.PayLogDao;
import com.pyg.core.pojo.log.PayLog;
import com.pyg.core.utils.http.HttpClient;
import com.pyg.core.utils.uniquekey.IdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Service
public class WxPayServiceImpl implements WxPayService {

    // 注入微信支付凭证
    @Value("${appid}")
    private String appid;

    // 商户号
    @Value("${partner}")
    private String partner;

    // 秘钥
    @Value("${partnerkey}")
    private String partnerkey;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    // 订单日志接口代理对象
    @Resource
    private PayLogDao payLogDao;

    @Resource
    private IdWorker idWorker;


    /**
     * 动态生成支付二维码:微信支付
     * @return
     */
    @Override
    public Map<String, String> createNative(String userId) {
        Map<String,String> map = null;
        try {
            // 从redis中获取订单日志对象
            PayLog payLog = (PayLog) redisTemplate.boundHashOps("payLog").get(userId);
            // 1,创建map对象,封装向微信平台传递的数据
            map = new HashMap<>();
            // appid
            map.put("appid",appid);
            // 商户号
            map.put("mch_id",partner);
            // 随机字符串
            map.put("nonce_str",WXPayUtil.generateNonceStr());
            // 商品描述
            map.put("body","品优购订单支付");
            // 商户订单号
            //String out_trade_no = idWorker.nextId()+"";
            map.put("out_trade_no",payLog.getOutTradeNo());
            // 金额
            map.put("total_fee",payLog.getTotalFee()+"");
            // 终端ip
            map.put("spbill_create_ip","127.0.0.1");
            // 回调地址
            map.put("notify_url","http://www.baidu.com");
            // 交易类型
            map.put("trade_type","NATIVE");

            // 2,将map对象转换为具有签名的xml格式参数
            String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);

            // 3,创建httpClient对象,向微信支付平台发送请求,获取支付地址
            HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
            // 设置请求参数
            httpClient.setHttps(true);
            // 设置请求参数
            httpClient.setXmlParam(signedXml);
            // 设置请求方式
            httpClient.post();

            // 4,获取统一下单结果(xml)
            String resultXml = httpClient.getContent();

            // 5,把返回结果xml转换为map
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);

            // 6,返回结果封装订单号和金额
            resultMap.put("out_trade_no",payLog.getOutTradeNo());
            resultMap.put("total_fee",payLog.getTotalFee()+"");
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    /**
     * 查询订单状态
     * @param out_trade_no
     * @return
     */
    @Override
    public Map<String, String>  queryPayStatus(String out_trade_no) {
        try {
            // 微信查询订单接口
            String url = "https://api.mch.weixin.qq.com/pay/orderquery";
            // 封装接口需要的数据
            Map<String,String> map = new HashMap<>();
            map.put("appid",appid);
            map.put("mch_id",partner);
            map.put("out_trade_no",out_trade_no);
            map.put("nonce_str",WXPayUtil.generateNonceStr());
            String signedXml = WXPayUtil.generateSignedXml(map, partnerkey);
            // 发送请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setHttps(true); // 是否为http请求
            httpClient.setXmlParam(signedXml); // 设置参数
            httpClient.post(); // 设置方式
            // 获取返回值: 返回值类型也是xml
            String resultXml = httpClient.getContent();

            // 把返回值转换为map对象
            Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
            //System.out.println(resultMap.get("trade_state"));
            // 如果支付成功,更新订单交易日志
            if(resultMap.get("trade_state").equals("SUCCESS")){
                // 创建订单对象
                PayLog payLog = new PayLog();
                payLog.setOutTradeNo(out_trade_no); // 根据主键更新
                payLog.setPayTime(new Date()); // 支付时间
                payLog.setTradeState("1"); // 支付状态
                payLog.setTransactionId(resultMap.get("transaction_id")); // 交易流水
                payLogDao.insertSelective(payLog);
            }
            return resultMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
