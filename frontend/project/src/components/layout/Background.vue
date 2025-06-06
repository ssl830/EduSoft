<template>
  <div ref="vantaRef" class="vanta-canvas-container"></div>
</template>

<script setup lang="ts">
import { ref, onMounted, onBeforeUnmount } from 'vue'
import * as THREE from "three"
import { themeEventBus, vantaThemes } from '../../stores/themeStore'

// 定义 ref
const vantaRef = ref<HTMLElement | null>(null)
let vantaEffect: any = null
let themeUnsubscribe: (() => void) | null = null

// 动态导入 Vanta 效果
const initVanta = async () => {
  try {
    console.log('Starting vanta initialization with theme:', themeEventBus.getCurrentTheme())
    // 动态导入 Vanta 效果
    const { default: VANTA_NET_EFFECT } = await import("vanta/dist/vanta.net.min.js")
    
    if (vantaRef.value && typeof VANTA_NET_EFFECT === 'function' && THREE) {
      const currentTheme = themeEventBus.getCurrentTheme()
      const config = vantaThemes[currentTheme]
      console.log('Initializing Vanta with config:', config)
      
      vantaEffect = VANTA_NET_EFFECT({
        el: vantaRef.value,
        THREE: THREE,
        mouseControls: true,
        touchControls: true,
        gyroControls: false,
        minHeight: 200.0,
        minWidth: 200.0,
        scale: 1.0,
        scaleMobile: 1.0,
        color: config.colors[0],
        backgroundColor: config.backgroundColor,
        points: config.points,
        maxDistance: config.maxDistance,
        spacing: config.spacing
      })
      console.log('Vanta effect initialized successfully:', vantaEffect)
    } else {
      if (!vantaRef.value) {
        console.error("[Vanta Background] Error: Target element (vantaRef) not found.")
      }
      if (!THREE) {
        console.error("[Vanta Background] Error: THREE.js (THREE) is not loaded or undefined.")
      }
      if (typeof VANTA_NET_EFFECT !== 'function') {
        console.error("[Vanta Background] Error: VANTA_NET_EFFECT is not a function.")
      }
    }
  } catch (error) {
    console.warn('[Vanta Background] Failed to load Vanta effect:', error)
  }
}

// 更新主题配置
const updateTheme = (themeName: string) => {
  if (vantaEffect && vantaThemes[themeName]) {
    const config = vantaThemes[themeName]
    console.log('Updating vanta theme to:', themeName, config)
    
    // 更新vanta效果的配置
    vantaEffect.setOptions({
      color: config.colors[0],
      backgroundColor: config.backgroundColor,
      points: config.points,
      maxDistance: config.maxDistance,
      spacing: config.spacing
    })
  }
}

// 清理 Vanta 效果
const destroyVanta = () => {
  if (vantaEffect) {
    try {
      vantaEffect.destroy()
    } catch (error) {
      console.warn('[Vanta Background] Error destroying Vanta effect:', error)
    } finally {
      vantaEffect = null
    }
  }
}

onMounted(() => {
  // 延迟初始化，确保 DOM 完全加载
  setTimeout(initVanta, 100)
  
  // 监听主题变化
  themeUnsubscribe = themeEventBus.onThemeChange(updateTheme)
})

onBeforeUnmount(() => {
  destroyVanta()
  
  // 取消主题监听
  if (themeUnsubscribe) {
    themeUnsubscribe()
  }
})
</script>
<script lang="ts">
export default {
  name: 'Background' 
}
</script>
<style scoped>
.vanta-canvas-container {
  position: fixed !important;
  top: 0 !important;
  left: 0 !important;
  width: 100vw !important;
  height: 100vh !important;
  z-index: -1 !important;
  pointer-events: none;
}
</style>