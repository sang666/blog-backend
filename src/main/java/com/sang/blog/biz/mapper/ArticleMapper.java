package com.sang.blog.biz.mapper;

import com.sang.blog.biz.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */

@Repository
public interface ArticleMapper extends BaseMapper<Article> {

    int deleteByState(String articleId);

    int topArticle(String articleId);

    String listArticleLabelsById(String id);
}
