<template>
    <div class="check-container">
        <div v-if="loading">加载中...</div>
        <div v-else-if="error" class="error-message">{{ error }}</div>
        <div >
            <div v-for="question in questions" :key="question.questionName" class="question-item">
                <h3>{{ question.questionName }}</h3>
                <p class="answer-text">学生答案：{{ question.answerText }}</p>
                <p>最大分值：{{ question.maxScore }}</p>
                <div class="score-input">
                    <label>
                        评分：
                        <input
                            type="number"
                            v-model.number="question.score"
                            :min="0"
                            :max="question.maxScore"
                            class="score-field"
                        />
                    </label>
                </div>
            </div>
            <div class="total-score">总成绩：{{ totalScore }}</div>
            <button @click="submitScores" class="submit-button">提交评分</button>
        </div>
    </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue';
import ExerciseApi from "../../api/exercise.ts";
import Exercise from "../../api/exercise.ts";
import {useRoute, useRouter} from "vue-router";

interface QuestionItem {
    questionName: string;
    answerText: string;
    maxScore: number;
    score: number
}
const route = useRoute()
const router = useRouter()
const practiceId = route.params.practiceId
const submissionId = route.params.submissionId

const error = ref('')

const questions = ref<QuestionItem[]>([]);
const loading = ref(true);

const fetchPendingAnswers = async () => {
    try {
        console.log(submissionId)
        const response = await ExerciseApi.fetchPendingAnswers({
                submissionId: submissionId,
        });
        console.log(response.data.code)
        if (response.data.code === 200) {
            questions.value = response.data.data;
        } else {
            console.error('获取待评分答案失败:', response.data.message);
        }
    } catch (error) {
        console.error('请求失败:', error);
    } finally {
        loading.value = false;
    }
};

onMounted(fetchPendingAnswers);

const totalScore = computed(() => {
    return questions.value.reduce((sum, q) => sum + q.score, 0);
});

const submitScores = async () => {
    const isValid = questions.value.every(q =>
        q.score >= 0 && q.score <= q.maxScore
    );
    if (!isValid) {
        error.value = '分数必须在0到最大分值之间';
        return;
    }

    const requestData = [
        {
            submissionId: submissionId,
            question: questions.value.map(q => ({
                answerText: q.answerText,
                score: q.score,
                maxScore: q.maxScore,
            })),
        },
    ];

    try {
        const response = await Exercise.gradeAnswer(requestData);
        if (response.data.code === 200) {
            router.push('/')
        } else {
            error.value = '评分提交失败：' + response.data.message;
        }
    } catch (err) {
        error.value = '请求失败，请稍后重试';
        console.error(err);
    }
};
</script>

<style scoped>
.error-message {
    color: #e53935;
    padding: 1rem;
    text-align: center;
    background-color: #ffebee;
    border-radius: 8px;
    margin: 1rem 0;
}

.check-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
}

.question-item {
    margin-bottom: 20px;
    padding: 20px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    background-color: #f9f9f9;
}

.answer-text {
    color: #666;
    margin: 10px 0;
}

.score-input {
    margin-top: 10px;
}

.score-field {
    padding: 6px 12px;
    border: 1px solid #ddd;
    border-radius: 4px;
    width: 100px;
}

.total-score {
    margin-top: 30px;
    font-size: 18px;
    font-weight: bold;
    color: #2c3e50;
}

.submit-button {
    margin-top: 20px;
    padding: 10px 20px;
    background-color: #3498db;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    transition: background-color 0.3s;
}

.submit-button:hover {
    background-color: #2980b9;
}
</style>
