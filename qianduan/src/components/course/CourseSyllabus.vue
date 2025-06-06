<script setup lang="ts">
import {computed, defineProps, onMounted, ref} from 'vue'
import { useAuthStore } from '../../stores/auth'
import CourseApi from "../../api/course.ts";
import {useRoute} from "vue-router";

const props = defineProps<{
  course: any;
}>()

const authStore = useAuthStore()
const isTeacher = authStore.userRole === 'teacher'
const error = ref('')
const uploadError = ref('')
const showSectionForm = ref(false)
const uploadForm = ref({
    sections: [
        { sectionId: -1, title: '' }
    ]
})

// Remove an option
const removeSection = (index: number) => {
    if (uploadForm.value.sections.length > 1) {
        uploadForm.value.sections.splice(index, 1)
    }
}

// Add an option for choice questions
const addSection = () => {
    uploadForm.value.sections.push({ sectionId: -1, title: '' })
}

const uploadSections = async () => {
    const hasEmptyField = uploadForm.value.sections.some(section =>
        section.title.trim() === '' ||
        String(section.sectionId).trim() === '' ||
        isNaN(Number(section.sectionId)) ||  // 新增数字校验
        Number(section.sectionId) < 0       // 确保转换为数字后比较
    )

    if (hasEmptyField) {
        uploadError.value = '请补全所有章节的序号和名称，并保证章节序号>=0'
        return
    }

    try {
        const response = await CourseApi.uploadSections(props.course.id, uploadForm.value.sections)
        console.log(response)
        showSectionForm.value = !showSectionForm.value
        // 删除section
    } catch (err) {
        error.value = '上传章节失败，请稍后再试'
        console.error(err)
    }
    fetchCourse()
}
const route = useRoute()
const courseId = computed(() => route.params.id as string)
const fetchCourse = async() => {
    try {
        const response = await CourseApi.getCourseById(courseId.value)
        props.course.value = response.data.data
    } catch (err) {
        error.value = '获取课程详情失败，请稍后再试'
        console.error(err)
    }
}

// Download resource
const deleteSection = async (sectionId: bigint) => {
    try {
        const response = await CourseApi.deleteSection(props.course.id, sectionId)
        props.course.sections = props.course.sections.filter(s => s.id !== sectionId);
        console.log(response)
        // 删除section
    } catch (err) {
        error.value = '下载资源失败，请稍后再试'
        console.error(err)
    }
}

</script>

<template>
  <div class="course-syllabus">
    <section class="syllabus-section">
      <div class="section-header">
        <h2>教学目标</h2>
        <button v-if="isTeacher" class="btn-edit">编辑</button>
      </div>
      <div class="section-content">
        <div v-if="course.objective" v-html="course.objective"></div>
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
        <div v-if="course.outline" v-html="course.outline"></div>
        <div v-else class="empty-state">尚未设置课程大纲</div>
      </div>
    </section>
    <section class="syllabus-section">
      <div class="section-header">
          <h2>课程章节</h2>
          <button v-if="isTeacher" class="btn-edit" @click="showSectionForm = !showSectionForm">{{ showSectionForm ? '取消新增' : '新增章节' }}</button>
      </div>

      <div class="resource-list-container">
          <div v-if="showSectionForm" class="upload-form">
              <h3>新增章节</h3>

              <div v-if="uploadError" class="error-message">{{ uploadError }}</div>

              <div class="form-group">
                  <label>选项</label>
                  <div
                      v-for="(section, index) in uploadForm.sections"
                      :key="index"
                      class="option-row"
                  >
                      <input
                          v-model="section.sectionId"
                          type="text"
                          :placeholder="`章节号`"
                          class="option-input"
                          style="max-width: 60px; margin-right: 20px"
                      />
                      <input
                          v-model="section.title"
                          type="text"
                          :placeholder="`章节${section.sectionId}标题`"
                          class="option-input"
                      />
                      <button
                          type="button"
                          class="btn-icon"
                          @click="removeSection(index)"
                          :disabled="uploadForm.sections.length <= 1"
                      >
                          ✕
                      </button>
                  </div>

                  <button
                      type="button"
                      class="btn-text"
                      @click="addSection"
                  >
                      + 添加章节
                  </button>
              </div>
              <div class="form-actions">
                  <button
                      type="button"
                      class="btn-primary"
                      @click="uploadSections"
                  >
                      上传
                  </button>
              </div>
          </div>
          <div v-if="course.sections" class="resource-table-wrapper">
              <table class="resource-table">
                  <thead>
                  <tr>
                      <th>章节序号</th>
                      <th>章节名称</th>
                      <th>操作</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr v-for="section in course.sections" :key="section.sort_order">
                      <td>{{ section.sort_order }}</td>
                      <td>{{ section.title}}</td>
                      <td class="actions">
                          <button
                              class="btn-action preview"
                              @click="deleteSection(section.id)"
                              title="编辑"
                          >
                              删除
                          </button>
                      </td>
                  </tr>
                  </tbody>
              </table>
          </div>
          <div v-else-if="error" class="error-message">{{ error }}</div>
          <div v-else class="empty-state">尚未设置课程大纲</div>
      </div>
    </section>
  </div>
