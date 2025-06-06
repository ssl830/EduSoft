<script setup lang="ts">
import { defineProps } from 'vue'

interface Class {
    id: bigint;
    course_id: bigint;
    course_name: string;
    teacherID: string;
    class_name: string;
    class_code: string;
}

const props = defineProps<{
    aclass: Class;
}>()
</script>

<template>
    <router-link :to="`/class/${aclass.id}`" class="course-card">
        <div class="course-header">
            <h3 class="course-title">{{ aclass.course_name }}</h3>
            <h3 class="course-title2">{{ aclass.class_name }}</h3>
        </div>

        <div class="course-content">
            <p class="course-teacher">教师: {{ aclass.teacherID }}</p>
        </div>

        <div class="card-footer">
            <span class="view-details">查看详情</span>
        </div>
    </router-link>
</template>

<style scoped>
.course-card {
    display: block;
    background-color: #ffffff;
    border-radius: 8px;
    overflow: hidden;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
    text-decoration: none;
    color: inherit;
}

.course-card:hover {
    transform: translateY(-4px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.course-header {
    padding: 1.25rem 1.25rem 0.75rem;
    border-bottom: 1px solid #f0f0f0;
}

.course-title {
    margin: 0 0 0.5rem;
    font-size: 1.25rem;
    color: #333;
}

.course-title2 {
    margin: 0 0 0.5rem;
    font-size: 1rem;
    color: #333;
}

.course-code {
    font-size: 0.875rem;
    color: #757575;
}

.course-content {
    padding: 1.25rem;
    min-height: 100px;
}

.course-teacher {
    margin: 0 0 1rem;
    font-size: 0.9375rem;
}

.course-progress {
    margin-top: 1rem;
}

.progress-label {
    display: flex;
    justify-content: space-between;
    margin-bottom: 0.5rem;
    font-size: 0.875rem;
    color: #616161;
}

.progress-bar {
    height: 6px;
    background-color: #e0e0e0;
    border-radius: 3px;
    overflow: hidden;
}

.progress-value {
    height: 100%;
    background-color: #2c6ecf;
    border-radius: 3px;
    transition: width 0.3s ease;
}

.card-footer {
    padding: 0.75rem 1.25rem;
    background-color: #f9f9f9;
    border-top: 1px solid #f0f0f0;
    text-align: right;
}

.view-details {
    font-size: 0.875rem;
    color: #2c6ecf;
    font-weight: 500;
}
</style>
