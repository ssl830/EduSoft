import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import AuthApi from '../api/auth'

interface User {
  id: string;
  username: string;
  email: string;
  role: string;
}

interface RegisterData {
  username: string;
  email: string;
  password: string;
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
  const login = async (username: string, password: string) => {
    try {
      const response = await AuthApi.login({ username, password })
      user.value = response.data.user
      token.value = response.data.token
      
      // Save to localStorage
      localStorage.setItem('user', JSON.stringify(user.value))
      localStorage.setItem('token', token.value)
      
      return response.data
    } catch (error) {
      throw error
    }
  }
  
  const register = async (registerData: RegisterData) => {
    try {
      const response = await AuthApi.register(registerData)
      user.value = response.data.user
      token.value = response.data.token
      
      // Save to localStorage
      localStorage.setItem('user', JSON.stringify(user.value))
      localStorage.setItem('token', token.value)
      
      return response.data
    } catch (error) {
      throw error
    }
  }
  
  const logout = async () => {
    try {
      // Call logout API if needed
      // await AuthApi.logout()
      
      // Clear local state
      user.value = null
      token.value = null
      
      // Clear localStorage
      localStorage.removeItem('user')
      localStorage.removeItem('token')
    } catch (error) {
      console.error('Logout error:', error)
      throw error
    }
  }
  
  const updateProfile = async (profileData: Partial<User>) => {
    try {
      const response = await AuthApi.updateProfile(profileData)
      user.value = { ...user.value, ...response.data.user }
      
      // Update localStorage
      localStorage.setItem('user', JSON.stringify(user.value))
      
      return response.data
    } catch (error) {
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
    updateProfile
  }
})