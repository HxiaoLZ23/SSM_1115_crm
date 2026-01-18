package com.form1115.f1115.main.controller.admin;

import com.form1115.f1115.common.domain.UserProfile;
import com.form1115.f1115.common.exception.BusinessException;
import com.form1115.f1115.common.utils.Result;
import com.form1115.f1115.main.mapper.UserMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {
    
    @Autowired
    private UserMapper userMapper;
    
    private static final String SESSION_USER_KEY = "currentUser";
    
    /**
     * 获取用户列表（分页、搜索）
     */
    @GetMapping("/list")
    public Result getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(required = false) String keyword,
                              HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        PageHelper.startPage(pageNum, pageSize);
        // 简化实现：查询所有用户
        List<UserProfile> users = userMapper.selectUserList(keyword);
        PageInfo<UserProfile> pageInfo = new PageInfo<>(users);
        
        return Result.success(pageInfo.getList(), pageInfo.getTotal());
    }
    
    /**
     * 更新用户状态
     */
    @PutMapping("/{userId}/status")
    public Result updateUserStatus(@PathVariable Long userId,
                                   @RequestBody Map<String, Integer> params,
                                   HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Integer status = params.get("status");
        UserProfile user = new UserProfile();
        user.setId(userId);
        user.setStatus(status);
        userMapper.update(user);
        
        return Result.success("操作成功");
    }
    
    /**
     * 删除用户
     */
    @DeleteMapping("/{userId}")
    public Result deleteUser(@PathVariable Long userId, HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        UserProfile user = userMapper.selectById(userId);
        if (user != null && user.getRole() == 1) {
            return Result.error("不能删除管理员账号");
        }
        
        // 简化实现：更新状态为禁用
        UserProfile updateUser = new UserProfile();
        updateUser.setId(userId);
        updateUser.setStatus(0);
        userMapper.update(updateUser);
        
        return Result.success("删除成功");
    }
    
    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    public Result getUserStats(HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userMapper.countTotalUsers());
        stats.put("todayNewUsers", userMapper.countTodayNewUsers());
        stats.put("activeUsers", userMapper.countActiveUsers());
        
        return Result.success(stats);
    }
    
    /**
     * 检查是否是管理员
     */
    private boolean checkAdmin(HttpSession session) {
        UserProfile currentUser = (UserProfile) session.getAttribute(SESSION_USER_KEY);
        return currentUser != null && currentUser.getRole() == 1;
    }
}
