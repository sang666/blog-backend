package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.service.WebSiteInfoService;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@RestController
@RequestMapping("/admin/web_site_info")
public class WebSiteInfoController {


    @Autowired
    private WebSiteInfoService webSiteInfoService;


    /**
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @PreAuthorize("@permission.admin()")
    public Result getWebSiteInfo(@PathVariable("id") String id) {

        return webSiteInfoService.getWebSiteInfo(id);
    }

    /**
     * @return
     */
    @GetMapping("/title")
    @PreAuthorize("@permission.admin()")
    public Result getWebSiteTitle() {

        return webSiteInfoService.getWebSiteTitle();
    }

    /**
     * @param title
     * @return
     */
    @PutMapping("/title/{title}")
    @PreAuthorize("@permission.admin()")
    public Result updateWebSiteTitle(@PathVariable("title") String title) {

        return webSiteInfoService.putWebSiteTitle(title);
    }

    /**
     * @return
     */
    @GetMapping("/seo")
    @PreAuthorize("@permission.admin()")
    public Result getSeoInfo() {

        return webSiteInfoService.getSeoInfo( );
    }

    /**
     * @param keywords
     * @param description
     * @return
     */
    @PutMapping("/seo")
    @PreAuthorize("@permission.admin()")
    public Result putSeoInfo(@RequestParam String keywords,
                             @RequestParam String description) {

        return webSiteInfoService.putSeoInfo(keywords,description);
    }


    /**
     * @return
     */
    @GetMapping("/view_count")
    @PreAuthorize("@permission.admin()")
    public Result getWebSiteCount() {

        return webSiteInfoService.getWebSiteCount();
    }
}
