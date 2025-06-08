"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
var ClassApi = {
    // Get all classes for current user
    getUserClasses: function (id) {
        return axios_1.default.get("/api/classes/user/".concat(id));
    },
    uploadStudents: function (data) {
        return axios_1.default.post("/api/imports/students", data, {
            headers: {
                'Content-Type': 'application/json',
            },
        });
    },
    // Get class by ID
    getClassById: function (id) {
        return axios_1.default.get("/api/classes/".concat(id));
    },
    // Create new class (teacher only)
    createClass: function (data) {
        return axios_1.default.post('/api/classes', data);
    },
    getHomeworkList: function (classId) {
        return axios_1.default.get("/api/homework/list?classId=".concat(classId));
    },
    deleteHomework: function (homeworkId) {
        return axios_1.default.delete("/api/homework/".concat(homeworkId));
    },
    fetchSubmissions: function (homeworkId) {
        return axios_1.default.get("/api/homework/submissions/".concat(homeworkId));
    },
    // Download resource
    downloadSubmissionFile: function (submissionId) {
        return axios_1.default.get("/api/homework/submission/file/".concat(submissionId));
    },
    // Upload resource
    uploadSubmissionFile: function (homeworkId, formData) {
        return axios_1.default.post("/api/homework/submit/".concat(homeworkId), formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
        });
    },
    createHomework: function (formData) {
        return axios_1.default.post('/api/homework/create', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
        });
    },
    downloadHomeworkFile: function (homeworkId) {
        return axios_1.default.get("/api/homework/file/".concat(homeworkId));
    },
    // Update class (teacher only)
    updateClass: function (id, data) {
        return axios_1.default.put("/classes/".concat(id), data);
    },
    // Get classes for a class
    getClassClasses: function (classId) {
        return axios_1.default.get("/classes/".concat(classId, "/classes"));
    },
    // Create class for a class (teacher only)
    // createClass(classId: string, data: {
    //   name: string;
    // }) {
    //   return axios.post(`/classes/${classId}/classes`, data)
    // },
    // Get class details
    getClassDetails: function (classId) {
        return axios_1.default.get("/classes/".concat(classId));
    },
    joinClass: function (studentId, classCode) {
        // 使用 URLSearchParams 自动处理编码
        var params = new URLSearchParams({
            studentId: studentId,
            classCode: classCode
        });
        return axios_1.default.post("/api/classes/join?".concat(params.toString()));
    },
    // Get students in a class (teacher/tutor only)
    getClassStudents: function (classId) {
        return axios_1.default.get("/api/classes/".concat(classId, "/users"));
    },
    deleteStudents: function (classId, studentId) {
        return axios_1.default.delete("/api/classes/".concat(classId, "/students/").concat(studentId));
    },
    // Import students to a class (teacher only)
    importStudents: function (classId, file) {
        var formData = new FormData();
        formData.append('file', file);
        return axios_1.default.post("/classes/".concat(classId, "/students/import"), formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
    /**
     * 老师获取自己负责的所有班级（不查成员表，不会把老师当成员）
     * @param teacherId 老师用户ID
     * @returns Promise
     */
    getTeacherClasses: function (teacherId) {
        return axios_1.default.get("/api/classes/teacher/simple/".concat(teacherId));
    },
    /**
     * 查询学生某作业的提交记录（用于判断是否已提交）
     * @param homeworkId 作业ID
     * @param studentId 学生ID
     * @returns Promise
     */
    getStudentSubmission: function (homeworkId, studentId) {
        var params = new URLSearchParams({
            homeworkId: homeworkId,
            studentId: studentId
        });
        return axios_1.default.get("/api/homework/submission?".concat(params.toString()));
    },
};
exports.default = ClassApi;
