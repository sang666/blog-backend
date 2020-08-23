package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.Article;
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
public interface ArticleService extends IService<Article> {


    Result postArticle(Article article);

    Result listArticle(long current, long limit,Integer state,
                       String name,String categoryId,
                       String begin,String end,String labels,String labelsLike);

    Result getArticleById(String id);

    Result updateArticle(String id, Article article);

    Result deleteArticle(String id);

    Result deleteArticleByState(String articleId);

    Result topArticle(String id);

    Result listRecommendArticles(String id, Integer size);

    Result listLabels(Integer size);


    Result getArticleByIdNoContent(String id);

    Result searchByContent(Integer page, Integer size, String keyword);

    Result listArticleNoCache(long current, long limit, Integer state,
                              String name, String categoryId, String begin,
                              String end, String labels, String labelsLike);

    Result getTotalCount();

}
