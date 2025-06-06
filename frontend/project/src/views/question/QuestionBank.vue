<script setup lang="ts">
import {ref, onMounted, watch} from 'vue'
import QuestionApi from '../../api/question.ts'
import {useAuthStore} from "../../stores/auth.ts";
import CourseApi from "../../api/course.ts";

const authStore = useAuthStore()
const isTeacher = authStore.userRole === 'teacher'

// const isTeacher = authStore.userRole === 'teacher'

const questions = ref<any[]>([])
const loading = ref(true)
const error = ref('')

// Filters
const selectedChapter = ref('')
const selectedCourse = ref()

// Chapters and types (will be populated from questions)
const chapters = ref([])
const courses = ref([])

// File upload
const showUploadForm = ref(false)
const uploadForm = ref({
    courseId: 0,
    sectionId: 0,
    uploaderId: 0,
    title: '',
    type: '',
    file: null as File | null,
    visibility: '',
})
const uploadProgress = ref(0)
const uploadError = ref('')

// Temporary question data for editing
const tempQuestion = ref({
    type: 'single_choice',
    content: '',
    options: [
        { key: 'A', text: '' },
        { key: 'B', text: '' },
        { key: 'C', text: '' },
        { key: 'D', text: '' }
    ],
    answer: '', // 始终保持字符串类型
    courseId: 0,
    sectionId: 0,
    analysis: '',
    creatorId: authStore.user?.id,
    // 使用计算属性处理多选答案
    get answerArray(): string[] {
        return this.type === 'multiple_choice' && this.answer
            ? this.answer.split(',')
            : [];
    },
    set answerArray(values: string[]) {
        this.answer = values.join(',');
    },

})

// Add an option for choice questions
const addOption = () => {
    const nextKey = String.fromCharCode(65 + tempQuestion.value.options.length)
    tempQuestion.value.options.push({ key: nextKey, text: '' })
}

// Remove an option
const removeOption = (index: number) => {
    if (tempQuestion.value.options.length > 2) {
        tempQuestion.value.options.splice(index, 1)

        // Re-key the options
        tempQuestion.value.options.forEach((opt, i) => {
            opt.key = String.fromCharCode(65 + i)
        })
    }
}

// Fetch questions
const fetchQuestions = async () => {
    loading.value = true
    error.value = ''

    try {
        // 使用课程ID而不是名称
        const response = await QuestionApi.getQuestionList({
            courseId: selectedCourse.value
        })

        questions.value = response.data.data.questions
    } catch (err) {
        error.value = '获取资源列表失败，请稍后再试'
        console.error(err)
    } finally {
        loading.value = false
    }
}

// ---
// 在接口定义部分添加类型
interface Course {
    id: number
    name: string
    sections: Section[]
}

interface Section {
    id: number
    course_id: number
    title: string
    sort_order: number
}

// 添加课程和章节数据
const sections = ref<Section[]>([])

// 获取教师课程列表
const fetchCourses = async () => {
    try {
        const response = await CourseApi.getUserCourses(authStore.user?.id)
        // 直接使用 API 返回的课程对象数组
        courses.value = response.data.data

        // 删除下面两行，不再需要提取课程名称
        // const courseSet = new Set(courses.value.map((r: any) => r.name).filter(Boolean))
        // courses.value = Array.from(courseSet)
    } catch (err) {
        console.error('获取课程列表失败:', err)
    }
}

// 监听课程选择变化
watch(() => tempQuestion.value.courseId, (newCourseId) => {
    const selectedCourse = courses.value.find(c => c.id === newCourseId)
    sections.value = selectedCourse?.sections || []
    console.log(sections.value)
    tempQuestion.value.sectionId = 0 // 重置章节选择
})

// watch(() => tempQuestion.value.courseId, (newCourseId) => {
//     const selectedCourse = courses2.value.find(c => c.id === newCourseId)
//
//     // 新增数据校验
//     const safeSections = Array.isArray(selectedCourse?.sections)
//         ? [...selectedCourse.sections]
//         : []
//     console.log(selectedCourse?.sections)
//     console.log(Array.isArray(selectedCourse?.sections))
//     console.log(safeSections)
//     sections.value = safeSections.filter(s => s?.id && s?.title) // 过滤无效数据
//     console.log("处理后的章节数据:", sections.value)
//
//     tempQuestion.value.sectionId = 0
// })

