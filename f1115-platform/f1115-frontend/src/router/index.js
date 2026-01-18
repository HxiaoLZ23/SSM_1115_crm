import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'
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
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/AdminLayout.vue'),
    meta: { requiresAuth: true, requiresAdmin: true },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue')
      },
      {
        path: 'users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/UserManage.vue')
      },
      {
        path: 'posts',
        name: 'AdminPosts',
        component: () => import('@/views/admin/PostManage.vue')
      },
      {
        path: 'comments',
        name: 'AdminComments',
        component: () => import('@/views/admin/CommentManage.vue')
      }
    ]
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
  } else if (to.meta.requiresAdmin) {
    // 需要管理员权限
    if (!userStore.currentUser || userStore.currentUser.role !== 1) {
      ElMessage.error('需要管理员权限')
      next('/home')
      return
    }
    next()
  } else if ((to.path === '/login' || to.path === '/register') && userStore.isLoggedIn) {
    // 已登录用户访问登录/注册页，跳转到首页
    next('/home')
  } else {
    next()
  }
})

export default router
