package com.yyj.controller;

import com.yyj.service.UserService;
import com.yyj.utils.ResultVOUtil;
import com.yyj.vo.LoginVO;
import com.yyj.vo.ResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/login")
@Slf4j
public class LoginController {
    @Autowired
    private UserService userService;

    @GetMapping("/to_login")
    public String toLogin() {
        return "login";
    }

    @PostMapping("/do_login")
    @ResponseBody
    public ResultVO doLogin(HttpServletResponse response, @Valid LoginVO loginVO) {
        log.info(loginVO.toString());
        String token=userService.login(response, loginVO);
        return ResultVOUtil.success(token);
    }
}
