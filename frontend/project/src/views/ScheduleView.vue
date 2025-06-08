<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useAuthStore } from '../stores/auth'
import { useRouter } from 'vue-router'
import ClassApi from '../api/class'

const authStore = useAuthStore()
const router = useRouter()
const classes = ref<any[]>([])
const loading = ref(true)
const error = ref('')

const title = computed(() => authStore.userRole === 'teacher' ? '教学安排' : '我的课表')

const weekDays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
const startHour = 7
const endHour = 23
const hours = Array.from({length: endHour - startHour + 1}, (_, i) => startHour + i)

// 课程色彩池
const colorPool = [
  '#6fa8dc', '#f6b26b', '#93c47d', '#e06666', '#8e7cc3', '#ffd966', '#76a5af', '#c27ba0', '#a2c4c9', '#b4a7d6', '#f9cb9c', '#b6d7a8', '#ea9999', '#b7b7b7'
]
function getColor(idx: number) {
  return colorPool[idx % colorPool.length]
}

// 解析班级名中的时间部分
function parseClassTime(name: string) {
  // 例：太极拳周一 14:00-15:30
  const match = name.match(/(周[一二三四五六日天])\s*(\d{1,2}):(\d{2})-(\d{1,2}):(\d{2})/)
  if (match) {
    return {
      week: match[1],
      startHour: parseInt(match[2]),
      startMin: parseInt(match[3]),
      endHour: parseInt(match[4]),
      endMin: parseInt(match[5])
    }
  }
  return null
}
function stripClassTime(name: string) {
  // 去掉时间部分，保留课程/班级名
  return name.replace(/(周[一二三四五六日天]\s*\d{1,2}:\d{2}-\d{1,2}:\d{2})/, '').trim()
}

const scheduledBlocks = computed(() => {
  // 返回 { weekIdx, hourIdx, start, end, class, colorIdx } 列表
  return classes.value
    .map((c, idx) => {
      const t = parseClassTime(c.name)
      if (!t) return null
      const weekIdx = weekDays.indexOf(t.week)
      if (weekIdx === -1) return null
      const start = t.startHour + t.startMin / 60
      const end = t.endHour + t.endMin / 60
      return {
        weekIdx,
        start,
        end,
        class: c,
        colorIdx: idx
      }
    })
    .filter(Boolean)
})
const unscheduledClasses = computed(() =>
  classes.value.filter(c => !parseClassTime(c.name))
)

onMounted(async () => {
  currentWeek.value = calculateCurrentWeek()
  loading.value = true
  try {
    let response
    if (authStore.userRole === 'teacher') {
      response = await ClassApi.getTeacherClasses(String(authStore.user?.id ?? ''))
    } else {
      response = await ClassApi.getUserClasses(BigInt(authStore.user?.id ?? 0))
    }
    if ((response as any).code === 200 && Array.isArray(response.data)) {
      classes.value = response.data.map(classItem => ({
        ...classItem,
        id: classItem.id || classItem.classId,
        name: classItem.className || classItem.name || '未命名班级',
        code: classItem.classCode || classItem.code || '无代码',
        courseName: classItem.courseName || '未知课程',
        createdAt: classItem.createdAt || classItem.joinedAt || new Date().toISOString()
      }))
    } else {
      error.value = (response as any).message || '获取班级列表失败'
    }
  } catch (err) {
    error.value = '获取班级列表失败，请稍后再试'
  } finally {
    loading.value = false
  }
})

function getBlockStyle(block: any, hourIdx: number) {
  const blockStart = block.start
  const blockEnd = block.end
  const cellStart = hourIdx
  const cellEnd = hourIdx + 1

  if (blockEnd <= cellStart || blockStart >= cellEnd) return { display: 'none' }

  const totalHeight = 60
  const top = Math.max(0, (blockStart - cellStart) * totalHeight)
  const bottom = Math.max(0, (cellEnd - blockEnd) * totalHeight)
  const height = totalHeight - top - bottom

  return {
    top: top + 'px',
    height: height + 'px',
    '--block-color': getColor(block.colorIdx)
  }
}
function getCellBlocks(hourIdx: number, weekIdx: number) {
  // 返回该小时、该周的所有课程块
  return scheduledBlocks.value.filter(b => b && b.weekIdx === weekIdx && b.start < hourIdx + 1 && b.end > hourIdx)
}
function goToClassDetail(classId: number) {
  router.push(`/class/${classId}`)
}

// 当前周数
const currentWeek = ref(1)

// 学期开始日期
const semesterStartDate = new Date('2025-02-24')

