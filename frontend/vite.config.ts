import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { transformAssetUrls } from '@quasar/vite-plugin'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import { resolve } from 'path'

// https://vitejs.dev/config/
export default defineConfig({
  base: './', // 确保资源路径正确
  plugins: [
    vue({
      template: { transformAssetUrls }
    }),
    // quasar({
    //   sassVariables: 'src/quasar-variables.sass'
    // }),
    AutoImport({
      resolvers: [ElementPlusResolver()],
    }),
    Components({
      resolvers: [ElementPlusResolver()],
    }),  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      '@popperjs/core/lib/modifiers/offset.js': resolve('./node_modules/@popperjs/core/dist/esm/modifiers/offset.js'),
    }
  },
  server: {
    port: 3000, // 修改为 3000 端口
    fs: {
      allow: ['..'] // 允许访问上级目录
    },
    hmr: {
      overlay: false // 禁用热更新错误覆盖
    },
    proxy: {
      '/api/user': { 
        target: process.env.NODE_ENV === 'development' 
          ? 'http://localhost:8081'
          : 'http://user-service:8081', 
        changeOrigin: true,
        rewrite: path => path.replace(/^\/api\/user/, '/')
      },
      '/api/imports': { target: 'http://course-service:8082', changeOrigin: true },
      '/api/courses': { target: 'http://course-service:8082', changeOrigin: true },
      '/api/classes': { target: 'http://course-service:8082', changeOrigin: true },
      // TODO: 按实际路径调整 service3 前缀
      '/service3': { target: 'http://content-service:8083', changeOrigin: true },
      // content-service 代理配置
      '/api/content/notifications': { target: 'http://content-service:8083', changeOrigin: true },
      '/api/content/task-reminders': { target: 'http://content-service:8083', changeOrigin: true },
       // learning-service 相关接口 (8084端口)
      '/api/resources': { target: 'http://content-service:8083', changeOrigin: true },
      // 添加通用的content-service代理规则，确保所有/api/content/*路径都能正确转发
      '/api/content': { target: 'http://content-service:8083', changeOrigin: true },
      // learning-service 代理配置
      '/api/judge': { target: 'http://learning-service:8084', changeOrigin: true },
      '/api/record': { target: 'http://learning-service:8084', changeOrigin: true },
      '/api/practice': { target: 'http://learning-service:8084', changeOrigin: true },
      '/api/selfpractice': { target: 'http://learning-service:8084', changeOrigin: true },
      '/api/submission': { target: 'http://learning-service:8084', changeOrigin: true },
      '/api/learning': { target: 'http://learning-service:8084', changeOrigin: true },
      
      // 微服务健康检查 - 优先匹配 learning-service 的健康检查
      '/api/health/services': { target: 'http://learning-service:8084', changeOrigin: true },
      '/api/health/check': { target: 'http://learning-service:8084', changeOrigin: true },
      // AI服务代理 - 将前端的 /api/ai/* 请求转发到 learning-service 的 /api/learning/ai/*
      '/api/ai': { 
        target: 'http://learning-service:8084', 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/ai/, '/api/learning/ai')
      },
      // 聊天服务代理 - 将前端的 /api/chat/* 请求转发到 learning-service
      '/api/chat': { 
        target: 'http://learning-service:8084', 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/chat/, '/api/learning/chat')
      },
      // 知识库嵌入服务代理 - 将前端的 /api/embedding/* 请求转发到 learning-service
      '/api/embedding': { 
        target: 'http://learning-service:8084', 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/embedding/, '/api/learning/ai/embedding')
      },
      // RAG服务代理 - 将前端的 /api/rag/* 请求转发到 learning-service
      '/api/rag': { 
        target: 'http://learning-service:8084', 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/rag/, '/api/learning/ai/rag')
      },
      // 存储服务代理 - 将前端的 /api/storage/* 请求转发到 learning-service
      '/api/storage': { 
        target: 'http://learning-service:8084', 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/storage/, '/api/learning/ai/storage')
      },
      // 视频服务代理 - 将前端的 /api/video/* 请求转发到 learning-service
      '/api/video': { 
        target: 'http://learning-service:8084', 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/video/, '/api/learning/ai/video')
      },
      // AI健康检查代理 - 将前端的 /api/ai/health 请求转发到 learning-service
      '/api/ai/health': { 
        target: 'http://learning-service:8084', 
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api\/ai\/health/, '/api/learning/ai/health')
      },
      '/api/admin': { target: 'http://learning-service:8084', changeOrigin: true },
      '/api/discussion':{target: 'http://content-service:8083', changeOrigin: true },
      '/api/discussion-like':{target: 'http://content-service:8083', changeOrigin: true },
      '/api/discussion-reply':{target: 'http://content-service:8083', changeOrigin: true },
      '/api/homework':{target: 'http://content-service:8083', changeOrigin: true },
    }
  },
  optimizeDeps: {
    include: ['vue3-quill','quill']
  },
  css: {
    preprocessorOptions: {
      sass: {
        // 移除了 require('sass')
        additionalData: ''
      }
    }
  }
})
