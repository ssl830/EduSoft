<script setup lang="ts">
import {ref, onMounted} from 'vue'
import { useAuthStore } from '../../stores/auth'
import ClassCard from '../../components/class/ClassCard.vue'
import ClassApi from '../../api/class'
import CourseApi from '../../api/course'  // 新增课程API引入

const authStore = useAuthStore()
const classes = ref([])
const courses = ref([])
const loading = ref(true)
const error = ref('')
const errorDialog = ref('')

// 弹窗相关状态
const showCreateClassDialog = ref(false)
const newClass = ref({
    classTime: '',
    classCode: '',
    courseId: 0  // 新增课程ID字段
})

const showJoinClassDialog = ref(false)
const joinClassCode = ref('')

// 加入班级功能
const joinClass = async () => {
    if (!joinClassCode.value.trim()) {
        errorDialog.value = '请输入班级代码'
        return
    }

    try {
        await ClassApi.joinClass(String(authStore.user?.id), joinClassCode.value)

        // 重置表单并关闭弹窗
        resetJoinForm()

        // 刷新班级列表
        await fetchClasses()
    } catch (err) {
        errorDialog.value = '加入班级失败: ' + (err.response?.data?.message || '请检查代码是否正确')
        console.error(err)
    }
}

// 重置加入表单
const resetJoinForm = () => {
    showJoinClassDialog.value = false
    joinClassCode.value = ''
    errorDialog.value = ''
}

// 获取班级列表
const fetchClasses = async () => {
    try {
        const response = await ClassApi.getUserClasses(authStore.user?.id)
        classes.value = response.data.data
    } catch (err) {
        error.value = '获取班级列表失败，请稍后再试'
        console.error(err)
    }
}

// 获取用户课程列表
const fetchCourses = async () => {
    try {
        const response = await CourseApi.getUserCourses(authStore.user?.id)
        courses.value = response.data.data
    } catch (err) {
        console.error('获取课程列表失败:', err)
    }
}

const resetUploadForm = () => {
    showCreateClassDialog.value = false
    newClass.value.classTime = ''
    newClass.value.classCode = ''
    newClass.value.courseId = 0
}

// 可以添加更严格的时间有效性验证（可选）
const isValidTime = (time: string) => {
    const hours = parseInt(time.slice(0, 2))
    const minutes = parseInt(time.slice(2))
    return hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59
}

// 创建班级
const createClass = async () => {
    if (!newClass.value.classTime || !newClass.value.classCode || newClass.value.courseId == 0) {
        errorDialog.value = '请填写所有必填字段'
        return
    }

    const timeFormat = /^\d{4}-\d{4}$/
    if (!timeFormat.test(newClass.value.classTime)) {
        errorDialog.value = '时间格式应为 0800-0935 格式'
        return
    }

    // 在格式验证后添加时间有效性检查
    const [startTime, endTime] = newClass.value.classTime.split('-')
    if (!isValidTime(startTime) || !isValidTime(endTime)) {
        errorDialog.value = '无效的时间值（时间范围应为0000-2359）'
        return
    }

    const toMinutes = (time: string) => {
        const hours = parseInt(time.slice(0, 2))
        const minutes = parseInt(time.slice(2))
        return hours * 60 + minutes
    }

    const startMinutes = toMinutes(startTime)
    const endMinutes = toMinutes(endTime)

    // 验证时间顺序
    if (startMinutes >= endMinutes) {
        errorDialog.value = '开始时间必须早于结束时间'
        return
    }

    try {
        await ClassApi.createClass({
            courseId: newClass.value.courseId,
            name: newClass.value.classTime,
            code: newClass.value.classCode,
        })

        // 重置表单
        resetUploadForm()

        // 刷新列表
        await fetchClasses()
    } catch (err) {
        errorDialog.value = '创建班级失败: ' + (err.response?.data?.message || '请稍后再试')
        console.error(err)
    }
}

onMounted(async () => {
    if (authStore.isAuthenticated) {
        try {
            await Promise.all([fetchClasses(), fetchCourses()])
        } finally {
            loading.value = false
        }
    } else {
        loading.value = false
    }
})
</script>

