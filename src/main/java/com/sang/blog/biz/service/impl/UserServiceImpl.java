package com.sang.blog.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.sang.blog.biz.entity.RefreshToken;
import com.sang.blog.biz.entity.Settings;
import com.sang.blog.biz.entity.User;
import com.sang.blog.biz.mapper.RefreshTokenMapper;
import com.sang.blog.biz.mapper.SettingsMapper;
import com.sang.blog.biz.mapper.UserMapper;
import com.sang.blog.biz.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sang.blog.commom.result.Result;
import com.sang.blog.commom.utils.*;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.sang.blog.commom.utils.Constants.user.COOKIE_TOKEN_KEY;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {


    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;
    @Resource
    private UserMapper userMapper;
    @Autowired
    private SettingsMapper settingsMapper;
    @Autowired
    private Random random;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RefreshTokenMapper refreshTokenMapper;
    @Autowired
    private Gson gson;

    /**
     * 管理员注册
     *
     * @param user
     * @param request
     * @return
     */
    @Override
    public Result initManagerAccount(User user, HttpServletRequest request) {

        //判断状态
        Settings managerAcountState = settingsMapper.findOneByKey(Constants.settings.MANAGER_ACCOUNT_INIT_STATE);
        if (managerAcountState != null) {
            return Result.err().message("管理员账户已经初始化了");

        }
        //检察数据

        if (StringUtils.isEmpty(user.getUserName())) {
            return Result.err().message("用户名不能为空");

        }

        if (StringUtils.isEmpty(user.getPassword())) {
            return Result.err().message("密码不能为空");

        }

        if (StringUtils.isEmpty(user.getEmail())) {
            return Result.err().message("邮箱不能为空");

        }
        //补充数据
        user.setRoles(Constants.user.ROLE_ADMIN);
        user.setAvatar(Constants.user.DEFAULT_AVATER);
        user.setState(Constants.user.DEFAULT_STATE);
        //获取id
        String remoteAddr = request.getRemoteAddr();
        String localAddr = request.getLocalAddr();
        user.setLoginIp(remoteAddr);
        user.setRegIp(remoteAddr);
        //对密码进行加密
        String password = user.getPassword();
        //加密
        String encode = bCryptPasswordEncoder.encode(password);
        user.setPassword(encode);


        log.info("remoteAddr==>" + remoteAddr);
        log.info("localAddr==>" + localAddr);

        //插入用户信息
        userMapper.insert(user);

        //
        Settings settings = new Settings();
        settings.setValue("1");
        settings.setKey(Constants.settings.MANAGER_ACCOUNT_INIT_STATE);
        settingsMapper.insert(settings);
        return Result.ok().message("成功");
    }

    /**
     * 存不同的验证码样式
     */
    public static final int[] captcha_font_types = {
            Captcha.FONT_1,
            Captcha.FONT_2,
            Captcha.FONT_3,
            Captcha.FONT_4,
            Captcha.FONT_5,
            Captcha.FONT_6,
            Captcha.FONT_7,
            Captcha.FONT_8,
            Captcha.FONT_9,
            Captcha.FONT_10
    };

    /**
     * 获取验证码
     *
     * @param response
     * @param captchakey
     * @throws IOException
     * @throws FontFormatException
     */
    @Override
    public void createCaptcha(HttpServletResponse response, String captchakey) throws IOException, FontFormatException {
        if (StringUtils.isEmpty(captchakey) || captchakey.length() < 13) {
            return;
        }
        long key;
        try {
            key = Long.parseLong(captchakey);
        } catch (Exception e) {
            return;
        }
        // 设置请求头为输出图片类型
        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        int captchaType = random.nextInt(3);
        Captcha targetCaptcha;

        if (captchaType == 0) {
            // 三个参数分别为宽、高、位数
            targetCaptcha = new SpecCaptcha(200, 60, 5);
        } else if (captchaType == 1) {
            //gif类型
            targetCaptcha = new GifCaptcha(200, 60);
        } else {
            //算术类型
            targetCaptcha = new ArithmeticCaptcha(200, 60);
            targetCaptcha.setLen(2);
        }

        int index = random.nextInt(captcha_font_types.length);
        log.info("captcha font type index==>" + index);
        targetCaptcha.setFont(captcha_font_types[index]);
        targetCaptcha.setCharType(Captcha.TYPE_DEFAULT);
        String content = targetCaptcha.text().toLowerCase();
        log.info("captcha content ==>" + content);
        //存到redis中
        redisUtils.set(Constants.user.KEY_COPTCHA_CONTENT + key, content, 60 * 10);
        targetCaptcha.out(response.getOutputStream());
    }

    /**
     * 发送邮箱验证码
     *
     * @param request
     * @param emailAddress
     * @return
     */
    @Override
    public Result sendEmail(String type, HttpServletRequest request, String emailAddress) {
        if (emailAddress == null) {
            return Result.err().message("邮箱地址不可以为空");
        }
        //根据类型查询邮箱是否存在
        if ("register".equals(type) || "update".equals(type)) {
            QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
            userQueryWrapper1.eq("email", emailAddress);
            User userByEmail = userMapper.selectOne(userQueryWrapper1);
            if (userByEmail != null) {
                return Result.err().message("该邮箱已注册");
            }
        } else if ("forget".equals(type)) {
            QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
            userQueryWrapper1.eq("email", emailAddress);
            User userByEmail = userMapper.selectOne(userQueryWrapper1);
            if (userByEmail == null) {
                return Result.err().message("该邮箱并未注册");
            }
        }

        //1.防止暴力发送，同一个邮箱间隔不能超过30秒，同一个ip最多只能发10次，如果是短信最多只能发3次
        String remoteaddr = request.getRemoteAddr();
        log.info("senemail==>ip==>" + remoteaddr);
        if (remoteaddr != null) {
            remoteaddr = remoteaddr.replaceAll(":", "_");
        }
        log.info("senemail==>ip==>" + remoteaddr);
        //拿出来，如果有，哪有过了
        log.info("Constants.user.KEY_EMAIL_SEND_IP + remoteaddr===>" + Constants.user.KEY_EMAIL_SEND_IP + remoteaddr);
        Integer ipSendTime = (Integer) redisUtils.get(Constants.user.KEY_EMAIL_SEND_IP + remoteaddr);
        if (ipSendTime != null && ipSendTime > 10) {
            return Result.err().message("请不要发送的太频繁");
        }

        Object addressSendTime = redisUtils.get(Constants.user.KEY_EMAIL_SEND_ADDRESS + emailAddress);
        if (addressSendTime != null) {
            return Result.err().message("请不要发送的太频繁");
        }

        //2.检查邮箱地址是否正确
        boolean emailAddressOk = TextUtils.isEmailAddressOk(emailAddress);
        if (!emailAddressOk) {
            return Result.err().message("邮箱地址不正确");
        }
        //0-99999
        int code = random.nextInt(99999);
        if (code < 10000) {
            code += 10000;
        }
        log.info("sendEmail==>" + code);
        //3.发送验证码,六位数:10000-99999
        try {
            taskService.sendEmailVerifyCode(String.valueOf(code), emailAddress);
        } catch (Exception e) {
            return Result.err().message("发送失败请稍后重试");
        }
        //4做记录
        //发送记录，code
        if (ipSendTime == null) {
            ipSendTime = 0;
        }
        ipSendTime++;
        //一个小时有效期
        redisUtils.set(Constants.user.KEY_EMAIL_SEND_IP + remoteaddr, ipSendTime, 60 * 60);
        redisUtils.set(Constants.user.KEY_EMAIL_SEND_ADDRESS + emailAddress, "true", 30);
        //保存code,10分钟内有效
        redisUtils.set(Constants.user.KEY_EMAIL_CODE_CONTENT + emailAddress, String.valueOf(code), 60 * 10);
        log.info(Constants.user.KEY_EMAIL_CODE_CONTENT + emailAddress);
        return Result.ok().message("验证码发送成功");
    }


    /**
     * 注册实现
     *
     * @param user
     * @return
     */
    @Override
    public Result register(User user, String emailCode, String captchaCode, String captchaKey, HttpServletRequest request) {
        //第一步，检查当前用户名是否注册
        String userName = user.getUserName();
        if (StringUtils.isEmpty(userName)) {
            return Result.err().message("用户名不能为空");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_name", userName);
        User userByName = userMapper.selectOne(userQueryWrapper);
        if (userByName != null) {
            return Result.err().message("该用户已注册");
        }
        //第二部，检查邮箱格式是否正确
        String email = user.getEmail();
        if (StringUtils.isEmpty(email)) {
            return Result.err().message("邮箱不能为空");
        }
        if (!TextUtils.isEmailAddressOk(email)) {
            return Result.err().message("邮箱格式不正确");
        }
        //第三步，检查邮箱是否被注册
        QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
        userQueryWrapper1.eq("email", email);
        User userByEmail = userMapper.selectOne(userQueryWrapper1);
        if (userByEmail != null) {
            return Result.err().message("该邮箱已注册");
        }
        //第四步，检查邮箱验证码是否正确
        String emailVerifyCode = (String) redisUtils.get(Constants.user.KEY_EMAIL_CODE_CONTENT + email);
        if (StringUtils.isEmpty(emailVerifyCode)) {
            log.info(user.getEmail());
            log.info("emailKey----->"+Constants.user.KEY_EMAIL_CODE_CONTENT + email);
            log.info("邮箱验证码-----》"+emailVerifyCode);
            return Result.err().message("邮箱验证码已过期");
        }
        if (!emailVerifyCode.equals(emailCode)) {
            return Result.err().message("邮箱验证码不正确");
        } else {
            redisUtils.del(Constants.user.KEY_EMAIL_CODE_CONTENT + email);
        }
        //第五步，检查图灵验证是否正确
        String captchaVerifyCode = (String) redisUtils.get(Constants.user.KEY_COPTCHA_CONTENT + captchaKey);
        if (StringUtils.isEmpty(captchaVerifyCode)) {
            return Result.err().message("人类验证码已过期");
        }
        if (!captchaVerifyCode.equals(captchaCode)) {
            return Result.err().message("人类验证码不正确");

        } else {
            redisUtils.del(Constants.user.KEY_COPTCHA_CONTENT + captchaKey);
        }
        //达到可以注册的条件
        String password = user.getPassword();
        if (StringUtils.isEmpty(password)) {
            return Result.err().message("密码不可以为空");
        }
        //对密码进行加密
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        //补全数据(双ip，角色，头像，创建时间，更新时间)
        String ipAddress = request.getRemoteAddr();
        user.setRegIp(ipAddress);
        user.setLoginIp(ipAddress);
        user.setAvatar(Constants.user.DEFAULT_AVATER);
        user.setRoles(Constants.user.ROLE_NORMAL);
        //存到数据库中
        userMapper.insert(user);
        //返回结果
        return Result.ok().code(20002).message("注册成功");

    }

    /**
     * 用户登录
     *
     * @param captcha_key
     * @param captcha
     * @param user
     * @return
     */
    @Override
    public Result doLogin(String captcha_key,
                          String captcha,
                          User user,
                          HttpServletRequest request,
                          HttpServletResponse response) {

        String captchaValue = (String) redisUtils.get(Constants.user.KEY_COPTCHA_CONTENT + captcha_key);
        if (!captcha.equals(captchaValue)) {
            return Result.err().message("人类验证码不正确");
        }

        //验证成功，使用之后删除redis里的验证码
        redisUtils.del(Constants.user.KEY_COPTCHA_CONTENT + captcha_key);
        //有可能是邮箱，也有可能是用户名
        String userName = user.getUserName();
        if (StringUtils.isEmpty(userName)) {
            return Result.err().message("用户名不能为空");
        }
        String password = user.getPassword();
        if (StringUtils.isEmpty(password)) {
            return Result.err().message("密码不能为空");
        }
        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq("user_name", userName);
        User userFromDb = userMapper.selectOne(userQueryWrapper);

        QueryWrapper<User> userQueryWrapper1 = new QueryWrapper<>();
        userQueryWrapper1.eq("email", userName);
        //User userByEmail = userMapper.selectOne(userQueryWrapper1);

        if (userFromDb == null) {
            userFromDb = userMapper.selectOne(userQueryWrapper1);
        }
        if (userFromDb == null) {
            return Result.err().message("用户名或密码不正确");
        }
        //用户存在
        //对比密码
        boolean matches = bCryptPasswordEncoder.matches(password, userFromDb.getPassword());
        if (!matches) {
            return Result.err().message("用户名或密码不正确");
        }
        //密码是正确的
        //判断用户状态,如果是非正常则返回结果
        Integer i = 0;
        log.info("state===>" + userFromDb.getState());
        if (!i.equals(userFromDb.getState())) {
            return Result.err().message("当前帐号已被禁止");
        }
        createToken(response, userFromDb);


        return Result.ok().message("登陆成功");

    }


    /**
     * 本质，检查用户是否有登录，如果登录了，就返回用户信息
     *
     * @param request
     * @param response
     * @return
     */
    @Override
    public User checkUser(HttpServletRequest request, HttpServletResponse response) {

        //拿到cookie
        String tokenKey = CookieUtils.getCookie(request, COOKIE_TOKEN_KEY);
        User user = parseByTokenKey(tokenKey);
        if (user == null) {
            //说明解析出错
            //去mysql数据库查询refreshToken
            QueryWrapper<RefreshToken> refreshTokenQueryWrapper = new QueryWrapper<>();
            refreshTokenQueryWrapper.eq("token_key", tokenKey);
            RefreshToken refreshToken = refreshTokenMapper.selectOne(refreshTokenQueryWrapper);
            //如果不存在，就是没登录,提示用户登录
            if (refreshToken == null) {
                return null;
            }
            //如果存在，就解析
            try {

                JwtUtil.parseJWT(refreshToken.getRefreshToken());
                //如果有效创建新的token和新的refreshToken
                String userId = refreshToken.getUserId();
                User userFromDb = userMapper.selectById(userId);
                //删掉refreshToken的记录

                String newTokenKey = createToken(response, userFromDb);

                //返回token
                return parseByTokenKey(newTokenKey);

            } catch (Exception e1) {
                //如果refreshToken过期了,就当访问美誉登录，提示用户登录
                return null;
            }
        }
        return user;
    }

    /**
     *
     * @param userId
     * @return
     */
    @Override
    public Result getUserInfo(String userId) {
        //从数据库里获取，
        User user = userMapper.selectById(userId);
        //判断结果
        if (user == null) {
            //如果不存在则返回不存在
            return Result.err().message("用户不存在");
        }
        //如果存在就复制对象，清空密码等保密信息
        String userJson = gson.toJson(user);
        User newUser = gson.fromJson(userJson, User.class);
        newUser.setPassword("");
        newUser.setEmail("");
        newUser.setRegIp("");
        newUser.setLoginIp("");
        //返回结果
        return Result.ok().message("获取成功").data("newUser",newUser);
    }

    /**
     * 更新用户信息
     * @param id
     * @param user
     * @param request
     * @param response
     * @return
     */
    @Override
    public Result updateByUserId(String id, User user, HttpServletRequest request, HttpServletResponse response) {

        //从token解析出来的user,为了校验权限，只有用户自己才可以修改自己的信息
        User userFromTokenKey = checkUser(request, response);
        //检查是否登录
        if (userFromTokenKey ==null) {
            return Result.accountNotLogin();
        }
        User userFromDB = userMapper.selectById(userFromTokenKey.getId());
        //可以判断用户id是否一致，一直才可以修改
        if (!userFromDB.getId().equals(id)) {
            return Result.err().message("无权修改");
        }
        //用户名
        if (!StringUtils.isEmpty(user.getUserName())) {
            User selectByUserName = userMapper.selectByUserName(user.getUserName());
            if (selectByUserName != null) {
                return Result.err().message("该用户名已注册");
            }
            userFromDB.setUserName(user.getUserName());
        }
        //可以进行修改
        if (!StringUtils.isEmpty(user.getEmail())) {
            userFromDB.setEmail(user.getEmail());
        }
        //签名可以为空
        userFromDB.setSign(user.getSign());
        userMapper.updateById(userFromDB);


        //干掉redis里的token，下一次请求，需要解析token的，就会根据refreshToken重新创建一个
        String tokenKey = CookieUtils.getCookie(request, COOKIE_TOKEN_KEY);
        redisUtils.del(tokenKey);
        return Result.ok().message("用户信息修改成功");
    }

    @Override
    public Result checkEmail(String email) {
        User selectByEmail = userMapper.selectByEmail(email);

        return selectByEmail==null?Result.ok().message("该邮箱未注册") : Result.err().message("改邮箱已经被注册");
    }

    @Override
    public Result checkUserName(String userName) {

        User selectByUserName = userMapper.selectByUserName(userName);
        return selectByUserName==null?Result.ok().message("该用户名未注册") : Result.err().message("改用户已经被注册");
    }

    /**
     * 删除角色
     * 并不是真的删除而是修改状态
     * @param request
     * @param response
     * @param id
     * @return
     */
    @Override
    public Result deleteById(HttpServletRequest request, HttpServletResponse response, String id) {
        //可以操作了
        int deleteById = userMapper.deleteById(id);
        if (deleteById>0) {
            return Result.ok().message("删除成功");

        }Result.err().message("删除失败,用户不存在");

        return null;


    }

    /**
     * 需要管理员权限
     * @param request
     * @param response
     * @param current  当前页
     * @param limit 每页记录数
     * @return
     */
    @Override
    public Result listUserInfo(HttpServletRequest request, HttpServletResponse response, long current, long limit) {


        //可以获取用户列表
        //创建page对象
        Page<User> page = new Page<>(current,limit);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //查询部分字段，不查密码
        wrapper.select("id","user_name","roles","avatar","email",
                "sign","state","reg_ip","login_ip","create_time","update_time");

        userMapper.selectPage(page,wrapper);
        long total = page.getTotal();//总记录数
        List<User> records = page.getRecords();

        return Result.ok().message("获取用户列表成功").data("total",total).data("rows",records);
    }

    /**
     * 更新密码
     *
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
    @Override
    public Result updatePassword(String verifyCode, User user) {
        //检查是否有邮箱填写
        String email = user.getEmail();
        if (StringUtils.isEmpty(email)) {
            return Result.err().message("邮箱验证码不能为空");
        }
        //根据邮箱去redis那验证码对比
        String redisVerifyCode = (String) redisUtils.get(Constants.user.KEY_EMAIL_CODE_CONTENT+email);
        if (redisVerifyCode == null || !redisVerifyCode.equals(verifyCode)) {
            return Result.err().message("验证码错误");
        }
        //如果一样
        redisUtils.del(Constants.user.KEY_EMAIL_CODE_CONTENT+email);
        int result = userMapper.updatePasswordByEmail(bCryptPasswordEncoder.encode(user.getPassword()), email);


        return result>0? Result.ok().message("密码修改成功"):Result.err().message("密码修改失败");
    }

    /**
     * 更新邮箱地址
     *
     * @param request
     * @param response
     * @param email
     * @param verifyCode
     * @return
     */
    @Override
    public Result updateEmail(HttpServletRequest request,
                              HttpServletResponse response,
                              String email, String verifyCode) {
        //确保已经登录
        User checkUser = checkUser(request, response);
        if (checkUser == null) {
            //没有登录
            return Result.accountNotLogin();
        }
        //2对比验证吗，确保新的邮箱是属于新的用户的
        String redisVerifyCode = (String) redisUtils.get(Constants.user.KEY_EMAIL_CODE_CONTENT + email);
        if (StringUtils.isEmpty(redisVerifyCode) || !redisVerifyCode.equals(verifyCode)) {
            return Result.err().message("验证码错误");
        }
        //可以修改了
        int i = userMapper.updateEmailById(email, checkUser.getId());
        return i>0? Result.ok().message("邮箱修改成功"):Result.err().message("邮箱修改失败");
    }

    /**
     * 退出登录
     * @param request
     * @param response
     * @return
     */
    @Override
    public Result logout(HttpServletRequest request, HttpServletResponse response) {
        String tokenKey = CookieUtils.getCookie(request, COOKIE_TOKEN_KEY);
        if (StringUtils.isEmpty(tokenKey)) {
            return Result.accountNotLogin();
        }
        //删除redis里的token
        redisUtils.del(Constants.user.KEY_TOKEN+tokenKey);
        //删除mysql里的refreshToken
        refreshTokenMapper.deleteByTokenKey(tokenKey);
        //删除cookies里的token_key
        CookieUtils.deleteCookie(response, COOKIE_TOKEN_KEY);
        return Result.ok().message("退出登陆成功");
    }


    /**
     * 抽取的方法
     *
     * @param response
     * @param userFromDb
     * @return tokenKey
     */
    private String createToken(HttpServletResponse response, User userFromDb) {
        QueryWrapper<RefreshToken> refreshTokenQueryWrapper = new QueryWrapper<>();
        refreshTokenQueryWrapper.eq("user_id", userFromDb.getId());
        refreshTokenMapper.delete(refreshTokenQueryWrapper);
        //生成token
        Map<String, Object> claims = claimsUtils.user2Claims(userFromDb);
        //token默认有效为两个小时
        String token = JwtUtil.createToken(claims);
        //返回token的md5值，token会保存在redis里
        //如果前端访问的时候携带md5key，从redis中获取即可
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        //保存token到redis中，有效期为2个小时,key是tokenKey
        redisUtils.set(Constants.user.KEY_TOKEN + tokenKey, token, Constants.TimeValueInMillions.HOUR_2);
        //把token写到cookies里
        //这个要动态获取，可以从request中获取，后面后工具类
        CookieUtils.setUpCookie(response, Constants.user.COOKIE_TOKEN_KEY, tokenKey);
        //生成refreshToken

        String refreshTokenValue = JwtUtil.createRefreshToken(userFromDb.getId(), Constants.TimeValueInMillions.MONTH);
        //:保存到数据库里
        //table:   refreshToken,tokenKey,userId,createTime，updateTime
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setRefreshToken(refreshTokenValue);
        refreshToken.setUserId(userFromDb.getId());
        refreshToken.setTokenKey(tokenKey);

        refreshTokenMapper.insert(refreshToken);
        return tokenKey;
    }


    /**
     * 提取出来的方法
     * 解析token
     *
     * @param tokenKey
     * @return
     */
    private User parseByTokenKey(String tokenKey) {
        //拿到cookie
        String token = (String) redisUtils.get(Constants.user.KEY_TOKEN + tokenKey);
        if (token != null) {
            try {
                Claims claims = JwtUtil.parseJWT(token);
                return claimsUtils.claims2User(claims);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

}
