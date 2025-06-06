<template>
  <div class="learning-records-container">
    <!-- 下载错误提示 -->
    <div v-if="downloadStatus.error" class="alert alert-error">
      {{ downloadStatus.error }}
      <button class="alert-close" @click="downloadStatus.error = null">&times;</button>
    </div>
    
    <header class="page-header">
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
    </header>
    
    <section class="filter-section">
      <div class="filter-controls">
        <input type="text" v-model="filters.exerciseName" placeholder="练习名称" class="filter-input">
        <select v-model="filters.courseId" class="filter-select" :disabled="coursesLoading">
          <option value="">所有课程</option>
          <option v-for="course in availableCourses" :key="course.id" :value="course.id">
            {{ course.name }}
          </option>
        </select>
        <select v-model="filters.status" class="filter-select">
          <option value="">所有完成状态</option>
          <option value="completed">已完成</option>
          <option value="in-progress">进行中</option>
          <option value="not-started">未开始</option>
        </select>
        <template v-if="userRole === 'teacher' || userRole === 'assistant'">
          <input type="text" v-model="filters.studentName" placeholder="学生姓名" class="filter-input">
        </template>
        <button @click="loadCoursesList" class="btn btn-primary">筛选</button>
      </div>
    </section>
    
    <section class="records-list-section">
      <div v-if="loading" class="loading-indicator">加载中...</div>
      <div v-else-if="error" class="error-message">{{ error }}</div>
      <div v-else-if="coursesList.length === 0" class="empty-state">
        <p>暂无课程记录。</p>
      </div>
      <div v-else class="courses-container">
        <!-- 课程列表 -->
        <div 
          v-for="course in coursesList" 
          :key="course.id" 
          class="course-item"
        >
          <div class="course-header" @click="toggleCourseExpand(course.id)">
            <div class="course-info">
              <i :class="['fa', expandedCourses.includes(course.id) ? 'fa-caret-down' : 'fa-caret-right']"></i>
              <span class="course-name">{{ course.name }}</span>
              <span class="course-exercise-count">({{ course.exerciseCount }}个练习)</span>
            </div>            <div class="course-actions">
              <button 
                @click.stop="exportCourseRecords(course.id)" 
                class="btn btn-export-course"
                :disabled="downloadStatus.loading"
              >
                <i class="fa fa-download"></i> 导出记录
              </button>
              <button 
                @click.stop="exportCoursePracticeRecords(course.id)" 
                class="btn btn-export-practice"
                :disabled="downloadStatus.loading"
              >
                <i class="fa fa-file-excel-o"></i> 导出课程练习记录
              </button>
            </div>
          </div>
          
          <!-- 展开的练习列表 -->
          <div v-if="expandedCourses.includes(course.id)" class="exercises-list">
            <table class="records-table">
              <thead>
                <tr>
                  <th>练习名称</th>
                  <th>完成状态</th>
                  <th>得分</th>
                  <th>操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="record in getCourseExercises(course.id)" :key="record.id">
                  <td>{{ record.title }}</td>
                  <td>
                    <div class="status-wrapper">
                      <span :class="['status-badge', getStatusStyle(record.status)]">
                        {{ getStatusText(record.status) }}
                      </span>
                      <span class="progress-indicator" :style="`width: ${record.percent || 0}%`"></span>
                    </div>
                  </td>
                  <td>{{ record.score !== undefined ? record.score : '-' }}</td>                  <td class="actions-cell actions-cell-center">
                    <button 
                      @click="viewSubmissionReport(record.submission_id, record)" 
                      class="btn btn-action btn-view"
                      :disabled="false"
                      title="查看练习记录详情"
                    >查看记录</button>
                    <button 
                      @click="downloadReport(record.id)" 
                      class="btn btn-action btn-export"
                      :disabled="downloadStatus.loading || !record.submission_id"
                      :title="!record.submission_id ? '暂无提交记录' : '导出报告'"
                    >{{ downloadStatus.loading ? '下载中...' : '导出报告' }}</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>    <!-- 练习记录详情模态框 -->
    <div v-if="submissionReportModal.visible" class="modal-overlay" @click="closeSubmissionReportModal">
      <div class="submission-report-modal" @click.stop>
        <div class="modal-header">
          <h3>练习记录详情</h3>
          <button class="modal-close" @click="closeSubmissionReportModal">&times;</button>
        </div>
        
        <div class="modal-body">
          <!-- 加载状态 -->
          <div v-if="submissionReportModal.loading" class="modal-loading">
            <div class="loading-spinner"></div>
            <p>正在加载练习记录...</p>
          </div>
            <!-- 错误状态 -->
          <div v-else-if="submissionReportModal.error" class="modal-error">
            <i class="fa fa-exclamation-triangle"></i>
            <p>{{ submissionReportModal.error }}</p>
            <button @click="closeSubmissionReportModal" class="btn btn-primary">关闭</button>
          </div>
          
          <!-- 数据为空状态 -->
          <div v-else-if="!submissionReportModal.data" class="modal-empty">
            <i class="fa fa-info-circle"></i>
            <p>暂无数据显示</p>
            <button @click="closeSubmissionReportModal" class="btn btn-primary">关闭</button>
          </div>
            <!-- 练习记录详情 -->
          <div v-else-if="submissionReportModal.data" class="submission-report-content">            <!-- 调试信息 -->
            <div v-if="true" class="debug-info" style="background: #f0f0f0; padding: 10px; margin-bottom: 20px; font-size: 12px; border: 1px solid #ccc;">
              <p><strong>调试信息:</strong></p>
              <p>数据存在: {{ !!submissionReportModal.data }}</p>
              <p>提交信息存在: {{ !!submissionReportModal.data?.submissionInfo }}</p>
              <p>题目数量: {{ submissionReportModal.data?.questions?.length || 0 }}</p>
              <p>排名: {{ submissionReportModal.data?.rank }}</p>
              <p>总学生数: {{ submissionReportModal.data?.totalStudents }}</p>
              <p>分数分布: {{ submissionReportModal.data?.scoreDistribution?.length || 0 }}项</p>
            </div><!-- 基本信息 -->
            <div class="report-section">
              <h4><i class="fa fa-info-circle"></i> 基本信息</h4>
              <div class="info-grid">
                <div class="info-item">
                  <label>练习名称:</label>
                  <span>{{ submissionReportModal.data.submissionInfo?.practiceTitle || '未知练习' }}</span>
                </div>
                <div class="info-item">
                  <label>课程名称:</label>
                  <span>{{ submissionReportModal.data.submissionInfo?.courseName || '未知课程' }}</span>
                </div>
                <div class="info-item">
                  <label>班级:</label>
                  <span>{{ submissionReportModal.data.submissionInfo?.className || '未知班级' }}</span>
                </div>
                <div class="info-item">
                  <label>提交时间:</label>
                  <span>{{ submissionReportModal.data.submissionInfo?.submittedAt ? new Date(submissionReportModal.data.submissionInfo.submittedAt).toLocaleString() : '未知时间' }}</span>
                </div>
              </div>
            </div>
              <!-- 成绩信息 -->
            <div class="report-section">
              <h4><i class="fa fa-chart-bar"></i> 成绩统计</h4>
              <div class="score-overview">
                <div class="score-card score-main">
                  <div class="score-label">总分</div>
                  <div class="score-value">{{ submissionReportModal.data.submissionInfo?.score || 0 }}分</div>
                  <div class="score-percentage">已获得分数</div>
                </div>
                <div class="score-card">
                  <div class="score-label">班级排名</div>
                  <div class="score-value">{{ submissionReportModal.data.rank || '未知' }}/{{ submissionReportModal.data.totalStudents || '未知' }}</div>
                  <div class="score-percentage">超过{{ Math.round(submissionReportModal.data.percentile || 0) }}%同学</div>
                </div>
              </div>
            </div>
              <!-- 分数分布 -->
            <div class="report-section" v-if="submissionReportModal.data.scoreDistribution && submissionReportModal.data.scoreDistribution.length > 0">
              <h4><i class="fa fa-chart-pie"></i> 班级分数分布</h4>
              <div class="score-distribution">
                <div 
                  v-for="item in submissionReportModal.data.scoreDistribution" 
                  :key="item.score_range"
                  class="distribution-item"
                  :class="getDistributionClass(item.score_range)"
                >
                  <span class="grade-label">{{ item.score_range }}</span>
                  <span class="grade-count">{{ item.count }}人 ({{ item.percentage }}%)</span>
                </div>
              </div>
            </div>
              <!-- 题目详情 -->
            <div class="report-section" v-if="submissionReportModal.data.questions && submissionReportModal.data.questions.length > 0">
              <h4><i class="fa fa-list-alt"></i> 题目详情</h4>
              <div class="questions-list">
                <div 
                  v-for="(question, index) in submissionReportModal.data.questions" 
                  :key="question.id"
                  class="question-item"
                  :class="{ 'correct': question.isCorrect, 'incorrect': !question.isCorrect }"
                >
                  <div class="question-header">
                    <span class="question-number">第{{ index + 1 }}题</span>
                    <span class="question-type">{{ question.type }}</span>
                    <span class="question-score" :class="{ 'full-score': question.isCorrect }">
                      {{ question.score || 0 }}分
                    </span>
                  </div>
                  
                  <div class="question-content">
                    <p><strong>题目:</strong> {{ question.content }}</p>
                    <p v-if="question.options"><strong>选项:</strong> {{ question.options }}</p>
                    <p><strong>我的答案:</strong> <span :class="question.isCorrect ? 'correct-answer' : 'wrong-answer'">{{ question.studentAnswer || '未作答' }}</span></p>
                    <p><strong>正确答案:</strong> <span class="correct-answer">{{ question.correctAnswer }}</span></p>
                    <div v-if="question.analysis" class="question-analysis">
                      <strong>题目解析:</strong>
                      <p>{{ question.analysis }}</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>        <div class="modal-footer">
          <button @click="closeSubmissionReportModal" class="btn btn-secondary">关闭</button>
          <button v-if="submissionReportModal.data && submissionReportModal.data.submissionInfo" @click="downloadReport(String(submissionReportModal.data.submissionInfo.id))" class="btn btn-primary">
            <i class="fa fa-download"></i> 导出报告
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue';
import { useAuthStore } from '../../stores/auth';
import StudyRecordsApi from '../../api/studyRecords';
import type { SubmissionReport } from '../../api/studyRecords';
import CourseApi from '../../api/course';

