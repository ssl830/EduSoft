<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { defineProps } from 'vue'
import { useRouter } from 'vue-router'
import ExerciseApi from '../../api/exercise'

const props = defineProps<{
  courseId: string;
  isTeacher: boolean;
}>()

const router = useRouter()
const exercises = ref([])
const loading = ref(true)
const error = ref('')

// Fetch exercises
const fetchExercises = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await ExerciseApi.getCourseExercises(props.courseId)
    exercises.value = response.data
  } catch (err) {
    error.value = '获取练习列表失败，请稍后再试'
    console.error(err)
  } finally {
    loading.value = false
  }
}

// Navigate to exercise creation page
const createExercise = () => {
  router.push({ 
    name: 'ExerciseCreate',
    query: { courseId: props.courseId }
  })
}

// Take exercise
const takeExercise = (exercise: any) => {
  router.push({
    name: 'ExerciseTake',
    params: { id: exercise.id }
  })
}

// View exercise feedback
const viewFeedback = (exercise: any) => {
  router.push({
    name: 'ExerciseFeedback',
    params: { id: exercise.id }
  })
}

// Check if exercise is in progress for current student
const isExerciseInProgress = (exercise: any) => {
  const now = new Date()
  const startTime = new Date(exercise.startTime)
  const endTime = new Date(exercise.endTime)
  
  return now >= startTime && now <= endTime
}

// Format date
const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  return date.toLocaleString()
}

onMounted(() => {
  fetchExercises()
})
</script>

<template>
  <div class="exercise-list-container">
    <div class="exercise-header">
      <h2>在线练习</h2>
      <button 
        v-if="isTeacher" 
        class="btn-primary"
        @click="createExercise"
      >
        新建练习
      </button>
    </div>
    
    <div v-if="loading" class="loading-container">加载中...</div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <div v-else-if="exercises.length === 0" class="empty-state">
      暂无练习
    </div>
    <div v-else class="exercise-list">
      <div 
        v-for="exercise in exercises" 
        :key="exercise.id"
        class="exercise-item"
      >
        <div class="exercise-item-header">
          <h3 class="exercise-title">{{ exercise.title }}</h3>
          <div class="exercise-status" :class="exercise.status">
            {{ 
              exercise.status === 'not_started' ? '未开始' : 
              exercise.status === 'in_progress' ? '进行中' : 
              exercise.status === 'completed' ? '已完成' : '已截止'
            }}
          </div>
        </div>
        
        <div class="exercise-item-content">
          <div class="exercise-info">
            <div class="info-row">
              <span class="info-label">开始时间:</span>
              <span class="info-value">{{ formatDate(exercise.startTime) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">截止时间:</span>
              <span class="info-value">{{ formatDate(exercise.endTime) }}</span>
            </div>
            <div class="info-row">
              <span class="info-label">总分:</span>
              <span class="info-value">{{ exercise.totalPoints }}分</span>
            </div>
            <div class="info-row" v-if="exercise.score !== undefined">
              <span class="info-label">得分:</span>
              <span class="info-value score">{{ exercise.score }}分</span>
            </div>
          </div>
          
          <div class="exercise-actions">
            <template v-if="isTeacher">
              <button class="btn-secondary">查看详情</button>
              <button class="btn-secondary">查看统计</button>
            </template>
            <template v-else>
              <button 
                v-if="isExerciseInProgress(exercise) && exercise.status !== 'completed'"
                class="btn-primary"
                @click="takeExercise(exercise)"
              >
                做题
              </button>
              <button 
                v-if="exercise.status === 'completed'"
                class="btn-secondary"
                @click="viewFeedback(exercise)"
              >
                查看结果
              </button>
            </template>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.exercise-list-container {
  padding: 1.5rem;
}

.exercise-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.exercise-header h2 {
  margin: 0;
}

.exercise-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.exercise-item {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  border: 1px solid #e0e0e0;
}

.exercise-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem 1.5rem;
  background-color: #f5f5f5;
  border-bottom: 1px solid #e0e0e0;
}

.exercise-title {
  margin: 0;
  font-size: 1.125rem;
}

.exercise-status {
  padding: 0.25rem 0.75rem;
  border-radius: 4px;
  font-size: 0.875rem;
  font-weight: 500;
}

.exercise-status.not_started {
  background-color: #e0e0e0;
  color: #616161;
}

.exercise-status.in_progress {
  background-color: #e3f2fd;
  color: #1976d2;
}

.exercise-status.completed {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.exercise-status.expired {
  background-color: #ffebee;
  color: #c62828;
}

.exercise-item-content {
  padding: 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.exercise-info {
  flex: 1;
}

.info-row {
  margin-bottom: 0.5rem;
  display: flex;
}

.info-label {
  width: 5rem;
  color: #757575;
}

.info-value {
  font-weight: 500;
}

.info-value.score {
  color: #2e7d32;
}

.exercise-actions {
  display: flex;
  gap: 0.75rem;
}

.empty-state {
  padding: 2rem;
  text-align: center;
  background-color: #f5f5f5;
  border-radius: 8px;
  color: #757575;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 2rem;
  color: #616161;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
}

.btn-primary, .btn-secondary {
  padding: 0.5rem 1rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s;
}

.btn-primary {
  background-color: #2c6ecf;
  color: white;
}

.btn-primary:hover {
  background-color: #215bb4;
}

.btn-secondary {
  background-color: #f5f5f5;
  color: #424242;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

@media (max-width: 768px) {
  .exercise-item-content {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .exercise-info {
    margin-bottom: 1rem;
  }
  
  .exercise-actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>