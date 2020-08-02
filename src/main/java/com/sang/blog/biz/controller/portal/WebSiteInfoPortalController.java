package com.sang.blog.biz.controller.portal;


import com.sang.blog.commom.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/potral/web_site_info")
public class WebSiteInfoPortalController {


    @GetMapping("/Categories")
    public Result getCategories() {

        return Result.ok();
    }

    @GetMapping("/title")
    public Result getWebSiteTitle() {

        return Result.ok();
    }

    @GetMapping("/view_count")
    public Result getWebSiteViewCount() {

        return Result.ok();
    }

    @GetMapping("/seo")
    public Result getWebsizeSeoInfo() {

        return Result.ok();
    }

    @GetMapping("/loop")
    public Result getLoops() {

        return Result.ok();
    }

    @GetMapping("/friend_link")
    public Result getLink() {

        return Result.ok();
    }


}