<template>
    <div class="home-container">
        <section v-if="authStore.isAuthenticated" class="courses-section">
            <div class="section-header">
                <h2>我的班级</h2>
                <button
                    v-if="authStore.userRole === 'student'"
                    class="btn-primary"
                    @click="showJoinClassDialog = true"
                >
                    加入班级
                </button>
                <button
                    v-if="authStore.userRole === 'teacher'"
                    class="btn-primary"
                    @click="showCreateClassDialog = true"
                >
                    创建班级
                </button>
            </div>

            <!-- 加入班级弹窗 -->
            <div v-if="showJoinClassDialog" class="dialog-overlay">
                <div class="create-class-dialog">
                    <h3>加入班级</h3>
                    <div class="form-group">
                        <label>班级代码</label>
                        <input
                            v-model="joinClassCode"
                            type="text"
                            placeholder="输入班级代码"
                        >
                    </div>
                    <div class="dialog-actions">
                        <button class="btn-secondary" @click="resetJoinForm()">
                            取消
                        </button>
                        <button class="btn-primary" @click="joinClass">
                            确定
                        </button>
                    </div>
                    <div v-if="errorDialog" class="error-message">{{ errorDialog }}</div>
                </div>
            </div>

            <!-- 创建班级弹窗 -->
            <div v-if="showCreateClassDialog" class="dialog-overlay">
                <div class="create-class-dialog">
                    <h3>新建班级</h3>
                    <div class="form-group">
                        <label>所属课程</label>
                        <select v-model="newClass.courseId">
                            <option value="" disabled>请选择课程</option>
                            <option
                                v-for="course in courses"
                                :key="course.id"
                                :value="course.id"
                            >
                                {{ course.name }} ({{ course.code }})
                            </option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>授课时间</label>
                        <input
                            v-model="newClass.classTime"
                            type="text"
                            placeholder="例如：0800-0935"
                        >
                    </div>
                    <div class="form-group">
                        <label>班级代码</label>
                        <input
                            v-model="newClass.classCode"
                            type="text"
                            placeholder="输入班级唯一代码"
                        >
                    </div>
                    <div class="dialog-actions">
                        <button class="btn-secondary" @click="resetUploadForm">
                            取消
                        </button>
                        <button class="btn-primary" @click="createClass">
                            确定
                        </button>
                    </div>
                    <div v-if="errorDialog" class="error-message">{{ errorDialog }}</div>
                </div>
            </div>

            <div v-if="loading" class="loading">加载中...</div>
            <div v-else-if="error" class="error-message">{{ error }}</div>
            <div v-else-if="classes.length === 0" class="empty-state">
                <p>您还没有参与任何班级</p>
            </div>
            <div v-else class="course-grid">
                <ClassCard
                    v-for="aclass in classes"
                    :key="aclass.id"
                    :aclass="aclass"
                />
            </div>
        </section>
    </div>
</template>

<style scoped>

/* 新增弹窗样式 */
.dialog-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.create-class-dialog {
    background: white;
    padding: 2rem;
    border-radius: 8px;
    width: 400px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.create-class-dialog h3 {
    margin-top: 0;
    margin-bottom: 1.5rem;
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
.form-group select {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
}

select {
    appearance: none;
    background: white url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e") no-repeat right 0.75rem center;
    background-size: 1em;
}

.dialog-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    margin-top: 1.5rem;
}

.btn-secondary {
    background-color: #f5f5f5;
    color: #424242;
    border: 1px solid #ddd;
}



.home-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem 1rem;
}

.welcome-section {
    margin-bottom: 2rem;
    text-align: center;
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.course-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 1.5rem;
}

.features-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 1.5rem;
    margin-top: 1.5rem;
}

.feature-card {
    padding: 1.5rem;
    border-radius: 8px;
    background-color: #f7f9fc;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    transition: transform 0.3s ease;
}

.feature-card:hover {
    transform: translateY(-5px);
}

.loading, .empty-state {
    text-align: center;
    padding: 2rem;
    color: #666;
}

.error-message {
    color: #e53935;
    text-align: center;
    padding: 1rem;
}

.btn-primary {
    background-color: #2c6ecf;
    color: white;
    border: none;
    padding: 0.5rem 1rem;
    border-radius: 4px;
    cursor: pointer;
    font-weight: 500;
    transition: background-color 0.2s;
}

.btn-primary:hover {
    background-color: #215bb4;
}
</style>
