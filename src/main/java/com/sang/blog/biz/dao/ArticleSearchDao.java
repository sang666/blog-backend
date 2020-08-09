package com.sang.blog.biz.dao;

import com.sang.blog.biz.vo.ArticleSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ArticleSearchDao extends ElasticsearchRepository<ArticleSearch,String> {


    ArticleSearch queryArticleSearchById(String id);




}
