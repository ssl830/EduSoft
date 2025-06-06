<template>  <div class="learning-records-container">
    <!-- 下载错误提示 -->
    <div v-if="downloadStatus.error" class="alert alert-error">
      {{ downloadStatus.error }}
      <button class="alert-close" @click="downloadStatus.error = null">&times;</button>
    </div>    <header class="page-header">
      <h1>学习记录</h1>
      <p>查看您的学习轨迹、练习记录和反馈，帮助您更好地评估和调整学习计划。</p>
      <!-- 显示当前班级信息（教师/助教可见） -->
      <div v-if="(userRole === 'teacher' || userRole === 'assistant') && currentClass" class="class-info">
        <span class="class-badge">
          当前班级：{{ currentClass.className }} ({{ currentClass.courseName }})
        </span>
      </div>
      <div v-else-if="(userRole === 'teacher' || userRole === 'assistant') && classError" class="class-error">
        <span class="error-text">{{ classError }}</span>
      </div>
    </header><section class="filter-section">
      <div class="filter-controls">
        <input type="text" v-model="filters.exerciseName" placeholder="练习名称" class="filter-input">
        <select v-model="filters.status" class="filter-select">
          <option value="">所有完成状态</option>
          <option value="completed">已完成</option>
          <option value="in-progress">进行中</option>
          <option value="not-started">未开始</option>
        </select>
        <template v-if="userRole === 'teacher' || userRole === 'assistant'">
          <input type="text" v-model="filters.studentName" placeholder="学生姓名" class="filter-input">
        </template>
        <button @click="fetchRecords" class="btn btn-primary">筛选</button>
        <button 
          @click="exportAllRecords" 
          class="btn btn-export-all"
          :disabled="downloadStatus.loading"
        >{{ downloadStatus.loading ? '导出中...' : '导出全部记录' }}</button>
      </div>
    </section>

    <section class="records-list-section">
      <div class="course-badge-triangle-outer">
        <span class="course-badge-triangle">
          <span class="course-badge-text">课程A</span>
        </span>
      </div>
      <div v-if="loading" class="loading-indicator">加载中...</div>
      <div v-else-if="error" class="error-message">{{ error }}</div>
      <div v-else-if="records.length === 0" class="empty-state">
        <p>暂无学习记录。</p>
      </div>
      <div v-else>
        <table class="records-table">
          <thead>
            <tr>
              <th>练习名称</th>
              <th v-if="userRole === 'teacher' || userRole === 'assistant'">姓名</th>
              <th>完成状态</th>
              <th>得分</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="record in records" :key="record.id">
              <td>{{ record.exercise_title }}</td>
              <td v-if="userRole === 'teacher' || userRole === 'assistant'">{{ record.student_name }}</td>              <td>
                <div class="status-wrapper">
                  <span :class="['status-badge', getStatusStyle(record.status)]">
                    {{ getStatusText(record.status) }}
                  </span>
                  <ProgressKnob :value="record.percent || 61" />
                </div>
              </td>
              <td>{{ record.score !== null ? record.score : '-' }}</td>              <td class="actions-cell actions-cell-center">
                <button @click="doExercise(record.exercise_id)" class="btn btn-action btn-exercise">练习</button>
                <button @click="viewRecordDetail(record.exercise_id)" class="btn btn-action btn-view">查看</button>
                <button @click="correct(record.exercise_id)" class="btn btn-action btn-correct">批改</button>
                <button 
                  @click="exportRecord(record.exercise_id)" 
                  class="btn btn-action btn-export-blue"
                  :disabled="downloadStatus.loading"
                >{{ downloadStatus.loading ? '导出中...' : '导出记录' }}</button>
                <button 
                  @click="downloadReport(record.exercise_id)" 
                  class="btn btn-action btn-export"
                  :disabled="downloadStatus.loading"
                >{{ downloadStatus.loading ? '下载中...' : '导出报告' }}</button>
              </td>
            </tr>
          </tbody>
        </table>
        <div class="pagination-controls" v-if="totalPages > 1">
          <button @click="prevPage" :disabled="currentPage === 1" class="btn">上一页</button>
          <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="btn">下一页</button>
        </div>
      </div>
    </section>

    <!-- 练习记录详情模态框 -->
    <!-- <Modal :show="showDetailModal" @close="closeDetailModal">
      <template #header>
        <h3>练习详情</h3>
      </template>
      <template #body>
        <div v-if="detailLoading" class="loading-indicator">加载详情中...</div>
        <div v-else-if="detailError" class="error-message">{{ detailError }}</div>
        <div v-else-if="currentRecordDetail" class="record-detail-content">
          <h4>{{ currentRecordDetail.exercise_title }}</h4>
          <p><strong>总分：</strong> {{ currentRecordDetail.total_score }}</p>
          <p><strong>您的得分：</strong> {{ currentRecordDetail.user_score }}</p>
          <hr class="detail-divider"/>
          <h5>题目列表：</h5>
          <div v-for="(question, index) in currentRecordDetail.questions" :key="question.id" class="question-detail">
            <p><strong>题目 {{ index + 1 }}:</strong> {{ question.content }}</p>
            <p><strong>您的答案:</strong> <span class="answer">{{ formatAnswer(question.user_answer) }}</span></p>
            <p><strong>正确答案:</strong> <span class="answer">{{ formatAnswer(question.correct_answer) }}</span></p>
            <p><strong>反馈:</strong> {{ question.feedback || '无' }}</p>
            <hr v-if="index < currentRecordDetail.questions.length - 1" class="question-divider"/>
          </div>
        </div>
      </template>
      <template #footer>
        <button class="btn btn-secondary" @click="closeDetailModal">关闭</button>
      </template>
    </Modal> -->
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import StudyRecordsApi from '../../api/studyRecords'
import { defineAsyncComponent } from 'vue'
import { useAuthStore } from '../../stores/auth'
import CourseApi from '../../api/course'

