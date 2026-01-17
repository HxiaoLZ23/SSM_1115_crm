import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const routes = [
  {
    path: '/',
    redirect: '/home'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/home',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/profile',
    name: 'Profile',
    redirect: to => {
      // 重定向到当前用户的主页
      const userStore = useUserStore()
      if (userStore.currentUser) {
        return `/user/${userStore.currentUser.id}`
      }
      return '/login'
    }
  },
  {
    path: '/user/:userId',
    name: 'UserProfile',
    component: () => import('@/views/Profile.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/post/:postId',
    name: 'PostDetail',
    component: () => import('@/views/PostDetail.vue'),
    meta: { requiresAuth: false }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore()
  
  // 如果有本地存储的用户信息，但还没验证过，先验证一下
  if (userStore.currentUser && to.meta.requiresAuth) {
    // 验证Session是否有效
    await userStore.fetchUserProfile()
  }
  
  if (to.meta.requiresAuth && !userStore.isLoggedIn) {
    // 需要登录但未登录，跳转到登录页
    next('/login')
  } else if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    // 已登录用户访问登录/注册页，跳转到首页
    next('/home')
  } else {
    next()
  }
})

export default router
