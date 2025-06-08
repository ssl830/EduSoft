<template>
    <div class="feedback-container">
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <div v-else>
            <div class="header">
                <h1>{{ practiceData?.title }} - 练习反馈</h1>
                <div class="total-score">总分：{{ totalScore }}</div>
<!--                &lt;!&ndash; 新增操作按钮 &ndash;&gt;-->
<!--                <div class="feedback-actions">-->

<!--                </div>-->
            </div>

            <div class="questions-list2">                <div v-for="(question, index) in practiceData?.questions" :key="question.id" class="question-item">
                    <div class="question-header2">
                        <h3>题目 {{ index + 1 }} {{ question.content || question.name }}</h3>
                        <div style="display: flex; gap: 8px;">
                            <button
                                @click="toggleFavorite(question)"
                                class="favorite-btn"
                                :class="{ 'favorited': question.isFavorited }"
                            >
                                ★
                            </button>
                            <!-- 新增：添加到错题集按钮 -->
                            <button
                                v-if="question.answer != studentAnswers[question.id]"
                                class="btn-action preview"
                                :class="{ 'added': question.isadded }"
                                style="font-size: 1rem; padding: 0 10px;"
                                @click="addToWrongSet(question)"
                            >
                                添加
                            </button>
                        </div>
                    </div>

                    <div class="question-content2">
                        <div class="question-type2">{{ questionTypeMap[question.type] }}</div>
                        <div class="question-points">分值：{{ question.score || question.points }}分</div>

                        <!-- 客观题展示 -->
                        <template v-if="isObjective(question.type)">
                            <div class="answer-section">
                                <div>标准答案：{{ formatAnswer(question.answer, question.type) }}</div>
                                <div>你的答案：{{ formatAnswer(studentAnswers[question.id], question.type) }}</div>
                            </div>
                        </template>

                        <!-- 主观题展示 -->
                        <template v-else>
                            <div class="answer-section">
                                <div>你的答案：{{ studentAnswers[question.id] || '未回答' }}</div>
                            </div>
                        </template>

                        <div v-if="question.explanation" class="explanation">
                            解析：{{ question.explanation }}
                        </div>
                    </div>
                </div>
            </div>
            <button
                style="margin-top: 20px; margin-bottom: 20px;"
                class="btn-primary"
                @click="() => router.push('/')"
            >
                返回
            </button>
            <button class="btn-primary" @click="handleViewRecord" style="margin-left: 10px; margin-right: 10px;">
                查看记录
            </button>
            <button class="btn-primary" @click="handleDownloadReport" :disabled="downloadStatus.loading">
                {{ downloadStatus.loading ? '下载中...' : '导出报告' }}
            </button>
        </div>
        <!-- 记录详情弹窗 -->
        <div v-if="submissionReportModal.visible" class="modal-overlay" @click="closeSubmissionReportModal">
            <div class="submission-report-modal" @click.stop>
                <div class="modal-header">
                    <h3>练习记录详情</h3>
                    <button class="modal-close" @click="closeSubmissionReportModal">&times;</button>
                </div>
                <div class="modal-body">
                    <div v-if="submissionReportModal.loading" class="modal-loading">
                        <div class="loading-spinner"></div>
                        <p>正在加载练习记录...</p>
                    </div>
                    <div v-else-if="submissionReportModal.error" class="modal-error">
                        <i class="fa fa-exclamation-triangle"></i>
                        <p>{{ submissionReportModal.error }}</p>
                        <button @click="closeSubmissionReportModal" class="btn btn-primary">关闭</button>
                    </div>
                    <div v-else-if="!submissionReportModal.data" class="modal-empty">
                        <i class="fa fa-info-circle"></i>
                        <p>暂无数据显示</p>
                        <button @click="closeSubmissionReportModal" class="btn btn-primary">关闭</button>
                    </div>
                    <div v-else-if="submissionReportModal.data" class="submission-report-content">
                        <!-- 详情内容可根据需要精简 -->
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
                                        <span class="question-type">{{ questionTypeMap[question.type] }}</span>
                                        <span class="question-score" :class="{ 'full-score': question.isCorrect }">
                                            {{ question.score || 0 }}分
                                        </span>
                                    </div>
                                    <div class="question-content">
                                        <p><strong>题目:</strong> {{ question.content }}</p>
                                        <p v-if="question.options && question.type === 'singlechoice'"><strong>选项:</strong> {{ question.options }}</p>
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
                </div>
                <div class="modal-footer">
                    <button @click="closeSubmissionReportModal" class="btn btn-secondary">关闭</button>
                    <button v-if="submissionReportModal.data && submissionReportModal.data.submissionInfo" @click="handleDownloadReport" class="btn btn-primary">
                        <i class="fa fa-download"></i> 导出报告
                    </button>
                </div>
            </div>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import {useRoute, useRouter} from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import ExerciseApi from "../../api/exercise.ts";
