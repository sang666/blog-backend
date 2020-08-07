package com.sang.blog.biz.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sang.blog.biz.entity.Looper;
import com.sang.blog.biz.mapper.LooperMapper;
import com.sang.blog.biz.service.LooperService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sang.blog.commom.result.Result;
import org.apache.commons.lang3.StringUtils;
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
public class LooperServiceImpl extends ServiceImpl<LooperMapper, Looper> implements LooperService {


    @Autowired
    private LooperMapper looperMapper;


    /**
     * 添加轮播图
     * @param looper
     * @return
     */
    @Override
    public Result addLooper(Looper looper) {

        //判空
        String title = looper.getTitle();
        if (StringUtils.isEmpty(title)) {
            return Result.err().message("标题不能为空");
        }
        String imageUrl = looper.getImageUrl();
        if (StringUtils.isEmpty(imageUrl)) {
            return Result.err().message("url不能为空");
        }
        String targetUrl = looper.getTargetUrl();
        if (StringUtils.isEmpty(targetUrl)) {
            return Result.err().message("跳转链接不能为空");
        }
        //补充数据
        looper.setOrder(1);

        looperMapper.insert(looper);
        return Result.ok().message("轮播图插入成功");
    }

    /**
     * 删除图片
     * @param looperId
     * @return
     */
    @Override
    public Result deleteLooper(String looperId) {
        int result = looperMapper.deleteById(looperId);
        if (result==0) {
            return Result.err().message("轮播图不存在");
        }
        return Result.ok().message("删除成功");
    }

    /**
     * 过去单个轮播图
     * @param looperId
     * @return
     */
    @Override
    public Result getLooper(String looperId) {

        Looper looper = looperMapper.selectById(looperId);
        if (looper == null) {
            return Result.err().message("轮播图不存在");
        }
        return Result.ok().data("lopper",looper).message("轮播图获取成功");
    }

    /**
     * 更新
     * @param looperId
     * @param looper
     * @return
     */
    @Override
    public Result updateLooper(String looperId, Looper looper) {
        Looper selectById = looperMapper.selectById(looperId);
        if (selectById == null) {
            return Result.err().message("轮播图不存在");
        }
        //不能为空的要判空
        String title = looper.getTitle();
        if (!StringUtils.isEmpty(title)) {
            selectById.setTitle(title);
        }
        String targetUrl = looper.getTargetUrl();
        if (!StringUtils.isEmpty(targetUrl)) {
            selectById.setTargetUrl(targetUrl);
        }

        String imageUrl = looper.getImageUrl();
        if (!StringUtils.isEmpty(imageUrl)) {
            selectById.setImageUrl(imageUrl);
        }

        looperMapper.updateById(selectById);
        return Result.ok().message("轮播图更新成功");
    }

    @Override
    public Result listLooper() {


        List<Looper> loopers = looperMapper.selectList(null);
        //进行查询
        return Result.ok().message("获取轮播图列表成功").data("loopers",loopers);
    }
}
