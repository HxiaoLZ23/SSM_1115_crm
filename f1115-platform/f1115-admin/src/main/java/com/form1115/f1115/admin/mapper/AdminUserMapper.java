package com.form1115.f1115.admin.mapper;

import com.form1115.f1115.common.domain.UserProfile;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 管理后台 - 用户Mapper接口
 */
public interface AdminUserMapper {
    
    /**
     * 查询用户列表（支持搜索和筛选）
     */
    List<UserProfile> selectUserList(@Param("keyword") String keyword,
                                     @Param("status") Integer status,
                                     @Param("role") Integer role);
    
    /**
     * 根据ID查询用户
     */
    UserProfile selectById(@Param("id") Long id);
    
    /**
     * 更新用户状态
     */
    int updateUserStatus(@Param("id") Long id, @Param("status") Integer status);
    
    /**
     * 删除用户
     */
    int deleteUser(@Param("id") Long id);
    
    /**
     * 更新用户角色
     */
    int updateUserRole(@Param("id") Long id, @Param("role") Integer role);
    
    /**
     * 统计用户总数
     */
    int countTotalUsers();
    
    /**
     * 统计今日新增用户
     */
    int countTodayNewUsers();
    
    /**
     * 统计活跃用户数
     */
    int countActiveUsers();
}
