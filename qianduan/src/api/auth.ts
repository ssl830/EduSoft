import axios from './axios'

const AuthApi = {
  // Login
  login(data: { username: string; password: string }) {
    return axios.post('/users/login', data)
  },
  
  // Register
  register(data: { 
    username: string; 
    email: string; 
    password: string;
    role: string;
  }) {
    return axios.post('/users/register', data)
  },
  
  // Update Profile
  updateProfile(data: {
    email?: string;
    bio?: string;
    avatar?: string;
  }) {
    return axios.put('/users/profile', data)
  },
  
  // Change Password
  changePassword(data: {
    oldPassword: string;
    newPassword: string;
  }) {
    return axios.post('/users/change-password', data)
  },
  
  // Get Current User Profile
  getProfile() {
    return axios.get('/users/profile')
  }
}

export default AuthApi