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
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __generator = (this && this.__generator) || function (thisArg, body) {
    var _ = { label: 0, sent: function() { if (t[0] & 1) throw t[1]; return t[1]; }, trys: [], ops: [] }, f, y, t, g = Object.create((typeof Iterator === "function" ? Iterator : Object).prototype);
    return g.next = verb(0), g["throw"] = verb(1), g["return"] = verb(2), typeof Symbol === "function" && (g[Symbol.iterator] = function() { return this; }), g;
    function verb(n) { return function (v) { return step([n, v]); }; }
    function step(op) {
        if (f) throw new TypeError("Generator is already executing.");
        while (g && (g = 0, op[0] && (_ = 0)), _) try {
            if (f = 1, y && (t = op[0] & 2 ? y["return"] : op[0] ? y["throw"] || ((t = y["return"]) && t.call(y), 0) : y.next) && !(t = t.call(y, op[1])).done) return t;
            if (y = 0, t) op = [op[0] & 2, t.value];
            switch (op[0]) {
                case 0: case 1: t = op; break;
                case 4: _.label++; return { value: op[1], done: false };
                case 5: _.label++; y = op[1]; op = [0]; continue;
                case 7: op = _.ops.pop(); _.trys.pop(); continue;
                default:
                    if (!(t = _.trys, t = t.length > 0 && t[t.length - 1]) && (op[0] === 6 || op[0] === 2)) { _ = 0; continue; }
                    if (op[0] === 3 && (!t || (op[1] > t[0] && op[1] < t[3]))) { _.label = op[1]; break; }
                    if (op[0] === 6 && _.label < t[1]) { _.label = t[1]; t = op; break; }
                    if (t && _.label < t[2]) { _.label = t[2]; _.ops.push(op); break; }
                    if (t[2]) _.ops.pop();
                    _.trys.pop(); continue;
            }
            op = body.call(thisArg, _);
        } catch (e) { op = [6, e]; y = 0; } finally { f = t = 0; }
        if (op[0] & 5) throw op[1]; return { value: op[0] ? op[1] : void 0, done: true };
    }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.useAuthStore = void 0;
var pinia_1 = require("pinia");
var vue_1 = require("vue");
var auth_1 = require("../api/auth");
var axios_1 = require("../api/axios");
exports.useAuthStore = (0, pinia_1.defineStore)('auth', function () {
    // State
    var user = (0, vue_1.ref)(null);
    var token = (0, vue_1.ref)(null);
    // Initialize state from localStorage
    if (typeof window !== 'undefined') {
        var savedUser = localStorage.getItem('user');
        var savedToken = localStorage.getItem('free-fs-token');
        if (savedUser)
            user.value = JSON.parse(savedUser);
        if (savedToken)
            token.value = savedToken;
    }
    // Computed
    var isAuthenticated = (0, vue_1.computed)(function () { return !!token.value; });
    var userRole = (0, vue_1.computed)(function () { var _a; return ((_a = user.value) === null || _a === void 0 ? void 0 : _a.role) || ''; });
    // Actions
    var login = function (userId, password) { return __awaiter(void 0, void 0, void 0, function () {
        var response, userData, error_1;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    _a.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, auth_1.default.login({ userId: userId, password: password })];
                case 1:
                    response = _a.sent();
                    if (response.code === 200) {
                        userData = {
                            id: response.data.userInfo.id,
                            userId: response.data.userInfo.userId,
                            username: response.data.userInfo.username,
                            email: response.data.userInfo.email,
                            role: response.data.userInfo.role
                        };
                        user.value = userData;
                        token.value = response.data.token;
                        // Save to localStorage
                        localStorage.setItem('user', JSON.stringify(userData));
                        localStorage.setItem('free-fs-token', response.data.token);
                        return [2 /*return*/, response];
                    }
                    else {
                        throw new Error(response.msg || '登录失败');
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_1 = _a.sent();
                    console.error('Login error:', error_1);
                    throw error_1;
                case 3: return [2 /*return*/];
            }
        });
    }); };
    var register = function (data) { return __awaiter(void 0, void 0, void 0, function () {
        var registerData, response, error_2;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    _a.trys.push([0, 2, , 3]);
                    registerData = __assign(__assign({}, data), { passwordHash: data.password // 后端需要 passwordHash 而不是 password
                     });
                    delete registerData.password; // 删除 password 字段
                    return [4 /*yield*/, auth_1.default.register(registerData)];
                case 1:
                    response = _a.sent();
                    if (response.code === 200) {
                        return [2 /*return*/, response];
                    }
                    else {
                        throw new Error(response.msg || '注册失败');
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_2 = _a.sent();
                    console.error('Register error:', error_2);
                    throw error_2;
                case 3: return [2 /*return*/];
            }
        });
    }); };
    var logout = function () { return __awaiter(void 0, void 0, void 0, function () {
        var currentToken, error_3;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    currentToken = localStorage.getItem('free-fs-token');
                    if (!currentToken) {
                        console.log('用户未登录，直接清除本地数据');
                        // 清除所有用户相关数据
                        localStorage.removeItem('free-fs-token');
                        localStorage.removeItem('userInfo');
                        localStorage.removeItem('user');
                        user.value = null;
                        token.value = null;
                        // 重定向到登录页
                        window.location.href = '/login';
                        return [2 /*return*/];
                    }
                    _a.label = 1;
                case 1:
                    _a.trys.push([1, 3, 4, 5]);
                    // 发送退出登录请求
                    return [4 /*yield*/, auth_1.default.logout()];
                case 2:
                    // 发送退出登录请求
                    _a.sent();
                    return [3 /*break*/, 5];
                case 3:
                    error_3 = _a.sent();
                    console.error('退出登录请求失败:', error_3);
                    return [3 /*break*/, 5];
                case 4:
                    // 无论请求成功与否，都清除所有数据
                    localStorage.removeItem('free-fs-token');
                    localStorage.removeItem('userInfo');
                    localStorage.removeItem('user');
                    user.value = null;
                    token.value = null;
                    // 重定向到登录页
                    window.location.href = '/login';
                    return [7 /*endfinally*/];
                case 5: return [2 /*return*/];
            }
        });
    }); };
    var fetchUserInfo = function () { return __awaiter(void 0, void 0, void 0, function () {
        var response, userData, error_4;
        var _a;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    _b.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, auth_1.default.getProfile()];
                case 1:
                    response = _b.sent();
                    console.log('获取用户信息响应:', response);
                    // 检查响应数据
                    if (response && response.code === 200) {
                        // 确保response.data存在
                        if (!response.data) {
                            console.error('API返回数据为空');
                            throw new Error('获取用户信息失败：返回数据为空');
                        }
                        userData = {
                            id: response.data.id,
                            userId: response.data.userId,
                            username: response.data.username,
                            email: response.data.email,
                            role: response.data.role,
                            createdAt: response.data.createdAt,
                            updatedAt: response.data.updatedAt
                        };
                        console.log('处理后的用户数据:', userData);
                        // 更新状态
                        user.value = userData;
                        // 更新本地存储
                        localStorage.setItem('user', JSON.stringify(userData));
                        return [2 /*return*/, userData];
                    }
                    else {
                        console.error('API返回错误:', response);
                        throw new Error((response === null || response === void 0 ? void 0 : response.msg) || '获取用户信息失败');
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_4 = _b.sent();
                    console.error('获取用户信息错误:', error_4);
                    if (error_4.response) {
                        // API返回了错误响应
                        console.error('API错误响应:', error_4.response);
                        throw new Error(((_a = error_4.response.data) === null || _a === void 0 ? void 0 : _a.msg) || '服务器错误');
                    }
                    else if (error_4.request) {
                        // 请求发出但没有收到响应
                        console.error('网络错误:', error_4.request);
                        throw new Error('网络连接失败，请检查网络连接');
                    }
                    else {
                        // 其他错误
                        console.error('其他错误:', error_4);
                        throw error_4;
                    }
                    return [3 /*break*/, 3];
                case 3: return [2 /*return*/];
            }
        });
    }); };
    var updateProfile = function (data) { return __awaiter(void 0, void 0, void 0, function () {
        var response, error_5;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    _a.trys.push([0, 5, , 6]);
                    return [4 /*yield*/, auth_1.default.updateProfile(data)];
                case 1:
                    response = _a.sent();
                    if (!(response.code === 200)) return [3 /*break*/, 3];
                    // 更新成功后重新获取用户信息
                    return [4 /*yield*/, fetchUserInfo()];
                case 2:
                    // 更新成功后重新获取用户信息
                    _a.sent();
                    return [2 /*return*/, response];
                case 3: throw new Error(response.msg || '更新用户信息失败');
                case 4: return [3 /*break*/, 6];
                case 5:
                    error_5 = _a.sent();
                    console.error('Update profile error:', error_5);
                    throw error_5;
                case 6: return [2 /*return*/];
            }
        });
    }); };
    var changePassword = function (oldPassword, newPassword) { return __awaiter(void 0, void 0, void 0, function () {
        var formData, response, error_6;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    _a.trys.push([0, 2, , 3]);
                    console.log('=== 开始修改密码 ===');
                    console.log('auth store中的密码数据:', { oldPassword: oldPassword, newPassword: newPassword });
                    formData = new URLSearchParams();
                    formData.append('oldPassword', oldPassword);
                    formData.append('newPassword', newPassword);
                    console.log('构建的表单数据:', formData.toString());
                    console.log('发送请求到:', '/api/user/changePassword');
                    return [4 /*yield*/, axios_1.default.post('/api/user/changePassword', formData, {
                            headers: {
                                'Content-Type': 'application/x-www-form-urlencoded'
                            }
                        })];
                case 1:
                    response = _a.sent();
                    console.log('密码修改响应:', response);
                    console.log('=== 修改密码结束 ===');
                    if (response.code === 200) {
                        return [2 /*return*/, response];
                    }
                    else {
                        throw new Error(response.msg || '修改密码失败');
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_6 = _a.sent();
                    console.error('Change password error:', error_6);
                    throw error_6;
                case 3: return [2 /*return*/];
            }
        });
    }); };
    var uploadAvatar = function (formData) { return __awaiter(void 0, void 0, void 0, function () {
        var response, userInfo, error_7;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    _a.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, auth_1.default.uploadAvatar(formData)];
                case 1:
                    response = _a.sent();
                    console.log('上传头像响应:', response);
                    if (response.code === 200) {
                        userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
                        userInfo.avatar = response.data.avatar;
                        localStorage.setItem('userInfo', JSON.stringify(userInfo));
                        return [2 /*return*/, response.data];
                    }
                    else {
                        throw new Error(response.msg || '上传头像失败');
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_7 = _a.sent();
                    console.error('上传头像失败:', error_7);
                    throw error_7;
                case 3: return [2 /*return*/];
            }
        });
    }); };
    return {
        user: user,
        token: token,
        isAuthenticated: isAuthenticated,
        userRole: userRole,
        login: login,
        register: register,
        logout: logout,
        fetchUserInfo: fetchUserInfo,
        updateProfile: updateProfile,
        changePassword: changePassword,
        uploadAvatar: uploadAvatar
    };
});
