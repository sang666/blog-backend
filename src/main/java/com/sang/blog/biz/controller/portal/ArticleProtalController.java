package com.sang.blog.biz.controller.portal;


import com.sang.blog.commom.result.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/protal/Article")
public class ArticleProtalController {

    /**
     * 文章分页
     *
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{current}/{limit}")
    public Result listArticle(@PathVariable long current,
                              @PathVariable long limit) {

        return Result.ok();
    }

    /**
     * 分类文章分页
     *
     * @param categoryId
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{categoryId}/{current}/{limit}")
    public Result listArticleByCategory(@PathVariable String categoryId,
                                        @PathVariable long current,
                                        @PathVariable long limit) {

        return Result.ok();
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getArticleDetail(@PathVariable String id) {

        return Result.ok();
    }

    @GetMapping("/recommend{id}")
    public Result getRecommendArticles(@PathVariable String id) {

        return Result.ok();
    }


}