// 计算当前是第几周
function calculateCurrentWeek(): number {
  const today = new Date()
  const startDate = new Date(semesterStartDate)

  // 将时间都设置为当天的0点，以便准确计算天数差
  today.setHours(0, 0, 0, 0)
  startDate.setHours(0, 0, 0, 0)

  // 计算天数差
  const diffTime = today.getTime() - startDate.getTime()
  const diffDays = Math.floor(diffTime / (1000 * 60 * 60 * 24))

  // 计算周数（从第1周开始）
  const week = Math.floor(diffDays / 7) + 1

  // 限制在1-20周之间
  return Math.max(1, Math.min(20, week))
}

// 切换到当前周
function goToCurrentWeek() {
  currentWeek.value = calculateCurrentWeek()
}

// 切换周数
function changeWeek(delta: number) {
  const newWeek = currentWeek.value + delta
  if (newWeek >= 1 && newWeek <= 20) {
    currentWeek.value = newWeek
  }
}

// 获取当前周的日期范围
function getWeekDateRange(): string {
  const startDate = new Date(semesterStartDate)
  startDate.setDate(semesterStartDate.getDate() + (currentWeek.value - 1) * 7)
  const endDate = new Date(startDate)
  endDate.setDate(startDate.getDate() + 6)

  return `${startDate.getMonth() + 1}/${startDate.getDate()} - ${endDate.getMonth() + 1}/${endDate.getDate()}`
}

// 获取当前周的日期
function getDateByWeekIndex(index: number): string {
  const date = new Date(semesterStartDate)
  date.setDate(semesterStartDate.getDate() + (currentWeek.value - 1) * 7 + index)
  return `${date.getMonth() + 1}/${date.getDate()}`
}

// 格式化课程块时间
function formatBlockTime(block: any): string {
  const start = Math.floor(block.start)
  const startMin = Math.round((block.start - start) * 60)
  const end = Math.floor(block.end)
  const endMin = Math.round((block.end - end) * 60)
  return `${start}:${startMin.toString().padStart(2, '0')}-${end}:${endMin.toString().padStart(2, '0')}`
}
</script>

<template>
  <div class="schedule-container">
    <div class="schedule-header">
      <h2 class="schedule-title">{{ title }}</h2>
      <div class="schedule-controls">
        <div class="week-selector">
          <button class="nav-btn" @click="changeWeek(-1)" :disabled="currentWeek <= 1">
            <i class="fas fa-chevron-left"></i>
          </button>
          <div class="week-info">
            <span class="week-text">第 {{ currentWeek }} 周</span>
            <div class="week-dates">{{ getWeekDateRange() }}</div>
          </div>
          <button class="nav-btn" @click="changeWeek(1)" :disabled="currentWeek >= 20">
            <i class="fas fa-chevron-right"></i>
          </button>
        </div>
        <button class="current-week-btn" @click="goToCurrentWeek">
          <i class="fas fa-calendar-day"></i>
          本周
        </button>
      </div>
    </div>

    <div v-if="loading" class="loading-container">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <div v-else class="schedule-content-wrapper">
      <div class="schedule-grid">
        <!-- 时间轴 -->
        <div class="time-axis">
          <div class="time-label"></div>
          <div v-for="hour in hours" :key="hour" class="time-label">
            <span class="time-text">{{ hour }}:00</span>
            <div class="time-line"></div>
          </div>
        </div>

        <!-- 星期标题 -->
        <div class="week-header">
          <div v-for="(week, index) in weekDays" :key="week" class="week-label">
            <span class="week-text">{{ week }}</span>
            <span class="date-text">{{ getDateByWeekIndex(index) }}</span>
          </div>
        </div>

        <!-- 课程网格 -->
        <div class="schedule-content">
          <div v-for="week in weekDays" :key="week" class="week-column">
            <div v-for="hour in hours" :key="hour" class="time-slot">
              <div class="time-indicator"></div>
              <div
                v-for="block in getCellBlocks(hour, weekDays.indexOf(week))"
                :key="block?.class?.id"
                class="schedule-block"
                :style="getBlockStyle(block, hour)"
                @click="goToClassDetail(block?.class?.id)"
                :title="stripClassTime(block?.class?.name) + ' ' + parseClassTime(block?.class?.name)"
              >
                <div class="block-content">
                  <span class="block-title">{{ stripClassTime(block?.class?.name) }}</span>
                  <span class="block-time">{{ formatBlockTime(block) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div v-if="unscheduledClasses.length" class="unscheduled-section">
        <div class="section-header">
          <h3>未确定时间的课程</h3>
          <span class="badge">{{ unscheduledClasses.length }}</span>
        </div>
        <div class="unscheduled-list">
          <div
            v-for="c in unscheduledClasses"
            :key="c.id"
            class="unscheduled-item"
            @click="goToClassDetail(c.id)"
          >
            <div class="item-icon">
              <i class="fas fa-clock"></i>
            </div>
            <div class="item-content">
              <h4>{{ c.name }}</h4>
              <span class="class-code">{{ c.code }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.schedule-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  background: #f8fafc;
  min-height: 100vh;
}

.schedule-header {
  margin-bottom: 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 1rem;
}

.schedule-title {
  font-size: 2.3rem;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0;
  background: linear-gradient(135deg, #2c3e50, #3498db);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.schedule-controls {
  display: flex;
  gap: 1.5rem;
  align-items: center;
}

.week-selector {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: #fff;
  padding: 0.5rem 1rem;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  border: 1px solid #e2e8f0;
}

.week-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.2rem;
  min-width: 180px;
}

.week-text {
  font-weight: 600;
  color: #2c3e50;
  font-size: 1.1rem;
}

.week-dates {
  font-size: 0.85rem;
  color: #64748b;
}

.nav-btn {
  width: 36px;
  height: 36px;
  border: none;
  background: #f8fafc;
  border-radius: 8px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  color: #2c3e50;
  border: 1px solid #e2e8f0;
}

.nav-btn:hover:not(:disabled) {
  background: #e2e8f0;
  transform: translateY(-1px);
}

.nav-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background: #f1f5f9;
}

.current-week-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.2rem;
  background: #3498db;
  color: white;
  border: none;
  border-radius: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.2);
}

