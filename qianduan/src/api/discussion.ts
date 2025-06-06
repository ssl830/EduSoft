import apiClient from './axios';

// ==================== 接口类型定义 ====================

// 讨论帖信息
export interface Discussion {
  id: number;
  title: string;
  content: string;
  authorId: number;
  authorName: string;
  courseId: number;
  courseName: string;
  isPinned: boolean;
  isClosed: boolean;
  replyCount: number;
  viewCount: number;
  likeCount: number;
  createdAt: string;
  updatedAt: string;
}

// 创建讨论帖的请求数据
export interface CreateDiscussionRequest {
  title: string;
  content: string;
  courseId: number;
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
  courseId?: number;
  keyword?: string;
  sortBy?: 'createdAt' | 'replyCount' | 'likeCount' | 'viewCount';
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
  authorId: number;
  authorName: string;
  discussionId: number;
  parentReplyId?: number;
  isTeacher: boolean;
  likeCount: number;
  createdAt: string;
  updatedAt: string;
  children?: DiscussionReply[]; // 子回复列表
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
  createDiscussion: (data: CreateDiscussionRequest) => {
    return apiClient.post<Discussion>('/api/discussions', data);
  },

  // 2. 更新讨论帖
  updateDiscussion: (discussionId: number, data: UpdateDiscussionRequest) => {
    return apiClient.put<Discussion>(`/api/discussions/${discussionId}`, data);
  },

  // 3. 删除讨论帖
  deleteDiscussion: (discussionId: number) => {
    return apiClient.delete(`/api/discussions/${discussionId}`);
  },

  // 4. 获取讨论帖详情
  getDiscussionDetail: (discussionId: number) => {
    return apiClient.get<Discussion>(`/api/discussions/${discussionId}`);
  },

  // 5. 分页查询讨论帖列表
  getDiscussionList: (params?: DiscussionListParams) => {
    return apiClient.get<PageResponse<Discussion>>('/api/discussions', { params });
  },

  // 6. 获取当前用户的讨论帖列表
  getMyDiscussions: (params?: { page?: number; size?: number }) => {
    return apiClient.get<PageResponse<Discussion>>('/api/discussions/my', { params });
  },

  // 7. 管理员置顶/取消置顶讨论帖
  pinDiscussion: (discussionId: number, isPinned: boolean) => {
    return apiClient.patch<Discussion>(`/api/discussions/${discussionId}/admin`, {
      isPinned
    });
  },
  // 8. 管理员关闭/开启讨论帖
  closeDiscussion: (discussionId: number, isClosed: boolean) => {
    return apiClient.patch<Discussion>(`/api/discussions/${discussionId}/admin`, {
      isClosed
    });
  },

  // ==================== 回复相关接口 ====================

  // 1. 创建回复
  createReply: (discussionId: number, data: CreateReplyRequest) => {
    return apiClient.post<DiscussionReply>(`/api/discussion-reply/discussion/${discussionId}`, data);
  },

  // 2. 更新回复
  updateReply: (replyId: number, data: UpdateReplyRequest) => {
    return apiClient.put<DiscussionReply>(`/api/discussion-reply/${replyId}`, data);
  },

  // 3. 删除回复
  deleteReply: (replyId: number) => {
    return apiClient.delete<SuccessResponse>(`/api/discussion-reply/${replyId}`);
  },

  // 4. 获取回复详情
  getReplyDetail: (replyId: number) => {
    return apiClient.get<DiscussionReply>(`/api/discussion-reply/${replyId}`);
  },

  // 5. 获取讨论下的所有回复
  getAllReplies: (discussionId: number) => {
    return apiClient.get<DiscussionReply[]>(`/api/discussion-reply/discussion/${discussionId}`);
  },

  // 6. 获取讨论的顶层回复
  getTopLevelReplies: (discussionId: number) => {
    return apiClient.get<DiscussionReply[]>(`/api/discussion-reply/discussion/${discussionId}/top-level`);
  },

  // 7. 获取指定回复的子回复
  getChildReplies: (parentReplyId: number) => {
    return apiClient.get<DiscussionReply[]>(`/api/discussion-reply/parent/${parentReplyId}`);
  },

  // 8. 获取讨论的教师回复
  getTeacherReplies: (discussionId: number) => {
    return apiClient.get<DiscussionReply[]>(`/api/discussion-reply/discussion/${discussionId}/teacher`);
  },

  // 9. 删除讨论的所有回复（教师权限）
  deleteAllReplies: (discussionId: number) => {
    return apiClient.delete<SuccessResponse>(`/api/discussion-reply/discussion/${discussionId}`);
  }
};

export default discussionApi;

