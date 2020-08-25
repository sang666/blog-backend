package com.sang.blog.biz.controller.portal;


import com.sang.blog.biz.service.*;
import com.sang.blog.commom.result.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private UserService userService;

    /**
     * 门户获得分类列表
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/Categories/{current}/{limit}")
    public Result getCategories(@PathVariable("current") long current,
                                @PathVariable("limit") long limit) {

        return categoriesService.listCategory();
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


    /**
     * 统计访问页，每个页面都统计一次，pv，page view
     * 直接增加一个访问量，可以刷量
     * 。。根据ip进行一些过滤，可以集成一个第三方的统计工具，
     * 统计信息，通过redis来统计，数据也会保存再mysql里
     * 不会每次更新到mysql里，当用户去获取访问量的时候，会更新一次
     *
     * redis时机：每个页面访问的时候，如果不在mysql中读取数据，写道redi s中
     * 如果就自增
     *
     * mysql的时机，用户读取网站总访问量的时候，我们就读取redis的
     * 如果redis额米有，就读取mysql写道redis里
     *
     */
    @PutMapping("/view_count")
    public void updateViewCount(){
        webSiteInfoService.updateViewCount();

        return;
    }

    @GetMapping("/list")
    public Result listCategory() {

        return categoriesService.listCategory();

    }

    @ApiOperation(value = "获取用户信息" )
    @GetMapping("/user_info/{userId}")
    public Result getUserInfo(@PathVariable("userId") String userId) {
        return userService.getUserInfo(userId);
    }


}
