<template>
    <div class="homework-container">
        <!-- 顶部标题和创建作业按钮 -->
        <div class="header">
            <h2>班级作业</h2>
            <button
                v-if="isTeacher"
                class="create-btn"
                @click="showCreateDialog = true"
            >
                <i class="icon-add"></i> 创建作业
            </button>
        </div>

        <!-- 作业列表 -->
        <div class="homework-list">
            <div v-if="loading" class="loading">加载中...</div>
            <div v-else-if="error" class="error">{{ error }}</div>
            <div v-else-if="homeworks.length === 0" class="empty">
                <i class="icon-empty"></i>
                <p>暂无作业</p>
                <button v-if="isTeacher" @click="showCreateDialog = true">创建第一个作业</button>
            </div>
            <table v-else class="homework-table">
                <thead>
                <tr>
                    <th>作业名称</th>
                    <th>截止时间</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="hw in homeworks" :key="hw.homeworkId">
                    <td>
                        <div class="homework-title">
                            <i class="icon-hw"></i>
                            <span>{{ hw.title }}</span>
                            <span v-if="hw.fileUrl" class="attachment-tag">
                  <i class="icon-attachment"></i>附件
                </span>
                        </div>
                    </td>
                    <td>{{ formatDate(hw.end_time) }}</td>
                    <td class="actions">
                        <button class="btn-view" @click="viewHomework(hw)">
                            <i class="icon-view"></i> 查看
                        </button>
                        <button
                            v-if="isTeacher"
                            class="btn-delete"
                            @click="confirmDelete(hw)"
                        >
                            <i class="icon-delete"></i> 删除
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <!-- 创建作业弹窗 -->
        <div v-if="showCreateDialog" class="modal-backdrop">
            <div class="modal">
                <div class="modal-header">
                    <h3>创建作业</h3>
                    <button class="close-btn" @click="closeCreateDialog">&times;</button>
                </div>
                <div class="modal-body">
                    <form @submit.prevent="createHomework">
                        <div class="form-group">
                            <label>作业名称 *</label>
                            <input
                                type="text"
                                v-model="createForm.title"
                                placeholder="输入作业名称"
                                required
                            >
                        </div>
                        <div class="form-group">
                            <label>作业要求</label>
                            <textarea
                                v-model="createForm.description"
                                placeholder="详细描述作业要求"
                                rows="4"
                            ></textarea>
                        </div>
                        <div class="form-group">
                            <label>附件（可选）</label>
                            <div class="file-upload">
                                <input
                                    type="file"
                                    ref="fileInput"
                                    @change="handleFileChange"
                                >
                                <button
                                    v-if="createForm.file"
                                    type="button"
                                    class="remove-btn"
                                    @click="createForm.file = null"
                                >
                                    <i class="icon-remove"></i>
                                </button>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>截止时间 *</label>
                                <input
                                    type="datetime-local"
                                    v-model="createForm.end_time"
                                    required
                                >
                            </div>
                        </div>
                        <div class="form-actions">
                            <button
                                type="button"
                                class="btn-cancel"
                                @click="closeCreateDialog"
                            >
                                取消
                            </button>
                            <button
                                type="submit"
                                class="btn-submit"
                                :disabled="creating"
                            >
                                <span v-if="creating">创建中...</span>
                                <span v-else>创建作业</span>
                            </button>
                        </div>
                        <div v-if="createError" class="error">{{ createError }}</div>
                    </form>
                </div>
            </div>
        </div>

        <!-- 作业详情弹窗（学生） -->
        <div v-if="showDetailDialog" class="modal-backdrop">
            <div class="modal detail-modal">
                <div class="modal-header">
                    <h3>{{ currentHomework.title }}</h3>
                    <button class="close-btn" @click="closeDetailDialog">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="homework-info">
                        <div class="info-item">
                            <label>截止时间：</label>
                            <span>{{ formatDate(currentHomework.end_time) }}</span>
                        </div>
                        <div class="info-item">
                            <label>作业状态：</label>
                            <span :class="getStatusClass(currentHomework)">
                {{ getStatusText(currentHomework) }}
              </span>
                        </div>
                    </div>

                    <div class="description">
                        <label>作业要求：</label>
                        <p>{{ currentHomework.description || '无具体要求' }}</p>
                    </div>

                    <div v-if="currentHomework.fileUrl" class="attachment">
                        <label>作业附件：</label>
                        <div class="attachment-info">
                            <i class="icon-attachment"></i>
                            <span>{{ currentHomework.fileName }}</span>
                            <button
                                class="btn-download"
                                @click="downloadHomeworkAttachment(currentHomework)"
                            >
                                下载附件
                            </button>
                        </div>
                    </div>
                    <div v-if="downloadError" class="error">{{ downloadError }}</div>

                    <div
                        v-if="!isTeacher && canSubmit(currentHomework)"
                        class="submit-section"
                    >
                        <div class="form-group">
                            <label>提交作业</label>
                            <div class="file-upload">
                                <input
                                    type="file"
                                    ref="submitFileInput"
                                    @change="handleSubmitFileChange"
                                >
                                <button
                                    v-if="submitForm.file"
                                    type="button"
                                    class="remove-btn"
                                    @click="submitForm.file = null"
                                >
                                    <i class="icon-remove"></i>
                                </button>
                            </div>
                            <div class="submit-actions">
                                <button
                                    class="btn-submit"
                                    @click="submitHomework"
                                    :disabled="submitting"
                                >
                                    <span v-if="submitting">提交中...</span>
                                    <span v-else>提交作业</span>
                                </button>
                            </div>
                            <div v-if="submitError" class="error">{{ submitError }}</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 作业提交列表弹窗（教师） -->
        <div v-if="showSubmissionDialog" class="modal-backdrop">
            <div class="modal submissions-modal">
                <div class="modal-header">
                    <h3>{{ currentHomework.title }} - 提交列表</h3>
                    <button class="close-btn" @click="closeSubmissionDialog">&times;</button>
                </div>
                <div class="modal-body">
                    <div class="submissions-header">
                        <div class="submissions-count">
                            共 {{ submissions.length }} 份提交
                        </div>
                    </div>

                    <div class="submissions-list">
                        <div v-if="downloadError2" class="error">{{ downloadError2 }}</div>
                        <div v-if="submissionsLoading" class="loading">加载中...</div>
                        <div v-else-if="submissionsError" class="error">{{ submissionsError }}</div>
                        <div v-else-if="submissions.length === 0" class="empty">
                            <i class="icon-empty"></i>
                            <p>暂无提交</p>
                        </div>
                        <table v-else class="submissions-table">
                            <thead>
                            <tr>
                                <th>学号</th>
                                <th>姓名</th>
                                <th>提交时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <tr v-for="sub in submissions" :key="sub.id">
                                <td>{{ sub.studentId }}</td>
                                <td>{{ sub.studentName }}</td>
                                <td>{{ formatDateTime(sub.submit_time) }}</td>
                                <td>
                                    <button
                                        class="btn-download"
                                        @click="downloadSubmissionFile(sub)"
                                    >
                                        <i class="icon-download"></i> 下载
                                    </button>
                                </td>
                            </tr>
                            </tbody>
                        </table>
                   </div>
                </div>
            </div>
        </div>

        <!-- 删除确认弹窗 -->
        <div v-if="showDeleteConfirm" class="modal-backdrop">
            <div class="modal confirm-modal">
                <div class="modal-header">
                    <h3>确认删除</h3>
                    <button class="close-btn" @click="showDeleteConfirm = false">&times;</button>
                </div>
                <div class="modal-body">
                    <p>确定要删除作业 "{{ homeworkToDelete?.title }}" 吗？此操作不可恢复。</p>
                    <div class="confirm-actions">
                        <button class="btn-cancel" @click="showDeleteConfirm = false">
                            取消
                        </button>
                        <button
                            class="btn-delete"
                            @click="deleteHomework"
                            :disabled="deleting"
                        >
                            <span v-if="deleting">删除中...</span>
                            <span v-else>确认删除</span>
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'
import { format } from 'date-fns'
import ClassApi from "../../api/class.ts";
import ResourceApi from "../../api/resource.ts";
import {useAuthStore} from "../../stores/auth.ts";

