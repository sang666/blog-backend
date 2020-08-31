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

    Result doLogin(String captcha_key, String captcha, User user, HttpServletRequest request, HttpServletResponse response, String from);

    User checkUser(HttpServletRequest request, HttpServletResponse response);



    Result getUserInfo(String userId);

    Result updateByUserId(String id, User user, HttpServletRequest request, HttpServletResponse response);

    Result checkEmail(String email);

    Result checkUserName(String userName);

    Result deleteById(HttpServletRequest request, HttpServletResponse response, String id);

    Result listUserInfo(HttpServletRequest request, HttpServletResponse response, long current, long limit, String userName, String email);

    Result updatePassword(String verifyCode, User user);

    Result updateEmail(HttpServletRequest request, HttpServletResponse response, String email, String verifyCode);

    Result logout(HttpServletRequest request, HttpServletResponse response);

    Result getPcLoginQrCodeInfo();

    Result parseToken(HttpServletRequest request, HttpServletResponse response);

    Result resetPassword(String userId, String password);

    Result resetPasswordByEmail(String email, String email_code, String captcha_code, String captcha_key);
}