// 使用认证状态管理
const authStore = useAuthStore();

// 用户角色
const userRole = computed(() => authStore.userRole);

// 响应式数据
const records = ref<RecordDisplay[]>([]);
const loading = ref(false)
const error = ref<string | null>(null)
const totalRecords = ref(0)
const currentPage = ref(1)

// 过滤器数据
const coursesList = ref<CourseItem[]>([]); // 课程列表
const coursesLoading = ref(false)
const expandedCourses = ref<number[]>([]); // 展开的课程ID列表
const courseExercises = ref<Record<number, RecordDisplay[]>>({}); // 每个课程的练习列表
const availableCourses = ref<any[]>([]); // 所有可用课程列表

// 导出和下载相关的状态
const downloadStatus = ref<{
  loading: boolean;
  error: string | null;
}>({
  loading: false,
  error: null
});

// 班级加载状态
const classLoading = ref(false);

// 练习记录详情弹窗相关数据
const submissionReportModal = ref({
  visible: false,
  loading: false,
  error: null as string | null,
  data: null as SubmissionReport | null
});

const filters = ref({
  exerciseName: '',
  courseId: '', // 数据库是数字，但表单使用字符串
  status: '' as RecordDisplay['status'] | '',
  studentName: '' // 仅教师/助教可见
})

