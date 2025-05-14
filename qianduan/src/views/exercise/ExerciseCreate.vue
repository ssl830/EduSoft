<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import ExerciseApi from '../../api/exercise'

const router = useRouter()

interface Question {
  type: string;
  content: string;
  options?: Array<{ key: string; text: string }>;
  answer?: string | string[];
  points: number;
}

const exercise = reactive({
  title: '',
  courseId: '',
  classIds: [] as string[],
  startTime: '',
  endTime: '',
  timeLimit: 0,
  allowedAttempts: 0,
  questions: [] as Question[]
})

const loading = ref(false)
const error = ref('')
const courses = ref([])
const classes = ref([])
const selectedQuestion = ref<Question | null>(null)
const isEditingQuestion = ref(false)
const currentStep = ref(1) // 1: Basic Info, 2: Questions

// Temporary question data for editing
const tempQuestion = reactive({
  type: 'single_choice',
  content: '',
  options: [
    { key: 'A', text: '' },
    { key: 'B', text: '' },
    { key: 'C', text: '' },
    { key: 'D', text: '' }
  ],
  answer: '',
  points: 5
})

// Fetch courses on component mount
const fetchCourses = async () => {
  try {
    const response = await ExerciseApi.getTeacherCourses()
    courses.value = response.data
  } catch (err) {
    error.value = '获取课程列表失败'
    console.error(err)
  }
}

// Fetch classes when a course is selected
const fetchClasses = async (courseId: string) => {
  try {
    const response = await ExerciseApi.getCourseClasses(courseId)
    classes.value = response.data
    exercise.classIds = []
  } catch (err) {
    error.value = '获取班级列表失败'
    console.error(err)
  }
}

// Move to the next step in the exercise creation process
const nextStep = () => {
  if (currentStep.value === 1) {
    // Validate first step
    if (!exercise.title || !exercise.courseId || exercise.classIds.length === 0) {
      error.value = '请填写所有必填字段'
      return
    }
    
    currentStep.value = 2
    error.value = ''
    return
  }
  
  // Submit the exercise (last step)
  submitExercise()
}

// Go back to the previous step
const prevStep = () => {
  currentStep.value = 1
  error.value = ''
}

// Add a new question or update an existing one
const addOrUpdateQuestion = () => {
  // Validate question data
  if (!tempQuestion.content || tempQuestion.points <= 0) {
    error.value = '请完成题目内容并设置分值'
    return
  }
  
  // Validate options for choice questions
  if (['single_choice', 'multiple_choice'].includes(tempQuestion.type)) {
    const hasEmptyOption = tempQuestion.options.some(opt => !opt.text)
    if (hasEmptyOption) {
      error.value = '请填写所有选项内容'
      return
    }
    
    if (!tempQuestion.answer) {
      error.value = '请设置正确答案'
      return
    }
  }
  
  // Create a copy of the question to add to the exercise
  const questionCopy = JSON.parse(JSON.stringify(tempQuestion))
  
  if (isEditingQuestion.value && selectedQuestion.value) {
    // Update existing question
    const index = exercise.questions.indexOf(selectedQuestion.value)
    if (index !== -1) {
      exercise.questions[index] = questionCopy
    }
  } else {
    // Add new question
    exercise.questions.push(questionCopy)
  }
  
  // Reset form and state
  resetQuestionForm()
  error.value = ''
}

// Edit an existing question
const editQuestion = (question: Question) => {
  selectedQuestion.value = question
  isEditingQuestion.value = true
  
  // Copy question data to temp form
  Object.assign(tempQuestion, JSON.parse(JSON.stringify(question)))
}

// Remove a question
const removeQuestion = (question: Question) => {
  const index = exercise.questions.indexOf(question)
  if (index !== -1) {
    exercise.questions.splice(index, 1)
  }
  
  // If editing this question, reset the form
  if (selectedQuestion.value === question) {
    resetQuestionForm()
  }
}

// Reset the question form
const resetQuestionForm = () => {
  tempQuestion.type = 'single_choice'
  tempQuestion.content = ''
  tempQuestion.options = [
    { key: 'A', text: '' },
    { key: 'B', text: '' },
    { key: 'C', text: '' },
    { key: 'D', text: '' }
  ]
  tempQuestion.answer = ''
  tempQuestion.points = 5
  
  selectedQuestion.value = null
  isEditingQuestion.value = false
}

// Add an option for choice questions
const addOption = () => {
  const nextKey = String.fromCharCode(65 + tempQuestion.options.length)
  tempQuestion.options.push({ key: nextKey, text: '' })
}

// Remove an option
const removeOption = (index: number) => {
  if (tempQuestion.options.length > 2) {
    tempQuestion.options.splice(index, 1)
    
    // Re-key the options
    tempQuestion.options.forEach((opt, i) => {
      opt.key = String.fromCharCode(65 + i)
    })
  }
}

