package com.sang.blog.biz.controller.portal;


import com.sang.blog.biz.entity.Comment;
import com.sang.blog.biz.service.CommentService;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/portal/comment")
public class CommentPortalController {
    @Autowired
    private CommentService commentService;

    /**
     *
     * 添加评论
     * @param comment
     * @return
     */
    @PostMapping("/comment")
    public Result addComment(@RequestBody Comment comment, HttpServletRequest request, HttpServletResponse response){

        return commentService.addComment(comment,request,response);
    }

    /**
     * @param commentId
     * @return
     */
    @DeleteMapping("/{commentId}")
    public Result deleteComment(@PathVariable String commentId,
                                HttpServletRequest request,HttpServletResponse response) {

        return commentService.deleteCommentById(commentId,request,response);
    }

    @GetMapping("/{articleId}/{current}/{limit}")
    public Result listComments(@PathVariable String articleId,@PathVariable long current,@PathVariable long limit) {

        return commentService.listCommentById(articleId,current,limit);
    }
}
