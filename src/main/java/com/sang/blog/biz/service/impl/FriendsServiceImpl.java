package com.sang.blog.biz.service.impl;

import com.sang.blog.biz.entity.Friends;
import com.sang.blog.biz.mapper.FriendsMapper;
import com.sang.blog.biz.service.FriendsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sang.blog.commom.result.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class FriendsServiceImpl extends ServiceImpl<FriendsMapper, Friends> implements FriendsService {

    @Autowired
    private FriendsMapper friendsMapper;
    /**
     * 添加友情连接
     * @param friends
     * @return
     */
    @Override
    public Result addFriendLink(Friends friends) {
        String url = friends.getUrl();
        if (StringUtils.isEmpty(url)) {
            return Result.err().message("链接不能为空");
        }
        String logo = friends.getLogo();

        if (StringUtils.isEmpty(logo)) {
            return  Result.err().message("logo不能为空");
        }

        String name = friends.getName();
        if (StringUtils.isEmpty(name)) {
            return Result.err().message("名字不能为空");
        }
        friends.setOrder(1);
        friendsMapper.insert(friends);

        return Result.ok().message("创建友情链接成功");
    }

    /**
     * 得到友情链接
     * @param friendLinkId
     * @return
     */
    @Override
    public Result getFriend(String friendLinkId) {
        Friends selectById = friendsMapper.selectById(friendLinkId);
        if (selectById == null) {
            return  Result.err().message("友情链接不存在");

        }

        return Result.ok().data("selectById",friendLinkId).message("查询友情链接成功");
    }

    /**
     * 获取有脸列表
     * @return
     */
    @Override
    public Result listFriendLind() {

        /*Page<Friends> page = new Page<>(current,limit);
        friendsMapper.selectPage(page,null);
        long total = page.getTotal();//总记录数
        List<Friends> records = page.getRecords();*/
        //进行查询
        List<Friends> friends = friendsMapper.selectList(null);
        return Result.ok().message("获取友链列表成功").data("item",friends);
    }

    /**
     * 更新
     * @param id
     * @param friends
     * @return
     */
    @Override
    public Result updateFriendLink(String id, Friends friends) {
        Friends selectById = friendsMapper.selectById(id);
        if (selectById == null) {
            return Result.err().message("要修改的友链不存在");
        }

        String name = friends.getName();
        if (!StringUtils.isEmpty(name)) {
            selectById.setName(name);
        }
        String logo = friends.getLogo();
        if (!StringUtils.isEmpty(logo)) {
            selectById.setLogo(logo);
        }
        String url = friends.getUrl();
        if (!StringUtils.isEmpty(url)) {
            selectById.setUrl(url);
        }
        selectById.setOrder(friends.getOrder());

        friendsMapper.updateById(selectById);

        return Result.ok().message("更新友链成功");
    }

    /**
     * 删除友链
     * @param friendLinkId
     * @return
     */
    @Override
    public Result deleteFriendLink(String friendLinkId) {

        int deleteById = friendsMapper.deleteById(friendLinkId);
        if (deleteById==0) {
            return Result.err().message("该友链不存在");
        }
        return Result.ok().message("删除友链成功");
    }
}