import QuestionApi from '../../api/question'
// 新增
import StudyRecordsApi from '../../api/studyRecords'

const router = useRouter()

export interface PracticeDetail {
    title: string;
    code: number;
    message: string;
    courseld: number;
    startTime: string;
    endTime: string;
    timelimit: number;
    allowedAttempts: boolean;
    questions: Question[];
}

export interface Question {
    id: number;
    name: string;
    content?: string;
    course_id: number;
    teacher_id: string;
    type: 'singlechoice' | 'multiplechoice' | 'judge' | 'program' | 'fillblank';
    options: Option[];
    answer: string;
    points: number;
    score?: number;
    explanation?: string;
    isFavorited?: boolean;
    studentAnswer?: string | string[];
    isadded: boolean;
    correctAnswer?: string;
}

interface Option {
    key: string;
    text: string;
}

const route = useRoute()
const practiceId = route.params.practiceId as string
const submissionId = route.params.submissionId as string

const practiceData = ref<PracticeDetail | null>(null)
const studentAnswers = ref<Record<number, string | string[]>>({})
const loading = ref(true)
const error = ref('')

// 获取 answerList（兼容刷新和直接访问）
let answerList: string[] | undefined = undefined
try {
    // vue-router@4: history.state
    answerList = window.history.state?.answerList
} catch (e) {
    answerList = undefined
}

// 题目类型映射
const questionTypeMap: Record<string, string> = {
    'singlechoice': '单选题',
    'multiplechoice': '多选题',
    'judge': '判断题',
    'program': '简答题',
    'fillblank': '填空题'
}

// 计算总分
const totalScore = computed(() => {
    if (!practiceData.value) return 0
    return practiceData.value.questions.reduce((sum, q) => sum + (q.score || q.points), 0)
})

// 判断是否客观题
const isObjective = (type: string) => {
    return ['singlechoice', 'multiplechoice', 'judge', 'fillblank'].includes(type)
}

// 格式化答案显示
const formatAnswer = (answer: string | string[], type: string) => {
    if (Array.isArray(answer)) return answer.join(', ')
    if (type === 'true_false') return answer === 'True' ? '正确' : '错误'
    return answer
}

// 切换收藏状态
const toggleFavorite = async (question: Question) => {
    const originalState = question.isFavorited;
    try {
        // 先切换本地状态提供即时反馈
        question.isFavorited = !originalState;

        // 根据新状态调用对应接口
        const apiMethod = question.isFavorited
            ? ExerciseApi.favouriteQuestions
            : ExerciseApi.enFavouriteQuestions;        const response: any = await apiMethod(String(question.id));

        if (response.code !== 200) {
            // 请求失败时回滚状态
            question.isFavorited = originalState;
            console.error('操作失败:', response.message);
        }
    } catch (err) {
        // 请求异常时回滚状态
        question.isFavorited = originalState;
        console.error('收藏操作失败:', err);
    }
}

// 添加到错题集
const addToWrongSet = async (question: Question) => {
    try {
        const answerValue = studentAnswers.value[question.id];
        const wrongAnswer = Array.isArray(answerValue) ? answerValue.join(',') : (answerValue || '');
        
        const res: any = await QuestionApi.addWrongQuestion(
            question.id,
            {
                wrongAnswer: wrongAnswer
            }
        )
        console.log(res)
        if (res.code === 200) {
            console.log("YESSSSS")
            question.isadded = true
        } else {
            console.log("NOOOOOO")
        }
    } catch (e) {
        console.error('添加到错题集失败:', e)
    }
}

