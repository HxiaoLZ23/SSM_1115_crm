<template>
  <div class="post-manage">
    <h2>帖子管理</h2>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="帖子内容" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="全部" clearable>
            <el-option label="正常" :value="1" />
            <el-option label="已删除" :value="0" />
            <el-option label="审核中" :value="2" />
            <el-option label="违规" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="全部" clearable>
            <el-option label="普通" :value="0" />
            <el-option label="置顶" :value="1" />
            <el-option label="AI生成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 帖子列表 -->
    <el-card>
      <el-table :data="posts" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="发布者" width="150">
          <template #default="{ row }">
            {{ row.user?.nickname || row.user?.username }}
            <el-tag v-if="row.user?.role === 2" type="success" size="small">AI</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.type === 1" type="warning">置顶</el-tag>
            <el-tag v-else-if="row.type === 2" type="success">AI</el-tag>
            <el-tag v-else type="info">普通</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.status === 1" type="success">正常</el-tag>
            <el-tag v-else-if="row.status === 0" type="info">已删除</el-tag>
            <el-tag v-else-if="row.status === 2" type="warning">审核中</el-tag>
            <el-tag v-else type="danger">违规</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞" width="80" />
        <el-table-column prop="commentCount" label="评论" width="80" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" fixed="right" width="200">
          <template #default="{ row }">
            <el-button
              v-if="row.type !== 1"
              size="small"
              type="warning"
              @click="handleTop(row)"
            >
              置顶
            </el-button>
            <el-button
              v-else
              size="small"
              @click="handleCancelTop(row)"
            >
              取消置顶
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="total"
          layout="total, prev, pager, next, jumper"
          @current-change="loadPosts"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const posts = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  keyword: '',
  status: null,
  type: null
})

const loadPosts = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/admin/post/list',
      method: 'get',
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        ...searchForm.value
      }
    })
    if (res.code === 200) {
      posts.value = res.data
      total.value = res.total
    }
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadPosts()
}

const handleReset = () => {
  searchForm.value = {
    keyword: '',
    status: null,
    type: null
  }
  handleSearch()
}

const handleTop = async (row) => {
  try {
    const res = await request({
      url: `/admin/post/${row.id}/top`,
      method: 'put',
      data: { type: 1 }
    })
    
    if (res.code === 200) {
      ElMessage.success('置顶成功')
      loadPosts()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleCancelTop = async (row) => {
  try {
    const res = await request({
      url: `/admin/post/${row.id}/top`,
      method: 'put',
      data: { type: 0 }
    })
    
    if (res.code === 200) {
      ElMessage.success('取消置顶成功')
      loadPosts()
    }
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条帖子吗？', '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    const res = await request({
      url: `/admin/post/${row.id}`,
      method: 'delete'
    })
    
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadPosts()
    }
  } catch (error) {
    // 用户取消
  }
}

onMounted(() => {
  loadPosts()
})
</script>

<style scoped>
.post-manage h2 {
  margin: 0 0 20px 0;
  color: #303133;
}

.search-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>
