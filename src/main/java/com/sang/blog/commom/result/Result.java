package com.sang.blog.commom.result;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

//返回结果
@Data
public class Result {

    private Boolean success;
    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<String, Object>();

    //构造方法私有化

    private Result() {
    }
    //成功静态方法

    //链式编程

    public static Result ok() {
        Result result = new Result();
        result.setSuccess(true);
        result.setCode(ResultCode.SUCCESS);
        result.setMessage("成功");
        return result;

    }

    //失败静态方法

    public static Result err() {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR);
        result.setMessage("失败");
        return result;
    }

    public static Result accountNotLogin() {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.ACCOUNT_NOT_LOGIN);
        result.setMessage("账号未登录");
        return result;
    }

    public static Result PERMISSION_FORBID() {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.PERMISSION_FORBID);
        result.setMessage("权限不足");
        return result;
    }



    public Result success(Boolean success) {
        this.setSuccess(success);
        return this;
    }

    public Result message(String message) {
        this.setMessage(message);
        return this;
    }

    public Result code(Integer code) {
        this.setCode(code);
        return this;
    }

    public Result data(String key, Object value) {
        this.data.put(key, value);
        return this;
    }


    public Result data(Map<String, Object> map) {
        this.setData(map);
        return this;
    }
}