package com.form1115.f1115.main.service;

import com.form1115.f1115.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务
 */
@Service
public class FileUploadService {
    
    @Value("${upload.imagePath}")
    private String imagePath;
    
    @Value("${upload.maxFileSize}")
    private Long maxFileSize;
    
    @Value("${upload.allowedImageTypes}")
    private String allowedImageTypes;
    
    /**
     * 上传图片
     */
    public String uploadImage(MultipartFile file) {
        // 1. 验证文件是否为空
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }
        
        // 2. 验证文件大小
        if (file.getSize() > maxFileSize) {
            throw new BusinessException("文件大小不能超过" + (maxFileSize / 1024 / 1024) + "MB");
        }
        
        // 3. 验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            throw new BusinessException("文件名不能为空");
        }
        
        String ext = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedTypes = Arrays.asList(allowedImageTypes.split(","));
        if (!allowedTypes.contains(ext)) {
            throw new BusinessException("不支持的文件类型，仅支持：" + allowedImageTypes);
        }
        
        // 4. 生成唯一文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + "." + ext;
        
        // 5. 获取webapp真实路径
        String realPath = null;
        try {
            // 获取ServletContext来获取真实路径
            javax.servlet.ServletContext servletContext = 
                org.springframework.web.context.ContextLoader.getCurrentWebApplicationContext().getServletContext();
            realPath = servletContext.getRealPath("/");
        } catch (Exception e) {
            // 如果获取失败，使用相对路径
            realPath = "";
        }
        
        // 6. 创建保存目录
        File dir = new File(realPath, imagePath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                throw new BusinessException("创建上传目录失败");
            }
        }
        
        // 7. 保存文件
        File targetFile = new File(dir, fileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
        
        // 8. 返回访问路径
        return "/upload/images/" + fileName;
    }
    
    /**
     * 批量上传图片
     */
    public List<String> uploadImages(MultipartFile[] files) {
        if (files == null || files.length == 0) {
            throw new BusinessException("请选择要上传的文件");
        }
        
        if (files.length > 9) {
            throw new BusinessException("最多只能上传9张图片");
        }
        
        return Arrays.stream(files)
                .map(this::uploadImage)
                .toList();
    }
}
