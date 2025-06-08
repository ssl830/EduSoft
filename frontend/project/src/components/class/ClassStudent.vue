<script setup lang="ts">
import {onMounted, reactive, ref, computed} from 'vue'
import { defineProps } from 'vue'
import ClassApi from '../../api/class'
import {useAuthStore} from "../../stores/auth.ts";

import Papa from 'papaparse'  // CSV解析库
import * as XLSX from 'xlsx'  // 新增：xlsx解析库

const props = defineProps<{
    classId: string;
    isTeacher: boolean;
    teacherId: number | string;
}>()

const students = ref<any[]>([])
const loading = ref(true)
const error = ref('')

// File upload
const showUploadFileForm = ref(false)
const showUploadAloneForm = ref(false)

const uploadFileForm = ref({
    classId: props.classId,
    file: null as File | null,
    parsedStudents: []
})
let uploadAloneForm = reactive({
    classId: props.classId,
    file: null as File | null,
    students: [
        { id: 23373999, name: "士小信" }
    ]
})
const uploadProgress = ref(0)
const uploadError = ref('')
const uploadAloneError = ref('')

const authStore = useAuthStore()

// Fetch students
const fetchStudents = async () => {
    loading.value = true
    error.value = ''

    try {
        const response = await ClassApi.getClassStudents(props.classId)
        // 修正：正确赋值为response.data
        students.value = (response.data || []).map((stu: any) => ({
            ...stu,
            isTeacher: String(stu.userId) === String(props.teacherId)
        }))
    } catch (err) {
        error.value = '获取学生列表失败，请稍后再试'
        console.error(err)
        students.value = []
    } finally {
        loading.value = false
    }
}

// 新增CSV解析相关状态
const isParsing = ref(false)
const parsingError = ref('')

// 新增：下载模板方法
const downloadTemplate = () => {
    const ws = XLSX.utils.aoa_to_sheet([
        ['name', 'id'],
        ['张三', '20230001'],
        ['李四', '20230002']
    ])
    const wb = XLSX.utils.book_new()
    XLSX.utils.book_append_sheet(wb, ws, '模板')
    XLSX.writeFile(wb, '学生导入模板.xlsx')
}

