package com.yyj.service.impl;

import com.yyj.pojo.MiaoshaOrder;
import com.yyj.pojo.OrderInfo;
import com.yyj.pojo.User;
import com.yyj.redis.MiaoshaPrefix;
import com.yyj.redis.RedisService;
import com.yyj.service.GoodsService;
import com.yyj.service.MiaoshaService;
import com.yyj.service.OrderService;
import com.yyj.utils.MD5Util;
import com.yyj.utils.UUIDUtil;
import com.yyj.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

@Service
public class MiaoshaServiceImpl implements MiaoshaService {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisService redisService;

    private static char[] operators = new char[]{'+', '-', '*'};

    /**
     * 秒杀操作，减库存，加订单
     *
     * @param user
     * @param goodsVO
     * @return
     */
    @Transactional
    public OrderInfo miaosha(User user, GoodsVO goodsVO) {
        Boolean result = goodsService.reduceStock(goodsVO);
        //这里一定要判断减库存操作的返回结果，成功则创建订单
        if (result) {
            return orderService.createOrder(user, goodsVO);
        } else {
            return null;
        }
    }

    /**
     * 获取秒杀结果
     *
     * @param id
     * @param goodsId
     * @return
     */
    @Override
    public Long getMiaoshaResult(Long userId, Long goodsId) {
        MiaoshaOrder miaoshaOrder = orderService.findMiaoshaOrderByUserIdAndGoodsId(userId, goodsId);
        if (miaoshaOrder != null) {  //秒杀成功
            return miaoshaOrder.getOrderId();
        } else {  //秒杀失败或排队中
            Boolean isQueueUp = getQueueUp(userId, goodsId);
            if (isQueueUp) {  //排队中
                return 0L;
            } else {  //秒杀失败
                return -1L;
            }
        }
    }

    /**
     * 重置商品列表
     *
     * @param goodsVOList
     */
    @Override
    public void reset(List<GoodsVO> goodsVOList) {
        goodsService.resetStock(goodsVOList);
        orderService.deleteOrders();
    }

    /**
     * 用户秒杀成功的进行标志存入redis
     *
     * @param userId
     * @param goodsId
     */
    @Override
    public void setQueueUp(Long userId, Long goodsId) {
        redisService.set(MiaoshaPrefix.isQueueUp, userId + "_" + goodsId, true);
    }

    /**
     * 查询redis中是否有用户秒杀成功的进行标志
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Override
    public Boolean getQueueUp(Long userId, Long goodsId) {
        return redisService.isExist(MiaoshaPrefix.isQueueUp, userId + "_" + goodsId);
    }

    /**
     * 生成秒杀路径
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public String createMiaoshaPath(User user, Long goodsId) {
        String md5 = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisService.set(MiaoshaPrefix.MiaoshaPath, user.getId() + "_" + goodsId, md5);
        return md5;
    }


    /**
     * 验证秒杀路径
     *
     * @param user
     * @param goodsId
     * @param path
     * @return
     */
    @Override
    public Boolean checkPath(User user, Long goodsId, String path) {
        if (path == null) {
            return false;
        }
        String oldPath = redisService.get(MiaoshaPrefix.MiaoshaPath, user.getId() + "_" + goodsId, String.class);
        return path.equals(oldPath);
    }

    /**
     * 生成验证码
     *
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public BufferedImage createVerifyCode(User user, Long goodsId) {
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        // generate a random code
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        //把验证码存到redis中
        int rnd = calc(verifyCode);
        redisService.set(MiaoshaPrefix.MiaoshaVerifyCode, user.getId() + "_" + goodsId, rnd);
        //输出图片
        return image;
    }

    /**
     * 验证验证码
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @Override
    public Boolean checkVerifyCode(User user, Long goodsId, Integer verifyCode) {
        Integer oldCode = redisService.get(MiaoshaPrefix.MiaoshaVerifyCode, user.getId() + "_" + goodsId, Integer.class);
        if (oldCode == null || oldCode != verifyCode) {
            return false;
        }
//        redisService.delete(MiaoshaPrefix.MiaoshaVerifyCode, user.getId() + "_" + goodsId);
        return true;
    }

    private String generateVerifyCode(Random rdm) {
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = operators[rdm.nextInt(3)];
        char op2 = operators[rdm.nextInt(3)];
        String expression = "" + num1 + op1 + num2 + op2 + num3;
        return expression;
    }

    private Integer calc(String verifyCode) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
