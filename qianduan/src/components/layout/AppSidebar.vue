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
        <li v-if="isTeacher">
          <router-link to="/course/create" class="sidebar-link">
            <span class="icon">➕</span> 创建课程
          </router-link>
        </li>
        <li v-if="isTeacher || isAssistant">
          <router-link to="/exercise/create" class="sidebar-link">
            <span class="icon">📝</span> 创建练习
          </router-link>
        </li>
        <li>
          <router-link to="/question-bank" class="sidebar-link">
            <span class="icon">📚</span> 题库中心
          </router-link>
        </li>
        <li>
          <router-link to="/learning-records" class="sidebar-link">
            <span class="icon">📊</span> 学习记录
          </router-link>
        </li>
      </ul>
    </div>
    
    <div class="sidebar-section" v-if="isTeacher || isAssistant">
      <h3 class="sidebar-title">教师工具</h3>
      <ul class="sidebar-menu">
        <li v-if="isTeacher">
          <router-link to="/class/manage" class="sidebar-link">
            <span class="icon">👥</span> 班级管理
          </router-link>
        </li>
        <li>
          <router-link to="/exercise/grading" class="sidebar-link">
            <span class="icon">✓</span> 批阅练习
          </router-link>
        </li>
        <li>
          <router-link to="/statistics" class="sidebar-link">
            <span class="icon">📈</span> 统计分析
          </router-link>
        </li>
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
  background-color: #f5f7fa;
  border-right: 1px solid #e0e0e0;
  padding: 1.5rem 0;
  overflow-y: auto;
  flex-shrink: 0;
}

.sidebar-section {
  margin-bottom: 1.5rem;
}

.sidebar-title {
  padding: 0 1.5rem;
  margin-bottom: 0.75rem;
  font-size: 0.875rem;
  font-weight: 600;
  color: #757575;
  text-transform: uppercase;
  letter-spacing: 0.5px;
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
  color: #424242;
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
  background-color: rgba(0, 0, 0, 0.03);
  color: #2c6ecf;
}

.sidebar-link.router-link-active {
  background-color: rgba(44, 110, 207, 0.1);
  color: #2c6ecf;
  border-left: 3px solid #2c6ecf;
}

.icon {
  margin-right: 0.75rem;
  font-size: 1.125rem;
}

.sidebar-divider {
  height: 1px;
  background-color: #e0e0e0;
  margin: 1rem 1.5rem;
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
    border-bottom: 1px solid #e0e0e0;
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