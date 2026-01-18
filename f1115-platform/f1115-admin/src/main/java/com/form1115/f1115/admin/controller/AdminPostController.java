package com.form1115.f1115.admin.controller;

import com.form1115.f1115.common.domain.Post;
import com.form1115.f1115.common.domain.UserProfile;
import com.form1115.f1115.common.utils.Result;
import com.form1115.f1115.admin.service.AdminPostService;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 管理后台 - 帖子管理控制器
 */
@RestController
@RequestMapping("/api/admin/post")
public class AdminPostController {
    
    @Autowired
    private AdminPostService adminPostService;
    
    private static final String SESSION_USER_KEY = "currentUser";
    
    /**
     * 获取帖子列表（分页、搜索）
     */
    @GetMapping("/list")
    public Result getPostList(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              @RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Integer status,
                              @RequestParam(required = false) Integer type,
                              HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        PageInfo<Post> pageInfo = adminPostService.getPostList(pageNum, pageSize, keyword, status, type);
        
        return Result.success(pageInfo.getList(), pageInfo.getTotal());
    }
    
    /**
     * 删除帖子
     */
    @DeleteMapping("/{postId}")
    public Result deletePost(@PathVariable Long postId, HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        adminPostService.deletePost(postId);
        
        return Result.success("删除成功");
    }
    
    /**
     * 置顶/取消置顶帖子
     */
    @PutMapping("/{postId}/top")
    public Result toggleTop(@PathVariable Long postId,
                           @RequestBody Map<String, Integer> params,
                           HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Integer type = params.get("type");
        adminPostService.updatePostType(postId, type);
        
        return Result.success(type == 1 ? "置顶成功" : "取消置顶成功");
    }
    
    /**
     * 更新帖子状态
     */
    @PutMapping("/{postId}/status")
    public Result updatePostStatus(@PathVariable Long postId,
                                   @RequestBody Map<String, Integer> params,
                                   HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Integer status = params.get("status");
        adminPostService.updatePostStatus(postId, status);
        
        return Result.success("操作成功");
    }
    
    /**
     * 获取帖子统计数据
     */
    @GetMapping("/stats")
    public Result getPostStats(HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Map<String, Object> stats = adminPostService.getPostStats();
        
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
