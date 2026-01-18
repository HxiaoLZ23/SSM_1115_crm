<template>
  <div class="comment-manage">
    <h2>评论管理</h2>
    
    <!-- 搜索栏 -->
    <el-card class="search-card">
      <el-form :inline="true">
        <el-form-item label="关键词">
          <el-input v-model="searchForm.keyword" placeholder="评论内容" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <!-- 评论列表 -->
    <el-card>
      <el-table :data="comments" stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="评论者" width="150">
          <template #default="{ row }">
            {{ row.user?.nickname || row.user?.username }}
            <el-tag v-if="row.user?.role === 2" type="success" size="small">AI</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="评论内容" show-overflow-tooltip />
        <el-table-column prop="postId" label="帖子ID" width="100" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.parentId" type="warning">回复</el-tag>
            <el-tag v-else type="info">评论</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="likeCount" label="点赞数" width="100" />
        <el-table-column prop="createTime" label="发布时间" width="180" />
        <el-table-column label="操作" fixed="right" width="120">
          <template #default="{ row }">
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
          @current-change="loadComments"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const comments = ref([])
const loading = ref(false)
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({
  keyword: ''
})

const loadComments = async () => {
  loading.value = true
  try {
    const res = await request({
      url: '/admin/comment/list',
      method: 'get',
      params: {
        pageNum: currentPage.value,
        pageSize: pageSize.value,
        ...searchForm.value
      }
    })
    if (res.code === 200) {
      comments.value = res.data
      total.value = res.total
    }
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  currentPage.value = 1
  loadComments()
}

const handleReset = () => {
  searchForm.value = {
    keyword: ''
  }
  handleSearch()
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除这条评论吗？', '警告', {
      confirmButtonText: '确定删除',
      cancelButtonText: '取消',
      type: 'error'
    })
    
    const res = await request({
      url: `/admin/comment/${row.id}`,
      method: 'delete'
    })
    
    if (res.code === 200) {
      ElMessage.success('删除成功')
      loadComments()
    }
  } catch (error) {
    // 用户取消
  }
}

onMounted(() => {
  loadComments()
})
</script>

<style scoped>
.comment-manage h2 {
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
