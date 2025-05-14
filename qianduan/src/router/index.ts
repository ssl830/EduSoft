import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../stores/auth'

import Home from '../views/Home.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import NotFound from '../views/NotFound.vue'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: {
      title: '首页',
      requiresAuth: false,
      showSidebar: true
    }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: {
      title: '登录',
      requiresAuth: false,
      showSidebar: false
    }
  },
  {
    path: '/register',
    name: 'Register',
    component: Register,
    meta: {
      title: '注册',
      requiresAuth: false,
      showSidebar: false
    }
  },
  {
    path: '/course/:id',
    name: 'CourseDetail',
    component: () => import('../views/course/CourseDetail.vue'),
    meta: {
      title: '课程详情',
      requiresAuth: true,
      showSidebar: true
    }
  },
  {
    path: '/exercise/create',
    name: 'ExerciseCreate',
    component: () => import('../views/exercise/ExerciseCreate.vue'),
    meta: {
      title: '创建练习',
      requiresAuth: true,
      roles: ['teacher', 'assistant'],
      showSidebar: true
    }
  },
  // {
  //   path: '/exercise/:id',
  //   name: 'ExerciseDetail',
  //   component: () => import('../views/exercise/ExerciseDetail.vue'),
  //   meta: {
  //     title: '练习详情',
  //     requiresAuth: true,
  //     showSidebar: true
  //   }
  // },
  // {
  //   path: '/exercise/:id/take',
  //   name: 'ExerciseTake',
  //   component: () => import('../views/exercise/ExerciseTake.vue'),
  //   meta: {
  //     title: '参与练习',
  //     requiresAuth: true,
  //     roles: ['student'],
  //     showSidebar: false
  //   }
  // },
  // {
  //   path: '/exercise/:id/feedback',
  //   name: 'ExerciseFeedback',
  //   component: () => import('../views/exercise/ExerciseFeedback.vue'),
  //   meta: {
  //     title: '练习反馈',
  //     requiresAuth: true,
  //     showSidebar: true
  //   }
  // },
  // {
  //   path: '/question-bank',
  //   name: 'QuestionBank',
  //   component: () => import('../views/question/QuestionBank.vue'),
  //   meta: {
  //     title: '题库',
  //     requiresAuth: true,
  //     showSidebar: true
  //   }
  // },
  // {
  //   path: '/learning-records',
  //   name: 'LearningRecords',
  //   component: () => import('../views/records/LearningRecords.vue'),
  //   meta: {
  //     title: '学习记录',
  //     requiresAuth: true,
  //     showSidebar: true
  //   }
  // },
  {
    path: '/:pathMatch(.*)*',
    name: 'NotFound',
    component: NotFound,
    meta: {
      title: '页面未找到',
      requiresAuth: false,
      showSidebar: false
    }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 }
  }
})

// router.beforeEach((to, from, next) => {
//   const authStore = useAuthStore()
//   const { requiresAuth, roles } = to.meta
//
//   // Update document title
//   document.title = `${to.meta.title} | 课程管理平台`
//
//   // Check authentication requirements
//   if (requiresAuth && !authStore.isAuthenticated) {
//     next({ name: 'Login', query: { redirect: to.fullPath } })
//     return
//   }
//
//   // Check role requirements if specified
//   if (roles && !roles.includes(authStore.userRole)) {
//     next({ name: 'Home' })
//     return
//   }
//
//   next()
// })

export default router