const ProgressKnob = defineAsyncComponent(() => import('../../components/littlecomponents/ProgressKnob.vue'))

interface ExerciseRecord {
  id: string;
  exercise_id: string;
  exercise_title: string;
  student_name?: string;
  status: 'completed' | 'in-progress' | 'not-started';
  score: number | null;
  rank: number | null;
  percent?: number;
}

const router = useRouter()
const authStore = useAuthStore()
const userRole = computed(() => authStore.userRole)

// 班级相关状态
const currentClass = ref<{classId: string; className: string; courseId: string; courseName: string} | null>(null)
const classLoading = ref(false)
const classError = ref<string | null>(null)

// mock数据
const mockRecords = [
  {
    id: '1',
    exercise_id: '1',
    exercise_title: '人工智能',
    student_name: '45',
    status: 'completed' as const,
    score: 95,
    rank: 2,
    percent: 10 // 完成百分比
  }
]

const records = ref<ExerciseRecord[]>(mockRecords)
const loading = ref(false)
const error = ref<string | null>(null)

const filters = ref({
  exerciseName: '',
  status: '' as ExerciseRecord['status'] | '',
  studentName: ''
})

const currentPage = ref(1)
const pageSize = ref(10)
const totalRecords = ref(1)
const totalPages = computed(() => Math.ceil(totalRecords.value / pageSize.value))

// 获取用户当前班级
const fetchUserClass = async () => {
  try {
    classLoading.value = true
    classError.value = null
    
    // 尝试获取用户的默认班级
    const response = await CourseApi.getUserDefaultClass()
    currentClass.value = response.data
  } catch (err: any) {
    console.error('获取用户班级失败:', err)
    classError.value = '获取班级信息失败'
    
    // 如果获取默认班级失败，尝试获取用户所有班级中的第一个
    try {
      const classesResponse = await CourseApi.getUserClasses()
      if (classesResponse.data && classesResponse.data.length > 0) {
        const firstClass = classesResponse.data[0]
        currentClass.value = {
          classId: firstClass.id || firstClass.classId,
          className: firstClass.name || firstClass.className,
          courseId: firstClass.courseId,
          courseName: firstClass.courseName
        }
        classError.value = null
      }
    } catch (secondErr) {
      console.error('获取用户班级列表也失败:', secondErr)
      // 保持错误状态，使用默认值
    }
  } finally {
    classLoading.value = false
  }
}

const fetchRecords = async () => {
  // 这里用mock数据
  loading.value = false
  error.value = null
  records.value = mockRecords
  totalRecords.value = mockRecords.length
}

const getStatusText = (status: ExerciseRecord['status']) => {
  switch (status) {
    case 'completed': return '已完成'
    case 'in-progress': return '进行中'
    case 'not-started': return '未开始'
    default: return '未知'
  }
}

const getStatusStyle = (status: ExerciseRecord['status']) => {
  switch (status) {
    case 'completed': return 'status-completed'
    case 'in-progress': return 'status-progress'
    case 'not-started': return 'status-not-started'
    default: return ''
  }
}

// 分页方法
const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    fetchRecords()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    fetchRecords()
  }
}

// 操作方法
const doExercise = (exerciseId: string) => {
  router.push(`/exercise/${exerciseId}`)
}

