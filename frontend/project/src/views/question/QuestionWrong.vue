<script setup lang="ts">
import {ref, onMounted} from 'vue'
import QuestionApi from '../../api/question.ts'

const questions = ref<any[]>([])
const loading = ref(true)
const error = ref('')

// 获取错题列表
const fetchQuestions = async () => {
    loading.value = true
    error.value = ''

    try {
        const response = await QuestionApi.getWrongQuestionList()
        console.log('错题列表响应:', response)

        if ((response as any).code === 200 && response.data) {
            questions.value = Array.isArray(response.data) ? response.data : []
            console.log('错题列表数据:', questions.value)
            if (questions.value.length > 0) {
                const firstQuestion = questions.value[0]
                console.log('第一个错题的完整数据结构:', JSON.stringify(firstQuestion, null, 2))
                console.log('第一个错题的ID:', firstQuestion.id)
                console.log('第一个错题的所有字段:', Object.keys(firstQuestion))
            }
        } else {
            error.value = (response as any).message || '获取错题列表失败'
            console.error('获取错题列表失败:', response)
            questions.value = []
        }
    } catch (err) {
        error.value = '获取错题列表失败，请稍后再试'
        console.error('获取错题列表错误:', err)
        questions.value = []
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchQuestions()
})

// 弹窗相关状态
const showDetailDialog = ref(false)
const selectedQuestion = ref<any>(null)

// 预览题目详情
const showQuestionDetail = (question: any) => {
    selectedQuestion.value = question
    showDetailDialog.value = true
}

// 删除错题
const deleteQuestion = async (questionId: string) => {
    try {
        if (!questionId) {
            error.value = '题目ID不能为空'
            console.error('删除错题失败: 题目ID为空')
            return
        }
        console.log('删除错题，ID:', questionId)
        const response = await QuestionApi.removeWrongQuestion(questionId)
        console.log('删除错题响应:', response)

        if ((response as any).code === 200) {
            await fetchQuestions()
        } else {
            error.value = (response as any).message || '删除错题失败'
            console.error('删除错题失败:', response)
        }
    } catch (err) {
        error.value = '删除错题失败，请稍后再试'
        console.error('删除错题错误:', err)
    }
}

// 格式化答案
const formatAnswer = (question: any) => {
    if (!question) return '-'
    if (question.type === 'multiple_choice') {
        return question.correct_answer.split(',').join(', ')
    }
    return question.correct_answer || '-'
}

// 类似题目弹窗相关状态
const showSimilarDialog = ref(false)
const similarApiKey = ref('')
const similarLoading = ref(false)
const similarError = ref('')
const similarQuestion = ref('')
const similarSourceQuestion = ref<any>(null)

// 打开类似题目弹窗
const openSimilarDialog = (question: any) => {
    similarApiKey.value = ''
    similarError.value = ''
    similarQuestion.value = ''
    similarSourceQuestion.value = question
    showSimilarDialog.value = true
}