// 班级数据（仅教师/助教可见）
const currentClass = ref<{
  classId: string;
  className: string;
  courseName: string;
} | null>(null)
const classError = ref<string | null>(null)

// 定义记录展示类型
interface RecordDisplay {
  id: string; // 练习ID
  submission_id?: string; // 提交ID
  status: 'completed' | 'in-progress' | 'not-started';
  title: string;
  courseName: string;
  courseId: number;
  percent?: number;
  score?: number;
  student_name?: string;
  type?: string;
  completedAt?: string;
}

// 课程项目类型
interface CourseItem {
  id: number;
  name: string;
  code?: string;
  exerciseCount: number;
}

// 获取所有课程
const fetchCourses = async () => {
  try {
    coursesLoading.value = true;
    const response = await CourseApi.getUserCourses(authStore.user?.id.toString() || '');
    const coursesData = response.data && response.data.data ? response.data.data : response.data;
    
    if (!Array.isArray(coursesData)) {
      console.error('获取课程列表失败：数据格式不正确', coursesData);
      return [];
    }
    
    const courses = coursesData.map(course => ({
      id: course.id,
      name: course.name,
      code: course.code
    }));
    
    // 保存所有可用课程
    availableCourses.value = courses;
    
    return courses;
  } catch (err: any) {
    console.error('获取课程列表失败:', err);
    error.value = err.response?.data?.message || err.message || '获取课程列表失败，请稍后再试';
    return [];
  } finally {
    coursesLoading.value = false;
  }
}

// 主要方法：加载课程列表和相关练习
const loadCoursesList = async () => {
  try {
    loading.value = true;
    error.value = null;
    
    // 获取课程
    const courses = await fetchCourses();
    console.log('获取到的课程列表:', courses);
    
    // 获取所有记录
    await fetchRecords();
    console.log('获取到的记录列表:', records.value);
    
    // 整理课程数据，并统计每个课程的练习数量
    const coursesWithExercises: Record<number, CourseItem> = {};
    const exercisesByCourse: Record<number, RecordDisplay[]> = {};
    
    // 首先初始化所有课程
    courses.forEach(course => {
      coursesWithExercises[course.id] = {
        ...course,
        exerciseCount: 0
      };
      exercisesByCourse[course.id] = [];
    });
    
    // 处理记录
    records.value.forEach(record => {
      console.log('处理记录:', record.title, '课程ID:', record.courseId, '课程名称:', record.courseName);
      
      // 如果记录没有courseId，尝试通过courseName匹配
      if (!record.courseId && record.courseName) {
        const matchedCourse = courses.find(c => c.name === record.courseName);
        if (matchedCourse) {
          record.courseId = matchedCourse.id;
        } else {
          // 如果没有找到匹配的课程，创建一个新的课程
          const newCourseId = Object.keys(coursesWithExercises).length + 1;
          coursesWithExercises[newCourseId] = {
            id: newCourseId,
            name: record.courseName,
            exerciseCount: 0
          };
          exercisesByCourse[newCourseId] = [];
          record.courseId = newCourseId;
        }
      }
      
      if (record.courseId) {
        // 添加到对应课程的练习列表
        if (!exercisesByCourse[record.courseId]) {
          exercisesByCourse[record.courseId] = [];
        }
        exercisesByCourse[record.courseId].push(record);
        
        // 更新课程信息
        if (!coursesWithExercises[record.courseId]) {
          coursesWithExercises[record.courseId] = {
            id: record.courseId,
            name: record.courseName || `课程 ${record.courseId}`,
            exerciseCount: 1
          };
        } else {
          coursesWithExercises[record.courseId].exerciseCount++;
        }
      }
    });
    
    // 保存课程练习数据
    courseExercises.value = exercisesByCourse;
    
    // 转换为数组并根据筛选条件过滤
    let filteredCourses = Object.values(coursesWithExercises);
    
    // 根据课程ID筛选
    if (filters.value.courseId) {
      const selectedCourseId = parseInt(filters.value.courseId);
      filteredCourses = filteredCourses.filter(course => course.id === selectedCourseId);
    }
    
    // 根据练习名称筛选
    if (filters.value.exerciseName.trim()) {
      const searchTerm = filters.value.exerciseName.trim().toLowerCase();
      // 过滤每个课程的练习
      Object.keys(exercisesByCourse).forEach(courseId => {
        const courseIdNum = parseInt(courseId);
        exercisesByCourse[courseIdNum] = exercisesByCourse[courseIdNum].filter((record: RecordDisplay) => 
          record.title.toLowerCase().includes(searchTerm)
        );
      });
      // 更新课程练习数量
      filteredCourses.forEach(course => {
        course.exerciseCount = exercisesByCourse[course.id]?.length || 0;
      });
      // 移除没有练习的课程
      filteredCourses = filteredCourses.filter(course => course.exerciseCount > 0);
    } else {
      // 当没有搜索条件时，显示所有记录
      Object.keys(exercisesByCourse).forEach(courseId => {
        const courseIdNum = parseInt(courseId);
        // 保持原有记录不变
        exercisesByCourse[courseIdNum] = exercisesByCourse[courseIdNum] || [];
      });
      // 更新课程练习数量
      filteredCourses.forEach(course => {
        course.exerciseCount = exercisesByCourse[course.id]?.length || 0;
      });
      // 显示所有课程，即使没有练习记录
      filteredCourses = courses.map(course => ({
        ...course,
        exerciseCount: exercisesByCourse[course.id]?.length || 0
      }));
    }
    
    // 根据状态筛选
    if (filters.value.status) {
      // 过滤每个课程的练习
      Object.keys(exercisesByCourse).forEach(courseId => {
        const courseIdNum = parseInt(courseId);
        exercisesByCourse[courseIdNum] = exercisesByCourse[courseIdNum].filter((record: RecordDisplay) => {
          // 如果是练习记录，默认都是已完成的
          if (record.type === 'practice') {
            return filters.value.status === 'completed';
          }
          // 如果是学习记录，根据实际状态判断
          return record.status === filters.value.status;
        });
      });
      // 更新课程练习数量
      filteredCourses.forEach(course => {
        course.exerciseCount = exercisesByCourse[course.id]?.length || 0;
      });
      // 移除没有练习的课程
      filteredCourses = filteredCourses.filter(course => course.exerciseCount > 0);
    } else {
      // 当选择"所有完成状态"时，显示所有记录
      Object.keys(exercisesByCourse).forEach(courseId => {
        const courseIdNum = parseInt(courseId);
        // 保持原有记录不变
        exercisesByCourse[courseIdNum] = exercisesByCourse[courseIdNum] || [];
      });
      // 更新课程练习数量
      filteredCourses.forEach(course => {
        course.exerciseCount = exercisesByCourse[course.id]?.length || 0;
      });
      // 显示所有课程，即使没有练习记录
      filteredCourses = courses.map(course => ({
        ...course,
        exerciseCount: exercisesByCourse[course.id]?.length || 0
      }));
    }
    
    // 根据学生姓名筛选（仅教师/助教可见）
    if (filters.value.studentName.trim() && (userRole.value === 'teacher' || userRole.value === 'assistant')) {
      const searchTerm = filters.value.studentName.trim().toLowerCase();
      // 过滤每个课程的练习
      Object.keys(exercisesByCourse).forEach(courseId => {
        const courseIdNum = parseInt(courseId);
        exercisesByCourse[courseIdNum] = exercisesByCourse[courseIdNum].filter((record: RecordDisplay) => 
          record.student_name?.toLowerCase().includes(searchTerm)
        );
      });
      // 更新课程练习数量
      filteredCourses.forEach(course => {
        course.exerciseCount = exercisesByCourse[course.id]?.length || 0;
      });
      // 移除没有练习的课程
      filteredCourses = filteredCourses.filter(course => course.exerciseCount > 0);
    }
    
    // 如果没有应用任何筛选条件，显示所有课程
    if (!filters.value.courseId && !filters.value.exerciseName.trim() && !filters.value.status && !filters.value.studentName.trim()) {
      filteredCourses = courses.map(course => ({
        ...course,
        exerciseCount: exercisesByCourse[course.id]?.length || 0
      }));
    }
    
    coursesList.value = filteredCourses;
    console.log('最终生成的课程列表:', coursesList.value);
    
  } catch (err: any) {
    console.error('获取课程和练习列表失败:', err);
    error.value = err.response?.data?.message || err.message || '获取数据失败，请稍后再试';
  } finally {
    loading.value = false;
  }
}

