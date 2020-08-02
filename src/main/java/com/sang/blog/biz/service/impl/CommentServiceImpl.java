package com.sang.blog.biz.service.impl;

import com.sang.blog.biz.entity.Comment;
import com.sang.blog.biz.mapper.CommentMapper;
import com.sang.blog.biz.service.CommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sang666
 * @since 2020-07-03
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

}
