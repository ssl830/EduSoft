<template>
    <div class="feedback-container">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else>
            <div class="header">
                <h1>{{ practiceData.title }} - 练习反馈</h1>
                <div class="total-score">总分：{{ totalScore }}</div>
            </div>

            <div class="questions-list">
                <div v-for="(question, index) in practiceData.questions" :key="question.id" class="question-item">
                    <div class="question-header">
                        <h3>题目 {{ index + 1 }} {{ question.name }}</h3>
                        <div style="display: flex; gap: 8px;">
                            <button
                                @click="toggleFavorite(question)"
                                class="favorite-btn"
                                :class="{ 'favorited': question.isFavorited }"
                            >
                                ★
                            </button>
                            <!-- 新增：添加到错题集按钮 -->
                            <button
                                v-if="question.answer != studentAnswers[question.id]"
                                class="btn-action preview"
                                :class="{ 'added': question.isadded }"
                                style="font-size: 1rem; padding: 0 10px;"
                                @click="addToWrongSet(question)"
                            >
                                添加
                            </button>
                        </div>
                    </div>

                    <div class="question-content">
                        <div class="question-type">{{ questionTypeMap[question.type] }}</div>
                        <div class="question-points">分值：{{ question.points }}分</div>

                        <!-- 客观题展示 -->
                        <template v-if="isObjective(question.type)">
                            <div class="answer-section">
                                <div>标准答案：{{ formatAnswer(question.answer, question.type) }}</div>
                                <div>你的答案：{{ formatAnswer(studentAnswers[question.id], question.type) }}</div>
                            </div>
                        </template>

                        <!-- 主观题展示 -->
                        <template v-else>
                            <div class="answer-section">
                                <div>你的答案：{{ studentAnswers[question.id] || '未回答' }}</div>
                            </div>
                        </template>

                        <div v-if="question.explanation" class="explanation">
                            解析：{{ question.explanation }}
                        </div>
                    </div>
                </div>
            </div>
            <button
                style="margin-top: 20px; margin-bottom: 20px;"
                class="btn-primary"
                @click="() => router.push('/')"
            >
                返回
            </button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {useRoute, useRouter} from 'vue-router'
// import axios from 'axios'
import { useAuthStore } from '../../stores/auth'
import ExerciseApi from "../../api/exercise.ts";
import QuestionApi from '../../api/question'
const router = useRouter()

export interface PracticeDetail {
    title: string;
    code: number;
    message: string;
    courseld: number;
    startTime: string;
    endTime: string;
    timelimit: number;
    allowedAttempts: boolean;
    questions: Question[];
}

export interface Question {
    id: number;
    name: string;
    course_id: number;
    teacher_id: string;
    type: 'single_choice' | 'multiple_choice' | 'true_false' | 'short_answer' | 'fill_blank';
    options: Option[];
    answer: string;
    points: number;
    explanation?: string;
    isFavorited?: boolean;
    studentAnswer?: string | string[];
    isadded: boolean;

}

interface Option {
    key: string;
    text: string;
}

const authStore = useAuthStore()
const route = useRoute()
const practiceId = route.params.practiceId as string
const submissionId = route.params.submissionId as string

const practiceData = ref<PracticeDetail | null>(null)
const studentAnswers = ref<Record<number, string | string[]>>({})
const loading = ref(true)
const error = ref('')

// 题目类型映射
const questionTypeMap = {
    single_choice: '单选题',
    multiple_choice: '多选题',
    true_false: '判断题',
    short_answer: '简答题',
    fill_blank: '填空题'
}

// 计算总分
const totalScore = computed(() => {
    if (!practiceData.value) return 0
    return practiceData.value.questions.reduce((sum, q) => sum + q.points, 0)
})

// 判断是否客观题
const isObjective = (type: string) => {
    return ['single_choice', 'multiple_choice', 'true_false'].includes(type)
}

// 格式化答案显示
const formatAnswer = (answer: string | string[], type: string) => {
    if (Array.isArray(answer)) return answer.join(', ')
    if (type === 'true_false') return answer === 'True' ? '正确' : '错误'
    return answer
}

// 切换收藏状态
const toggleFavorite = async (question: Question) => {
    const originalState = question.isFavorited;
    try {
        // 先切换本地状态提供即时反馈
        question.isFavorited = !originalState;

        // 根据新状态调用对应接口
        const apiMethod = question.isFavorited
            ? ExerciseApi.favouriteQuestions
            : ExerciseApi.enFavouriteQuestions;

        const response = await apiMethod(question.id);

        if (response.data.code !== 200) {
            // 请求失败时回滚状态
            question.isFavorited = originalState;
            console.error('操作失败:', response.data.message);
        }
    } catch (err) {
        // 请求异常时回滚状态
        question.isFavorited = originalState;
        console.error('收藏操作失败:', err);
    }
}

// 添加到错题集
const addToWrongSet = async (question: Question) => {
    try {
        const res = await QuestionApi.addWrongQuestion(
            question.id,
            {
                wrongAnswer: studentAnswers.value[question.id] || ''
            }
        )
        if (res.data.code === 200) {
            console.log("YESSSSS")
            question.isadded = true
        } else {
            console.log("NOOOOOO")
        }
    } catch (e) {
        console.error('添加到错题集失败:', e)
    }
}

// 获取练习详情
const fetchPracticeDetail = async () => {
    try {
        const response = await ExerciseApi.getExerciseDetails(practiceId, {submissionId})

        practiceData.value = response.data
        // 初始化学生答案（假设从API获取）
        response.data.questions.forEach((q: Question) => {
            studentAnswers.value[q.id] = q.studentAnswer || ''
        })

    } catch (err) {
        error.value = '获取练习详情失败'
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchPracticeDetail()
})
</script>

<style scoped>
.feedback-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.total-score {
    font-size: 1.5rem;
    color: #2196F3;
    font-weight: bold;
}

.questions-list {
    display: grid;
    gap: 2rem;
}

.question-item {
    padding: 1.5rem;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    background: white;
}

.question-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.favorite-btn {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 1.5rem;
    color: #ccc;
    transition: color 0.2s;
}

.favorite-btn.favorited {
    color: #FFC107;
}

.question-content {
    display: grid;
    gap: 1rem;
}

.question-type {
    color: #666;
    font-size: 0.9rem;
}

.question-points {
    color: #4CAF50;
    font-weight: bold;
}

.answer-section {
    padding: 1rem;
    background: #f5f5f5;
    border-radius: 4px;
}

.explanation {
    padding: 1rem;
    background: #E3F2FD;
    border-radius: 4px;
    margin-top: 1rem;
}

.loading, .error {
    text-align: center;
    padding: 2rem;
    font-size: 1.2rem;
}

.error {
    color: #D32F2F;
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

.btn-action {
    padding: 0.375rem 0.75rem;
    border-radius: 4px;
    border: none;
    font-size: 0.875rem;
    cursor: pointer;
    transition: background-color 0.2s;
}

.btn-action.preview {
    background-color: #e3f2fd;
    color: #1976d2;
}

.btn-action.preview:hover {
    background-color: #bbdefb;
}
.btn-action.preview.added {
    background-color: #f5f5f5;
    color: #424242;
}
</style>
