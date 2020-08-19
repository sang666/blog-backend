package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.dao.ArticleSearchDao;
import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.service.ArticleService;
import com.sang.blog.biz.vo.ArticleQuery;
import com.sang.blog.biz.vo.ArticleSearch;
import com.sang.blog.commom.result.Result;
import com.sang.blog.commom.utils.Constants;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

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
    @PostMapping("/add")
    @PreAuthorize("@permission.admin()")
    public Result addArticle(@RequestBody Article article) {

        return articleService.postArticle(article);
    }

    /**
     *
     * 真删！！
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@permission.admin()")
    public Result deleteArticle(@PathVariable("id") String id) {

        return articleService.deleteArticle(id);
    }


    @DeleteMapping("/state/{articleId}")
    @PreAuthorize("@permission.admin()")
    public Result deleteArticleByUpdateState(@PathVariable("articleId")String articleId){

        return articleService.deleteArticleByState(articleId);
    }


    /**
     * @param id
     * @param article
     * @return
     */
    @PutMapping("/{id}")
    @PreAuthorize("@permission.admin()")
    public Result updateArticle(@PathVariable("id") String id, @RequestBody Article article) {

        return articleService.updateArticle(id,article);
    }


    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @PreAuthorize("@permission.admin()")
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
    @PreAuthorize("@permission.admin()")
    public Result listArticle(@PathVariable("current") long current,
                              @PathVariable("limit") long limit,
                              Integer state,
                              String name,String categoryId,String begin,String end,String labels) {

        return articleService.listArticle(current,limit,state,name,categoryId,begin,end,labels,null);
    }


    /**
     * @param state
     * @return
     */
  /*  @PutMapping("/sate/{id}/{state}")
    @PreAuthorize("@permission.admin()")
    public Result updateArticleState(@PathVariable String id, @PathVariable String state) {

        return Result.ok();

    }*/

    /**
     * @param
     * @return
     */
    @PutMapping("/top/{id}")
    @PreAuthorize("@permission.admin()")
    public Result updateArticleTop(@PathVariable("id") String id) {

        return articleService.topArticle(id);

    }


    @Autowired
    private ArticleSearchDao articleSearchDao;
    @PostMapping("/es/add")
    public Result addArticle(){
        List<Article> articleList = articleService.list(null);
        for (Article article : articleList) {


            String articleType = article.getType();
            String html;
            if (Constants.Article.TUPE_MARKDOWM.equals(articleType)) {
                //转成html
                MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS, Arrays.asList(
                        TablesExtension.create(),
                        JekyllTagExtension.create(),
                        TocExtension.create(),
                        SimTocExtension.create()
                ));
                Parser parser = Parser.builder(options).build();
                HtmlRenderer renderer = HtmlRenderer.builder(options).build();
                Node document = parser.parse(article.getContent());
                html = renderer.render(document);
                //存到es数据库
            }else {
                html = article.getContent();
            }
            String content = Jsoup.parse(html).text();
            ArticleSearch articleSearch = new ArticleSearch();
            articleSearch.setId(article.getId());
            articleSearch.setContent(content);
            articleSearch.setSummary(article.getSummary());
            articleSearch.setTitle(article.getTitle());
            articleSearch.setLabels(article.getLabels());
            articleSearch.setCategoryId(article.getCategoryId());
            //log.info(article.getLabelList().listIterator().next());
            articleSearchDao.save(articleSearch);

        }

        return Result.ok().message("台南佳成功");
    }


    /*@GetMapping("/es/select")
    public Result selectArticle(){

        Iterable<ArticleSearch> articles = articleSearchDao.findAll();

        return Result.ok().data("dd",articles);
    }
*/

}
