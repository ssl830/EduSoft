<script setup lang="ts">
import {ref, reactive, onMounted, watch} from 'vue'
import { useRouter } from 'vue-router'
import ExerciseApi from '../../api/exercise'
import ClassApi from "../../api/class.ts";
import {useAuthStore} from "../../stores/auth.ts";
import { ElMessage } from 'element-plus';
import CourseApi from '../../api/course.ts';

const router = useRouter()
const authStore = useAuthStore()

interface Question {
  type: string;
  content: string;
  options?: Array<{ key: string; text: string }>;
  explanation?: string;
  answer?: string;
  points: number;
}

const exercise = reactive({
  title: '',
    courseId: 0,
  classId: 0,
  practiceId: 0,
  startTime: '',
  endTime: '',
    createdBy: authStore.user?.id,
    allowMultipleSubmission: true,
  questions: [] as Question[]
})

const loading = ref(false)
const error = ref('')
const classes = ref([])
const selectedQuestion = ref<Question | null>(null)
const isEditingQuestion = ref(false)
const currentStep = ref(1) // 1: Basic Info, 2: Questions
const allowMultipleSubmission = ref([
    { id: true, name: '是' },
    { id: false, name: '否' }
])
const tempSectionId = ref(0)
// Temporary question data for editing
// 修改tempQuestion的响应式对象
const tempQuestion = reactive({
    type: 'single_choice',
    content: '',
    options: [
        { key: 'A', text: '' },
        { key: 'B', text: '' },
        { key: 'C', text: '' },
        { key: 'D', text: '' }
    ],
    explanation: '',
    points: 5,
    // 使用计算属性处理多选答案
    get answerArray(): string[] {
        return this.type === 'multiple_choice' && this.answer
            ? this.answer.split(',')
            : [];
    },
    set answerArray(values: string[]) {
        this.answer = values.join(',');
    },
    answer: '' // 始终保持字符串类型
});

// 新增：章节相关状态
const sections = ref<{ id: number; title: string }[]>([]);

// 新增：获取课程章节
const fetchSections = async (courseId: number) => {
    if (!courseId) {
        sections.value = [];
        return;
    }
    try {
        const res = await CourseApi.getCourseById(String(courseId));
        // 假设返回结构为 res.data.data.sections
        sections.value = res.data.data.sections || [];
    } catch (err) {
        sections.value = [];
        console.error('获取章节失败', err);
    }
};

const fetchClasses = async () => {
    if (authStore.isAuthenticated) {
        try {
            const response = await ClassApi.getUserClasses(authStore.user?.id)
            console.log(authStore.user?.id)
            console.log(response.data.classes)
            classes.value = response.data.data
        } catch (err) {
            error.value = '获取班级列表失败，请稍后再试'
            console.error(err)
        } finally {
            loading.value = false
        }
    } else {
        loading.value = false
    }
}

// Fetch classes on component mount
onMounted(() => {
    fetchClasses()
})

// TODO
// // Fetch classes when a course is selected
// const fetchClasses = async (courseId: string) => {
//   try {
//     const response = await ExerciseApi.getCourseClasses(courseId)
//     classes.value = response.data
//     exercise.classIds = []
//   } catch (err) {
//     error.value = '获取班级列表失败'
//     console.error(err)
//   }
// }

// Move to the next step in the exercise creation process
const nextStep = async () => {
    if (currentStep.value === 1) {
        // Validate first step
        if ((exercise.endTime <= exercise.startTime && exercise.endTime != '' && exercise.startTime != '') && (!exercise.title || !exercise.classId)) {
            error.value = '截止时间应大于开始时间；请填写所有必填字段'
            return;
        } else if (exercise.endTime <= exercise.startTime && exercise.endTime != '' && exercise.startTime != '') {
            error.value = '截止时间应大于开始时间'
            return;
        } else if (!exercise.title || !exercise.classId) { //  || exercise.classIds.length === 0 TODO:
            error.value = '请填写所有必填字段'
            return
        }

        loading.value = true;
        try {
            const response = await ExerciseApi.createExercise({
                title: exercise.title,
                classId: exercise.classId,
                startTime: exercise.startTime,
                endTime: exercise.endTime,
                createdBy: authStore.user?.id,
                allowMultipleSubmission: exercise.allowMultipleSubmission,
            });

            // 存储练习ID
            exercise.practiceId = response.data.data.practiceId;
            currentStep.value = 2;
            error.value = '';
            // console.log(response.data.data.practiceId)
            // console.log(exercise.practiceId);
            // 获取题库题目
            await fetchRepoQuestions();
        } catch (err) {
            error.value = '创建练习失败，请稍后再试';
            console.error(err);
        } finally {
            loading.value = false;
        }
        return;
    }

    // Submit the exercise (last step)
    submitExercise()
}

