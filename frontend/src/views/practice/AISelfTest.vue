<template>
    <div class="ai-selftest-page" ref="pageTop">
        <h1 class="main-title">AI 自测练习生成</h1>

        <div class="form-section">
            <label class="form-label-lg">*练习要求（题型、数量、难度等）</label>
            <textarea v-model="form.requirements" placeholder="例如：选择题3道，填空题1道，难度 medium" />
        </div>

        <div class="form-section">
            <label class="form-label-lg">知识点偏好</label>
            <textarea v-model="form.knowledge_preferences" placeholder="例如：进程调度、同步机制" />
        </div>

        <!-- 模式选择 -->
        <div class="form-section">
            <label class="form-label-lg">生成模式</label>
            <select v-model="mode" class="mode-select">
                <option value="auto">智能生成（根据需求/偏好）</option>
                <option value="selected">按选定题目生成</option>
            </select>
        </div>

        <!-- 选题 JSON 输入，仅在 selected 模式显示 -->
        <div v-if="mode === 'selected'" class="form-section">
            <div class="question-list">
                <div class="question-list-header">
                    <div class="question-list-title-row">
                        <label class="form-label-lg">选择题目</label>
                        <div class="tabs simple-tabs">
                            <button :class="['tab-btn', tab==='wrong'?'active':'']" @click="tab='wrong'">
                                <span class="iconfont icon-wrong"></span>错题库
                            </button>
                            <button :class="['tab-btn', tab==='favorite'?'active':'']" @click="tab='favorite'">
                                <span class="iconfont icon-favorite"></span>收藏题库
                            </button>
                        </div>
                    </div>
                </div>
                <p v-if="loadingQuestions">加载中...</p>
                <p v-else-if="currentList.length===0">暂无数据</p>
                <ul v-else>
                    <li v-for="q in currentList" :key="q.question_id || q.id">
                        <span>{{ q.content?.slice(0,50) }}</span>
                        <button
                            class="add-btn"
                            :disabled="selectedIds.includes(getRealId(q))"
                            @click="addQuestion(getRealId(q))"
                        >{{ selectedIds.includes(getRealId(q)) ? '已添加' : '添加' }}</button>
                    </li>
                </ul>
            </div>

            <!-- 已选择题目列表 -->
            <div v-if="selectedIds.length" class="selected-list">
                <label class="form-label-lg">已选择 {{ selectedIds.length }} 题 </label>
                <ul>
                    <li v-for="id in selectedIds" :key="id">
                        <span>{{ findQuestionContent(id)?.slice(0,50) || '题目 ' + id }}</span>
                        <button class="remove-btn" @click="removeQuestion(id)">移除</button>
                    </li>
                </ul>
            </div>
        </div>

        <div class="button-group" ref="buttonGroup">
            <button class="btn primary" :disabled="loading" @click="generate">
                {{ loading ? '生成中...' : '生成练习' }}
            </button>
            <button
                v-if="exercises.length"
                class="btn"
                @click="saveProgress"
                :disabled="saveLoading"
            >{{ saveLoading ? '保存中...' : '暂存进度' }}</button>
            <button
                v-if="exercises.length"
                class="btn success"
                @click="submitPractice"
                :disabled="submitLoading"
            >{{ submitLoading ? '提交中...' : '提交练习' }}</button>
        </div>

        <div v-if="loading" class="progress-wrapper">
            <div class="progress-bar"></div>
        </div>

        <div v-if="exercises.length" class="exercise-list">
            <h2 class="result-title">生成结果</h2>
            <div v-for="(item, idx) in exercises" :key="idx" class="exercise-item">
                <div class="exercise-question">
          <span class="type-tag" :class="'type-' + (item.type || '').toLowerCase()">
            {{ typeLabel(item.type) }}
          </span>
                    <span class="question-text">{{ idx + 1 }}. {{ item.question }}</span>
                </div>
                <!-- 只在 options 存在且第一个不为空时渲染 -->
                <ul
                    v-if="item.options && item.options.length && item.options[0]"
                    class="option-list-no-dot"
                >
                    <template v-if="item.type && item.type.toLowerCase() === 'singlechoice'">
                        <li
                            v-for="(opt, oIdx) in item.options"
                            :key="'single-' + oIdx"
                            class="option-no-dot"
                        >
                            {{ String.fromCharCode(65 + oIdx) + '. ' + opt }}
                        </li>
                    </template>
                    <template v-else>
                        <li
                            v-for="(opt, oIdx) in item.options"
                            :key="'other-' + oIdx"
                            class="option-no-dot"
                        >
                            {{ opt }}
                        </li>
                    </template>
                </ul>
                <input
                    v-model="answers[idx].answer"
                    :placeholder="inputPlaceholder(item.type)"
                    class="answer-input"
                />
                <div v-if="resultDetails.length" class="result-detail">
          <span :class="resultDetails[idx].correct ? 'correct' : 'wrong'">
            {{ resultDetails[idx].correct ? '✔ 正确' : '✘ 错误' }}，得分 {{ resultDetails[idx].score }}/{{ resultDetails[idx].maxScore }}
          </span>
                    <p v-if="resultDetails[idx].feedback">反馈：{{ resultDetails[idx].feedback }}</p>
                    <p class="correct-ans" v-if="resultDetails[idx].correctAnswer">正确答案：{{ resultDetails[idx].correctAnswer }}</p>
                </div>
            </div>
            <div v-if="result.totalScore !== undefined" class="total-score">总得分：{{ result.totalScore }}/{{ result.totalPossible }}</div>
            <button class="btn" style="margin: 1.5rem auto 0; display: block;" @click="scrollToBtnGroup">回到顶部</button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { reactive, ref, computed, watch, nextTick } from 'vue'
