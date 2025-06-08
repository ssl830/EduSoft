<script setup lang="ts">
import {ref, reactive, onMounted} from 'vue'
import { useRoute } from 'vue-router'
import ExerciseApi from '../../api/exercise'
import { ElMessage } from 'element-plus';
import QuestionApi from '../../api/question'

const route = useRoute()

const practiceId = Number(route.params.id)

interface Question {
  id: number;
  content: string;
  type: string;
  options?: any[];
  answer?: string;
  score?: number;
  [key: string]: any;
}
const exercise = reactive({
  title: '',
  courseId: 0,
  classId: 0,
  startTime: '',
  endTime: '',
  allowMultipleSubmission: true,
  questions: [] as Question[]
})

const loading = ref(false)
const error = ref('')

// 题库相关
const questionBankLoading = ref(false)
const questionBankError = ref('')
const questionBankList = ref<Question[]>([])
const selectedQuestionIds = ref<number[]>([])

// 拉取题库题目
const fetchQuestionBank = async () => {
  questionBankLoading.value = true
  questionBankError.value = ''
  try {
    const res = await QuestionApi.getQuestionList(
      exercise.courseId ? { courseId: exercise.courseId } : {}
    )
    let list = []
    if (res.data && Array.isArray(res.data.questions)) {
      list = res.data.questions
    } else if (res.data && Array.isArray(res.data.data?.questions)) {
      list = res.data.data.questions
    }    // 类型修正：单选题但答案长度大于1，视为多选题
    list.forEach((q: any) => {
      if (q.type === 'singlechoice' && typeof q.answer === 'string' && q.answer.length > 1) {
        q.type = 'multiplechoice'
      }
    })
    questionBankList.value = list
  } catch (err) {
    questionBankError.value = '获取题库失败'
    questionBankList.value = []
  } finally {
    questionBankLoading.value = false
  }
}

// 添加全部选中题目到练习
const addAllSelectedQuestions = () => {
  const selected = questionBankList.value.filter(q => selectedQuestionIds.value.includes(q.id))
  const existIds = exercise.questions.map(q => q.id)
  const toAdd = selected.filter(q => !existIds.includes(q.id))
  toAdd.forEach(q => {
    exercise.questions.push({
      id: q.id,
      content: q.name || q.content,
      type: q.type,
      options: q.options,
      answer: q.answer,
      score: q.score || 5
    } as Question)
  })
  ElMessage.success('题目已添加')
}

// 拉取练习详情
const fetchPracticeDetail = async () => {
  loading.value = true
  try {
    const res: any = await ExerciseApi.takeExercise(String(practiceId))
    const data = res.data
    exercise.title = data.title
    exercise.courseId = data.courseId
    exercise.classId = data.classId
    exercise.startTime = data.startTime
    exercise.endTime = data.endTime
    exercise.allowMultipleSubmission = data.allowMultipleSubmission
    exercise.questions = (data.questions || []).map((q: any) => ({
      id: q.id,
      content: q.name || q.content,
      type: q.type,
      options: q.options,
      answer: q.answer,
      score: q.score || 5
    }))
    // 拉取题库
    fetchQuestionBank()
  } catch (err) {
    error.value = '获取练习详情失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchPracticeDetail()
})

// 保存练习基本信息和题目
const saveBasicInfo = async () => {
  loading.value = true
  try {
    const response: any = await ExerciseApi.updateExercise(practiceId, {
      title: exercise.title,
      startTime: exercise.startTime,
      endTime: exercise.endTime,
      allowMultipleSubmission: exercise.allowMultipleSubmission
    })
    if (response.code !== 200) {
        ElMessage.error(response.message || '保存基本信息失败，请稍后再试')
    }else{
        // 题目同步到后端
        if (exercise.questions.length > 0) {
            const questionIds = exercise.questions.map(q => q.id)
            const scores = exercise.questions.map(q => q.score || 5)
            const response2: any = await ExerciseApi.importQuestionsToPractice({
                practiceId,
                questionIds,
                scores
            })
            if(response2.code !== 200) {
                ElMessage.error(response2.message || '题目保存失败，请稍后再试')
                return
            }else{
                ElMessage.success('基本信息和题目保存成功')
            }
        }
    }

  } catch (err) {
    error.value = '保存失败，请稍后再试'
  } finally {
    loading.value = false
  }
}

