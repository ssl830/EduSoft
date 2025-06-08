import axios from 'axios'
import { useAuthStore } from '../stores/auth'
//
// interface ApiResponse<T> {
//   code: number;
//   message: string;
//   data: T;
// }

// Create an axios instance
const instance = axios.create({
  baseURL: 'http://localhost:8080',  // 修改为本地后端服务器地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// Add a request interceptor
instance.interceptors.request.use(
    config => {
      // 确保所有请求路径都以/开头
      if (config.url && !config.url.startsWith('/')) {
        config.url = '/' + config.url;
      }

      const authStore = useAuthStore()
      if (authStore.token) {
        config.headers['free-fs-token'] = authStore.token
      } else if (!config.url?.includes('/login') && !config.url?.includes('/register')) {
        // 对于非登录和注册请求，如果没有token，记录日志
        console.log('未找到token，请求:', config.url)
      }

      return config
    },
    error => {
      console.error('请求错误:', error)
      return Promise.reject(error)
    }
)

// Add a response interceptor
instance.interceptors.response.use(
    response => {
      // 记录响应信息
      console.log('响应状态:', response.status);
      console.log('响应URL:', response.config.url);
      console.log('响应数据:', response.data);

      // 检查响应格式
      if (response.data && typeof response.data === 'object') {
        // 如果响应数据是对象，直接返回
        return response.data;
      } else {
        // 如果响应数据不是对象，包装成标准格式
        return {
          code: response.status,
          msg: '请求成功',
          data: response.data
        };
      }
    },
    error => {
      if (error.response) {
        // 处理401错误
        if (error.response.status === 401) {
          console.log('未授权，清除用户数据');
          // const authStore = useAuthStore()
          // authStore.clearUserData()
          // 重定向到登录页
          window.location.href = '/login';
        }
        console.error('响应错误:', error.response.status);
        console.error('错误数据:', error.response.data);

        // 返回标准错误格式
        return Promise.reject({
          code: error.response.status,
          msg: error.response.data?.msg || '请求失败',
          data: error.response.data
        });
      } else {
        console.error('请求错误:', error.message);
        return Promise.reject({
          code: 500,
          msg: error.message || '网络错误',
          data: null
        });
      }
    }
)

export default instance
