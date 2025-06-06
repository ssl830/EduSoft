<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { defineProps } from 'vue'
import ResourceApi from '../../api/resource'
import {useAuthStore} from "../../stores/auth.ts";

const props = defineProps<{
  courseId: string;
  isTeacher: boolean;
}>()

const resources = ref<any[]>([])
const resourcesVer = ref<any[]>([])
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
const showHistoryForm = ref(false)
const isRenew = ref(false)

const uploadForm = ref({
    courseId: 0,
    sectionId: 0,
    uploaderId: 0,
    title: '',
    type: '',
    file: null as File | null,
    visibility: '',
})
const uploadProgress = ref(0)
const uploadError = ref('')

// 单选框选项配置
const typeOptions = ref([
    { value: 'PPT', label: 'PPT' },
    { value: 'PDF', label: 'PDF' },
    { value: 'VIDEO', label: 'VIDEO' },
    { value: 'CODE', label: 'CODE' },
    { value: 'OTHER', label: 'OTHER' },
])

const visibilityOptions = ref([
    { value: 'PUBLIC', label: '公开' },
    { value: 'COURSE-ONLY', label: '仅课程成员' }
])

const authStore = useAuthStore()

// Fetch resources
const fetchResources = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await ResourceApi.getCourseResources(props.courseId, {
      chapter: selectedChapter.value,
      type: selectedType.value,
      title: searchQuery.value,
      userid: authStore.user?.id,
    })
    // console.log(selectedChapter.value)
    resources.value = response.data.data

    // Extract unique chapters and types
    const chaptersSet = new Set(resources.value.map((r: any) => r.sectionId).filter(Boolean))
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

const toggleHistory = async (title: string) => {
    showHistoryForm.value = !showHistoryForm.value;
    if (showHistoryForm) {  // 仅在展开时加载数据
        await fetchResourcesVer(title);
    }
}

const fetchResourcesVer = async (title: string) => {
    try {
        const response = await ResourceApi.getCourseResources(props.courseId, {
            chapter: "",
            type:"",
            title: title
        })
        // console.log(selectedChapter.value)
        resourcesVer.value = response.data.data
    } catch (err) {
        error.value = '获取资源列表失败，请稍后再试'
        console.error(err)
    } finally {
        loading.value = false
    }
}

const renewResource = async (resource: any) => {
    isRenew.value = !isRenew.value;
    uploadForm.value.title = resource.title
    uploadForm.value.courseId = resource.courseId
    uploadForm.value.sectionId = resource.sectionId
    uploadForm.value.uploaderId = resource.uploaderId
    uploadForm.value.type = resource.type
    uploadForm.value.visibility = resource.visibility
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

  if (!uploadForm.value.title) {
    uploadError.value = '请填写资源标题'
    return
  }
  if(!uploadForm.value.file){
      uploadError.value = '请选择文件'
      return
  }

  uploadProgress.value = 0
  uploadError.value = ''

  const formData = new FormData()
  formData.append('title', uploadForm.value.title)
  formData.append('file', uploadForm.value.file)

    if (uploadForm.value.sectionId) {
        formData.append('sectionId', uploadForm.value.sectionId.toString())
    }
    if (uploadForm.value.uploaderId) {
        formData.append('uploaderId', uploadForm.value.uploaderId.toString())
    }

    if (uploadForm.value.type) {
        formData.append('type', uploadForm.value.type)
    }
    if (uploadForm.value.visibility) {
        formData.append('visibility', uploadForm.value.visibility)
    }


  try {
    await ResourceApi.uploadResource(props.courseId, formData, (progress) => {
      uploadProgress.value = progress
    })

    // Reset form and refresh list
      showUploadForm.value = false
      isRenew.value = false
      resetUploadForm()
      fetchResources()

  } catch (err) {
    uploadError.value = '上传资源失败，请稍后再试'
    console.error(err)
  }
}

// Reset upload form
const resetUploadForm = () => {
  if(isRenew.value){
      uploadForm.value.file = null
      uploadError.value = ''
      return
  }
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
  window.open(resource.fileUrl, '_blank')
}

