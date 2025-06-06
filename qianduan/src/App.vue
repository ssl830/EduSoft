<script setup lang="ts">
import { useRoute } from 'vue-router'
import AppHeader from './components/layout/AppHeader.vue'
import AppSidebar from './components/layout/AppSidebar.vue'
import { computed } from 'vue'

const route = useRoute()
</script>

<template>
  <div class="app-container">
    <AppHeader />
    <div class="main-content">
      <AppSidebar v-if="route.meta.showSidebar" />
      <main class="page-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: transparent; /* 改为透明，让vanta背景显示 */
  color: var(--text-primary);
  transition: background-color var(--transition-normal), color var(--transition-normal);
}

.main-content {
  display: flex;
  flex: 1;
  position: relative;
  background: transparent; /* 改为透明 */
  transition: background-color var(--transition-normal);
}

.page-content {
  width: 100%;
  position: relative;
  background: transparent; /* 改为透明 */
  transition: background-color var(--transition-normal);
}
</style>

<style>
/* Reset some default browser styles to ensure full-screen background works properly */
body, html {
  margin: 0;
  padding: 0;
  height: 100%;
  width: 100%;
  overflow-x: hidden; /* Prevent horizontal scrolling */
}

#app {
  height: 100%;
  width: 100%;
}
</style>