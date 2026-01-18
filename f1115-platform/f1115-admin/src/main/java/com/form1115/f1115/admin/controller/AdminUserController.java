package com.form1115.f1115.admin.controller;

import com.form1115.f1115.common.domain.UserProfile;
import com.form1115.f1115.common.utils.Result;
import com.form1115.f1115.admin.service.AdminUserService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 管理后台 - 用户管理控制器
 */
@RestController
@RequestMapping("/api/admin/user")
public class AdminUserController {
    
    @Autowired
    private AdminUserService adminUserService;
    
    private static final String SESSION_USER_KEY = "currentUser";
    
    /**
     * 获取用户列表（分页、搜索）
     */
    @GetMapping("/list")
    public Result getUserList(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(required = false) Integer role,
                              HttpSession session) {
        // 验证管理员权限
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        PageInfo<UserProfile> pageInfo = adminUserService.getUserList(pageNum, pageSize, keyword, status, role);
        
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
        adminUserService.updateUserStatus(userId, status);
        
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
        
        adminUserService.deleteUser(userId);
        
        return Result.success("删除成功");
    }
    
    /**
     * 修改用户角色
     */
    @PutMapping("/{userId}/role")
    public Result updateUserRole(@PathVariable Long userId,
                                 @RequestBody Map<String, Integer> params,
                                 HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Integer role = params.get("role");
        adminUserService.updateUserRole(userId, role);
        
        return Result.success("操作成功");
    }
    
    /**
     * 获取用户统计数据
     */
    @GetMapping("/stats")
    public Result getUserStats(HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Map<String, Object> stats = adminUserService.getUserStats();
        
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
