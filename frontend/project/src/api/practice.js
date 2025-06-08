"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
var PracticeApi = {
    // 获取教师相关的所有练习信息
    getTeacherPractices: function () {
        return axios_1.default.get('/api/practice/teacher/practices');
    },
    // 获取某个练习的统计信息
    getPracticeStats: function (practiceId) {
        return axios_1.default.get("/api/practice/stats/".concat(practiceId));
    },
    // 查询班级总人数
    getClassStudentCount: function (classId) {
        return axios_1.default.get("/api/classes/".concat(classId, "/student-count"));
    },
};
exports.default = PracticeApi;
