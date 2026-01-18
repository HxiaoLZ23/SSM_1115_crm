# 视觉模型使用指南

## 📋 概述

F1115平台已集成通义千问视觉模型（qwen-vl-plus），可以理解图片内容并生成相关评论。

---

## ⚠️ 重要：使用条件

### 视觉模型需要公网可访问的图片URL

**为什么？**
- AI服务器在阿里云
- 需要下载图片进行分析
- localhost地址无法访问

---

## 🔧 开发环境 vs 生产环境

### 开发环境（localhost）

**当前状态**：
- ❌ 无法使用视觉模型
- ✅ 使用文本模型降级方案
- ✅ 功能完整可用

**实现方式**：
```java
// 使用文本模型
Generation gen = new Generation();
String prompt = "这是一条帖子，内容是：" + postContent;
if (imageList != null && !imageList.isEmpty()) {
    prompt += "（帖子包含" + imageList.size() + "张图片）";
}
// AI知道有图片，但看不到图片内容
```

**效果**：
- AI知道帖子包含图片
- 生成相关评论
- 只是无法"看到"图片具体内容

### 生产环境（公网部署）

**条件**：
- ✅ 图片部署到公网
- ✅ 图片URL可公开访问

**实现方式**：
```java
// 使用视觉模型
MultiModalConversation conv = new MultiModalConversation();

// 添加公网图片URL
content.add(Collections.singletonMap("image", "https://your-domain.com/upload/xxx.jpg"));
content.add(Collections.singletonMap("text", "请生成评论"));

// 使用视觉模型
param.model("qwen-vl-plus");
```

**效果**：
- AI可以"看到"图片
- 理解图片内容
- 生成更准确的评论

---

## 🚀 如何启用视觉模型

### 步骤1: 配置云存储

#### 方案1: 阿里云OSS

1. **开通OSS服务**
   - 访问：https://oss.console.aliyun.com/
   - 创建Bucket

2. **配置SDK**
   ```xml
   <dependency>
       <groupId>com.aliyun.oss</groupId>
       <artifactId>aliyun-sdk-oss</artifactId>
       <version>3.15.1</version>
   </dependency>
   ```

3. **修改上传服务**
   ```java
   // 上传到OSS
   String url = ossClient.putObject(bucketName, fileName, file.getInputStream());
   return "https://your-bucket.oss-cn-hangzhou.aliyuncs.com/" + fileName;
   ```

#### 方案2: 腾讯云COS

类似阿里云OSS的配置方式。

#### 方案3: 自建服务器

1. 部署到公网服务器
2. 配置Nginx
3. 返回公网URL

### 步骤2: 修改代码

在 `AICommentService.java` 中：

```java
// 恢复视觉模型代码
private String generateCommentWithVisionModel(String postContent, List<String> imageList) throws Exception {
    MultiModalConversation conv = new MultiModalConversation();
    
    List<Map<String, Object>> content = new ArrayList<>();
    
    // 添加图片（现在是公网URL）
    if (imageList != null && !imageList.isEmpty()) {
        String firstImage = imageList.get(0);
        // 直接使用公网URL
        content.add(Collections.singletonMap("image", firstImage));
    }
    
    // 添加文本
    String prompt = "这是一条社交媒体帖子";
    if (postContent != null && !postContent.isEmpty()) {
        prompt += "，内容是：" + postContent;
    }
    prompt += "。请生成一条简短、友好、有趣的评论，不超过50字，可以使用emoji。";
    content.add(Collections.singletonMap("text", prompt));
    
    MultiModalMessage userMessage = MultiModalMessage.builder()
        .role(Role.USER.getValue())
        .content(content)
        .build();
    
    MultiModalConversationParam param = MultiModalConversationParam.builder()
        .apiKey(apiKey)
        .model("qwen-vl-plus")  // 使用视觉模型
        .messages(Arrays.asList(userMessage))
        .build();
    
    MultiModalConversationResult result = conv.call(param);
    return result.getOutput().getChoices().get(0).getMessage().getContent().get(0).get("text").toString();
}
```

### 步骤3: 测试

1. 上传图片到云存储
2. 发布带图片的帖子
3. 等待AI评论
4. 查看AI是否理解了图片内容

---

## 📊 对比效果

### 文本模型（当前）

**用户发帖**：
```
内容："分享一张美丽的风景照片"
图片：[风景照片]
```

**AI评论**：
```
"照片一定很美！期待看到更多分享！📷"
```
（AI知道有图片，但看不到具体内容）

### 视觉模型（生产环境）

**用户发帖**：
```
内容："分享一张美丽的风景照片"
图片：[蓝天白云山景]
```

**AI评论**：
```
"好美的山景！蓝天白云让人心旷神怡，这是在哪里拍的呀？🏔️"
```
（AI能看到图片，评论更准确）

---

## 💡 建议

### 开发阶段
- 使用当前的文本模型方案
- 功能完整，可以正常测试
- 不需要额外配置

### 上线前
- 配置云存储
- 启用视觉模型
- 提升AI评论质量

---

## 🎯 总结

| 环境 | 图片存储 | 模型 | AI能力 | 状态 |
|------|---------|------|--------|------|
| 开发环境 | localhost | 文本模型 | 知道有图片 | ✅ 当前 |
| 生产环境 | 云存储 | 视觉模型 | 理解图片内容 | ⏳ 待配置 |

**当前方案已经可以正常使用，生产环境建议启用视觉模型以获得更好的效果。**

---

**文档创建日期**: 2026-01-17  
**当前模型**: qwen-flash（文本）  
**推荐模型**: qwen-vl-plus（视觉，生产环境）
