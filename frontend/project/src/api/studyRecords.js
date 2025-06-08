"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.StudyRecordsApi = void 0;
var axios_1 = require("./axios");
// API 实现
exports.StudyRecordsApi = {
    // 获取所有学习记录
    getAllStudyRecords: function () {
        return axios_1.default.get('/api/record/study');
    },
    // 获取指定课程的学习记录
    getStudyRecordsByCourse: function (courseId) {
        return axios_1.default.get("/api/record/study/course/".concat(courseId));
    },
    // 获取所有练习记录
    getAllPracticeRecords: function () {
        return axios_1.default.get('/api/record/practice');
    },
    // 获取指定课程的练习记录
    getPracticeRecordsByCourse: function (courseId) {
        return axios_1.default.get("/api/record/practice/course/".concat(courseId));
    },
    // 获取学生的学习记录列表（包含筛选和分页）- 保留兼容性
    getStudentStudyRecords: function (params) {
        return axios_1.default.get('/api/record/student/list', { params: params });
    },
    // 学生获取单个练习的详细记录，包括题目、答案和反馈
    getStudentExerciseRecordDetail: function (exerciseId) {
        return axios_1.default.get("/api/record/student/exercises/".concat(exerciseId));
    },
    // 学生下载练习报告（Excel 或 PDF）
    downloadStudentExerciseReport: function (exerciseId, format) {
        if (format === void 0) { format = 'pdf'; }
        return axios_1.default.get("/api/record/student/exercises/".concat(exerciseId, "/export"), {
            params: { format: format },
            responseType: 'blob',
            headers: {
                Accept: format === 'excel' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/pdf'
            }
        });
    }, // 导出练习提交报告（通过submissionId）
    exportSubmissionReport: function (submissionId) {
        return axios_1.default.get("/api/record/submission/".concat(submissionId, "/export-report"), {
            responseType: 'blob',
            headers: {
                'Accept': '*/*'
            }
        });
    },
    // 获取练习提交详情报告（通过submissionId）
    getSubmissionReport: function (submissionId) {
        return axios_1.default.get("/api/record/submission/".concat(submissionId, "/report"));
    },
    // 导出指定课程的学习记录（Excel 格式）
    exportStudyRecordsByCourse: function (courseId, params) {
        if (params === void 0) { params = {}; }
        return axios_1.default.get("/api/record/study/course/".concat(courseId, "/export"), {
            params: {
                format: params.format || 'excel',
                startDate: params.startDate,
                endDate: params.endDate
            },
            responseType: 'blob',
            headers: {
                Accept: params.format === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            }
        });
    },
    // 直接导出课程学习记录（Excel 格式）- 简化调用
    exportCourseStudyRecords: function (courseId) {
        return axios_1.default.get("/api/record/study/course/".concat(courseId, "/export"), {
            responseType: 'blob',
            headers: {
                Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            }
        });
    },
    // 学生导出学习记录（Excel 格式）
    exportStudentStudyRecords: function (params) {
        if (params === void 0) { params = {}; }
        return axios_1.default.get('/api/record/student/export', {
            params: params,
            responseType: 'blob',
            headers: {
                Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            }
        });
    },
    // 教师/助教查看指定班级所有学生的练习完成情况（学习进度概览）
    getClassExerciseProgress: function (classId, params) {
        return axios_1.default.get("/api/record/teacher/classes/".concat(classId, "/progress"), { params: params });
    },
    // 教师/助教查看单个学生在某个练习上的详细记录
    getStudentExerciseAttemptForTeacher: function (exerciseId, studentId) {
        return axios_1.default.get("/api/record/teacher/exercises/".concat(exerciseId, "/students/").concat(studentId));
    },
    // 教师/助教导出班级练习报告
    exportClassExerciseReport: function (params) {
        return axios_1.default.get("/api/record/teacher/classes/".concat(params.classId, "/export"), {
            params: {
                exerciseId: params.exerciseId,
                format: params.format || 'excel',
                startDate: params.startDate,
                endDate: params.endDate
            },
            responseType: 'blob',
            headers: {
                Accept: params.format === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            }
        });
    },
    // 教师/助教导出单个学生的练习报告
    exportStudentExerciseReport: function (params) {
        return axios_1.default.get("/api/record/teacher/students/".concat(params.studentId, "/exercises/").concat(params.exerciseId, "/export"), {
            params: {
                format: params.format || 'pdf'
            },
            responseType: 'blob',
            headers: {
                Accept: params.format === 'excel' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/pdf'
            }
        });
    },
    // 教师/助教导出整体学习记录（Excel 格式）
    exportTeacherStudyRecords: function (params) {
        if (params === void 0) { params = {}; }
        return axios_1.default.get('/api/record/teacher/export', {
            params: params,
            responseType: 'blob',
            headers: {
                Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            }
        });
    },
    // 导出所有练习记录（Excel 格式）
    exportAllPracticeRecords: function () {
        return axios_1.default.get('/api/record/practice/export', {
            responseType: 'blob',
            headers: {
                Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            }
        });
    },
    // 导出指定课程的练习记录（Excel 格式）
    exportPracticeRecordsByCourse: function (courseId) {
        return axios_1.default.get("/api/record/practice/course/".concat(courseId, "/export"), {
            responseType: 'blob',
            headers: {
                'Accept': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/json',
                'Content-Type': 'application/json'
            }
        });
    }
};
exports.default = exports.StudyRecordsApi;