.current-week-btn:hover {
  background: #2980b9;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(52, 152, 219, 0.3);
}

.current-week-btn i {
  font-size: 0.9rem;
}

.schedule-content-wrapper {
  background: #fff;
  border-radius: 20px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
  overflow: hidden;
}

.schedule-grid {
  display: grid;
  grid-template-columns: 80px 1fr;
  grid-template-rows: auto 1fr;
  gap: 1px;
  background: #e2e8f0;
}

.time-axis {
  grid-column: 1;
  grid-row: 2;
  background: #f8fafc;
  border-right: 1px solid #e2e8f0;
}

.time-label {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.time-text {
  font-weight: 600;
  color: #64748b;
  font-size: 0.9rem;
  z-index: 1;
  background: #f8fafc;
  padding: 0 0.5rem;
}

.time-line {
  position: absolute;
  left: 0;
  right: 0;
  height: 1px;
  background: #e2e8f0;
}

.week-header {
  grid-column: 2;
  grid-row: 1;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #f8fafc;
  border-bottom: 1px solid #e2e8f0;
}

.week-label {
  padding: 1rem;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 0.3rem;
  border-right: 1px solid #e2e8f0;
}

.week-text {
  font-weight: 700;
  color: #2c3e50;
  font-size: 1.1rem;
}

.date-text {
  font-size: 0.85rem;
  color: #64748b;
}

.schedule-content {
  grid-column: 2;
  grid-row: 2;
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background: #fff;
}

.week-column {
  display: flex;
  flex-direction: column;
  border-right: 1px solid #e2e8f0;
}

.time-slot {
  height: 60px;
  position: relative;
  border-bottom: 1px solid #e2e8f0;
}

.time-indicator {
  position: absolute;
  left: 0;
  right: 0;
  height: 1px;
  background: #e2e8f0;
}

.schedule-block {
  position: absolute;
  left: 4px;
  right: 4px;
  background: var(--block-color, #3498db);
  color: #fff;
  border-radius: 12px;
  padding: 8px;
  cursor: pointer;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  transition: all 0.3s ease;
  z-index: 1;
}

.block-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.block-title {
  font-weight: 600;
  font-size: 0.9rem;
  line-height: 1.2;
}

.block-time {
  font-size: 0.8rem;
  opacity: 0.9;
}

.schedule-block:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  filter: brightness(1.05);
}

.unscheduled-section {
  margin-top: 2rem;
  padding: 1.5rem;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 1rem;
  margin-bottom: 1rem;
}

.section-header h3 {
  font-size: 1.2rem;
  color: #2c3e50;
  margin: 0;
}

.badge {
  background: #3498db;
  color: #fff;
  padding: 0.2rem 0.6rem;
  border-radius: 12px;
  font-size: 0.9rem;
  font-weight: 600;
}

.unscheduled-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 1rem;
}

.unscheduled-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  background: #f8fafc;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.unscheduled-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.08);
}

.item-icon {
  width: 40px;
  height: 40px;
  background: #3498db;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.item-content {
  flex: 1;
}

.item-content h4 {
  margin: 0;
  font-size: 1rem;
  color: #2c3e50;
}

.class-code {
  font-size: 0.9rem;
  color: #64748b;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem;
  gap: 1rem;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #3498db;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  text-align: center;
  padding: 2rem;
  color: #dc3545;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.06);
}
</style>
