<template>
    <div class="exercise-container" @copy.prevent @paste.prevent>
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">加载中...</div>

        <!-- 错误提示 -->
        <div v-if="error" class="error">{{ error }}</div>

        <!-- 练习主体 -->
        <div v-if="exercise" class="exercise-content">
            <!-- 头部信息 -->
            <div class="exercise-header">
                <h2>{{ exercise.title }}</h2>
                <div class="time-limit">
                    <span class="time-icon">⏳</span>
                    剩余时间：{{ formattedTime }}
                </div>
            </div>

            <!-- 题目列表 -->
            <div class="question-list">
                <div
                    v-for="(question, index) in exercise.questions"
                    :key="question.id"
                    class="question-item"
                >
                    <h3>题目 {{ index + 1 }}：{{ question.name }}</h3>
                    <div class="question-meta">
                        <span class="question-type">{{ questionTypeMap[question.type] }}</span>
                        <span class="question-points">{{ question.points }}分</span>
                    </div>

                    <!-- 单选题 -->
                    <div v-if="question.type === 'single_choice'" class="options">
                        <label
                            v-for="option in question.options"
                            :key="option.key"
                            class="option-item"
                        >
                            <input
                                type="radio"
                                :name="'q'+question.id"
                                :value="option.key"
                                v-model="answers[question.id]"
                                class="radio-input"
                            />
                            <span class="option-text">
                                {{ option.key }}. {{ option.text }}
                            </span>
                        </label>
                    </div>

                    <!-- 多选题 -->
                    <div v-else-if="question.type === 'multiple_choice'" class="options">
                        <label
                            v-for="option in question.options"
                            :key="option.key"
                            class="option-item"
                        >
                            <input
                                type="checkbox"
                                :name="'q'+question.id"
                                :value="option.key"
                                v-model="answers[question.id]"
                                class="checkbox-input"
                            />
                            <span class="option-text">
                                {{ option.key }}. {{ option.text }}
                            </span>
                        </label>
                    </div>

                    <!-- 判断题 -->
                    <div v-else-if="question.type === 'true_false'" class="options">
                        <label class="option-item">
                            <input
                                type="radio"
                                :name="'q'+question.id"
                                value="True"
                                v-model="answers[question.id]"
                                class="radio-input"
                            />
                            <span class="option-text">正确</span>
                        </label>
                        <label class="option-item">
                            <input
                                type="radio"
                                :name="'q'+question.id"
                                value="False"
                                v-model="answers[question.id]"
                                class="radio-input"
                            />
                            <span class="option-text">错误</span>
                        </label>
                    </div>

                    <!-- 简答题 & 填空题 -->
                    <div v-else class="short-answer-wrapper">
                        <textarea
                            v-model="answers[question.id]"
                            class="short-answer"
                            placeholder="请输入你的答案..."
                            rows="4"
                        ></textarea>
                    </div>
                </div>
            </div>

            <!-- 提交按钮 -->
            <button
                class="submit-btn"
                @click="handleSubmit"
                :disabled="isSubmitting"
            >
                {{ isSubmitting ? '提交中...' : '提交答案' }}
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">
import {ref, computed, onUnmounted, onMounted} from 'vue'
import {useRoute, useRouter} from 'vue-router'
import ExerciseApi from "../../api/exercise.js";
const route = useRoute()
import { useAuthStore } from '../../stores/auth'
const authStore = useAuthStore()
const practiceId = computed(() => route.params.id as string)

const router = useRouter()
const exercise = ref(null)
const loading = ref(true)
const error = ref('')
const answers = ref({})
const timeLeft = ref(0)
const isSubmitting = ref(false)
let timer = null

const questionTypeMap = {
    single_choice: '单选题',
    multiple_choice: '多选题',
    true_false: '判断题',
    short_answer: '简答题',
    fill_blank: '填空题'
}

// 获取练习详情
const fetchExercise = async () => {
    try {
        const res = await ExerciseApi.takeExercise(practiceId.value)
        exercise.value = res.data
        console.log("herrrrrrrrrrrrr")
        console.log(exercise.value)
        // 初始化多选题答案结构
        exercise.value.questions.forEach(question => {
            if (question.type === 'multiple_choice') {
                // 显式初始化为数组
                answers.value[question.id] = []
            }
        })
        timeLeft.value = res.data.timeLimit * 60 // 转换为秒
        startTimer()
    } catch (err) {
        error.value = '获取练习详情失败，请稍后重试'
    } finally {
        loading.value = false
    }
}

// 启动计时器
const startTimer = () => {
    timer = setInterval(() => {
        if (timeLeft.value <= 0) {
            handleAutoSubmit()
            clearInterval(timer)
            return
        }
        timeLeft.value--
    }, 1000)
}