const viewRecordDetail = async (exerciseId: string) => {
  router.push(`/exercise/${exerciseId}/detail`)
}

const correct = (exerciseId: string) => {
  router.push(`/exercise/${exerciseId}/correct`)
}

// 导出和下载相关的状态
const downloadStatus = ref<{
  loading: boolean;
  error: string | null;
}>({
  loading: false,
  error: null
});

const handleFileDownload = (blob: Blob, filename: string) => {
  try {
    // 检查是否为错误响应（通常错误响应是JSON格式）
    if (blob.type === 'application/json') {
      // 读取blob内容作为错误信息
      const reader = new FileReader();
      reader.onload = () => {
        try {
          const errorData = JSON.parse(reader.result as string);
          downloadStatus.value.error = errorData.message || '下载失败，请稍后再试';
        } catch {
          downloadStatus.value.error = '下载失败，请稍后再试';
        }
      };
      reader.readAsText(blob);
      return;
    }

    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    
    // 安全地移除DOM元素和释放URL对象
    if (link.parentNode) {
      document.body.removeChild(link);
    }
    window.URL.revokeObjectURL(url);
    
    // 显示成功消息
    downloadStatus.value.error = null;
  } catch (error) {
    console.error('文件下载失败:', error);
    downloadStatus.value.error = '文件下载失败，请稍后再试';
  }
};

// 导出记录（Excel 格式）
const exportRecord = async (exerciseId: string) => {
  try {
    downloadStatus.value.loading = true;
    downloadStatus.value.error = null;
    
    // 根据用户角色决定调用不同的导出方法
    if (userRole.value === 'student') {
      const response = await StudyRecordsApi.downloadStudentExerciseReport(exerciseId, 'excel');
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      handleFileDownload(response.data, `练习记录_${exerciseId}_${timestamp}.xlsx`);
    } else if (userRole.value === 'teacher' || userRole.value === 'assistant') {      const response = await StudyRecordsApi.exportClassExerciseReport({
        classId: currentClass.value?.classId || '1', // 使用动态获取的班级ID，如果没有则回退到默认值
        exerciseId,
        format: 'excel'
      });
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      handleFileDownload(response.data, `班级练习记录_${exerciseId}_${timestamp}.xlsx`);
    }
  } catch (err: any) {
    console.error('导出记录失败:', err);
    
    // 处理不同类型的错误
    if (err.response?.status === 401) {
      downloadStatus.value.error = '未登录或登录已过期，请重新登录';
    } else if (err.response?.status === 403) {
      downloadStatus.value.error = '权限不足，无法导出记录';
    } else if (err.response?.status === 404) {
      downloadStatus.value.error = '练习记录不存在';
    } else if (err.response?.data && typeof err.response.data === 'string') {
      downloadStatus.value.error = err.response.data;
    } else {
      downloadStatus.value.error = err.message || '导出记录失败，请稍后再试';
    }
  } finally {
    downloadStatus.value.loading = false;
  }
};

// 下载报告（PDF 格式）
const downloadReport = async (exerciseId: string) => {
  try {
    downloadStatus.value.loading = true;
    downloadStatus.value.error = null;

    // 根据用户角色决定调用不同的下载方法
    if (userRole.value === 'student') {
      const response = await StudyRecordsApi.downloadStudentExerciseReport(exerciseId, 'pdf');
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      handleFileDownload(response.data, `练习报告_${exerciseId}_${timestamp}.pdf`);
    } else if (userRole.value === 'teacher' || userRole.value === 'assistant') {
      const record = records.value.find(r => r.exercise_id === exerciseId);
      if (!record?.student_name) {
        throw new Error('未找到学生信息');
      }
      
      const response = await StudyRecordsApi.exportStudentExerciseReport({
        studentId: record.student_name,
        exerciseId,
        format: 'pdf'
      });
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      handleFileDownload(response.data, `学生练习报告_${exerciseId}_${timestamp}.pdf`);
    }
  } catch (err: any) {
    console.error('下载报告失败:', err);
    
    // 处理不同类型的错误
    if (err.response?.status === 401) {
      downloadStatus.value.error = '未登录或登录已过期，请重新登录';
    } else if (err.response?.status === 403) {
      downloadStatus.value.error = '权限不足，无法下载报告';
    } else if (err.response?.status === 404) {
      downloadStatus.value.error = '练习报告不存在';
    } else if (err.response?.data && typeof err.response.data === 'string') {
      downloadStatus.value.error = err.response.data;
    } else {
      downloadStatus.value.error = err.message || '下载报告失败，请稍后再试';
    }
  } finally {
    downloadStatus.value.loading = false;
  }
};

