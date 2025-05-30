<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { defineProps } from 'vue'
import ResourceApi from '../../api/resource'

const props = defineProps<{
  courseId: string;
  isTeacher: boolean;
}>()

const resources = ref([])
const loading = ref(true)
const error = ref('')

// Filters
const selectedChapter = ref('')
const selectedType = ref('')
const searchQuery = ref('')

// Chapters and types (will be populated from resources)
const chapters = ref([])
const resourceTypes = ref([])

// File upload
const showUploadForm = ref(false)
const uploadForm = ref({
  title: '',
  chapter: '',
  type: '',
  file: null as File | null,
  description: ''
})
const uploadProgress = ref(0)
const uploadError = ref('')

// Fetch resources
const fetchResources = async () => {
  loading.value = true
  error.value = ''
  
  try {
    const response = await ResourceApi.getCourseResources(props.courseId, {
      chapter: selectedChapter.value,
      type: selectedType.value,
      search: searchQuery.value
    })
    
    resources.value = response.data
    
    // Extract unique chapters and types
    const chaptersSet = new Set(resources.value.map((r: any) => r.chapter).filter(Boolean))
    chapters.value = Array.from(chaptersSet)
    
    const typesSet = new Set(resources.value.map((r: any) => r.type).filter(Boolean))
    resourceTypes.value = Array.from(typesSet)
    
  } catch (err) {
    error.value = '获取资源列表失败，请稍后再试'
    console.error(err)
  } finally {
    loading.value = false
  }
}

// Handle file selection
const handleFileChange = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files.length > 0) {
    uploadForm.value.file = input.files[0]
  }
}

// Upload resource
const uploadResource = async () => {
  if (!uploadForm.value.title || !uploadForm.value.file) {
    uploadError.value = '请填写资源标题并选择文件'
    return
  }
  
  uploadProgress.value = 0
  uploadError.value = ''
  
  const formData = new FormData()
  formData.append('title', uploadForm.value.title)
  formData.append('file', uploadForm.value.file)
  
  if (uploadForm.value.chapter) {
    formData.append('chapter', uploadForm.value.chapter)
  }
  
  if (uploadForm.value.type) {
    formData.append('type', uploadForm.value.type)
  }
  
  if (uploadForm.value.description) {
    formData.append('description', uploadForm.value.description)
  }
  
  try {
    await ResourceApi.uploadResource(props.courseId, formData, (progress) => {
      uploadProgress.value = progress
    })
    
    // Reset form and refresh list
    resetUploadForm()
    showUploadForm.value = false
    fetchResources()
    
  } catch (err) {
    uploadError.value = '上传资源失败，请稍后再试'
    console.error(err)
  }
}

// Reset upload form
const resetUploadForm = () => {
  uploadForm.value = {
    title: '',
    chapter: '',
    type: '',
    file: null,
    description: ''
  }
  uploadProgress.value = 0
  uploadError.value = ''
}

// Preview resource
const previewResource = (resource: any) => {
  window.open(resource.url, '_blank')
}

// Download resource
const downloadResource = async (resource: any) => {
  try {
    const response = await ResourceApi.downloadResource(resource.id)
    
    // Create a download link
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', resource.title)
    document.body.appendChild(link)
    link.click()
    link.remove()
    
  } catch (err) {
    error.value = '下载资源失败，请稍后再试'
    console.error(err)
  }
}

// Filter resources when criteria change
watch([selectedChapter, selectedType, searchQuery], () => {
  fetchResources()
})

onMounted(() => {
  fetchResources()
})
</script>

