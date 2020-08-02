package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Friends;
import com.sang.blog.biz.entity.Images;
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
@RequestMapping("/biz/friends")
public class FriendsController {

    /**
     * @param friends
     * @return
     */
    @PostMapping
    public Result addFriends(@RequestBody Friends friends) {

        return Result.ok();
    }

    /**
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public Result deleteFriends(@PathVariable String id) {

        return Result.ok();
    }


    /**
     * @param id
     * @param Friends
     * @return
     */
    @PutMapping("/{id}")
    public Result updateFriends(@PathVariable String id, @RequestBody Friends Friends) {

        return Result.ok();
    }


    /**
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result getFriends(@PathVariable String id) {

        return Result.ok();
    }

    /**
     * @param current
     * @param limit
     * @return
     */
    @GetMapping("/list/{current}/{limit}")
    public Result listFriends(@PathVariable String current, @PathVariable String limit) {

        return Result.ok();
    }

}
