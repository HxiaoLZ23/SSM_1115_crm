package com.form1115.f1115.admin.service;

import com.form1115.f1115.common.domain.UserProfile;
import com.form1115.f1115.common.exception.BusinessException;
import com.form1115.f1115.admin.mapper.AdminUserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 用户服务类
 */
@Service
public class AdminUserService {
    
    @Autowired
    private AdminUserMapper adminUserMapper;
    
    /**
     * 获取用户列表（分页、搜索）
     */
    public PageInfo<UserProfile> getUserList(Integer pageNum, Integer pageSize, 
                                             String keyword, Integer status, Integer role) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserProfile> users = adminUserMapper.selectUserList(keyword, status, role);
        return new PageInfo<>(users);
    }
    
    /**
     * 更新用户状态
     */
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("状态参数错误");
        }
        
        int result = adminUserMapper.updateUserStatus(userId, status);
        if (result <= 0) {
            throw new BusinessException("操作失败");
        }
    }
    
    /**
     * 删除用户
     */
    @Transactional
    public void deleteUser(Long userId) {
        // 不能删除管理员
        UserProfile user = adminUserMapper.selectById(userId);
        if (user != null && user.getRole() == 1) {
            throw new BusinessException("不能删除管理员账号");
        }
        
        int result = adminUserMapper.deleteUser(userId);
        if (result <= 0) {
            throw new BusinessException("删除失败");
        }
    }
    
    /**
     * 修改用户角色
     */
    @Transactional
    public void updateUserRole(Long userId, Integer role) {
        if (role == null || (role != 0 && role != 1 && role != 2)) {
            throw new BusinessException("角色参数错误");
        }
        
        int result = adminUserMapper.updateUserRole(userId, role);
        if (result <= 0) {
            throw new BusinessException("操作失败");
        }
    }
    
    /**
     * 获取用户统计数据
     */
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // 用户总数
        stats.put("totalUsers", adminUserMapper.countTotalUsers());
        
        // 今日新增用户
        stats.put("todayNewUsers", adminUserMapper.countTodayNewUsers());
        
        // 活跃用户数（最近7天有发帖或评论）
        stats.put("activeUsers", adminUserMapper.countActiveUsers());
        
        return stats;
    }
}
