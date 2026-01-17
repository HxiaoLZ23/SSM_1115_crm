import request from './request'

/**
 * 发布帖子
 */
export function createPost(content, images) {
  return request({
    url: '/post',
    method: 'post',
    data: { content, images }
  })
}

/**
 * 获取帖子详情
 */
export function getPostDetail(postId) {
  return request({
    url: `/post/${postId}`,
    method: 'get'
  })
}

/**
 * 获取帖子列表
 */
export function getPostList(pageNum = 1, pageSize = 10) {
  return request({
    url: '/post/list',
    method: 'get',
    params: { pageNum, pageSize }
  })
}

/**
 * 获取首页时间线
 */
export function getTimeline(pageNum = 1, pageSize = 10) {
  return request({
    url: '/post/timeline',
    method: 'get',
    params: { pageNum, pageSize }
  })
}

/**
 * 获取用户发帖列表
 */
export function getUserPosts(userId, pageNum = 1, pageSize = 10) {
  return request({
    url: `/post/user/${userId}`,
    method: 'get',
    params: { pageNum, pageSize }
  })
}

/**
 * 删除帖子
 */
export function deletePost(postId) {
  return request({
    url: `/post/${postId}`,
    method: 'delete'
  })
}

/**
 * 上传图片
 */
export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  
  return request({
    url: '/upload/image',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量上传图片
 */
export function uploadImages(files) {
  const formData = new FormData()
  files.forEach(file => {
    formData.append('files', file)
  })
  
  return request({
    url: '/upload/images',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
