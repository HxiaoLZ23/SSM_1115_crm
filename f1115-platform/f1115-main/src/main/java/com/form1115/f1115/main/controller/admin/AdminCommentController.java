package com.form1115.f1115.main.controller.admin;

import com.form1115.f1115.common.domain.Comment;
import com.form1115.f1115.common.domain.UserProfile;
import com.form1115.f1115.common.utils.Result;
import com.form1115.f1115.main.mapper.CommentMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 管理后台 - 评论管理控制器
 */
@RestController
@RequestMapping("/api/admin/comment")
public class AdminCommentController {
    
    @Autowired
    private CommentMapper commentMapper;
    
    private static final String SESSION_USER_KEY = "currentUser";
    
    /**
     * 获取评论列表
     */
    @GetMapping("/list")
    public Result getCommentList(@RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "10") Integer pageSize,
                                 HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> comments = commentMapper.selectAllComments();
        PageInfo<Comment> pageInfo = new PageInfo<>(comments);
        
        return Result.success(pageInfo.getList(), pageInfo.getTotal());
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Result deleteComment(@PathVariable Long commentId, HttpSession session) {
        if (!checkAdmin(session)) {
            return Result.forbidden();
        }
        
        commentMapper.deleteById(commentId);
        
        return Result.success("删除成功");
    }
    
    private boolean checkAdmin(HttpSession session) {
        UserProfile currentUser = (UserProfile) session.getAttribute(SESSION_USER_KEY);
        return currentUser != null && currentUser.getRole() == 1;
    }
}
