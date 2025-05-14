<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import CourseCard from '../components/course/CourseCard.vue'
import CourseApi from '../api/course'

const authStore = useAuthStore()
const courses = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  if (authStore.isAuthenticated) {
    try {
      const response = await CourseApi.getUserCourses()
      courses.value = response.data
    } catch (err) {
      error.value = '获取课程列表失败，请稍后再试'
      console.error(err)
    } finally {
      loading.value = false
    }
  } else {
    loading.value = false
  }
})
</script>

<template>
  <div class="home-container">
    <section class="welcome-section">
      <h1>欢迎使用课程管理平台</h1>
      <p v-if="authStore.isAuthenticated">
        {{ authStore.user?.user_name }}，您好！开始您的学习之旅吧。
      </p>
      <p v-else>
        请<router-link to="/login">登录</router-link>或<router-link to="/register">注册</router-link>以开始使用所有功能。
      </p>
    </section>

    <section v-if="authStore.isAuthenticated" class="courses-section">
      <div class="section-header">
        <h2>我的课程</h2>
        <button
          v-if="authStore.userRole === 'teacher'"
          class="btn-primary"
        >
          创建课程
        </button>
      </div>

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="error" class="error-message">{{ error }}</div>
      <div v-else-if="courses.length === 0" class="empty-state">
        <p>您还没有参与任何课程</p>
      </div>
      <div v-else class="course-grid">
        <CourseCard
          v-for="course in courses"
          :key="course.id"
          :course="course"
        />
      </div>
    </section>

    <section v-else class="features-section">
      <h2>平台特色</h2>
      <div class="features-grid">
        <div class="feature-card">
          <h3>课程管理</h3>
          <p>轻松创建和管理课程，上传教学资料，追踪学生进度。</p>
        </div>
        <div class="feature-card">
          <h3>在线练习</h3>
          <p>支持多种题型，自动评分，实时反馈，助力学习提升。</p>
        </div>
        <div class="feature-card">
          <h3>学习记录</h3>
          <p>查看学习轨迹，分析进步情况，导出学习报告。</p>
        </div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.welcome-section {
  margin-bottom: 2rem;
  text-align: center;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.course-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
  margin-top: 1.5rem;
}

.feature-card {
  padding: 1.5rem;
  border-radius: 8px;
  background-color: #f7f9fc;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.loading, .empty-state {
  text-align: center;
  padding: 2rem;
  color: #666;
}

.error-message {
  color: #e53935;
  text-align: center;
  padding: 1rem;
}

.btn-primary {
  background-color: #2c6ecf;
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: background-color 0.2s;
}

.btn-primary:hover {
  background-color: #215bb4;
}
</style>