import { generateStudentExercise, generateSelectedStudentExercise, StudentExerciseRequest, StudentExerciseResponse, ExerciseItem, submitSelfPractice } from '@/api/ai'
import QuestionApi from '@/api/question'
import { ElMessage, ElMessageBox } from 'element-plus'

const pageTop = ref<HTMLElement | null>(null)
const buttonGroup = ref<HTMLElement | null>(null)

const form = reactive<StudentExerciseRequest>({
    requirements: '',
    knowledge_preferences: '',
    wrongQuestions: []
})

// 生成模式：auto | selected
const mode = ref<'auto' | 'selected'>('auto')

// 选题模式相关状态
const tab = ref<'wrong' | 'favorite'>('wrong')
const wrongList = ref<any[]>([])
const favoriteList = ref<any[]>([])
const loadingQuestions = ref(false)
const selectedIds = ref<number[]>([])

const currentList = computed(() => (tab.value === 'wrong' ? wrongList.value : favoriteList.value))

async function loadQuestionLists() {
    loadingQuestions.value = true
    try {
        const [wrongRes, favorRes] = await Promise.all([
            QuestionApi.getWrongQuestionList(),
            QuestionApi.getFavorQuestionList()
        ])
        wrongList.value = wrongRes.data.data || wrongRes.data || []
        favoriteList.value = favorRes.data.data || favorRes.data || []
    } catch (e) {
        ElMessage.error('加载题库失败')
    } finally {
        loadingQuestions.value = false
    }
}

watch(mode, (val) => {
    if (val === 'selected' && (wrongList.value.length === 0 && favoriteList.value.length === 0)) {
        loadQuestionLists()
    }
})

function getRealId(q: any): number {
    return q.question_id ?? q.id
}

function addQuestion(id: number) {
    if (selectedIds.value.includes(id)) {
        ElMessage.warning('该题目已被添加')
        return
    }
    selectedIds.value.push(id)
    ElMessage.success('已添加')
}

function removeQuestion(id: number) {
    const idx = selectedIds.value.indexOf(id)
    if (idx >= 0) {
        selectedIds.value.splice(idx, 1)
        ElMessage.success('已移除')
    }
}

const loading = ref(false)
const exercises = ref<ExerciseItem[]>([])

// 保存答案数组，用于暂存与提交。结构：[{questionId, answer, type}]
const answers = ref<any[]>([])
const result = reactive<any>({})
const resultDetails = ref<any[]>([])
const practiceId = ref<number | null>(null)
const saveLoading = ref(false)
const submitLoading = ref(false)

