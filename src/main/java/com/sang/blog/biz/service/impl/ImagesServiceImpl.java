package com.sang.blog.biz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sang.blog.biz.entity.Images;
import com.sang.blog.biz.mapper.ImagesMapper;
import com.sang.blog.biz.service.ImagesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Service
@Transactional
public class ImagesServiceImpl extends ServiceImpl<ImagesMapper, Images> implements ImagesService {

    @Autowired
    private ImagesMapper imagesMapper;

    /**
     * 获取图片
     * @param imageId
     * @return
     */
    @Override
    public Result getImage(String imageId) {

        Images images = imagesMapper.selectById(imageId);
        if (images == null) {
            return Result.err().message("没有此图片");
        }

        return Result.ok().message("图片获取成功").data("images",images);
    }

    /**
     * 删除图片
     * 逻辑删除
     * @param imageId
     * @return
     */
    @Override
    public Result deleteImage(String imageId) {

        int result = imagesMapper.deleteById(imageId);


        if (result==0) {
            return Result.err().message("该图片不存在");
        }
        return Result.ok().message("删除图片成功");
    }

    /**
     * 获取图片列表
     * @param current
     * @param limit
     * @return
     */
    @Override
    public Result listImage(long current, long limit) {

        Page<Images> page = new Page<>(current,limit);
        imagesMapper.selectPage(page,null);
        long total = page.getTotal();//总记录数
        List<Images> records = page.getRecords();
        //进行查询
        return Result.ok().message("获取分类列表成功").data("total",total).data("rows",records);
    }
}
