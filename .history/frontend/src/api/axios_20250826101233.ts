import axios from 'axios'
import { useAuthStore } from '../stores/auth'

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
}

// Create an axios instance
const instance = axios.create({
  baseURL: 'http://localhost:8080',  // 修改为本地后端服务器地址
  timeout: 1000000,
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

      const data = response.data;
      // 判断是否已经是标准格式（有 code 和 data 字段）
      if (
        data &&
        typeof data === 'object' &&
        'code' in data &&
        'data' in data
      ) {
        return data;
      } else {
        // 否则包装成统一格式
        return {
          code: response.status,
          message: '请求成功',
          data: data
        };
      }
    },
    error => {
      if (error.response) {
        // 处理401错误
        if (error.response.status === 401) {
          console.log('未授权，清除用户数据');
          const authStore = useAuthStore()
          authStore.clearUserData()
          // 重定向到登录页
          window.location.href = '/login';
        }
        console.error('响应错误:', error.response.status);
        console.error('错误数据:', error.response.data);

        // 修正：兼容Spring Boot静态资源404和message字段
        let msg = error.response.data?.msg || error.response.data?.message || '请求失败';
        // 针对静态资源404的特殊提示
        if (
          error.response.status === 500 &&
          typeof msg === 'string' &&
          msg.includes('No static resource')
        ) {
          msg = '后端未正确转发到AI微服务，请检查Spring Boot路由配置或接口路径。';
        }
        return Promise.reject({
          code: error.response.status,
          msg,
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
