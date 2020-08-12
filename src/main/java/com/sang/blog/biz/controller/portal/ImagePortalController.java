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
public class ImagePortalController {

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


    // TODO: 2020/8/12 二维码登陆未做
    @GetMapping("/qr-code/{code}")
    public void getQrCodeImage(@PathVariable("code") String code){
        //生成二维码
        //二维码内容是什么
        //可以是简单的一个code，也就是传进来这个
        //如果是我们自己写的app来扫描，是识别并解析，请求对应的接口
        //如果是第三方的扫描，可以识别但没有用，只能显示这个code，
        // 如果是我们自己的app扫到，切割后面的内容拿到code进行解析
        //请求对应接口

    }


}