// 调用DeepSeek接口生成类似题目 (修复：API端点)
const generateSimilarQuestion = async () => {
    similarError.value = ''
    similarQuestion.value = ''
    if (!similarApiKey.value) {
        similarError.value = '请输入API-Key'
        return
    }

    // 检查原题内容是否存在
    if (!similarSourceQuestion.value?.content) {
        similarError.value = '原题内容为空，无法生成类似题目'
        return
    }

    similarLoading.value = true
    try {
        const prompt = `请根据以下题目，生成一道风格、难度和知识点类似的新题目（不要与原题重复），请不要返回markdown格式，返回普通格式即可，返回题目和答案即可：\n${similarSourceQuestion.value.content}`

        // 修复：正确的API端点
        const resp = await fetch('https://api.deepseek.com/chat/completions', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${similarApiKey.value}`
            },
            body: JSON.stringify({
                model: 'deepseek-chat',
                messages: [
                    {role: 'user', content: prompt}
                ],
                temperature: 0.8,
                max_tokens: 512
            })
        })

        if (!resp.ok) {
            const errorData = await resp.json();
            similarError.value = `API请求失败: ${errorData.error?.message || resp.statusText}`
            return
        }

        const data = await resp.json()
        if (data.choices?.[0]?.message?.content) {
            similarQuestion.value = data.choices[0].message.content.trim()
        } else {
            similarError.value = '未能生成类似题目，请重试'
        }
    } catch (e) {
        similarError.value = '请求出错，请检查API-Key或稍后重试'
        console.error('DeepSeek API调用错误:', e)
    } finally {
        similarLoading.value = false
    }
}
</script>

<template>
  <div class="question-bank-container">
    <div class="resource-header">
      <h2>错题本</h2>
    </div>

    <!-- Resource List -->
    <div v-if="loading" class="loading-container">加载中...</div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <div v-else-if="questions.length === 0" class="empty-state">
      暂无错题记录
    </div>
    <div v-else class="resource-table-wrapper">
      <table class="resource-table">
        <thead>
        <tr>
          <th>题目内容</th>
          <th>所属课程</th>
          <th>所属章节</th>
          <th>所属练习</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="question in questions" :key="question.id">
          <td>{{ question.content }}</td>
          <td>{{ question.course_name }}</td>
          <td>{{ question.section_title || '-' }}</td>
          <td>{{ question.practice_title || '-' }}</td>
          <td class="actions">
            <button
                class="btn-action preview"
                @click="showQuestionDetail(question)"
                title="查看"
            >
              查看
            </button>
            <button
                class="btn-action delete"
                @click="deleteQuestion(question.question_id)"
                title="删除"
            >
              删除
            </button>
            <!-- 新增类似题目按钮 -->
            <button
                class="btn-action"
                style="background:#e8f5e9;color:#2e7d32;"
                @click="openSimilarDialog(question)"
                title="生成类似题目"
            >
              类似题目
            </button>
          </td>
        </tr>
        </tbody>
      </table>
      <!-- 在resource-table下方添加弹窗 -->
      <div v-if="showDetailDialog" class="modal-mask">
        <div class="modal-container">
          <div class="modal-header">
            <h3>题目详情</h3>
            <button class="modal-close" @click="showDetailDialog = false">&times;</button>
          </div>

          <div class="modal-body" v-if="selectedQuestion">
            <div class="detail-row">
              <label>课程名称:</label>
              <span>{{ selectedQuestion.course_name || '-' }}</span>
            </div>
            <div class="detail-row">
              <label>章节标题:</label>
              <span>{{ selectedQuestion.section_title || '-' }}</span>
            </div>
            <div class="detail-row">
              <label>所属练习:</label>
              <span>{{ selectedQuestion.practice_title || '-' }}</span>
            </div>
            <div class="detail-row">
              <label>题目类型:</label>
              <span>{{
                  selectedQuestion.type === 'single_choice' ? '单选题' :
                      selectedQuestion.type === 'multiple_choice' ? '多选题' :
                          selectedQuestion.type === 'true_false' ? '判断题' :
                              selectedQuestion.type === 'short_answer' ? '简答题' : '填空题'
                }}</span>
            </div>
            <div class="detail-row">
              <label>题目内容:</label>
              <div class="content-box">{{ selectedQuestion.content }}</div>
            </div>

            <!-- 选项展示 -->
            <div v-if="['single_choice', 'multiple_choice'].includes(selectedQuestion.type)"
                 class="detail-row">
              <label>题目选项:</label>
              <div class="options-list">
                <div v-for="(opt, index) in selectedQuestion.options"
                     :key="index"
                     class="option-item">
                  <span class="option-key">{{ opt.key }}.</span>
                  <span class="option-text">{{ opt.text }}</span>
                  <span v-if="selectedQuestion.answer.includes(opt.key)"
                        class="correct-badge">✓</span>
                </div>
              </div>
            </div>

            <div class="detail-row">
              <label>正确答案:</label>
              <span class="answer-text">{{ formatAnswer(selectedQuestion) || '-' }}</span>
            </div>
            <div class="detail-row">
              <label>你的答案:</label>
              <span class="answer-text">{{ selectedQuestion.wrong_answer || '-' }}</span>
            </div>
          </div>
        </div>
      </div>
      <!-- 新增：类似题目弹窗 -->
      <div v-if="showSimilarDialog" class="modal-mask">
        <div class="modal-container">
          <div class="modal-header">
            <h3>生成类似题目</h3>
            <button class="modal-close" @click="showSimilarDialog = false">&times;</button>
          </div>
          <div class="modal-body">
            <div class="detail-row">
              <label>原题目:</label>
              <div class="content-box" style="flex:1;">{{ similarSourceQuestion?.content }}</div>
            </div>
            <!-- 优化API-Key输入区域 -->
            <div class="form-group" style="margin-bottom:0.5rem;">
              <label for="api-key-input" style="font-weight:500;">Deepseek API-Key:</label>
              <input
                id="api-key-input"
                v-model="similarApiKey"
                type="password"
                placeholder="请输入您的API-Key"
                class="input-api-key"
                autocomplete="off"
              />
              <div style="margin-top:0.25rem;">
                <a
                  href="https://platform.deepseek.com/api_keys"
                  target="_blank"
                  rel="noopener"
                  style="color:#2c6ecf;text-decoration:underline;font-size:0.95em;"
                >
                  获取您的API-KEY
                </a>
              </div>
            </div>
            <div class="form-actions" style="margin-top:1rem;">
              <button class="btn-primary" @click="generateSimilarQuestion" :disabled="similarLoading">
                {{ similarLoading ? '生成中...' : '生成类似题目' }}
              </button>
              <button class="btn-secondary" @click="showSimilarDialog = false">关闭</button>
            </div>
            <div v-if="similarError" class="error-message" style="margin-top:1rem;">{{ similarError }}</div>
            <div v-if="similarQuestion" class="detail-row" style="margin-top:1.5rem;">
              <label>生成结果:</label>
              <div class="content-box" style="flex:1;">{{ similarQuestion }}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 新增弹窗样式 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  border-radius: 8px;
  padding: 20px;
  width: 600px;
  max-width: 90%;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
  margin-bottom: 15px;
}

.modal-close {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.detail-row {
  margin: 12px 0;
  display: flex;
  align-items: flex-start;
}

.detail-row label {
  width: 100px;
  font-weight: 500;
  color: #666;
  flex-shrink: 0;
}

.content-box {
  background: #f5f5f5;
  padding: 10px;
  border-radius: 4px;
  white-space: pre-wrap;
}

.options-list {
  width: 100%;
}

.option-item {
  display: flex;
  align-items: center;
  margin: 6px 0;
  padding: 8px;
  background: #f8f8f8;
  border-radius: 4px;
}

.option-key {
  font-weight: 500;
  margin-right: 10px;
  min-width: 20px;
}

.correct-badge {
  color: #2e7d32;
  margin-left: 10px;
  font-weight: bold;
}

.answer-text {
  font-weight: 500;
  color: #2c6ecf;
}

.checkbox-group {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.checkbox-item {
  display: flex;
  align-items: center;
  margin-right: 1rem;
}

.checkbox-item input {
  margin-right: 0.5rem;
}

.question-bank-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 1.5rem;
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

select:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
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

.btn-action.delete {
  background: #ff4d4f;
  color: white;
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

.input-api-key {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
  margin-top: 0.25rem;
  box-sizing: border-box;
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

.question-list-section, .question-form-byhand-section, question-form-fromrepo-section {
  margin-bottom: 2rem;
}

.question-list {
  margin-top: 1rem;
}

.question-list-item {
  padding: 1rem;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  margin-bottom: 1rem;
  background-color: #f9f9f9;
}

.question-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 0.75rem;
  padding-bottom: 0.5rem;
  border-bottom: 1px solid #e0e0e0;
}

.question-number {
  font-weight: bold;
}

.question-type-badge {
  font-size: 0.875rem;
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  background-color: #e3f2fd;
  color: #1565c0;
}

.question-type-badge.single_choice {
  background-color: #e3f2fd;
  color: #1565c0;
}

.question-type-badge.multiple_choice {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.question-type-badge.true_false {
  background-color: #fff3e0;
  color: #e65100;
}

.question-type-badge.short_answer {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.question-type-badge.fill_blank {
  background-color: #e8eaf6;
  color: #3949ab;
}

.question-points {
  font-weight: 500;
}

.question-list-content {
  margin-bottom: 1rem;
}

.question-list-actions {
  display: flex;
  justify-content: flex-end;
  gap: 0.5rem;
}

.option-row {
  display: flex;
  align-items: center;
  margin-bottom: 0.5rem;
}

.option-key {
  flex: 0 0 2rem;
  text-align: center;
  font-weight: 500;
  background-color: #f5f5f5;
  border-radius: 4px;
  padding: 0.5rem;
  margin-right: 0.5rem;
}

.option-input {
  flex: 1;
}

.empty-state {
  padding: 2rem;
  text-align: center;
  color: #757575;
  background-color: #f5f5f5;
  border-radius: 4px;
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

</style>
