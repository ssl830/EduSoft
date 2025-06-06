<script setup lang="ts">
import {ref, onMounted, watch, computed} from 'vue'
import { defineProps } from 'vue'
import ExerciseApi from '../../api/exercise'
import {useAuthStore} from "../../stores/auth.ts";
import router from "../../router";

const props = defineProps<{
    classId: string;
    isTeacher: boolean;
}>()

const practices = ref<any[]>([])
const pendingCorrections = ref<any[]>([]) // 新增：待批改列表
const loading = ref(true)
const error = ref('')
const currentView = ref('exercises') // 新增：视图切换（练习/批改）

// Filters
const selectedChapter = ref('')
const selectedType = ref('')
const selectedExer = ref(-1)

// Chapters and types (will be populated from resources)
const names = ref([])
const practicesTypes = ref([
    { value: 'unFinished', label: '未完成' },
    { value: 'unScored', label: '待批改' },
    { value: 'scored', label: '已批改' },
    { value: 'overdue', label: '已过期' },
])
const practicesName = ref([])

const authStore = useAuthStore()
const isTeacher = computed(() => authStore.userRole === 'teacher')
const isStudent = computed(() => authStore.userRole === 'student')

// 新增：判断是否超过截止时间
const isOverdue = (endTime: string) => {
    if (!endTime) return false
    const now = new Date()
    const end = new Date(endTime)
    return now > end
}

// 获取班级练习列表 (学生视图)
const fetchPractices = async () => {
    loading.value = true
    error.value = ''

    try {
        if(isStudent.value){ // 学生视图
            // 调用新的API端点
            const response = await ExerciseApi.getPracticeList(props.classId)
            // 更新数据结构处理
            practices.value = response.data.practices
        }else{ // 老师视图
            // 调用新的API端点
            const response = await ExerciseApi.getPracticeTeachList(props.classId)
            // 更新数据结构处理
            practices.value = response.data.practices
        }

        // // 提取唯一练习名称
        // const practiceSet = new Map()
        // practices.value.forEach((ex: any) => {
        //     if (ex.id && ex.name) {
        //         practiceSet.set(ex.id, ex.name)
        //     }
        // })
        // practicesName.value = Array.from(practiceSet, ([id, name]) => ({id, name}))
        //
        // // 提取学生姓名
        // const namesSet = new Set(practices.value.map((r: any) => r.name).filter(Boolean))
        // names.value = Array.from(namesSet)

    } catch (err) {
        error.value = '获取练习列表失败，请稍后再试'
        console.error(err)
    } finally {
        loading.value = false
    }
}

// 新增：获取待批改列表 (老师视图)
const fetchPendingCorrections = async () => {
    loading.value = true
    error.value = ''

    try {
        // 调用新的API端点
        const response = await ExerciseApi.getPendingJudgeList({
            classId: props.classId,
            practiced: selectedExer.value === -1 ? undefined : selectedExer.value
        })
        // 更新数据结构
        pendingCorrections.value = response.data.data
        console.log(pendingCorrections.value)

    } catch (err) {
        error.value = '获取待批改列表失败，请稍后再试'
        console.error(err)
    } finally {
        loading.value = false
    }
}

// 根据当前视图获取数据
const fetchData = async () => {
    if (isStudent.value || currentView.value === 'exercises') {
        await fetchPractices()
    } else {
        await fetchPendingCorrections()
    }
}

// 导出报告
const downloadPracticeReport = async (practiceId: number) => {
    // TODO: 实现导出逻辑
    console.log(`导出报告: ${practiceId}`)
}

// 做练习
const doPractice = (practiceId: number) => {
    router.push({
        name: 'TakeExercise',
        params: { id: practiceId }
    })
}

// 批改练习
const checkPractice = (submissionId: number) => {
    router.push({
        name: 'CheckExercise',
        params: { submissionId }
    })
}

// 查看练习
const getPracticeReport = async (practiceId: number, submissionId: number) => {
    // TODO: 或者跳转到“学习记录”
    router.push({
        name: 'ExerciseFeedback',
        params: { practiceId, submissionId }
    })
}

// 导出报告
const exportReport = async() => {
    // TODO: 实现导出逻辑
}

// 切换视图
const switchView = (view: string) => {
    currentView.value = view
    fetchData()
}

// 新增：跳转到新建练习页面
const goToCreateExercise = () => {
    router.push({ name: 'ExerciseCreate'})
}

// 过滤数据
watch([selectedChapter, selectedType, selectedExer], () => {
    fetchData()
})

onMounted(() => {
    fetchData()
})
</script>

