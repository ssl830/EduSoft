"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
var AuthApi = {
    // Login
    login: function (data) {
        // 直接使用形如 userId=xxx&password=xxx 的格式
        var formData = new URLSearchParams();
        formData.append('userId', data.userId);
        formData.append('password', data.password);
        return axios_1.default.post('/api/user/login', formData, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
    },
    // Register
    register: function (data) {
        return axios_1.default.post('/api/user/register', data);
    },
    // Update Profile
    updateProfile: function (data) {
        return axios_1.default.post('/api/user/update', data);
    },
    // Change Password
    changePassword: function (data) {
        return axios_1.default.post('/api/user/changePassword', data);
    },
    // Upload Avatar
    uploadAvatar: function (formData) {
        return axios_1.default.post('/api/user/avatar', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
    // Get Current User Profile
    getProfile: function () {
        return axios_1.default.get('/api/user/info');
    },
    // Logout
    logout: function () {
        return axios_1.default.post('/api/user/logout');
    }
};
exports.default = AuthApi;