// 展开/折叠课程条目
const toggleCourseExpand = (courseId: number) => {
  const index = expandedCourses.value.indexOf(courseId);
  if (index > -1) {
    // 已展开，则折叠
    expandedCourses.value.splice(index, 1);
  } else {
    // 未展开，则展开
    expandedCourses.value.push(courseId);
  }
}

// 获取指定课程的练习列表
const getCourseExercises = (courseId: number): RecordDisplay[] => {
  return courseExercises.value[courseId] || [];
}

// 导出课程所有记录
const exportCourseRecords = async (courseId: number) => {
  try {
    downloadStatus.value.loading = true;
    downloadStatus.value.error = null;

    // 直接调用课程学习记录导出API，获取Excel文件
    const response = await StudyRecordsApi.exportCourseStudyRecords(courseId.toString());
    console.log('导出课程记录响应:', response);
    
    // 检查响应数据
    if (!response) {
      throw new Error('服务器返回的数据为空');
    }

    // 确保response是Blob类型
    if (!(response instanceof Blob)) {
      console.error('响应数据不是Blob类型:', response);
      throw new Error('服务器返回的数据格式不正确');
    }

    console.log('Blob数据:', response);
    console.log('Blob类型:', response.type);
    console.log('Blob大小:', response.size);
    
    if (response.size > 0) {
      console.log('开始创建下载链接');
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      const courseName = coursesList.value.find(c => c.id === courseId)?.name || '课程';
      const filename = `${courseName}_学习记录_${timestamp}.xlsx`;
      
      // 创建下载链接
      const url = window.URL.createObjectURL(response);
      console.log('创建的对象URL:', url);
      
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      console.log('触发下载');
      link.click();
      
      // 清理
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      console.log(`成功导出课程学习记录: ${filename}`);
    } else {
      console.error('Blob数据检查失败:', {
        hasBlob: !!response,
        isBlob: response instanceof Blob,
        size: response.size,
        type: response.type
      });
      throw new Error('服务器返回的数据为空');
    }
    
  } catch (err: any) {
    console.error('导出课程记录失败:', err);
    handleExportError(err);
  } finally {
    downloadStatus.value.loading = false;
  }
}