// Download resource
const downloadResource = async (resource: any) => {
  try {
    // const response = await ResourceApi.downloadResource(resource.id)
    //
    // // Create a download link
    // const downloadUrl = response.data.url
    // console.log(response.data.url)
    // console.log(response.data)
    // const link = document.createElement('a')
    // link.href = downloadUrl
    // link.setAttribute('download', resource.title)
    // document.body.appendChild(link)
    // link.click()
    // link.remove()
      // 1. 获取文件直链
      const response = await ResourceApi.downloadResource(resource.id)
      const fileUrl = response.data.url
      const fileName = resource.title

      // 2. 通过 Fetch/Blob 间接下载
      const res = await fetch(fileUrl)
      const blob = await res.blob()

      // 3. 创建本地下载链接
      const url = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = fileName
      document.body.appendChild(link)
      link.click()

      // 4. 清理内存
      window.URL.revokeObjectURL(url)
      document.body.removeChild(link)
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
    <div v-if="showHistoryForm" class="resource-list-container">
        <div v-if="showHistoryForm" class="resource-header">
            <h2>版本管理</h2>
            <button
                class="btn-primary"
                @click="showHistoryForm = false"
            >
                {{ '取消管理' }}
            </button>
        </div>
        <!-- Resource List -->
        <div class="resource-table-wrapper">
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
                <tr v-for="resource in resourcesVer" :key="resource.id">
                    <td>{{ resource.title }}</td>
                    <td>{{ resource.sectionId || '-' }}</td>
                    <td>{{ resource.type || '-' }}</td>
                    <td>{{ resource.createdAt || '-' }}</td>
                    <!--         aTODO: 时间-->
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
          <label for="sectionId">所属章节（-1表示无章节属性）</label>
          <input
            id="sectionId"
            v-model="uploadForm.sectionId"
            type="number"
          />
        </div>

          <!-- 资料类型单选框 -->
          <div class="form-group form-group-half">
              <label>资料类型</label>
              <div class="radio-group">
                  <label v-for="option in typeOptions" :key="option.value">
                      <input
                          type="radio"
                          v-model="uploadForm.type"
                          :value="option.value"
                      />
                      <span class="radio-label">{{ option.label }}</span>
                  </label>
              </div>
          </div>

          <!-- 可见性单选框 -->
          <div class="form-group form-group-half">
              <label>可见性</label>
              <div class="radio-group">
                  <label v-for="option in visibilityOptions" :key="option.value">
                      <input
                          type="radio"
                          v-model="uploadForm.visibility"
                          :value="option.value"
                      />
                      <span class="radio-label">{{ option.label }}</span>
                  </label>
              </div>
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

    <!-- Renew Form -->
    <div v-if="isRenew" class="upload-form">
        <div class="resource-header">
            <h3>更新</h3>
            <button
                v-if="isTeacher && isRenew"
                class="btn-primary"
                @click="isRenew = !isRenew"
            >
                {{ '取消更新' }}
            </button>
        </div>

      <div v-if="uploadError" class="error-message">{{ uploadError }}</div>

      <div class="form-group">
          <label for="file">选择文件</label>
          <input
              id="file"
              type="file"
              @change="handleFileChange"
              required
          />
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
              @click="uploadResource()"
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
          <option v-for="sectionId in chapters" :key="sectionId" :value="sectionId">
            {{ sectionId }}
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
            <td>{{ resource.sectionId || '-' }}</td>
            <td>{{ resource.type || '-' }}</td>
            <td>{{ resource.createdAt || '-' }}</td>
<!--         aTODO: 时间-->
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
              <button
                v-if="isTeacher"
                class="btn-action history"
                @click="toggleHistory(resource.title)"
                title="历史版本"
              >
                  历史版本
              </button>
              <button
                v-if="isTeacher"
                class="btn-action renew"
                @click="renewResource(resource)"
                title="更新"
              >
                更新
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<style scoped>
/* 单选框样式 */
.radio-group {
    display: flex;
    gap: 1rem;
    margin-top: 0.5rem;
}

.radio-group label {
    display: flex;
    align-items: center;
    cursor: pointer;
}

.radio-label {
    margin-left: 0.25rem;
}

input[type="radio"] {
    accent-color: #409eff; /* 与Element Plus主色一致 */
}

.resource-list-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 1.5rem;
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

/* 黄色系按钮 */
.btn-action.history {
    background-color: #fff3e0;
    color: #ffa000;
}

.btn-action.history:hover {
    background-color: #ffe0b2;
}

.btn-action.renew {
    background-color: #ede7f6;
    color: #673ab7;
}

.btn-action.renew:hover {
    background-color: #d1c4e9;
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
