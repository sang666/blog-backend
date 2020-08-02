package com.sang.blog.biz.controller.admin;


import com.sang.blog.commom.result.Result;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@RestController
@RequestMapping("/admin/web_site_info")
public class WebSiteInfoController {


    /**
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Result getWebSiteInfo(@PathVariable String id) {

        return Result.ok();
    }

    /**
     * @return
     */
    @GetMapping("/title")
    public Result getWebSiteTitle() {

        return Result.ok();
    }

    /**
     * @param title
     * @return
     */
    @PutMapping("/title/{title}")
    public Result updateWebSiteTitle(@PathVariable String title) {

        return Result.ok();
    }

    /**
     * @return
     */
    @GetMapping("/seo")
    public Result getSeoInfo() {

        return Result.ok();
    }

    /**
     * @param keywords
     * @param description
     * @return
     */
    @PutMapping("/seo")
    public Result putSeoInfo(@RequestParam String keywords,
                             @RequestParam String description) {

        return Result.ok();
    }


    /**
     * @return
     */
    @GetMapping("/view_count")
    public Result getWebSiteCount() {

        return Result.ok();
    }
}
