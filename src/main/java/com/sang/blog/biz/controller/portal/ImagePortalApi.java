package com.sang.blog.biz.controller.portal;


import com.sang.blog.biz.service.ImagesService;
import com.sang.blog.biz.service.LooperService;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/protal/image")
public class ImagePortalApi {

    @Autowired
    private ImagesService imagesService;

    @Autowired
    private LooperService looperService;

    /**
     * 门户获得文章图片
     * @param imageId
     * @return
     */
    @GetMapping("/{imageId}")
    public Result getImage(@PathVariable("imageId") String imageId) {

        return imagesService.getImage(imageId);
    }

    /**
     * 门户获得轮播图
     * @return
     */
    @GetMapping("/list")
    public Result listLooper() {

        return looperService.listLooper();
    }


}
