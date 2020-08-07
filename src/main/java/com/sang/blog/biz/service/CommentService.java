package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sang.blog.commom.result.Result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
public interface CommentService extends IService<Comment> {

    Result addComment(Comment comment, HttpServletRequest request, HttpServletResponse response);

    Result listCommentById(String articleId, long current, long limit);

    Result deleteCommentById(String commentId, HttpServletRequest request, HttpServletResponse response);
}
