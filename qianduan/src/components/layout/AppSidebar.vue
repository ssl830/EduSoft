<script setup lang="ts">
import { computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()

const isTeacher = computed(() => authStore.userRole === 'teacher')
const isAssistant = computed(() => authStore.userRole === 'assistant')
const isStudent = computed(() => authStore.userRole === 'student')
const isTutor = computed(() => authStore.userRole === 'tutor')

</script>
<script lang="ts">
export default {
  name: 'AppSidebar' // 显式设置组件名
}
</script>

<template>
  <aside class="app-sidebar">
    <div class="sidebar-section">
      <h3 class="sidebar-title">快速导航</h3>
      <ul class="sidebar-menu">
        <li>
          <router-link to="/" class="sidebar-link">
            <span class="icon">🏠</span> 课程中心
          </router-link>
        </li>
        <li>
          <router-link to="/class" class="sidebar-link">
              <span class="icon">👥</span> 班级中心
          </router-link>
        </li>

        <li>
          <router-link to="/questionBank" class="sidebar-link">
            <span class="icon">📚</span> 题库中心
          </router-link>
        </li>
          <li>
              <router-link to="/questionFavor" class="sidebar-link">
                  <span class="icon">📚</span> 收藏题库
              </router-link>
          </li>
          <li>
              <router-link to="/questionWrong" class="sidebar-link">
                  <span class="icon">📈</span> 错误题库
              </router-link>
          </li>
        <li>
          <router-link to="/CourseCalendar" class="sidebar-link">
            <i class="fa fa-calendar-check-o fa-lg"></i><span class="icon-text-spacer"></span>我的课表
          </router-link>
        </li>
        <li>
          <router-link to="/learning-records" class="sidebar-link">
            <span class="icon">📊</span> 学习记录
          </router-link>
        </li>
        <li>
          <router-link to="/notifications" class="sidebar-link">
            <span class="icon">🔔</span> 通知中心
          </router-link>
        </li>
        <li>
          <router-link to="/discussions" class="sidebar-link">
            <span class="icon">💬</span> 讨论区
          </router-link>
        </li>
      </ul>
    </div>

    <div class="sidebar-section" ><!--      v-if="isTeacher || isTutor"-->
      <h3 class="sidebar-title">教师工具</h3>
      <ul class="sidebar-menu">
        <li>
          <router-link to="/course/create" class="sidebar-link">
              <span class="icon">➕</span> 创建课程
          </router-link>
        </li>
        <li>
          <router-link to="/exercise/create" class="sidebar-link">
              <span class="icon">📝</span> 创建练习
          </router-link>
        </li>
<!--        <li>-->
<!--          <router-link to="/exercise/grading" class="sidebar-link">-->
<!--            <span class="icon">✓</span> 批阅练习-->
<!--          </router-link>-->
<!--        </li>-->
<!--        <li>-->
<!--          <router-link to="/statistics" class="sidebar-link">-->
<!--            <span class="icon">📈</span> 统计分析-->
<!--          </router-link>-->
<!--        </li>-->
      </ul>
    </div>
    <div class="sidebar-divider"></div>

    <div class="sidebar-section">
      <h3 class="sidebar-title">账户</h3>
      <ul class="sidebar-menu">
        <li>
          <router-link to="/profile" class="sidebar-link">
            <span class="icon">👤</span> 个人信息
          </router-link>
        </li>
        <li>
          <button @click="authStore.logout()" class="sidebar-link logout">
            <span class="icon">🚪</span> 退出登录
          </button>
        </li>
      </ul>
    </div>
  </aside>
</template>

<style scoped>
.app-sidebar {
  width: 240px;
  background-color: var(--bg-secondary);
  border-right: 1px solid var(--border-color);
  padding: 1.5rem 0;
  overflow-y: auto;
  flex-shrink: 0;
  transition: background-color var(--transition-normal), border-color var(--transition-normal);
}

.sidebar-section {
  margin-bottom: 1.5rem;
}

.sidebar-title {
  padding: 0 1.5rem;
  margin-bottom: 0.75rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: var(--text-secondary);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  transition: color var(--transition-normal);
}

.sidebar-menu {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sidebar-link {
  display: flex;
  align-items: center;
  padding: 0.75rem 1.5rem;
  color: var(--text-primary);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
  cursor: pointer;
  border: none;
  background: none;
  width: 100%;
  text-align: left;
  font-size: 1rem;
}

.sidebar-link:hover {
  background-color: var(--bg-tertiary);
  color: var(--primary);
}

.sidebar-link.router-link-active {
  background-color: var(--primary-light);
  color: var(--primary);
  border-left: 3px solid var(--primary);
}

.icon {
  margin-right: 0.75rem;
  font-size: 1.125rem;
}

.icon-text-spacer {
  display: inline-block;
  width: 0.75rem;
}

.sidebar-divider {
  height: 1px;
  background-color: var(--border-color);
  margin: 1rem 1.5rem;
  transition: background-color var(--transition-normal);
}

.logout {
  color: #f44336;
}

.logout:hover {
  background-color: rgba(244, 67, 54, 0.05);
  color: #d32f2f;
}

@media (max-width: 768px) {
  .app-sidebar {
    width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--border-color);
    padding: 1rem 0;
  }

  .sidebar-menu {
    display: flex;
    flex-wrap: wrap;
  }

  .sidebar-menu li {
    flex: 1 0 50%;
    min-width: 160px;
  }

  .sidebar-link {
    padding: 0.5rem 1rem;
  }
}
</style>