// 定义作业类型
interface Homework {
    homeworkId: bigint
    class_id: string
    title: string
    description: string
    fileUrl: string | null
    fileName: string | null
    end_time: string
}

// 定义提交类型
interface Submission {
    submissionId: bigint
    studentId: string
    studentName: string
    fileUrl: string
    fileName: string
    submit_time: string
}

// 组件属性
const props = defineProps<{
    classId: bigint
    isTeacher: boolean
}>()

// 状态变量
const homeworks = ref<Homework[]>([])
const loading = ref(true)
const error = ref<string | null>(null)
const createError = ref<string | null>(null)
const submitError = ref<string | null>(null)
const downloadError = ref<string | null>(null)
const downloadError2 = ref<string | null>(null)
const submissionsError = ref<string | null>(null)

// 弹窗状态
const showCreateDialog = ref(false)
const showDetailDialog = ref(false)
const showSubmissionDialog = ref(false)
const showDeleteConfirm = ref(false)

// 当前操作的作业
const currentHomework = ref<Homework | null>(null)
const homeworkToDelete = ref<Homework | null>(null)

// 提交列表
const submissions = ref<Submission[]>([])
const submissionsLoading = ref(false)

// 表单数据
const createForm = ref({
    title: '',
    description: '',
    file: null as File | null,
    start_time: '',
    end_time: ''
})

