package com.sang.blog.biz.service.impl;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sang.blog.biz.entity.Categories;
import com.sang.blog.biz.mapper.CategoriesMapper;
import com.sang.blog.biz.service.CategoriesService;
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
public class CategoriesServiceImpl extends ServiceImpl<CategoriesMapper, Categories> implements CategoriesService {

    @Autowired
    private CategoriesMapper categoriesMapper;

    /**
     * 添加分类
     * @param categories
     * @return
     */
    @Override
    public Result addCategory(Categories categories) {
        //先检查数据
        //必须的数据:分类名称 pinyin 顺序 描述
        if (StringUtils.isEmpty(categories.getName())) {
            return Result.err().message("分类名称不可以为空");
        }
        if (StringUtils.isEmpty(categories.getPinyin())) {
            return  Result.err().message("分类拼音不能为空");

        }
        if (StringUtils.isEmpty(categories.getDescription())) {
            return Result.err().message("分类描述不能为空");
        }
        //不全数据
        categories.setOrder(1);
        //保存数据
        categoriesMapper.insert(categories);
        //返回结果
        return Result.ok().message("添加分类成功");
    }

    /**
     * 获取分类
     * @param id
     * @return
     */
    @Override
    public Result getCategory(String id) {
        Categories selectById = categoriesMapper.selectById(id);
        if (selectById == null) {
            return Result.err().message("分类不存在");

        }
        return Result.ok().message("获取分类成功").data("category",selectById);
    }


    /**
     * 获取分类列表
     * @return
     */
    @Override
    public Result listCategory() {

        List<Categories> categories = categoriesMapper.selectList(null);

        //进行查询
        return Result.ok().message("获取分类列表成功").data("categories",categories);
    }

    /**
     * 修改
     * @param categoryId
     * @param categories
     * @return
     */
    @Override
    public Result updateCategory(String categoryId, Categories categories) {
        Categories selectById = categoriesMapper.selectById(categoryId);
        if (categoryId == null) {
            return Result.err().message("分类不存在");
        }
        //对内容判断，有些字段不可以为空
        String name = categories.getName();
        if (!StringUtils.isEmpty(name)) {
            selectById.setName(name);
        }
        String pinyin = categories.getPinyin();
        if (!StringUtils.isEmpty(pinyin)) {
            selectById.setPinyin(pinyin);
        }
        String description = categories.getDescription();
        if (!StringUtils.isEmpty(description)) {
            selectById.setDescription(description);
        }
        selectById.setOrder(categories.getOrder());
        //保存数据
        categoriesMapper.updateById(selectById);
        //返回结果
        return Result.ok().message("更新分类成功");
    }

    /**
     * 逻辑删除
     * @param categoryId
     * @return
     */
    @Override
    public Result deleteCategory(String categoryId) {
        int result = categoriesMapper.deleteById(categoryId);
        if (result==0) {
            return Result.err().message("该分类不存在");
        }
        return Result.ok().message("删除分类成功");
    }
}
