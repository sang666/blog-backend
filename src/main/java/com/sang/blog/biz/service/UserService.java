package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sang.blog.commom.result.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
public interface UserService extends IService<User> {

    Result initManagerAccount(User user, HttpServletRequest request);

    void createCaptcha(HttpServletResponse response, String captchakey) throws Exception;

    Result sendEmail(String type, HttpServletRequest request, String emailAddress);

    Result register(User user, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request);

    Result doLogin(String captcha_key, String captcha, User user, HttpServletRequest request, HttpServletResponse response);

    User checkUser(HttpServletRequest request, HttpServletResponse response);


    Result getUserInfo(String userId);

    Result updateByUserId(String id, User user, HttpServletRequest request, HttpServletResponse response);

    Result checkEmail(String email);

    Result checkUserName(String userName);
}