package com.sang.blog.biz.controller.portal;


import com.sang.blog.biz.service.CategoriesService;
import com.sang.blog.biz.service.FriendsService;
import com.sang.blog.biz.service.LooperService;
import com.sang.blog.biz.service.WebSiteInfoService;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal/web_site_info")
public class WebSiteInfoPortalController {


    @Autowired
    private CategoriesService categoriesService;

    @Autowired
    private FriendsService friendsService;

    @Autowired
    private LooperService looperService;

    @Autowired
    private WebSiteInfoService webSiteInfoService;

    /**
     * 门户获得分类列表
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/Categories/{current}/{limit}")
    public Result getCategories(@PathVariable("current") long current,
                                @PathVariable("limit") long limit) {

        return categoriesService.listCategory(current,limit);
    }

    /**
     * 门户title
     * @return
     */
    @GetMapping("/title")
    public Result getWebSiteTitle() {

        return webSiteInfoService.getWebSiteTitle();
    }

    /**
     * 门户网站view——count
     * @return
     */
    @GetMapping("/view_count")
    public Result getWebSiteViewCount() {

        return webSiteInfoService.getWebSiteCount();
    }

    /**
     * 门户seo——info
     * @return
     */
    @GetMapping("/seo")
    public Result getWebsizeSeoInfo() {

        return webSiteInfoService.getSeoInfo();
    }

    /**
     * 门户轮播图
     * @return
     */
    @GetMapping("/loop")
    public Result getLoops() {

        return looperService.listLooper();
    }

    /**
     * 友链列表
     * @return
     */
    @GetMapping("/friend_link")
    public Result getLink() {

        return friendsService.listFriendLind();
    }


}
