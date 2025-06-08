<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { StudyRecord } from '../../api/studyRecords'
import axios from '../../api/axios'
import { ElMessage } from 'element-plus'
import StudyRecordsApi from '../../api/studyRecords'
import CourseApi from '../../api/course'

const records = ref<StudyRecord[]>([])
const loading = ref(true)
const error = ref('')
const selectedCourse = ref<number | ''>('')
const downloadStatus = ref({
  loading: false,
  courseId: null as number | null
})

// 练习记录详情弹窗相关数据
const submissionReportModal = ref({
  visible: false,
  loading: false,
  error: null as string | null,
  data: null as any
})

// 获取所有课程列表
const courses = computed(() => {
  const courseSet = new Set<{id: number, name: string}>()
  records.value.forEach(record => {
    if (record.courseName && record.resourceId) {
      console.log('处理课程:', record.courseName, 'ID:', record.resourceId)
      courseSet.add({id: record.resourceId, name: record.courseName})
    }
  })
  console.log('课程列表:', Array.from(courseSet))
  return Array.from(courseSet)
})

// 根据筛选条件过滤记录
const filteredRecords = computed(() => {
  if (!selectedCourse.value) {
    return records.value
  }
  return records.value.filter(record => record.resourceId === selectedCourse.value)
})

const fetchRecords = async () => {
  loading.value = true
  error.value = ''
  try {
    const response = await axios.get('/api/record/study')
    // 兼容后端返回数组的情况
    if (Array.isArray(response.data)) {
      records.value = response.data
      console.log('获取到的学习记录:', records.value)
    } else if (response.data) {
      records.value = [response.data]
      console.log('获取到的学习记录:', records.value)
    } else {
      error.value = '获取学习记录失败'
    }
  } catch (err) {
    error.value = '获取学习记录失败，请稍后再试'
    console.error('获取学习记录错误:', err)
  } finally {
    loading.value = false
  }
}