// 格式化剩余时间
const formattedTime = computed(() => {
    const minutes = Math.floor(timeLeft.value / 60)
    const seconds = timeLeft.value % 60
    return `${minutes}:${seconds.toString().padStart(2, '0')}`
})

// 自动提交
const handleAutoSubmit = async () => {
    if (isSubmitting.value) return
    await submitAnswers()
}

// 手动提交
const handleSubmit = async () => {
    if (timeLeft.value <= 0) return
    await submitAnswers()
}

// 提交答案
const submitAnswers = async () => {
    isSubmitting.value = true
    console.log(answers)
    try {
        const res = await ExerciseApi.submitExercise({
            practiceId: practiceId.value,
            studentId: authStore.user?.id, // 从authStore获取
            answers: answers.value
        })
        console.log(res)

        router.push({
            name: 'ExerciseFeedback',
            params: { practiceId: practiceId.value, submissionId: res.data.data.submissionId }
        })
    } catch (err) {
        error.value = '提交失败，请检查网络连接'
    } finally {
        isSubmitting.value = false
    }
}
// 组件卸载时清除定时器
onUnmounted(() => {
    if (timer) clearInterval(timer)
})
onMounted(() => {
    fetchExercise()
    // 初始化多选题答案为数组
    exercise.value?.questions.forEach(question => {
        if (question.type === 'multiple_choice') {
            answers.value[question.id] = []
        }
    })
})

</script>

<style scoped>
.exercise-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 1.5rem;
    background: white;
    min-height: 100vh;
    padding: 2rem;
}

.exercise-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
    padding: 1rem;
    border-bottom: 2px solid #2196F3;
}

.time-limit {
    color: #2196F3;
    font-weight: bold;
    font-size: 1.2rem;
}

.time-icon {
    margin-right: 0.5rem;
}

.question-item {
    margin: 2rem 0;
    padding: 1.5rem;
    border: 1px solid #BBDEFB;
    border-radius: 8px;
    background: #F5F5F5;
}

.options {
    display: flex;
    flex-direction: column;
    font-size: 20px;
}

.option-item {
    display: flex;
    align-items: center;
    padding: 0.8rem;
    border-radius: 4px;
    transition: background 0.2s;
}

.option-item:hover {
    background: #E3F2FD;
}

.radio-input {
    margin-right: 1rem;
    accent-color: #2196F3;
}

.code-editor, .short-answer {
    font-size: 18px;
    width: 100%;
    padding: 1rem;
    border: 2px solid #90CAF9;
    border-radius: 6px;
    font-family: monospace;
    resize: vertical;
}

.submit-btn {
    display: block;
    width: 200px;
    margin: 2rem auto;
    padding: 1rem;
    background: #2196F3;
    color: white;
    border: none;
    border-radius: 25px;
    font-size: 1.1rem;
    cursor: pointer;
    transition: opacity 0.2s;
}

.submit-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
}

.submit-btn:hover:not(:disabled) {
    opacity: 0.9;
}

.loading, .error {
    text-align: center;
    padding: 2rem;
    font-size: 1.2rem;
}

.error {
    color: #D32F2F;
}

/* 添加多选题和填空题样式 */
.checkbox-input {
    /* 强制使用原生复选框样式 */
    -webkit-appearance: checkbox;
    -moz-appearance: checkbox;
    appearance: checkbox;
    width: 18px;
    height: 18px;
    margin-right: 1rem;
}

/* 修复 Safari 浏览器显示问题 */
@supports (-webkit-hyphens:none) {
    .checkbox-input {
        -webkit-appearance: none;
        background: white;
        border: 2px solid #2196F3;
        border-radius: 4px;
        position: relative;
    }

    .checkbox-input:checked::after {
        content: "✓";
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate(-50%, -50%);
        color: #2196F3;
        font-weight: bold;
    }
}

.fill-blank-wrapper input {
    border: none;
    border-bottom: 2px solid #2196F3;
    padding: 4px 8px;
    margin: 0 4px;
}

.fill-blank-wrapper input:focus {
    outline: none;
    border-color: #1565C0;
}

.question-meta {
    display: flex;
    gap: 1rem;
    margin: 0.5rem 0;
    color: #666;
}

.question-type {
    background: #E3F2FD;
    padding: 2px 8px;
    border-radius: 4px;
}

.question-points {
    color: #2196F3;
}

/* 添加简答题placeholder样式 */
.short-answer::placeholder {
    font-size: 18px; /* 增大提示文字 */
    color: #757575;  /* 调整颜色 */
    font-family: inherit; /* 继承字体 */
}

/* 修正多选题选项间距 */
.checkbox-input {
    margin-right: 1rem; /* 与单选题对齐 */
    width: 18px;       /* 固定宽度 */
    height: 18px;      /* 固定高度 */
}

/* 调整选项文字对齐 */
.option-item {
    align-items: center;
    gap: 0.8rem;
}


</style>
