package com.yyj.rabbitmq;

import com.yyj.pojo.User;
import com.yyj.service.GoodsService;
import com.yyj.service.MiaoshaService;
import com.yyj.utils.BeanAndStringConvertUtil;
import com.yyj.vo.GoodsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQReceiver {
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private GoodsService goodsService;

    /**
     * 出队后进行减库存，下订单
     * @param msg
     */
    @RabbitListener(queues = MQConfig.MIAOSHA_QUEUE)
    public void miaoshaReceive(String msg){
        MiaoshaMessage miaoshaMessage = BeanAndStringConvertUtil.stringToBean(msg, MiaoshaMessage.class);
        if(miaoshaMessage==null){
            return;
        }
        User user=miaoshaMessage.getUser();
        Long goodsId=miaoshaMessage.getGoodsId();

        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        if(goodsVO.getStockCount()<=0){
            return;
        }

        miaoshaService.miaosha(user, goodsVO);
    }

    @RabbitListener(queues = MQConfig.DIRECT_QUEUE)
    public void directReceive(String msg){
        log.info("receive direct message: "+msg);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE1)
    public void topicReceive1(String msg){
        log.info("receive topic1 message: "+msg);
    }

    @RabbitListener(queues = MQConfig.TOPIC_QUEUE2)
    public void topicReceive2(String msg){
        log.info("receive topic2 message: "+msg);
    }

    @RabbitListener(queues = MQConfig.HEADERS_QUEUE)
    public void headersReceive(byte[] msg){
        log.info("receive headers message: "+new String(msg));
    }
}
