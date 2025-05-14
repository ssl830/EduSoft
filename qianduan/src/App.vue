<script setup lang="ts">
import { useRoute } from 'vue-router'
import AppHeader from './components/layout/AppHeader.vue'
import AppSidebar from './components/layout/AppSidebar.vue'

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
}

.main-content {
  display: flex;
  flex: 1;
}

.page-content {
  flex: 1;
  padding: 1rem;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }
}
</style>