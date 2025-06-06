<script setup lang="ts">
import { computed, ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import ThemeToggle from '../littlecomponents/ThemeToggle.vue'

const router = useRouter()
const authStore = useAuthStore()

const isLoggedIn = computed(() => authStore.isAuthenticated)
const username = computed(() => authStore.user?.username || '')

// 时钟相关
const currentTime = ref(new Date())
let timeInterval: NodeJS.Timeout | null = null

const formatTime = computed(() => {
  return currentTime.value.toLocaleTimeString('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
})

const formatDate = computed(() => {
  return currentTime.value.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  })
})

onMounted(() => {
  // 每秒更新时间
  timeInterval = setInterval(() => {
    currentTime.value = new Date()
  }, 1000)
})

onUnmounted(() => {
  if (timeInterval) {
    clearInterval(timeInterval)
  }
})

const handleLogout = async () => {
  await authStore.logout()
  router.push('/')
}
</script>
<script lang="ts">
export default {
  name: 'AppHeader' // 显式设置组件名
}
</script>
<template>
  <header class="app-header">
    <div class="header-container">
      <div class="logo">
        <router-link to="/">
          <h1 >EduSoft</h1>
        </router-link>
      </div>

      <nav class="main-nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/questionBank" class="nav-link" v-if="isLoggedIn">题库</router-link>
        <router-link to="/learning-records" class="nav-link" v-if="isLoggedIn">学习记录</router-link>
      </nav>      <div class="auth-section">
        <div class="clock-display">
          <div class="time">{{ formatTime }}</div>
          <div class="date">{{ formatDate }}</div>
        </div>
        <ThemeToggle />
        <template v-if="isLoggedIn">
          <span class="user-greeting">你好，{{ username }}</span>
          <button class="logout-btn" @click="handleLogout">登出</button>
        </template>
        <template v-else>
          <router-link to="/login" class="btn-auth login">登录</router-link>
          <router-link to="/register" class="btn-auth register">注册</router-link>
        </template>
      </div>
    </div>
  </header>
</template>

<style scoped>
.app-header {
  background: url('@/assets/sky.png') center/cover;
  background-color: #2c6ecf; /* 备用颜色 */
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: relative;
}

.app-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(44, 110, 207, 0.3);
  z-index: 1;
}

.header-container {
  position: relative;
  z-index: 2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1280px;
  margin: 0 auto;
  padding: 1rem;
  height: 64px;
}

.logo {
  font-size: 0.875rem;
}

.logo a {
  color: white;
  text-decoration: none;
}

.logo h1 {
  margin: 0;
  font-size: 2.25rem;
  font-family: "Pinyon Script", cursive;
  font-weight: 800;
  font-style: normal;
}

.main-nav {
  display: flex;
  gap: 1.5rem;
}

.nav-link {
  color: rgba(255, 255, 255, 0.9);
  text-decoration: none;
  font-weight: 500;
  padding: 0.5rem;
  border-radius: 4px;
  transition: all 0.2s;
}

.nav-link.router-link-active {
  color: white;
  background-color: rgba(255, 255, 255, 0.15);
}

.nav-link:hover {
  color: white;
  background-color: rgba(255, 255, 255, 0.1);
}

.auth-section {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.user-greeting {
  margin-right: 0.5rem;
  font-weight: 500;
}

.btn-auth {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.2s;
}

.login {
  color: white;
  background-color: transparent;
  border: 1px solid rgba(255, 255, 255, 0.5);
}

.login:hover {
  background-color: rgba(255, 255, 255, 0.1);
  border-color: white;
}

.register {
  color: #2c6ecf;
  background-color: white;
}

.register:hover {
  background-color: rgba(255, 255, 255, 0.9);
}

.logout-btn {
  padding: 0.5rem 1rem;
  background-color: rgba(255, 255, 255, 0.1);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.logout-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.clock-display {
  display: flex;
  flex-direction: column;
  align-items: center;
  font-size: 1rem;
  margin-right: 1rem;
  background: rgba(255, 255, 255, 0.1);
  padding: 0.5rem;
  border-radius: 8px;
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.time {
  font-weight: 600;
  font-size: 1rem;
  letter-spacing: 1px;
}

.date {
  color: rgba(255, 255, 255, 0.8);
  font-size: 0.75rem;
  margin-top: 2px;
}

.clock {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  font-size: 0.875rem;
}

.current-time {
  font-weight: 500;
}

.current-date {
  color: rgba(255, 255, 255, 0.7);
}

@media (max-width: 768px) {
  .header-container {
    flex-wrap: wrap;
    height: auto;
    padding: 0.75rem;
  }

  .logo {
    flex: 1;
  }

  .main-nav {
    order: 3;
    width: 100%;
    margin-top: 0.75rem;
    justify-content: space-around;
  }

  .auth-section {
    flex: 0 0 auto;
  }
}
</style>
