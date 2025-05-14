<template>
  <div class="min-h-screen flex items-center justify-center bg-gray-100">
    <div class="text-center">
      <h1 class="text-6xl font-bold text-gray-800 mb-4">404</h1>
      <p class="text-xl text-gray-600 mb-8">页面未找到</p>
      <router-link 
        to="/" 
        class="px-6 py-3 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
      >
        返回首页
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
// No additional logic needed
</script>