// 导出课程练习记录
const exportCoursePracticeRecords = async (courseId: number) => {
  try {
    downloadStatus.value.loading = true;
    downloadStatus.value.error = null;

    const response = await StudyRecordsApi.exportPracticeRecordsByCourse(courseId.toString());
    console.log('导出课程练习记录响应:', response);
    
    // 检查响应数据
    if (!response) {
      throw new Error('服务器返回的数据为空');
    }

    // 确保response是Blob类型
    if (!(response instanceof Blob)) {
      console.error('响应数据不是Blob类型:', response);
      throw new Error('服务器返回的数据格式不正确');
    }

    console.log('Blob数据:', response);
    console.log('Blob类型:', response.type);
    console.log('Blob大小:', response.size);
    
    if (response.size > 0) {
      console.log('开始创建下载链接');
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      const courseName = coursesList.value.find(c => c.id === courseId)?.name || '课程';
      const filename = `${courseName}_练习记录_${timestamp}.xlsx`;
      
      // 创建下载链接
      const url = window.URL.createObjectURL(response);
      console.log('创建的对象URL:', url);
      
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      console.log('触发下载');
      link.click();
      
      // 清理
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      console.log(`成功导出课程练习记录: ${filename}`);
    } else {
      console.error('Blob数据检查失败:', {
        hasBlob: !!response,
        isBlob: response instanceof Blob,
        size: response.size,
        type: response.type
      });
      throw new Error('服务器返回的数据为空');
    }
    
  } catch (err: any) {
    console.error('导出课程练习记录失败:', err);
    handleExportError(err);
  } finally {
    downloadStatus.value.loading = false;
  }
}