// 导出全部学习记录（Excel 格式）
const exportAllRecords = async () => {
  try {
    downloadStatus.value.loading = true;
    downloadStatus.value.error = null;

    // 根据用户角色决定调用不同的导出方法
    if (userRole.value === 'student') {
      const response = await StudyRecordsApi.exportStudentStudyRecords({
        exerciseName: filters.value.exerciseName,
        status: filters.value.status
      });
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      handleFileDownload(response.data, `学习记录_${timestamp}.xlsx`);
    } else if (userRole.value === 'teacher' || userRole.value === 'assistant') {      const response = await StudyRecordsApi.exportTeacherStudyRecords({
        classId: currentClass.value?.classId || '1', // 使用动态获取的班级ID，如果没有则回退到默认值
        exerciseName: filters.value.exerciseName,
        studentName: filters.value.studentName,
        status: filters.value.status
      });
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      handleFileDownload(response.data, `全部学习记录_${timestamp}.xlsx`);
    }
  } catch (err: any) {
    console.error('导出全部记录失败:', err);
    
    // 处理不同类型的错误
    if (err.response?.status === 401) {
      downloadStatus.value.error = '未登录或登录已过期，请重新登录';
    } else if (err.response?.status === 403) {
      downloadStatus.value.error = '权限不足，无法导出记录';
    } else if (err.response?.status === 404) {
      downloadStatus.value.error = '学习记录不存在';
    } else if (err.response?.data && typeof err.response.data === 'string') {
      downloadStatus.value.error = err.response.data;
    } else {
      downloadStatus.value.error = err.message || '导出全部记录失败，请稍后再试';
    }
  } finally {
    downloadStatus.value.loading = false;
  }
};

// 监听筛选条件变化
watch([filters, currentPage], () => {
  fetchRecords()
})

// 组件挂载时获取数据
onMounted(async () => {
  // 首先获取用户班级信息（如果需要的话）
  if (userRole.value === 'teacher' || userRole.value === 'assistant') {
    await fetchUserClass()
  }
  // 然后获取学习记录
  fetchRecords()
})

</script>

<style scoped>
/* CSS 变量定义 */
:root {
  --bg-primary: #ffffff;
  --bg-secondary: #f8f9fa;
  --text-primary: #333333;
  --text-secondary: #666666;
  --primary: #0055a2;
  --primary-light: #e1f5fe;
  --border-color: #e0e0e0;
  --success: #67c23a;
  --warning: #e6a23c;
  --danger: #f56c6c;
  --info: #409eff;
}

.learning-records-container {
  padding: 2rem;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 60px);
  font-family: 'Microsoft YaHei', Arial, sans-serif;
}

/* 页面头部 */
.page-header {
  background: white;
  padding: 2rem;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  margin-bottom: 2rem;
  border-left: 4px solid var(--primary);
}

.page-header h1 {
  color: var(--primary);
  margin: 0 0 0.5rem 0;
  font-size: 2rem;
  font-weight: 600;
}

.page-header p {
  color: var(--text-secondary);
  margin: 0;
  font-size: 1rem;
  line-height: 1.6;
}

/* 班级信息样式 */
.class-info {
  margin-top: 1rem;
}

.class-badge {
  display: inline-block;
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #1976d2;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  border: 1px solid #90caf9;
}

.class-error {
  margin-top: 1rem;
}

.error-text {
  display: inline-block;
  background: linear-gradient(135deg, #ffebee 0%, #ffcdd2 100%);
  color: #d32f2f;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: 500;
  border: 1px solid #ef9a9a;
}

/* 筛选区域 */
.filter-section {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  margin-bottom: 2rem;
}

.filter-controls {
  display: flex;
  gap: 1rem;
  align-items: center;
  flex-wrap: wrap;
}

.filter-input,
.filter-select {
  padding: 0.75rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  min-width: 180px;
}

.filter-input:focus,
.filter-select:focus {
  outline: none;
  border-color: var(--primary);
  box-shadow: 0 0 0 3px rgba(0, 85, 162, 0.1);
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary) 0%, #003c72 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 85, 162, 0.3);
}

/* 记录列表区域 */
.records-list-section {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  overflow: hidden;
  position: relative;
}

/* 课程徽章 */
.course-badge-triangle-outer {
  position: absolute;
  top: 0;
  right: 0;
  width: 0;
  height: 0;
  border-style: solid;
  border-width: 0 60px 60px 0;
  border-color: transparent var(--primary) transparent transparent;
  z-index: 2;
}

.course-badge-triangle {
  position: absolute;
  top: -55px;
  right: -55px;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  transform: rotate(45deg);
}

