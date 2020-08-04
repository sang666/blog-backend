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

    public static Result ERROR_403() {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR_403);
        result.setMessage("权限不足");
        return result;
    }
    public static Result ERROR_404() {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR_404);
        result.setMessage("页面丢失");
        return result;
    }
    public static Result ERROR_504() {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR_504);
        result.setMessage("系统繁忙");
        return result;
    }
    public static Result ERROR_505() {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.ERROR_505);
        result.setMessage("权限不足");
        return result;
    }
    public static Result PERMISSION_FORBID() {
        Result result = new Result();
        result.setSuccess(false);
        result.setCode(ResultCode.PERMISSION_DENIED);
        result.setMessage("请求错误，请检查所提交数据");
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