<template>
  <div class="post-detail-container">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header>
        <div class="header-content">
          <el-button @click="router.back()" icon="ArrowLeft">返回</el-button>
          <h1>帖子详情</h1>
          <div></div>
        </div>
      </el-header>
      
      <!-- 主内容区 -->
      <el-main v-if="post">
        <el-card class="post-card">
          <!-- 帖子头部 -->
          <div class="post-header">
            <el-avatar 
              :src="getImageUrl(post.user?.avatar)" 
              :size="50"
              class="clickable-avatar"
              @click="goToUserProfile(post.userId)"
            >
              {{ post.user?.nickname?.[0] || post.user?.username?.[0] }}
            </el-avatar>
            <div class="user-info">
              <div class="nickname clickable-name" @click="goToUserProfile(post.userId)">
                {{ post.user?.nickname || post.user?.username }}
              </div>
              <div class="time">{{ formatTime(post.createTime) }}</div>
            </div>
          </div>
          
          <!-- 帖子内容 -->
          <div class="post-content">
            {{ post.content }}
          </div>
          
          <!-- 帖子图片 -->
          <div v-if="post.imageList && post.imageList.length > 0" class="post-images">
            <el-image
              v-for="(img, index) in post.imageList"
              :key="index"
              :src="getImageUrl(img)"
              :preview-src-list="post.imageList.map(i => getImageUrl(i))"
              :initial-index="index"
              fit="cover"
              class="post-image"
            />
          </div>
          
          <!-- 帖子统计 -->
          <div class="post-stats">
            <el-space :size="20">
              <span>
                <el-icon><View /></el-icon>
                {{ post.viewCount }} 浏览
              </span>
              <span>
                <el-icon><Star /></el-icon>
                {{ post.likeCount }} 点赞
              </span>
              <span>
                <el-icon><ChatDotRound /></el-icon>
                {{ post.commentCount }} 评论
              </span>
            </el-space>
          </div>
          
          <!-- 操作按钮 -->
          <div class="post-actions">
            <el-button type="primary" :icon="post.isLiked ? 'StarFilled' : 'Star'">
              {{ post.isLiked ? '已点赞' : '点赞' }}
            </el-button>
            <el-button icon="ChatDotRound">评论</el-button>
          </div>
        </el-card>
        
        <!-- 评论区域 -->
        <el-card class="comment-section">
          <template #header>
            <div class="section-header">
              <span>评论 ({{ post.commentCount }})</span>
            </div>
          </template>
          
          <el-empty description="评论功能开发中..." />
        </el-card>
      </el-main>
      
      <el-main v-else>
        <el-skeleton :rows="10" animated />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPostDetail } from '@/api/post'

const router = useRouter()
const route = useRoute()

const post = ref(null)

// 格式化时间
const formatTime = (time) => {
  if (!time) return ''
  const date = new Date(time)
  const now = new Date()
  const diff = now - date
  
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return Math.floor(diff / 60000) + '分钟前'
  if (diff < 86400000) return Math.floor(diff / 3600000) + '小时前'
  if (diff < 2592000000) return Math.floor(diff / 86400000) + '天前'
  
  return date.toLocaleDateString()
}

// 获取图片完整URL
const getImageUrl = (path) => {
  if (!path) return ''
  // 如果是完整URL，直接返回
  if (path.startsWith('http://') || path.startsWith('https://')) {
    return path
  }
  // 如果是相对路径，添加后端服务器地址
  if (path.startsWith('/')) {
    return 'http://localhost:8080' + path
  }
  return 'http://localhost:8080/' + path
}

// 跳转到用户主页
const goToUserProfile = (userId) => {
  router.push(`/user/${userId}`)
}

// 加载帖子详情
const loadPostDetail = async () => {
  try {
    const postId = route.params.postId
    const res = await getPostDetail(postId)
    if (res.code === 200) {
      post.value = res.data
    }
  } catch (error) {
    ElMessage.error('加载帖子失败')
    router.back()
  }
}

onMounted(() => {
  loadPostDetail()
})
</script>

<style scoped>
.post-detail-container {
  min-height: 100vh;
  background-color: #f5f5f5;
}

.el-header {
  background-color: #fff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 100%;
}

.header-content h1 {
  margin: 0;
  font-size: 20px;
  color: #303133;
}

.el-main {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.post-card {
  margin-bottom: 20px;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.user-info {
  margin-left: 15px;
}

.nickname {
  font-weight: bold;
  font-size: 16px;
  color: #303133;
}

.clickable-avatar {
  cursor: pointer;
  transition: transform 0.2s;
}

.clickable-avatar:hover {
  transform: scale(1.1);
}

.clickable-name {
  cursor: pointer;
  transition: color 0.2s;
}

.clickable-name:hover {
  color: #409eff;
}

.time {
  font-size: 13px;
  color: #909399;
  margin-top: 5px;
}

.post-content {
  margin: 20px 0;
  line-height: 1.8;
  font-size: 15px;
  color: #303133;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin: 20px 0;
}

.post-image {
  width: 100%;
  height: 200px;
  border-radius: 8px;
  cursor: pointer;
}

.post-stats {
  padding: 15px 0;
  border-top: 1px solid #ebeef5;
  border-bottom: 1px solid #ebeef5;
  color: #909399;
  font-size: 14px;
}

.post-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.comment-section {
  margin-top: 20px;
}

.section-header {
  font-weight: bold;
  font-size: 16px;
}
</style>