// Go back to the previous step
const prevStep = () => {
  currentStep.value = 1
  error.value = ''
}

// Add a new question or update an existing one
const addOrUpdateQuestion = async () => {
    // Validate question data
    if (!tempQuestion.content || tempQuestion.points < 0) {
        error.value = '请完成题目内容并设置分值'
        return
    }

    // Validate options for choice questions
    if (['single_choice', 'multiple_choice'].includes(tempQuestion.type)) {
        const hasEmptyOption = tempQuestion.options.some(opt => !opt.text)
        if (hasEmptyOption) {
            error.value = '请填写所有选项内容'
            return
        }

        if (!tempQuestion.answer) {
            error.value = '请设置正确答案'
            return
        }
    }

    // 处理多选题答案格式
    if (tempQuestion.type === 'multiple_choice') {
        if (!tempQuestion.answerArray.length) {
            error.value = '请设置正确答案';
            return;
        }
    }

    if(tempSectionId.value === 0) {
        error.value = '请选择所属章节';
        return;
    }

    // const questionCopy = JSON.parse(JSON.stringify(tempQuestion))
    //
    // if (isEditingQuestion.value && selectedQuestion.value) {
    //     // Update existing question
    //     const index = exercise.questions.indexOf(selectedQuestion.value)
    //     if (index !== -1) {
    //         exercise.questions[index] = questionCopy
    //     }
    // } else {
    //     // Add new question
    //     exercise.questions.push(questionCopy)
    // }

    // Reset form and state
    try {
        // 创建题目到题库
        const questionData = {
            type: tempQuestion.type,
            content: tempQuestion.content,
            options: tempQuestion.options,
            answer: tempQuestion.type === 'multiple_choice'
                ? tempQuestion.answerArray.join(',')
                : tempQuestion.answer,
            analysis: tempQuestion.explanation,
            courseId: exercise.courseId, // 使用练习的课程ID
            sectionId: tempSectionId.value, // 绑定的章节ID
            creatorId: authStore.user?.id
        };

        await QuestionApi.createQuestion(questionData);
        // const questionId = createResponse.data.data.questionId;

        // // 将题目添加到当前练习
        // await ExerciseApi.importQuestionsToPractice({
        //     practiceId: exercise.practiceId,
        //     questions: [questionId],
        //     scores: [tempQuestion.points]
        // });
        //
        // // 添加到本地列表
        // const questionCopy = {
        //     ...JSON.parse(JSON.stringify(tempQuestion)),
        //     id: questionId
        // };
        //
        // if (isEditingQuestion.value && selectedQuestion.value) {
        //     const index = exercise.questions.indexOf(selectedQuestion.value);
        //     if (index !== -1) {
        //         exercise.questions[index] = questionCopy;
        //     }
        // } else {
        //     exercise.questions.push(questionCopy);
        // }
        await fetchRepoQuestions();
        error.value = '';
        ElMessage.success('题目创建成功');
    } catch (err) {
        error.value = '题目创建失败，请稍后再试';
        console.error(err);
    }

    resetQuestionForm();
}
//
// // Edit an existing question
// // 修改编辑题目逻辑
// const editQuestion = (question: Question) => {
//     selectedQuestion.value = question;
//     isEditingQuestion.value = true;
//
//     // 处理答案格式转换
//     const copiedQuestion = JSON.parse(JSON.stringify(question));
//     if (question.type === 'multiple_choice') {
//         copiedQuestion.answerArray = question.answer?.split(',') || [];
//     }
//     Object.assign(tempQuestion, copiedQuestion);
// };

// Remove a question
const removeQuestion = (question: Question) => {
  const index = exercise.questions.indexOf(question)
  if (index !== -1) {
    exercise.questions.splice(index, 1)
  }

  // If editing this question, reset the form
  if (selectedQuestion.value === question) {
    resetQuestionForm()
  }
}

