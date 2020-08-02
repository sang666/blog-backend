package com.sang.blog.biz.service.impl;

import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.mapper.ArticleMapper;
import com.sang.blog.biz.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

}
