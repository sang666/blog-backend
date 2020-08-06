package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.service.ArticleService;
import com.sang.blog.biz.vo.ArticleQuery;
import com.sang.blog.commom.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private ArticleService articleService;


    /**
     * @param article
     * @return
     */
    @PostMapping
    @PreAuthorize("@permission.admin()")
    public Result addArticle(@RequestBody Article article) {

        return articleService.postArticle(article);
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteArticle(@PathVariable("id") String id) {

        return articleService.deleteArticle(id);
    }


    /**
     * @param id
     * @param article
     * @return
     */
    @PutMapping("/{id}")
    public Result updateArticle(@PathVariable("id") String id, @RequestBody Article article) {

        return articleService.updateArticle(id,article);
    }


    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getArticle(@PathVariable("id") String id) {

        return articleService.getArticleById(id);
    }

    /**
     * 条件分页查询
     * @param current
     * @param limit
     * @return
     */
    @PostMapping("/list/{current}/{limit}")
    public Result listArticle(@PathVariable("current") long current, @PathVariable("limit") long limit,@RequestBody(required = false) ArticleQuery articleQuery) {

        return articleService.listArticle(current,limit,articleQuery);
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
