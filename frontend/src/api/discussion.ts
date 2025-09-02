import apiClient from './axios';

// ==================== 接口类型定义 ====================

// 讨论帖信息
export interface Discussion {
  id: number;
  title: string;
  content: string;
  courseId: number;
  classId: number;
  creatorId: number;
  creatorNum: string;
  replyCount: number;
  viewCount: number;
  isPinned: boolean;
  isClosed: boolean;
  createdAt: string;
  updatedAt: string;
}

// 创建讨论帖的请求数据
export interface CreateDiscussionRequest {
  title: string;
  content: string;
}

// 更新讨论帖的请求数据
export interface UpdateDiscussionRequest {
  title?: string;
  content?: string;
}

// 分页查询参数
export interface DiscussionListParams {
  page?: number;
  size?: number;
  keyword?: string;
  sortBy?: 'createdAt' | 'replyCount' | 'viewCount';
  sortOrder?: 'asc' | 'desc';
}

// 分页响应结果
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  first: boolean;
  last: boolean;
}

// 管理员操作请求数据
export interface AdminActionRequest {
  isPinned?: boolean;
  isClosed?: boolean;
}

// 回复信息
export interface DiscussionReply {
  id: number;
  content: string;
  creatorId: number;
  creatorNum: string;
  discussionId: number;
  parentReplyId?: number;
  isTeacher: boolean;
  createdAt: string;
  updatedAt: string;
  children?: DiscussionReply[];
  likeCount?: number;
  likedByMe?: boolean;
}

// 后端返回的回复数据结构
export interface BackendDiscussionReply {
  id: number;
  content: string;
  userId: number;
  userNum: string;
  discussionId: number;
  parentReplyId: number | null;
  isTeacherReply: boolean;
  createdAt: string;
  updatedAt: string;
  likeCount: number;
  likedByMe: boolean;
}

// 创建回复的请求数据
export interface CreateReplyRequest {
  content: string;
  parentReplyId?: number;
}

// 更新回复的请求数据
export interface UpdateReplyRequest {
  content: string;
}

// 通用成功响应
export interface SuccessResponse {
  message: string;
}

// 错误响应
export interface ErrorResponse {
  error: string;
}

// ==================== API 接口函数 ====================

const discussionApi = {
  // 1. 创建讨论帖
  createDiscussion: (courseId: number, classId: number, data: CreateDiscussionRequest) => {
    return apiClient.post<Discussion>(`/api/discussion/course/${courseId}/class/${classId}`, data);
  },

  // 2. 更新讨论帖
  updateDiscussion: (discussionId: number, data: UpdateDiscussionRequest) => {
    return apiClient.put<Discussion>(`/api/discussion/${discussionId}`, data);
  },

  // 3. 删除讨论帖
  deleteDiscussion: (discussionId: number) => {
    return apiClient.delete<SuccessResponse>(`/api/discussion/${discussionId}`);
  },

  // 4. 获取讨论帖详情
  getDiscussionDetail: (discussionId: number) => {
    return apiClient.get<Discussion>(`/api/discussion/${discussionId}`);
  },

  // 5. 获取课程下的讨论列表
  getDiscussionListByCourse: (courseId: number) => {
    return apiClient.get<Discussion[]>(`/api/discussion/course/${courseId}`);
  },

  // 6. 获取班级下的讨论列表
  getDiscussionListByClass: (classId: number) => {
    return apiClient.get<Discussion[]>(`/api/discussion/class/${classId}`);
  },

  // 7. 更新讨论置顶状态
  pinDiscussion: (discussionId: number, isPinned: boolean) => {
    return apiClient.put<SuccessResponse>(`/api/discussion/${discussionId}/pin?isPinned=${isPinned}`);
  },

  // 8. 更新讨论关闭状态
  closeDiscussion: (discussionId: number, isClosed: boolean) => {
    return apiClient.put<SuccessResponse>(`/api/discussion/${discussionId}/close?isClosed=${isClosed}`);
  },

  // ==================== 回复相关接口 ====================

  // 1. 创建回复
  createReply: (discussionId: number, data: CreateReplyRequest) => {
    // 后端接口定义为 POST /api/discussion-reply，Body 需包含 discussionId
    return apiClient.post<BackendDiscussionReply>(`/api/discussion-reply`, { discussionId, ...data });
  },

  // 2. 更新回复
  updateReply: (replyId: number, data: UpdateReplyRequest) => {
    return apiClient.put<BackendDiscussionReply>(`/api/discussion-reply/${replyId}`, data);
  },

  // 3. 删除回复
  deleteReply: (replyId: number) => {
    return apiClient.delete<SuccessResponse>(`/api/discussion-reply/${replyId}`);
  },

  // 4. 获取回复详情
  getReplyDetail: (replyId: number) => {
    return apiClient.get<BackendDiscussionReply>(`/api/discussion-reply/${replyId}`);
  },

  // 5. 获取讨论下的所有回复
  getAllReplies: (discussionId: number) => {
    return apiClient.get<BackendDiscussionReply[]>(`/api/discussion-reply/discussion/${discussionId}`);
  },

  // 6. 获取讨论的顶层回复
  getTopLevelReplies: (discussionId: number) => {
    return apiClient.get<BackendDiscussionReply[]>(`/api/discussion-reply/discussion/${discussionId}/top-level`);
  },

  // 7. 获取指定回复的子回复
  getChildReplies: (parentReplyId: number) => {
    return apiClient.get<BackendDiscussionReply[]>(`/api/discussion-reply/parent/${parentReplyId}`);
  },

  // 8. 获取讨论的教师回复
  getTeacherReplies: (discussionId: number) => {
    return apiClient.get<BackendDiscussionReply[]>(`/api/discussion-reply/discussion/${discussionId}/teacher`);
  },

  // 9. 删除讨论的所有回复（教师权限）
  deleteAllReplies: (discussionId: number) => {
    return apiClient.delete<SuccessResponse>(`/api/discussion-reply/discussion/${discussionId}`);
  },

  // 10. 获取回复的点赞用户列表
  getLikeUsers: (replyId: number) => {
    return apiClient.get<{userNum: string; userId: number}[]>(`/api/discussion-reply/${replyId}/likes`);
  },

  // ==================== 点赞相关接口 ====================
  
  // 1. 点赞回复
  likeReply: (replyId: number) => {
    return apiClient.post<SuccessResponse>(`/api/discussion-reply/${replyId}/like`);
  },

  // 2. 取消点赞回复
  unlikeReply: (replyId: number) => {
    return apiClient.delete<SuccessResponse>(`/api/discussion-reply/${replyId}/like`);
  },

  // 3. 获取回复的点赞状态
  getLikeStatus: (replyId: number) => {
    return apiClient.get<{liked: boolean; count: number}>(`/api/discussion-reply/${replyId}/like-status`);
  }
};

export default discussionApi;