const submitForm = ref({
    file: null as File | null
})

// 操作状态
const creating = ref(false)
const submitting = ref(false)
const deleting = ref(false)

// 计算属性：当前时间
const currentTime = computed(() => new Date())

// 获取班级作业列表
const fetchHomeworks = async () => {
    loading.value = true
    error.value = null
    try {
        console.log(props.classId)
        const response = await ClassApi.getHomeworkList(props.classId)
        homeworks.value = response.data.data
    } catch (err) {
        error.value = '获取作业列表失败，请稍后再试'
        console.error(err)
    } finally {
        loading.value = false
    }
}

// 创建作业
const createHomework = async () => {
    creating.value = true
    createError.value = null

    try {
        const formData = new FormData()
        formData.append('class_id', props.classId)
        formData.append('title', createForm.value.title)
        formData.append('description', createForm.value.description)
        formData.append('end_time', createForm.value.end_time)

        if (createForm.value.file) {
            formData.append('file', createForm.value.file)
        }

        await ClassApi.createHomework(formData)

        closeCreateDialog()
        await fetchHomeworks()
    } catch (err) {
        createError.value = '创建作业失败，请稍后再试'
        console.error(err)
    } finally {
        creating.value = false
    }
}

// 查看作业
const viewHomework = (hw: Homework) => {
    currentHomework.value = hw
    if (props.isTeacher) {
        fetchSubmissions(hw.homeworkId)
        showSubmissionDialog.value = true
    } else {
        showDetailDialog.value = true
    }
}

// 获取作业提交列表（教师）
const fetchSubmissions = async (homeworkId: bigint) => {
    submissionsLoading.value = true
    submissionsError.value = null
    console.log(homeworkId)
    try {
        const response = await ClassApi.fetchSubmissions(homeworkId)
        submissions.value = response.data.data
        console.log(response.data.data)
    } catch (err) {
        submissionsError.value = '获取提交列表失败，请稍后再试'
        console.error(err)
    } finally {
        submissionsLoading.value = false
    }
}
const authStore = useAuthStore()

