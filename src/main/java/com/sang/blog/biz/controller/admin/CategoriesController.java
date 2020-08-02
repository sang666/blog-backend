package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Categories;
import com.sang.blog.biz.service.CategoriesService;
import com.sang.blog.commom.result.Result;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param categories
     * @return
     */
    @PostMapping("/addCat")
    public Result addCategory(@RequestBody Categories categories) {
        boolean save = categoriesService.save(categories);
        if (!save) {
            return Result.err();
        }
        return Result.ok();

    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteCategory(@PathVariable String id) {

        return Result.ok();

    }

    /**
     * @param id
     * @param categories
     * @return
     */
    @PutMapping("/{id}")
    public Result updateCategory(@PathVariable String id, @RequestBody Categories categories) {

        return Result.ok();

    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getCategory(@PathVariable String id) {

        return Result.ok();

    }

    /**
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{current}/{limit}")
    public Result listCategory(@PathVariable @ApiParam("当前页") long current,
                               @PathVariable @ApiParam("每页n条") long limit) {

        return Result.ok();

    }

}