<template>
  <div class="resource-list-container">
    <div class="resource-header">
      <h2>教学资料</h2>
      <button 
        v-if="isTeacher" 
        class="btn-primary"
        @click="showUploadForm = !showUploadForm"
      >
        {{ showUploadForm ? '取消上传' : '上传资料' }}
      </button>
    </div>
    
    <!-- Upload Form -->
    <div v-if="showUploadForm" class="upload-form">
      <h3>上传教学资料</h3>
      
      <div v-if="uploadError" class="error-message">{{ uploadError }}</div>
      
      <div class="form-group">
        <label for="title">资料标题</label>
        <input 
          id="title"
          v-model="uploadForm.title"
          type="text"
          placeholder="输入资料标题"
          required
        />
      </div>
      
      <div class="form-row">
        <div class="form-group form-group-half">
          <label for="chapter">所属章节</label>
          <input 
            id="chapter"
            v-model="uploadForm.chapter"
            type="text"
            placeholder="例如: 第一章、第二节"
          />
        </div>
        
        <div class="form-group form-group-half">
          <label for="type">资料类型</label>
          <input 
            id="type"
            v-model="uploadForm.type"
            type="text"
            placeholder="例如: 课件、视频、代码"
          />
        </div>
      </div>
      
      <div class="form-group">
        <label for="file">选择文件</label>
        <input 
          id="file"
          type="file"
          @change="handleFileChange"
          required
        />
      </div>
      
      <div class="form-group">
        <label for="description">资料说明 (可选)</label>
        <textarea 
          id="description"
          v-model="uploadForm.description"
          rows="3"
          placeholder="资料的说明或备注"
        ></textarea>
      </div>
      
      <div v-if="uploadProgress > 0" class="upload-progress">
        <div class="progress-bar">
          <div 
            class="progress-value" 
            :style="{ width: `${uploadProgress}%` }"
          ></div>
        </div>
        <div class="progress-text">{{ uploadProgress }}%</div>
      </div>
      
      <div class="form-actions">
        <button 
          type="button"
          class="btn-secondary"
          @click="resetUploadForm"
        >
          重置
        </button>
        <button 
          type="button"
          class="btn-primary"
          @click="uploadResource"
        >
          上传
        </button>
      </div>
    </div>
    
    <!-- Filter Section -->
    <div class="resource-filters">
      <div class="filter-section">
        <label for="chapterFilter">按章节筛选:</label>
        <select 
          id="chapterFilter"
          v-model="selectedChapter"
        >
          <option value="">所有章节</option>
          <option v-for="chapter in chapters" :key="chapter" :value="chapter">
            {{ chapter }}
          </option>
        </select>
      </div>
      
      <div class="filter-section">
        <label for="typeFilter">按类型筛选:</label>
        <select 
          id="typeFilter"
          v-model="selectedType"
        >
          <option value="">所有类型</option>
          <option v-for="type in resourceTypes" :key="type" :value="type">
            {{ type }}
          </option>
        </select>
      </div>
      
      <div class="filter-section search">
        <input 
          v-model="searchQuery"
          type="text"
          placeholder="搜索资料..."
          class="search-input"
        />
      </div>
    </div>
    
    <!-- Resource List -->
    <div v-if="loading" class="loading-container">加载中...</div>
    <div v-else-if="error" class="error-message">{{ error }}</div>
    <div v-else-if="resources.length === 0" class="empty-state">
      暂无教学资料
    </div>
    <div v-else class="resource-table-wrapper">
      <table class="resource-table">
        <thead>
          <tr>
            <th>资料名称</th>
            <th>所属章节</th>
            <th>类型</th>
            <th>上传时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="resource in resources" :key="resource.id">
            <td>{{ resource.title }}</td>
            <td>{{ resource.chapter || '-' }}</td>
            <td>{{ resource.type || '-' }}</td>
            <td>{{ new Date(resource.uploadTime).toLocaleString() }}</td>
            <td class="actions">
              <button 
                class="btn-action preview"
                @click="previewResource(resource)"
                title="预览"
              >
                预览
              </button>
              <button 
                class="btn-action download"
                @click="downloadResource(resource)"
                title="下载"
              >
                下载
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
.resource-list-container {
  padding: 1.5rem;
}

.resource-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
}

.resource-header h2 {
  margin: 0;
}

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
  margin-top: 1.5rem;
}

.upload-progress {
  margin-top: 1rem;
}

.progress-bar {
  height: 8px;
  background-color: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 0.5rem;
}

.progress-value {
  height: 100%;
  background-color: #2c6ecf;
  border-radius: 4px;
}

.progress-text {
  text-align: right;
  font-size: 0.875rem;
  color: #616161;
}

.resource-filters {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 1.5rem;
  gap: 1rem;
  align-items: center;
}

.filter-section {
  display: flex;
  align-items: center;
}

.filter-section label {
  margin-right: 0.5rem;
  white-space: nowrap;
}

.filter-section select {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9375rem;
}

.filter-section.search {
  flex-grow: 1;
}

.search-input {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 0.9375rem;
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

.btn-action.download {
  background-color: #e8f5e9;
  color: #2e7d32;
}

.btn-action.download:hover {
  background-color: #c8e6c9;
}

.empty-state {
  padding: 2rem;
  text-align: center;
  background-color: #f5f5f5;
  border-radius: 8px;
  color: #757575;
}

.loading-container {
  display: flex;
  justify-content: center;
  padding: 2rem;
  color: #616161;
}

.error-message {
  background-color: #ffebee;
  color: #c62828;
  padding: 1rem;
  border-radius: 4px;
  margin-bottom: 1rem;
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

.btn-secondary {
  background-color: #f5f5f5;
  color: #424242;
  border: 1px solid #ddd;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}

@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 0;
  }
  
  .resource-filters {
    flex-direction: column;
    align-items: stretch;
  }
  
  .filter-section {
    width: 100%;
  }
  
  .filter-section select,
  .search-input {
    flex-grow: 1;
  }
}
</style>