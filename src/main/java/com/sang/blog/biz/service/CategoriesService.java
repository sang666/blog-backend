package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.Categories;
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
public interface CategoriesService extends IService<Categories> {

    Result addCategory(Categories categories);

    Result getCategory(String id);

    Result listCategory();

    Result updateCategory(String categoryId, Categories categories);

    Result deleteCategory(String categoryId);
}
