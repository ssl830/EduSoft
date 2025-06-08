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
