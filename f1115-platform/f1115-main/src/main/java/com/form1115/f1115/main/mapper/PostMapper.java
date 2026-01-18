package com.form1115.f1115.main.mapper;

import com.form1115.f1115.common.domain.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 帖子Mapper接口
 */
public interface PostMapper {
    
    /**
     * 插入帖子
     */
    int insert(Post post);
    
    /**
     * 根据ID查询帖子
     */
    Post selectById(@Param("id") Long id);
    
    /**
     * 查询帖子列表（分页）
     */
    List<Post> selectList(@Param("status") Integer status);
    
    /**
     * 查询用户发帖列表
     */
    List<Post> selectByUserId(@Param("userId") Long userId, @Param("status") Integer status);
    
    /**
     * 查询首页时间线（关注的人的帖子）
     */
    List<Post> selectTimeline(@Param("userId") Long userId);
    
    /**
     * 更新帖子
     */
    int update(Post post);
    
    /**
     * 删除帖子（逻辑删除）
     */
    int deleteById(@Param("id") Long id);
    
    /**
     * 增加点赞数
     */
    int incrementLikeCount(@Param("id") Long id);
    
    /**
     * 减少点赞数
     */
    int decrementLikeCount(@Param("id") Long id);
    
    /**
     * 增加评论数
     */
    int incrementCommentCount(@Param("id") Long id);
    
    /**
     * 增加浏览量
     */
    int incrementViewCount(@Param("id") Long id);
    
    /**
     * 查询帖子详情（包含用户信息和点赞状态）
     */
    Post selectDetailById(@Param("id") Long id, @Param("currentUserId") Long currentUserId);
    
    /**
     * 统计帖子总数
     */
    int countTotalPosts();
    
    /**
     * 统计今日新增帖子
     */
    int countTodayNewPosts();
    
    /**
     * 查询热门帖子
     */
    List<Post> selectHotPosts();
}