const questionTypeMap: Record<string, string> = {
    'singlechoice': '单选题',
    'multiplechoice': '多选题',
    'judge': '判断题',
    'program': '简答题',
    'fillblank': '填空题'
}

// 修改题目分值
const updateQuestionScore = (questionId: number, score: number) => {
  const q = exercise.questions.find((q: Question) => q.id === questionId)
  if (q) q.score = score
}

// 删除题目
const removeQuestion = (questionId: number) => {
  exercise.questions = exercise.questions.filter((q: Question) => q.id !== questionId)
}
</script>

<template>
  <div class="exercise-edit-layout">
    <!-- 左侧：练习编辑与题目列表 -->
    <div class="exercise-edit-main">
      <div class="edit-card">
        <h2 class="main-title">编辑练习</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else>
          <div class="form-section">
            <label class="form-label">标题</label>
            <input v-model="exercise.title" type="text" class="input" placeholder="请输入练习标题" />
            <div class="row">
              <div class="col">
                <label class="form-label">开始时间</label>
                <input v-model="exercise.startTime" type="datetime-local" class="input" />
              </div>
              <div class="col">
                <label class="form-label">截止时间</label>
                <input v-model="exercise.endTime" type="datetime-local" class="input" />
              </div>
            </div>
            <label class="form-label">允许多次提交</label>
            <select v-model="exercise.allowMultipleSubmission" class="input">
              <option :value="true">是</option>
              <option :value="false">否</option>
            </select>
            <div class="form-actions">
              <button class="btn btn-primary" @click="saveBasicInfo">保存更改</button>
            </div>
          </div>
          <hr class="divider" />
          <div class="question-section">
            <div class="q-header">
              <h3 class="section-title">题目列表</h3>
            </div>
            <table class="q-table">
              <thead>
                <tr>
                  <th style="width:40px;">#</th>
                  <th>题目内容</th>
                  <th style="width:90px;">分值</th>
                  <th style="width:90px;">操作</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(q, idx) in exercise.questions" :key="q.id">
                  <td>{{ idx + 1 }}</td>
                  <td class="q-content">{{ q.content }}</td>
                  <td>
                    <input type="number" v-model.number="q.score" @change="updateQuestionScore(q.id, q.score)" class="score-input" min="1" />
                  </td>
                  <td>
                    <button class="btn2 btn-danger" @click="removeQuestion(q.id)">移除</button>
                  </td>
                </tr>
                <tr v-if="!exercise.questions.length">
                  <td colspan="4" class="empty">暂无题目，请从右侧题库添加</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
        <transition name="fade">
          <div v-if="error" class="error-tip">{{ error }}</div>
        </transition>
      </div>
    </div>
    <!-- 右侧：题库表格 -->
    <div class="exercise-edit-bank">
      <div class="bank-header">
        <h3 class="section-title">题库</h3>
      </div>
      <div v-if="questionBankLoading" class="loading">加载中...</div>
      <div v-else-if="questionBankError" class="error-tip">{{ questionBankError }}</div>
      <div v-else>
        <div class="bank-table-wrapper">
          <table class="q-table">
            <thead>
              <tr>
                <th style="width:40px;">#</th>
                <th>题目内容</th>
                <th>类型</th>
                <th>选项</th>
                <th>答案</th>
                <th style="width:60px;">选择</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(q, idx) in questionBankList" :key="q.id">
                <td>{{ idx + 1 }}</td>
                <td class="q-content">{{ q.name || q.content }}</td>
                <td>
                  <span class="type-tag">{{ questionTypeMap[q.type] }}</span>
                </td>
                <td>
                  <div v-if="q.options && q.options.length">
                    <div v-for="opt in q.options" :key="opt.key" class="option-item">{{ opt.key }}. {{ opt.text }}</div>
                  </div>
                </td>
                <td>{{ q.answer }}</td>
                <td>
                  <input type="checkbox" v-model="selectedQuestionIds" :value="q.id" />
                </td>
              </tr>
              <tr v-if="!questionBankList.length">
                <td colspan="6" class="empty">暂无题目</td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="bank-actions">
          <button class="btn btn-primary" @click="addAllSelectedQuestions" :disabled="!selectedQuestionIds.length">
            添加全部题目
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.exercise-edit-layout {
  display: flex;
  gap: 32px;
  align-items: flex-start;
  background: #f6f8fa;
  min-height: 100vh;
  padding: 32px 0;
}
.exercise-edit-main {
    margin-left: 200px;
    max-width: 500px;
  flex: 2;
  min-width: 0;
}
.exercise-edit-bank {
  flex: 1.2;
  min-width: 340px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(44,110,207,0.07);
  padding: 2rem 1.2rem 1.5rem 1.2rem;
  transition: box-shadow 0.2s;
    margin-right: 200px;
}
.bank-header {
  margin-bottom: 1.2rem;
  border-bottom: 1px solid #e3e8ee;
  padding-bottom: 0.7rem;
}
.bank-table-wrapper {
  max-height: 60vh;
  overflow-y: auto;
}
.bank-actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 1.2rem;
}
.q-table {
  width: 100%;
  border-collapse: collapse;
  background: #fff;
  font-size: 15px;
}
.q-table th, .q-table td {
  border: 1px solid #f0f1f3;
  padding: 0.6rem 0.5rem;
  text-align: left;
  vertical-align: top;
}
.q-table th {
  background: #f7fafd;
  font-weight: 600;
  color: #2c6ecf;
}
.q-content {
  max-width: 320px;
  white-space: pre-wrap;
  word-break: break-all;
  color: #333;
}
.score-input {
  width: 60px;
  padding: 3px 8px;
  border: 1px solid #cfd8dc;
  border-radius: 4px;
  font-size: 15px;
  background: #f7fafd;
  transition: border-color 0.2s;
}
.score-input:focus {
  border-color: #2c6ecf;
  outline: none;
}
.btn2 {
  padding: 4px 12px;
  border-radius: 4px;
  border: none;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.2s;
}
.btn-danger {
  background: #ffebee;
  color: #d32f2f;
  border: 1px solid #ffcdd2;
}
.btn-danger:hover {
  background: #ffcdd2;
}
.btn-primary {
  background-color: #2c6ecf;
  color: white;
  padding: 0.6rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  font-size: 15px;
  transition: background-color 0.2s;
  box-shadow: 0 2px 8px rgba(44,110,207,0.08);
}
.btn-primary:hover {
  background-color: #215bb4;
}
.loading {
  color: #888;
  padding: 1.2rem;
  text-align: center;
  font-size: 16px;
}
.error-tip {
  color: #d32f2f;
  background: #fff0f0;
  padding: 0.9rem 1rem;
  border-radius: 4px;
  margin-top: 1.2rem;
  font-size: 15px;
  text-align: center;
}
.edit-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(44,110,207,0.07);
  padding: 2rem 2.5rem 2.5rem 2.5rem;
  min-width: 400px;
  transition: box-shadow 0.2s;
}
.form-section label.form-label {
  font-weight: 600;
  margin-top: 0.5rem;
  display: block;
  color: #2c6ecf;
  margin-bottom: 0.3rem;
}
.input {
  width: 100%;
  padding: 0.6rem 0.8rem;
  border: 1px solid #cfd8dc;
  border-radius: 4px;
  margin-bottom: 1.2rem;
  font-size: 15px;
  background: #f7fafd;
  transition: border-color 0.2s;
}
.input:focus {
  border-color: #2c6ecf;
  outline: none;
}
.form-actions {
  margin-top: 1.2rem;
  display: flex;
  justify-content: flex-end;
}
.row {
  display: flex;
  gap: 1.2rem;
}
.col {
  flex: 1;
}
.divider {
  margin: 2rem 0 1.5rem 0;
  border: none;
  border-top: 1px solid #e3e8ee;
}
.q-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.2rem;
}
.section-title {
  font-size: 1.18rem;
  font-weight: 600;
  color: #2c6ecf;
  letter-spacing: 1px;
}
.main-title {
  font-size: 1.5rem;
  font-weight: 700;
  color: #215bb4;
  margin-bottom: 1.5rem;
  letter-spacing: 1px;
}
.empty {
  color: #aaa;
  text-align: center;
  font-size: 15px;
}
.type-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  background: #e3f2fd;
  color: #1976d2;
  font-size: 13px;
  font-weight: 500;
}
.option-item {
  background: #f7fafd;
  border-radius: 3px;
  padding: 1px 6px;
  margin-bottom: 2px;
  display: inline-block;
  font-size: 13px;
}
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
