package com.sang.blog.biz.controller.user;

import com.sang.blog.biz.entity.User;
import com.sang.blog.biz.service.UserService;
import com.sang.blog.commom.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;


/**
 * <p>
 * 前端控制器
 * 用户注册
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Slf4j
@RestController
@RequestMapping("/biz/user")
public class UserController {

    @Resource
    UserService userService;

    /**
     * @param user
     * @return
     */

    @ApiOperation(value = "管理员注册")
    @PostMapping("/admin_account")
    public Result initManagerAccount(@RequestBody User user, HttpServletRequest request) {


        return userService.initManagerAccount(user, request);

    }

    /**
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/{current}/{limit}")
    public Result listUserInfo(@PathVariable long current, @PathVariable long limit) {
        return Result.ok();
    }


    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteUserInfo(@PathVariable String id) {
        return Result.ok();


    }


    /**
     * 允许用户修改信息
     * 用户名和email唯一
     * 头像签名密码
     * 密码和email单独修改
     *
     * @param id
     * @param user
     * @return
     */
    @PutMapping("/{userId}")
    public Result updateUserInfo(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @PathVariable("userId") String id, @RequestBody User user) {

        return userService.updateByUserId(id,user,request,response);
    }

    /**
     * 检查email是否唯一
     * @param email
     * @return
     */
    @GetMapping("/email")
    public Result checkEmail(@RequestParam("email") String email){

        return userService.checkEmail(email);
    }

    /**
     * 检查用户名是否唯一
     * @param userName
     * @return
     */
    @GetMapping("/userName")
    public Result checkUserName(@RequestParam("userName") String userName){

        return userService.checkUserName(userName);
    }




    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @ApiOperation(value = "获取用户信息" )
    @GetMapping("/{userId}")
    public Result getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * @param user
     * @return
     */
    @ApiOperation(value = "注册")
    @PostMapping
    public Result register(@RequestBody User user,
                           @RequestParam("email_code") String emailCode,
                           @RequestParam("captcha_code") String captchaCode,
                           @RequestParam("captcha_key") String captchaKey,
                           HttpServletRequest request) {
        //第一步，检查当前用户名是否注册
        //第二部，检查邮箱格式是否正确
        //第三步，检查邮箱是否正确
        //第四步，检查邮箱验证码是否正确
        //第五步，检查图灵验证是否正确
        //对密码进行加密
        //补全数据(双ip，角色，头像，创建时间，更新时间)
        //存到数据库中
        //返回结果

        return userService.register(user, emailCode, captchaCode, captchaKey, request);
    }


    /**
     * 时长为十分钟
     * 获取图灵验证码
     *
     * @return
     */
    @ApiOperation(value = "获取人类验证码")
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletResponse response, @RequestParam("captcha_key") String captchakey) throws IOException, FontFormatException {

        try {
            userService.createCaptcha(response, captchakey);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * 使用场景：注册，找回密码,修改邮箱(会输入新的邮箱)
     * 发送邮件email
     *
     * @param emailAddress
     * @return
     */
    @ApiOperation(value = "发送邮件")
    @GetMapping("/verify_code")
    public Result sendVerifycode(HttpServletRequest request,
                                 @RequestParam String type,
                                 @RequestParam("email") String emailAddress) {
        log.info("email==>" + emailAddress);
        return userService.sendEmail(type, request, emailAddress);
    }

    /**
     * 登录sign-up
     * 需要提交的数据
     * 用户账号-可以昵称-可以邮箱-->做了唯一处理
     * 密码
     * 图灵验证码
     * 涂料验证码的key
     *
     * @param captcha     图灵验证码
     * @param user        用户信息(账号核密码)
     * @param captcha_key 验证码的key
     * @return
     */
    @ApiOperation(value = "登录方法")
    @PostMapping("/{captcha}/{captcha_key}")
    public Result login(@PathVariable("captcha_key") String captcha_key,
                        @PathVariable("captcha") String captcha,
                        @RequestBody User user,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        return userService.doLogin(captcha_key, captcha, user, request, response);
    }

}