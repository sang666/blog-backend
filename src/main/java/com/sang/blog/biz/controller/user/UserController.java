package com.sang.blog.biz.controller.user;

import com.sang.blog.biz.entity.User;
import com.sang.blog.biz.service.UserService;
import com.sang.blog.commom.result.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{current}/{limit}")
    public Result listUserInfo(HttpServletRequest request,
                               HttpServletResponse response,
                               @PathVariable long current,
                               @PathVariable long limit) {
        return userService.listUserInfo(request,response,current,limit);
    }


    /**
     *需要管理员权限
     *
     * @param id
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{userId}")
    public Result deleteUserInfo(HttpServletRequest request,
                                 HttpServletResponse response,
                                 @PathVariable("userId") String id) {
        //判断当前的用户是谁，更据用户角色是否可以删除
        //TODO：通过注解的方式来控制权限
        return userService.deleteById(request,response,id);


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
     * 更新密码
     * 修改密码
     * 普通做法：通过旧密码来对比
     *
     * <p>
     * 既可以找回密码也可以修改密码
     * 发送验证码到邮箱手机--->判断验证码是否正确来判断
     * 发送邮箱所注册的账号是否属于你
     * <p>
     *
     * 步骤：
     * 1.用户填写邮箱
     * 2.用户获取验证码type (forget)
     * 3.填写验证码
     * 4.用户填写新的密码
     * 5.提交数据
     *
     * 数据包括
     * 1.邮箱和新密码
     * 2.验证吗
     *
     * 如果验证码正确，所用邮箱注册的账号就是你的，可以修改密码
     *
     * @param verifyCode
     * @param user
     * @return
     */
    @PutMapping("/password/{verifyCode}")
    public Result updatePassword(@PathVariable("verifyCode") String verifyCode, @RequestBody User user){

        return userService.updatePassword(verifyCode,user);
    }


    /**
     * 1.必须已经登录了
     * 2。新的邮箱已经注册了
     *
     * 用户的步骤：
     * 1.已经登陆
     * 2.输入新的邮箱地址
     * 3.获取验证码
     * 4.输入验证码
     * 5.提交数据
     *
     * 需要提交的数据
     * 1.新的邮箱地址
     * 2验证阿门
     * 3.提的信息可以从token里那
     * @return
     */
    @PutMapping("/email")
    public Result updateEmail(HttpServletRequest request,HttpServletResponse response,
                              @RequestParam("email") String email,
                              @RequestParam("verify_code") String verifyCode){

        return userService.updateEmail(request,response,email,verifyCode);
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
    @GetMapping("/user_info/{userId}")
    public Result getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }

    /**
     * @param user
     * @return
     */
    @ApiOperation(value = "注册")
    @PostMapping("/join_in")
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
    @PostMapping("/login/{captcha}/{captcha_key}")
    public Result login(@PathVariable("captcha_key") String captcha_key,
                        @PathVariable("captcha") String captcha,
                        @RequestBody User user,
                        HttpServletRequest request,
                        HttpServletResponse response) {
        return userService.doLogin(captcha_key, captcha, user, request, response);
    }


    /**
     * 拿到tokenkey
     * 删除redis里的token
     * 删除mysqk里的refreshtiken
     * 删除cookie里的tokenkey
     * @return
     */
    @GetMapping("/logout")
    public Result logout(HttpServletRequest request,HttpServletResponse response){

        return userService.logout(request,response);

    }

}