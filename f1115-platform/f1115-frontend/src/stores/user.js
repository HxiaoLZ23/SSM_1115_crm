import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login, register, logout, getProfile } from '@/api/user'

export const useUserStore = defineStore('user', () => {
  // 从localStorage恢复用户信息
  const savedUser = localStorage.getItem('currentUser')
  const currentUser = ref(savedUser ? JSON.parse(savedUser) : null)
  
  const isLoggedIn = computed(() => currentUser.value !== null)
  
  // 保存用户信息到localStorage
  const saveUser = (user) => {
    currentUser.value = user
    if (user) {
      localStorage.setItem('currentUser', JSON.stringify(user))
    } else {
      localStorage.removeItem('currentUser')
    }
  }
  
  // 登录
  async function userLogin(username, password) {
    const res = await login(username, password)
    if (res.code === 200) {
      saveUser(res.data)
      return true
    }
    return false
  }
  
  // 注册
  async function userRegister(username, password, email, nickname) {
    const res = await register(username, password, email, nickname)
    return res.code === 200
  }
  
  // 登出
  async function userLogout() {
    await logout()
    saveUser(null)
  }
  
  // 获取用户信息
  async function fetchUserProfile() {
    try {
      const res = await getProfile()
      if (res.code === 200) {
        saveUser(res.data)
      } else if (res.code === 401) {
        // 未登录，清除本地存储
        saveUser(null)
      }
    } catch (error) {
      // 请求失败，清除本地存储
      saveUser(null)
    }
  }
  
  return {
    currentUser,
    isLoggedIn,
    userLogin,
    userRegister,
    userLogout,
    fetchUserProfile
  }
})
