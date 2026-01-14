package com.from1115.crm.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.GifCaptcha;
import com.from1115.crm.common.Constants;
import com.from1115.crm.utils.WebScopeUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/captcha")
public class CaptchaController {

    @GetMapping("/getCaptcha.do")
    public void getCaptcha(HttpServletResponse response) throws IOException {
        //生成一个验证码
        GifCaptcha gifCaptcha = CaptchaUtil.createGifCaptcha(150,45,4);

        //获取生成的验证码
        String code = gifCaptcha.getCode();
        HttpSession session = WebScopeUtil.getSession();
        session.setAttribute(Constants.SESSION_USER_NAME,code);

        //将验证码以流的形式写出
        gifCaptcha.write(response.getOutputStream());
    }
}