// 获取练习详情
const fetchPracticeDetail = async () => {
    try {
        const response = await ExerciseApi.getExerciseDetails(practiceId, {submissionId})
        console.log(response.data)
        practiceData.value = response.data
        // 初始化学生答案（假设从API获取）
        practiceData.value?.questions.forEach((q: Question, idx: number) => {
            // 优先用 answerList 覆盖 studentAnswer
            if (answerList && answerList[idx] !== undefined) {
                q.studentAnswer = answerList[idx]
                studentAnswers.value[q.id] = answerList[idx]
            } else {
                studentAnswers.value[q.id] = q.studentAnswer || ''
            }
            if (q.type === 'singlechoice' && typeof q.answer === 'string' && q.answer.length > 1) {
                q.type = 'multiplechoice'
            }
        })
        console.log("final数据", practiceData.value)
    } catch (err) {
        error.value = '获取练习详情失败'
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchPracticeDetail()
})

// 新增：导出报告和查看记录相关状态
const downloadStatus = ref<{ loading: boolean; error: string | null }>({ loading: false, error: null })
const submissionReportModal = ref({
    visible: false,
    loading: false,
    error: null as string | null,
    data: null as any
})

// 新增：导出报告
const handleDownloadReport = async () => {
    try {
        downloadStatus.value.loading = true
        downloadStatus.value.error = null
        // submissionId 取自当前页面参数
        const submissionId = route.params.submissionId as string
        if (!submissionId) {
            downloadStatus.value.error = '未找到提交记录，无法导出报告'
            return
        }
        const response = await StudyRecordsApi.exportSubmissionReport(submissionId)
        if (!response || !(response instanceof Blob) || response.size === 0) {
            downloadStatus.value.error = '服务器返回的数据为空'
            return
        }
        const timestamp = new Date().toISOString().slice(0, 19).replace(/:/g, '-')
        const filename = `提交报告_${submissionId}_${timestamp}.pdf`
        const url = window.URL.createObjectURL(response)
        const link = document.createElement('a')
        link.href = url
        link.setAttribute('download', filename)
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        downloadStatus.value.error = null
    } catch (err: any) {
        downloadStatus.value.error = err?.message || '下载报告失败，请稍后再试'
    } finally {
        downloadStatus.value.loading = false
    }
}

// 新增：查看记录
const handleViewRecord = async () => {
    const submissionId = route.params.submissionId as string
    if (!submissionId) return
    try {
        submissionReportModal.value.visible = true
        submissionReportModal.value.loading = true
        submissionReportModal.value.error = null
        submissionReportModal.value.data = null
        const response = await StudyRecordsApi.getSubmissionReport(submissionId)
        let reportData
        if (response.data) {
            if (response.data.code === 200) {
                reportData = response.data.data
            } else {
                reportData = response.data
            }
            if (!reportData) throw new Error('API返回的数据为空')
            submissionReportModal.value.data = reportData;        
            submissionReportModal.value.data.questions.forEach((q: Question, _idx: number) => {
            if (q.type === 'singlechoice' && typeof q.correctAnswer === 'string' && q.correctAnswer.length > 1) {
                    q.type = 'multiplechoice'
                }
            })

        } else {
            throw new Error('API响应数据格式不正确')
        }
    } catch (err: any) {
        submissionReportModal.value.error = err?.message || '获取记录失败，请稍后再试'
    } finally {
        submissionReportModal.value.loading = false
    }
}
const closeSubmissionReportModal = () => {
    submissionReportModal.value.visible = false
    submissionReportModal.value.data = null
    submissionReportModal.value.error = null
}
</script>

<style scoped>
.feedback-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
}

.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 2rem;
}

.total-score {
    font-size: 1.5rem;
    color: #2196F3;
    font-weight: bold;
}

.feedback-actions {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-left: 2rem;
}

.questions-list2 {
    display: grid;
    gap: 2rem;
}

.question-item {
    padding: 1.5rem;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    background: white;
}

