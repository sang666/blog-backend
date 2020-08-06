package com.sang.blog.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.entity.User;
import com.sang.blog.biz.mapper.ArticleMapper;
import com.sang.blog.biz.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sang.blog.biz.service.UserService;
import com.sang.blog.biz.vo.ArticleQuery;
import com.sang.blog.commom.result.Result;
import com.sang.blog.commom.utils.Constants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static com.sang.blog.commom.utils.Constants.Article.SUMMARY_MAX_LENGTH;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Service
@Transactional
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 添加文章
     * 如果是多人的博客系统，得考虑审核的问题，通知通过或不通过
     *
     * 保存成草稿的问题
     * 1.用户手动提交：会发生页面跳转--提交完即可
     * 2.代码自动提交，每隔一段时间就会提交--程序提交不会发生跳转--重复提交--如果没有唯一标识，就会重复添加到数据库里
     * 不管哪种草稿--必须有标题
     *
     * 方案一：每次用户发新文章之前--现象后台请求一个唯一文章id
     * 如果是更新文件，则不需要请求这个唯一的id
     *
     * 方案二：可以直接提交，后台判断如果没有id就新创建，并且id作为此次返回的结果
     * 如果有id，就修改已经存在的内容
     *
     * 推荐做法：自动保存草稿，在前端本地完成，也就是保存在本地
     * 如果是用户手动提交的，就提交到后台
     *
     * 防止重复提交：可以通过id的方式，
     * 通过token——key的提交频率来计算，如果30秒之内多次提交，只有最前的一次有效
     * 其他的提交 直接return，提示用户不要太频繁操作
     *
     * 前端的处理，点击提交时候，禁止按钮，等到有响应结果再放开按钮
     *
     * TODO:后期可以有定时发布功能
     * @param article
     * @return
     */
    @Override
    public Result postArticle(Article article) {
        //拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();



        //检察用户
        User user = userService.checkUser(request,response);
        if (user == null) {
            return Result.err().message("用户未登录");
        }
        //检查数据 title,分类id，内容，类型，摘要，标签，
        String title = article.getTitle();
        if (StringUtils.isEmpty(title)) {
            return Result.err().message("标题不可以为空");
        }

        //两种,草稿和发布
        String state = article.getState();
        if (!Constants.Article.STATE_PUBLISH.equals(state) && !Constants.Article.STATE_DRAFT.equals(state)){
            //不支持该操作
            return Result.err().message("不支持此操作");
        }

        //类型（0表示富文本，1表示markdown）
        String type = article.getType();
        if (StringUtils.isEmpty(type)) {
            return Result.err().message("类型不可以为空");

        }
        if (!"0".equals(type)&& !"1".equals(type)) {
            return Result.err().message("类型格式不对");
        }


        //一下检查是发布的检查。草稿不需要检查
        if (Constants.Article.STATE_PUBLISH.equals(state)) {
            if (title.length()> Constants.Article.TITLE_MAX_LENGTH) {
                return Result.err().message("标题不可以超过123个字符");
            }

            String content = article.getContent();
            if (StringUtils.isEmpty(content)) {
                return Result.err().message("内容不可以为空");

            }

            String summary = article.getSummary();
            if (StringUtils.isEmpty(summary)) {
                return Result.err().message("摘要不可以为空");

            }
            if (summary.length()>SUMMARY_MAX_LENGTH) {
                return Result.err().message("摘要不可以超过256个字符");
            }

            String labels = article.getLabels();
            if (StringUtils.isEmpty(labels)) {
                return Result.err().message("标签不可以为空");

            }
        }

        String articleId = article.getId();
        if (StringUtils.isEmpty(articleId)) {
            //新内容


        }else {
            //更新内容
            Article articleFromDb = articleMapper.selectById(articleId);
            if (Constants.Article.STATE_PUBLISH.equals(articleFromDb.getState()) && Constants.Article.STATE_DRAFT.equals(state)) {
                //已经发布了，只能更新，不能保存为草稿
                return Result.err().message("已经发布的文章不支持保存草稿");
            }
        }

        //补充数据
        article.setUserId(user.getId());
        article.setAvatar(user.getAvatar());
        article.setUserName(user.getUserName());
        //保存到数据库
        articleMapper.insert(article);

        //TODO：保存到搜索的数据库里
        //返回结果,前端拿到id，只有一种case使用到这个id
        //如果要做程序自动保存成草稿（比若说每30秒保存一次，就需要加上这个id了，否则会创建多个item）
        return Result.ok().message(Constants.Article.STATE_DRAFT.equals(state)?"草稿保存成功":
                "文章发表成功").data("id",article.getId());
    }

    /**
     * 打出文章列表(带条件查询)
     * @param current
     * @param limit
     * @param articleQuery
     * @return
     */
    @Override
    public Result listArticle(long current, long limit, ArticleQuery articleQuery) {

        //创建page对象
        Page<Article> page = new Page<>(current,limit);
        //构建条件
        QueryWrapper<Article> wrapper = new QueryWrapper<>();

        String name = articleQuery.getName();
        String categoryId = articleQuery.getCategoryId();
        String begin = articleQuery.getBegin();
        String end = articleQuery.getEnd();
        //spring包中的stringUtils工具类

        //判断条件是否为空，如果为空就拼接条件
        if (!StringUtils.isEmpty(name)){
            wrapper.like("title",name);
        }

        if (!StringUtils.isEmpty(categoryId)){
            wrapper.eq("category_id",categoryId);
        }

        if (!StringUtils.isEmpty(begin)){
            wrapper.ge("update_time",begin);
        }

        if (!StringUtils.isEmpty(end)){
            wrapper.le("update_time",end);
        }

        //排序
        wrapper.orderByDesc("update_time");
        wrapper.select("id","title","user_id","category_id","type","cover",
                "state","summary","labels","view_count","create_time","update_time");

        articleMapper.selectPage(page,wrapper);
        long total = page.getTotal();//总记录数
        List<Article> records = page.getRecords();//

        return Result.ok().data("total",total).data("rows",records);
    }

    /**
     * 获取单个文章
     * 如果有审核机制，审核中的文章只有管理员和作者可以获取
     * 有草稿的，删除，置顶，已经发布的
     * 删除的不能获取，其他的都可以
     * @param id
     * @return
     */
    @Override
    public Result getArticleById(String id) {
        //拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        //查询出文章
        Article selectById = articleMapper.selectById(id);
        if (selectById == null) {
            return Result.err().message("文章不存在");
        }
        //判断文章状态
        String state = selectById.getState();
        if (Constants.Article.STATE_PUBLISH.equals(state) || Constants.Article.STATE_TOP.equals(state)) {
            //直接返回
            return Result.ok().message("获取文章成功").data("article",selectById);
        }
        //如果是删除/草稿，需要管理员角色
        User checkUser = userService.checkUser(request, response);
        String roles = checkUser.getRoles();
        if (!Constants.user.ROLE_ADMIN.equals(roles)) {
            return Result.PERMISSION_FORBID();
        }
        //返回结果
        return Result.ok().message("获取文章成功").data("article",selectById);
    }

    /**
     * 更新文章
     *
     * 只支持修改内容：标题，内容，分类，标签，摘要
     * @param id
     * @param article
     * @return
     */
    @Override
    public Result updateArticle(String id, Article article) {
        //先找出来，没有就返回
        Article selectById = articleMapper.selectById(id);
        if (selectById == null) {
            return Result.err().message("文章不存在");
        }

        String title = article.getTitle();
        if (!StringUtils.isEmpty(article.getTitle())) {
            selectById.setTitle(title);
        }

        String summary = article.getSummary();
        if (!StringUtils.isEmpty(article.getSummary())) {
            selectById.setSummary(summary);
        }

        String content = article.getContent();
        if (!StringUtils.isEmpty(article.getContent())) {
            selectById.setContent(content);
        }

        String labels = article.getLabels();
        if (!StringUtils.isEmpty(article.getLabels())) {
            selectById.setLabels(labels);
        }

        String categoryId = article.getCategoryId();
        if (!StringUtils.isEmpty(article.getCategoryId())) {
            selectById.setCategoryId(categoryId);
        }

        articleMapper.updateById(selectById);

        //修改
        //返回结果
        return Result.ok().message("文章更新成功");
    }
}
