package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Article;
import com.sang.blog.biz.entity.Comment;
import com.sang.blog.commom.result.Result;
import org.springframework.web.bind.annotation.*;

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


    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteComment(@PathVariable String id) {

        return Result.ok();
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
    public Result listComment(@PathVariable long current, @PathVariable long limit) {

        return Result.ok();
    }


    /**
     * 置顶评论接口
     *
     * @param id
     * @return
     */
    @PutMapping("/top/{id}")
    public Result topComment(@PathVariable String id) {


        return Result.ok();
    }
}