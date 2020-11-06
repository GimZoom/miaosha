package com.yyj.controller;

import com.yyj.rabbitmq.MQSender;
import com.yyj.redis.RedisService;
import com.yyj.redis.UserPrefix;
import com.yyj.service.UserService;
import com.yyj.utils.ResultVOUtil;
import com.yyj.vo.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/sample")
public class SampleController {
    @Autowired
    private UserService userService;
   @Autowired
    private RedisService redisService;
   @Autowired
   private MQSender mqSender;
/*
    @RequestMapping("/helloSuccess")
    @ResponseBody
    public ResultVO helloSuccess() {
        return ResultVOUtil.success("hellooooo");
    }

    @RequestMapping("/helloError")
    @ResponseBody
    public ResultVO helloError() {
        return ResultVOUtil.error(StatusEnum.SERVER_ERROR);
    }

    @RequestMapping("/thymeleaf")
    public String thymeleaf(Model model) {
        model.addAttribute("name", "decade");
        return "hello-thymeleaf";
    }

    @RequestMapping("/db/get")
    @ResponseBody
    public ResultVO<User> dbGet() {
        User user = userService.findById(1);
        return ResultVOUtil.success(user);
    }

    @RequestMapping("/db/insert")
    @ResponseBody
    public ResultVO<Integer> dbInsert() {
        User user=new User();
        user.setName("kabuto");
        Integer insert = userService.insert(user);
        return ResultVOUtil.success(insert);
    }

*/

    @RequestMapping("/redis/set")
    @ResponseBody
    public ResultVO<Boolean> set() {
        Boolean flag = redisService.set(UserPrefix.test, "2", "decade");
        return ResultVOUtil.success(flag);
    }

    @RequestMapping("/redis/get")
    @ResponseBody
    public ResultVO<Integer> get() {
        String user = redisService.get(UserPrefix.test, "2", String.class);
        return ResultVOUtil.success(user);
    }

    @RequestMapping("/mq/direct")
    @ResponseBody
    public ResultVO mqDirect(){
        mqSender.directSend("fucking crazy");
        return ResultVOUtil.success();
    }

    @RequestMapping("/mq/topic")
    @ResponseBody
    public ResultVO mqTopic(){
        mqSender.topicSend("fucking crazy");
        return ResultVOUtil.success();
    }

    @RequestMapping("/mq/fanout")
    @ResponseBody
    public ResultVO mqFanout(){
        mqSender.fanoutSend("fucking crazy");
        return ResultVOUtil.success();
    }

    @RequestMapping("/mq/headers")
    @ResponseBody
    public ResultVO mqHeaders(){
        mqSender.headersSend("fucking crazy");
        return ResultVOUtil.success();
    }
}
