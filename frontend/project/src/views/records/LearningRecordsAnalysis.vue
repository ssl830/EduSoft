<template>
  <div class="page-container">
    <AppSidebar />
    <div class="content-container">
      <div class="learning-records-analysis">
        <h1>教师练习统计</h1>
        <div class="filter-section">
          <label for="courseFilter">按课程筛选：</label>
          <select id="courseFilter" v-model="selectedCourse">
            <option value="">所有课程</option>
            <option v-for="course in uniqueCourses" :key="course" :value="course">
              {{ course }}
            </option>
          </select>
        </div>
        <div v-if="loading" class="loading-indicator">加载中...</div>
        <div v-else-if="error" class="error-message">{{ error }}</div>
        <div v-else>
          <div class="card-container">
            <div class="practice-card" v-for="practice in filteredPractices" :key="practice.id">
              <h3>{{ practice.title }}</h3>
              <p><strong>课程名称：</strong>{{ practice.course_name }}</p>
              <p><strong>开始时间：</strong>{{ practice.start_time }}</p>
              <p><strong>结束时间：</strong>{{ practice.end_time }}</p>
              <p><strong>已提交人数：</strong>
                <span v-if="practice.classTotal !== null">{{ practice.submissionCount }}/{{ practice.classTotal }}</span>
                <span v-else>{{ practice.submissionCount }}</span>
              </p>
              <p><strong>平均分：</strong>{{ practice.averageScore !== null ? practice.averageScore.toFixed(2) : '-' }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import AppSidebar from '@/components/layout/AppSidebar.vue';
import PracticeApi from '@/api/practice';

// Define the type for a practice object
interface Practice {
  id: number;
  title: string;
  start_time: string;
  end_time: string;
  course_name: string;
  class_id: number; // 班级ID，后端需返回
}

interface PracticeWithStats extends Practice {
  submissionCount: number;
  averageScore: number | null;
  classTotal: number | null;
}

const loading = ref(false);
const error = ref<string | null>(null);
const practices = ref<Array<PracticeWithStats>>([]);
const selectedCourse = ref('');

const uniqueCourses = computed(() => {
  const courses = practices.value.map(practice => practice.course_name);
  return Array.from(new Set(courses));
});

const filteredPractices = computed(() => {
  if (!selectedCourse.value) {
    return practices.value;
  }
  return practices.value.filter(practice => practice.course_name === selectedCourse.value);
});

const fetchTeacherPractices = async () => {
  try {
    loading.value = true;
    error.value = null;
    const response = await PracticeApi.getTeacherPractices();
    const practiceList: Practice[] = response.data;
    // 并发获取所有练习的统计数据和班级总人数
    const statsList = await Promise.all(
      practiceList.map(async (practice) => {
        let submissionCount = 0;
        let averageScore = null;
        let classTotal = null;
        try {
          const statsRes = await PracticeApi.getPracticeStats(practice.id);
          const data = statsRes.data || {};
          submissionCount = data.total_submissions ?? 0;
          averageScore = data.average_score ?? null;
        } catch {}
        try {
          // 需要后端提供此API：/api/class/{classId}/student-count
          const classRes = await PracticeApi.getClassStudentCount(practice.class_id);
          classTotal = classRes.data?.total ?? null;
        } catch {}
        return {
          ...practice,
          submissionCount,
          averageScore,
          classTotal,
        };
      })
    );
    practices.value = statsList;
  } catch (err: any) {
    error.value = err.message || '加载练习信息失败';
  } finally {
    loading.value = false;
  }
};

onMounted(() => {
  fetchTeacherPractices();
});
</script>

<style scoped>
/* Updated styles for better aesthetics and added color changes */
.page-container {
  display: flex;
  height: 100vh;
  background-color: #f0f4f8; /* Changed background color for better contrast */
}

.content-container {
  flex: 1;
  padding: 2rem;
  overflow-y: auto;
  background-color: #ffffff;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  border-radius: 8px;
  margin: 1rem;
}

.learning-records-analysis {
  font-size: 1rem;
  color: #333;
}

.filter-section {
  margin-bottom: 1.5rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-section label {
  font-weight: bold;
  color: #555;
}

.filter-section select {
  padding: 0.5rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-size: 1rem;
  background-color: #fff;
  transition: border-color 0.3s;
}

.filter-section select:hover {
  border-color: #888;
}

.card-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(270px, 1fr));
  gap: 1.2rem;
  padding: 10px 0;
}

.practice-card {
  background: #fafbfc;
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(60, 60, 60, 0.06);
  padding: 1.1rem 1rem 1rem 1rem;
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
  transition: box-shadow 0.2s, transform 0.2s;
  border: 1px solid #ececec;
  position: relative;
}

.practice-card:hover {
  box-shadow: 0 6px 18px rgba(60, 60, 60, 0.13);
  transform: translateY(-2px) scale(1.01);
  border-color: #bcd4e6;
  background: #f5f7fa;
}

.practice-card h3 {
  font-size: 1.18rem;
  color: #2d3a4a;
  font-weight: 600;
  margin: 0 0 0.4rem 0;
  background: #f0f2f5;
  border-radius: 5px;
  padding: 0.4rem 0.7rem;
  border: 1px solid #e0e3e8;
  letter-spacing: 0.2px;
}

.practice-card p {
  font-size: 1.08rem;
  color: #444;
  margin: 0.08rem 0;
  line-height: 1.6;
  word-break: break-all;
  padding-left: 2px;
}

.practice-card p strong {
  color: #3b6ea5;
  font-weight: 500;
}

.loading-indicator {
  font-size: 1.2rem;
  color: #666;
  text-align: center;
  margin-top: 2rem;
}

.error-message {
  color: red;
  font-size: 1.2rem;
  text-align: center;
  margin-top: 2rem;
}

.view-stats-btn {
  margin-top: 1rem;
  padding: 0.5rem 1rem;
  background-color: #1890ff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.view-stats-btn:hover {
  background-color: #40a9ff;
}

.modal-mask {
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

.modal-container {
  background: white;
  border-radius: 8px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.modal-header {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-header h3 {
  margin: 0;
  color: #333;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #999;
  cursor: pointer;
  padding: 0;
  line-height: 1;
}

.modal-close:hover {
  color: #666;
}

.modal-body {
  padding: 24px;
}

@media (max-width: 600px) {
  .card-container {
    grid-template-columns: 1fr;
    gap: 0.7rem;
    padding: 0;
  }
  .practice-card {
    padding: 0.7rem 0.4rem;
  }
  .practice-card h3 {
    font-size: 1rem;
    padding: 0.3rem 0.4rem;
  }
  .practice-card p {
    font-size: 0.98rem;
  }
}
</style>