// Reset the question form
const resetQuestionForm = () => {
  tempQuestion.type = 'single_choice'
  tempQuestion.content = ''
  tempQuestion.options = [
    { key: 'A', text: '' },
    { key: 'B', text: '' },
    { key: 'C', text: '' },
    { key: 'D', text: '' }
  ]
  tempQuestion.answer = tempQuestion.type === 'multiple_choice' ? [] : ''
  tempQuestion.points = 5

  selectedQuestion.value = null
  isEditingQuestion.value = false
}

// Add an option for choice questions
const addOption = () => {
  const nextKey = String.fromCharCode(65 + tempQuestion.options.length)
  tempQuestion.options.push({ key: nextKey, text: '' })
}

// Remove an option
const removeOption = (index: number) => {
  if (tempQuestion.options.length > 2) {
    tempQuestion.options.splice(index, 1)

    // Re-key the options
    tempQuestion.options.forEach((opt, i) => {
      opt.key = String.fromCharCode(65 + i)
    })
  }
}

// Submit the exercise to the server
const submitExercise = async () => {
  // Validate exercise data
  if (exercise.questions.length === 0) {
    error.value = '请至少添加一道题目'
    return
  }

  loading.value = true
  error.value = ''

  try {
    await ExerciseApi.createExercise(exercise)
    router.push('/') // or to a success page
  } catch (err) {
    error.value = '创建练习失败，请稍后再试'
    console.error(err)
  } finally {
    loading.value = false
  }
}
// 添加题目类型变化的watch
watch(() => tempQuestion.type, (newType) => {
    tempQuestion.answer = newType === 'multiple_choice' ? [] : '';
});

// 新增：导入QuestionApi
import QuestionApi from '../../api/question.ts';

// 新增：题库题目相关状态
const repoQuestions = ref<any[]>([]);
const repoLoading = ref(false);
const repoError = ref('');
const showRepoDetailDialog = ref(false);
const selectedRepoQuestion = ref<any>(null);

// 新增：获取题库题目
const fetchRepoQuestions = async () => {
    if (!exercise.courseId) {
        repoError.value = '请先选择课程';
        return;
    }
    repoLoading.value = true;
    repoError.value = '';
    try {
        const response = await QuestionApi.getQuestionList({
            courseId: exercise.courseId
        });
        repoQuestions.value = response.data.data.questions;
    } catch (err) {
        repoError.value = '获取题库题目失败，请稍后再试';
        console.error(err);
    } finally {
        repoLoading.value = false;
    }
};

// 新增：当进入题目添加步骤且课程已选择时，获取题库题目
watch(() => currentStep.value, (newVal) => {
    if (newVal === 2 && exercise.classId) {
        fetchRepoQuestions();
    }
});

// 新增：查看题目详情
const showRepoQuestionDetail = (question: any) => {
    selectedRepoQuestion.value = question;
    showRepoDetailDialog.value = true;
};

// 替换 selectedRepoScore 为 repoScores
const repoScores = ref<Record<number, number>>({});

// 新增：添加题目到练习
const addRepoQuestion = async (question: any) => {
    const score = repoScores.value[question.id];
    if (!score || score <= 0) {
        error.value = '分值必须大于0';
        return;
    }

    try {
        await ExerciseApi.importQuestionsToPractice({
            practiceId: exercise.practiceId,
            questions: [question.id],
            scores: [score]
        });

        const newQuestion: Question = {
            id: question.id,
            type: question.type,
            content: question.name,
            options: question.options,
            explanation: '',
            points: score,
            answer: question.answer
        };

        exercise.questions.push(newQuestion);
        ElMessage.success('题目添加成功');
        error.value = '';
    } catch (err) {
        error.value = '添加题目失败，请稍后再试';
        console.error(err);
    }
};

// 新增：格式化答案显示
const formatRepoAnswer = (question: any) => {
    if (question.type === 'multiple_choice') {
        return question.answer.split(',').join(', ');
    }
    return question.answer;
};

// 修改班级选择逻辑，选择班级时同步 courseId 和 classId，并拉取章节
const handleClassChange = (classId: number) => {
    const aclass = classes.value.find((c: any) => c.id === classId);
    if (aclass) {
        exercise.classId = aclass.id;
        exercise.courseId = aclass.courseId;
        fetchSections(aclass.courseId);
    } else {
        exercise.classId = 0;
        exercise.courseId = 0;
        sections.value = [];
    }
};
</script>

