package com.yyj.service;

import com.yyj.pojo.OrderInfo;
import com.yyj.pojo.User;
import com.yyj.vo.GoodsVO;

import java.awt.image.BufferedImage;
import java.util.List;

public interface MiaoshaService {

    OrderInfo miaosha(User user, GoodsVO goodsVO);

    Long getMiaoshaResult(Long id, Long goodsId);

    void reset(List<GoodsVO> goodsVOList);

    void setQueueUp(Long userId, Long goodsId);

    Boolean getQueueUp(Long userId, Long goodsId);

    String createMiaoshaPath(User user, Long goodsId);

    Boolean checkPath(User user, Long goodsId, String path);

    BufferedImage createVerifyCode(User user, Long goodsId);

    Boolean checkVerifyCode(User user, Long goodsId, Integer verifyCode);
}
