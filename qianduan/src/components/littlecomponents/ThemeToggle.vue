<template>
  <var-button 
    type="primary" 
    @click="toggleTheme"
    class="theme-toggle-btn"
  >
    <i :class="themeIconClass"></i>
    <span class="theme-label">{{ themeText }}</span>
  </var-button>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { StyleProvider, Themes } from '@varlet/ui'
import { themeEventBus } from '../../stores/themeStore'

// 定义可接受的主题名称类型
type ThemeName = 'light' | 'dark' | 'md3Light' | 'md3Dark';

// 可用主题列表
const themes = [
  { name: 'light' as ThemeName, label: 'Twilight', iconClass: 'fa fa-lightbulb-o fa-lg' },
  { name: 'dark' as ThemeName, label: 'MaterialDark', iconClass: 'fa fa-adjust fa-lg' },
  { name: 'md3Light' as ThemeName, label: 'Sunrise', iconClass: 'fa fa-sun-o fa-lg' },
  { name: 'md3Dark' as ThemeName, label: 'BeforeDawn', iconClass: 'fa fa-moon-o fa-lg' }
]

// 当前主题状态
const currentTheme = ref<ThemeName>((localStorage.getItem('theme') as ThemeName) || 'md3Light')

// 应用主题的函数
const applyTheme = (theme: ThemeName) => {
  // 移除所有主题类
  document.documentElement.classList.remove('theme-dark', 'theme-md3-light', 'theme-md3-dark')
  
  switch(theme) {
    case 'light':
      StyleProvider(null)
      break
    case 'dark':
      StyleProvider(Themes.dark)
      document.documentElement.classList.add('theme-dark')
      break
    case 'md3Light':
      StyleProvider(Themes.md3Light)
      document.documentElement.classList.add('theme-md3-light')
      break
    case 'md3Dark':
      StyleProvider(Themes.md3Dark)
      document.documentElement.classList.add('theme-md3-dark')
      break
  }
  
  // 同时更新vanta背景主题
  themeEventBus.setMaterialTheme(theme)
}

// 组件挂载时应用保存的主题
onMounted(() => {
  applyTheme(currentTheme.value)
})

// 计算属性和方法
const themeIconClass = computed(() => 
  themes.find(t => t.name === currentTheme.value)?.iconClass || 'fa fa-adjust fa-lg'
)

const themeText = computed(() => 
  themes.find(t => t.name === currentTheme.value)?.label || '切换主题'
)

const toggleTheme = () => {
  console.log('当前主题:', currentTheme.value)
  const nextIndex = themes.findIndex(t => t.name === currentTheme.value) + 1
  const nextTheme = themes[nextIndex % themes.length].name

  console.log('切换到主题:', nextTheme)
  currentTheme.value = nextTheme
  localStorage.setItem('theme', nextTheme)
  
  applyTheme(nextTheme)
}
</script>
<script lang="ts">
export default {
  name: 'ThemeToggle' // 显式设置组件名
}
</script>