<template>
  <div class="exercise-create-container">
    <h1 class="page-title">{{ currentStep === 1 ? '创建在线练习 - 基本信息' : '创建在线练习 - 添加题目' }}</h1>

    <div v-if="error" class="error-message">{{ error }}</div>

    <!-- Step 1: Basic Exercise Information -->
    <div v-if="currentStep === 1" class="form-step">
      <div class="form-group">
        <label for="title">练习标题</label>
        <input
          id="title"
          v-model="exercise.title"
          type="text"
          placeholder="输入练习标题"
          required
        />
      </div>

      <div class="form-row">
          <div class="form-group form-group-half">
            <label for="courseId">所属班级</label>
            <select
              id="courseId"
              v-model="exercise.classId"
              @change="handleClassChange(Number(exercise.classId))"
              required
            >
              <option value="" disabled>选择课程</option>
              <option v-for="aclass in classes" :key="aclass.id" :value="aclass.id">
                {{ aclass.courseName }} {{ aclass.className }}
              </option>
            </select>
          </div>
          <div class="form-group form-group-half">
              <label for="allowMultipleSubmission">是否允许多次提交</label>
              <!--            TODO:选择是否（boolean）-->
              <select
                  id="allowMultipleSubmission"
                  v-model="exercise.allowMultipleSubmission"
                  required
              >
                  <option value="" disabled>请选择</option>
                  <option v-for="type in allowMultipleSubmission" :key="type.id" :value="type.id">
                      {{ type.name }}
                  </option>
              </select>
          </div>
      </div>

      <div class="form-row">
        <div class="form-group form-group-half">
          <label for="startTime">开始时间</label>
          <input
            id="startTime"
            v-model="exercise.startTime"
            type="datetime-local"
          />
        </div>

        <div class="form-group form-group-half">
          <label for="endTime">截止时间</label>
          <input
            id="endTime"
            v-model="exercise.endTime"
            type="datetime-local"
          />
        </div>
      </div>

    </div>

    <!-- Step 2: Question Management -->
    <div v-else-if="currentStep === 2" class="form-step">
      <!-- Question List -->
      <div class="question-list-section">
        <h2>题目列表 ({{ exercise.questions.length }})</h2>

        <div v-if="exercise.questions.length === 0" class="empty-state">
          尚未添加题目，请使用下方表单添加题目
        </div>

        <div v-else class="question-list">
          <div
            v-for="(question, index) in exercise.questions"
            :key="index"
            class="question-list-item"
          >
            <div class="question-list-header">
              <span class="question-number">{{ index + 1 }}</span>
              <span class="question-type-badge" :class="question.type">
                {{
                  question.type === 'single_choice' ? '单选题' :
                  question.type === 'multiple_choice' ? '多选题' :
                  question.type === 'true_false' ? '判断题' :
                  question.type === 'short_answer' ? '简答题' : '填空题'
                }}
              </span>
              <span class="question-points">{{ question.points }}分</span>
            </div>

            <div class="question-list-content">
              <div v-html="question.content"></div>
            </div>

            <div class="question-list-actions">
<!--              <button-->
<!--                class="btn-secondary btn-sm"-->
<!--                @click="editQuestion(question)"-->
<!--              >-->
<!--                编辑-->
<!--              </button>-->
              <button
                class="btn-danger btn-sm"
                @click="removeQuestion(question)"
              >
                删除
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Question Form -->
      <div class="question-form-byhand-section">
