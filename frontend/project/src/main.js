"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
var vue_1 = require("vue");
var quasar_1 = require("quasar");
var pinia_1 = require("pinia");
var router_1 = require("./router");
require("./styles/index.scss");
var App_vue_1 = require("./App.vue");
var element_plus_1 = require("element-plus");
require("element-plus/dist/index.css");
var ProgressKnob_vue_1 = require("./components/littlecomponents/ProgressKnob.vue");
var WriteBoard_vue_1 = require("./components/littlecomponents/WriteBoard.vue");
// 导入修改后的 Quasar CSS 以解决 math.div 问题
// import 'quasar/src/css/index.sass' // 注释掉原来的导入
// 导入图标集
require("@quasar/extras/material-icons/material-icons.css");
require("font-awesome/css/font-awesome.min.css");
var ui_1 = require("@varlet/ui");
require("@varlet/ui/es/style");
var ui_2 = require("@varlet/ui");
// 从本地存储读取用户偏好或使用默认主题
var themeMap = {
    light: null, // 对应 Material Design 2 亮色
    dark: ui_2.Themes.dark, // 对应 Material Design 2 暗色
    md3Light: ui_2.Themes.md3Light,
    md3Dark: ui_2.Themes.md3Dark
};
// 初始化主题
var savedTheme = localStorage.getItem('theme') || 'md3Light';
(0, ui_2.StyleProvider)(themeMap[savedTheme]);
// 应用主题类到 HTML 元素
var applyThemeClass = function (theme) {
    document.documentElement.classList.remove('theme-dark', 'theme-md3-light', 'theme-md3-dark');
    switch (theme) {
        case 'dark':
            document.documentElement.classList.add('theme-dark');
            break;
        case 'md3Light':
            document.documentElement.classList.add('theme-md3-light');
            break;
        case 'md3Dark':
            document.documentElement.classList.add('theme-md3-dark');
            break;
    }
};
applyThemeClass(savedTheme);
var app = (0, vue_1.createApp)(App_vue_1.default);
var pinia = (0, pinia_1.createPinia)();
// 使用各种插件
app.use(pinia)
    .use(router_1.default)
    .use(element_plus_1.default)
    .use(quasar_1.Quasar)
    .use(ui_1.default)
    .component('ProgressKnob', ProgressKnob_vue_1.default)
    .component('WriteBoard', WriteBoard_vue_1.default);
// 注册全局实例
//全局注册组件
// app.component('ProgressKnob', ProgressKnob)  // 删除重复注册
// app.component('WriteBoard', WriteBoard)      // 删除重复注册
// app.use(createPinia())  // 删除重复创建
app.use(ui_1.default);
app.use(element_plus_1.default);
app.use(quasar_1.Quasar, {
    plugins: {}, // 可以在此导入 Quasar 插件
});
// 挂载应用
app.mount('#app');