<template>
    <div class="resource-list-container">
        <div class="resource-header">
            <h2>{{ isStudent ? '我的练习' : '练习管理' }}</h2>
            <div>
                <!-- 新建练习按钮，仅老师可见 -->
                <button
                    v-if="!isStudent"
                    class="btn-primary"
                    style="margin-right: 1rem"
                    @click="goToCreateExercise"
                >
                    新建练习
                </button>
                <button
                    v-if="!isStudent"
                    class="btn-secondary"
                    @click="switchView('exercises')"
                    :class="{ active: currentView === 'exercises' }"
                >
                    练习列表
                </button>
                <button
                    v-if="!isStudent"
                    class="btn-secondary"
                    style="margin-left: 1rem"
                    @click="switchView('corrections')"
                    :class="{ active: currentView === 'corrections' }"
                >
                    待批改
                </button>
                <button
                    v-if="isStudent"
                    class="btn-primary"
                    @click="exportReport"
                >
                    {{ '导出报告' }}
                </button>
            </div>
        </div>

        <!-- 筛选区域 -->
        <div class="resource-filters" v-if="currentView === 'exercises'">
            <div class="filter-section">
                <label for="typeFilter">按练习筛选:</label>
                <select
                    id="typeFilter"
                    v-model="selectedExer"
                >
                    <option value=-1>所有练习</option>
                    <option
                        v-for="practice in practicesName"
                        :key="practice.id"
                        :value="practice.id"
                    >
                        {{ practice.name }}
                    </option>
                </select>
            </div>

        </div>

        <!-- 待批改列表视图 -->
        <div v-if="!isStudent && currentView === 'corrections'">
            <div v-if="loading" class="loading-container">加载中...</div>
            <div v-else-if="error" class="error-message">{{ error }}</div>
            <div v-else-if="pendingCorrections.length === 0" class="empty-state">
                暂无待批改练习
            </div>
            <div v-else class="resource-table-wrapper">
                <table class="resource-table">
                    <thead>
                    <tr>
                        <th>练习名称</th>
                        <th>学生姓名</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="item in pendingCorrections" :key="item.submissionId">
                        <td>{{ item.practiceName }}</td>
                        <td>{{ item.studentName }}</td>
                        <td class="actions">
                            <button
                                class="btn-action history"
                                @click="checkPractice(item.submissionId)"
                                title="批改"
                            >
                                批改
                            </button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 练习列表视图 -->
        <div v-else>
            <div v-if="loading" class="loading-container">加载中...</div>
            <div v-else-if="error" class="error-message">{{ error }}</div>
            <div v-else-if="practices.length === 0" class="empty-state">
                暂无练习
            </div>
            <div v-else class="resource-table-wrapper">
                <table class="resource-table">
                    <thead>
                    <tr>
                        <th>练习名称</th>
                        <th>开始时间</th>
                        <th>截止时间</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="practice in practices" :key="practice.practiceid">
                        <td>{{ practice.name }}</td>
                        <td>{{ practice.start_time || '-' }}</td>
                        <td>{{ practice.end_time || '-' }}</td>
                        <td class="actions">
                            <!-- 练习按钮  -->
                            <button
                                v-if="isStudent"
                                class="btn-action preview"
                                @click="doPractice(practice.practiceid)"
                                title="练习"
                                :disabled="isOverdue(practice.end_time) ||
                            (practice.isSubmitted && !practice.allowMultiple)"
                            >
                                练习
                            </button>

                            <!-- 查看按钮  TODO 学生查看练习报告？-->
                            <button
                                v-if="isStudent"
                                class="btn-action download"
                                @click="getPracticeReport(practice.id, practice.submitId)"
                                title="查看"
                                :disabled="!practice.isSubmitted"
                            >
                                查看
                            </button>
<!-- TODO 老师查看练习都有什么题-->
                            <button
                                v-if="isTeacher"
                                class="btn-action download"
                                @click="getPracticeReport(practice.id, practice.submitId)"
                                title="查看"
                                :disabled="!practice.isSubmitted"
                            >
                                查看
                            </button>

                            <!-- 批改按钮 (老师) -->
<!--                            <button-->
<!--                                v-if="isTeacher && practice.type === 'unScored'"-->
<!--                                class="btn-action history"-->
<!--                                @click="checkPractice(practice.submitId)"-->
<!--                                title="批改"-->
<!--                            >-->
<!--                                批改-->
<!--                            </button>-->

                            <!-- 导出按钮 -->
<!--                            <button-->
<!--                                class="btn-action renew"-->
<!--                                @click="downloadPracticeReport(practice.id)"-->
<!--                                title="导出"-->
<!--                                :disabled="!practice.isSubmitted"-->
<!--                            >-->
<!--                                导出-->
<!--                            </button>-->
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</template>

<style scoped>
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

.resource-list-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 1.5rem;
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

.btn-action:disabled {
    cursor: not-allowed; /* Change cursor to indicate the button is not clickable */
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

/* 黄色系按钮 */
.btn-action.history {
    background-color: #fff3e0;
    color: #ffa000;
}

.btn-action.history:hover {
    background-color: #ffe0b2;
}

.btn-action.renew {
    background-color: #ede7f6;
    color: #673ab7;
}

.btn-action.renew:hover {
    background-color: #d1c4e9;
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
</style>
