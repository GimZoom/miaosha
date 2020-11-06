package com.yyj.service;

import com.yyj.pojo.MiaoshaOrder;
import com.yyj.pojo.OrderInfo;
import com.yyj.pojo.User;
import com.yyj.vo.GoodsVO;

public interface OrderService {

    OrderInfo findOrderInfoById(Long orderId);

    OrderInfo findOrderInfoByUserIdAndGoodsId(Long userId, Long goodsId);

    MiaoshaOrder findMiaoshaOrderByUserIdAndGoodsId(Long userId, Long goodsId);

    OrderInfo createOrder(User user, GoodsVO goodsVO);

    void deleteOrders();
}
