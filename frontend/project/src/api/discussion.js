"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var axios_1 = require("./axios");
// ==================== API 接口函数 ====================
var discussionApi = {
    // 1. 创建讨论帖
    createDiscussion: function (courseId, classId, data) {
        return axios_1.default.post("/api/discussion/course/".concat(courseId, "/class/").concat(classId), data);
    },
    // 2. 更新讨论帖
    updateDiscussion: function (discussionId, data) {
        return axios_1.default.put("/api/discussion/".concat(discussionId), data);
    },
    // 3. 删除讨论帖
    deleteDiscussion: function (discussionId) {
        return axios_1.default.delete("/api/discussion/".concat(discussionId));
    },
    // 4. 获取讨论帖详情
    getDiscussionDetail: function (discussionId) {
        return axios_1.default.get("/api/discussion/".concat(discussionId));
    },
    // 5. 获取课程下的讨论列表
    getDiscussionListByCourse: function (courseId) {
        return axios_1.default.get("/api/discussion/course/".concat(courseId));
    },
    // 6. 获取班级下的讨论列表
    getDiscussionListByClass: function (classId) {
        return axios_1.default.get("/api/discussion/class/".concat(classId));
    },
    // 7. 更新讨论置顶状态
    pinDiscussion: function (discussionId, isPinned) {
        return axios_1.default.put("/api/discussion/".concat(discussionId, "/pin?isPinned=").concat(isPinned));
    },
    // 8. 更新讨论关闭状态
    closeDiscussion: function (discussionId, isClosed) {
        return axios_1.default.put("/api/discussion/".concat(discussionId, "/close?isClosed=").concat(isClosed));
    },
    // ==================== 回复相关接口 ====================
    // 1. 创建回复
    createReply: function (discussionId, data) {
        return axios_1.default.post("/api/discussion-reply/discussion/".concat(discussionId), data);
    },
    // 2. 更新回复
    updateReply: function (replyId, data) {
        return axios_1.default.put("/api/discussion-reply/".concat(replyId), data);
    },
    // 3. 删除回复
    deleteReply: function (replyId) {
        return axios_1.default.delete("/api/discussion-reply/".concat(replyId));
    },
    // 4. 获取回复详情
    getReplyDetail: function (replyId) {
        return axios_1.default.get("/api/discussion-reply/".concat(replyId));
    },
    // 5. 获取讨论下的所有回复
    getAllReplies: function (discussionId) {
        return axios_1.default.get("/api/discussion-reply/discussion/".concat(discussionId));
    },
    // 6. 获取讨论的顶层回复
    getTopLevelReplies: function (discussionId) {
        return axios_1.default.get("/api/discussion-reply/discussion/".concat(discussionId, "/top-level"));
    },
    // 7. 获取指定回复的子回复
    getChildReplies: function (parentReplyId) {
        return axios_1.default.get("/api/discussion-reply/parent/".concat(parentReplyId));
    },
    // 8. 获取讨论的教师回复
    getTeacherReplies: function (discussionId) {
        return axios_1.default.get("/api/discussion-reply/discussion/".concat(discussionId, "/teacher"));
    },
    // 9. 删除讨论的所有回复（教师权限）
    deleteAllReplies: function (discussionId) {
        return axios_1.default.delete("/api/discussion-reply/discussion/".concat(discussionId));
    },
    // 10. 获取回复的点赞用户列表
    getLikeUsers: function (replyId) {
        return axios_1.default.get("/api/discussion-reply/".concat(replyId, "/likes"));
    },
    // ==================== 点赞相关接口 ====================
    // 1. 点赞回复
    likeReply: function (replyId) {
        return axios_1.default.post("/api/discussion-reply/".concat(replyId, "/like"));
    },
    // 2. 取消点赞回复
    unlikeReply: function (replyId) {
        return axios_1.default.delete("/api/discussion-reply/".concat(replyId, "/like"));
    },
    // 3. 获取回复的点赞状态
    getLikeStatus: function (replyId) {
        return axios_1.default.get("/api/discussion-reply/".concat(replyId, "/like-status"));
    }
};
exports.default = discussionApi;
