package com.sang.blog.biz.mapper;

import com.sang.blog.biz.entity.RefreshToken;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author sang666
 * @since 2020-07-16
 */
public interface RefreshTokenMapper extends BaseMapper<RefreshToken> {

    int deleteByTokenKey(String tokenKey);
}
