<template>
  <div class="progress-knob">
    <div class="progress-bar">
      <div 
        class="progress-inner" 
        :style="{width: `${value}%`, backgroundColor: barColor}"
      ></div>
      <span class="progress-text">{{value}}%</span>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'

defineOptions({
  name: 'ProgressKnob'
})

const props = defineProps<{
  value: number
}>()

const barColor = computed(() => {
  if (props.value < 60) return '#f56c6c'  // 红色
  if (props.value < 80) return '#FFDB58'  // 黄色
  return '#67c23a'                       // 绿色
})
defineExpose({
  props,
  barColor
})
</script>

<style scoped>
.progress-knob {
  width: 120px;
  margin: 0 auto;
  padding: 8px 0;
}
.progress-bar {
  position: relative;
  height: 20px;
  background-color: #e9e9e9;
  border-radius: 10px;
  overflow: hidden;
}
.progress-inner {
  height: 100%;
  border-radius: 10px;
  transition: width 0.3s ease, background-color 0.3s ease;
}
.progress-text {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #333;
  font-size: 12px;
  font-weight: bold;
}
</style>