.course-badge-text {
  color: white;
  font-size: 0.8rem;
  font-weight: bold;
  transform: rotate(-45deg);
  white-space: nowrap;
}

/* 加载和状态 */
.loading-indicator {
  text-align: center;
  padding: 3rem;
  color: var(--text-secondary);
  font-size: 1.1rem;
}

.error-message {
  text-align: center;
  padding: 3rem;
  color: var(--danger);
  font-size: 1.1rem;
  background-color: #fef0f0;
  margin: 1rem;
  border-radius: 8px;
}

.empty-state {
  text-align: center;
  padding: 4rem 2rem;
  color: var(--text-secondary);
}

.empty-state p {
  font-size: 1.2rem;
  margin: 0;
}

/* 表格样式 */
.records-table {
  width: 100%;
  border-collapse: collapse;
  background: white;
}

.records-table th {
  background: linear-gradient(135deg, var(--bg-secondary) 0%, #e9ecef 100%);
  padding: 1.2rem;
  text-align: left;
  font-weight: 600;
  color: var(--text-primary);
  border-bottom: 2px solid var(--border-color);
  font-size: 0.9rem;
}

.records-table td {
  padding: 1.2rem;
  border-bottom: 1px solid #f0f0f0;
  vertical-align: middle;
}

.records-table tbody tr {
  transition: all 0.3s ease;
}

.records-table tbody tr:hover {
  background-color: #f8f9fa;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

/* 警告提示样式 */
.alert {
  padding: 1rem 1.5rem;
  margin-bottom: 1.5rem;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  animation: slideDown 0.3s ease;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.alert-error {
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
  color: var(--danger);
  border: 1px solid #fbc4c4;
}

.alert-close {
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  font-size: 1.2rem;
  padding: 0.25rem;
  border-radius: 4px;
  transition: background-color 0.3s ease;
}

.alert-close:hover {
  background-color: rgba(0, 0, 0, 0.1);
}

/* 状态徽章样式 */
.status-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
}

.status-badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.status-completed {
  background: linear-gradient(135deg, #f0f9eb 0%, #d4edda 100%);
  color: var(--success);
  border: 1px solid #c2e7b0;
}

.status-progress {
  background: linear-gradient(135deg, #fdf6ec 0%, #fff3cd 100%);
  color: var(--warning);
  border: 1px solid #f5dab1;
}

.status-not-started {
  background: linear-gradient(135deg, #f4f4f5 0%, #e9ecef 100%);
  color: #909399;
  border: 1px solid #d3d4d6;
}

/* 操作按钮样式 */
.actions-cell {
  display: flex;
  gap: 0.5rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn-action {
  padding: 0.5rem 1rem;
  border-radius: 6px;
  font-size: 0.8rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  border: none;
  min-width: 60px;
  text-align: center;
}

.btn-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-action:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-exercise {
  background: linear-gradient(135deg, var(--info) 0%, #66b1ff 100%);
  color: white;
}

.btn-view {
  background: linear-gradient(135deg, var(--success) 0%, #85ce61 100%);
  color: white;
}

.btn-correct {
  background: linear-gradient(135deg, var(--warning) 0%, #ebb563 100%);
  color: white;
}

.btn-export-blue {
  background: linear-gradient(135deg, #ecf5ff 0%, #d1ecf1 100%);
  color: var(--info);
  border: 1px solid var(--info);
}

.btn-export {
  background: linear-gradient(135deg, #f4f4f5 0%, #e9ecef 100%);
  color: #909399;
  border: 1px solid #d3d4d6;
}

.btn-export-all {
  background: linear-gradient(135deg, #f0f9eb 0%, #d4edda 100%);
  color: var(--success);
  border: 1px solid #c2e7b0;
  margin-left: 0.5rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .learning-records-container {
    padding: 1rem;
  }
  
  .page-header {
    padding: 1.5rem;
  }
  
  .page-header h1 {
    font-size: 1.5rem;
  }
  
  .filter-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-input,
  .filter-select {
    min-width: auto;
    width: 100%;
  }
  
  .records-table {
    font-size: 0.8rem;
  }
  
  .records-table th,
  .records-table td {
    padding: 0.8rem 0.5rem;
  }
  
  .actions-cell {
    flex-direction: column;
    gap: 0.25rem;
  }
  
  .btn-action {
    font-size: 0.7rem;
    padding: 0.4rem 0.8rem;
  }
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.records-list-section {
  animation: fadeInUp 0.6s ease;
}

.filter-section {
  animation: fadeInUp 0.4s ease;
}

.page-header {
  animation: fadeInUp 0.2s ease;
}
</style>
