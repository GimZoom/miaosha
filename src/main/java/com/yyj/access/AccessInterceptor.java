package com.yyj.access;

import com.alibaba.fastjson.JSON;
import com.yyj.enums.StatusEnum;
import com.yyj.pojo.User;
import com.yyj.redis.AccessPrefix;
import com.yyj.redis.RedisService;
import com.yyj.service.UserService;
import com.yyj.service.impl.UserServiceImpl;
import com.yyj.utils.CookieUtil;
import com.yyj.utils.ResultVOUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AccessInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            //获取用户并注入UserContext
            User user = getUser(request, response);

            //获取注解
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) {
                //若该方法未添加@AccessLimit注解，直接放行
                return true;
            }
            //该方法添加了@AccessLimit注解
            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            if (needLogin) {
                //若该方法需要登录，则验证用户
                if (user == null) {
                    returnResult(response, StatusEnum.SESSION_ERROR);
                    return false;
                }
            }

            //限制访问次数
            String requestURI = request.getRequestURI();
            String key = requestURI + "_" + user.getId();
            AccessPrefix accessPrefix = AccessPrefix.getAccessPrefix(seconds);
            Integer count = redisService.get(accessPrefix, key, Integer.class);
            if (count == null) {
                redisService.set(accessPrefix, key, 1);
            } else if (count < maxCount) {
                redisService.increase(accessPrefix, key);
            } else {
                returnResult(response, StatusEnum.ACCESS_LIMIT);
                return false;
            }
        }
        return true;
    }

    //通过cookie或参数中的token获取User
    private User getUser(HttpServletRequest request, HttpServletResponse response) {
        //根据Cookie中或参数中传来的token来去redis中获取user对象
        String token_parameter = request.getParameter(UserServiceImpl.COOKIE_TOKEN);
        String token_cookie = CookieUtil.getCookieValue(request, UserServiceImpl.COOKIE_TOKEN);

        if (StringUtils.isEmpty(token_cookie) && StringUtils.isEmpty(token_parameter)) {
            return null;
        }
        //优先使用参数中的token
        String token = StringUtils.isEmpty(token_parameter) ? token_cookie : token_parameter;
        User user = userService.getByToken(response, token);
        UserContext.setUser(user);
        return user;
    }

    private void returnResult(HttpServletResponse response, StatusEnum statusEnum) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        String jsonString = JSON.toJSONString(ResultVOUtil.error(statusEnum));
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(jsonString.getBytes("UTF-8"));
        outputStream.flush();
        outputStream.close();
    }
}
