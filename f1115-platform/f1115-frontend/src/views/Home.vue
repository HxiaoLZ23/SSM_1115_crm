<template>
  <div class="home-container">
    <el-container>
      <!-- 顶部导航栏 -->
      <el-header>
        <div class="header-content">
          <h1>F1115 社交平台</h1>
          <div class="user-info">
            <span>欢迎，{{ userStore.currentUser?.nickname || userStore.currentUser?.username }}</span>
            <el-button type="primary" @click="router.push('/profile')">个人中心</el-button>
            <el-button @click="handleLogout">退出登录</el-button>
          </div>
        </div>
      </el-header>
      
      <!-- 主内容区 -->
      <el-main>
        <el-row :gutter="20">
          <!-- 左侧：发帖区域 -->
          <el-col :span="16">
            <!-- 发帖卡片 -->
            <el-card class="post-card">
              <template #header>
                <div class="card-header">
                  <span>发布动态</span>
                </div>
              </template>
              
              <el-input
                v-model="postContent"
                type="textarea"
                :rows="4"
                placeholder="分享你的想法..."
                maxlength="5000"
                show-word-limit
              />
              
              <!-- 图片预览 -->
              <div v-if="uploadedImages.length > 0" class="image-preview">
                <div v-for="(img, index) in uploadedImages" :key="index" class="preview-item">
                  <el-image :src="img.preview" fit="cover" class="preview-image" />
                  <el-icon class="remove-icon" @click="removeImage(index)">
                    <CircleClose />
                  </el-icon>
                </div>
              </div>
              
              <div class="post-actions">
                <div>
                  <el-upload
                    :auto-upload="false"
                    :on-change="handleImageSelect"
                    :show-file-list="false"
                    accept="image/*"
                    multiple
                  >
                    <el-button icon="Picture">添加图片</el-button>
                  </el-upload>
                </div>
                <el-button type="primary" @click="handlePublish" :loading="publishing">
                  发布
                </el-button>
              </div>
            </el-card>
            
            <!-- 帖子列表 -->
            <div class="post-list">
              <el-card v-for="post in posts" :key="post.id" class="post-item" @click="goToPostDetail(post.id)">
                <div class="post-header">
                  <el-avatar 
                    :src="getImageUrl(post.user?.avatar)" 
                    :size="40"
                    class="clickable-avatar"
                    @click.stop="goToUserProfile(post.userId)"
                  >
                    {{ post.user?.nickname?.[0] || post.user?.username?.[0] }}
                  </el-avatar>
                  <div class="user-info">
                    <div class="nickname clickable-name" @click.stop="goToUserProfile(post.userId)">
                      {{ post.user?.nickname || post.user?.username }}
                    </div>
                    <div class="time">{{ formatTime(post.createTime) }}</div>
                  </div>
                </div>
                
                <div class="post-content">
                  {{ post.content }}
                </div>
                
                <!-- 图片展示 -->
                <div v-if="post.imageList && post.imageList.length > 0" class="post-images">
                  <el-image
                    v-for="(img, index) in post.imageList.slice(0, 9)"
                    :key="index"
                    :src="getImageUrl(img)"
                    :preview-src-list="post.imageList.map(i => getImageUrl(i))"
                    :initial-index="index"
                    fit="cover"
                    class="post-image"
                    @click.stop
                  />
                </div>
                
                <div class="post-footer">
                  <el-space>
                    <el-button text @click.stop>
                      <el-icon><ChatDotRound /></el-icon>
                      {{ post.commentCount }}
                    </el-button>
                    <el-button text @click.stop>
                      <el-icon><Star /></el-icon>
                      {{ post.likeCount }}
                    </el-button>
                    <el-button text @click.stop>
                      <el-icon><View /></el-icon>
                      {{ post.viewCount }}
                    </el-button>
                  </el-space>
                </div>
              </el-card>
              
              <!-- 分页 -->
              <div class="pagination">
                <el-pagination
                  v-model:current-page="currentPage"
                  v-model:page-size="pageSize"
                  :total="total"
                  layout="prev, pager, next"
                  @current-change="loadPosts"
                />
              </div>
            </div>
          </el-col>
          
          <!-- 右侧：用户信息 -->
          <el-col :span="8">
            <el-card>
              <div class="user-card">
                <el-avatar :size="80">
                  {{ userStore.currentUser?.nickname?.[0] || userStore.currentUser?.username?.[0] }}
                </el-avatar>
                <h3>{{ userStore.currentUser?.nickname || userStore.currentUser?.username }}</h3>
                <p>{{ userStore.currentUser?.bio || '暂无简介' }}</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { getPostList, createPost, uploadImages } from '@/api/post'