// 格式化时间
const formatDateTime = (dateTimeStr: string) => {
  if (!dateTimeStr) return '-'
  const date = new Date(dateTimeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 导出课程学习记录
const exportCourseRecords = async (courseId: number) => {
  downloadStatus.value = {
    loading: true,
    courseId
  }
  
  try {
    const response = await StudyRecordsApi.exportCourseStudyRecords(courseId.toString())
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    const courseName = courses.value.find(c => c.id === courseId)?.name || '课程'
    link.setAttribute('download', `${courseName}学习记录.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    
    ElMessage.success('导出成功')
  } catch (err) {
    console.error('导出失败:', err)
    ElMessage.error('导出失败，请稍后重试')
  } finally {
    downloadStatus.value = {
      loading: false,
      courseId: null
    }
  }
}

// 查看练习提交报告
const viewSubmissionReport = async (recordIdOrSubmissionId: string | undefined, record?: any) => {
  if (!recordIdOrSubmissionId && !record) {
    console.error('缺少必要参数，无法查看练习记录');
    return;
  }

  try {
    submissionReportModal.value.visible = true;
    submissionReportModal.value.loading = true;
    submissionReportModal.value.error = null;
    submissionReportModal.value.data = null;

    let submissionId = recordIdOrSubmissionId;
    
    // 如果有submission_id，直接使用
    if (record && record.submission_id) {
      submissionId = record.submission_id;
    } else if (record && record.id) {
      // 如果没有submission_id但有record.id，尝试使用练习ID
      submissionId = record.id;
    }

    if (!submissionId) {
      throw new Error('无法获取有效的提交ID或练习ID');
    }

    console.log('尝试获取练习提交报告，ID:', submissionId);
    console.log('记录对象:', record);
    
    // 获取提交报告
    const response = await StudyRecordsApi.getSubmissionReport(submissionId);
    console.log('练习提交报告API响应:', response);
    
    if (response.data) {
      // 检查响应格式
      let reportData;
      if (response.data.code === 200 && response.data.data) {
        reportData = response.data.data;
      } else if (response.data.code === 200) {
        throw new Error('API返回的数据为空');
      } else {
        throw new Error(response.data.message || '获取练习提交报告失败');
      }
      
      console.log('处理后的报告数据:', reportData);
      
      if (!reportData) {
        throw new Error('API返回的数据为空');
      }
      
      // 设置报告数据
      submissionReportModal.value.data = reportData;
      console.log('成功设置练习提交报告数据:', submissionReportModal.value.data);
      
    } else {
      throw new Error('API响应数据格式不正确');
    }
    
  } catch (err: any) {
    console.error('获取练习提交报告失败:', err);
    submissionReportModal.value.error = err.message || '获取记录失败，请稍后再试';
  } finally {
    submissionReportModal.value.loading = false;
  }
};

// 关闭练习记录详情弹窗
const closeSubmissionReportModal = () => {
  submissionReportModal.value.visible = false;
  submissionReportModal.value.data = null;
  submissionReportModal.value.error = null;
};

onMounted(() => {
  fetchRecords()
})
</script>

<template>
  <div class="study-record-container">
    <header class="page-header">
      <h1>学习记录</h1>
      <div class="filter-section">
        <select v-model="selectedCourse" class="course-filter">
          <option value="">所有课程</option>
          <option v-for="course in courses" :key="course.id" :value="course.id">
            {{ course.name }}
          </option>
        </select>
        <button 
          v-if="selectedCourse"
          @click="exportCourseRecords(selectedCourse)" 
          class="btn btn-export-course"
          :disabled="downloadStatus.loading"
        >
          <i class="fa fa-download"></i> 导出记录
        </button>
      </div>
    </header>

    <div v-if="loading" class="loading-container">
      加载中...
    </div>
    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>
    <div v-else-if="records.length === 0" class="empty-state">
      暂无学习记录
    </div>
    <div v-else class="records-list">
      <div v-for="record in filteredRecords" :key="record.id" class="record-card">
        <div class="record-header">
          <h3>{{ record.courseName }}</h3>
          <span class="completion-status"
            :class="{
              completed: Number(record.progress) >= 100,
              'in-progress': Number(record.progress) > 0 && Number(record.progress) < 100,
              'not-started': Number(record.progress) === 0
            }"
          >
            {{ record.formattedProgress }}
          </span>
        </div>
        <div class="record-content">
          <p class="section-title">{{ record.sectionTitle }}</p>
          <p class="resource-title">资源：{{ record.resourceTitle }}</p>
          <div class="progress-bar-container">
            <div class="progress-bar" :style="{ width: record.formattedProgress }"></div>
          </div>
          <div class="watch-info">
            <span class="watch-count">观看次数：{{ record.watchCount }}</span>
            <span v-if="record.lastWatchTime" class="last-watch">
              最后观看：{{ formatDateTime(record.lastWatchTime) }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.study-record-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
}

.page-header {
  margin-bottom: 2rem;
}

.page-header h1 {
  color: #333;
  font-size: 2rem;
  margin: 0;
}

.filter-section {
  display: flex;
  align-items: center;
  margin-bottom: 1rem;
}

.course-filter {
  margin-right: 1rem;
}

.btn-export-course {
  padding: 0.5rem 1rem;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.btn-export-course:hover {
  background-color: #36a372;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 3rem;
  color: #666;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 1rem;
  border-radius: 8px;
  margin: 1rem 0;
}

.empty-state {
  text-align: center;
  padding: 3rem;
  color: #666;
  background: #f5f5f5;
  border-radius: 8px;
}

.records-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1.5rem;
}

.record-card {
  background: white;
  border-radius: 12px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.record-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.record-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.record-header h3 {
  margin: 0;
  color: #333;
  font-size: 1.25rem;
}

.completion-status {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.875rem;
  font-weight: 500;
  background-color: #fff3e0;
  color: #f57c00;
}
.completion-status.completed {
  background-color: #e8f5e9;
  color: #2e7d32;
}
.completion-status.in-progress {
  background-color: #fffde7;
  color: #fbc02d;
}
.completion-status.not-started {
  background-color: #eeeeee;
  color: #888;
}

.record-content {
  color: #666;
}

.section-title {
  margin: 0 0 0.5rem;
  font-weight: 500;
}
.resource-title {
  margin: 0 0 0.5rem;
  font-size: 0.95rem;
  color: #555;
}
.progress-bar-container {
  background: #f0f0f0;
  border-radius: 6px;
  height: 8px;
  margin: 0.5rem 0 1rem;
  width: 100%;
}
.progress-bar {
  height: 100%;
  border-radius: 6px;
  background: linear-gradient(90deg, #42b983, #2e7d32);
  transition: width 0.4s;
}
.watch-info {
  margin: 0.5rem 0 0;
  font-size: 0.875rem;
  color: #888;
}
.watch-count {
  margin-right: 1.5rem;
}
.last-watch {
  color: #888;
}

@media (max-width: 768px) {
  .study-record-container {
    padding: 1rem;
  }
  .records-list {
    grid-template-columns: 1fr;
  }
  .page-header h1 {
    font-size: 1.5rem;
  }
}
</style>