// 在onMounted中添加
onMounted(() => {
    fetchCourses()
    fetchQuestions()
})

const addQuestion = async () => {
    // Validate question data
    if (!tempQuestion.value.content) {
        error.value = '请完成题目内容并设置分值'
        return
    }

    // Validate options for choice questions
    if (['single_choice', 'multiple_choice'].includes(tempQuestion.value.type)) {
        const hasEmptyOption = tempQuestion.value.options.some(opt => !opt.text)
        if (hasEmptyOption) {
            error.value = '请填写所有选项内容'
            return
        }

        if (!tempQuestion.value.answer) {
            error.value = '请设置正确答案'
            return
        }
    }

    // 处理多选题答案格式
    if (tempQuestion.value.type === 'multiple_choice') {
        if (!tempQuestion.value.answerArray.length) {
            error.value = '请设置正确答案';
            return;
        }
    }

    // 添加验证
    if (!tempQuestion.value.courseId) {
        error.value = '请选择所属课程'
        return
    }

    try {
        console.log(tempQuestion.value)
        const res = await QuestionApi.createQuestion({
            ...tempQuestion.value,
        })
        console.log(res)

        // Reset form and refresh list
        resetUploadForm()
        showUploadForm.value = false
        fetchQuestions()

    } catch (err) {
        uploadError.value = '上传问题失败，请稍后再试'
        console.error(err)
    }
}

// Reset upload form
const resetUploadForm = () => {
    tempQuestion.value = {
        type: 'single_choice',
        range: '',
        content: '',
        teacherId: authStore.user?.id,
        options: [
            { key: 'A', text: '' },
            { key: 'B', text: '' },
            { key: 'C', text: '' },
            { key: 'D', text: '' }
        ],
        analysis: '',
        answer: ''
    }
    uploadProgress.value = 0
    uploadError.value = ''
}

// 新增弹窗相关状态
const showDetailDialog = ref(false)
const selectedQuestion = ref<any>(null)

// 修改预览方法
const showQuestionDetail = (question: any) => {
    selectedQuestion.value = question
    showDetailDialog.value = true
}

// 新增答案格式化方法
const formatAnswer = (question: any) => {
    if (question.type === 'multiple_choice') {
        return question.answer.split(',').join(', ')
    }
    return question.answer
}

// Filter questions when criteria change
watch([selectedChapter, selectedCourse], () => {
    fetchQuestions()
})

onMounted(() => {
    fetchQuestions()
})


</script>

<template>
    <div class="question-bank-container">
        <div class="resource-header">
            <h2>题库中心</h2>
            <button
                v-if="isTeacher"
                class="btn-primary"
                @click="showUploadForm = !showUploadForm"
            >
                {{ showUploadForm ? '取消新建' : '新建题目' }}
            </button>
        </div>

        <!-- Upload Form -->
        <div v-if="showUploadForm" class="upload-form">
            <h3>新建题目</h3>

            <div v-if="uploadError" class="error-message">{{ uploadError }}</div>

            <!-- Question Form -->
            <div class="question-form-byhand-section">

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

                <div class="form-row">
                    <div class="form-group form-group-half">
                        <label for="courseSelect">所属课程</label>
                        <select
                            id="courseSelect"
                            v-model="tempQuestion.courseId"
                            required
                        >
                            <option value="" disabled>请选择课程</option>
                            <option
                                v-for="course in courses"
                                :key="course.id"
                                :value="course.id"
                            >
                                {{ course.name }}
                            </option>
                        </select>
                    </div>

                    <div class="form-group form-group-half">
                        <label for="sectionSelect">所属章节</label>
                        <select
                            id="sectionSelect"
                            v-model="tempQuestion.sectionId"
                            :disabled="!tempQuestion.courseId"
                        >
                            <option value=0 disabled>请选择章节</option>
                            <template v-if="sections.length">
                                <option
                                    v-for="section in sections"
                                    :key="section.id"
                                    :value="section.id"
                                >
                                    {{ section.title }}
                                </option>
                            </template>
                            <option v-else disabled>加载中...</option>
                        </select>
                    </div>
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
                                v-model="tempQuestion.answerArray"
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
                    <label for="analysis">答案解析</label>
                    <textarea
                        id="analysis"
                        v-model="tempQuestion.analysis"
                        rows="4"
                        placeholder="输入答案解析"
                    ></textarea>
                </div>
            </div>

            <div class="form-actions">
                <button
                    type="button"
                    class="btn-secondary"
                    @click="resetUploadForm"
                >
                    重置
                </button>
                <button
                    type="button"
                    class="btn-primary"
                    @click="addQuestion"
                >
                    上传
                </button>
            </div>
        </div>

        <!-- Filter Section -->
        <div class="resource-filters">
            <div class="filter-section">
                <label for="typeFilter">按课程筛选:</label>
                <select
                    id="typeFilter"
                    v-model="selectedCourse"
                >
                    <option value="">所有课程</option>
                    <!-- 显示课程名称，绑定值为课程ID -->
                    <option v-for="course in courses" :key="course.id" :value="course.id">
                        {{ course.name }}
                    </option>
                </select>
            </div>

