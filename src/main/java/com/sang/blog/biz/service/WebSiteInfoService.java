package com.sang.blog.biz.service;


import com.sang.blog.commom.result.Result;

public interface WebSiteInfoService {
    Result getWebSiteTitle();

    Result getWebSiteInfo(String id);

    Result putWebSiteTitle(String title);

    Result getSeoInfo();

    Result putSeoInfo(String keywords, String description);

    Result getWebSiteCount();

    void updateViewCount();

}
