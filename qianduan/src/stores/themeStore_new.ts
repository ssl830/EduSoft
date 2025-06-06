import { ref } from 'vue'

// Vanta主题配置类型
export interface VantaTheme {
  name: string
  colors: number[]
  backgroundColor: number
  maxDistance: number
  spacing: number
  points: number
}

// Material Design主题到Vanta主题的映射
export const themeMapping = {
  light: 'ocean',      // Twilight -> 海洋主题
  dark: 'galaxy',      // MaterialDark -> 银河主题  
  md3Light: 'sunset',  // Sunrise -> 日落主题
  md3Dark: 'forest'    // BeforeDawn -> 森林主题
} as const

// Vanta主题配置
export const vantaThemes: Record<string, VantaTheme> = {
  ocean: {
    name: 'ocean',
    colors: [0x4fc3f7, 0x29b6f6, 0x03a9f4, 0x039be5],
    backgroundColor: 0x0d47a1,
    maxDistance: 20,
    spacing: 18,
    points: 8
  },
  forest: {
    name: 'forest',
    colors: [0x66bb6a, 0x4caf50, 0x388e3c, 0x2e7d32],
    backgroundColor: 0x1b5e20,
    maxDistance: 25,
    spacing: 15,
    points: 12
  },
  sunset: {
    name: 'sunset',
    colors: [0xffab91, 0xff8a65, 0xff7043, 0xff5722],
    backgroundColor: 0xbf360c,
    maxDistance: 22,
    spacing: 16,
    points: 10
  },
  galaxy: {
    name: 'galaxy',
    colors: [0x9c27b0, 0x673ab7, 0x3f51b5, 0x2196f3],
    backgroundColor: 0x1a237e,
    maxDistance: 30,
    spacing: 20,
    points: 15
  }
}

// 当前活动的vanta主题
const currentVantaTheme = ref<string>('sunset') // 默认日落主题

// 主题变化事件监听器
const themeChangeListeners = new Set<(theme: string) => void>()

// 主题事件总线
export const themeEventBus = {
  // 获取当前vanta主题
  getCurrentTheme: () => currentVantaTheme.value,
  
  // 设置vanta主题
  setTheme: (themeName: string) => {
    if (themeName in vantaThemes) {
      currentVantaTheme.value = themeName
      // 通知所有监听器
      themeChangeListeners.forEach(listener => listener(themeName))
    }
  },
  
  // 添加主题变化监听器
  onThemeChange: (listener: (theme: string) => void) => {
    themeChangeListeners.add(listener)
    return () => themeChangeListeners.delete(listener)
  },
  
  // 根据Material Design主题映射到Vanta主题
  setMaterialTheme: (materialTheme: keyof typeof themeMapping) => {
    const vantaTheme = themeMapping[materialTheme]
    if (vantaTheme) {
      themeEventBus.setTheme(vantaTheme)
    }
  }
}
