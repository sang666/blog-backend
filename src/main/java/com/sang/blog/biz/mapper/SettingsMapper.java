package com.sang.blog.biz.mapper;

import com.sang.blog.biz.entity.Settings;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Repository
public interface SettingsMapper extends BaseMapper<Settings> {
    Settings findOneByKey(String key);

}
