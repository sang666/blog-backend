package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.User;
import com.sang.blog.commom.utils.Constants;
import com.sang.blog.commom.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service("permission")
public class PermissionService {

    @Autowired
    private UserService userService;

    /**
     * 判断是不是管理员
     * @return
     */
    public boolean admin(){
        //拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();


        String toToken = CookieUtils.getCookie(request, Constants.user.COOKIE_TOKEN_KEY);
        if (StringUtils.isEmpty(toToken)) {
            return false;
        }
        User checkUser = userService.checkUser(request, response);
        if (checkUser == null) {
            return false;
        }
        if (Constants.user.ROLE_ADMIN.equals(checkUser.getRoles())) {
            //管理员
            return true;
        }
        return false;
    }
}
