package com.yyj.controller;

import com.yyj.enums.StatusEnum;
import com.yyj.exception.GlobalException;
import com.yyj.pojo.OrderInfo;
import com.yyj.pojo.User;
import com.yyj.service.GoodsService;
import com.yyj.service.OrderService;
import com.yyj.utils.ResultVOUtil;
import com.yyj.vo.GoodsVO;
import com.yyj.vo.OrderDetailVO;
import com.yyj.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/order")
@Controller
public class OrderController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;

    @GetMapping("/detail")
    @ResponseBody
    public ResultVO<OrderDetailVO> detail(User user, @RequestParam("orderId") Long orderId) {
        if (user == null) {
            throw new GlobalException(StatusEnum.SESSION_ERROR);
        }

        OrderInfo orderInfo = orderService.findOrderInfoById(orderId);
        if (orderInfo == null) {
            throw new GlobalException(StatusEnum.ORDER_NOT_EXIST);
        }

        Long goodsId = orderInfo.getGoodsId();
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        OrderDetailVO orderDetailVO = new OrderDetailVO();
        orderDetailVO.setGoods(goodsVO);
        orderDetailVO.setOrder(orderInfo);

        return ResultVOUtil.success(orderDetailVO);
    }
}
