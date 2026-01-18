package com.form1115.f1115.admin.service;

import com.alibaba.fastjson.JSON;
import com.form1115.f1115.common.domain.Post;
import com.form1115.f1115.common.exception.BusinessException;
import com.form1115.f1115.admin.mapper.AdminPostMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 帖子服务类
 */
@Service
public class AdminPostService {
    
    @Autowired
    private AdminPostMapper adminPostMapper;
    
    /**
     * 获取帖子列表（分页、搜索）
     */
    public PageInfo<Post> getPostList(Integer pageNum, Integer pageSize,
                                      String keyword, Integer status, Integer type) {
        PageHelper.startPage(pageNum, pageSize);
        List<Post> posts = adminPostMapper.selectPostList(keyword, status, type);
        
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
    public void deletePost(Long postId) {
        int result = adminPostMapper.deletePost(postId);
        if (result <= 0) {
            throw new BusinessException("删除失败");
        }
    }
    
    /**
     * 更新帖子类型（置顶）
     */
    @Transactional
    public void updatePostType(Long postId, Integer type) {
        if (type == null || (type != 0 && type != 1 && type != 2)) {
            throw new BusinessException("类型参数错误");
        }
        
        int result = adminPostMapper.updatePostType(postId, type);
        if (result <= 0) {
            throw new BusinessException("操作失败");
        }
    }
    
    /**
     * 更新帖子状态
     */
    @Transactional
    public void updatePostStatus(Long postId, Integer status) {
        if (status == null || (status < 0 || status > 3)) {
            throw new BusinessException("状态参数错误");
        }
        
        int result = adminPostMapper.updatePostStatus(postId, status);
        if (result <= 0) {
            throw new BusinessException("操作失败");
        }
    }
    
    /**
     * 获取帖子统计数据
     */
    public Map<String, Object> getPostStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 帖子总数
        stats.put("totalPosts", adminPostMapper.countTotalPosts());
        
        // 今日新增帖子
        stats.put("todayNewPosts", adminPostMapper.countTodayNewPosts());
        
        // 热门帖子（点赞数最多的前10）
        stats.put("hotPosts", adminPostMapper.selectHotPosts());
        
        return stats;
    }
}
