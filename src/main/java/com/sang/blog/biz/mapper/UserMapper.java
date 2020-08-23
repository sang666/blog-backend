package com.sang.blog.biz.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sang.blog.biz.entity.User;
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
public interface UserMapper extends BaseMapper<User> {

    User selectByEmail(String email);

    User selectByUserName(String userName);


    int updatePasswordByEmail(String encode, String email);

    int updateEmailById(String email, String id);
}
