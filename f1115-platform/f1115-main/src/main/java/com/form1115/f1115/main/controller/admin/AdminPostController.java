package com.form1115.f1115.main.controller.admin;

import com.alibaba.fastjson.JSON;
import com.form1115.f1115.common.domain.Post;
import com.form1115.f1115.common.domain.UserProfile;
import com.form1115.f1115.common.utils.Result;
import com.form1115.f1115.main.mapper.PostMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理后台 - 帖子管理控制器
 */
@RestController
@RequestMapping("/api/admin/post")
public class AdminPostController {
    
    @Autowired
    private PostMapper postMapper;
    
    private static final String SESSION_USER_KEY = "currentUser";
    
    /**
     * 获取帖子列表
     */
    @GetMapping("/list")
    public Result getPostList(@RequestParam(defaultValue = "1") Integer pageNum,
                              @RequestParam(defaultValue = "10") Integer pageSize,
                              HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<Post> posts = postMapper.selectList(null); // null表示查询所有状态
        
        // 解析图片列表
        for (Post post : posts) {
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                List<String> imageList = JSON.parseArray(post.getImages(), String.class);
                post.setImageList(imageList);
            }
        }
        
        PageInfo<Post> pageInfo = new PageInfo<>(posts);
        
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
        
        postMapper.deleteById(postId);
        
        return Result.success("删除成功");
    }
    
    /**
     * 置顶/取消置顶
     */
    @PutMapping("/{postId}/top")
    public Result toggleTop(@PathVariable Long postId,
                           @RequestBody Map<String, Integer> params,
                           HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Integer type = params.get("type");
        Post post = new Post();
        post.setId(postId);
        post.setType(type);
        postMapper.update(post);
        
        return Result.success(type == 1 ? "置顶成功" : "取消置顶成功");
    }
    
    /**
     * 获取帖子统计
     */
    @GetMapping("/stats")
    public Result getPostStats(HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPosts", postMapper.countTotalPosts());
        stats.put("todayNewPosts", postMapper.countTodayNewPosts());
        stats.put("hotPosts", postMapper.selectHotPosts());
        
        return Result.success(stats);
    }
    
    private boolean checkAdmin(HttpSession session) {
        UserProfile currentUser = (UserProfile) session.getAttribute(SESSION_USER_KEY);
        return currentUser != null && currentUser.getRole() == 1;
    }
}
