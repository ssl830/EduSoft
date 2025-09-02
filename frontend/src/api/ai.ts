import  http  from './axios'

export interface AiAskRequest {
  question: string;
  userId: string;
  context?: string;
}

export interface AiAskResponse {
  answer: string;
  sources: string[];
}

export interface TeachingContentRequest {
  courseOutline: string
  courseId: number
  courseName: string
  expectedHours: number
}

export interface LessonContent {
  title: string
  content: string
  practiceContent: string
  teachingGuidance: string
  suggestedHours: number
  knowledgeSources: string[]
}

export interface TeachingContentResponse {
  lessons: LessonContent[]
  totalHours: number
  timeDistribution: string
  teachingAdvice: string
}

export interface AssistantRequest {
  question: string
  courseName?: string
  chatHistory?: { role: 'user' | 'assistant'; content: string }[]
  sessionId?: string
}

export interface AssistantResponse {
  answer: string
  references: { source: string; content: string }[]
  knowledgePoints: string[]
}

export interface StudentExerciseRequest {
  requirements: string
  knowledge_preferences: string
  wrongQuestions?: any[]
}

export interface SelectedStudentExerciseRequest {
  questionIds: number[]
  requirements?: string
  knowledge_preferences?: string
}

export interface ExerciseItem {
  id?: number
  type: string
  question: string
  options: string[]
  answer: string
  explanation: string
  knowledge_points: string[]
}

export interface StudentExerciseResponse {
  data:{
    exercises: ExerciseItem[]
    practiceId?: number
  }
}

// ========== 自测练习相关 ==========
export interface SelfPracticeGenerateRequest {
  prompt: string
}

// 生成自测练习（保存到数据库）
export function generateSelfPracticeExercise(data: SelfPracticeGenerateRequest) {
  return http.post('/api/selfpractice/generate', data)
}

export interface SelfPracticeProgressRequest {
  practiceId: number
  answers: any[]
}

// AI问答
export function askAI(question: string) {
  return http.post('/api/ai/ask', { question })
}

// 生成教学内容
export function generateTeachingContent(request: TeachingContentRequest) {
  return http.post<TeachingContentResponse>('/api/ai/teaching/generate', request)
}

export function askQuestion(data: AiAskRequest) {
  return http.post<AiAskResponse>('/api/ai/ask', data);
}

// 在线学习助手
export function askAssistant(data: AssistantRequest) {
  return http.post<AssistantResponse>('/api/ai/rag/assistant', data)
}
// 评估主观题答案
export interface EvaluateSubjectiveRequest {
  question: string;
  student_answer: string;
  reference_answer: string;
  max_score: number;
}

export interface EvaluateSubjectiveResponse {
  score: number;
  analysis: any;
  feedback: string;
  improvement_suggestions: string[];
  knowledge_context: any[];
}

export function evaluateSubjectiveAnswer(data: EvaluateSubjectiveRequest) {
  return http.post<EvaluateSubjectiveResponse>('/api/ai/evaluate-subjective', data)
}

// 生成学生自测练习
export function generateStudentExercise(data: StudentExerciseRequest) {
  return http.post<StudentExerciseResponse>('/api/ai/rag/generate_student_exercise', data)
}

// 根据学生选择题目生成自测练习
export function generateSelectedStudentExercise(data: SelectedStudentExerciseRequest) {
  return http.post<StudentExerciseResponse>('/api/ai/rag/generate_selected_student_exercise', data)
}

export function saveSelfPracticeProgress(data: SelfPracticeProgressRequest) {
  return http.post('/api/selfpractice/save-progress', data)
}

export function submitSelfPractice(data: SelfPracticeProgressRequest) {
  return http.post('/api/selfpractice/submit', data)
}

// ========== 学生自测历史 ==========
export function getSelfPracticeHistory() {
  return http.get('/api/selfpractice/history')
}

export function getSelfPracticeDetail(pid: number | string) {
  return http.get(`/api/selfpractice/history/${pid}`)
}

// AI学情分析（练习得分率分析）
export function analyzeExercise(practiceId: number) {
  return http.post('/api/ai/analyze-exercise', { practiceId })
}