// 处理导出错误
const handleExportError = (err: any) => {
  if (err.response?.status === 401) {
    downloadStatus.value.error = '未登录或登录已过期，请重新登录';
  } else if (err.response?.status === 403) {
    downloadStatus.value.error = '权限不足，无法导出记录';
  } else if (err.response?.status === 404) {
    downloadStatus.value.error = '记录不存在';
  } else if (err.response?.data && typeof err.response.data === 'string') {
    try {
      const errorData = JSON.parse(err.response.data);
      downloadStatus.value.error = errorData.message || '导出失败，请稍后再试';
    } catch {
      downloadStatus.value.error = err.response.data || '导出失败，请稍后再试';
    }
  } else {
    downloadStatus.value.error = err.message || '导出失败，请稍后再试';
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
      if (response.data.code === 200) {
        reportData = response.data.data;
      } else {
        reportData = response.data;
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
    submissionReportModal.value.error = err.response?.data?.message || err.message || '获取记录失败，请稍后再试';
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

// 获取分数分布项的样式类
const getDistributionClass = (scoreRange: string) => {
  if (!scoreRange) return '';
  
  // 根据分数段返回对应的样式类
  if (scoreRange.includes('90') || scoreRange.includes('100')) {
    return 'excellent';
  } else if (scoreRange.includes('80') || scoreRange.includes('89')) {
    return 'good';
  } else if (scoreRange.includes('70') || scoreRange.includes('79')) {
    return 'average';
  } else if (scoreRange.includes('60') || scoreRange.includes('69')) {
    return 'poor';
  } else {
    return 'fail';
  }
};

// 处理文件下载 - 支持从响应头或参数获取文件名
const handleFileDownload = async (blob: Blob, filename?: string, response?: any) => {
  try {
    // 检查是否为错误响应（通过更精确的判断）
    // 如果是错误响应，通常 blob 会很小且内容为 JSON
    if (blob.type === 'application/json' && blob.size < 10000) {
      try {
        // 尝试读取并解析为 JSON
        const text = await blob.text();
        const errorData = JSON.parse(text);
        downloadStatus.value.error = errorData.message || '下载失败，请稍后再试';
        return;
      } catch (e) {
        // 如果无法解析为 JSON，可能是正常文件，继续下载
        console.log('无法解析为JSON，继续文件下载');
      }
    }

    // 如果文件大小为0或过小，可能是错误响应
    if (blob.size === 0) {
      downloadStatus.value.error = '文件内容为空，下载失败';
      return;
    }

    // 尝试从响应头获取文件名
    let downloadFilename = filename;
    if (!downloadFilename && response?.headers) {
      const contentDisposition = response.headers['content-disposition'] || response.headers['Content-Disposition'];
      if (contentDisposition) {
        // 解析 Content-Disposition 头获取文件名
        const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"']).*?\2|[^;\n]*)/);
        if (filenameMatch) {
          downloadFilename = filenameMatch[1].replace(/['"]/g, '');
          // URL解码文件名
          try {
            if (downloadFilename) {
              downloadFilename = decodeURIComponent(downloadFilename);
            }
          } catch (e) {
            console.log('文件名解码失败，使用原始文件名');
          }
        }
      }
    }

    // 如果仍然没有文件名，使用默认文件名
    if (!downloadFilename) {
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      downloadFilename = `下载文件_${timestamp}`;
      
      // 根据blob类型推断文件扩展名
      if (blob.type.includes('pdf')) {
        downloadFilename += '.pdf';
      } else if (blob.type.includes('zip')) {
        downloadFilename += '.zip';
      } else if (blob.type.includes('excel') || blob.type.includes('spreadsheet')) {
        downloadFilename += '.xlsx';
      } else if (blob.type.includes('word') || blob.type.includes('document')) {
        downloadFilename += '.docx';
      }
    }

    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', downloadFilename);
    document.body.appendChild(link);
    link.click();
    
    // 安全地移除DOM元素和释放URL对象
    if (link.parentNode) {
      document.body.removeChild(link);
    }
    window.URL.revokeObjectURL(url);
    
    // 显示成功消息
    downloadStatus.value.error = null;
    console.log(`文件下载成功: ${downloadFilename}, 大小: ${blob.size} 字节`);
  } catch (error) {
    console.error('文件下载失败:', error);
    downloadStatus.value.error = '文件下载失败，请稍后再试';
  }
};

// 下载报告（PDF 格式）- 使用提交ID导出
const downloadReport = async (recordIdOrSubmissionId: string) => {
  try {
    downloadStatus.value.loading = true;
    downloadStatus.value.error = null;

    // 如果传入的是记录ID，需要查找对应的提交ID
    let submissionId = recordIdOrSubmissionId;
    
    // 检查是否是记录ID，如果是则查找对应的提交ID
    const record = records.value.find(r => r.id === recordIdOrSubmissionId);
    if (record && record.submission_id) {
      submissionId = record.submission_id;
    }

    if (!submissionId) {
      throw new Error('未找到提交记录，无法导出报告');
    }

    console.log('开始下载报告，submissionId:', submissionId);

    // 使用提交报告导出API
    const response = await StudyRecordsApi.exportSubmissionReport(submissionId);
    console.log('导出报告响应:', response);
    
    // 检查响应数据
    if (!response) {
      throw new Error('服务器返回的数据为空');
    }

    // 确保response是Blob类型
    if (!(response instanceof Blob)) {
      console.error('响应数据不是Blob类型:', response);
      throw new Error('服务器返回的数据格式不正确');
    }

    console.log('Blob数据:', response);
    console.log('Blob类型:', response.type);
    console.log('Blob大小:', response.size);
    
    if (response.size > 0) {
      console.log('开始创建下载链接');
      const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-');
      const filename = `提交报告_${submissionId}_${timestamp}.pdf`;
      
      // 创建下载链接
      const url = window.URL.createObjectURL(response);
      console.log('创建的对象URL:', url);
      
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      console.log('触发下载');
      link.click();
      
      // 清理
      document.body.removeChild(link);
      window.URL.revokeObjectURL(url);
      
      console.log(`成功导出提交报告: ${filename}`);
    } else {
      console.error('Blob数据检查失败:', {
        hasBlob: !!response,
        isBlob: response instanceof Blob,
        size: response.size,
        type: response.type
      });
      throw new Error('服务器返回的数据为空');
    }
    
  } catch (err: any) {
    console.error('下载报告失败:', err);
    
    // 处理不同类型的错误
    if (err.response?.status === 401) {
      downloadStatus.value.error = '未登录或登录已过期，请重新登录';
    } else if (err.response?.status === 403) {
      downloadStatus.value.error = '权限不足，无法下载报告';
    } else if (err.response?.status === 404) {
      downloadStatus.value.error = '提交记录不存在或报告未生成';
    } else {
      downloadStatus.value.error = err.message || '下载报告失败，请稍后再试';
    }
  } finally {
    downloadStatus.value.loading = false;
  }
};

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
  try {
    loading.value = true
    error.value = null
    
    const allRecords: RecordDisplay[] = [];
    
    // 根据是否选择了课程筛选来决定调用哪个API
    const courseId = filters.value.courseId;
    
    // 获取学习记录
    try {
      let studyResponse;
      if (courseId) {
        studyResponse = await StudyRecordsApi.getStudyRecordsByCourse(courseId);
      } else {
        studyResponse = await StudyRecordsApi.getAllStudyRecords();
      }
      
      console.log('学习记录API原始响应:', studyResponse);
      console.log('学习记录响应数据:', studyResponse.data);
      
      // 解析API响应数据
      let studyRecords = [];
      
      if (studyResponse.data && studyResponse.data.code === 200) {
        // 检查data是否是数组
        if (Array.isArray(studyResponse.data.data)) {
          studyRecords = studyResponse.data.data;
          console.log('成功解析学习记录数组:', studyRecords.length, '条');
        }
        // 检查data是否是单个对象
        else if (studyResponse.data.data && typeof studyResponse.data.data === 'object') {
          studyRecords = [studyResponse.data.data]; // 将单个对象包装成数组
          console.log('成功解析学习记录单个对象，转换为数组:', studyRecords.length, '条');
        }
        else {
          console.warn('学习记录API响应data格式不正确:', typeof studyResponse.data.data, studyResponse.data.data);
          studyRecords = [];
        }
        
        // 处理学习记录数据
        studyRecords.forEach((record: any) => {
          allRecords.push({
            id: record.id?.toString() || '',
            title: record.sectionTitle || '未知章节',
            courseName: record.courseName || '未知课程',
            courseId: record.courseId || 0,
            status: record.completed ? 'completed' : 'in-progress',
            score: undefined, // 学习记录没有分数
            completedAt: record.completedAt,
            type: 'study',
            percent: record.completed ? 100 : 50,
          });
        });
        
        console.log('处理后的学习记录数量:', allRecords.length);
      } else {
        console.warn('学习记录API响应格式不正确:', studyResponse.data);
      }
    } catch (studyErr: any) {
      console.error('获取学习记录失败:', studyErr);
      if (studyErr.response) {
        console.error('学习记录API错误响应:', studyErr.response.status, studyErr.response.data);
      }
    }
    
    // 获取练习记录
    try {
      let practiceResponse;
      if (courseId) {
        practiceResponse = await StudyRecordsApi.getPracticeRecordsByCourse(courseId);
      } else {
        practiceResponse = await StudyRecordsApi.getAllPracticeRecords();
      }
      
      console.log('练习记录API原始响应:', practiceResponse);
      console.log('练习记录响应数据:', practiceResponse.data);
      
      // 根据API文档解析练习记录
      let practiceRecords = [];
    
    // 检查响应数据
      if (practiceResponse.data) {
        // 如果响应是数组，直接使用
        if (Array.isArray(practiceResponse.data)) {
          practiceRecords = practiceResponse.data;
          console.log('成功解析练习记录数组:', practiceRecords.length, '条');
        }
        // 如果响应是对象且包含data字段
        else if (practiceResponse.data.code === 200 && Array.isArray(practiceResponse.data.data)) {
          practiceRecords = practiceResponse.data.data;
          console.log('成功解析练习记录数组(嵌套):', practiceRecords.length, '条');
        }
        // 如果响应是单个对象
        else if (typeof practiceResponse.data === 'object') {
          practiceRecords = [practiceResponse.data];
          console.log('成功解析练习记录单个对象:', practiceRecords.length, '条');
        }
        else {
          console.warn('练习记录API响应格式不正确:', typeof practiceResponse.data, practiceResponse.data);
          practiceRecords = [];
        }
        
        practiceRecords.forEach((record: any) => {
          // 从记录中提取课程ID
          const courseId = record.courseId || (record.questions && record.questions[0]?.courseId);
          
          allRecords.push({
            id: record.id?.toString() || record.practiceId?.toString() || '',
            title: record.practiceTitle || record.title || '未知练习',
            courseName: record.courseName || '未知课程',
            courseId: courseId || 0,
            status: 'completed', // 练习记录都是已完成的
            score: record.score,
            completedAt: record.submittedAt,
            type: 'practice',
            percent: 100, // 练习记录都是完成的
            submission_id: record.id?.toString() || '', // 确保将submission_id设置为记录ID
            student_name: record.className || record.studentName || '',
          });
        });
      } else {
        console.warn('练习记录API响应为空');
      }
    } catch (practiceErr: any) {
      console.error('获取练习记录失败:', practiceErr);
      if (practiceErr.response) {
        console.error('练习记录API错误响应:', practiceErr.response.status, practiceErr.response.data);
      }
    }
    
    console.log('已启用练习记录获取，显示学习记录和练习记录');
    
    // 应用其他筛选条件
    let filteredRecords = allRecords;
    
    // 按练习名称筛选
    if (filters.value.exerciseName.trim()) {
      const searchTerm = filters.value.exerciseName.trim().toLowerCase();
      filteredRecords = filteredRecords.filter(record => 
        record.title.toLowerCase().includes(searchTerm)
      );
    }
    
    // 按状态筛选
    if (filters.value.status) {
      filteredRecords = filteredRecords.filter(record => 
        record.status === filters.value.status
      );
    }
    
    // 按学生姓名筛选（仅教师/助教可见）
    if (filters.value.studentName.trim() && (userRole.value === 'teacher' || userRole.value === 'assistant')) {
      const searchTerm = filters.value.studentName.trim().toLowerCase();
      filteredRecords = filteredRecords.filter(record => 
        record.student_name?.toLowerCase().includes(searchTerm)
      );
    }
    
    records.value = filteredRecords;
    totalRecords.value = filteredRecords.length;
    
    console.log('获取学习记录成功:', records.value.length, '条记录');
    
  } catch (err: any) {
    console.error('获取学习记录失败:', err);
    error.value = err.response?.data?.message || err.message || '获取学习记录失败，请稍后再试';
    records.value = [];
    totalRecords.value = 0;
  } finally {
    loading.value = false;
  }
}

const getStatusText = (status: RecordDisplay['status']) => {
  switch (status) {
    case 'completed': return '已完成'
    case 'in-progress': return '进行中'
    case 'not-started': return '未开始'
    default: return '未知'
  }
}

const getStatusStyle = (status: RecordDisplay['status']) => {
  switch (status) {
    case 'completed': return 'status-completed'
    case 'in-progress': return 'status-progress'
    case 'not-started': return 'status-not-started'
    default: return ''
  }
}

// 监听筛选条件变化
watch([filters, currentPage], () => {
  loadCoursesList()
})

// 监听课程筛选变化，自动重新获取数据
watch(() => filters.value.courseId, () => {
  if (currentPage.value === 1) {
    loadCoursesList()
  } else {
    currentPage.value = 1
    loadCoursesList()
  }
})

// 组件挂载时获取数据
onMounted(async () => {
  // 首先获取用户班级信息（如果需要的话）
  if (userRole.value === 'teacher' || userRole.value === 'assistant') {
    await fetchUserClass()
  }
  // 获取课程列表
  await fetchCourses()
  // 然后获取学习记录
  await loadCoursesList()
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

.course-item {
  animation: fadeInUp 0.4s ease;
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

/* 课程列表样式 */
.courses-container {
  padding: 1.5rem;
}

.course-item {
  margin-bottom: 1.5rem;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
  background: white;
}

.course-item:hover {
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.course-header {
  padding: 1.2rem 1.5rem;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s ease;
}

.course-header:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
}

.course-info {
  display: flex;
  align-items: center;
  gap: 0.8rem;
}

.course-info i {
  font-size: 1.2rem;
  color: var(--primary);
  transition: transform 0.3s ease;
}

.course-name {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-primary);
}

.course-exercise-count {
  font-size: 0.9rem;
  color: var(--text-secondary);
}

.course-actions {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

/* 在小屏幕上调整按钮布局 */
@media (max-width: 768px) {
  .course-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .course-actions {
    justify-content: center;
  }
  
  .btn-export-course,
  .btn-export-practice {
    flex: 1;
    min-width: 140px;
    justify-content: center;
  }
}

.btn-export-course {
  background: linear-gradient(135deg, var(--primary) 0%, #003c72 100%);
  color: white;
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-export-course:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 85, 162, 0.3);
}

.btn-export-course:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.btn-export-practice {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  color: white;
  padding: 0.6rem 1.2rem;
  border: none;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.btn-export-practice:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(40, 167, 69, 0.3);
}

.btn-export-practice:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.exercises-list {
  border-top: 1px solid var(--border-color);
  animation: slideDown 0.3s ease;
}

.exercises-list .records-table {
  margin: 0;
}

.exercises-list .records-table th {
  background: linear-gradient(135deg, #f1f3f4 0%, #e8eaed 100%);
  font-size: 0.85rem;
}

.exercises-list .records-table td {
  font-size: 0.9rem;
}

.progress-indicator {
  height: 4px;
  background: linear-gradient(135deg, var(--primary) 0%, #66b1ff 100%);
  border-radius: 2px;
  transition: width 0.3s ease;
}

/* 练习记录详情弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.submission-report-modal {
  background: white;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  max-width: 900px;
  width: 90%;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  padding: 1.5rem 2rem;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, #e9ecef 100%);
  border-radius: 16px 16px 0 0;
}

.modal-header h3 {
  margin: 0;
  color: var(--primary);
  font-size: 1.4rem;
  font-weight: 600;
}

.modal-close {
  background: none;
  border: none;
  font-size: 1.8rem;
  color: var(--text-secondary);
  cursor: pointer;
  padding: 0.25rem;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.modal-close:hover {
  background: rgba(0, 0, 0, 0.1);
  color: var(--danger);
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 0;
}

.modal-loading {
  text-align: center;
  padding: 3rem;
  color: var(--text-secondary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid var(--primary);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.modal-error {
  text-align: center;
  padding: 3rem;
  color: var(--danger);
}

.modal-error i {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.submission-report-content {
  padding: 0;
}

.report-section {
  padding: 1.5rem 2rem;
  border-bottom: 1px solid #f5f5f5;
}

.report-section:last-child {
  border-bottom: none;
}

.report-section h4 {
  margin: 0 0 1.5rem 0;
  color: var(--primary);
  font-size: 1.1rem;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.report-section h4 i {
  color: var(--primary);
}

.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem;
  background: var(--bg-secondary);
  border-radius: 8px;
  border-left: 3px solid var(--primary);
}

.info-item label {
  font-weight: 600;
  color: var(--text-secondary);
}

.info-item span {
  color: var(--text-primary);
  font-weight: 500;
}

.score-overview {
  display: flex;
  gap: 1.5rem;
  flex-wrap: wrap;
}

.score-card {
  flex: 1;
  min-width: 150px;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 12px;
  text-align: center;
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.score-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

.score-main {
  background: linear-gradient(135deg, var(--primary-light) 0%, #e1f5fe 100%);
  border-color: var(--primary);
}

.score-label {
  font-size: 0.9rem;
  color: var(--text-secondary);
  font-weight: 500;
  margin-bottom: 0.5rem;
}

.score-value {
  font-size: 1.8rem;
  font-weight: bold;
  color: var(--primary);
  margin-bottom: 0.25rem;
}

.score-percentage {
  font-size: 1rem;
  color: var(--text-secondary);
  font-weight: 500;
}

.score-distribution {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
}

.distribution-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  font-weight: 500;
}

.distribution-item.excellent {
  background: linear-gradient(135deg, #f0f9eb 0%, #d4edda 100%);
  color: var(--success);
  border: 1px solid #c2e7b0;
}

.distribution-item.good {
  background: linear-gradient(135deg, #e1f5fd 0%, #cce7ff 100%);
  color: var(--info);
  border: 1px solid #b3d9ff;
}

.distribution-item.average {
  background: linear-gradient(135deg, #fdf6ec 0%, #fff3cd 100%);
  color: var(--warning);
  border: 1px solid #f5dab1;
}

.distribution-item.poor {
  background: linear-gradient(135deg, #fff8e1 0%, #fff3c4 100%);
  color: #f57c00;
  border: 1px solid #ffcc80;
}

.distribution-item.fail {
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
  color: var(--danger);
  border: 1px solid #fbc4c4;
}

.grade-label {
  font-size: 0.9rem;
}

.grade-count {
  font-weight: bold;
  font-size: 1.1rem;
}

.questions-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.question-item {
  border: 1px solid var(--border-color);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.question-item:hover {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.question-item.correct {
  border-left: 4px solid var(--success);
}

.question-item.incorrect {
  border-left: 4px solid var(--danger);
}

.question-header {
  background: linear-gradient(135deg, var(--bg-secondary) 0%, #e9ecef 100%);
  padding: 1rem 1.5rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
}

.question-number {
  font-weight: bold;
  color: var(--primary);
}

.question-type {
  background: linear-gradient(135deg, var(--primary-light) 0%, #e1f5fe 100%);
  color: var(--primary);
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  border: 1px solid var(--primary);
}

.question-score {
  font-weight: bold;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
  color: var(--danger);
  border: 1px solid #fbc4c4;
}

.question-score.full-score {
  background: linear-gradient(135deg, #f0f9eb 0%, #d4edda 100%);
  color: var(--success);
  border: 1px solid #c2e7b0;
}

.question-content {
  padding: 1.5rem;
}

.question-content p {
  margin: 0 0 1rem 0;
  line-height: 1.6;
}

.question-content p:last-child {
  margin-bottom: 0;
}

.correct-answer {
  color: var(--success);
  font-weight: 600;
}

.wrong-answer {
  color: var(--danger);
  font-weight: 600;
}

.question-analysis {
  margin-top: 1rem;
  padding: 1rem;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 8px;
  border-left: 3px solid var(--info);
}

.question-analysis strong {
  color: var(--info);
}

.modal-footer {
  padding: 1.5rem 2rem;
  border-top: 1px solid var(--border-color);
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  background: linear-gradient(135deg, var(--bg-secondary) 0%, #e9ecef 100%);
  border-radius: 0 0 16px 16px;
}

.btn-secondary {
  background: linear-gradient(135deg, #f4f4f5 0%, #e9ecef 100%);
  color: var(--text-secondary);
  border: 1px solid var(--border-color);
}

.btn-secondary:hover {
  background: linear-gradient(135deg, #e9ecef 0%, #dee2e6 100%);
  color: var(--text-primary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .submission-report-modal {
    width: 95%;
    margin: 1rem;
  }
  
  .modal-header,
  .modal-footer {
    padding: 1rem;
  }
  
  .report-section {
    padding: 1rem;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .score-overview {
    flex-direction: column;
  }
  
  .score-distribution {
    grid-template-columns: 1fr;
  }
  
  .question-header {
    flex-direction: column;
    gap: 0.5rem;
    align-items: flex-start;
  }
  
  .modal-footer {
    flex-direction: column;
  }
}
</style>
