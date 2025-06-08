"use strict";
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
var ResourceApi = {
    // 获取课程视频列表
    getChapterResources: function (courseId, formData) {
        return axios_1.default.post("/api/resources/chapter/".concat(courseId), formData);
    },
    // 记录视频播放进度
    recordWatchProgress: function (formData) {
        return axios_1.default.post('/api/resources/progress', formData);
    },
    // 上传视频
    uploadVideo: function (formData, onUploadProgress) {
        return axios_1.default.post("/api/resources/upload", formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: function (progressEvent) {
                if (progressEvent.total && onUploadProgress) {
                    var percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                    onUploadProgress(percentCompleted);
                }
            }
        });
    },
    // Get resources for a course 
    getCourseResources: function (courseId, data) {
        return axios_1.default.post("/api/courses/".concat(courseId, "/filelist"), __assign(__assign({}, data), { courseId: Number(courseId) }));
    },
    // Upload resource
    uploadResource: function (courseId, formData, onUploadProgress) {
        return axios_1.default.post("/api/courses/".concat(courseId, "/upload"), formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: function (progressEvent) {
                if (progressEvent.total && onUploadProgress) {
                    var percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                    onUploadProgress(percentCompleted);
                }
            }
        });
    },
    // Download resource
    downloadResource: function (resourceId) {
        return axios_1.default.get("/api/resources/".concat(resourceId, "/download"));
    },
    // Preview resource
    previewResource: function (resourceId) {
        return axios_1.default.get("/api/resources/".concat(resourceId, "/preview"));
    },
    // Update resource (teacher/tutor only)
    updateResource: function (resourceId, data) {
        var formData = new FormData();
        if (data.title)
            formData.append('title', data.title);
        if (data.chapter)
            formData.append('chapter', data.chapter);
        if (data.type)
            formData.append('type', data.type);
        if (data.visible_to_classes) {
            data.visible_to_classes.forEach(function (classId, index) {
                formData.append("visible_to_classes[".concat(index, "]"), classId);
            });
        }
        if (data.new_file)
            formData.append('new_file', data.new_file);
        return axios_1.default.put("/resources/".concat(resourceId), formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
    // Delete resource (teacher/tutor only)
    deleteResource: function (resourceId) {
        return axios_1.default.delete("/resources/".concat(resourceId));
    },
    // Update resource duration
    updateResourceDuration: function (resourceId, duration) {
        return axios_1.default.put("/api/resources/".concat(resourceId, "/duration"), { duration: duration });
    },
    // Get video progress
    getVideoProgress: function (resourceId, studentId) {
        return axios_1.default.get("/api/resources/progress/".concat(resourceId, "/").concat(studentId));
    }
};
exports.default = ResourceApi;