<!--          isEditingQuestion ? '编辑题目' : -->
        <h2>{{ '手动添加题目' }}</h2>
        <!-- 新增：选择章节 -->
        <div class="form-group">
          <label for="sectionId">所属章节</label>
          <select id="sectionId" v-model="tempSectionId">
            <option value="">请选择章节</option>
            <option v-for="section in sections" :key="section.id" :value="section.id">
              {{ section.title }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label for="questionType">题目类型</label>
          <select id="questionType" v-model="tempQuestion.type">
            <option value="single_choice">单选题</option>
            <option value="multiple_choice">多选题</option>
            <option value="true_false">判断题</option>
            <option value="short_answer">简答题</option>
            <option value="fill_blank">填空题</option>
          </select>
        </div>

        <div class="form-group">
          <label for="questionContent">题目内容</label>
          <textarea
            id="questionContent"
            v-model="tempQuestion.content"
            rows="4"
            placeholder="输入题目内容"
          ></textarea>
        </div>

        <!-- Options for choice questions -->
        <div v-if="['single_choice', 'multiple_choice'].includes(tempQuestion.type)" class="form-group">
          <label>选项</label>
          <div
            v-for="(option, index) in tempQuestion.options"
            :key="index"
            class="option-row"
          >
            <div class="option-key">{{ option.key }}</div>
            <input
              v-model="option.text"
              type="text"
              :placeholder="`选项${option.key}内容`"
              class="option-input"
            />
            <button
              type="button"
              class="btn-icon"
              @click="removeOption(index)"
              :disabled="tempQuestion.options.length <= 2"
            >
              ✕
            </button>
          </div>

          <button
            type="button"
            class="btn-text"
            @click="addOption"
          >
            + 添加选项
          </button>
        </div>

        <!-- Answer for choice questions -->
        <div v-if="tempQuestion.type === 'single_choice'" class="form-group">
          <label for="singleAnswer">正确答案</label>
          <select id="singleAnswer" v-model="tempQuestion.answer">
            <option value="" disabled>选择正确答案</option>
            <option
              v-for="option in tempQuestion.options"
              :key="option.key"
              :value="option.key"
            >
              {{ option.key }}
            </option>
          </select>
        </div>

          <div v-if="tempQuestion.type === 'multiple_choice'" class="form-group">
              <label>正确答案 (多选)</label>
              <div class="checkbox-group">
                  <div
                      v-for="option in tempQuestion.options"
                      :key="option.key"
                      class="checkbox-item"
                  >
                      <input
                          :id="`answer-${option.key}`"
                          type="checkbox"
                          v-model="tempQuestion.answerArray"
                          :value="option.key"
                      />
                      <label :for="`answer-${option.key}`">{{ option.key }}</label>
                  </div>
              </div>
          </div>
        <div v-if="tempQuestion.type === 'true_false'" class="form-group">
          <label for="truefalseAnswer">正确答案</label>
          <select id="truefalseAnswer" v-model="tempQuestion.answer">
            <option value="" disabled>选择正确答案</option>
            <option value="True">正确</option>
            <option value="False">错误</option>
          </select>
        </div>
        <div class="form-group">
          <label for="explanation">答案解析</label>
          <textarea
              id="explanation"
              v-model="tempQuestion.explanation"
              rows="4"
              placeholder="输入答案解析"
          ></textarea>
        </div>
        <div class="form-group">
          <label for="questionPoints">分值</label>
          <input
            id="questionPoints"
            v-model.number="tempQuestion.points"
            type="number"
            min="1"
          />
        </div>

        <div class="form-actions">
          <button
            type="button"
            class="btn-secondary"
            @click="resetQuestionForm"
          >
            取消
          </button>
          <button
            type="button"
            class="btn-primary"
            @click="addOrUpdateQuestion"
          >
            {{ isEditingQuestion ? '更新题目' : '添加题目' }}
          </button>
        </div>
      </div>
        <div class="question-form-fromrepo-section">
<!--            isEditingQuestion ? '编辑题目' : -->
            <h2>{{ '从题库中选择题目' }}</h2>

            <div v-if="repoError" class="error-message">{{ repoError }}</div>

            <div v-if="repoLoading" class="loading-container">加载中...</div>
            <div v-else-if="repoQuestions.length === 0" class="empty-state">
                题库中暂无题目
            </div>
            <div v-else class="resource-table-wrapper">
                <table class="resource-table">
                    <thead>
                    <tr>
                        <th>题目内容</th>
                        <th>所属章节</th>
                        <th>分值</th>
                        <th>操作</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="question in repoQuestions" :key="question.id">
                        <td>{{ question.name }}</td>
                        <td>{{ question.sectionName || '-' }}</td>
                        <td>
                            <input
                                v-model.number="repoScores[question.id]"
                                type="number"
                                min="1"
                                placeholder="输入分值"
                                class="score-input"
                            />
                        </td>
                        <td class="actions">
                            <button class="btn-action preview" @click="showRepoQuestionDetail(question)">查看</button>
                            <button class="btn-action add" @click="addRepoQuestion(question)">添加</button>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
        </div>

        <!-- 新增：题目详情弹窗 -->
        <div v-if="showRepoDetailDialog" class="modal-mask">
            <div class="modal-container">
                <div class="modal-header">
                    <h3>题目详情</h3>
                    <button class="modal-close" @click="showRepoDetailDialog = false">&times;</button>
                </div>

                <div class="modal-body" v-if="selectedRepoQuestion">
                    <div class="detail-row">
                        <label>课程名称:</label>
                        <span>{{ selectedRepoQuestion.course_name || '-' }}</span>
                    </div>
                    <div class="detail-row">
                        <label>章节标题:</label>
                        <span>{{ selectedRepoQuestion.section_name || '-' }}</span>
                    </div>
                    <div class="detail-row">
                        <label>题目类型:</label>
                        <span>{{
                                selectedRepoQuestion.type === 'single_choice' ? '单选题' :
                                    selectedRepoQuestion.type === 'multiple_choice' ? '多选题' :
                                        selectedRepoQuestion.type === 'true_false' ? '判断题' :
                                            selectedRepoQuestion.type === 'short_answer' ? '简答题' : '填空题'
                            }}</span>
                    </div>
                    <div class="detail-row">
                        <label>题目内容:</label>
                        <div class="content-box">{{ selectedRepoQuestion.name }}</div>
                    </div>

                    <!-- 选项展示 -->
                    <div
                        v-if="['single_choice', 'multiple_choice'].includes(selectedRepoQuestion.type)"
                        class="detail-row"
                    >
                        <label>题目选项:</label>
                        <div class="options-list">
                            <div
                                v-for="(opt, index) in selectedRepoQuestion.options"
                                :key="index"
                                class="option-item"
                            >
                                <span class="option-key">{{ opt.key }}.</span>
                                <span class="option-text">{{ opt.text }}</span>
                                <span
                                    v-if="selectedRepoQuestion.answer.includes(opt.key)"
                                    class="correct-badge"
                                >
                                    ✓
                                </span>
                            </div>
                        </div>
                    </div>

                    <div class="detail-row">
                        <label>正确答案:</label>
                        <span class="answer-text">{{ formatRepoAnswer(selectedRepoQuestion) }}</span>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Step Navigation Buttons -->
    <div class="step-navigation">
        <button
            type="button"
            class="btn-primary"
            @click="nextStep"
            :disabled="loading"
        >
            {{ currentStep === 1 ? '创建练习' : '完成' }}
            <span v-if="loading">...</span>
        </button>
    </div>
  </div>
</template>

<style scoped>
/* 新增：分值输入框样式 */
.score-input {
    width: 80px;
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
}

.exercise-create-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 1.5rem;
}

