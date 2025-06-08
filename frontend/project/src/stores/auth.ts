import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authApi from '../api/auth'
import apiClient from '../api/axios'

interface User {
  id: number;
  userId: string;
  username: string;
  email: string;
  role: string;
}

export const useAuthStore = defineStore('auth', () => {
  // State
  const user = ref<User | null>(null)
  const token = ref<string | null>(null)

  // Initialize state from localStorage
  if (typeof window !== 'undefined') {
    const savedUser = localStorage.getItem('user')
    const savedToken = localStorage.getItem('free-fs-token')

    if (savedUser) user.value = JSON.parse(savedUser)
    if (savedToken) token.value = savedToken
  }

  // Computed
  const isAuthenticated = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || '')

  // Actions
  const login = async (userId: string, password: string) => {
    try {
      const response = await authApi.login({ userId, password })

      if (response.code === 200) {
        const userData = {
          id: response.data.userInfo.id,
          userId: response.data.userInfo.userId,
          username: response.data.userInfo.username,
          email: response.data.userInfo.email,
          role: response.data.userInfo.role
        }

        user.value = userData
        token.value = response.data.token

        // Save to localStorage
        localStorage.setItem('user', JSON.stringify(userData))
        localStorage.setItem('free-fs-token', response.data.token)

        return response
      } else {
        throw new Error(response.msg || '登录失败')
      }
    } catch (error) {
      console.error('Login error:', error)
      throw error
    }
  }

  const register = async (data: {
    username: string;
    password: string;
    role: string;
    email: string;
    name?: string;
    userId: string;
  }) => {
    try {
      const registerData = {
        ...data,
        passwordHash: data.password // 后端需要 passwordHash 而不是 password
      }
      delete (registerData as any).password // 删除 password 字段

      const response = await authApi.register(registerData)

      if (response.code === 200) {
        return response
      } else {
        throw new Error(response.msg || '注册失败')
      }
    } catch (error) {
      console.error('Register error:', error)
      throw error
    }
  }

  const logout = async () => {
    // 先检查是否有token
    const currentToken = localStorage.getItem('free-fs-token');
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
      return;
    }

    try {
      // 发送退出登录请求
      await authApi.logout();
    } catch (error) {
      console.error('退出登录请求失败:', error);
    } finally {
      // 无论请求成功与否，都清除所有数据
      localStorage.removeItem('free-fs-token');
      localStorage.removeItem('userInfo');
      localStorage.removeItem('user');
      user.value = null;
      token.value = null;
      // 重定向到登录页
      window.location.href = '/login';
    }
  }
  const fetchUserInfo = async () => {
    try {
      const response = await authApi.getProfile()

      console.log('获取用户信息响应:', response);

      // 检查响应数据
      if (response && response.code === 200) {
        // 确保response.data存在
        if (!response.data) {
          console.error('API返回数据为空');
          throw new Error('获取用户信息失败：返回数据为空');
        }

        const userData = {
          id: response.data.id,
          userId: response.data.userId,
          username: response.data.username,
          email: response.data.email,
          role: response.data.role,
          createdAt: response.data.createdAt,
          updatedAt: response.data.updatedAt
        }

        console.log('处理后的用户数据:', userData);

        // 更新状态
        user.value = userData
        // 更新本地存储
        localStorage.setItem('user', JSON.stringify(userData))

        return userData
      } else {
        console.error('API返回错误:', response);
        throw new Error(response?.msg || '获取用户信息失败');
      }
    } catch (error: any) {
      console.error('获取用户信息错误:', error);

      if (error.response) {
        // API返回了错误响应
        console.error('API错误响应:', error.response);
        throw new Error(error.response.data?.msg || '服务器错误');
      } else if (error.request) {
        // 请求发出但没有收到响应
        console.error('网络错误:', error.request);
        throw new Error('网络连接失败，请检查网络连接');
      } else {
        // 其他错误
        console.error('其他错误:', error);
        throw error;
      }
    }
  }
  const updateProfile = async (data: {
    email?: string;
    username?: string;
    bio?: string;
  }) => {
    try {
      const response = await authApi.updateProfile(data)

      if (response.code === 200) {
        // 更新成功后重新获取用户信息
        await fetchUserInfo()
        return response
      } else {
        throw new Error(response.msg || '更新用户信息失败')
      }
    } catch (error) {
      console.error('Update profile error:', error)
      throw error
    }
  }
  const changePassword = async (oldPassword: string, newPassword: string) => {
    try {
      console.log('=== 开始修改密码 ===');
      console.log('auth store中的密码数据:', { oldPassword, newPassword });

      // 直接使用URLSearchParams构建请求数据
      const formData = new URLSearchParams();
      formData.append('oldPassword', oldPassword);
      formData.append('newPassword', newPassword);
      
      console.log('构建的表单数据:', formData.toString());
      console.log('发送请求到:', '/api/user/changePassword');

      const response = await apiClient.post('/api/user/changePassword', formData, {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      });

      console.log('密码修改响应:', response);
      console.log('=== 修改密码结束 ===');

      if (response.code === 200) {
        return response
      } else {
        throw new Error(response.msg || '修改密码失败')
      }
    } catch (error) {
      console.error('Change password error:', error)
      throw error
    }
  }
  const uploadAvatar = async (formData: FormData) => {
    try {
      const response = await authApi.uploadAvatar(formData);
      console.log('上传头像响应:', response);

      if (response.code === 200) {
        // 更新本地存储的用户信息
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        userInfo.avatar = response.data.avatar;
        localStorage.setItem('userInfo', JSON.stringify(userInfo));

        return response.data;
      } else {
        throw new Error(response.msg || '上传头像失败');
      }
    } catch (error: any) {
      console.error('上传头像失败:', error);
      throw error;
    }
  }

  const clearUserData = () => {
    user.value = null;
    token.value = null;
    localStorage.removeItem('user');
    localStorage.removeItem('free-fs-token');
    localStorage.removeItem('userInfo');
  }

  return {
    user,
    token,
    isAuthenticated,
    userRole,
    login,
    register,
    logout,
    fetchUserInfo,
    updateProfile,
    changePassword,
    uploadAvatar,
    clearUserData
  }
})