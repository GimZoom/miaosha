package com.yyj.service;

import com.yyj.pojo.User;
import com.yyj.vo.LoginVO;

import javax.servlet.http.HttpServletResponse;

public interface UserService {
    User findById(Long id);

    Boolean updatePassword(String token, Long id, String formPass);

    String login(HttpServletResponse response, LoginVO loginVO);

    User getByToken(HttpServletResponse response, String token);
}
