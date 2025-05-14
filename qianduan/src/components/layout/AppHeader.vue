<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const isLoggedIn = computed(() => authStore.isAuthenticated)
const username = computed(() => authStore.user?.user_name || '')

const handleLogout = async () => {
  await authStore.logout()
  router.push('/')
}
</script>

<template>
  <header class="app-header">
    <div class="header-container">
      <div class="logo">
        <router-link to="/">
          <h1>课程管理平台</h1>
        </router-link>
      </div>

      <nav class="main-nav">
        <router-link to="/" class="nav-link">首页</router-link>
        <router-link to="/question-bank" class="nav-link" v-if="isLoggedIn">题库</router-link>
        <router-link to="/learning-records" class="nav-link" v-if="isLoggedIn">学习记录</router-link>
      </nav>

      <div class="auth-section">
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
  background-color: #2c6ecf;
  color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header-container {
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
  font-size: 1.25rem;
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
