import axios from './axios'

interface LoginResponse {
  code: number;
  msg: string;
  data: {
    userInfo: {
      role: string;
      id: number;
      userid: string;
      email: string;
      username: string;
    };
    token: string;
  };
}

interface UserResponse {
  code: number;
  msg: string;
  data: {
    id: number;
    userId: string;
    username: string;
    passwordHash: string | null;
    role: string;
    email: string;
    createdAt: string;
    updatedAt: string;
  };
}

interface CommonResponse {
  code: number;
  msg: string;
  data: null;
}

const AuthApi = {
  // Login
  login(data: { userId: string; password: string }) {
    // 直接使用形如 userId=xxx&password=xxx 的格式
    const formData = new URLSearchParams();
    formData.append('userId', data.userId);
    formData.append('password', data.password);

    return axios.post<LoginResponse>('/api/user/login', formData, {
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded'
      }
    })
  },

  // Register
  register(data: {
    username: string;
    passwordHash: string;
    role: string;
    email: string;
    name?: string;
    userId: string;
  }) {
    return axios.post<CommonResponse>('/api/user/register', data)
  },

  // Update Profile
  updateProfile(data: {
    email?: string;
    username?: string;
  }) {
    return axios.post<CommonResponse>('/api/user/update', data)
  },

  // Change Password
  changePassword(data: {
    oldPassword: string;
    newPassword: string;
  }) {
    return axios.post<CommonResponse>('/api/user/changePassword', data)
  },

  // Get Current User Profile
  getProfile() {
    return axios.get<UserResponse>('/api/user/info')
  },

  // Logout
  logout() {
    return axios.post<CommonResponse>('/api/user/logout')
  }
}

export default AuthApi
