package com.sang.blog.biz.controller.portal;


import com.sang.blog.biz.entity.Comment;
import com.sang.blog.commom.result.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/protal/comment")
public class CommentPortal {

    /**
     * @param comment
     * @return
     */
    @PostMapping
    public Result addComment(@RequestBody Comment comment) {

        return Result.ok();
    }

    /**
     * @param commentId
     * @return
     */
    @DeleteMapping("/{commentId}")
    public Result deleteComment(@PathVariable String commentId) {

        return Result.ok();
    }

    @GetMapping("/{articleId}")
    public Result listComments(@PathVariable String articleId) {

        return Result.ok();
    }
}
