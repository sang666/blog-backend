package com.sang.blog.biz.controller.admin;


import com.sang.blog.biz.entity.Friends;
import com.sang.blog.biz.entity.Images;
import com.sang.blog.biz.service.FriendsService;
import com.sang.blog.commom.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @Autowired
    private FriendsService friendsService;

    /**
     * @param friends
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public Result addFriends(@RequestBody Friends friends) {

        return friendsService.addFriendLink(friends);
    }

    /**
     * @param friendLinkId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{friendLinkId}")
    public Result deleteFriends(@PathVariable("friendLinkId") String friendLinkId) {

        return friendsService.deleteFriendLink(friendLinkId);
    }


    /**
     * @param id
     * @param Friends
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{friendLinkId}")
    public Result updateFriends(@PathVariable("friendLinkId") String id,
                                @RequestBody Friends Friends) {

        return friendsService.updateFriendLink(id,Friends);
    }


    /**
     * @param friendLinkId
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{friendLinkId}")
    public Result getFriends(@PathVariable("friendLinkId") String friendLinkId) {

        return friendsService.getFriend(friendLinkId);
    }

    /**
     * @param
     * @param
     * @return
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public Result listFriends() {

        return friendsService.listFriendLind();
    }

}
