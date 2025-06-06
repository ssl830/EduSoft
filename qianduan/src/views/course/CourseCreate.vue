<script setup lang="ts">
import {ref, reactive} from 'vue'
import { useRouter } from 'vue-router'
import CourseApi from '../../api/course'
import {useAuthStore} from "../../stores/auth.ts";

const router = useRouter()
const authStore = useAuthStore()

const course = reactive({
    teacherId: authStore.user?.id,
    name: '',
    code: '',
    outline: '',
    objective: '',
    assessment: ''
})

const loading = ref(false)
const error = ref('')


// Reset the question form
const resetCourse = () => {
    course.name = '';
    course.code = '';
    course.outline = '';
    course.objective = '';
    course.assessment = '';
}

// Submit the exercise to the server
const submitCourse = async () => {
    // Validate exercise data
    if (course.name === '') {
        error.value = '填写课程名称'
        return
    }
    if (!course.teacherId) {
        error.value = '用户未登录或无法获取教师ID'
        return
    }

    loading.value = true
    error.value = ''

    try {
        const response = await CourseApi.createCourse(course);
        if (response.data.code !== 200) {
            // 请求失败时回滚状态
            console.error('操作失败:', response.data.message);
            error.value = response.data.message || '创建课程失败，请稍后再试';
        }else{
            await router.push('/') // or to a success page
            resetCourse()   // TODO
        }
    } catch (err) {
        error.value = '创建课程失败，请稍后再试'
        console.error(err)
    } finally {
        loading.value = false
    }
}
</script>

<template>
    <div class="exercise-create-container">
        <h1 class="page-title">{{ '创建课程' }}</h1>

        <div v-if="error" class="error-message">{{ error }}</div>

        <div class="form-step">
            <div class="form-row">
                <div class="form-group form-group-half">
                    <label for="name">课程名称</label>
                    <input
                        id="name"
                        v-model="course.name"
                        type="text"
                        placeholder="输入课程名称"
                        required
                    />
                </div>
            </div>

            <div class="form-group">
                <label for="outline">课程大纲</label>
                <input
                    id="outline"
                    v-model="course.outline"
                    type="text"
                    placeholder="输入课程大纲"
                    required
                />
            </div>

            <div class="form-group">
                <label for="outline">课程代码</label>
                <input
                    id="outline"
                    v-model="course.code"
                    type="text"
                    placeholder="输入课程代码"
                    required
                />
            </div>

            <div class="form-group">
                <label for="objective">教学目标</label>
                <input
                    id="objective"
                    v-model="course.objective"
                    type="text"
                    placeholder="输入教学目标"
                    required
                />
            </div>

            <div class="form-group">
                <label for="assessment">考核方式</label>
                <input
                    id="assessment"
                    v-model="course.assessment"
                    type="text"
                    placeholder="输入考核方式"
                    required
                />
            </div>
        </div>

        <!-- Step Navigation Buttons -->
        <div class="step-navigation">
            <button
                type="button"
                class="btn-primary"
                @click="submitCourse"
                :disabled="loading"
            >
                {{ '创建课程' }}
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

.question-list-section, .question-form-byhand-section, question-form-fromrepo-section {
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
