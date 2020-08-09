package com.sang.blog.biz.service.impl;


import com.sang.blog.biz.entity.Settings;
import com.sang.blog.biz.mapper.SettingsMapper;
import com.sang.blog.biz.service.WebSiteInfoService;
import com.sang.blog.commom.result.Result;
import com.sang.blog.commom.utils.Constants;
import com.sang.blog.commom.utils.RedisUtils;
import com.sang.blog.commom.utils.TextUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static com.sang.blog.commom.utils.Constants.settings.WEB_SITE_TITLE;


@Service
@Transactional
public class WebSiteInfoServiceImpl implements WebSiteInfoService {

    @Autowired
    private SettingsMapper settingsMapper;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 获取网站title
     * @return
     */
    @Override
    public Result getWebSiteTitle() {

        Settings title = settingsMapper.findOneByKey(WEB_SITE_TITLE);


        return Result.ok().message("获取网站title成功").data("title",title);
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public Result getWebSiteInfo(String id) {
        return null;
    }

    /**
     * 更新网站标题
     * @param title
     * @return
     */
    @Override
    public Result putWebSiteTitle(String title) {

        if (StringUtils.isEmpty(title)) {
            return Result.err().message("网站标题不能为空");
        }
        Settings titleFormDB = settingsMapper.findOneByKey(WEB_SITE_TITLE);
        if (titleFormDB == null) {
            titleFormDB=new Settings();
            titleFormDB.setKey(WEB_SITE_TITLE);
            titleFormDB.setValue(title);
            settingsMapper.insert(titleFormDB);
            return Result.ok().message("网站title更新成功");
        }
        titleFormDB.setValue(title);
        settingsMapper.updateById(titleFormDB);
        return Result.ok().message("网站title更新成功");
    }

    /**
     * 获取seo信息
     * @return
     */
    @Override
    public Result getSeoInfo() {

        Settings description = settingsMapper.findOneByKey(Constants.settings.WEB_SITE_DESCRIPTION);
        Settings keyWords = settingsMapper.findOneByKey(Constants.settings.WEB_SITE_KEYWORDS);
        HashMap<String,String> result = new HashMap<>();
        result.put(description.getKey(),description.getValue());
        result.put(keyWords.getKey(),keyWords.getValue());

        return Result.ok().message("获取seo信息成功").data("result",result);
    }

    /**
     * 更新seo信息
     * @param keywords
     * @param description
     * @return
     */
    @Override
    public Result putSeoInfo(String keywords, String description) {
        if (StringUtils.isEmpty(keywords)) {
            return Result.err().message("关键字不能为空");
        }

        if (StringUtils.isEmpty(description)) {
            return Result.err().message("描述不能为空");
        }

        Settings descriptionFromDB = settingsMapper.findOneByKey(Constants.settings.WEB_SITE_DESCRIPTION);
        Settings keyWordsFromDB = settingsMapper.findOneByKey(Constants.settings.WEB_SITE_KEYWORDS);
        if (descriptionFromDB == null || keyWordsFromDB == null) {
            descriptionFromDB=new Settings();
            descriptionFromDB.setKey(Constants.settings.WEB_SITE_DESCRIPTION);
            descriptionFromDB.setValue(description);
            settingsMapper.insert(descriptionFromDB);
            keyWordsFromDB=new Settings();
            keyWordsFromDB.setKey(Constants.settings.WEB_SITE_KEYWORDS);
            keyWordsFromDB.setValue(keywords);
            settingsMapper.insert(keyWordsFromDB);
            return Result.ok().message("关键字和描述更新成功");

        }
        descriptionFromDB.setValue(description);
        settingsMapper.updateById(descriptionFromDB);

        keyWordsFromDB.setValue(keywords);
        settingsMapper.updateById(keyWordsFromDB);

        return Result.ok().message("关键字和描述更新成功");
    }

    /**
     * 这是全网站的访问量，要做的细一点，还得分来源
     * 这里只统计游览量，只统计文章的游览量
     * @return
     */
    @Override
    public Result getWebSiteCount() {
        //先从redis里拿出来
        String viewCountStr = (String) redisUtils.get(Constants.settings.WEB_SITE_VIEW_COUNT);
        Settings viewCount = settingsMapper.findOneByKey(Constants.settings.WEB_SITE_VIEW_COUNT);

        if (viewCount == null) {
            viewCount=new Settings();
            viewCount.setKey(Constants.settings.WEB_SITE_VIEW_COUNT);
            viewCount.setValue("1");
            settingsMapper.insert(viewCount);

        }

        if (StringUtils.isEmpty(viewCountStr)) {
            viewCountStr = viewCount.getValue();
            redisUtils.set(Constants.settings.WEB_SITE_VIEW_COUNT,viewCountStr);
        }else {
            //吧redis里的update到数据库里
            viewCount.setValue(viewCountStr);
            settingsMapper.updateById(viewCount);
        }
        HashMap<String, Integer> result = new HashMap<>();
        result.put(viewCount.getKey(),Integer.valueOf(viewCount.getValue()));


        return Result.ok().message("获取网络游览量成功").data("result",result);
    }


    /**
     * 1.并发量
     * 2.过滤相同的ip或id
     * 3.防止攻击，超过一定次数的访问，就提示请稍后重试
     */
    @Override
    public void updateViewCount() {
        //redis的时机
        Object viewCount = redisUtils.get(Constants.settings.WEB_SITE_VIEW_COUNT);
        if (viewCount == null) {
            Settings settings = settingsMapper.findOneByKey(Constants.settings.WEB_SITE_VIEW_COUNT);
            redisUtils.set(Constants.settings.WEB_SITE_VIEW_COUNT,settings.getValue());
        }
        //数字自增
        redisUtils.incr(Constants.settings.WEB_SITE_VIEW_COUNT,1);
    }
}
