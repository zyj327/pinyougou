package com.pyg.core.listener;

import com.pyg.core.service.staticpage.StaticPageService;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

/**
 * 自定义监听器:生成商品详情静态页
 */
public class PageListener implements MessageListener {

    @Resource
    private StaticPageService staticPageService;

    /**
     * 获取消息并消费消息
     * @param message
     */
    @Override
    public void onMessage(Message message) {
        try {
            // 取出消息
            ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
            String id = activeMQTextMessage.getText();
            // 消费消息
            //System.out.println("service-page获取得id:"+id);
            staticPageService.getHtml(Long.parseLong(id));
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}
