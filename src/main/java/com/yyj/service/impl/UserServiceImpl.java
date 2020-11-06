package com.yyj.service.impl;

import com.yyj.access.UserContext;
import com.yyj.dao.UserDao;
import com.yyj.enums.StatusEnum;
import com.yyj.exception.GlobalException;
import com.yyj.pojo.User;
import com.yyj.redis.RedisService;
import com.yyj.redis.UserPrefix;
import com.yyj.service.UserService;
import com.yyj.utils.MD5Util;
import com.yyj.utils.UUIDUtil;
import com.yyj.vo.LoginVO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserServiceImpl implements UserService {
    public final static String COOKIE_TOKEN = "token";

    @Autowired
    private UserDao userDao;

    @Autowired
    private RedisService redisService;

    /**
     * 根据id查询用户
     *
     * @param id
     * @return
     */
    @Override
    public User findById(Long id) {
        //读缓存
        User user = redisService.get(UserPrefix.id, "" + id, User.class);
        if (user != null) {
            return user;
        }

        //缓存中无，从数据库读；数据库中有，则存入缓存，并返回
        user = userDao.findById(id);
        if (user != null) {
            redisService.set(UserPrefix.id, "" + id, user);
        }
        return user;
    }

    /**
     * 更新用户密码
     *
     * @param token
     * @param id
     * @param formPass
     * @return
     */
    @Override
    public Boolean updatePassword(String token, Long id, String formPass) {
        User user = findById(id);
        if(user==null){
            throw new GlobalException(StatusEnum.MOBILE_NOT_EXIST);
        }

        //更新数据库
        User update=new User();
        update.setId(id);
        update.setPassword(MD5Util.formPassToDBPass(formPass,user.getSalt()));
        userDao.updatePassword(update);

        //更新缓存
        redisService.delete(UserPrefix.id,""+id);
        user.setPassword(update.getPassword());
        redisService.set(UserPrefix.id,""+id,user);

        return true;
    }

    /**
     * 用户登录
     *
     * @param response
     * @param loginVO
     */
    @Override
    public String login(HttpServletResponse response, LoginVO loginVO) {
        if (loginVO == null) {
            throw new GlobalException(StatusEnum.SERVER_ERROR);
        }
        String mobile = loginVO.getMobile();
        String password = loginVO.getPassword();
        //验证手机号是否存在
        User user = userDao.findById(Long.parseLong(mobile));
        if (user == null) {
            throw new GlobalException(StatusEnum.MOBILE_NOT_EXIST);
        }
        //验证密码
        String dbPassword = user.getPassword();
        String salt = user.getSalt();
        //        System.out.println("salt="+salt);
        String calPass = MD5Util.formPassToDBPass(password, salt);
        //        System.out.println("calPass="+calPass);
        //        System.out.println("dbPassword="+dbPassword);
        if (!calPass.equals(dbPassword)) {
            throw new GlobalException(StatusEnum.PASSWORD_ERROR);
        }

        String token = UUIDUtil.uuid();
        extendValidity(response, token, user);
        return token;
    }

    /**
     * 根据token从redis中获取user的json字符串，再转化成User对象
     *
     * @param response
     * @param token
     * @return
     */
    @Override
    public User getByToken(HttpServletResponse response, String token) {
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        User user = redisService.get(UserPrefix.token, token, User.class);
        if (user != null) {
            extendValidity(response, token, user);
        }
        return user;
    }

    /**
     * 延长有效期，登录后或者通过token从redis成功取得user后都需要延长
     *
     * @param response
     * @param token
     * @param user
     */
    private void extendValidity(HttpServletResponse response, String token, User user) {
        redisService.set(UserPrefix.token, token, user);
        Cookie cookie = new Cookie(COOKIE_TOKEN, token);
        cookie.setMaxAge(UserPrefix.token.getExpireSeconds());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
