"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("axios");
var auth_1 = require("../stores/auth");
// Create an axios instance
var instance = axios_1.default.create({
    baseURL: 'http://localhost:8080', // 修改为本地后端服务器地址
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
});
// Add a request interceptor
instance.interceptors.request.use(function (config) {
    var _a, _b;
    // 确保所有请求路径都以/开头
    if (config.url && !config.url.startsWith('/')) {
        config.url = '/' + config.url;
    }
    var authStore = (0, auth_1.useAuthStore)();
    if (authStore.token) {
        config.headers['free-fs-token'] = authStore.token;
    }
    else if (!((_a = config.url) === null || _a === void 0 ? void 0 : _a.includes('/login')) && !((_b = config.url) === null || _b === void 0 ? void 0 : _b.includes('/register'))) {
        // 对于非登录和注册请求，如果没有token，记录日志
        console.log('未找到token，请求:', config.url);
    }
    return config;
}, function (error) {
    console.error('请求错误:', error);
    return Promise.reject(error);
});
// Add a response interceptor
instance.interceptors.response.use(function (response) {
    // 记录响应信息
    console.log('响应状态:', response.status);
    console.log('响应URL:', response.config.url);
    console.log('响应数据:', response.data);
    // 检查响应格式
    if (response.data && typeof response.data === 'object') {
        // 如果响应数据是对象，直接返回
        return response.data;
    }
    else {
        // 如果响应数据不是对象，包装成标准格式
        return {
            code: response.status,
            msg: '请求成功',
            data: response.data
        };
    }
}, function (error) {
    var _a;
    if (error.response) {
        // 处理401错误
        if (error.response.status === 401) {
            console.log('未授权，清除用户数据');
            var authStore = (0, auth_1.useAuthStore)();
            authStore.clearUserData();
            // 重定向到登录页
            window.location.href = '/login';
        }
        console.error('响应错误:', error.response.status);
        console.error('错误数据:', error.response.data);
        // 返回标准错误格式
        return Promise.reject({
            code: error.response.status,
            msg: ((_a = error.response.data) === null || _a === void 0 ? void 0 : _a.msg) || '请求失败',
            data: error.response.data
        });
    }
    else {
        console.error('请求错误:', error.message);
        return Promise.reject({
            code: 500,
            msg: error.message || '网络错误',
            data: null
        });
    }
});
exports.default = instance;
