package com.sang.blog.biz.service;

import com.sang.blog.biz.entity.Friends;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sang.blog.commom.result.Result;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
public interface FriendsService extends IService<Friends> {

    Result addFriendLink(Friends friends);

    Result getFriend(String friendLinkId);

    Result listFriendLind(long current, long limit);

    Result updateFriendLink(String id, Friends friends);

    Result deleteFriendLink(String friendLinkId);
}
