package com.yyj.controller;

import com.yyj.enums.MiaoshaStatusEnum;
import com.yyj.enums.StatusEnum;
import com.yyj.exception.GlobalException;
import com.yyj.pojo.User;
import com.yyj.redis.GoodsPrefix;
import com.yyj.redis.RedisService;
import com.yyj.service.GoodsService;
import com.yyj.utils.ResultVOUtil;
import com.yyj.vo.GoodsDetailVO;
import com.yyj.vo.GoodsVO;
import com.yyj.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisService redisService;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /* 页面缓存 */
    //方法仅处理request请求中Accept头中包含了"text/html"的请求，同时暗示了返回的内容类型为text/html;
    @GetMapping(value = "/to_list", produces = "text/html")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
        //查看通过UserArgumentResolver赋值的user
        if (user != null) {
            log.info("com.yyj.controller.GoodsController.toList User:" + user.toString());
        }

        //从缓存中取列表页面
        String html = redisService.get(GoodsPrefix.goodsList, "", String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        //缓存中无列表页面
        //1.从数据库中查询商品列表
        List<GoodsVO> goodsVOList = goodsService.findGoodsVO();
        model.addAttribute("goodsList", goodsVOList);
        //2.手动渲染页面
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_list", webContext);

        //若渲染成功，将页面保存至缓存
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsPrefix.goodsList, "", html);
        }

        return html;
    }

    /* 页面静态化，常用 */
    @GetMapping("/to_detail/{goodsId}")
    @ResponseBody
    public ResultVO<GoodsDetailVO> toDetail(User user, @PathVariable("goodsId") Long goodsId) {
        if (user == null) {
            throw new GlobalException(StatusEnum.SESSION_ERROR);
        }

        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);

        Long startTime = goodsVO.getStartDate().getTime();
        Long endTime = goodsVO.getEndDate().getTime();
        Long nowTime = System.currentTimeMillis();

        Integer miaoshaStatus = 0;
        Integer remainSeconds = 0;  //秒杀还有多久开始
        if (nowTime < startTime) {  //秒杀未开始
            miaoshaStatus = MiaoshaStatusEnum.NOT_STARTED.getCode();
            remainSeconds = (int) (startTime - nowTime) / 1000;
        } else if (nowTime > endTime) {  //秒杀已结束
            miaoshaStatus = MiaoshaStatusEnum.HAS_ENDED.getCode();
            remainSeconds = -1;
        } else {  //秒杀正在进行
            miaoshaStatus = MiaoshaStatusEnum.BE_IN_PROGRESS.getCode();
            remainSeconds = 0;
        }


        //构造GoodsDetailVO
        GoodsDetailVO goodsDetailVO = new GoodsDetailVO();
        goodsDetailVO.setGoods(goodsVO);
        goodsDetailVO.setUser(user);
        goodsDetailVO.setMiaoshaStatus(miaoshaStatus);
        goodsDetailVO.setRemainSeconds(remainSeconds);

        return ResultVOUtil.success(goodsDetailVO);
    }

    /* 页面缓存 */
  /*  @GetMapping(value = "/to_detail/{goodsId}", produces = "text/html")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable("goodsId") Long goodsId, HttpServletRequest request, HttpServletResponse response) {
        //查看通过UserArgumentResolver赋值的user
        if (user != null) {
            log.info("com.yyj.controller.GoodsController.toDetail User:" + user.toString());
        }
        model.addAttribute("user", user);

        //从缓存中取列表页面
        String html = redisService.get(GoodsPrefix.goodsDetail, "" + goodsId, String.class);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        //缓存中无列表页面
        //1.从数据库中查询商品列表
        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        model.addAttribute("goods", goodsVO);

        Long startTime = goodsVO.getStartDate().getTime();
        Long endTime = goodsVO.getEndDate().getTime();
        Long nowTime = System.currentTimeMillis();

        Integer miaoshaStatus = 0;
        Integer remainSeconds = 0;  //秒杀还有多久开始
        if (nowTime < startTime) {  //秒杀未开始
            miaoshaStatus = MiaoshaStatusEnum.NOT_STARTED.getCode();
            remainSeconds = (int) (startTime - nowTime) / 1000;
        } else if (nowTime > endTime) {  //秒杀已结束
            miaoshaStatus = MiaoshaStatusEnum.HAS_ENDED.getCode();
            remainSeconds = -1;
        } else {  //秒杀正在进行
            miaoshaStatus = MiaoshaStatusEnum.BE_IN_PROGRESS.getCode();
            remainSeconds = 0;
        }
        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        //2.手动渲染页面
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goods_detail", webContext);

        //若渲染成功，将页面保存至缓存
        if (!StringUtils.isEmpty(html)) {
            redisService.set(GoodsPrefix.goodsDetail, "" + goodsId, html);
        }

        return html;
    }*/


    /* 普通 */
    /*@GetMapping(value = "/to_detail/{goodsId}")
    public String toDetail(Model model, User user, @PathVariable("goodsId") Long goodsId) {
        //查看通过UserArgumentResolver赋值的user
        if (user != null) {
            log.info("com.yyj.controller.GoodsController.toDetail User:" + user.toString());
        }
        model.addAttribute("user", user);

        GoodsVO goodsVO = goodsService.findGoodsVOByGoodsId(goodsId);
        model.addAttribute("goods", goodsVO);

        Long startTime = goodsVO.getStartDate().getTime();
        Long endTime = goodsVO.getEndDate().getTime();
        Long nowTime = System.currentTimeMillis();

        Integer miaoshaStatus = 0;
        Integer remainSeconds = 0;  //秒杀还有多久开始
        if (nowTime < startTime) {  //秒杀未开始
            miaoshaStatus = MiaoshaStatusEnum.NOT_STARTED.getCode();
            remainSeconds = (int) (startTime - nowTime) / 1000;
        } else if (nowTime > endTime) {  //秒杀已结束
            miaoshaStatus = MiaoshaStatusEnum.HAS_ENDED.getCode();
            remainSeconds = -1;
        } else {  //秒杀正在进行
            miaoshaStatus = MiaoshaStatusEnum.BE_IN_PROGRESS.getCode();
            remainSeconds = 0;
        }

        model.addAttribute("miaoshaStatus", miaoshaStatus);
        model.addAttribute("remainSeconds", remainSeconds);

        return "goods_detail";
    }*/
}