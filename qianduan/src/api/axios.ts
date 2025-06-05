import axios from 'axios'

// Create an axios instance
const instance = axios.create({
  baseURL: 'http://127.0.0.1:4523/m1/6341276-6036787-default',  // APIFox Mock 地址
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  }
})

// Add a request interceptor
instance.interceptors.request.use(
  config => {
    // Get token from localStorage
    const token = localStorage.getItem('token')    // If token exists, add to headers
    if (token) {
      config.headers.token = token  // APIFox 模拟环境使用 token 头
    }

    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// Add a response interceptor
instance.interceptors.response.use(
  response => {
    return response
  },
  error => {
    // Handle common errors
    if (error.response) {
      // The request was made and the server responded with a status code
      // that falls out of the range of 2xx
      const { status } = error.response

      if (status === 401) {
        // Unauthorized - clear local storage and redirect to login
        localStorage.removeItem('token')
        localStorage.removeItem('user')

        // If in browser environment and not already on login page
        if (typeof window !== 'undefined' && window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      }
    }

    return Promise.reject(error)
  }
)

export default instance