<!--            <div class="filter-section">-->
<!--                <label for="chapterFilter">按章节筛选:</label>-->
<!--                <select-->
<!--                    id="chapterFilter"-->
<!--                    v-model="selectedChapter"-->
<!--                >-->
<!--                    <option value="">所有章节</option>-->
<!--                    <option v-for="sectionId in chapters" :key="sectionId" :value="sectionId">-->
<!--                        {{ sectionId }}-->
<!--                    </option>-->
<!--                </select>-->
<!--            </div>-->
        </div>

        <!-- Resource List -->
        <div v-if="loading" class="loading-container">加载中...</div>
        <div v-else-if="error" class="error-message">{{ error }}</div>
        <div v-else-if="questions.length === 0" class="empty-state">
            暂无教学资料
        </div>
        <div v-else-if="!selectedCourse" class="error-message">请选择课程再查看题库</div>
        <div v-else class="resource-table-wrapper">
            <table class="resource-table">
                <thead>
                <tr>
                    <th>题目内容</th>
                    <th>所属课程</th>
                    <th>所属章节</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="question in questions" :key="question.id">
                    <td>{{ question.name }}</td>
                    <td>{{ question.courseName }}</td>
                    <td>{{ question.sectionName || '-' }}</td>
                    <!--         aTODO: 时间-->
                    <td class="actions">
                        <button
                            class="btn-action preview"
                            @click="showQuestionDetail(question)"
                            title="查看"
                        >
                            查看
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>
            <!-- 在resource-table下方添加弹窗 -->
            <div v-if="showDetailDialog" class="modal-mask">
                <div class="modal-container">
                    <div class="modal-header">
                        <h3>题目详情</h3>
                        <button class="modal-close" @click="showDetailDialog = false">&times;</button>
                    </div>

                    <div class="modal-body" v-if="selectedQuestion">
                        <div class="detail-row">
                            <label>课程名称:</label>
                            <span>{{ selectedQuestion.courseName || '-' }}</span>
                        </div>
                        <div class="detail-row">
                            <label>章节标题:</label>
                            <span>{{ selectedQuestion.sectionName || '-' }}</span>
                        </div>
                        <div class="detail-row">
                            <label>题目类型:</label>
                            <span>{{
                                    selectedQuestion.type === 'single_choice' ? '单选题' :
                                        selectedQuestion.type === 'multiple_choice' ? '多选题' :
                                            selectedQuestion.type === 'true_false' ? '判断题' :
                                                selectedQuestion.type === 'short_answer' ? '简答题' : '填空题'
                                }}</span>
                        </div>
                        <div class="detail-row">
                            <label>题目内容:</label>
                            <div class="content-box">{{ selectedQuestion.name }}</div>
                        </div>

                        <!-- 选项展示 -->
                        <div v-if="['single_choice', 'multiple_choice'].includes(selectedQuestion.type)"
                             class="detail-row">
                            <label>题目选项:</label>
                            <div class="options-list">
                                <div v-for="(opt, index) in selectedQuestion.options"
                                     :key="index"
                                     class="option-item">
                                    <span class="option-key">{{ opt.key }}.</span>
                                    <span class="option-text">{{ opt.text }}</span>
                                    <span v-if="selectedQuestion.answer.includes(opt.key)"
                                          class="correct-badge">✓</span>
                                </div>
                            </div>
                        </div>

                        <div class="detail-row">
                            <label>正确答案:</label>
                            <span class="answer-text">{{ formatAnswer(selectedQuestion) }}</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
