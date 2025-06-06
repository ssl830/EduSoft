import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
// import { useAuthStore } from '../stores/auth'

import Home from '../views/Home.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import NotFound from '../views/NotFound.vue'
import DiscussionList from '../views/discuss/DiscussionList.vue'
import ThreadDetail from '../views/discuss/ThreadDetail.vue'

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
    path: '/class',
    name: 'Class',
    component: () => import('../views/class/ClassList.vue'),
    meta: {
      title: '班级列表',
      requiresAuth: true,
      showSidebar: true
    }
  },
  {
    path: '/class/:id',
    name: 'ClassDetail',
    component: () => import('../views/class/ClassDetail.vue'),
    meta: {
      title: '班级详情',
      requiresAuth: true,
      showSidebar: true
    }
  },  {
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
    path: '/course/create',
    name: 'CourseCreate',
    component: () => import('../views/course/CourseCreate.vue'),
    meta: {
      title: '创建课程',
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
      roles: ['teacher', 'tutor'],
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
  {
    path: '/takeExercise/:id',
    name: 'TakeExercise',
    component: () => import('../views/exercise/ExerciseTake.vue'),
    meta: {
      title: '参与练习',
      requiresAuth: true,
      roles: ['student', 'teacher'], // TODO
      showSidebar: false
    }
  },
  {
    path: '/checkExercise/:submissionId',
    name: 'CheckExercise',
    component: () => import('../views/exercise/CheckExercise.vue'),
    meta: {
      title: '批改练习',
      requiresAuth: true,
      roles: ['tutor', 'teacher'], // TODO
      showSidebar: false
    }
  },
  {
    path: '/exerciseFeedback/:practiceId/:submissionId',
    name: 'ExerciseFeedback',
    component: () => import('../views/exercise/ExerciseFeedback.vue'),
    meta: {
      title: '练习反馈',
      requiresAuth: true,
      roles: ['student', 'teacher'], // TODO
      showSidebar: false
    }
  },  // {
  //   path: '/exercise/:id/feedback',
  //   name: 'ExerciseFeedback',
  //   component: () => import('../views/exercise/ExerciseFeedback.vue'),
  //   meta: {
  //     title: '练习反馈',
  //     requiresAuth: true,
  //     showSidebar: true
  //   }
  // },
  {
    path: '/questionBank',
    name: 'QuestionBank',
    component: () => import('../views/question/QuestionBank.vue'),
    meta: {
      title: '题库',
      requiresAuth: true,
      showSidebar: true
    }
  },
  {
    path: '/questionFavor',
    name: 'QuestionFavor',
    component: () => import('../views/question/QuestionFavor.vue'),
    meta: {
      title: '收藏题库',
      requiresAuth: true,
      showSidebar: true
    }
  },
  {
    path: '/questionWrong',
    name: 'QuestionWrong',
    component: () => import('../views/question/QuestionWrong.vue'),
    meta: {
      title: '错误题库',
      requiresAuth: true,
      showSidebar: true
    }
  },
  {
    path: '/learning-records',
    name: 'LearningRecords',
    component: () => import('../views/records/LearningRecords.vue'),
    meta: {
      title: '学习记录',
      requiresAuth: true,
      showSidebar: true
    }
  },  {
    path: '/CourseCalendar',
    name: 'CourseCalendar',
    component: () => import('../views/CourseCalendar/CourseCalendar.vue'),
    meta: {
      title: '课程表',
      requiresAuth: true,
      showSidebar: true
    }
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: () => import('../views/profile/UserProfile.vue'),
    meta: {
      title: '个人信息',
      requiresAuth: true,
      showSidebar: true
    }
  },  {
    path: '/courses/:courseId/discussions',
    name: 'CourseDiscussionList', // 重命名以区分
    component: DiscussionList,
    props: true,
    meta: { requiresAuth: true }
  },
  { // +++ 新增的路由规则 +++
    path: '/discussions',
    name: 'GeneralDiscussionList',
    component: DiscussionList,
    meta: {
      requiresAuth: true,
      showSidebar: true  // 显示侧边栏
    }
  },
  {
    path: '/discussions/:threadId',
    name: 'ThreadDetail',
    component: ThreadDetail,
    props: true, // Pass route params as props to the component
    meta: {
      requiresAuth: true,
      showSidebar: true  // 显示侧边栏
    }
  },
  {
    path: '/notifications',
    name: 'Notification',
    component: () => import('../views/notification/Notification.vue'),
    meta: {
      title: '通知中心',
      requiresAuth: true,
      showSidebar: true
    }
  },
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