.question-header2 {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.favorite-btn {
    background: none;
    border: none;
    cursor: pointer;
    font-size: 1.5rem;
    color: #ccc;
    transition: color 0.2s;
}

.favorite-btn.favorited {
    color: #FFC107;
}

.question-content2 {
    display: grid;
    gap: 1rem;
}

.question-type2 {
    color: #666;
    font-size: 0.9rem;
    /* max-width: 10px; */
}

.question-points {
    color: #4CAF50;
    font-weight: bold;
}

.answer-section {
    padding: 1rem;
    background: #f5f5f5;
    border-radius: 4px;
}

.explanation {
    padding: 1rem;
    background: #E3F2FD;
    border-radius: 4px;
    margin-top: 1rem;
}

.loading, .error {
    text-align: center;
    padding: 2rem;
    font-size: 1.2rem;
}

.error {
    color: #D32F2F;
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
.btn-action.preview.added {
    background-color: #f5f5f5;
    color: #424242;
}

/* 新增样式 */
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
.modal-header {
    padding: 1.5rem 2rem;
    border-bottom: 1px solid #e0e0e0;
    display: flex;
    justify-content: space-between;
    align-items: center;
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    border-radius: 16px 16px 0 0;
}
.modal-header h3 {
    margin: 0;
    color: #2c6ecf;
    font-size: 1.4rem;
    font-weight: 600;
}
.modal-close {
    background: none;
    border: none;
    font-size: 1.8rem;
    color: #666;
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
    color: #f56c6c;
}
.modal-body {
    flex: 1;
    overflow-y: auto;
    padding: 0;
}
.modal-loading {
    text-align: center;
    padding: 3rem;
    color: #666;
}
.loading-spinner {
    width: 40px;
    height: 40px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #2c6ecf;
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
    color: #f56c6c;
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
    color: #2c6ecf;
    font-size: 1.1rem;
    font-weight: 600;
    display: flex;
    align-items: center;
    gap: 0.5rem;
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
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 3px solid #2c6ecf;
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
    background: #f8f9fa;
    border-radius: 12px;
    text-align: center;
    border: 1px solid #e0e0e0;
    transition: all 0.3s ease;
}
.score-main {
    background: #e1f5fe;
    border-color: #2c6ecf;
}
.score-label {
    font-size: 0.9rem;
    color: #666;
    font-weight: 500;
    margin-bottom: 0.5rem;
}
.score-value {
    font-size: 1.8rem;
    font-weight: bold;
    color: #2c6ecf;
    margin-bottom: 0.25rem;
}
.score-percentage {
    font-size: 1rem;
    color: #666;
    font-weight: 500;
}
.questions-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}
.question-item {
    border: 1px solid #e0e0e0;
    border-radius: 12px;
    overflow: hidden;
    transition: all 0.3s ease;
}
.question-item.correct {
    border-left: 4px solid #67c23a;
}
.question-item.incorrect {
    border-left: 4px solid #f56c6c;
}
.question-header {
    background: #f8f9fa;
    padding: 1rem 1.5rem;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e0e0e0;
}
.question-number {
    font-weight: bold;
    color: #2c6ecf;
}
.question-type {
    background: #e1f5fe;
    color: #2c6ecf;
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    font-size: 0.8rem;
    font-weight: 500;
    border: 1px solid #2c6ecf;
}
.question-score {
    font-weight: bold;
    padding: 0.25rem 0.75rem;
    border-radius: 20px;
    background: #fde2e2;
    color: #f56c6c;
    border: 1px solid #fbc4c4;
}
.question-score.full-score {
    background: #d4edda;
    color: #67c23a;
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
    color: #67c23a;
    font-weight: 600;
}
.wrong-answer {
    color: #f56c6c;
    font-weight: 600;
}
.question-analysis {
    margin-top: 1rem;
    padding: 1rem;
    background: #f8f9fa;
    border-radius: 8px;
    border-left: 3px solid #409eff;
}
.question-analysis strong {
    color: #409eff;
}
.modal-footer {
    padding: 1.5rem 2rem;
    border-top: 1px solid #e0e0e0;
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
    background: #f8f9fa;
    border-radius: 0 0 16px 16px;
}
.btn-secondary {
    background: #e9ecef;
    color: #666;
    border: 1px solid #e0e0e0;
}
.btn-secondary:hover {
    background: #dee2e6;
    color: #333;
}
</style>