/* 新增弹窗样式 */
.modal-mask {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 1000;
}

.modal-container {
    background: white;
    border-radius: 8px;
    padding: 20px;
    width: 600px;
    max-width: 90%;
    max-height: 80vh;
    overflow-y: auto;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
    margin-bottom: 15px;
}

.modal-close {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #666;
}

.detail-row {
    margin: 12px 0;
    display: flex;
    align-items: flex-start;
}

.detail-row label {
    width: 100px;
    font-weight: 500;
    color: #666;
    flex-shrink: 0;
}

.content-box {
    background: #f5f5f5;
    padding: 10px;
    border-radius: 4px;
    white-space: pre-wrap;
}

.options-list {
    width: 100%;
}

.option-item {
    display: flex;
    align-items: center;
    margin: 6px 0;
    padding: 8px;
    background: #f8f8f8;
    border-radius: 4px;
}

.option-key {
    font-weight: 500;
    margin-right: 10px;
    min-width: 20px;
}

.correct-badge {
    color: #2e7d32;
    margin-left: 10px;
    font-weight: bold;
}

.answer-text {
    font-weight: 500;
    color: #2c6ecf;
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

.question-bank-container {
    max-width: 1000px;
    margin: 0 auto;
    padding: 1.5rem;
}
/* 单选框样式 */
.radio-group {
    display: flex;
    gap: 1rem;
    margin-top: 0.5rem;
}

.radio-group label {
    display: flex;
    align-items: center;
    cursor: pointer;
}

.radio-label {
    margin-left: 0.25rem;
}

input[type="radio"] {
    accent-color: #409eff; /* 与Element Plus主色一致 */
}

.resource-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

.resource-header h2 {
    margin: 0;
}

.upload-form {
    background-color: #f9f9f9;
    border-radius: 8px;
    padding: 1.5rem;
    margin-bottom: 1.5rem;
    border: 1px solid #e0e0e0;
}

.upload-form h3 {
    margin-top: 0;
    margin-bottom: 1rem;
}

.form-group {
    margin-bottom: 1rem;
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
}

.form-row {
    display: flex;
    gap: 1rem;
}

.form-group-half {
    flex: 1;
}

select:disabled {
    background-color: #f5f5f5;
    cursor: not-allowed;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 1.5rem;
}

.upload-progress {
    margin-top: 1rem;
}

.progress-bar {
    height: 8px;
    background-color: #e0e0e0;
    border-radius: 4px;
    overflow: hidden;
    margin-bottom: 0.5rem;
}

.progress-value {
    height: 100%;
    background-color: #2c6ecf;
    border-radius: 4px;
}

.progress-text {
    text-align: right;
    font-size: 0.875rem;
    color: #616161;
}

.resource-filters {
    display: flex;
    flex-wrap: wrap;
    margin-bottom: 1.5rem;
    gap: 1rem;
    align-items: center;
}

.filter-section {
    display: flex;
    align-items: center;
}

.filter-section label {
    margin-right: 0.5rem;
    white-space: nowrap;
}

.filter-section select {
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 0.9375rem;
}

.filter-section.search {
    flex-grow: 1;
}

.search-input {
    width: 100%;
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 0.9375rem;
}

.resource-table-wrapper {
    overflow-x: auto;
}

.resource-table {
    width: 100%;
    border-collapse: collapse;
}

.resource-table th,
.resource-table td {
    padding: 1rem;
    text-align: left;
    border-bottom: 1px solid #e0e0e0;
}

.resource-table th {
    background-color: #f5f5f5;
    font-weight: 600;
}

.resource-table tr:hover {
    background-color: #f9f9f9;
}

.actions {
    display: flex;
    gap: 0.5rem;
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

.btn-action.download {
    background-color: #e8f5e9;
    color: #2e7d32;
}

.btn-action.download:hover {
    background-color: #c8e6c9;
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
    .form-row {
        flex-direction: column;
        gap: 0;
    }

    .resource-filters {
        flex-direction: column;
        align-items: stretch;
    }

    .filter-section {
        width: 100%;
    }

    .filter-section select,
    .search-input {
        flex-grow: 1;
    }
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

</style>
