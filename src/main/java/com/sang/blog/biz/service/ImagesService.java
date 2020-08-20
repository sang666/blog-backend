package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.Images;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sang.blog.commom.result.Result;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
public interface ImagesService extends IService<Images> {

    Result getImage(String imageId);

    Result deleteImage(String imageId);

    Result listImage(long current, long limit, String original);
}
