<script setup lang="ts">
import { defineProps } from 'vue'
import { useAuthStore } from '../../stores/auth'

const props = defineProps<{
  course: any;
}>()

const authStore = useAuthStore()
const isTeacher = authStore.userRole === 'teacher'
</script>

<template>
  <div class="course-syllabus">
    <section class="syllabus-section">
      <div class="section-header">
        <h2>教学目标</h2>
        <button v-if="isTeacher" class="btn-edit">编辑</button>
      </div>
      <div class="section-content">
        <div v-if="course.objectives" v-html="course.objectives"></div>
        <div v-else class="empty-state">尚未设置教学目标</div>
      </div>
    </section>

    <section class="syllabus-section">
      <div class="section-header">
        <h2>考核方式</h2>
        <button v-if="isTeacher" class="btn-edit">编辑</button>
      </div>
      <div class="section-content">
        <div v-if="course.assessment" v-html="course.assessment"></div>
        <div v-else class="empty-state">尚未设置考核方式</div>
      </div>
    </section>

    <section class="syllabus-section">
      <div class="section-header">
        <h2>课程大纲</h2>
        <button v-if="isTeacher" class="btn-edit">编辑</button>
      </div>
      <div class="section-content">
        <div v-if="course.syllabus" v-html="course.syllabus"></div>
        <div v-else class="empty-state">尚未设置课程大纲</div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.course-syllabus {
  padding: 1.5rem;
}

.syllabus-section {
  margin-bottom: 2rem;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
  padding-bottom: 0.5rem;
  border-bottom: 2px solid #e3f2fd;
}

.section-header h2 {
  margin: 0;
  font-size: 1.25rem;
  color: #1976d2;
}

.btn-edit {
  background: none;
  border: 1px solid #2c6ecf;
  color: #2c6ecf;
  padding: 0.375rem 0.75rem;
  border-radius: 4px;
  font-size: 0.875rem;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-edit:hover {
  background-color: #e3f2fd;
}

.section-content {
  line-height: 1.6;
}

.empty-state {
  padding: 1.5rem;
  background-color: #f5f5f5;
  border-radius: 4px;
  color: #757575;
  text-align: center;
}
</style>
