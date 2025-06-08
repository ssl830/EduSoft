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
exports.NotificationType = void 0;
var axios_1 = require("axios");
var axios_2 = require("./axios");
// 通知类型枚举（严格按照后端规范）
var NotificationType;
(function (NotificationType) {
    NotificationType["COURSE_NOTICE"] = "COURSE_NOTICE";
    NotificationType["PRACTICE_NOTICE"] = "PRACTICE_NOTICE";
    NotificationType["DDL_REMINDER"] = "DDL_REMINDER";
    NotificationType["TASK_REMINDER"] = "TASK_REMINDER";
    NotificationType["HOMEWORK"] = "HOMEWORK";
    NotificationType["TASK"] = "TASK";
    NotificationType["PRACTICE"] = "PRACTICE";
    NotificationType["SYSTEM"] = "SYSTEM";
    NotificationType["DISCUSSION_REPLY"] = "DISCUSSION_REPLY";
    NotificationType["ASSIGNMENT"] = "ASSIGNMENT"; // 作业分配
})(NotificationType || (exports.NotificationType = NotificationType = {}));
var notificationApi = {
    // 获取通知列表
    getNotifications: function (params) { return __awaiter(void 0, void 0, void 0, function () {
        var response, backendNotifications, notifications, result, errorMsg, error_1, errorMessage;
        var _a;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    console.log('[getNotifications] Called with params:', params);
                    _b.label = 1;
                case 1:
                    _b.trys.push([1, 3, , 4]);
                    return [4 /*yield*/, axios_2.default.get('/api/notifications', { params: params })];
                case 2:
                    response = _b.sent();
                    console.log('[getNotifications] Raw API response:', response);
                    if (response && response.code === 200) {
                        console.log('[getNotifications] API response code 200. Response data:', response.data);
                        backendNotifications = [];
                        // 检查 response.data 的类型和内容
                        if (Array.isArray(response.data)) {
                            // 标准情况：data 是一个通知数组
                            backendNotifications = response.data;
                            console.log('[getNotifications] response.data is an array. backendNotifications:', backendNotifications);
                        }
                        else if (response.data === '' || response.data == null) {
                            // 处理空字符串、null 或 undefined，表示没有通知
                            backendNotifications = [];
                            console.log('[getNotifications] response.data is empty string, null, or undefined. Set backendNotifications to [].');
                        }
                        else {
                            // 数据存在但不是数组，也不是预期的空值类型。
                            // 这是一个意外的格式。记录警告并视为空列表以防止后续错误。
                            console.warn('[getNotifications] API success (200) but notification data format is unexpected. Expected array, empty string, null, or undefined. Received:', response.data, '(Type:', typeof response.data + ')');
                            backendNotifications = [];
                        }
                        notifications = backendNotifications.map(function (notification) {
                            if (!notification) { // Add null check for items within the array if necessary
                                console.warn('[getNotifications] Encountered null/undefined notification object in backendNotifications array. Skipping this item.');
                                return {
                                    id: 0, // Or some other unique temporary ID
                                    title: '无效通知',
                                    message: '无效的通知数据',
                                    type: NotificationType.SYSTEM, // Default type
                                    readFlag: false,
                                    createdAt: new Date().toISOString(),
                                    // Frontend specific fields
                                    content: '无效的通知数据',
                                    read: false,
                                    priority: 'low'
                                };
                            }
                            // 确保 notification.type 是有效的枚举值
                            var validType = Object.values(NotificationType).includes(notification.type)
                                ? notification.type
                                : NotificationType.SYSTEM; // 如果类型无效，默认为系统通知
                            return __assign(__assign({}, notification), { content: notification.message || '', read: notification.readFlag !== undefined ? notification.readFlag : false, type: validType, priority: getPriorityByType(validType) // 根据类型获取优先级
                             });
                        });
                        result = {
                            notifications: notifications,
                            total: response.total !== undefined ? response.total : notifications.length
                        };
                        console.log('[getNotifications] Processed notifications and total:', result);
                        return [2 /*return*/, result];
                    }
                    else {
                        errorMsg = (response === null || response === void 0 ? void 0 : response.message) || '获取通知失败';
                        console.error('[getNotifications] API returned error or non-200 code:', errorMsg, response);
                        throw new Error(errorMsg);
                    }
                    return [3 /*break*/, 4];
                case 3:
                    error_1 = _b.sent();
                    errorMessage = '获取通知失败';
                    if (error_1.response) { // Error from server (e.g. 4xx, 5xx)
                        errorMessage = ((_a = error_1.response.data) === null || _a === void 0 ? void 0 : _a.message) ||
                            "\u670D\u52A1\u5668\u9519\u8BEF (".concat(error_1.response.status, ")");
                    }
                    else if (error_1.request) { // Network error or no response from server
                        errorMessage = '无法连接到服务器';
                    }
                    else if (error_1.message && !error_1.response && !error_1.request) { // Errors thrown by our code before request or non-Axios errors
                        errorMessage = error_1.message;
                    }
                    else if (error_1.message) { // Other JavaScript errors
                        errorMessage = error_1.message;
                    }
                    console.error('[getNotifications] API call failed (Outer Catch):', errorMessage, error_1);
                    throw new Error(errorMessage);
                case 4: return [2 /*return*/];
            }
        });
    }); },
    // 获取未读通知数量
    getUnreadCount: function () { return __awaiter(void 0, void 0, void 0, function () {
        var resultFromAxios, apiResponse, errorMsg, error_2, errorMessage;
        var _a;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    console.log('[getUnreadCount] Called.');
                    _b.label = 1;
                case 1:
                    _b.trys.push([1, 3, , 4]);
                    return [4 /*yield*/, axios_2.default.get('/api/notifications/unread')];
                case 2:
                    resultFromAxios = _b.sent();
                    console.log('[getUnreadCount] Raw response from axiosInstance.get:', resultFromAxios, '(Type:', typeof resultFromAxios + ')');
                    // 情况1: 后端直接返回空字符串表示数量为0 
                    if (resultFromAxios === '') {
                        console.log('[getUnreadCount] Condition 1: Received empty string directly. Interpreting as 0 unread notifications.');
                        return [2 /*return*/, 0];
                    }
                    // 情况2: 后端返回标准的 ApiResponse 对象
                    if (typeof resultFromAxios === 'object' && resultFromAxios !== null && 'code' in resultFromAxios) {
                        console.log('[getUnreadCount] Condition 2: Received an object, potentially ApiResponse.');
                        apiResponse = resultFromAxios;
                        console.log('[getUnreadCount] ApiResponse content:', apiResponse);
                        if (apiResponse.code === 200) {
                            console.log('[getUnreadCount] ApiResponse code is 200. Checking apiResponse.data:', apiResponse.data);
                            // 子情况2.1: ApiResponse.data 是空字符串
                            if (apiResponse.data === '') {
                                console.log('[getUnreadCount] Sub-condition 2.1: ApiResponse.data is empty string. Interpreting as 0.');
                                return [2 /*return*/, 0];
                            }
                            // 子情况2.2: ApiResponse.data 是 { count: N }
                            if (apiResponse.data && typeof apiResponse.data.count === 'number') {
                                console.log('[getUnreadCount] Sub-condition 2.2: ApiResponse.data has count. Returning:', apiResponse.data.count);
                                return [2 /*return*/, apiResponse.data.count];
                            }
                            // 子情况2.3: ApiResponse.data 格式非预期
                            console.warn('[getUnreadCount] Sub-condition 2.3: API success (200) but unread count data format is unexpected. Data:', apiResponse.data, '(Type:', typeof apiResponse.data + ')');
                            console.log('[getUnreadCount] Defaulting to 0 due to unexpected data format.');
                            return [2 /*return*/, 0]; // 默认返回0并记录警告
                        }
                        else {
                            errorMsg = apiResponse.message || "\u83B7\u53D6\u672A\u8BFB\u901A\u77E5\u6570\u91CF\u5931\u8D25 (\u4EE3\u7801: ".concat(apiResponse.code, ")");
                            console.error('[getUnreadCount] ApiResponse code is not 200:', errorMsg, apiResponse);
                            throw new Error(errorMsg);
                        }
                    }
                    // 情况3: 响应格式未知 (既不是空字符串，也不是预期的ApiResponse对象)
                    console.error('[getUnreadCount] Condition 3: Unknown response format. Response:', resultFromAxios);
                    throw new Error('获取未读通知数量失败 (未知响应格式)');
                case 3:
                    error_2 = _b.sent();
                    errorMessage = '获取未读通知数量API调用失败';
                    // 检查是否是我们主动抛出的错误，如果是，则使用其消息
                    if (error_2 && error_2.message && !error_2.response && !error_2.request) {
                        errorMessage = error_2.message;
                        console.log('[getUnreadCount] Caught error explicitly thrown in try block:', errorMessage);
                    }
                    else if (error_2.response) { // Axios 的错误结构 (服务器错误)
                        errorMessage = ((_a = error_2.response.data) === null || _a === void 0 ? void 0 : _a.message) || "\u670D\u52A1\u5668\u9519\u8BEF (".concat(error_2.response.status, ")");
                    }
                    else if (error_2.request) { // Axios 的错误结构 (网络错误)
                        errorMessage = '无法连接到服务器';
                    }
                    else if (error_2.message) { // 其他JavaScript错误
                        errorMessage = error_2.message;
                    }
                    console.error('[getUnreadCount] API call failed (Outer Catch):', errorMessage, error_2);
                    throw new Error(errorMessage);
                case 4: return [2 /*return*/];
            }
        });
    }); },
    // 标记通知为已读
    markAsRead: function (id) { return __awaiter(void 0, void 0, void 0, function () {
        var response, errorMsg, error_3, errorMessage;
        var _a;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    _b.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, axios_2.default.put("/api/notifications/".concat(id, "/read"))];
                case 1:
                    response = _b.sent();
                    if (response && response.code === 200) {
                        return [2 /*return*/, true];
                    }
                    else {
                        errorMsg = (response === null || response === void 0 ? void 0 : response.message) || '标记通知已读失败';
                        console.error('标记通知已读API调用失败:', errorMsg, response);
                        throw new Error(errorMsg);
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_3 = _b.sent();
                    errorMessage = '标记通知已读API调用失败';
                    if (error_3.response) {
                        errorMessage = ((_a = error_3.response.data) === null || _a === void 0 ? void 0 : _a.message) || "\u670D\u52A1\u5668\u9519\u8BEF (".concat(error_3.response.status, ")");
                    }
                    else if (error_3.request) {
                        errorMessage = '无法连接到服务器';
                    }
                    else if (error_3.message) {
                        errorMessage = error_3.message;
                    }
                    console.error('标记通知已读API调用失败:', errorMessage, error_3);
                    throw new Error(errorMessage);
                case 3: return [2 /*return*/];
            }
        });
    }); },
    // 标记所有通知为已读
    markAllAsRead: function () { return __awaiter(void 0, void 0, void 0, function () {
        var response, errorMsg, error_4, errorMessage;
        var _a;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    _b.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, axios_2.default.put('/api/notifications/read-all')];
                case 1:
                    response = _b.sent();
                    if (response && response.code === 200) {
                        return [2 /*return*/, true];
                    }
                    else {
                        errorMsg = (response === null || response === void 0 ? void 0 : response.message) || '标记所有通知已读失败';
                        console.error('标记所有通知已读API调用失败:', errorMsg, response);
                        throw new Error(errorMsg);
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_4 = _b.sent();
                    errorMessage = '标记所有通知已读API调用失败';
                    if (error_4.response) {
                        errorMessage = ((_a = error_4.response.data) === null || _a === void 0 ? void 0 : _a.message) || "\u670D\u52A1\u5668\u9519\u8BEF (".concat(error_4.response.status, ")");
                    }
                    else if (error_4.request) {
                        errorMessage = '无法连接到服务器';
                    }
                    else if (error_4.message) {
                        errorMessage = error_4.message;
                    }
                    console.error('标记所有通知已读API调用失败:', errorMessage, error_4);
                    throw new Error(errorMessage);
                case 3: return [2 /*return*/];
            }
        });
    }); },
    // 批量标记为已读
    markMultipleAsRead: function (ids) { return __awaiter(void 0, void 0, void 0, function () {
        var promises, results, error_5, errorMessage;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    _a.trys.push([0, 2, , 3]);
                    promises = ids.map(function (id) { return notificationApi.markAsRead(id); });
                    return [4 /*yield*/, Promise.all(promises)];
                case 1:
                    results = _a.sent();
                    return [2 /*return*/, results.every(function (result) { return result === true; })];
                case 2:
                    error_5 = _a.sent();
                    errorMessage = error_5.message || '批量标记已读失败';
                    console.error('批量标记已读失败:', errorMessage, error_5);
                    throw new Error(errorMessage);
                case 3: return [2 /*return*/];
            }
        });
    }); },
    // 删除通知
    deleteNotification: function (id) { return __awaiter(void 0, void 0, void 0, function () {
        var response, errorMsg, error_6, errorMessage;
        var _a;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    _b.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, axios_2.default.delete("/api/notifications/".concat(id))];
                case 1:
                    response = _b.sent();
                    if (response && response.code === 200) {
                        return [2 /*return*/, true];
                    }
                    else {
                        errorMsg = (response === null || response === void 0 ? void 0 : response.message) || '删除通知失败';
                        console.error('删除通知API调用失败:', errorMsg, response);
                        throw new Error(errorMsg);
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_6 = _b.sent();
                    errorMessage = '删除通知API调用失败';
                    if (error_6.response) {
                        errorMessage = ((_a = error_6.response.data) === null || _a === void 0 ? void 0 : _a.message) || "\u670D\u52A1\u5668\u9519\u8BEF (".concat(error_6.response.status, ")");
                    }
                    else if (error_6.request) {
                        errorMessage = '无法连接到服务器';
                    }
                    else if (error_6.message) {
                        errorMessage = error_6.message;
                    }
                    console.error('删除通知API调用失败:', errorMessage, error_6);
                    throw new Error(errorMessage);
                case 3: return [2 /*return*/];
            }
        });
    }); },
    // 创建通知（用于教师/助教）
    createNotification: function (notificationData) { return __awaiter(void 0, void 0, void 0, function () {
        var response, errorMsg, error_7, errorMessage;
        var _a;
        return __generator(this, function (_b) {
            switch (_b.label) {
                case 0:
                    _b.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, axios_2.default.post('/notifications', notificationData)];
                case 1:
                    response = _b.sent();
                    if (response && response.code === 200 && response.data) {
                        return [2 /*return*/, response.data];
                    }
                    else {
                        errorMsg = (response === null || response === void 0 ? void 0 : response.message) || '创建通知失败';
                        console.error('创建通知失败:', errorMsg, response);
                        throw new Error(errorMsg);
                    }
                    return [3 /*break*/, 3];
                case 2:
                    error_7 = _b.sent();
                    errorMessage = '创建通知API调用失败';
                    if (error_7.response) {
                        errorMessage = ((_a = error_7.response.data) === null || _a === void 0 ? void 0 : _a.message) || "\u670D\u52A1\u5668\u9519\u8BEF (".concat(error_7.response.status, ")");
                    }
                    else if (error_7.request) {
                        errorMessage = '无法连接到服务器';
                    }
                    else if (error_7.message) {
                        errorMessage = error_7.message;
                    }
                    console.error('创建通知API调用失败:', errorMessage, error_7);
                    throw new Error(errorMessage);
                case 3: return [2 /*return*/];
            }
        });
    }); },
    // 自动生成任务分配通知
    generateTaskAssignmentNotification: function (taskAssignment) { return __awaiter(void 0, void 0, void 0, function () {
        var response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, axios_1.default.post('/notifications/auto-generate/task-assignment', taskAssignment)];
                case 1:
                    response = _a.sent();
                    return [2 /*return*/, response.data];
            }
        });
    }); },
    // 自动生成DDL提醒
    generateDeadlineReminder: function (taskId, reminderDays) { return __awaiter(void 0, void 0, void 0, function () {
        var response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, axios_1.default.post('/notifications/auto-generate/deadline-reminder', {
                        taskId: taskId,
                        reminderDays: reminderDays
                    })];
                case 1:
                    response = _a.sent();
                    return [2 /*return*/, response.data];
            }
        });
    }); },
    // 任务完成时自动删除相关通知
    autoDeleteOnTaskCompletion: function (taskId) { return __awaiter(void 0, void 0, void 0, function () {
        var response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, axios_1.default.delete("/notifications/auto-delete/task/".concat(taskId))];
                case 1:
                    response = _a.sent();
                    return [2 /*return*/, response.data];
            }
        });
    }); },
    // 获取个人任务清单
    getPersonalTasks: function (params) { return __awaiter(void 0, void 0, void 0, function () {
        var response, error_8;
        var _a, _b, _c, _d;
        return __generator(this, function (_e) {
            switch (_e.label) {
                case 0:
                    _e.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, axios_1.default.get('/api/personal-tasks', { params: params })];
                case 1:
                    response = _e.sent();
                    return [2 /*return*/, {
                            tasks: ((_a = response.data) === null || _a === void 0 ? void 0 : _a.data) || [],
                            total: ((_c = (_b = response.data) === null || _b === void 0 ? void 0 : _b.data) === null || _c === void 0 ? void 0 : _c.length) || 0, // 这可能不准确，取决于后端如何返回 total
                            success: ((_d = response.data) === null || _d === void 0 ? void 0 : _d.code) === 200
                        }];
                case 2:
                    error_8 = _e.sent();
                    console.error('获取个人任务列表失败:', error_8);
                    return [2 /*return*/, { tasks: [], total: 0, success: false }];
                case 3: return [2 /*return*/];
            }
        });
    }); },
    // 创建个人任务
    createPersonalTask: function (task) { return __awaiter(void 0, void 0, void 0, function () {
        var response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, axios_1.default.post('/personal-tasks', task)];
                case 1:
                    response = _a.sent();
                    return [2 /*return*/, response.data];
            }
        });
    }); },
    // 更新个人任务
    updatePersonalTask: function (id, updates) { return __awaiter(void 0, void 0, void 0, function () {
        var response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, axios_1.default.patch("/personal-tasks/".concat(id), updates)];
                case 1:
                    response = _a.sent();
                    return [2 /*return*/, response.data];
            }
        });
    }); },
    // 删除个人任务
    deletePersonalTask: function (id) { return __awaiter(void 0, void 0, void 0, function () {
        var response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, axios_1.default.delete("/personal-tasks/".concat(id))];
                case 1:
                    response = _a.sent();
                    return [2 /*return*/, response.data];
            }
        });
    }); },
    // 检查DDL并生成提醒（系统调用）
    checkDeadlinesAndGenerateReminders: function () { return __awaiter(void 0, void 0, void 0, function () {
        var response, error_9;
        var _a, _b;
        return __generator(this, function (_c) {
            switch (_c.label) {
                case 0:
                    _c.trys.push([0, 2, , 3]);
                    return [4 /*yield*/, axios_1.default.post('/api/notifications/check-deadlines')];
                case 1:
                    response = _c.sent();
                    return [2 /*return*/, {
                            success: ((_a = response.data) === null || _a === void 0 ? void 0 : _a.code) === 200,
                            message: ((_b = response.data) === null || _b === void 0 ? void 0 : _b.message) || '提醒生成成功'
                        }];
                case 2:
                    error_9 = _c.sent();
                    console.error('检查DDL并生成提醒失败:', error_9);
                    return [2 /*return*/, { success: false, message: '检查DDL并生成提醒失败' }];
                case 3: return [2 /*return*/];
            }
        });
    }); },
    // 获取通知设置
    getNotificationSettings: function () { return __awaiter(void 0, void 0, void 0, function () {
        var response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, axios_1.default.get('/notifications/settings')];
                case 1:
                    response = _a.sent();
                    return [2 /*return*/, response.data];
            }
        });
    }); },
    // 更新通知设置
    updateNotificationSettings: function (settings) { return __awaiter(void 0, void 0, void 0, function () {
        var response;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0: return [4 /*yield*/, axios_1.default.patch('/notifications/settings', settings)];
                case 1:
                    response = _a.sent();
                    return [2 /*return*/, response.data];
            }
        });
    }); }
};
// 辅助函数: 根据通知类型设置优先级
function getPriorityByType(type) {
    switch (type) {
        case NotificationType.DDL_REMINDER:
        case NotificationType.HOMEWORK:
        case NotificationType.ASSIGNMENT: // 作业分配设为高优先级
            return 'high';
        case NotificationType.TASK_REMINDER:
            return 'urgent';
        case NotificationType.DISCUSSION_REPLY: // 讨论回复设为中优先级
        case NotificationType.PRACTICE_NOTICE:
        case NotificationType.PRACTICE:
            return 'medium';
        case NotificationType.COURSE_NOTICE:
        case NotificationType.TASK:
        case NotificationType.SYSTEM: // 系统通知设为低优先级
        default:
            return 'low';
    }
}
exports.default = notificationApi;