// Submit the exercise to the server
const submitExercise = async () => {
  // Validate exercise data
  if (exercise.questions.length === 0) {
    error.value = '请至少添加一道题目'
    return
  }
  
  loading.value = true
  error.value = ''
  
  try {
    await ExerciseApi.createExercise(exercise)
    router.push('/') // or to a success page
  } catch (err) {
    error.value = '创建练习失败，请稍后再试'
    console.error(err)
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="exercise-create-container">
    <h1 class="page-title">{{ currentStep === 1 ? '创建在线练习 - 基本信息' : '创建在线练习 - 添加题目' }}</h1>
    
    <div v-if="error" class="error-message">{{ error }}</div>
    
    <!-- Step 1: Basic Exercise Information -->
    <div v-if="currentStep === 1" class="form-step">
      <div class="form-group">
        <label for="title">练习标题</label>
        <input 
          id="title"
          v-model="exercise.title"
          type="text"
          placeholder="输入练习标题"
          required
        />
      </div>
      
      <div class="form-group">
        <label for="courseId">所属课程</label>
        <select 
          id="courseId"
          v-model="exercise.courseId"
          @change="fetchClasses(exercise.courseId)"
          required
        >
          <option value="" disabled>选择课程</option>
          <option v-for="course in courses" :key="course.id" :value="course.id">
            {{ course.name }}
          </option>
        </select>
      </div>
      
      <div class="form-group">
        <label>发布班级</label>
        <div class="checkbox-group">
          <div v-for="classItem in classes" :key="classItem.id" class="checkbox-item">
            <input 
              :id="`class-${classItem.id}`"
              type="checkbox"
              v-model="exercise.classIds"
              :value="classItem.id"
            />
            <label :for="`class-${classItem.id}`">{{ classItem.name }}</label>
          </div>
        </div>
      </div>
      
      <div class="form-row">
        <div class="form-group form-group-half">
          <label for="startTime">开始时间</label>
          <input 
            id="startTime"
            v-model="exercise.startTime"
            type="datetime-local"
          />
        </div>
        
        <div class="form-group form-group-half">
          <label for="endTime">截止时间</label>
          <input 
            id="endTime"
            v-model="exercise.endTime"
            type="datetime-local"
          />
        </div>
      </div>
      
      <div class="form-row">
        <div class="form-group form-group-half">
          <label for="timeLimit">答题时间限制（分钟，0表示不限时）</label>
          <input 
            id="timeLimit"
            v-model.number="exercise.timeLimit"
            type="number"
            min="0"
          />
        </div>
        
        <div class="form-group form-group-half">
          <label for="allowedAttempts">允许提交次数（0表示不限）</label>
          <input 
            id="allowedAttempts"
            v-model.number="exercise.allowedAttempts"
            type="number"
            min="0"
          />
        </div>
      </div>
    </div>
    
    <!-- Step 2: Question Management -->
    <div v-else-if="currentStep === 2" class="form-step">
      <!-- Question List -->
      <div class="question-list-section">
        <h2>题目列表 ({{ exercise.questions.length }})</h2>
        
        <div v-if="exercise.questions.length === 0" class="empty-state">
          尚未添加题目，请使用下方表单添加题目
        </div>
        
        <div v-else class="question-list">
          <div 
            v-for="(question, index) in exercise.questions" 
            :key="index"
            class="question-list-item"
          >
            <div class="question-list-header">
              <span class="question-number">{{ index + 1 }}</span>
              <span class="question-type-badge" :class="question.type">
                {{ 
                  question.type === 'single_choice' ? '单选题' : 
                  question.type === 'multiple_choice' ? '多选题' : 
                  question.type === 'true_false' ? '判断题' : 
                  question.type === 'short_answer' ? '简答题' : '填空题' 
                }}
              </span>
              <span class="question-points">{{ question.points }}分</span>
            </div>
            
            <div class="question-list-content">
              <div v-html="question.content"></div>
            </div>
            
            <div class="question-list-actions">
              <button 
                class="btn-secondary btn-sm"
                @click="editQuestion(question)"
              >
                编辑
              </button>
              <button 
                class="btn-danger btn-sm"
                @click="removeQuestion(question)"
              >
                删除
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Question Form -->
      <div class="question-form-section">
        <h2>{{ isEditingQuestion ? '编辑题目' : '添加题目' }}</h2>
        
        <div class="form-group">
          <label for="questionType">题目类型</label>
          <select id="questionType" v-model="tempQuestion.type">
            <option value="single_choice">单选题</option>
            <option value="multiple_choice">多选题</option>
            <option value="true_false">判断题</option>
            <option value="short_answer">简答题</option>
            <option value="fill_blank">填空题</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="questionContent">题目内容</label>
          <textarea 
            id="questionContent"
            v-model="tempQuestion.content"
            rows="4"
            placeholder="输入题目内容"
          ></textarea>
        </div>
        
        <!-- Options for choice questions -->
        <div v-if="['single_choice', 'multiple_choice'].includes(tempQuestion.type)" class="form-group">
          <label>选项</label>
          <div 
            v-for="(option, index) in tempQuestion.options" 
            :key="index"
            class="option-row"
          >
            <div class="option-key">{{ option.key }}</div>
            <input 
              v-model="option.text"
              type="text"
              :placeholder="`选项${option.key}内容`"
              class="option-input"
            />
            <button 
              type="button"
              class="btn-icon"
              @click="removeOption(index)"
              :disabled="tempQuestion.options.length <= 2"
            >
              ✕
            </button>
          </div>
          
          <button 
            type="button"
            class="btn-text"
            @click="addOption"
          >
            + 添加选项
          </button>
        </div>
        
        <!-- Answer for choice questions -->
        <div v-if="tempQuestion.type === 'single_choice'" class="form-group">
          <label for="singleAnswer">正确答案</label>
          <select id="singleAnswer" v-model="tempQuestion.answer">
            <option value="" disabled>选择正确答案</option>
            <option 
              v-for="option in tempQuestion.options" 
              :key="option.key"
              :value="option.key"
            >
              {{ option.key }}
            </option>
          </select>
        </div>
        
        <div v-if="tempQuestion.type === 'multiple_choice'" class="form-group">
          <label>正确答案 (多选)</label>
          <div class="checkbox-group">
            <div 
              v-for="option in tempQuestion.options" 
              :key="option.key"
              class="checkbox-item"
            >
              <input 
                :id="`answer-${option.key}`"
                type="checkbox"
                v-model="tempQuestion.answer"
                :value="option.key"
              />
              <label :for="`answer-${option.key}`">{{ option.key }}</label>
            </div>
          </div>
        </div>
        
        <div v-if="tempQuestion.type === 'true_false'" class="form-group">
          <label for="truefalseAnswer">正确答案</label>
          <select id="truefalseAnswer" v-model="tempQuestion.answer">
            <option value="" disabled>选择正确答案</option>
            <option value="True">正确</option>
            <option value="False">错误</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="questionPoints">分值</label>
          <input 
            id="questionPoints"
            v-model.number="tempQuestion.points"
            type="number"
            min="1"
          />
        </div>
        
        <div class="form-actions">
          <button 
            type="button"
            class="btn-secondary"
            @click="resetQuestionForm"
          >
            取消
          </button>
          <button 
            type="button"
            class="btn-primary"
            @click="addOrUpdateQuestion"
          >
            {{ isEditingQuestion ? '更新题目' : '添加题目' }}
          </button>
        </div>
      </div>
    </div>
    
    <!-- Step Navigation Buttons -->
    <div class="step-navigation">
      <button 
        v-if="currentStep > 1"
        type="button"
        class="btn-secondary"
        @click="prevStep"
        :disabled="loading"
      >
        返回上一步
      </button>
      
      <button 
        type="button"
        class="btn-primary"
        @click="nextStep"
        :disabled="loading"
      >
        {{ currentStep < 2 ? '下一步' : '创建练习' }}
        <span v-if="loading">...</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.exercise-create-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 1.5rem;
}

.page-title {
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e0e0e0;
}

.form-step {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.25rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #2c6ecf;
  outline: none;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-group-half {
  flex: 1;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.checkbox-item {
  display: flex;
  align-items: center;
  margin-right: 1rem;
}

.checkbox-item input {
  margin-right: 0.5rem;
}

.step-navigation {
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
}

.btn-primary, .btn-secondary, .btn-danger {
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s;
}

.btn-sm {
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
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
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.btn-danger {
  background-color: #f44336;
  color: white;
}

.btn-danger:hover {
  background-color: #d32f2f;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  color: #757575;
  font-size: 1rem;
  padding: 0.5rem;
}

.btn-icon:hover {
  color: #d32f2f;
}

.btn-icon:disabled {
  color: #bdbdbd;
  cursor: not-allowed;
}

.btn-text {
  background: none;
  border: none;
  color: #2c6ecf;
  padding: 0;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  margin-top: 0.5rem;
}

.btn-text:hover {
  text-decoration: underline;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 0.75rem;
  border-radius: 4px;
  margin-bottom: 1.25rem;
}

.question-list-section, .question-form-section {
  margin-bottom: 2rem;
}

.question-list {
  margin-top: 1rem;
}

.question-list-item {
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin-bottom: 1rem;
  background-color: #f9f9f9;
}

.question-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e0e0e0;
}

.question-number {
  font-weight: bold;
}

.question-type-badge {
  font-size: 0.875rem;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  background-color: #e3f2fd;
  color: #1565c0;
}

.question-type-badge.single_choice {
  background-color: #e3f2fd;
  color: #1565c0;
}

.question-type-badge.multiple_choice {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.question-type-badge.true_false {
  background-color: #fff3e0;
  color: #e65100;
}

.question-type-badge.short_answer {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.question-type-badge.fill_blank {
  background-color: #e8eaf6;
  color: #3949ab;
}

.question-points {
  font-weight: 500;
}

.question-list-content {
  margin-bottom: 1rem;
}

.question-list-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.option-row {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.option-key {
  flex: 0 0 2rem;
  text-align: center;
  font-weight: 500;
  background-color: #f5f5f5;
  border-radius: 4px;
  padding: 0.5rem;
  margin-right: 0.5rem;
}

.option-input {
  flex: 1;
}

.empty-state {
  padding: 2rem;
  text-align: center;
  color: #757575;
  background-color: #f5f5f5;
  border-radius: 4px;
}
</style>