"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var vue_router_1 = require("vue-router");
// import { useAuthStore } from '../stores/auth'
var Home_vue_1 = require("../views/Home.vue");
var Login_vue_1 = require("../views/auth/Login.vue");
var Register_vue_1 = require("../views/auth/Register.vue");
var NotFound_vue_1 = require("../views/NotFound.vue");
var DiscussionList_vue_1 = require("../views/discuss/DiscussionList.vue");
var ThreadDetail_vue_1 = require("../views/discuss/ThreadDetail.vue");
var LearningRecordsAnalysis_vue_1 = require("@/views/records/LearningRecordsAnalysis.vue");
var routes = [
    {
        path: '/',
        name: 'Home',
        component: Home_vue_1.default,
        meta: {
            title: '首页',
            requiresAuth: false,
            showSidebar: true
        }
    },
    {
        path: '/login',
        name: 'Login',
        component: Login_vue_1.default,
        meta: {
            title: '登录',
            requiresAuth: false,
            showSidebar: false
        }
    },
    {
        path: '/register',
        name: 'Register',
        component: Register_vue_1.default,
        meta: {
            title: '注册',
            requiresAuth: false,
            showSidebar: false
        }
    },
    {
        path: '/class',
        name: 'Class',
        component: function () { return Promise.resolve().then(function () { return require('../views/class/ClassList.vue'); }); },
        meta: {
            title: '班级列表',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/class/create',
        name: 'ClassCreate',
        component: function () { return Promise.resolve().then(function () { return require('../views/class/ClassCreate.vue'); }); },
        meta: {
            title: '创建班级',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/class/:id',
        name: 'ClassDetail',
        component: function () { return Promise.resolve().then(function () { return require('../views/class/ClassDetail.vue'); }); },
        meta: {
            title: '班级详情',
            requiresAuth: true,
            showSidebar: true
        }
    }, {
        path: '/course/:id',
        name: 'CourseDetail',
        component: function () { return Promise.resolve().then(function () { return require('../views/course/CourseDetail.vue'); }); },
        meta: {
            title: '课程详情',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/course/create',
        name: 'CourseCreate',
        component: function () { return Promise.resolve().then(function () { return require('../views/course/CourseCreate.vue'); }); },
        meta: {
            title: '创建课程',
            requiresAuth: true,
            showSidebar: true
        }
    }, {
        path: '/exercise/create',
        name: 'ExerciseCreate',
        component: function () { return Promise.resolve().then(function () { return require('../views/exercise/ExerciseCreate.vue'); }); },
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
        component: function () { return Promise.resolve().then(function () { return require('../views/record/StudyRecordList.vue'); }); },
        meta: {
            title: '学习记录',
            requiresAuth: true,
            roles: ['student'],
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
        component: function () { return Promise.resolve().then(function () { return require('../views/exercise/ExerciseTake.vue'); }); },
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
        component: function () { return Promise.resolve().then(function () { return require('../views/exercise/CheckExercise.vue'); }); },
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
        component: function () { return Promise.resolve().then(function () { return require('../views/exercise/ExerciseFeedback.vue'); }); },
        meta: {
            title: '练习反馈',
            requiresAuth: true,
            roles: ['student', 'teacher'], // TODO
            showSidebar: false
        }
    }, // {
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
        component: function () { return Promise.resolve().then(function () { return require('../views/question/QuestionBank.vue'); }); },
        meta: {
            title: '题库',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/questionFavor',
        name: 'QuestionFavor',
        component: function () { return Promise.resolve().then(function () { return require('../views/question/QuestionFavor.vue'); }); },
        meta: {
            title: '收藏题库',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/questionWrong',
        name: 'QuestionWrong',
        component: function () { return Promise.resolve().then(function () { return require('../views/question/QuestionWrong.vue'); }); },
        meta: {
            title: '错误题库',
            requiresAuth: true, showSidebar: true
        }
    },
    {
        path: '/study-records',
        name: 'StudyRecordList',
        component: function () { return Promise.resolve().then(function () { return require('../views/record/StudyRecordList.vue'); }); },
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
        component: function () { return Promise.resolve().then(function () { return require('../views/records/LearningRecords.vue'); }); },
        meta: {
            title: '学习记录',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/profile',
        name: 'UserProfile',
        component: function () { return Promise.resolve().then(function () { return require('../views/profile/UserProfile.vue'); }); },
        meta: {
            title: '个人信息',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/courses/:courseId/discussions',
        name: 'CourseDiscussionList',
        component: DiscussionList_vue_1.default,
        props: true,
        meta: { requiresAuth: true }
    },
    {
        path: '/discussions',
        name: 'GeneralDiscussionList',
        component: DiscussionList_vue_1.default,
        meta: {
            requiresAuth: true,
            showSidebar: true // 显示侧边栏
        }
    },
    {
        path: '/discussions/:threadId',
        name: 'ThreadDetail',
        component: ThreadDetail_vue_1.default,
        props: true, // Pass route params as props to the component
        meta: {
            requiresAuth: true,
            showSidebar: true // 显示侧边栏
        }
    },
    {
        path: '/help',
        name: 'HelpAndFeedback',
        component: function () { return Promise.resolve().then(function () { return require('@/views/help/HelpAndFeedback.vue'); }); }, // Changed path
        meta: {
            title: '帮助与反馈',
            requiresAuth: false,
            showSidebar: true
        }
    },
    {
        path: '/notifications',
        name: 'Notification',
        component: function () { return Promise.resolve().then(function () { return require('../views/notification/Notification.vue'); }); },
        meta: {
            title: '通知中心',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/exercise/edit/:id',
        name: 'ExerciseEdit',
        component: function () { return Promise.resolve().then(function () { return require('../views/exercise/ExerciseEdit.vue'); }); },
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
        component: function () { return Promise.resolve().then(function () { return require('../views/ScheduleView.vue'); }); },
        meta: {
            title: '课表',
            requiresAuth: true,
            showSidebar: true
        }
    },
    {
        path: '/learning-records-analysis',
        name: 'LearningRecordsAnalysis',
        component: LearningRecordsAnalysis_vue_1.default,
    },
    {
        path: '/:pathMatch(.*)*',
        name: 'NotFound',
        component: NotFound_vue_1.default,
        meta: {
            title: '页面未找到',
            requiresAuth: false,
            showSidebar: false
        }
    }
];
var router = (0, vue_router_1.createRouter)({
    history: (0, vue_router_1.createWebHistory)(),
    routes: routes,
    scrollBehavior: function () {
        return { top: 0 };
    }
});
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
exports.default = router;
