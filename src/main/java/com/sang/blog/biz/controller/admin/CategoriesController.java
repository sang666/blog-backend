package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Categories;
import com.sang.blog.biz.service.CategoriesService;
import com.sang.blog.commom.result.Result;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/biz/categories")
public class CategoriesController {


    @Autowired
    private CategoriesService categoriesService;


    /**
     *
     * 需要管理员权限
     * @param categories
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping("/addCat")
    public Result addCategory(@RequestBody Categories categories) {

        return categoriesService.addCategory(categories);

    }

    /**
     * 需要管理员权限
     * @param categoryId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{categoryId}")
    public Result deleteCategory(@PathVariable("categoryId") String categoryId) {


        return categoriesService.deleteCategory(categoryId);

    }

    /**
     * 管理员权限
     * 修改 名称 拼音 描述 名称
     * @param categoryId
     * @param categories
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{categoryId}")
    public Result updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Categories categories) {

        return categoriesService.updateCategory(categoryId,categories);

    }

    /**
     * 获取分类
     * 使用的case 修改的时候，获取一下，填充弹窗
     * 不获取也是可以的，从列表里获取数据
     * 需要管理员权限
     *
     * @param id
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{categoryId}")
    public Result getCategory(@PathVariable("categoryId") String id) {

        return categoriesService.getCategory(id);

    }

    /**
     * 管理员权限
     * @param
     * @param
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public Result listCategory() {

        return categoriesService.listCategory();

    }

}