async function generate() {
    exercises.value = []
    if (!form.requirements.trim()) {
        ElMessage.error('请填写练习要求')
        return
    }
    loading.value = true
    try {
        let res: StudentExerciseResponse & { practiceId?: number }
        if (mode.value === 'selected') {
            if (selectedIds.value.length === 0) {
                ElMessage.error('请先选择题目')
                loading.value = false
                return
            }
            const payload = {
                questionIds: selectedIds.value,
                requirements: form.requirements,
                knowledge_preferences: form.knowledge_preferences
            }
            res = await generateSelectedStudentExercise(payload) as unknown as StudentExerciseResponse & { practiceId?: number }
        } else {
            res = await generateStudentExercise(form) as unknown as StudentExerciseResponse & { practiceId?: number }
        }
        exercises.value = res.data.exercises || []
        // 初始化答案结构
        answers.value = exercises.value.map(q => ({ questionId: q.id || 0, answer: '', type: q.type, correctAnswer: q.answer }))
        if(res.data.practiceId){
            practiceId.value = res.data.practiceId
        } else {
            practiceId.value = null
        }
    }
    catch (e: any) {
        ElMessage.error(e?.message || '生成失败')
    } 
    finally {
        loading.value = false
    }
}

async function saveProgress() {
    saveLoading.value = true
    try {
        localStorage.setItem('aiSelfTestProgress', JSON.stringify(answers.value))
        ElMessage.success('进度已保存')
    } catch (e) {
        ElMessage.error('保存失败')
    } finally {
        saveLoading.value = false
    }
}

const submitting = ref(false)
const scoreData = ref<any>({})
const showResults = ref(false)

