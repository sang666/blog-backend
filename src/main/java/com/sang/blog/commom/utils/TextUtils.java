package com.sang.blog.commom.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextUtils {

    public static final String regex = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 邮箱正则校验
     *
     * @param emailAddress
     * @return
     */
    public static boolean isEmailAddressOk(String emailAddress) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(emailAddress);
        return m.matches();

    }
}
