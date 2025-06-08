"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var vite_1 = require("vite");
var plugin_vue_1 = require("@vitejs/plugin-vue");
var vite_plugin_1 = require("@quasar/vite-plugin");
var vite_2 = require("unplugin-auto-import/vite");
var vite_3 = require("unplugin-vue-components/vite");
var resolvers_1 = require("unplugin-vue-components/resolvers");
var path_1 = require("path");
// https://vitejs.dev/config/
exports.default = (0, vite_1.defineConfig)({
    base: './', // 确保资源路径正确
    plugins: [
        (0, plugin_vue_1.default)({
            template: { transformAssetUrls: vite_plugin_1.transformAssetUrls }
        }),
        // quasar({
        //   sassVariables: 'src/quasar-variables.sass'
        // }),
        (0, vite_2.default)({
            resolvers: [(0, resolvers_1.ElementPlusResolver)()],
        }),
        (0, vite_3.default)({
            resolvers: [(0, resolvers_1.ElementPlusResolver)()],
        }),
    ],
    resolve: {
        alias: {
            '@': (0, path_1.resolve)(__dirname, 'src'),
            '@popperjs/core/lib/modifiers/offset.js': (0, path_1.resolve)('./node_modules/@popperjs/core/dist/esm/modifiers/offset.js'),
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
        include: ['vue3-quill', 'quill']
    },
    css: {
        preprocessorOptions: {
            sass: {
                // 移除了 require('sass')
                additionalData: ''
            }
        }
    }
});
