package com.form1115.f1115.admin.mapper;

import com.form1115.f1115.common.domain.Post;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理后台 - 帖子Mapper接口
 */
public interface AdminPostMapper {
    
    /**
     * 查询帖子列表（支持搜索和筛选）
     */
    List<Post> selectPostList(@Param("keyword") String keyword,
                              @Param("status") Integer status,
                              @Param("type") Integer type);
    
    /**
     * 删除帖子
     */
    int deletePost(@Param("id") Long id);
    
    /**
     * 更新帖子类型
     */
    int updatePostType(@Param("id") Long id, @Param("type") Integer type);
    
    /**
     * 更新帖子状态
     */
    int updatePostStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 统计帖子总数
     */
    int countTotalPosts();
    
    /**
     * 统计今日新增帖子
     */
    int countTodayNewPosts();
    
    /**
     * 查询热门帖子（点赞数最多的前10）
     */
    List<Post> selectHotPosts();
}
