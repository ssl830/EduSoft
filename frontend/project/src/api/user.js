"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
var userApi = {
    // 注册用户
    register: function (data) {
        return axios_1.default.post('/api/user/register', data);
    },
    // 用户登录
    login: function (data) {
        return axios_1.default.post('/api/user/login', data);
    },
    // 退出登录
    logout: function () {
        return axios_1.default.post('/api/user/logout');
    },
    // 获取当前用户信息
    getUserInfo: function () {
        return axios_1.default.get('/api/user/info');
    },
    // 更新用户信息
    updateUserInfo: function (data) {
        return axios_1.default.post('/api/user/update', data);
    },
    // 修改密码
    changePassword: function (data) {
        console.log('发送的密码数据:', data);
        var params = new URLSearchParams();
        params.append('oldPassword', data.oldPassword);
        params.append('newPassword', data.newPassword);
        console.log('转换后的参数:', params.toString());
        return axios_1.default.post('/api/user/changePassword', params, {
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            }
        });
    }
};
exports.default = userApi;
