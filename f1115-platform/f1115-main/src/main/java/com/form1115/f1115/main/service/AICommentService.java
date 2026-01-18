package com.form1115.f1115.main.service;

import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.fastjson.JSON;
import com.form1115.f1115.common.domain.Comment;
import com.form1115.f1115.common.domain.Post;
import com.form1115.f1115.main.mapper.CommentMapper;
import com.form1115.f1115.main.mapper.PostMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * AI评论服务
 */
@Service
public class AICommentService {
    
    private static final Logger logger = LoggerFactory.getLogger(AICommentService.class);
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private CommentService commentService;
    
    @Value("${dashscope.apiKey}")
    private String apiKey;
    
    @Value("${dashscope.model}")
    private String model;
    
    private static final Long AI_USER_ID = 2L;
    private static final String VL_MODEL = "qwen-vl-plus";  // 视觉模型
    
    /**
     * AI对新发布的帖子进行评论（统一使用视觉模型）
     */
    @Transactional
    public void commentOnNewPost(Long postId) {
        try {
            // 获取帖子详情
            Post post = postMapper.selectById(postId);
            if (post == null || post.getUserId().equals(AI_USER_ID)) {
                return; // 不评论AI自己的帖子
            }
            
            // 统一使用视觉模型生成评论
            List<String> imageList = null;
            if (post.getImages() != null && !post.getImages().isEmpty()) {
                imageList = JSON.parseArray(post.getImages(), String.class);
            }
            
            String commentContent = generateCommentWithVisionModel(post.getContent(), imageList);
            
            // 发布评论
            commentService.createComment(postId, AI_USER_ID, commentContent, null);
            
            logger.info("AI评论成功 - 帖子ID:{}", postId);
            
        } catch (Exception e) {
            logger.error("AI评论失败 - 帖子ID:{}", postId, e);
        }
    }
    
    /**
     * AI回复评论（当有人回复AI的评论时）
     * 保持对话连贯性，传入帖子内容和对话历史
     */
    @Transactional
    public void replyToComment(Long commentId) {
        try {
            // 获取评论详情
            Comment comment = commentMapper.selectById(commentId);
            if (comment == null) {
                return;
            }
            
            // 检查是否是回复AI的评论
            if (comment.getParentId() != null) {
                Comment parentComment = commentMapper.selectById(comment.getParentId());
                if (parentComment != null && parentComment.getUserId().equals(AI_USER_ID)) {
                    // 获取帖子详情（包含图片）
                    Post post = postMapper.selectById(comment.getPostId());
                    
                    // 构建对话上下文
                    String context = buildConversationContext(post, parentComment, comment);
                    
                    // 获取图片列表
                    List<String> imageList = null;
                    if (post.getImages() != null && !post.getImages().isEmpty()) {
                        imageList = JSON.parseArray(post.getImages(), String.class);
                    }
                    
                    // 使用视觉模型生成回复（保持上下文）
                    String replyContent = generateReplyWithContext(context, imageList);
                    
                    // 发布回复
                    commentService.createComment(comment.getPostId(), AI_USER_ID, replyContent, commentId);
                    
                    logger.info("AI回复评论成功 - 评论ID:{}", commentId);
                }
            }
            
        } catch (Exception e) {
            logger.error("AI回复评论失败 - 评论ID:{}", commentId, e);
        }
    }
    
    /**
     * 构建对话上下文
     */
    private String buildConversationContext(Post post, Comment aiComment, Comment userReply) {
        StringBuilder context = new StringBuilder();
        context.append("【帖子内容】").append(post.getContent()).append("\n\n");
        context.append("【AI的评论】").append(aiComment.getContent()).append("\n\n");
        context.append("【用户的回复】").append(userReply.getContent());
        return context.toString();
    }
    
    /**
     * 统一使用视觉模型生成评论（支持图片和纯文本）
     * 注意：由于本地图片AI无法访问，暂时只处理文本
     */
    private String generateCommentWithVisionModel(String postContent, List<String> imageList) throws Exception {
        // 由于本地图片AI服务器无法访问，暂时使用文本模型
        // 生产环境中，图片应该部署到公网可访问的地址
        
        Generation gen = new Generation();
        
        String prompt = "这是一条社交媒体帖子";
        if (postContent != null && !postContent.isEmpty()) {
            prompt += "，内容是：" + postContent;
        }
        if (imageList != null && !imageList.isEmpty()) {
            prompt += "（帖子包含" + imageList.size() + "张图片）";
        }
        prompt += "。请生成一条简短、友好、有趣的评论，不超过50字，可以使用emoji。";
        
        Message systemMsg = Message.builder()
            .role(Role.SYSTEM.getValue())
            .content("你是一个友好的社交媒体用户，请生成简短、友好、有趣的评论。")
            .build();
            
        Message userMsg = Message.builder()
            .role(Role.USER.getValue())
            .content(prompt)
            .build();
        
        GenerationParam param = GenerationParam.builder()
            .model(model)
            .messages(Arrays.asList(systemMsg, userMsg))
            .resultFormat(GenerationParam.ResultFormat.MESSAGE)
            .apiKey(apiKey)
            .build();
        
        GenerationResult result = gen.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }
    
    /**
     * 生成回复（包含完整对话上下文）
     * 注意：暂时使用文本模型，保持对话连贯性
     */
    private String generateReplyWithContext(String conversationContext, List<String> imageList) throws Exception {
        Generation gen = new Generation();
        
        String prompt = conversationContext;
        if (imageList != null && !imageList.isEmpty()) {
            prompt += "\n（原帖包含图片）";
        }
        prompt += "\n\n请根据以上对话内容，生成一个简短、友好、连贯的回复。" +
                "回复要自然，不超过30字，可以使用emoji。";
        
        Message systemMsg = Message.builder()
            .role(Role.SYSTEM.getValue())
            .content("你是一个友好的AI助手，请根据对话上下文生成连贯的回复。")
            .build();
            
        Message userMsg = Message.builder()
            .role(Role.USER.getValue())
            .content(prompt)
            .build();
        
        GenerationParam param = GenerationParam.builder()
            .model(model)
            .messages(Arrays.asList(systemMsg, userMsg))
            .resultFormat(GenerationParam.ResultFormat.MESSAGE)
            .apiKey(apiKey)
            .build();
        
        GenerationResult result = gen.call(param);
        return result.getOutput().getChoices().get(0).getMessage().getContent();
    }
}
