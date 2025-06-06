import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import authApi from '../api/auth'

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
    const savedToken = localStorage.getItem('token')

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

      if (response.data.code === 200) {
        const userData = {
          id: response.data.data.userInfo.id,
          userId: response.data.data.userInfo.userid,
          username: response.data.data.userInfo.username,
          email: response.data.data.userInfo.email,
          role: response.data.data.userInfo.role
        }

        user.value = userData
        token.value = response.data.data.token

        // Save to localStorage
        localStorage.setItem('user', JSON.stringify(userData))
        localStorage.setItem('token', response.data.data.token)

        return response.data
      } else {
        throw new Error(response.data.msg || '登录失败')
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

      if (response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data.msg || '注册失败')
      }
    } catch (error) {
      console.error('Register error:', error)
      throw error
    }
  }

  const logout = async () => {
    try {
      if (token.value) {
        const response = await authApi.logout()
        if (response.data.code !== 200) {
          console.warn('Logout API failed:', response.data.msg)
        }
      }

      // Clear local state
      user.value = null
      token.value = null

      // Clear localStorage
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    } catch (error) {
      console.error('Logout error:', error)
      // 即使API调用失败，也需要清除本地状态
      user.value = null
      token.value = null
      localStorage.removeItem('user')
      localStorage.removeItem('token')
      throw error
    }
  }

  const fetchUserInfo = async () => {
    try {
      const response = await authApi.getProfile()

      if (response.data.code === 200) {
        const userData = {
          id: response.data.data.id,
          userId: response.data.data.userId,
          username: response.data.data.username,
          email: response.data.data.email,
          role: response.data.data.role
        }

        user.value = userData
        // Update localStorage
        localStorage.setItem('user', JSON.stringify(userData))

        return response.data.data
      } else {
        throw new Error(response.data.msg || '获取用户信息失败')
      }
    } catch (error) {
      console.error('Fetch user info error:', error)
      throw error
    }
  }

  const updateProfile = async (data: {
    email?: string;
    username?: string;
  }) => {
    try {
      const response = await authApi.updateProfile(data)

      if (response.data.code === 200) {
        // 更新成功后重新获取用户信息
        await fetchUserInfo()
        return response.data
      } else {
        throw new Error(response.data.msg || '更新用户信息失败')
      }
    } catch (error) {
      console.error('Update profile error:', error)
      throw error
    }
  }

  const changePassword = async (oldPassword: string, newPassword: string) => {
    try {
      const response = await authApi.changePassword({ oldPassword, newPassword })

      if (response.data.code === 200) {
        return response.data
      } else {
        throw new Error(response.data.msg || '密码修改失败')
      }
    } catch (error) {
      console.error('Change password error:', error)
      throw error
    }
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
    changePassword
  }
})
