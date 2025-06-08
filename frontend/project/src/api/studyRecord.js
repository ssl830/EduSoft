"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
exports.default = {
    // 获取所有学习记录
    getAllStudyRecords: function () {
        return (0, axios_1.default)({
            url: '/api/record/study',
            method: 'get'
        });
    }
};
