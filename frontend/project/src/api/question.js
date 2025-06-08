"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
var auth_ts_1 = require("../stores/auth.ts");
var authStore = (0, auth_ts_1.useAuthStore)();
var QuestionApi = {
    getQuestionList: function (data) {
        if (data === void 0) { data = {}; }
        return axios_1.default.get('/api/practice/question/list', { params: data });
    },
    uploadQuestion: function (data) {
        return axios_1.default.post('/api/question/upload', data);
    },
    createQuestion: function (data) {
        return axios_1.default.post('/api/practice/question/create', data);
    },
    getFavorQuestionList: function () {
        return axios_1.default.get('/api/practice/questions/favorites', {
            headers: {
                "free-fs-token": authStore.token // APIKey
            }
        });
    },
    getWrongQuestionList: function () {
        return axios_1.default.get('/api/practice/questions/wrong', {
            headers: {
                "free-fs-token": authStore.token // APIKey
            }
        });
    },
    removeWrongQuestion: function (questionId) {
        return axios_1.default.delete("/api/practice/questions/".concat(questionId, "/wrong"), {
            headers: {
                "free-fs-token": authStore.token // APIKey
            }
        });
    },
    addWrongQuestion: function (questionId, data) {
        return axios_1.default.post("/api/practice/questions/".concat(questionId, "/wrong"), data, {
            headers: {
                "free-fs-token": authStore.token
            }
        });
    }
};
exports.default = QuestionApi;
