<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import CourseApi from '../../api/course'

import CourseSyllabus from '../../components/course/CourseSyllabus.vue'
import CourseResourceList from '../../components/course/CourseResourceList.vue'
import CourseVideoList from '../../components/course/CourseVideoList.vue'

const route = useRoute()
const authStore = useAuthStore()
const courseId = computed(() => route.params.id as string)

const loading = ref(true)
const error = ref('')
const course = ref<any>(null)
const activeTab = ref('syllabus') // 'syllabus', 'resources', 'video'

const isTeacherOrTutor = computed(() => {
  return ['teacher', 'tutor'].includes(authStore.userRole)
})

onMounted(async () => {
  try {
    const response = await CourseApi.getCourseById(courseId.value)
    course.value = response.data.data
  } catch (err) {
    error.value = '获取课程详情失败，请稍后再试'
    console.error(err)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="course-detail-container">
    <div v-if="loading" class="loading-container">加载中...</div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <template v-else-if="course">
      <header class="course-header">
        <h1>{{ course.name }} {{ course.code }}</h1>
      </header>

      <div class="course-content">
        <!-- Left Sidebar - Tabs -->
        <div class="course-tabs">
          <button
            :class="['tab-button', { active: activeTab === 'syllabus' }]"
            @click="activeTab = 'syllabus'"
          >
            课程概况
          </button>
          <button
            :class="['tab-button', { active: activeTab === 'resources' }]"
            @click="activeTab = 'resources'"
          >
            教学资料
          </button>
            <button
                :class="['tab-button', { active: activeTab === 'video' }]"
                @click="activeTab = 'video'"
            >
                视频学习
            </button>
        </div>

        <!-- Main Content - Three Panel Layout -->
        <div class="course-main-content">
          <!-- Panel 1: Syllabus, objectives, assessment -->
          <CourseSyllabus
            v-if="activeTab === 'syllabus'"
            :course="course"
          />

          <!-- Panel 2: Teaching resources -->
          <CourseResourceList
            v-else-if="activeTab === 'resources'"
            :course-id="courseId"
            :is-teacher="isTeacherOrTutor"
          />
            <CourseVideoList
                v-else-if="activeTab === 'video'"
                :course-id="courseId"
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
