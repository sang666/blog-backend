package com.sang.blog.biz.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.entity.Comment;
import com.sang.blog.biz.entity.User;
import com.sang.blog.biz.mapper.ArticleMapper;
import com.sang.blog.biz.mapper.CommentMapper;
import com.sang.blog.biz.service.ArticleService;
import com.sang.blog.biz.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sang.blog.biz.service.UserService;
import com.sang.blog.commom.result.Result;
import com.sang.blog.commom.utils.Constants;
import com.sang.blog.commom.utils.RedisUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

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
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {



    @Autowired
    private UserService userService;
    @Autowired
    private ArticleMapper articleMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private ArticleService articleService;
    /**
     * 添加评论
     * @param comment
     * @param request
     * @param response
     * @return
     */
    @Override
    public Result addComment(Comment comment, HttpServletRequest request, HttpServletResponse response) {

        //检查是否登录
        User checkUser = userService.checkUser(request, response);
        if (checkUser == null) {
            return Result.accountNotLogin();
        }
        String articleId = comment.getArticleId();
        if (StringUtils.isEmpty(articleId)) {
            return Result.err().message("文章id不能为空");
        }

        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        wrapper.eq("id",articleId);
        wrapper.select("id","title","user_id","category_id","type","cover",
                "state","summary","labels","view_count","create_time","update_time");
        Article article = articleMapper.selectOne(wrapper);

        if (article == null) {
            return Result.err().message("文章不存在");
        }
        String content = comment.getContent();
        if (StringUtils.isEmpty(content)) {
            return Result.err().message("评论内容不可以为空");
        }
        //补全内容
        comment.setUserAvatar(checkUser.getAvatar());
        comment.setUserName(checkUser.getUserName());
        comment.setUserId(checkUser.getId());
        //保存
        commentMapper.insert(comment);
        //发邮件通知
        //todo:发邮件通知不做了，感觉太蠢了2333,单人博客属实没必要

        return Result.ok().message("评论成功");
    }

    /**
     * 获取评论列表
     * @param articleId
     * @param current
     * @param limit
     * @return
     */
    @Override
    public Result listCommentById(String articleId, long current, long limit) {


        Page<Comment> page = new Page<>(current,limit);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();

        wrapper.eq("article_id",articleId);
        wrapper.orderByDesc("create_time");
        commentMapper.selectPage(page,wrapper);

        long total = page.getTotal();
        List<Comment> records = page.getRecords();


        return Result.ok().message("获取评论列表成功").data("total",total).data("rows",records);
    }


    /**
     * 删除评论
     *
     * @param commentId
     * @param request
     * @param response
     * @return
     */
    @Override
    public Result deleteCommentById(String commentId, HttpServletRequest request, HttpServletResponse response) {

        //检查用户角色
        User checkUser = userService.checkUser(request, response);
        if (checkUser == null) {
            return Result.accountNotLogin();
        }
        //把评论找出来
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            return Result.err().message("评论不存在");
        }
        if (checkUser.getId().equals(comment.getUserId()) || Constants.user.ROLE_ADMIN.equals(checkUser.getRoles()) ) {
            //与用户id一样，说明次评论是当前用户
            commentMapper.deleteById(commentId);
            return Result.ok().message("评论删除成功");
        }else {
            return Result.PERMISSION_FORBID();
        }

    }


    /**
     * 管理员，列出全部的评论
     * @param current
     * @param limit
     * @return
     */
    @Override
    public Result listComment(long current, long limit) {
        Page<Comment> page = new Page<>(current, limit);
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        commentMapper.selectPage(page,wrapper);


        return Result.ok().message("管理员获取评论列表成功").data("page",page);
    }

    /**
     * 置顶
     * @param id
     * @return
     */
    @Override
    public Result topComment(String id) {
        Comment comment = commentMapper.selectById(id);
        if (comment == null) {
            return Result.err().message("评论不存在");
        }
        Integer state = comment.getState();
        if (Constants.Comment.STATE_PUBLISH.equals(state)) {
            comment.setState(Constants.Comment.STATE_TOP);
            commentMapper.updateById(comment);
            return Result.ok().message("置顶成功");
        }else if (Constants.Comment.STATE_TOP.equals(state)){
            comment.setState(Constants.Comment.STATE_PUBLISH);
            commentMapper.updateById(comment);
            return Result.ok().message("取消置顶");
        }else {
            return  Result.err().message("评论状态非法");
        }


    }

    /**
     * 评论总数
     * @return
     */
    @Override
    public Result getComment() {

        long count = commentMapper.selectCount(null);

        return Result.ok().data("count",count).message("评论总数获取成功");
    }
}
