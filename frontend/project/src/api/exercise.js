"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
var auth_ts_1 = require("../stores/auth.ts");
var authStore = (0, auth_ts_1.useAuthStore)();
var ExerciseApi = {
    // Get exercises for a course
    getClassExercises: function (classId, data) {
        return axios_1.default.get("/classes/".concat(classId, "/exercises"), data);
    },
    // 从题库中导入题目
    importQuestionsToPractice: function (data) {
        return axios_1.default.post('/api/practice/question/import', data);
    },
    // 获取待批改练习列表
    getPendingJudgeList: function (data) {
        console.log("data:", data);
        console.log();
        return axios_1.default.post('/api/judge/pendinglist', data);
    },
    // Get teacher's courses (for exercise creation)
    getTeacherCourses: function () {
        return axios_1.default.get('/courses/teaching');
    },
    // Get classes for a course
    getCourseClasses: function (courseId) {
        return axios_1.default.get("/courses/".concat(courseId, "/classes"));
    },
    // Create exercise (teacher/tutor only)
    createExercise: function (data) {
        return axios_1.default.post("/api/practice/create", data);
    },
    takeExercise: function (practiceId) {
        return axios_1.default.get("/api/practice/".concat(practiceId));
    },
    submitExercise: function (data) {
        return axios_1.default.post('/api/submission/submit', data);
    },
    // Get exercise details
    getExerciseDetails: function (exerciseId, data) {
        return axios_1.default.get("/api/practice/".concat(exerciseId), data
        // ,{
        // headers: {
        //     Authorization: `Bearer ${authStore.token}`
        // }
        // }
        );
    },
    // 获取学生所有练习
    getPracticeList: function (studentId, classId) {
        var params = new URLSearchParams({
            studentId: studentId,
            classId: classId
        });
        return axios_1.default.get("/api/practice/student/list?".concat(params.toString()));
    },
    getPracticeTeachList: function (classId) {
        var params = new URLSearchParams({
            classId: classId
        });
        return axios_1.default.get("/api/practice/list/teacher?".concat(params.toString()));
    },
    favouriteQuestions: function (questionId) {
        return axios_1.default.post("/api/practice/questions/".concat(questionId, "/favorite"), {
            headers: {
                "free-fs-token": authStore.token // APIKey
            }
        });
    },
    enFavouriteQuestions: function (questionId) {
        return axios_1.default.delete("/api/practice/questions/".concat(questionId, "/favorite"), {
            headers: {
                "free-fs-token": authStore.token // APIKey
            }
        });
    },
    fetchPendingAnswers: function (data) {
        console.log(data);
        console.log();
        return axios_1.default.post('/api/judge/pending', data);
    },
    gradeAnswer: function (data) {
        // 确保submissionId是数字类型
        var payload = {
            submissionId: Number(data.submissionId),
            questions: data.questions
        };
        return axios_1.default.post('/api/judge/judge', payload);
    },
    // Start exercise attempt (student only)
    startExerciseAttempt: function (exerciseId) {
        return axios_1.default.post("/practices/".concat(exerciseId, "/attempts"));
    },
    // Submit answer (student only)
    submitAnswer: function (attemptId, data) {
        return axios_1.default.post("/attempts/".concat(attemptId, "/answers"), data);
    },
    // Finish exercise attempt (student only)
    finishAttempt: function (attemptId) {
        return axios_1.default.post("/attempts/".concat(attemptId, "/finish"));
    },
    // Get exercise result (student)
    getExerciseResult: function (attemptId) {
        return axios_1.default.get("/attempts/".concat(attemptId, "/result"));
    },
    // Get all results for an exercise (teacher/tutor)
    getExerciseResults: function (exerciseId) {
        return axios_1.default.get("/practices/".concat(exerciseId, "/results"));
    },
    // Grade a subjective answer (teacher/tutor)
    // 获取某个练习的题目列表
    getPracticeQuestions: function (practiceId) {
        return axios_1.default.get("/api/practice/".concat(practiceId, "/questions"));
    },
    // 更新练习基本信息
    updateExercise: function (practiceId, data) {
        return axios_1.default.put("/api/practice/".concat(practiceId), data);
    },
    // 修改练习中题目的分值
    updatePracticeQuestionScore: function (practiceId, questionId, score) {
        return axios_1.default.put("/api/practice/".concat(practiceId, "/questions/").concat(questionId, "?score=").concat(score));
    },
    // 移除练习中的题目
    removePracticeQuestion: function (practiceId, questionId) {
        return axios_1.default.delete("/api/practice/".concat(practiceId, "/questions/").concat(questionId));
    },
};
exports.default = ExerciseApi;
