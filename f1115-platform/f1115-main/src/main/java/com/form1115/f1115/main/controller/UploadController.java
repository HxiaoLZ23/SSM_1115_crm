package com.form1115.f1115.main.controller;

import com.form1115.f1115.common.domain.UserProfile;
import com.form1115.f1115.common.utils.Result;
import com.form1115.f1115.main.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/upload")
public class UploadController {
    
    @Autowired
    private FileUploadService fileUploadService;
    
    private static final String SESSION_USER_KEY = "currentUser";
    
    /**
     * 上传单张图片
     */
    @PostMapping("/image")
    public Result uploadImage(@RequestParam("file") MultipartFile file, HttpSession session) {
        UserProfile currentUser = (UserProfile) session.getAttribute(SESSION_USER_KEY);
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        String url = fileUploadService.uploadImage(file);
        
        return Result.success("上传成功", url);
    }
    
    /**
     * 批量上传图片
     */
    @PostMapping("/images")
    public Result uploadImages(@RequestParam("files") MultipartFile[] files, HttpSession session) {
        UserProfile currentUser = (UserProfile) session.getAttribute(SESSION_USER_KEY);
        if (currentUser == null) {
            return Result.unauthorized();
        }
        
        List<String> urls = fileUploadService.uploadImages(files);
        
        return Result.success("上传成功", urls);
    }
}