// 提交作业（学生）
const submitHomework = async () => {
    if (!submitForm.value.file) {
        submitError.value = '请选择要提交的文件'
        return
    }

    submitting.value = true
    submitError.value = null

    try {
        const formData = new FormData()
        formData.append('student_id', authStore.user?.id)
        formData.append('file', submitForm.value.file)

        await ClassApi.uploadSubmissionFile(currentHomework.value?.id, formData)

        closeDetailDialog()
    } catch (err) {
        submitError.value = '提交作业失败，请稍后再试'
        console.error(err)
    } finally {
        submitting.value = false
    }
}

// 删除作业
const confirmDelete = (hw: Homework) => {
    homeworkToDelete.value = hw
    showDeleteConfirm.value = true
}

const deleteHomework = async () => {
    if (!homeworkToDelete.value) return

    deleting.value = true
    try {
        await ClassApi.deleteHomework(homeworkToDelete.value.id)
        homeworks.value = homeworks.value.filter(
            hw => hw.id !== homeworkToDelete.value?.id
        )
        showDeleteConfirm.value = false
    } catch (err) {
        console.error('删除作业失败:', err)
    } finally {
        deleting.value = false
    }
}

// 下载作业附件
const downloadHomeworkAttachment = async (homework: Homework) => {
    try {
        console.log(homework.homeworkId)
        // 1. 获取文件直链
        const fileUrl = homework.fileUrl
        const fileName = homework.fileName

        // 2. 通过 Fetch/Blob 间接下载
        const res = await fetch(fileUrl)
        const blob = await res.blob()

        // 3. 创建本地下载链接
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()

        // 4. 清理内存
        window.URL.revokeObjectURL(url)
        document.body.removeChild(link)
    } catch (err) {
        downloadError.value = '下载附件失败，请稍后再试'
        console.error(err)
    }
}

// 下载学生提交的作业
const downloadSubmissionFile = async (submission: Submission) => {
    try {
        console.log(submission.submissionId)
        // 1. 获取文件直链
        const fileUrl = submission.fileUrl
        const fileName = submission.fileName

        // 2. 通过 Fetch/Blob 间接下载
        const res = await fetch(fileUrl)
        const blob = await res.blob()

        // 3. 创建本地下载链接
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()

        // 4. 清理内存
        window.URL.revokeObjectURL(url)
        document.body.removeChild(link)
    } catch (err) {
        downloadError2.value = '下载资源失败，请稍后再试'
        console.error(err)
    }
}

// 文件处理
const handleFileChange = (e: Event) => {
    const target = e.target as HTMLInputElement
    if (target.files && target.files.length > 0) {
        createForm.value.file = target.files[0]
    }
}

const handleSubmitFileChange = (e: Event) => {
    const target = e.target as HTMLInputElement
    if (target.files && target.files.length > 0) {
        submitForm.value.file = target.files[0]
    }
}

// 关闭弹窗
const closeCreateDialog = () => {
    showCreateDialog.value = false
    createForm.value = {
        title: '',
        description: '',
        file: null,
        start_time: '',
        end_time: ''
    }
    createError.value = null
}

const closeDetailDialog = () => {
    showDetailDialog.value = false
    currentHomework.value = null
    submitForm.value.file = null
    submitError.value = null
}

const closeSubmissionDialog = () => {
    showSubmissionDialog.value = false
    currentHomework.value = null
    submissions.value = []
}

// 工具函数
const formatDate = (dateString: string) => {
    return format(new Date(dateString), 'yyyy-MM-dd HH:mm')
}

const formatDateTime = (dateString: string) => {
    return format(new Date(dateString), 'yyyy-MM-dd HH:mm:ss')
}

const getStatusClass = (hw: Homework) => {
    const end = new Date(hw.end_time)

    if (currentTime.value <= end) {
        return 'status-ongoing'
    } else {
        return 'status-ended'
    }
}