const router = useRouter()
const userStore = useUserStore()

const postContent = ref('')
const publishing = ref(false)
const posts = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const uploadedImages = ref([])
const uploadingImages = ref([])

// 加载帖子列表
const loadPosts = async () => {
  try {
    const res = await getPostList(currentPage.value, pageSize.value)
    if (res.code === 200) {
      posts.value = res.data
      total.value = res.total
    }
  } catch (error) {
    console.error('加载帖子失败', error)
  }
}

// 选择图片
const handleImageSelect = async (file) => {
  // 限制图片数量
  if (uploadedImages.value.length >= 9) {
    ElMessage.warning('最多只能上传9张图片')
    return
  }
  
  // 验证文件类型
  const allowedTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/gif', 'image/webp']
  if (!allowedTypes.includes(file.raw.type)) {
    ElMessage.error('只支持 jpg, png, gif, webp 格式的图片')
    return
  }
  
  // 验证文件大小（10MB）
  if (file.raw.size > 10 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过10MB')
    return
  }
  
  // 添加到待上传列表
  uploadingImages.value.push(file.raw)
  
  // 预览图片（使用本地预览）
  const reader = new FileReader()
  reader.onload = (e) => {
    uploadedImages.value.push({
      preview: e.target.result, // 本地预览URL
      file: file.raw // 文件对象
    })
  }
  reader.readAsDataURL(file.raw)
}

// 移除图片
const removeImage = (index) => {
  uploadedImages.value.splice(index, 1)
  uploadingImages.value.splice(index, 1)
}

// 发布帖子
const handlePublish = async () => {
  if (!postContent.value.trim()) {
    ElMessage.warning('请输入帖子内容')
    return
  }
  
  publishing.value = true
  try {
    let imageUrls = []
    
    // 如果有图片，先上传图片
    if (uploadingImages.value.length > 0) {
      const uploadRes = await uploadImages(uploadingImages.value)
      if (uploadRes.code === 200) {
        imageUrls = uploadRes.data
      } else {
        ElMessage.error('图片上传失败')
        return
      }
    }
    
    // 发布帖子
    const res = await createPost(postContent.value, imageUrls)
    if (res.code === 200) {
      ElMessage.success('发布成功')
      postContent.value = ''
      uploadedImages.value = []
      uploadingImages.value = []
      // 重新加载帖子列表
      currentPage.value = 1
      await loadPosts()
    }
  } finally {
    publishing.value = false
  }
}

// 跳转到帖子详情
const goToPostDetail = (postId) => {
  router.push(`/post/${postId}`)
}

// 跳转到用户主页
const goToUserProfile = (userId) => {
  router.push(`/user/${userId}`)
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

// 退出登录
const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    
    await userStore.userLogout()
    ElMessage.success('退出成功')
    router.push('/login')
  } catch (error) {
    // 用户取消
  }
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.home-container {
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
  font-size: 24px;
  color: #303133;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.user-info span {
  color: #606266;
}

.el-main {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.post-card {
  margin-bottom: 20px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}

.post-actions {
  margin-top: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.image-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 15px;
}

.preview-item {
  position: relative;
  width: 100px;
  height: 100px;
}

.preview-image {
  width: 100%;
  height: 100%;
  border-radius: 8px;
}

.remove-icon {
  position: absolute;
  top: -8px;
  right: -8px;
  font-size: 20px;
  color: #f56c6c;
  background: white;
  border-radius: 50%;
  cursor: pointer;
}

.remove-icon:hover {
  color: #f56c6c;
  transform: scale(1.1);
}

.post-list {
  margin-top: 20px;
}

.post-item {
  margin-bottom: 15px;
  cursor: pointer;
  transition: all 0.3s;
}

.post-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.post-images {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
  margin: 15px 0;
}

.post-image {
  width: 100%;
  height: 150px;
  border-radius: 8px;
}

.post-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.user-info {
  margin-left: 10px;
}

.nickname {
  font-weight: bold;
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
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.post-content {
  margin: 15px 0;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.post-footer {
  padding-top: 15px;
  border-top: 1px solid #ebeef5;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

.user-card {
  text-align: center;
}

.user-card h3 {
  margin: 15px 0 10px 0;
  color: #303133;
}

.user-card p {
  color: #909399;
  font-size: 14px;
}
</style>
