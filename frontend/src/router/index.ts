import { createRouter, createWebHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import ClassApi from '../api/class'
import CourseApi from '../api/course'

import Home from '../views/Home.vue'
import Login from '../views/auth/Login.vue'
import Register from '../views/auth/Register.vue'
import NotFound from '../views/NotFound.vue'
import DiscussionList from '../views/discuss/DiscussionList.vue'
import ThreadDetail from '../views/discuss/ThreadDetail.vue'
import LearningRecordsAnalysis from '@/views/records/LearningRecordsAnalysis.vue'

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
    path: '/class/create',
    name: 'ClassCreate',
    component: () => import('../views/class/ClassCreate.vue'),
    meta: {
      title: '创建班级',
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
  },  {
    path: '/exercise/create',
    name:'ExerciseCreate',
    component: () => import('../views/exercise/ExerciseCreate.vue'),
    meta: {
      title: '创建练习',
      requiresAuth: true,
      roles: ['teacher', 'tutor'],
      showSidebar: true
    }
  },
  {
    path: '/study-records',
    name: 'StudyRecords',
    component: () => import('../views/record/StudyRecordList.vue'),
    meta: {
      title: '学习记录',
      requiresAuth: true,
      roles: ['student'],
      showSidebar: true
    }
  },
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
    path: '/checkExercise/:practiceId/:submissionId',
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
  },
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
    path: '/study-records',
    name: 'StudyRecordList',
    component: () => import('../views/record/StudyRecordList.vue'),
    meta: {
      title: '学习记录',
      requiresAuth: true,
      roles: ['student'],
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
  },
  {
    path: '/courses/:courseId/discussions',
    name: 'CourseDiscussionList',
    component: DiscussionList,
    props: true,
    meta: {
      requiresAuth: true,
      title: '讨论区',
    }
  },
  { // +++ 新增的路由规则 +++
    path: '/discussions',
    name: 'GeneralDiscussionList',
    component: DiscussionList,
    meta: {
      requiresAuth: true,
      title: '讨论区',
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
      title: '讨论区',
      showSidebar: true  // 显示侧边栏
    }
  },
  {
    path: '/help',
    name: 'HelpAndFeedback',
    component: () => import('@/views/help/HelpAndFeedback.vue'), // Changed path
    meta: {
      title: '帮助与反馈',
      requiresAuth: false,
      showSidebar: true
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
    path: '/exercise/edit/:id',
    name: 'ExerciseEdit',
    component: () => import('../views/exercise/ExerciseEdit.vue'),
    meta: {
      title: '编辑练习',
      requiresAuth: true,
      roles: ['teacher', 'tutor'],
      showSidebar: true
    }
  },
  {
    path: '/schedule',
    name: 'Schedule',
    component: () => import('../views/ScheduleView.vue'),
    meta: {
      title: '课表',
      requiresAuth: true,
      showSidebar: true
    }
  },
  {
    path: '/learning-records-analysis',
    name: 'LearningRecordsAnalysis',
    component: LearningRecordsAnalysis,
    meta: {
      title: '练习分析',
      requiresAuth: true,
    } },
  {
    path: '/assistant',
    name: 'OnlineAssistant',
    component: () => import('../views/assistant/OnlineAssistant.vue'),
    meta: {
      title: '在线学习助手',
      requiresAuth: true,
      showSidebar: true
    }
  },
  {
    path: '/selfpractice/history',
    name: 'SelfPracticeHistory',
    component: () => import('../views/practice/SelfPracticeHistory.vue'),
    meta: {
      title: '自测历史',
      requiresAuth: true,
      roles: ['student'],
      showSidebar: true
    }
  },
  {
    path: '/ai-selftest',
    name: 'AISelfTest',
    component: () => import('../views/practice/AISelfTest.vue'),
    meta: {
      title: 'AI自测',
      requiresAuth: true,
      roles: ['student'],
      showSidebar: true
    }
  },
  {
    path: '/selfpractice/history/:pid',
    name: 'SelfPracticeDetail',
    component: () => import('../views/practice/SelfPracticeDetail.vue'),
    meta: {
      title: '自测详情',
      requiresAuth: true,
      roles: ['student'],
      showSidebar: false
    }
  },
  {

    path: '/manage/teachers',
    name: 'TeachersList',
    component: () => import('../views/management/TeachersList.vue'),
    meta: {
      title: '教师列表',
      requiresAuth: true,
      roles: ['tutor'],
      showSidebar: true
    }
  },
  {
    path: '/manage/students',
    name: 'StudentsList',
    component: () => import('../views/management/StudentsList.vue'),
    meta: {
      title: '学生列表',
      requiresAuth: true,
      roles: ['tutor'],
      showSidebar: true
    }
  },
  {
    path: '/manage/tutors',
    name: 'TutorsList',
    component: () => import('../views/management/TutorsList.vue'),
    meta: {
      title: '管理员列表',
      requiresAuth: true,
      roles: ['tutor'],
      showSidebar: true
    }
  },
  {
    path: '/manage/resources',
    name: 'ResourcesList',
    component: () => import('../views/management/ResourcesList.vue'),
    meta: {
      title: '资源列表',
      requiresAuth: true,
      roles: ['tutor'],
      showSidebar: true
    }
  },
  {
    path: '/manage/exercise',
    name: 'ExerciseList',
    component: () => import('../views/management/ExerciseList.vue'),
    meta: {
      title: '练习列表',
      requiresAuth: true,
      roles: ['tutor'],
      showSidebar: true
    }
  },
  {
    path: '/dashboard',
    name: 'DashboardOverview',
    component: () => import('../views/management/DashboardOverview.vue'),
    meta: {
      title: '系统概览',
      requiresAuth: true,
      roles: ['tutor'],
      showSidebar: true
    }
  },
  {
    path: '/settings/knowledge-base',
    name: 'KnowledgeBaseSettings',
    component: () => import('../views/management/KnowledgeBaseSettings.vue'),
    meta: {
      title: '私密知识库设置',
      requiresAuth: true,
      roles: ['teacher', 'tutor', 'admin'],
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

// 路由守卫
router.beforeEach( async (to, _from, next) => {
  const authStore = useAuthStore()
  const {requiresAuth} = to.meta

  // 设置页面标题
  if (to.meta && to.meta.title) {
    document.title = `${to.meta.title} | 课程管理平台`
  }

  // 检查登录
  if (requiresAuth && !authStore.isAuthenticated) {
    next({name: 'Login', query: {redirect: to.fullPath}})
    return
  }

  // 禁止 student 访问部分页面
  if (authStore.user && authStore.user.role === 'student' && (
    to.meta.title === '创建班级' || to.meta.title === '创建课程' || to.meta.title === '创建练习' ||
    to.meta.title === '批改练习' || to.meta.title === '练习列表' || to.meta.title === '教师列表' ||
    to.meta.title === '学生列表' || to.meta.title === '管理员列表' || to.meta.title === '资源列表' ||
    to.meta.title === '系统概览' || to.meta.title === '私密知识库设置' || to.meta.title === '编辑练习' ||
    to.meta.title === '练习分析'
  )) {
    next({name: 'Home'})
    return
  }

  // 禁止 teacher 访问部分页面
  if (authStore.user && authStore.user.role === 'teacher' && (
    to.meta.title === '参与练习' || to.meta.title === '练习反馈' || to.meta.title === '收藏题库' ||
    to.meta.title === '错误题库' || to.meta.title === '学习记录' || to.meta.title === '自测历史' ||
    to.meta.title === 'AI自测' || to.meta.title === '自测详情' || to.meta.title === '教师列表' ||
    to.meta.title === '学生列表' || to.meta.title === '管理员列表' || to.meta.title === '资源列表' ||
    to.meta.title === '系统概览' || to.meta.title === '练习列表'
  )) {
    next({name: 'Home'})
    return
  }

  // 禁止 tutor 访问部分页面
  if (authStore.user && authStore.user.role === 'tutor' && (
    to.meta.title === '班级列表' || to.meta.title === '创建班级' || to.meta.title === '班级详情' ||
    to.meta.title === '课程详情' || to.meta.title === '创建课程' || to.meta.title === '创建练习' ||
    to.meta.title === '参与练习' || to.meta.title === '批改练习' || to.meta.title === '练习反馈' ||
    to.meta.title === '题库' || to.meta.title === '收藏题库' || to.meta.title === '错误题库' ||
    to.meta.title === '学习记录' || to.meta.title === '通知中心' || to.meta.title === '课表' ||
    to.meta.title === '在线学习助手' || to.meta.title === '自测历史' || to.meta.title === 'AI自测' ||
    to.meta.title === '自测详情' || to.meta.title === '练习分析' || to.meta.title === '讨论区' ||
    to.meta.title === '首页'
  )) {
    next({name: 'TeachersList'})
    return
  }

  // 校验班级成员权限：仅班级成员（学生或老师）可访问班级详情
  if (
    to.meta.title === '班级详情' && authStore.user &&
    (authStore.user.role === 'student' || authStore.user.role === 'teacher')
  ) {
    try {
      const classId = to.params.id
      let isMember = false
      if (authStore.user.role === 'student') {
        // 查询学生是否属于该班级
        const res = await ClassApi.getClassStudents(String(classId))
        console.log("学生" + res.data)
        console.log(res.data)
        if (res.data && Array.isArray(res.data)) {
          isMember = res.data.some((u: any) => u.userId === authStore.user?.id)
        }
      } else if (authStore.user.role === 'teacher') {
        // 查询老师是否为该班级的教师
        const res = await ClassApi.getClassById(String(classId))
        if (res.data && res.data.teacherId) {
          isMember = res.data.teacherId === authStore.user.id
        }
      }
      if (!isMember) {
        next({name: 'Home'})
        return
      }
    } catch (e) {
      next({name: 'Home'})
      return
    }
  }

  // 校验课程成员权限：仅课程成员（学生或老师）可访问课程详情
  if (
    to.meta.title === '课程详情' && authStore.user &&
    (authStore.user.role === 'student' || authStore.user.role === 'teacher')
  ) {
    try {
      const courseId = to.params.id
      let isMember = false
      if (authStore.user.role === 'student') {
        // 查询学生是否属于该课程
        const res = await CourseApi.getUserCourses(authStore.user.id)
        if (res.data && Array.isArray(res.data)) {
          isMember = res.data.some((c: any) => String(c.id) === String(courseId))
        }
      } else if (authStore.user.role === 'teacher') {
        // 查询老师是否为该课程的教师
        const res = await CourseApi.getCourseById(String(courseId))
        if (res.data && res.data.teacherId) {
          isMember = res.data.teacherId === authStore.user.id
        }
      }
      if (!isMember) {
        next({name: 'Home'})
        return
      }
    } catch (e) {
      next({name: 'Home'})
      return
    }
  }

  // 校验学生参与练习权限：练习必须属于学生所在班级
  if (
    to.name === 'TakeExercise' &&
    authStore.user &&
    authStore.user.role === 'student'
  ) {
    try {
      const ExerciseApi = (await import('@/api/exercise.ts')).default
      const practiceId = String(to.params.id)
      // 获取该练习详情，拿到其班级ID
      const res = await ExerciseApi.getExerciseDetails(practiceId, {})
      const classId = res.data?.classId
      if (!classId) {
        next({ name: 'Home' })
        return
      }
      // 获取学生所在班级列表
      const classRes = await ClassApi.getUserClasses(authStore.user.id)
      const classList = classRes.data || []
      const inClass = classList.some((cls: any) => String(cls.id) === String(classId))
      if (!inClass) {
        next({ name: 'Home' })
        return
      }
    } catch (e) {
      next({ name: 'Home' })
      return
    }
  }

  // 校验/checkExercise权限：教师只能批改自己班级的练习，且submissionId必须属于该practiceId
  // if (
  //   to.name === 'CheckExercise' &&
  //   authStore.user &&
  //   (authStore.user.role === 'teacher' || authStore.user.role === 'tutor')
  // ) {
  //   try {
  //     const ExerciseApi = (await import('@/api/exercise.ts')).default
  //     const practiceId = String(to.params.practiceId)
  //     const submissionId = String(to.params.submissionId)
  //
  //     // 1. 获取练习详情，检查班级归属
  //     const exerciseRes = await ExerciseApi.getExerciseDetails(practiceId, {})
  //     const classId = exerciseRes.data?.classId
  //     if (!classId) {
  //       next({ name: 'Home' })
  //       return
  //     }
  //     // 获取教师负责的班级
  //     const teacherClassRes = await ClassApi.getTeacherClasses(authStore.user.id)
  //     const teacherClasses = teacherClassRes.data || []
  //     const isTeacherOfClass = teacherClasses.some((cls: any) => String(cls.classId) === String(classId))
  //     if (!isTeacherOfClass) {
  //       next({ name: 'Home' })
  //       return
  //     }
  //
  //     // 2. 获取该练习的所有提交，判断submissionId是否属于该practiceId
  //     const submissionsRes = await ExerciseApi.getPendingJudgeList({practiceId, classId})
  //     const submissions = submissionsRes.data || []
  //     const hasSubmission = submissions.some((sub: any) => String(sub.submissionId) === String(submissionId))
  //     if (!hasSubmission) {
  //       next({ name: 'Home' })
  //       return
  //     }
  //   } catch (e) {
  //     next({ name: 'Home' })
  //     return
  //   }
  // }

  // 校验/exerciseFeedback权限：学生只能查看自己班级的练习反馈，且submissionId必须属于该practiceId
  // if (
  //   to.name === 'ExerciseFeedback' &&
  //   authStore.user &&
  //   authStore.user.role === 'student'
  // ) {
  //   try {
  //     const ExerciseApi = (await import('@/api/exercise.ts')).default
  //     const practiceId = String(to.params.practiceId)
  //     const submissionId = String(to.params.submissionId)
  //
  //     // 1. 获取练习详情，检查班级归属
  //     const exerciseRes = await ExerciseApi.getExerciseDetails(practiceId, {})
  //     const classId = exerciseRes.data?.classId
  //     if (!classId) {
  //       next({ name: 'Home' })
  //       return
  //     }
  //     // 获取学生所在班级列表
  //     const classRes = await ClassApi.getUserClasses(authStore.user.id)
  //     const classList = classRes.data || []
  //     const inClass = classList.some((cls: any) => String(cls.id) === String(classId))
  //     if (!inClass) {
  //       next({ name: 'Home' })
  //       return
  //     }
  //
  //     // 2. 获取该练习的所有提交，判断submissionId是否属于该practiceId
  //     const submissionsRes = await ExerciseApi.getPendingJudgeList({practiceId, classId})
  //     const submissions = submissionsRes.data || []
  //     const hasSubmission = submissions.some((sub: any) => String(sub.submissionId) === String(submissionId))
  //     if (!hasSubmission) {
  //       next({ name: 'Home' })
  //       return
  //     }
  //   } catch (e) {
  //     next({ name: 'Home' })
  //     return
  //   }
  // }

  // 校验/selfpractice/history/:pid权限：学生只能查看自己创建的AI自测历史
  if (
    to.name === 'SelfPracticeDetail' &&
    authStore.user &&
    authStore.user.role === 'student'
  ) {
    try {
      const pid = String(to.params.pid)
      const { getSelfPracticeHistory } = await import('@/api/ai')
      const res = await getSelfPracticeHistory()
      const historyList = res.data || []
      const hasPractice = historyList.some((item: any) => String(item.practiceId) === pid)
      if (!hasPractice) {
        next({ name: 'Home' })
        return
      }
    } catch (e) {
      next({ name: 'Home' })
      return
    }
  }

  // // 校验/exercise/edit/:id权限：教师只能编辑自己班级的练习
  // if (
  //   to.name === 'ExerciseEdit' &&
  //   authStore.user &&
  //   (authStore.user.role === 'teacher')
  // ) {
  //   try {
  //     const ExerciseApi = (await import('@/api/exercise.ts')).default
  //     const practiceId = String(to.params.id)
  //     // 获取练习详情，拿到其班级ID
  //     const res = await ExerciseApi.getExerciseDetails(practiceId, {})
  //     const classId = res.data?.classId
  //     if (!classId) {
  //       next({ name: 'Home' })
  //       return
  //     }
  //     // 获取教师负责的班级
  //     const teacherClassRes = await ClassApi.getTeacherClasses(authStore.user.id)
  //     const teacherClasses = teacherClassRes.data || []
  //     const isTeacherOfClass = teacherClasses.some((cls: any) => String(cls.classId) === String(classId))
  //     if (!isTeacherOfClass) {
  //       next({ name: 'Home' })
  //       return
  //     }
  //   } catch (e) {
  //     next({ name: 'Home' })
  //     return
  //   }
  // }

  next()
})

export default router