.page-title {
  margin-bottom: 1.5rem;
  padding-bottom: 0.75rem;
  border-bottom: 1px solid #e0e0e0;
}

.form-step {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.25rem;
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
  transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  border-color: #2c6ecf;
  outline: none;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-group-half {
  flex: 1;
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

.step-navigation {
  display: flex;
  justify-content: space-between;
  margin-top: 1.5rem;
}

.btn-primary, .btn-secondary, .btn-danger {
  padding: 0.75rem 1.5rem;
  border-radius: 4px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  transition: background-color 0.2s;
}

.btn-sm {
  padding: 0.375rem 0.75rem;
  font-size: 0.875rem;
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
  color: #333;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

.btn-danger {
  background-color: #f44336;
  color: white;
}

.btn-danger:hover {
  background-color: #d32f2f;
}

.btn-icon {
  background: none;
  border: none;
  cursor: pointer;
  color: #757575;
  font-size: 1rem;
  padding: 0.5rem;
}

.btn-icon:hover {
  color: #d32f2f;
}

.btn-icon:disabled {
  color: #bdbdbd;
  cursor: not-allowed;
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

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 0.75rem;
  border-radius: 4px;
  margin-bottom: 1.25rem;
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

/* 新增：操作按钮样式 */
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

.btn-action.add {
    background-color: #e8f5e9;
    color: #2e7d32;
}

.btn-action.add:hover {
    background-color: #c8e6c9;
}

/* 新增：表格样式 */
.resource-table-wrapper {
    overflow-x: auto;
    margin-top: 1rem;
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

/* 新增：加载状态样式 */
.loading-container {
    display: flex;
    justify-content: center;
    padding: 2rem;
    color: #616161;
}
</style>