async function submitPractice() {
    if (submitting.value) return
    submitting.value = true
    try {
        // Check if we have a valid practiceId
        if (!practiceId.value) {
            ElMessageBox.confirm(
                '检测到您正在提交没有保存的练习。这可能导致无法记录历史记录。是否继续提交？',
                '提交确认',
                {
                    confirmButtonText: '继续提交',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(async () => {
                // Continue with submission despite no practiceId
                await doSubmitAnswers()
            }).catch(() => {
                submitting.value = false
            })
        } else {
            // Normal submission with valid practiceId
            await doSubmitAnswers()
        }
    } catch (e: any) {
        ElMessage.error(e?.message || '提交失败')
        submitting.value = false
    }
}

// Extract actual submission logic to separate function
async function doSubmitAnswers() {
    const payload = {
        practiceId: practiceId.value || 0, // Use 0 as fallback for invalid practiceId
        answers: answers.value.map(a => ({
            ...a,
            type: a.type || 'singlechoice'
        }))
    }
    
    try {
        const res = await submitSelfPractice(payload)
        ElMessage.success('提交成功')
        
        // Update the results with the response data
        const data = res.data as any
        if (data) {
            scoreData.value = {
                totalScore: data.totalScore || 0,
                totalPossible: data.totalPossible || 0,
                details: data.details || []
            }
            showResults.value = true
        }
        
        // Clear localStorage progress
        localStorage.removeItem('aiSelfTestProgress')
    } catch (e: any) {
        ElMessage.error(e?.message || '提交失败')
    } finally {
        submitting.value = false
    }
}

// 题型标签映射（仅保留指定四种类型）
function typeLabel(type: string) {
    switch ((type || '').toLowerCase()) {
        case 'singlechoice':
            return '选择题'
        case 'fillblank':
            return '填空题'
        case 'judge':
            return '判断题'
        case 'program':
            return '简答题'
        default:
            return type || '题目'
    }
}

// 回到顶部（回到按钮组）
function scrollToBtnGroup() {
    nextTick(() => {
        if (buttonGroup.value) {
            // 兼容性更好，且能精确到按钮组
            buttonGroup.value.scrollIntoView({ behavior: 'smooth', block: 'center' })
        } else if (pageTop.value) {
            pageTop.value.scrollIntoView({ behavior: 'smooth' })
        } else {
            window.scrollTo({ top: 0, behavior: 'smooth' })
        }
    })
}

// 输入框 placeholder 根据题型变化
function inputPlaceholder(type: string) {
    switch ((type || '').toLowerCase()) {
        case 'judge':
            return "填写'正确'或'错误'"
        case 'singlechoice':
            return "例：A"
        default:
            return "请输入答案"
    }
}

function findQuestionContent(id: number) {
    const all = [...wrongList.value, ...favoriteList.value]
    const q = all.find((item: any) => (item.question_id ?? item.id) === id)
    return q?.content || ''
}
</script>

<style scoped>
.ai-selftest-page {
    max-width: 900px;
    margin: 0 auto;
    padding: 2rem 1rem;
}
.main-title {
    font-size: 2.3rem;
    font-weight: 800;
    text-align: center;
    margin-bottom: 2.2rem;
    letter-spacing: 1px;
    color: #1976d2;
}
.result-title {
    font-size: 1.5rem;
    font-weight: 700;
    margin-bottom: 1.2rem;
    color: #333;
    text-align: left;
}
.button-group {
    display: flex;
    gap: 10px;
    margin-bottom: 1.5rem;
}
.form-section {
    margin-bottom: 1.5rem;
}
.form-section label {
    display: block;
    font-weight: 500;
    margin-bottom: 0.5rem;
}
.form-label-lg {
    font-size: 1.18rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
    display: block;
}
textarea {
    width: 100%;
    min-height: 96px;
    padding: 0.75rem 1rem;
    border: 1px solid var(--border-color);
    border-radius: 6px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05) inset;
    resize: vertical;
    transition: border-color 0.2s ease, box-shadow 0.2s ease;
    background: var(--bg-secondary, #fafafa);
}
textarea:focus {
    outline: none;
    border-color: var(--primary, #1976d2);
    box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.15);
}
.btn {
    cursor: pointer;
    border: none;
    padding: 0.6rem 1.25rem;
    font-size: 0.95rem;
    border-radius: 6px;
    transition: background 0.15s ease, box-shadow 0.15s ease;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}
.btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
}
.btn.primary {
    background: var(--primary, #1976d2);
    color: #fff;
}
.btn.primary:hover:not(:disabled) {
    background: var(--primary-dark, #1565c0);
}
.btn.success {
    background: var(--success, #28a745);
    color: #fff;
}
.btn.success:hover:not(:disabled) {
    background: #218838;
}
.exercise-list {
    margin-top: 2rem;
}
.exercise-item {
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    margin-bottom: 1.5rem;
    padding: 1.25rem 1rem;
    border-radius: 8px;
}
.exercise-question {
    font-size: 1.18rem;
    font-weight: 600;
    margin-bottom: 0.5rem;
    display: flex;
    align-items: center;
    gap: 0.7em;
}
.type-tag {
    display: inline-block;
    font-size: 0.92rem;
    font-weight: 500;
    padding: 0.12em 0.8em;
    border-radius: 1em;
    margin-right: 0.5em;
    background: #e3f2fd;
    color: #1976d2;
    border: 1px solid #90caf9;
    vertical-align: middle;
    letter-spacing: 1px;
    white-space: nowrap; /* 保证type标签永远单行 */
}
.type-singlechoice {
    background: #e3f2fd;
    color: #1976d2;
    border-color: #90caf9;
}
.type-fillblank {
    background: #fffde7;
    color: #fbc02d;
    border-color: #ffe082;
}
.type-judge {
    background: #e8f5e9;
    color: #388e3c;
    border-color: #a5d6a7;
}
.type-program {
    background: #fbe9e7;
    color: #d84315;
    border-color: #ffab91;
}
.question-text {
    font-size: 1.13em;
    font-weight: 500;
    color: #222;
}
.exercise-item ul {
    padding-left: 1.25rem;
    margin-bottom: 0.75rem;
}
.answer-input {
    width: 100%;
    padding: 0.6rem 0.8rem;
    border: 1px solid var(--border-color);
    border-radius: 6px;
    transition: border-color 0.2s ease;
    background: var(--bg-secondary, #fafafa);
}
.answer-input:focus {
    outline: none;
    border-color: var(--primary, #1976d2);
}
.result-detail {
    margin-top: 0.6rem;
    font-size: 0.95rem;
}
.correct {
    color: var(--success, green);
}
.wrong {
    color: var(--error, red);
}
.total-score {
    font-size: 1.4rem;
    font-weight: 600;
    margin-top: 1.5rem;
    text-align: center;
}
.progress-wrapper {
    width: 100%;
    background: var(--bg-tertiary, #e0e0e0);
    height: 6px;
    margin-top: 1rem;
    overflow: hidden;
    border-radius: 3px;
}
.progress-bar {
    width: 100%;
    height: 100%;
    background: var(--primary, #1976d2);
    animation: progress 1.2s linear infinite;
    transform-origin: left;
}
@keyframes progress {
    0% { transform: translateX(-100%); }
    100% { transform: translateX(100%); }
}
.error-msg {
    color: var(--error, red);
    margin-top: 1rem;
    text-align: center;
}
.correct-ans {
    color: #888;
    font-size: 0.9rem;
    margin-top: 0.25rem;
}
.mode-select {
    padding: 0.5rem 0.75rem;
    border: 1px solid var(--border-color);
    border-radius: 6px;
}

.styled-tabs {
    display: flex;
    gap: 12px;
    margin-bottom: 0;
}

.tab-btn {
    display: flex;
    align-items: center;
    gap: 0.4em;
    padding: 0.32em 1em;
    font-size: 1rem;
    font-weight: 500;
    border: 1px solid var(--border-color, #e0e0e0);
    border-radius: 16px;
    background: #f8f9fa;
    color: #1976d2;
    cursor: pointer;
    transition: background 0.15s, color 0.15s, border 0.15s;
    box-shadow: none;
    outline: none;
}
.tab-btn .iconfont {
    font-size: 1.1em;
}
.tab-btn.active, .tab-btn:hover {
    background: #e3f2fd;
    color: #1565c0;
    border-color: #90caf9;
}
.tab-btn.active .iconfont,
.tab-btn:hover .iconfont {
    color: #1565c0;
}
.question-list {
    border: 1px solid var(--border-color, #e0e0e0);
    border-radius: 6px;
    padding: 0.5rem 0.5rem 0 0.5rem;
    background: #fff;
    margin-bottom: 0.5rem;
}
.question-list-header {
    /* 保持原有结构，去除多余样式 */
    margin-bottom: 0.5rem;
    padding-bottom: 0;
    border-bottom: none;
}
.question-list-title-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: 1.5rem;
}
.simple-tabs {
    display: flex;
    gap: 8px;
    margin-bottom: 0;
}
.question-list ul {
    list-style: none;
    padding: 0;
    max-height: 260px;
    overflow-y: auto;
}
.question-list li {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.4rem 0;
    border-bottom: 1px solid var(--border-color);
}
.add-btn {
    background: #1976d2;
    color: #fff;
    border: none;
    padding: 0.3rem 0.6rem;
    border-radius: 4px;
    cursor: pointer;
}
.add-btn[disabled] {
    background: #ccc;
    cursor: not-allowed;
}
.selected-info {
    margin-top: 0.5rem;
    font-weight: 600;
}

.selected-list {
    margin-top: 1rem;
    border: 1px solid var(--border-color, #e0e0e0);
    border-radius: 6px;
    padding: 0.5rem 0.5rem 0 0.5rem;
    background: #fff;
}
.selected-list h4 {
    font-size: 1.2rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
}
.selected-list ul {
    list-style: none;
    padding: 0;
    max-height: 180px;
    overflow-y: auto;
}
.selected-list li {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0.3rem 0;
    border-bottom: 1px dashed var(--border-color);
}
.remove-btn {
    background: #e53935;
    color: #fff;
    border: none;
    padding: 0.25rem 0.6rem;
    border-radius: 4px;
    cursor: pointer;
}
.option-list-no-dot {
    list-style: none;
    padding-left: 0;
    margin-bottom: 0.75rem;
}
.option-no-dot {
    list-style: none;
    padding-left: 0;
}
</style>
