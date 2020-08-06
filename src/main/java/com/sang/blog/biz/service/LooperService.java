package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.Looper;
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
public interface LooperService extends IService<Looper> {

    Result addLooper(Looper looper);

    Result deleteLooper(String looperId);

    Result getLooper(String looperId);

    Result updateLooper(String looperId, Looper looper);

    Result listLooper(long current, long limit);
}
