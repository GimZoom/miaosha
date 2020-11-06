package com.yyj.rabbitmq;

import com.yyj.utils.BeanAndStringConvertUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQSender {
    @Autowired
    private AmqpTemplate amqpTemplate;

    public void miaoshaSend(Object msg) {
        String str = BeanAndStringConvertUtil.beanToString(msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE, str);
    }


    public void directSend(Object msg) {
        String str = BeanAndStringConvertUtil.beanToString(msg);
        log.info("send direct message: " + str);
        amqpTemplate.convertAndSend(MQConfig.DIRECT_QUEUE, str);
    }

    public void topicSend(Object msg) {
        String str = BeanAndStringConvertUtil.beanToString(msg);
        log.info("send topic message: " + str);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key1", str);
        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, "topic.key2", str);
    }

    public void fanoutSend(Object msg) {
        String str = BeanAndStringConvertUtil.beanToString(msg);
        log.info("send fanout message: " + str);
        //注意！fanout模式调用的方法一定要带一个空的routingKey
        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE, "", str);
    }

    public void headersSend(Object msg) {
        String str = BeanAndStringConvertUtil.beanToString(msg);
        log.info("send headers message: " + str);
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("header1", "value1");
        messageProperties.setHeader("header2", "value2");
        Message message = new Message(str.getBytes(), messageProperties);
        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE, "", message);
    }
}