// 新增：下载CSV模板方法
const downloadCSVTemplate = () => {
    const csvContent = 'name,id\n张三,20230001\n李四,20230002\n';
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.setAttribute('download', '学生导入模板.csv');
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

// 修改文件处理函数，支持xlsx和csv
const handleFileChange = async (event: Event) => {
    const input = event.target as HTMLInputElement
    if (!input.files || input.files.length === 0) return

    const file = input.files[0]
    const fileName = file.name.toLowerCase()

    // 支持csv和xlsx
    if (fileName.endsWith('.csv')) {
        // 解析CSV为JSON
        try {
            const parsedData = await parseCSV(file)
            if (!validateStudentData(parsedData)) {
                throw new Error('CSV格式错误：必须包含"name"和"id"列')
            }
            uploadFileForm.value.parsedStudents = parsedData
            uploadError.value = ''
        } catch (err) {
            parsingError.value = err instanceof Error ? err.message : '文件解析失败'
            uploadFileForm.value.parsedStudents = []
        }
    } else if (fileName.endsWith('.xlsx')) {
        // 解析XLSX为JSON
        try {
            const data = await file.arrayBuffer()
            const workbook = XLSX.read(data, { type: 'array' })
            const sheetName = workbook.SheetNames[0]
            const sheet = workbook.Sheets[sheetName]
            const json = XLSX.utils.sheet_to_json(sheet, { header: 1 })
            // 第一行为表头
            const [header, ...rows] = json
            if (!header || !header.includes('name') || !header.includes('id')) {
                throw new Error('XLSX格式错误：必须包含"name"和"id"表头')
            }
            const nameIdx = header.indexOf('name')
            const idIdx = header.indexOf('id')
            const parsedData = rows.filter(r => r.length >= 2).map(r => ({
                name: r[nameIdx],
                id: r[idIdx]
            }))
            if (!validateStudentData(parsedData)) {
                throw new Error('XLSX内容错误：必须包含"name"和"id"列')
            }
            uploadFileForm.value.parsedStudents = parsedData
            uploadError.value = ''
        } catch (err) {
            parsingError.value = err instanceof Error ? err.message : '文件解析失败'
            uploadFileForm.value.parsedStudents = []
        }
    } else {
        uploadError.value = '仅支持CSV或XLSX格式文件'
        uploadFileForm.value.parsedStudents = []
    }
}

// CSV文件验证
const validateCSVFile = (file: File) => {
    const validTypes = ['text/csv', 'application/vnd.ms-excel']
    const validExtension = file.name.endsWith('.csv')
    return validTypes.includes(file.type) || validExtension
}

// 修正parseCSV，自动检测分隔符，增强兼容性
const parseCSV = (file: File): Promise<any[]> => {
    return new Promise((resolve, reject) => {
        Papa.parse(file, {
            header: true,        // 使用第一行为header
            skipEmptyLines: true,
            delimiter: '', // 让PapaParse自动检测分隔符
            complete: (results) => {
                if (results.errors.length > 0) {
                    // 只要有一行是FieldMismatch就友好提示
                    const fieldMismatch = results.errors.find(e => e.code === 'TooManyFields' || e.code === 'TooFewFields')
                    if (fieldMismatch) {
                        reject('CSV文件格式有误，请确保首行为表头且分隔符为英文逗号（,），可下载模板参考。')
                    } else {
                        reject(results.errors[0].message)
                    }
                } else {
                    resolve(results.data)
                }
            },
            error: (err) => {
                reject(err)
            }
        })
    })
}

// 数据验证
const validateStudentData = (data: any[]) => {
    return data.every(item =>
        Object.keys(item).includes('name') &&
        Object.keys(item).includes('id')
    )
}

// 新增：导入错误弹窗状态
const showImportErrorDialog = ref(false)

// 修改上传函数，确保studentData包含student_id和student_name字段
const uploadFileStudents = async () => {
    if (!uploadFileForm.value.parsedStudents?.length) {
        uploadError.value = '请先上传并解析有效文件'
        showImportErrorDialog.value = true
        return
    }

    uploadProgress.value = 0
    uploadError.value = ''
    showImportErrorDialog.value = false

    try {
        // 适配后端参数：student_id和student_name
        const response = await ClassApi.uploadStudents({
            classId: props.classId,
            operatorId: authStore.user?.id,
            importType: 'FILE', // 文件导入
            studentData: uploadFileForm.value.parsedStudents.map(s => ({
                ...s,
                student_id: s.id, // 后端要求
                student_name: s.name // 后端如需
            }))
        })
        if(response.code != 200){
            uploadError.value = response.message || '上传失败，请稍后再试';
        }else{
            showUploadFileForm.value = false
            resetUploadFileForm()
            fetchStudents()  // 刷新列表
        }
    } catch (err: any) {
        // 兼容多种后端/拦截器格式，保证message一定有值
        const msg =
            err?.data?.message ||
            err?.data?.msg ||
            err?.data?.data?.message ||
            err?.response?.data?.message ||
            err?.response?.data?.msg ||
            '上传失败，请稍后再试';
        uploadError.value = msg;
        showImportErrorDialog.value = true;
        console.error(err);
    }
}

const uploadAloneStudents = async () => {
    // 检查是否存在空姓名或学号
    const hasEmptyField = uploadAloneForm.students.some(student =>
        student.name.trim() === '' || String(student.id).trim() === ''
    )

    if (hasEmptyField) {
        uploadAloneError.value = '请补全所有学生的姓名和学号'
        showImportErrorDialog.value = true
        return
    }

    uploadProgress.value = 0
    uploadAloneError.value = ''
    showImportErrorDialog.value = false

    try {
        // 适配后端参数：student_id和student_name
        const response = await ClassApi.uploadStudents({
            classId: props.classId,
            operatorId: authStore.user?.id,
            importType: 'MANUAL', // 手动导入
            studentData: uploadAloneForm.students.map(s => ({
                student_id: s.id, // 后端要求
                student_name: s.name // 后端如需
            }))
        })

        if(response.code != 200){
            uploadAloneError.value = response.message || '上传失败，请稍后再试';
        }else{
            showUploadAloneForm.value = false
            resetUploadAloneForm()
            fetchStudents()  // 刷新列表
        }
    } catch (err: any) {
        // 兼容多种后端/拦截器格式，保证message一定有值
        const msg =
            err?.data?.message ||
            err?.data?.msg ||
            err?.data?.data?.message ||
            err?.response?.data?.message ||
            err?.response?.data?.msg ||
            '上传失败，请稍后再试';
        uploadAloneError.value = msg;
        showImportErrorDialog.value = true;
        console.error(err);
    }
}

const resetUploadAloneForm = () => {
    uploadAloneForm.students = [{ id: 23373999, name: '士小信' }] // 保留一个空表单
    uploadProgress.value = 0
}

// 修改重置函数
const resetUploadFileForm = () => {
    uploadFileForm.value = {
        classId: props.classId,
        file: null,
        parsedStudents: []  // 新增解析结果存储
    }
    uploadProgress.value = 0
    uploadError.value = ''
    parsingError.value = ''
    // 清除文件输入框的 DOM 值
    const fileInput = document.getElementById('file') as HTMLInputElement
    if (fileInput) {
        fileInput.value = '' // 关键：清空 DOM 元素的值
    }
}

const toggleUploadForm = () => {
    showUploadFileForm.value = !showUploadFileForm.value
    if (!showUploadFileForm.value) {
        resetUploadFileForm()
    }
}

// Add an option for choice questions
const addStudent = () => {
    uploadAloneForm.students.push({ id: 0, name: '' })
}

// Remove an option
const deleteStudentByIndex = (index: number) => {
    if (uploadAloneForm.students.length > 1) {
        uploadAloneForm.students.splice(index, 1)
    }
}

// Preview resource
const deleteStudent = async(userId: string) => {
    if (!props.isTeacher) {
        alert('只有老师可以删除学生！')
        return
    }
    try {
        const response = await ClassApi.deleteStudents(props.classId, userId)
        students.value = students.value.filter(s => String(s.userId) !== String(userId));
        console.log(response)
        fetchStudents()  // 刷新列表
        alert('删除成功！')
    } catch (err) {
        uploadAloneError.value = '删除学生失败，请稍后再试'
        console.error(err)
    }
}

const sortedStudents = computed(() => {
    // 老师置顶，其他成员按原顺序
    return [...students.value].sort((a, b) => {
        if (a.isTeacher && !b.isTeacher) return -1;
        if (!a.isTeacher && b.isTeacher) return 1;
        return 0;
    });
});

onMounted(() => {
    fetchStudents()
})
</script>
<script lang="ts">
export default {
  name: 'ClassStudent'
}
</script>

<template>
    <div class="resource-list-container">
        <div class="resource-header">
            <h2>班级成员管理</h2>
            <div class="button-group">
                <button
                    v-if="isTeacher"
                    class="btn-primary"
                    @click="toggleUploadForm"
                >
                    {{ showUploadFileForm ? '取消导入' : '批量导入' }}
                </button>
                <button
                    v-if="isTeacher"
                    class="btn-primary"
                    @click="showUploadAloneForm = !showUploadAloneForm"
                >
                    {{ showUploadAloneForm ? '取消导入' : '手动导入' }}
                </button>
            </div>
        </div>
        <div v-if="showUploadAloneForm" class="upload-form">
            <h3>新增学生</h3>
            <div v-if="uploadAloneError" class="error-message">{{ uploadAloneError }}</div>
            <div class="form-group">
                <label>选项</label>
                <div
                    v-for="(student, index) in uploadAloneForm.students"
                    :key="index"
                    class="option-row"
                >
                    <input
                        v-model="student.id"
                        type="text"
                        :placeholder="`学号`"
                        class="option-input"
                        style="max-width: 600px; margin-right: 20px"
                    />
                    <input
                        v-model="student.name"
                        type="text"
                        :placeholder="`姓名`"
                        class="option-input"
                    />
                    <button
                        type="button"
                        class="btn-icon"
                        @click="deleteStudentByIndex(index)"
                        :disabled="uploadAloneForm.students.length <= 1"
                    >
                        ✕
                    </button>
                </div>

                <button
                    type="button"
                    class="btn-text"
                    @click="addStudent"
                >
                    + 添加学生
                </button>
            </div>
            <div class="form-actions">
                <button
                    type="button"
                    class="btn-primary"
                    @click="uploadAloneStudents"
                >
                    上传
                </button>
            </div>
        </div>
        <!-- 在上传表单部分添加解析状态显示 -->
        <div v-if="showUploadFileForm" class="upload-form">
            <h3>批量导入学生名单</h3>

            <div v-if="uploadError" class="error-message">{{ uploadError }}</div>
            <div v-if="parsingError" class="error-message">{{ parsingError }}</div>

            <div class="form-group">
                <label for="file">选择CSV文件</label>
                <input
                    id="file"
                    type="file"
                    accept=".csv,.xlsx"
                    @change="handleFileChange"
                    required
                />
                <div class="file-hint">支持格式：CSV/XLSX（需包含"name"和"id"列）
                    <button type="button" class="btn-text" @click="downloadTemplate">下载XLSX模板</button>
                    <button type="button" class="btn-text" @click="downloadCSVTemplate">下载CSV模板</button>
                </div>
            </div>

            <!-- 添加解析状态 -->
            <div v-if="isParsing" class="loading-container">正在解析文件...</div>

            <!-- 显示解析结果预览 -->
            <div v-if="uploadFileForm.parsedStudents?.length" class="preview-section">
                <h4>即将导入 {{ uploadFileForm.parsedStudents.length }} 条学生记录</h4>
                <div class="preview-table">
                    <div class="preview-row header">
                        <div>姓名</div>
                        <div>学号</div>
                    </div>
                    <div
                        v-for="(student, index) in uploadFileForm.parsedStudents.slice(0,5)"
                        :key="index"
                        class="preview-row"
                    >
                        <div>{{ student.name }}</div>
                        <div>{{ student.id }}</div>
                    </div>
                    <div v-if="uploadFileForm.parsedStudents.length > 5" class="preview-more">
                        更多记录...（共 {{ uploadFileForm.parsedStudents.length }} 条）
                    </div>
                </div>
            </div>
            <div class="form-actions">
                <button
                    type="button"
                    class="btn-secondary"
                    @click="resetUploadFileForm"
                >
                    重置
                </button>
                <button
                    type="button"
                    class="btn-primary"
                    @click="uploadFileStudents"
                >
                    上传
                </button>
            </div>

            <!-- ...其余上传表单内容保持不变... -->
        </div>
        <!-- Students List -->
        <div v-if="loading" class="loading-container">加载中...</div>
        <div v-else-if="error" class="error-message">{{ error }}</div>
        <div v-else-if="students.length === 0" class="empty-state">
            暂无学生
        </div>
        <div v-else class="resource-table-wrapper">
            <table class="resource-table">
                <thead>
                <tr>
                    <th style="width: 25%">姓名</th>
                    <th style="width: 25%">学号</th>
                    <th style="width: 25%">操作</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="student in sortedStudents" :key="student.userId">
                    <td>
                        {{ student.studentName }}
                        <span v-if="student.isTeacher" style="color: #1976d2; font-size: 0.85em; margin-left: 4px;">[老师]</span>
                    </td>
                    <td>{{ student.studentId }}</td>
                    <td class="actions">
                        <button
                            v-if="isTeacher && student.userId !== authStore.user?.id && !student.isTeacher"
                            class="btn-action preview"
                            @click="deleteStudent(student.userId)"
                            title="删除"
                        >
                            删除
                        </button>
                        <span v-else-if="student.userId === authStore.user?.id" style="color: #aaa;">不可删除</span>
                        <span v-else-if="student.isTeacher" style="color: #aaa;">不可删除</span>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
        <!-- 在<template>末尾添加弹窗组件 -->
        <div v-if="showImportErrorDialog" class="dialog-overlay">
            <div class="create-class-dialog">
                <h3>导入失败</h3>
                <div class="error-message">
                    {{ uploadError || uploadAloneError || '未知错误' }}
                </div>
                <div class="dialog-actions">
                    <button class="btn-primary" @click="showImportErrorDialog = false">确定</button>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>

.option-row {
    display: flex;
    align-items: center;
    margin-bottom: 0.5rem;
}

.option-input {
    flex: 1;
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

.file-hint {
    font-size: 0.875rem;
    color: #666;
    margin-top: 0.5rem;
}

.preview-section {
    margin-top: 1.5rem;
    border: 1px solid #eee;
    border-radius: 8px;
    padding: 1rem;
}

.preview-section h4 {
    margin: 0 0 1rem 0;
    font-size: 1rem;
}

.preview-table {
    font-size: 0.875rem;
}

.preview-row {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 1rem;
    padding: 0.5rem 0;
    border-bottom: 1px solid #eee;
}

.preview-row.header {
    font-weight: 600;
    background-color: #f8f8f8;
}

.preview-more {
    text-align: center;
    color: #666;
    padding: 0.5rem;
    font-size: 0.875rem;
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

.button-group {
    display: flex;
    gap: 10px;  /* 关键间距设置 */
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