</template>

<style scoped>
.upload-form {
    background-color: #f9f9f9;
    border-radius: 8px;
    padding: 1.5rem;
    margin-bottom: 1.5rem;
    border: 1px solid #e0e0e0;
}

.upload-form h3 {
    margin-top: 0;
    margin-bottom: 1rem;
}

.form-group {
    margin-bottom: 1rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
}

.form-row {
    display: flex;
    gap: 1rem;
}

.form-group-half {
    flex: 1;
}

.form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 1rem;
}

.btn-primary, .btn-secondary {
    padding: 0.5rem 1rem;
    border-radius: 4px;
    font-weight: 500;
    cursor: pointer;
    border: none;
    transition: background-color 0.2s;
}

.btn-primary {
    background-color: #2c6ecf;
    color: white;
}

.btn-primary:hover {
    background-color: #215bb4;
}

.error-message {
    background-color: #ffebee;
    color: #c62828;
    padding: 1rem;
    border-radius: 4px;
    margin-bottom: 1rem;
}

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

.resource-table-wrapper {
    overflow-x: auto;
}

.resource-table {
    width: 100%;
    border-collapse: collapse;
}

.resource-table th,
.resource-table td {
    padding: 1rem;
    text-align: left;
    border-bottom: 1px solid #e0e0e0;
}

.resource-table th {
    background-color: #f5f5f5;
    font-weight: 600;
}

.resource-table tr:hover {
    background-color: #f9f9f9;
}

.actions {
    display: flex;
    gap: 0.5rem;
}

.btn-action {
    padding: 0.375rem 0.75rem;
    border-radius: 4px;
    border: none;
    font-size: 0.875rem;
    cursor: pointer;
    transition: background-color 0.2s;
}

.btn-action.preview {
    background-color: #e3f2fd;
    color: #1976d2;
}

.btn-action.preview:hover {
    background-color: #bbdefb;
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

.form-group {
    margin-bottom: 1.25rem;
}

.form-group label {
    display: block;
    margin-bottom: 0.5rem;
    font-weight: 500;
}

.form-group input,
.form-group select,
.form-group textarea {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
    transition: border-color 0.3s;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
    border-color: #2c6ecf;
    outline: none;
}

.option-row {
    display: flex;
    align-items: center;
    margin-bottom: 0.5rem;
}

.option-input {
    flex: 1;
}

.btn-icon {
    background: none;
    border: none;
    cursor: pointer;
    color: #757575;
    font-size: 1rem;
    padding: 0.5rem;
}

.btn-icon:hover {
    color: #d32f2f;
}

.btn-icon:disabled {
    color: #bdbdbd;
    cursor: not-allowed;
}

.btn-text {
    background: none;
    border: none;
    color: #2c6ecf;
    padding: 0;
    font-weight: 500;
    cursor: pointer;
    display: inline-flex;
    align-items: center;
    margin-top: 0.5rem;
}

.btn-text:hover {
    text-decoration: underline;
}
</style>
