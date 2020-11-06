package com.yyj.controller;

import com.yyj.access.AccessLimit;
import com.yyj.enums.StatusEnum;
import com.yyj.pojo.MiaoshaOrder;
import com.yyj.pojo.OrderInfo;
import com.yyj.pojo.User;
import com.yyj.rabbitmq.MQSender;
import com.yyj.rabbitmq.MiaoshaMessage;
import com.yyj.redis.GoodsPrefix;
import com.yyj.redis.MiaoshaPrefix;
import com.yyj.redis.OrderPrefix;
import com.yyj.redis.RedisService;
import com.yyj.service.GoodsService;
import com.yyj.service.MiaoshaService;
import com.yyj.service.OrderService;
import com.yyj.utils.ResultVOUtil;
import com.yyj.vo.GoodsVO;
import com.yyj.vo.ResultVO;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MiaoshaService miaoshaService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MQSender mqSender;

    /*用于记录商品是否秒杀完毕*/
    private Map<Long, Boolean> goodsOverMap = new HashMap<>();


    /**
     * 验证验证码后获取秒杀路径
     *
     * @param user
     * @param goodsId
     * @param verifyCode
     * @return
     */
    @GetMapping("/path")
    @ResponseBody
    @AccessLimit(seconds = 5, maxCount = 5)
    public ResultVO<String> getMiaoshaPath(
            User user,
            @RequestParam("goodsId") Long goodsId,
            @RequestParam(value = "verifyCode", defaultValue = "0") Integer verifyCode
    ) {
        //若用户未登录，返回异常
        if (user == null) {
            return ResultVOUtil.error(StatusEnum.SESSION_ERROR);
        }

        Boolean checkResult = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!checkResult) {
            return ResultVOUtil.error(StatusEnum.VERIFY_CODE_ERROR);
        }

        String miaoshaPath = miaoshaService.createMiaoshaPath(user, goodsId);
        return ResultVOUtil.success(miaoshaPath);
    }

    /**
     * 秒杀操作
     * 此方法不用抛出异常，因为此方法为多并发，抛出异常消耗过大
     * 优化方法：优先减少对数据库的访问，再减少对redis的访问
     *
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/{path}/do_miaosha")
    @ResponseBody
    public ResultVO<OrderInfo> doMiaosha(
            User user,
            @RequestParam("goodsId") Long goodsId,
            @PathVariable("path") String path
    ) {
        //若用户未登录，返回异常
        if (user == null) {
            return ResultVOUtil.error(StatusEnum.SESSION_ERROR);
        }

        //验证path
        Boolean checkResult = miaoshaService.checkPath(user, goodsId, path);
        if (!checkResult) {
            return ResultVOUtil.error(StatusEnum.REQUEST_ILLEGAL);
        }

        //goodsOverMap判断秒杀商品是否秒杀完毕，减少redis访问
        Boolean result = goodsOverMap.get(goodsId);
        if (result == null) {
            return ResultVOUtil.error(StatusEnum.GOODS_NOT_EXIST);
        }
        if (result) {
            return ResultVOUtil.error(StatusEnum.MIAOSHA_OVER);
        }

        //判断是否已经秒杀成功过，若为是，返回不能重复秒杀
        MiaoshaOrder miaoshaOrder = orderService.findMiaoshaOrderByUserIdAndGoodsId(user.getId(), goodsId);
        if (miaoshaOrder != null) {
            return ResultVOUtil.error(StatusEnum.MIAOSHA_REPEAT);
        }

        //redis预减库存
        Long stock = redisService.decrease(GoodsPrefix.goodsStock, "" + goodsId);
        if (stock < 0) {
            goodsOverMap.put(goodsId, true);
            return ResultVOUtil.error(StatusEnum.MIAOSHA_OVER);
        }

        //下订单操作入队
        MiaoshaMessage miaoshaMessage = new MiaoshaMessage(user, goodsId);
        mqSender.miaoshaSend(miaoshaMessage);
        miaoshaService.setQueueUp(user.getId(), goodsId);

        //0:排队中
        return ResultVOUtil.success(0);
    }

    /**
     * 查询秒杀结果
     * 0:排队中
     * -1:秒杀失败
     * orderId:成功
     *
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/result")
    @ResponseBody
    public ResultVO<OrderInfo> miaoshaResult(User user, @RequestParam("goodsId") Long goodsId) {
        //若用户未登录，返回异常
        if (user == null) {
            return ResultVOUtil.error(StatusEnum.SESSION_ERROR);
        }

        Long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return ResultVOUtil.success(result);
    }

    /**
     * 重置
     *
     * @return
     */
    @GetMapping("/reset")
    @ResponseBody
    public ResultVO<Boolean> reset() {
        List<GoodsVO> goodsVOList = goodsService.findGoodsVO();
        for (GoodsVO goodsVO : goodsVOList) {
            goodsVO.setStockCount(10);
            redisService.set(GoodsPrefix.goodsStock, "" + goodsVO.getId(), 10);
            goodsOverMap.put(goodsVO.getId(), false);
        }
        redisService.delete(MiaoshaPrefix.isQueueUp);
        redisService.delete(OrderPrefix.getMiaoshaOrderByUserIdAndGoodsId);
        miaoshaService.reset(goodsVOList);
        return ResultVOUtil.success(true);
    }

    /**
     * 创建验证码图片
     *
     * @param response
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/verifyCode")
    @ResponseBody
    public ResultVO<String> getMiaoshaVerifyCode(
            HttpServletResponse response,
            User user,
            @RequestParam("goodsId") Long goodsId
    ) {
        //若用户未登录，返回异常
        if (user == null) {
            return ResultVOUtil.error(StatusEnum.SESSION_ERROR);
        }
        try {
            BufferedImage image = miaoshaService.createVerifyCode(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultVOUtil.error(StatusEnum.SERVER_ERROR);
        }
    }

    /**
     * 系统初始化时，将秒杀商品库存存入redis，初始化goodsOverMap
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVO> goodsVOList = goodsService.findGoodsVO();
        if (CollectionUtils.isEmpty(goodsVOList)) {
            return;
        }
        for (GoodsVO goodsVO : goodsVOList) {
            redisService.set(GoodsPrefix.goodsStock, "" + goodsVO.getId(), goodsVO.getStockCount());
            goodsOverMap.put(goodsVO.getId(), false);
        }
    }
}
