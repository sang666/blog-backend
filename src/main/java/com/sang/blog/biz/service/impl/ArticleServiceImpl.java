package com.sang.blog.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sang.blog.biz.dao.ArticleSearchDao;
import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.entity.Labels;
import com.sang.blog.biz.entity.User;
import com.sang.blog.biz.mapper.ArticleMapper;
import com.sang.blog.biz.mapper.CommentMapper;
import com.sang.blog.biz.mapper.LabelsMapper;
import com.sang.blog.biz.service.ArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sang.blog.biz.service.UserService;
import com.sang.blog.biz.vo.ArticleSearch;
import com.sang.blog.commom.result.Result;
import com.sang.blog.commom.utils.Constants;
import com.sang.blog.commom.utils.RedisUtils;
import com.vladsch.flexmark.ext.jekyll.tag.JekyllTagExtension;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.SimTocExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.parser.Parser;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.MutableDataSet;
import io.swagger.annotations.ApiModelProperty;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import java.io.IOException;
import java.util.*;

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
@Slf4j
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements ArticleService {

    @Autowired
    private ArticleSearchDao articleSearchDao;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private Random random;

    @Autowired
    private LabelsMapper labelsMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private Gson gson;
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private RestHighLevelClient restHighLevelClient;








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
        Integer state = article.getState();
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
        //存到es搜索库
        if (Constants.Article.STATE_PUBLISH.equals(article.getState())) {
            ArticleSearch articleSearch = new ArticleSearch();
            articleSearch.setId(article.getId());
            String articleType = article.getType();

            String html;
            if (Constants.Article.TUPE_MARKDOWM.equals(articleType)) {
                //转成html
                MutableDataSet options = new MutableDataSet().set(Parser.EXTENSIONS,Arrays.asList(
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

            //到这里原来不管是什么都是html
            String content = Jsoup.parse(html).text();
            articleSearch.setContent(content);
            articleSearch.setSummary(article.getSummary());
            articleSearch.setTitle(article.getTitle());
            articleSearch.setCategoryId(article.getCategoryId());
            articleSearch.setLabels(article.getLabels());
            articleSearchDao.save(articleSearch);
        }



        //打散标签，存到数据库
        this.setupLabels(article.getLabels());
        // 添加文章后删除redis里的缓存
        redisUtils.del(Constants.Article.KEY_ARTICLE_LIST_FIRST_PAGE);
        //返回结果,前端拿到id，只有一种case使用到这个id
        //如果要做程序自动保存成草稿（比若说每30秒保存一次，就需要加上这个id了，否则会创建多个item）
        return Result.ok().message(Constants.Article.STATE_DRAFT.equals(state)?"草稿保存成功":
                "文章发表成功");
    }

    /**
     * 打出文章列表(带条件查询)
     * @param current
     * @param limit
     * @param
     * @return
     */
    @Override
    public Result listArticle(long current, long limit,Integer state,
                              String name,String categoryId,
                              String begin,String end,String labels,String labelsLike) {

        String articleListJson = (String) redisUtils.get(Constants.Article.KEY_ARTICLE_LIST_FIRST_PAGE);
        if (!StringUtils.isEmpty(articleListJson)&&current==1) {
            Page<Article>articleList = gson.fromJson(articleListJson,new TypeToken<Page<Article>>(){
            }.getType());
            log.info("使用redis缓存。。。。");
            return Result.ok().message("文章列表获取成功").data("rows",articleList);
        }
        //创建page对象
        Page<Article> page = new Page<>(current,limit);

        //构建条件
        QueryWrapper<Article> wrapper = new QueryWrapper<>();

        /*String name = articleQuery.getName();
        String categoryId = articleQuery.getCategoryId();
        String begin = articleQuery.getBegin();
        String end = articleQuery.getEnd();*/
        //spring包中的stringUtils工具类

        wrapper.select("id","title","user_id","category_id","type","cover",
                "state","summary","labels","view_count","create_time","update_time");

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

        if (!(state ==null)){
            wrapper.eq("state",state);
        }

        if (!StringUtils.isEmpty(labels)){
            wrapper.like("labels",labels);
        }

        if (!StringUtils.isEmpty(labelsLike)){
            wrapper.like("labels",labelsLike);
        }
        //排序
        wrapper.orderByDesc("update_time");



        articleMapper.selectPage(page, wrapper);
        //long total = page.getTotal();//总记录数
        //List<Article> records = page.getRecords();//
        //保存到redis
        //List<Article> records = articleIPage.getRecords();
        if (current==1) {
            redisUtils.set(Constants.Article.KEY_ARTICLE_LIST_FIRST_PAGE,gson.toJson(page),Constants.TimeValue.MIN*15);
        }

        return Result.ok().data("rows",page);
    }

    /**
     * 获取单个文章
     * 如果有审核机制，审核中的文章只有管理员和作者可以获取
     * 有草稿的，删除，置顶，已经发布的
     * 删除的不能获取，其他的都可以
     *
     * 统计文章的阅读量，要精确一点的话要对ip进行处理，同一个ip的话则不保存
     * 先把阅读量再redis里更新
     * 文章也会在redis里缓存一份，比如缓存个十分钟
     * 当文章没有的时候，从mysql中获取，这个时同时更新阅读量
     * 十分钟以后在下一次访问的时候更新阅读量
     *
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
        //先从redis里获取文章
        String articleJson = (String) redisUtils.get(Constants.Article.KEY_ARTICLE_CACHE + id);
        if (!StringUtils.isEmpty(articleJson)) {
            Article article = gson.fromJson(articleJson, Article.class);
            //增加阅读数量
            redisUtils.incr(Constants.Article.KEY_ARTICLE_CACHE_COUNT+id,1);
            return Result.ok().message("获取文章成功").data("article",article);
        }
        //如果没有，再去数据库mysql里获取
        Article selectById = articleMapper.selectById(id);
        if (selectById == null) {
            return Result.err().message("文章不存在");
        }
        //判断文章状态
        Integer state = selectById.getState();
        if (Constants.Article.STATE_PUBLISH.equals(state) || Constants.Article.STATE_TOP.equals(state)) {
            //正常发布的状态才可以增加阅读量
            redisUtils.set(Constants.Article.KEY_ARTICLE_CACHE+id,gson.toJson(selectById),Constants.TimeValue.MIN*5);
            //设置阅读量的key，先从redis里拿，如果redis里没有，就从article中去，并且添加到redis里
            String viewCount= (String) redisUtils.get(Constants.Article.KEY_ARTICLE_CACHE_COUNT+id);
            if (StringUtils.isEmpty(viewCount)) {
                long newCount = selectById.getViewCount()+1;
                redisUtils.set(Constants.Article.KEY_ARTICLE_CACHE_COUNT+id,String.valueOf(newCount));
            }else {
                //有的话就更新到mysql中
                long newCount = redisUtils.incr(Constants.Article.KEY_ARTICLE_CACHE_COUNT+id,1);
                selectById.setViewCount(newCount);
                //更新搜索数据库里的阅读量
                articleMapper.updateById(selectById);
            }
            //直接返回
            return Result.ok().message("获取文章成功").data("article",selectById);
        }
        //如果是删除/草稿，需要管理员角色
        User checkUser = userService.checkUser(request, response);
        //String roles = checkUser.getRoles();
        if (checkUser==null || !Constants.user.ROLE_ADMIN.equals(checkUser.getRoles())) {
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
        ArticleSearch articleSearch = articleSearchDao.queryArticleSearchById(selectById.getId());
        String title = article.getTitle();
        if (!StringUtils.isEmpty(article.getTitle())) {
            selectById.setTitle(title);
            articleSearch.setTitle(title);
        }

        String summary = article.getSummary();
        if (!StringUtils.isEmpty(article.getSummary())) {
            selectById.setSummary(summary);
            articleSearch.setSummary(summary);
        }

        String content = article.getContent();
        if (!StringUtils.isEmpty(article.getContent())) {
            selectById.setContent(content);
            articleSearch.setContent(content);
        }

        String labels = article.getLabels();
        if (!StringUtils.isEmpty(article.getLabels())) {
            selectById.setLabels(labels);
            articleSearch.setLabels(labels);
        }

        String categoryId = article.getCategoryId();
        if (!StringUtils.isEmpty(article.getCategoryId())) {
            selectById.setCategoryId(categoryId);
            articleSearch.setCategoryId(categoryId);
        }

        articleMapper.updateById(selectById);
        //存到es搜索
        articleSearchDao.save(articleSearch);

        //修改
        //返回结果
        return Result.ok().message("文章更新成功");
    }

    /**
     * 真删
     * @param id
     * @return
     */
    @Override
    public Result deleteArticle(String id) {

        Article article = articleMapper.selectById(id);
        if (article == null) {
            return Result.err().message("文章不存在");
        }
        int result = articleMapper.deleteById(id);
        if (result>0) {
            redisUtils.del(Constants.Article.KEY_ARTICLE_CACHE+id);
            redisUtils.del(Constants.Article.KEY_ARTICLE_LIST_FIRST_PAGE);

            articleSearchDao.deleteArticleSearchById(id);
            commentMapper.deleteById(id);


            return Result.ok().message("文章删除成功");

        }
        return Result.err().message("文章删除失败");
    }

    /**
     * 删除通过状态，逻辑删除
     * @param articleId
     * @return
     */
    @Override
    public Result deleteArticleByState(String articleId) {

        int result = articleMapper.deleteByState(articleId);
        if (result>0) {
            articleSearchDao.deleteArticleSearchById(articleId);

            return Result.ok().message("文章删除成功");

        }

        return Result.err().message("文章不存在");
    }


    /**
     * 置顶
     * @param id
     * @return
     */

     /*int result = articleMapper.topArticle(id);
            if (result>0) {
                return Result.ok().message("文章置顶成功");
            }
            return Result.err().message("文章不存在");*/
    @Override
    public Result topArticle(String id) {

        //已经发布的才能置顶
        Article article = articleMapper.selectById(id);
        if (article == null) {
                return Result.err().message("文章不存在");
        }
        Integer state = article.getState();
        if (Constants.Article.STATE_PUBLISH.equals(state)) {

            article.setState(Constants.Article.STATE_TOP);
            articleMapper.updateById(article);
            return Result.ok().message("文章置顶成功");
        }if (Constants.Article.STATE_TOP.equals(state)){
            article.setState(Constants.Article.STATE_PUBLISH);
            articleMapper.updateById(article);
            return Result.ok().message("已取消置顶");
        }
        return Result.err().message("不支持该操作");

    }


    /**
     *
     * 获取推荐文章，通过标签来计算
     * @param id
     * @param size
     * @return
     */
    @Override
    public Result listRecommendArticles(String id, Integer size) {
        //查询文章，不需要文章，只需要标签
        String labels = articleMapper.listArticleLabelsById(id);
        log.info("labels---->"+labels);
        //打散标签
        List<String>labelList = new ArrayList<>();
        if (!labels.contains("-")) {
            labelList.add(labels);
        }else {
            labelList.addAll(Arrays.asList(labels.split("-")));
        }
        //从列表中随机获取一个标签，查询与此标签相似的文章
        String targetLabel = labelList.get(random.nextInt(labelList.size()));
        //不查询内容
        log.info("随机获取的是--->"+targetLabel);
        //创建page对象
        Page<Article> page = new Page<>(1,size);
        //构建条件
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.select("id","title","user_id","category_id","type","cover",
                "state","summary","labels","view_count","create_time","update_time");
        wrapper.or(i -> i.eq("state",Constants.Article.STATE_PUBLISH).or().eq("state",Constants.Article.STATE_TOP));
        wrapper.and(i->i.like("labels",targetLabel));



        articleMapper.selectPage(page,wrapper);
        long total = page.getTotal();//总记录数
        List<Article> records = page.getRecords();

        //TODO:这个功能砍了，不想写了，补充文章的
        /*if (total<size){
            //说明数量不够，补充新文章
            Integer dxSize = size-total;
            //
        }*/
        return Result.ok().data("total",total).data("rows",records);
    }

    /**
     * 标签云
     * @param size
     * @return
     */
    @Override
    public Result listLabels(Integer size) {

        Page<Labels> page = new Page<>(1,size);
        QueryWrapper<Labels> wrapper = new QueryWrapper<>();

        wrapper.orderByDesc("count");
        labelsMapper.selectPage(page,wrapper);
        long total = page.getTotal();//总记录数
        List<Labels> records = page.getRecords();

        return Result.ok().message("获取标签列表成功").data("total",total).data("rows",records);
    }

    /**
     * 得到一篇文章没有内容数据
     * @param id
     * @return
     */
    @Override
    public Result getArticleByIdNoContent(String id) {
        //拿到request和response
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        HttpServletResponse response = requestAttributes.getResponse();
        //查询出文章

        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        wrapper.select("id","title","user_id","category_id","type","cover",
                "state","summary","labels","view_count","create_time","update_time");
        Article selectById = articleMapper.selectOne(wrapper);
        if (selectById == null) {
            return Result.err().message("文章不存在");
        }
        //判断文章状态
        Integer state = selectById.getState();
        if (Constants.Article.STATE_PUBLISH.equals(state) || Constants.Article.STATE_TOP.equals(state)) {
            //直接返回
            return Result.ok().message("获取文章成功").data("article",selectById);
        }
        //如果是删除/草稿，需要管理员角色
        User checkUser = userService.checkUser(request, response);
        //String roles = checkUser.getRoles();
        if (checkUser==null || !Constants.user.ROLE_ADMIN.equals(checkUser.getRoles())) {
            return Result.PERMISSION_FORBID();
        }
        //返回结果
        return Result.ok().message("获取文章成功").data("article",selectById);
    }

    /**
     * 搜索服务，乞丐版，能用就行
     *
     * @param page
     * @param size
     * @param keyword
     * @return
     */

    @SneakyThrows
    @Override
    public Result searchByContent(Integer page, Integer size, String keyword) {



        if (keyword == null) {
            return Result.err().message("关键字不能为空");
        }

        /*// TODO: 2020/8/9 剩个高亮没整
        // 构建查询内容
        QueryStringQueryBuilder queryBuilder = new QueryStringQueryBuilder(keyword);
        // 查询的字段
        Pageable pageable = PageRequest.of(page, size);
        queryBuilder.field("title").field("content").field("labels");

        Iterable<ArticleSearch> searchResult = articleSearchDao.search(queryBuilder,pageable);
        Iterator<ArticleSearch> iterator = searchResult.iterator();
        List<ArticleSearch> list = new ArrayList<ArticleSearch>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }*/

        // TODO: 2020/8/20


        SearchRequest searchRequest = new SearchRequest("blog");
        List<ArticleSearch>list = new ArrayList<>();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("content",keyword)).from(page).size(size)
                .highlighter(new HighlightBuilder()
                        .field("*")
                        .requireFieldMatch(false).preTags("<span style='color:red'>").postTags("</span>"));

        searchRequest.types("article").source(searchSourceBuilder);

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        for (SearchHit hit : hits) {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            ArticleSearch articleSearch = new ArticleSearch();
            articleSearch.setId(hit.getId());
            articleSearch.setCategoryId(sourceAsMap.get("categoryId").toString());
            articleSearch.setLabels(sourceAsMap.get("labels").toString());
            articleSearch.setContent(sourceAsMap.get("content").toString());
            articleSearch.setSummary(sourceAsMap.get("summary").toString());
            articleSearch.setTitle(sourceAsMap.get("title").toString());
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields.containsKey("content")){
                articleSearch.setContent(highlightFields.get("content").fragments()[0].toString());
            }
            list.add(articleSearch);
        }
        long totalHits = searchResponse.getHits().getTotalHits();
        //根据一个值查询多个字段  并高亮显示  这里的查询是取并集，即多个字段只需要有一个字段满足即可
        //需要查询的字段

        return Result.ok().data("list",list).message("搜索成功").data("total",totalHits);

    }

    @Override
    public Result listArticleNoCache(long current, long limit, Integer state, String name, String categoryId, String begin, String end, String labels, String labelsLike) {

        //创建page对象
        Page<Article> page = new Page<>(current,limit);

        //构建条件
        QueryWrapper<Article> wrapper = new QueryWrapper<>();

        /*String name = articleQuery.getName();
        String categoryId = articleQuery.getCategoryId();
        String begin = articleQuery.getBegin();
        String end = articleQuery.getEnd();*/
        //spring包中的stringUtils工具类

        wrapper.select("id","title","user_id","category_id","type","cover",
                "state","summary","labels","view_count","create_time","update_time");

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

        if (!(state ==null)){
            wrapper.eq("state",state);
        }

        if (!StringUtils.isEmpty(labels)){
            wrapper.like("labels",labels);
        }

        if (!StringUtils.isEmpty(labelsLike)){
            wrapper.like("labels",labelsLike);
        }
        //排序
        wrapper.orderByDesc("update_time");



        articleMapper.selectPage(page, wrapper);
        //long total = page.getTotal();//总记录数
        //List<Article> records = page.getRecords();//
        //保存到redis
        //List<Article> records = articleIPage.getRecords();

        return Result.ok().data("rows",page);
    }


    /**
     * 打散标签到列表
     * @param labels
     */
    private void  setupLabels(String labels){

        ArrayList<String> labelList = new ArrayList<>();
        if (labels.contains("-")) {
            labelList.addAll(Arrays.asList(labels.split("-")));
        }else {
            labelList.add(labels);
        }

        //入库

        for (String label : labelList) {
            /*//找出来
            Labels targetLabel = labelsMapper.selectByName(label);
            if (targetLabel == null) {
                targetLabel=new Labels();
                targetLabel.setCount(0);
                targetLabel.setName(label);
            }
            Integer count = targetLabel.getCount();
            targetLabel.setCount(++count);*/
            int result = labelsMapper.updateCountByName(label);
            if (result==0) {
                Labels targetLabel=new Labels();
                targetLabel.setCount(1);
                targetLabel.setName(label);
                labelsMapper.insert(targetLabel);
            }

        }

    }


}
