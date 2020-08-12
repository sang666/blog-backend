package com.sang.blog.biz.controller;


import com.sang.blog.commom.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 错误码统一返回结果
 */
@RestController

public class ErrorPageController {
    /*@GetMapping("/404")
    public Result page404(){
        return Result.ERROR_404();

    }

    @GetMapping("/403")
    public Result page403(){
        return Result.ERROR_403();

    }

    @GetMapping("/504")
    public Result page504(){
        return Result.ERROR_504();

    }

    @GetMapping("/505")
    public Result page505(){
        return Result.ERROR_505();

    }*/

    @RequestMapping("/404")
    public Result page404() {
        return Result.ERROR_404();
    }

    @RequestMapping("/403")
    public Result page403() {
        return Result.ERROR_403();
    }

    @RequestMapping("/504")
    public Result page504() {
        return Result.ERROR_504();
    }

    @RequestMapping("/505")
    public Result page505() {
        return Result.ERROR_505();
    }


}
