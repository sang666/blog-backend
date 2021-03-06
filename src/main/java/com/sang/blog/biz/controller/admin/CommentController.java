package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.entity.Comment;
import com.sang.blog.biz.service.CommentService;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@RestController
@RequestMapping("/biz/comment")
public class CommentController {


    @Autowired
    private CommentService commentService;



    @PostMapping("/comment")
    public Result addComment(@RequestBody Comment comment, HttpServletRequest request, HttpServletResponse response){

        return Result.ok();
    }


    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@permission.admin()")
    public Result deleteComment(@PathVariable String id,HttpServletRequest request,HttpServletResponse response) {

        return commentService.deleteCommentById(id,request,response);
    }


    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getComment(@PathVariable String id) {

        return Result.ok();
    }

    /**
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{current}/{limit}")
    @PreAuthorize("@permission.admin()")
    public Result listComment(@PathVariable long current, @PathVariable long limit) {

        return commentService.listComment(current,limit);
    }


    /**
     * 置顶评论接口
     *
     * @param id
     * @return
     */
    @PutMapping("/top/{id}")
    public Result topComment(@PathVariable String id) {


        return commentService.topComment(id);
    }

    @GetMapping("/comment_count")
    public Result getComment(){
        return commentService.getComment();
    }
}
