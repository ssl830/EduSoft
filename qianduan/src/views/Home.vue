<template>
  <div class="home-page">
    <!-- 动态背景 -->
    <Background />

    <div class="home-container">
      <section class="welcome-section">
        <div class="welcome-content">
          <h1>欢迎使用EduSoft平台</h1>
          <p v-if="authStore.isAuthenticated">
            {{ authStore.user?.username }}，您好！开始您的学习之旅吧。
          </p>
          <p v-else>
            请<router-link to="/login" class="auth-link">登录</router-link>或<router-link to="/register" class="auth-link">注册</router-link>以开始使用所有功能。
          </p>
        </div>
      </section>

      <section v-if="authStore.isAuthenticated" class="courses-section">
        <div class="section-header">
          <h2>我的课程</h2>
            <router-link to="/course/create">
                <button
                    v-if="authStore.userRole === 'teacher'"
                    class="btn-primary"
                >
                    创建课程
                </button>
            </router-link>
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
          <div class="feature-card" style="--i: 1">
            <h3>课程管理</h3>
            <p>轻松创建和管理课程，上传教学资料，追踪学生进度。</p>
          </div>
          <div class="feature-card" style="--i: 2">
            <h3>在线练习</h3>
            <p>支持多种题型，自动评分，实时反馈，助力学习提升。</p>
          </div>
          <div class="feature-card" style="--i: 3">
            <h3>学习记录</h3>
            <p>查看学习轨迹，分析进步情况，导出学习报告。</p>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth'
import CourseApi from '../api/course'
import Background from '../components/layout/Background.vue'

const authStore = useAuthStore()
const courses = ref([])
const loading = ref(true)
const error = ref('')

onMounted(async () => {
  console.log('Home.vue mounted, Background component loaded')

  if (authStore.isAuthenticated) {
    try {
      const response = await CourseApi.getUserCourses(authStore.user?.id)
      courses.value = response.data.data
        console.log(courses.value)
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

<style scoped>
.home-page {
  position: relative;
  min-height: 100vh;
  padding: 20px 0;
  overflow-x: hidden;
  background: transparent;
}

.home-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  position: relative;
  z-index: 10;
}

.welcome-section {
  margin-bottom: 3rem;
  text-align: center;
  padding: 4rem 0;
}

.welcome-content {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  padding: 3rem;
  border-radius: 20px;
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: fadeInUp 0.8s ease-out forwards;
  max-width: 800px;
  margin: 0 auto;
  transition: all 0.3s ease;
}

.welcome-content:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-5px);
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
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
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.feature-card {
  padding: 2rem;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  box-shadow:
    0 8px 32px rgba(0, 0, 0, 0.1),
    inset 0 1px 0 rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.15);
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
  animation: slideInLeft 0.8s ease-out forwards;
  animation-delay: calc(var(--i, 0) * 0.2s);
  opacity: 0;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.1), transparent);
  transition: left 0.6s;
}

.feature-card:hover::before {
  left: 100%;
}

.feature-card:hover {
  transform: translateY(-8px) scale(1.02);
  background: rgba(255, 255, 255, 0.15);
  box-shadow:
    0 20px 40px rgba(0, 0, 0, 0.15),
    inset 0 1px 0 rgba(255, 255, 255, 0.3);
  border-color: rgba(255, 255, 255, 0.3);
}

.feature-card h3 {
  color: #fff;
  font-size: 1.4rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.feature-card p {
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.6;
  font-weight: 400;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.loading, .empty-state {
  text-align: center;
  padding: 2rem;
  color: #fff;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.4);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 16px;
  margin: 1rem 0;
  border: 1px solid rgba(255, 255, 255, 0.2);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.error-message {
  color: #ff8a80;
  text-align: center;
  padding: 1.5rem;
  background: rgba(255, 82, 82, 0.15);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border-radius: 16px;
  border: 1px solid rgba(255, 82, 82, 0.3);
  box-shadow: 0 8px 32px rgba(255, 82, 82, 0.1);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.8rem 1.6rem;
  border-radius: 50px;
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  box-shadow:
    0 4px 15px rgba(102, 126, 234, 0.4),
    0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 0.5px;
  text-transform: uppercase;
  font-size: 0.9rem;
  position: relative;
  overflow: hidden;
}

.btn-primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s;
}

.btn-primary:hover::before {
  left: 100%;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #5a6fd8 0%, #6a4190 100%);
  box-shadow:
    0 8px 25px rgba(102, 126, 234, 0.5),
    0 4px 8px rgba(0, 0, 0, 0.15);
  transform: translateY(-3px) scale(1.05);
}

.btn-primary:active {
  transform: translateY(-1px) scale(1.02);
}

h1 {
  color: #fff;
  font-size: 3rem;
  margin-bottom: 1.5rem;
  font-weight: 800;
  text-shadow: 0 4px 8px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #fff 0%, #e8f4fd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  line-height: 1.2;
}

h2 {
  color: #fff;
  text-shadow: 0 3px 6px rgba(0, 0, 0, 0.3);
  font-weight: 700;
  margin-bottom: 2rem;
  font-size: 2.2rem;
  background: linear-gradient(135deg, #fff 0%, #e8f4fd 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.welcome-content p {
  color: rgba(255, 255, 255, 0.95);
  font-size: 1.2rem;
  line-height: 1.6;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.auth-link {
  color: #64b5f6;
  font-weight: 600;
  text-decoration: none;
  padding: 0.2rem 0.5rem;
  border-radius: 6px;
  background: rgba(100, 181, 246, 0.1);
  border: 1px solid rgba(100, 181, 246, 0.3);
  transition: all 0.3s ease;
  text-shadow: none;
}

.auth-link:hover {
  color: #fff;
  background: rgba(100, 181, 246, 0.2);
  border-color: rgba(100, 181, 246, 0.5);
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(100, 181, 246, 0.3);
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInLeft {
  from {
    opacity: 0;
    transform: translateX(-30px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@media (max-width: 768px) {
  .home-container {
    padding: 1rem 0.5rem;
  }

  .welcome-content {
    padding: 2rem 1.5rem;
    margin: 0 1rem;
  }

  h1 {
    font-size: 2.2rem;
  }

  h2 {
    font-size: 1.8rem;
  }

  .features-grid {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }

  .feature-card {
    padding: 1.5rem;
  }
}

@media (max-width: 480px) {
  .welcome-section {
    padding: 2rem 0;
  }

  .welcome-content {
    padding: 1.5rem 1rem;
  }

  h1 {
    font-size: 1.8rem;
  }

  .welcome-content p {
    font-size: 1rem;
  }
}

.home-page * {
  will-change: transform;
}

.feature-card,
.welcome-content,
.btn-primary {
  transform: translateZ(0);
  backface-visibility: hidden;
}
</style>
