package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sang.blog.biz.vo.ArticleQuery;
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

    Result listArticle(long current, long limit, ArticleQuery articleQuery);

    Result getArticleById(String id);

    Result updateArticle(String id, Article article);

    Result deleteArticle(String id);

    Result deleteArticleByState(String articleId);

    Result topArticle(String id);
}
