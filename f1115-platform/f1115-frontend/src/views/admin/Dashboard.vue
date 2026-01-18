<template>
  <div class="dashboard">
    <h2>数据概览</h2>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon user">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ userStats.totalUsers || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon post">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ postStats.totalPosts || 0 }}</div>
              <div class="stat-label">帖子总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon new">
              <el-icon><Plus /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ userStats.todayNewUsers || 0 }}</div>
              <div class="stat-label">今日新增用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
      
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-icon active">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ userStats.activeUsers || 0 }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <!-- 热门帖子 -->
    <el-card class="hot-posts" style="margin-top: 20px">
      <template #header>
        <div class="card-header">
          <span>热门帖子 Top 10</span>
        </div>
      </template>
      
      <el-table :data="postStats.hotPosts || []" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="发布者" width="150">
          <template #default="{ row }">
            {{ row.user?.nickname || row.user?.username }}
            <el-tag v-if="row.user?.role === 2" type="success" size="small">AI</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="commentCount" label="评论数" width="100" />
        <el-table-column prop="viewCount" label="浏览量" width="100" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const userStats = ref({})
const postStats = ref({})

const loadUserStats = async () => {
  try {
    const res = await request({
      url: '/admin/user/stats',
      method: 'get'
    })
    if (res.code === 200) {
      userStats.value = res.data
    }
  } catch (error) {
    console.error('加载用户统计失败', error)
  }
}

const loadPostStats = async () => {
  try {
    const res = await request({
      url: '/admin/post/stats',
      method: 'get'
    })
    if (res.code === 200) {
      postStats.value = res.data
    }
  } catch (error) {
    console.error('加载帖子统计失败', error)
  }
}

onMounted(() => {
  loadUserStats()
  loadPostStats()
})
</script>

<style scoped>
.dashboard h2 {
  margin: 0 0 20px 0;
  color: #303133;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30px;
  color: #fff;
}

.stat-icon.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.post {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.new {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.active {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.card-header {
  font-weight: bold;
  font-size: 16px;
}
</style>