const getStatusText = (hw: Homework) => {
    const start = new Date(hw.start_time)
    const end = new Date(hw.end_time)

    if (currentTime.value < start) {
        return '未开始'
    } else if (currentTime.value >= start && currentTime.value <= end) {
        return '进行中'
    } else {
        return '已截止'
    }
}

const canSubmit = (hw: Homework) => {
    const end = new Date(hw.end_time)
    return currentTime.value <= end
}

// 初始化
onMounted(() => {
    fetchHomeworks()
})
</script>

<style scoped>
.homework-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 24px;
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid #eaeaea;
}

h2 {
    font-size: 24px;
    color: #333;
    margin: 0;
}

.create-btn {
    background-color: #4CAF50;
    color: white;
    border: none;
    padding: 10px 20px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
    display: flex;
    align-items: center;
    transition: background-color 0.3s;
}

.create-btn:hover {
    background-color: #45a049;
}

.icon-add {
    margin-right: 8px;
    font-weight: bold;
}

.homework-table {
    width: 100%;
    border-collapse: collapse;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    border-radius: 8px;
    overflow: hidden;
}

.homework-table th, .homework-table td {
    padding: 16px;
    text-align: left;
    border-bottom: 1px solid #f0f0f0;
}

.homework-table th {
    background-color: #f8f9fa;
    font-weight: 600;
    color: #495057;
}

.homework-table tr:hover {
    background-color: #f9f9f9;
}

.homework-title {
    display: flex;
    align-items: center;
    gap: 8px;
}

.icon-hw {
    color: #4CAF50;
    font-size: 18px;
}

.attachment-tag {
    background-color: #e3f2fd;
    color: #1976d2;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 12px;
    display: flex;
    align-items: center;
    gap: 4px;
}

.status-not-started {
    background-color: #e0e0e0;
    color: #616161;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 13px;
}

.status-ongoing {
    background-color: #e3f2fd;
    color: #1976d2;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 13px;
}

.status-ended {
    background-color: #ffebee;
    color: #c62828;
    padding: 4px 8px;
    border-radius: 4px;
    font-size: 13px;
}

.actions {
    display: flex;
    gap: 8px;
}

.btn-view {
    background-color: #2196F3;
    color: white;
    border: none;
    padding: 6px 12px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 4px;
}

.btn-view:hover {
    background-color: #0b7dda;
}

.btn-delete {
    background-color: #f44336;
    color: white;
    border: none;
    padding: 6px 12px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 13px;
    display: flex;
    align-items: center;
    gap: 4px;
}

.btn-delete:hover {
    background-color: #d32f2f;
}

/* 弹窗样式 */
.modal-backdrop {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal {
    background-color: white;
    border-radius: 8px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    width: 100%;
    max-width: 600px;
    max-height: 90vh;
    overflow-y: auto;
}

.detail-modal, .submissions-modal {
    max-width: 700px;
}

.modal-header {
    padding: 16px 24px;
    border-bottom: 1px solid #eaeaea;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.modal-header h3 {
    margin: 0;
    font-size: 18px;
    color: #333;
}

.close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #999;
}

.close-btn:hover {
    color: #333;
}

.modal-body {
    padding: 24px;
}

.form-group {
    margin-bottom: 20px;
}

.form-group label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #555;
}

.form-group input, .form-group textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    box-sizing: border-box;
}

.form-group textarea {
    resize: vertical;
    min-height: 80px;
}

.form-row {
    display: flex;
    gap: 16px;
}

.form-row .form-group {
    flex: 1;
}

.file-upload {
    display: flex;
    align-items: center;
    gap: 8px;
}

.upload-btn {
    background-color: #f5f5f5;
    border: 1px solid #ddd;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
}

.upload-btn:hover {
    background-color: #eaeaea;
}

.file-name {
    font-size: 14px;
    color: #666;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 200px;
}

