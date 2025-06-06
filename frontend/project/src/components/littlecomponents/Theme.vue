<script setup>
import { StyleProvider, Themes } from '@varlet/ui'
</script>

<template>
  <var-space direction="column" size="large">
    <var-button type="primary" block @click="StyleProvider(null)">Material Design 2 亮色</var-button>
    <var-button type="primary" block @click="StyleProvider(Themes.dark)">Material Design 2 暗色</var-button>
    <var-button type="primary" block @click="StyleProvider(Themes.md3Light)">Material Design 3 亮色</var-button>
    <var-button type="primary" block @click="StyleProvider(Themes.md3Dark)">Material Design 3 暗色</var-button>
  </var-space>
</template>