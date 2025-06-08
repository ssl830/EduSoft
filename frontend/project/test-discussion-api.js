"use strict";
// 讨论API集成验证脚本
// 这个文件可以用于测试新的讨论API是否正常工作
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
exports.runAllTests = runAllTests;
var discussion_ts_1 = require("@/src/api/discussion.ts");
// 测试用例
var testCases = {
    // 测试获取课程讨论
    testGetDiscussionsByCourse: function () {
        return __awaiter(this, void 0, void 0, function () {
            var response, error_1;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        console.log('测试获取课程讨论...');
                        return [4 /*yield*/, discussion_ts_1.default.getDiscussionsByCourse(1)];
                    case 1:
                        response = _a.sent();
                        console.log('✅ 获取课程讨论成功:', response.data);
                        return [2 /*return*/, true];
                    case 2:
                        error_1 = _a.sent();
                        console.error('❌ 获取课程讨论失败:', error_1);
                        return [2 /*return*/, false];
                    case 3: return [2 /*return*/];
                }
            });
        });
    },
    // 测试获取班级讨论
    testGetDiscussionsByClass: function () {
        return __awaiter(this, void 0, void 0, function () {
            var response, error_2;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        console.log('测试获取班级讨论...');
                        return [4 /*yield*/, discussion_ts_1.default.getDiscussionsByClass(1)];
                    case 1:
                        response = _a.sent();
                        console.log('✅ 获取班级讨论成功:', response.data);
                        return [2 /*return*/, true];
                    case 2:
                        error_2 = _a.sent();
                        console.error('❌ 获取班级讨论失败:', error_2);
                        return [2 /*return*/, false];
                    case 3: return [2 /*return*/];
                }
            });
        });
    },
    // 测试创建讨论
    testCreateDiscussion: function () {
        return __awaiter(this, void 0, void 0, function () {
            var response, error_3;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        console.log('测试创建讨论...');
                        return [4 /*yield*/, discussion_ts_1.default.createDiscussion(1, 1, {
                                title: '测试讨论标题',
                                content: '这是一个测试讨论的内容'
                            })];
                    case 1:
                        response = _a.sent();
                        console.log('✅ 创建讨论成功:', response.data);
                        return [2 /*return*/, response.data.id];
                    case 2:
                        error_3 = _a.sent();
                        console.error('❌ 创建讨论失败:', error_3);
                        return [2 /*return*/, null];
                    case 3: return [2 /*return*/];
                }
            });
        });
    },
    // 测试获取讨论详情
    testGetDiscussionDetail: function (discussionId) {
        return __awaiter(this, void 0, void 0, function () {
            var response, error_4;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        console.log('测试获取讨论详情...');
                        return [4 /*yield*/, discussion_ts_1.default.getDiscussionDetail(discussionId)];
                    case 1:
                        response = _a.sent();
                        console.log('✅ 获取讨论详情成功:', response.data);
                        return [2 /*return*/, true];
                    case 2:
                        error_4 = _a.sent();
                        console.error('❌ 获取讨论详情失败:', error_4);
                        return [2 /*return*/, false];
                    case 3: return [2 /*return*/];
                }
            });
        });
    },
    // 测试置顶功能
    testPinDiscussion: function (discussionId) {
        return __awaiter(this, void 0, void 0, function () {
            var error_5;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 3, , 4]);
                        console.log('测试置顶功能...');
                        return [4 /*yield*/, discussion_ts_1.default.updateDiscussionPinStatus(discussionId, true)];
                    case 1:
                        _a.sent();
                        console.log('✅ 置顶讨论成功');
                        // 取消置顶
                        return [4 /*yield*/, discussion_ts_1.default.updateDiscussionPinStatus(discussionId, false)];
                    case 2:
                        // 取消置顶
                        _a.sent();
                        console.log('✅ 取消置顶成功');
                        return [2 /*return*/, true];
                    case 3:
                        error_5 = _a.sent();
                        console.error('❌ 置顶功能测试失败:', error_5);
                        return [2 /*return*/, false];
                    case 4: return [2 /*return*/];
                }
            });
        });
    },
    // 测试关闭功能
    testCloseDiscussion: function (discussionId) {
        return __awaiter(this, void 0, void 0, function () {
            var error_6;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 3, , 4]);
                        console.log('测试关闭功能...');
                        return [4 /*yield*/, discussion_ts_1.default.updateDiscussionCloseStatus(discussionId, true)];
                    case 1:
                        _a.sent();
                        console.log('✅ 关闭讨论成功');
                        // 重新开启
                        return [4 /*yield*/, discussion_ts_1.default.updateDiscussionCloseStatus(discussionId, false)];
                    case 2:
                        // 重新开启
                        _a.sent();
                        console.log('✅ 开启讨论成功');
                        return [2 /*return*/, true];
                    case 3:
                        error_6 = _a.sent();
                        console.error('❌ 关闭功能测试失败:', error_6);
                        return [2 /*return*/, false];
                    case 4: return [2 /*return*/];
                }
            });
        });
    },
    // 测试回复功能
    testCreateReply: function (discussionId) {
        return __awaiter(this, void 0, void 0, function () {
            var response, error_7;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        console.log('测试创建回复...');
                        return [4 /*yield*/, discussion_ts_1.default.createReply(discussionId, {
                                content: '这是一个测试回复'
                            })];
                    case 1:
                        response = _a.sent();
                        console.log('✅ 创建回复成功:', response.data);
                        return [2 /*return*/, response.data.id];
                    case 2:
                        error_7 = _a.sent();
                        console.error('❌ 创建回复失败:', error_7);
                        return [2 /*return*/, null];
                    case 3: return [2 /*return*/];
                }
            });
        });
    },
    // 测试获取回复列表
    testGetReplies: function (discussionId) {
        return __awaiter(this, void 0, void 0, function () {
            var response, error_8;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        console.log('测试获取回复列表...');
                        return [4 /*yield*/, discussion_ts_1.default.getTopLevelReplies(discussionId)];
                    case 1:
                        response = _a.sent();
                        console.log('✅ 获取回复列表成功:', response.data);
                        return [2 /*return*/, true];
                    case 2:
                        error_8 = _a.sent();
                        console.error('❌ 获取回复列表失败:', error_8);
                        return [2 /*return*/, false];
                    case 3: return [2 /*return*/];
                }
            });
        });
    },
    // 测试删除讨论
    testDeleteDiscussion: function (discussionId) {
        return __awaiter(this, void 0, void 0, function () {
            var error_9;
            return __generator(this, function (_a) {
                switch (_a.label) {
                    case 0:
                        _a.trys.push([0, 2, , 3]);
                        console.log('测试删除讨论...');
                        return [4 /*yield*/, discussion_ts_1.default.deleteDiscussion(discussionId)];
                    case 1:
                        _a.sent();
                        console.log('✅ 删除讨论成功');
                        return [2 /*return*/, true];
                    case 2:
                        error_9 = _a.sent();
                        console.error('❌ 删除讨论失败:', error_9);
                        return [2 /*return*/, false];
                    case 3: return [2 /*return*/];
                }
            });
        });
    }
};
// 运行所有测试
function runAllTests() {
    return __awaiter(this, void 0, void 0, function () {
        var passedTests, totalTests, discussionId, replyId;
        return __generator(this, function (_a) {
            switch (_a.label) {
                case 0:
                    console.log('🚀 开始运行讨论API集成测试...\n');
                    passedTests = 0;
                    totalTests = 0;
                    // 测试获取功能
                    totalTests++;
                    return [4 /*yield*/, testCases.testGetDiscussionsByCourse()];
                case 1:
                    if (_a.sent())
                        passedTests++;
                    totalTests++;
                    return [4 /*yield*/, testCases.testGetDiscussionsByClass()];
                case 2:
                    if (_a.sent())
                        passedTests++;
                    return [4 /*yield*/, testCases.testCreateDiscussion()];
                case 3:
                    discussionId = _a.sent();
                    totalTests++;
                    if (!discussionId) return [3 /*break*/, 10];
                    passedTests++;
                    // 测试获取详情
                    totalTests++;
                    return [4 /*yield*/, testCases.testGetDiscussionDetail(discussionId)];
                case 4:
                    if (_a.sent())
                        passedTests++;
                    // 测试置顶功能
                    totalTests++;
                    return [4 /*yield*/, testCases.testPinDiscussion(discussionId)];
                case 5:
                    if (_a.sent())
                        passedTests++;
                    // 测试关闭功能
                    totalTests++;
                    return [4 /*yield*/, testCases.testCloseDiscussion(discussionId)];
                case 6:
                    if (_a.sent())
                        passedTests++;
                    return [4 /*yield*/, testCases.testCreateReply(discussionId)];
                case 7:
                    replyId = _a.sent();
                    totalTests++;
                    if (replyId)
                        passedTests++;
                    // 测试获取回复
                    totalTests++;
                    return [4 /*yield*/, testCases.testGetReplies(discussionId)];
                case 8:
                    if (_a.sent())
                        passedTests++;
                    // 最后删除测试数据
                    totalTests++;
                    return [4 /*yield*/, testCases.testDeleteDiscussion(discussionId)];
                case 9:
                    if (_a.sent())
                        passedTests++;
                    _a.label = 10;
                case 10:
                    console.log("\n\uD83D\uDCCA \u6D4B\u8BD5\u5B8C\u6210: ".concat(passedTests, "/").concat(totalTests, " \u4E2A\u6D4B\u8BD5\u901A\u8FC7"));
                    if (passedTests === totalTests) {
                        console.log('🎉 所有测试都通过了！API集成成功！');
                    }
                    else {
                        console.log('⚠️  部分测试失败，请检查API配置和后端服务');
                    }
                    return [2 /*return*/, { passed: passedTests, total: totalTests }];
            }
        });
    });
}
// 单独测试函数导出
exports.default = testCases;
