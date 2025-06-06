import apiClient from './axios'

// 用户信息接口
export interface User {
  id: number;
  userId: string;
  username: string;
  passwordHash?: string; // 密码只在后端使用，前端不返回
  role: 'student' | 'teacher' | 'admin';
  email: string;
  createdAt: string;
  updatedAt: string;
}

// 用户注册请求接口
export interface RegisterRequest {
  username: string;
  passwordHash: string;
  role: 'student' | 'teacher' | 'admin';
  email: string;
  name?: string;
  userId: string;
}

// 用户登录请求接口
export interface LoginRequest {
  userId: string;
  password: string;
}

// 用户登录响应接口
export interface LoginResponse {
  userInfo: {
    role: string;
    id: number;
    userid: string;
    email: string;
    username: string;
  };
  token: string;
}

// 更新用户信息请求接口
export interface UpdateUserRequest {
  email?: string;
  username?: string;
}

// 修改密码请求接口
export interface ChangePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

// API响应接口
export interface ApiResponse<T> {
  code: number;
  msg: string;
  data: T;
}

const userApi = {
  // 注册用户
  register: (data: RegisterRequest) => {
    return apiClient.post<ApiResponse<null>>('/api/user/register', data);
  },

  // 用户登录
  login: (data: LoginRequest) => {
    return apiClient.post<ApiResponse<LoginResponse>>('/api/user/login', data);
  },

  // 退出登录
  logout: () => {
    return apiClient.post<ApiResponse<null>>('/api/user/logout');
  },

  // 获取当前用户信息
  getUserInfo: () => {
    return apiClient.get<ApiResponse<User>>('/api/user/info');
  },

  // 更新用户信息
  updateUserInfo: (data: UpdateUserRequest) => {
    return apiClient.post<ApiResponse<null>>('/api/user/update', data);
  },

  // 修改密码
  changePassword: (data: ChangePasswordRequest) => {
    return apiClient.post<ApiResponse<null>>('/api/user/changePassword', data);
  }
};

export default userApi;