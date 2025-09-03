<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import ClassApi from '../../api/class'

import ClassStudent from '../../components/class/ClassStudent.vue'
import ClassHomework from '../../components/class/ClassHomework.vue'
import ClassProcessing from '../../components/class/ClassProcessing.vue'
// import CourseDiscussion from '../../components/course/CourseDiscussion.vue'

const route = useRoute()
const authStore = useAuthStore()
const aclassId = computed(() => route.params.id)

const loading = ref(true)
const error = ref('')
const aclass = ref<any>(null)
const activeTab = ref('students') // 'students', 'homework', 'processing'

const isTeacherOrTutor = computed(() => {
  return ['teacher', 'tutor'].includes(authStore.userRole)
})

onMounted(async () => {
  try {
    const response:any = await ClassApi.getClassById(aclassId.value)
    console.log('班级详情响应:', response)

    if (response.code === 200 && response.data) {
      // 处理班级数据
      aclass.value = {
        ...response.data,
        name: response.data.className || response.data.name || '未命名班级',
        code: response.data.classCode || response.data.code || '无代码',
        courseName: response.data.courseName || '未知课程'
      }
      console.log('处理后的班级数据:', aclass.value)
    } else {
      error.value = response.message || '获取班级详情失败'
      console.error('获取班级详情失败:', response)
    }
  } catch (err) {
    error.value = '获取班级详情失败，请稍后再试'
    console.error('获取班级详情错误:', err)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="course-detail-container">
    <div v-if="loading" class="loading-container">加载中...</div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <template v-else-if="aclass">
      <header class="course-header">
        <h1>{{ aclass.className || '未命名班级' }}</h1>
        <div class="class-info">
          <p class="info-item">班级代码: {{ aclass.classCode || '无代码' }}</p>
          <p class="info-item">课程名称: {{ aclass.courseName || '未知课程' }}</p>
          <p class="info-item">教师: {{ aclass.teacherName || '未知教师' }}</p>
        </div>
      </header>

      <div class="course-content">
        <!-- Left Sidebar - Tabs -->
        <div class="course-tabs">
          <button
              :class="['tab-button', { active: activeTab === 'students' }]"
              @click="activeTab = 'students'"
          >
            班级成员管理
          </button>
          <button
              :class="['tab-button', { active: activeTab === 'homework' }]"
              @click="activeTab = 'homework'"
          >
            课程作业
          </button>
          <button
              :class="['tab-button', { active: activeTab === 'processing' }]"
              @click="activeTab = 'processing'"
          >
            学习进度
          </button>
        </div>

        <!-- Main Content - Three Panel Layout -->
        <div class="course-main-content">
          <!-- Panel 1: 学生管理 -->
          <ClassStudent
              v-if="activeTab === 'students'"
              :class-id="String(aclassId)"
              :is-teacher="isTeacherOrTutor"
              :teacher-id="aclass.teacherId"
          />

          <!-- Panel 2: 在线练习 -->
          <ClassHomework
              v-else-if="activeTab === 'homework'"
              :class-id="String(aclassId)"
              :is-teacher="isTeacherOrTutor"
          />

          <!-- Panel 3: 学习进度 -->
          <ClassProcessing
              v-else-if="activeTab === 'processing'"
              :class-id="String(aclassId)"
              :is-teacher="isTeacherOrTutor"
          />
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
.course-detail-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 1.5rem;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 3rem 0;
}

.error-message {
  color: #e53935;
  padding: 1rem;
  text-align: center;
  background-color: #ffebee;
  border-radius: 8px;
  margin: 1rem 0;
}

.course-header {
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid #e0e0e0;
}

.course-code {
  color: #616161;
  margin-top: 0.5rem;
}

.course-content {
  display: flex;
  margin-bottom: 2rem;
  gap: 1.5rem;
}

.course-tabs {
  width: 200px;
  flex-shrink: 0;
}

.tab-button {
  display: block;
  width: 100%;
  padding: 1rem;
  margin-bottom: 0.5rem;
  text-align: left;
  background: none;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.2s;
}

.tab-button:hover {
  background-color: #f5f5f5;
}

.tab-button.active {
  background-color: #e3f2fd;
  color: #2c6ecf;
  border-left: 3px solid #2c6ecf;
}

.course-main-content {
  flex: 1;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.course-discussion-section {
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px solid #e0e0e0;
}

.class-info {
  margin-top: 1rem;
  padding: 1rem;
  background-color: #f5f5f5;
  border-radius: 8px;
}

.info-item {
  margin: 0.5rem 0;
  color: #424242;
  font-size: 0.95rem;
}

.info-item:first-child {
  margin-top: 0;
}

.info-item:last-child {
  margin-bottom: 0;
}

@media (max-width: 768px) {
  .course-content {
    flex-direction: column;
  }

  .course-tabs {
    width: 100%;
    display: flex;
    overflow-x: auto;
    margin-bottom: 1rem;
  }

  .tab-button {
    flex: 0 0 auto;
    margin-right: 0.5rem;
    margin-bottom: 0;
    white-space: nowrap;
  }
}
</style>
