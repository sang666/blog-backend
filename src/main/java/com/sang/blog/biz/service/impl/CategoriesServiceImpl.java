package com.sang.blog.biz.service.impl;

import com.sang.blog.biz.entity.Categories;
import com.sang.blog.biz.mapper.CategoriesMapper;
import com.sang.blog.biz.service.CategoriesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Service
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories> implements CategoriesService {

}
