package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.entity.Looper;
import com.sang.blog.commom.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */

@Slf4j
@RestController
@RequestMapping("/biz/article")
public class ArticleController {


    /**
     * @param article
     * @return
     */
    @PostMapping
    public Result addArticle(@RequestBody Article article) {

        return Result.ok();
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteArticle(@PathVariable String id) {

        return Result.ok();
    }


    /**
     * @param id
     * @param article
     * @return
     */
    @PutMapping("/{id}")
    public Result updateArticle(@PathVariable String id, @RequestBody Article article) {

        return Result.ok();
    }


    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getArticle(@PathVariable String id) {

        return Result.ok();
    }

    /**
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{current}/{limit}")
    public Result listArticle(@PathVariable long current, @PathVariable long limit) {

        return Result.ok();
    }


    /**
     * @param state
     * @return
     */
    @PutMapping("/sate/{id}/{state}")
    public Result updateArticleState(@PathVariable String id, @PathVariable String state) {

        return Result.ok();

    }

    /**
     * @param
     * @return
     */
    @PutMapping("/top/{id}")
    public Result updateArticleTop(@PathVariable String id) {

        return Result.ok();

    }


}