.remove-btn {
    background: none;
    border: none;
    color: #f44336;
    cursor: pointer;
    font-size: 18px;
    padding: 4px;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    margin-top: 24px;
}

.btn-cancel {
    background-color: #f5f5f5;
    border: 1px solid #ddd;
    padding: 8px 20px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.btn-cancel:hover {
    background-color: #eaeaea;
}

.btn-submit {
    background-color: #4CAF50;
    color: white;
    border: none;
    padding: 8px 20px;
    border-radius: 4px;
    cursor: pointer;
    font-size: 14px;
}

.btn-submit:hover {
    background-color: #45a049;
}

.btn-submit:disabled {
    background-color: #a5d6a7;
    cursor: not-allowed;
}

/* 作业详情样式 */
.homework-info {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    margin-bottom: 20px;
    padding-bottom: 20px;
    border-bottom: 1px solid #f0f0f0;
}

.info-item {
    min-width: 200px;
}

.info-item label {
    font-weight: 500;
    color: #666;
}

.description {
    margin-bottom: 20px;
}

.description label {
    font-weight: 500;
    color: #666;
    display: block;
    margin-bottom: 8px;
}

.description p {
    margin: 0;
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 4px;
    line-height: 1.6;
}

.attachment {
    margin-bottom: 24px;
}

.attachment label {
    font-weight: 500;
    color: #666;
    display: block;
    margin-bottom: 8px;
}

.attachment-info {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 10px;
    background-color: #f9f9f9;
    border-radius: 4px;
}

.btn-download {
    background-color: #2196F3;
    color: white;
    border: none;
    padding: 6px 12px;
    border-radius: 4px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 13px;
}

.btn-download:hover {
    background-color: #0b7dda;
}

.submit-section {
    margin-top: 24px;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;
}

.submit-actions {
    margin-top: 16px;
    display: flex;
    justify-content: flex-end;
}

/* 提交列表样式 */
.submissions-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 16px;
}

.submissions-count {
    font-weight: 500;
    color: #666;
}

.download-all {
    display: flex;
    gap: 8px;
}

.submissions-table {
    width: 100%;
    border-collapse: collapse;
    box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    border-radius: 4px;
    overflow: hidden;
}

.submissions-table th, .submissions-table td {
    padding: 12px 16px;
    text-align: left;
    border-bottom: 1px solid #f0f0f0;
}

.submissions-table th {
    background-color: #f8f9fa;
    font-weight: 600;
    color: #495057;
}

.submissions-table tr:hover {
    background-color: #f9f9f9;
}

/* 加载和空状态 */
.loading, .empty {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    padding: 40px;
    color: #666;
}

.empty p {
    margin: 16px 0;
}

.empty button {
    background-color: #4CAF50;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
}

.error {
    padding: 16px;
    background-color: #ffebee;
    color: #c62828;
    border-radius: 4px;
    margin: 16px 0;
}

.confirm-modal {
    max-width: 500px;
}

.confirm-modal .modal-body {
    padding: 24px;
}

.confirm-modal p {
    margin: 0 0 24px;
    line-height: 1.6;
}

.confirm-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
}

.btn-delete {
    background-color: #f44336;
    color: white;
    border: none;
    padding: 8px 20px;
    border-radius: 4px;
    cursor: pointer;
}

.btn-delete:hover {
    background-color: #d32f2f;
}

.btn-delete:disabled {
    background-color: #ef9a9a;
    cursor: not-allowed;
}

/* 图标字体（简单模拟） */
[class^="icon-"]:before {
    font-family: 'Material Icons';
    font-weight: normal;
    font-style: normal;
    display: inline-block;
    line-height: 1;
    text-transform: none;
    letter-spacing: normal;
    word-wrap: normal;
    white-space: nowrap;
    direction: ltr;
    -webkit-font-smoothing: antialiased;
    text-rendering: optimizeLegibility;
    -moz-osx-font-smoothing: grayscale;
    font-feature-settings: 'liga';
}

</style>
