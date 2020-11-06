package com.yyj.service.impl;

import com.yyj.dao.OrderDao;
import com.yyj.enums.OrderChannelEnum;
import com.yyj.enums.OrderStatusEnum;
import com.yyj.pojo.MiaoshaOrder;
import com.yyj.pojo.OrderInfo;
import com.yyj.pojo.User;
import com.yyj.redis.OrderPrefix;
import com.yyj.redis.RedisService;
import com.yyj.service.OrderService;
import com.yyj.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private OrderDao orderDao;

    /**
     * 通过Id查询订单
     *
     * @param orderId
     * @return
     */
    @Override
    public OrderInfo findOrderInfoById(Long orderId) {
        return orderDao.findOrderInfoById(orderId);
    }

    /**
     * 通过UserId和GoodsId查询订单
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Override
    public OrderInfo findOrderInfoByUserIdAndGoodsId(Long userId, Long goodsId) {
        return orderDao.findOrderInfoByUserIdAndGoodsId(userId, goodsId);
    }

    /**
     * 在缓存中通过UserId和GoodsId查询秒杀订单
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Override
    public MiaoshaOrder findMiaoshaOrderByUserIdAndGoodsId(Long userId, Long goodsId) {
        //        return orderDao.findMiaoshaOrderByUserIdAndGoodsId(userId,goodsId);
        return redisService.get(OrderPrefix.getMiaoshaOrderByUserIdAndGoodsId, userId + "_" + goodsId, MiaoshaOrder.class);
    }

    /**
     * 创建订单和秒杀订单
     *
     * @param user
     * @param goodsVO
     * @return
     */
    @Override
    @Transactional
    public OrderInfo createOrder(User user, GoodsVO goodsVO) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(user.getId());
        orderInfo.setGoodsId(goodsVO.getId());
        //默认配送地址id为 0
        orderInfo.setDeliveryAddrId(0L);
        orderInfo.setGoodsName(goodsVO.getGoodsName());
        //默认订单商品数为 1
        orderInfo.setGoodsCount(1);
        orderInfo.setGoodsPrice(goodsVO.getMiaoshaPrice());
        //默认订单渠道为 pc
        orderInfo.setOrderChannel(OrderChannelEnum.PC.getCode());
        //默认订单状态为 新建未支付
        orderInfo.setStatus(OrderStatusEnum.NEW_UNPAID.getCode());
        //默认订单创建时间为 now
        //支付时间为空
        orderInfo.setCreateDate(new Date());
        //插入OrderInfo
        orderDao.insertOrderInfo(orderInfo);

        MiaoshaOrder miaoshaOrder = new MiaoshaOrder();
        miaoshaOrder.setUserId(user.getId());
        miaoshaOrder.setGoodsId(goodsVO.getId());
        miaoshaOrder.setOrderId(orderInfo.getId());
        //插入MiaoshaOrder
        orderDao.insertMiaoshaOrder(miaoshaOrder);

        redisService.set(OrderPrefix.getMiaoshaOrderByUserIdAndGoodsId, user.getId() + "_" + goodsVO.getId(), miaoshaOrder);

        //将orderInfo插入数据库后会返回主键至orderInfo
        return orderInfo;
    }

    /**
     * 清空秒杀订单表和订单详情表
     */
    @Override
    public void deleteOrders() {
        orderDao.deleteOrders();
        orderDao.deleteMiaoshaOrders();
    }
}
