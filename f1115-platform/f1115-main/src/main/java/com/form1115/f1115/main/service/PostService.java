package com.form1115.f1115.main.service;

import com.alibaba.fastjson.JSON;
import com.form1115.f1115.common.domain.Post;
import com.form1115.f1115.common.exception.BusinessException;
import com.form1115.f1115.main.mapper.PostMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 帖子服务类
 */
@Service
public class PostService {
    
    @Autowired
    private PostMapper postMapper;
    
    /**
     * 发布帖子
     */
    @Transactional
    public Post createPost(Long userId, String content, List<String> imageList) {
        // 参数校验
        if (content == null || content.trim().isEmpty()) {
            throw new BusinessException("帖子内容不能为空");
        }
        if (content.length() > 5000) {
            throw new BusinessException("帖子内容不能超过5000字");
        }
        
        // 创建帖子对象
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        
        // 处理图片列表
        if (imageList != null && !imageList.isEmpty()) {
            if (imageList.size() > 9) {
                throw new BusinessException("最多只能上传9张图片");
            }
            post.setImages(JSON.toJSONString(imageList));
        }
        
        post.setType(0); // 普通帖子
        post.setStatus(1); // 正常状态
        
        // 插入数据库
        int result = postMapper.insert(post);
        if (result <= 0) {
            throw new BusinessException("发布失败");
        }
        
        return post;
    }
    
    /**
     * 获取帖子详情
     */
    public Post getPostDetail(Long postId, Long currentUserId) {
        Post post = postMapper.selectDetailById(postId, currentUserId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        
        // 解析图片列表
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            List<String> imageList = JSON.parseArray(post.getImages(), String.class);
            post.setImageList(imageList);
        }
        
        // 增加浏览量
        postMapper.incrementViewCount(postId);
        
        return post;
    }
    
    /**
     * 获取帖子列表（分页）
     */
    public PageInfo<Post> getPostList(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Post> posts = postMapper.selectList(1); // 只查询正常状态的帖子
        
        // 解析图片列表
        for (Post post : posts) {
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                List<String> imageList = JSON.parseArray(post.getImages(), String.class);
                post.setImageList(imageList);
            }
        }
        
        return new PageInfo<>(posts);
    }
    
    /**
     * 获取用户发帖列表（分页）
     */
    public PageInfo<Post> getUserPosts(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Post> posts = postMapper.selectByUserId(userId, 1);
        
        // 解析图片列表
        for (Post post : posts) {
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                List<String> imageList = JSON.parseArray(post.getImages(), String.class);
                post.setImageList(imageList);
            }
        }
        
        return new PageInfo<>(posts);
    }
    
    /**
     * 获取首页时间线（分页）
     */
    public PageInfo<Post> getTimeline(Long userId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Post> posts = postMapper.selectTimeline(userId);
        
        // 解析图片列表
        for (Post post : posts) {
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                List<String> imageList = JSON.parseArray(post.getImages(), String.class);
                post.setImageList(imageList);
            }
        }
        
        return new PageInfo<>(posts);
    }
    
    /**
     * 删除帖子
     */
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("帖子不存在");
        }
        
        // 只能删除自己的帖子
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException("无权删除该帖子");
        }
        
        int result = postMapper.deleteById(postId);
        if (result <= 0) {
            throw new BusinessException("删除失败");
        }
    }
}
