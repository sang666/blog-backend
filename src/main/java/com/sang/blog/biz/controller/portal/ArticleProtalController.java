package com.sang.blog.biz.controller.portal;


import com.sang.blog.biz.service.ArticleService;
import com.sang.blog.biz.vo.ArticleQuery;
import com.sang.blog.commom.result.Result;
import com.sang.blog.commom.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/protal/Article")
public class ArticleProtalController {


    @Autowired
    private ArticleService articleService;

    /**
     * 文章分页
     * 是已经发布的，置顶的有另一个接口获取，其他的不可以从此接口获取
     * @param current
     * @param limit
     * @return
     */
    @PostMapping("/list/{current}/{limit}")
    public Result listArticle(@PathVariable long current,
                              @PathVariable long limit) {

        return articleService.listArticle(current,limit,
                Constants.Article.STATE_PUBLISH,null,null,null,null,null,null);
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

        return articleService.listArticleNoCache(current,limit,
                Constants.Article.STATE_PUBLISH,null,categoryId,null,null,null,null);
    }

    /**
     * 置顶文章列表的接口
     * @param current
     * @param limit
     * @return
     */
    @PostMapping("/TopArticle/{current}/{limit}")
    public Result getTopArticle(@PathVariable long current,
                                @PathVariable long limit){

        return articleService.listArticleNoCache(current,limit,
                Constants.Article.STATE_TOP,null,null,null,null,null,null);
    }


    /**
     * 获取文章详情
     * 权限:任意用户
     * 内容过滤，只允许拿置顶的或者是已发布的
     * 其他的获取，比如草稿，只能用户获取，已经删除的只有管理员才能获取
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getArticleDetail(@PathVariable("id") String id) {

        return articleService.getArticleById(id);
    }


    /**
     * 通过标签来计算匹配度
     * 标签:有一个，或者多个，5个以内，包含五个
     * 从里面随即哪一个标签出来--->每一次获取的推荐文章不那么雷同
     * 通过标签去查询相关文章，则从数据库中获取最新的文章
     * @param id
     * @return
     */
    @GetMapping("/recommend{id}/{size}")
    public Result getRecommendArticles(@PathVariable String id,@PathVariable Integer size) {



        return articleService.listRecommendArticles(id,size);
    }

    /**
     * 获取标签云，用户点击标签就会通过标签获取相关文章列表
     * @return
     */
    @GetMapping("/labels/{size}")
    public Result getLabels(@PathVariable Integer size){

        return articleService.listLabels(size);
    }

    /**
     * 通过标签获取列表
     * @param current
     * @param limit
     * @return
     */
    @PostMapping("/labels/{current}/{limit}/{label}")
    public Result getArticleByLabel(@PathVariable long current,
                                    @PathVariable long limit,@PathVariable String label){

        return articleService.listArticle(current,limit,
                Constants.Article.STATE_PUBLISH,null,null,null,null,label,null);
    }


}
