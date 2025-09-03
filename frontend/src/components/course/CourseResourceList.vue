<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import { defineProps, withDefaults } from 'vue'
import ResourceApi from '../../api/resource'
import { useAuthStore } from "../../stores/auth.ts";
import { useQuasar } from 'quasar'

interface Resource {
    id: number;
    title: string;
    courseId: number;
    sectionId: number;
    version: number;
    uploaderId: number;
    type: string;
    visibility: string;
    fileUrl: string;
    createdAt: string;
}

interface UploadForm {
    courseId: number;
    sectionId: number;
    uploaderId: number;
    title: string;
    type: string;
    file: File | null;
    visibility: string;
}

interface Props {
    courseId: string;
    isTeacher: boolean;
    course?: {
        id: number;
        sections?: Array<{
            id: string;
            title: string;
            sortOrder: string;
        }>;
        classes?: Array<{
            id: string;
            name: string;
            classCode: string;
            studentCount: number;
        }>;
    };
}

const props = withDefaults(defineProps<Props>(), {
    course: undefined
})

const $q = useQuasar()

const resources = ref<Resource[]>([])
const resourcesVer = ref<Resource[]>([])
const loading = ref(true)
const error = ref('')

// Filters
const selectedChapter = ref('')
const selectedType = ref('')
const searchQuery = ref('')
const selectedClass = ref('')

// Types that can be used in resources
const resourceTypes = ref<string[]>([])

// 上传文件
const showUploadForm = ref(false)
const showHistoryForm = ref(false)
const isRenew = ref(false)

const authStore = useAuthStore()
const role = authStore.user?.role

// 上传表单
const uploadForm = ref<UploadForm>({
    courseId: parseInt(props.courseId),
    sectionId: -1,
    uploaderId: authStore.user?.id || 0,
    title: '',
    type: '',
    file: null,
    visibility: ''
})
const uploadProgress = ref(0)
const uploadError = ref('')
const kbUploadProgress = ref(0)
const kbUploading = ref(false)
// 新增：知识库上传弹窗
const showKbDialog = ref(false)
const kbDialogMsg = ref('')

// 单选框选项配置
const typeOptions = ref([
    { value: 'PPT', label: 'PPT' },
    { value: 'PDF', label: 'PDF' },
    { value: 'WORD', label: 'WORD' },
    { value: 'VIDEO', label: 'VIDEO' },
    { value: 'CODE', label: 'CODE' },
    { value: 'OTHER', label: 'OTHER' },
])

const visibilityOptions = ref([
    { value: 'PUBLIC', label: '课程内公开' },
    { value: 'CLASS_ONLY', label: '仅特定班级' }
])

// 在 script setup 部分添加新的常量和工具函数
const OFFICE_PREVIEW_URL = 'https://view.officeapps.live.com/op/embed.aspx?src='

const PREVIEW_MIME_TYPES: Record<string, string> = {
    'pdf': 'application/pdf',
    'jpg': 'image/jpeg',
    'jpeg': 'image/jpeg',
    'png': 'image/png',
    'gif': 'image/gif',
    'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    'doc': 'application/msword',
    'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
    'ppt': 'application/vnd.ms-powerpoint',
    'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'xls': 'application/vnd.ms-excel'
}

const isOfficeFile = (fileName: string) => {
    const ext = fileName.split('.').pop()?.toLowerCase() || ''
    return ['doc', 'docx', 'ppt', 'pptx', 'xls', 'xlsx'].includes(ext)
}

const getMimeType = (fileName: string) => {
    const ext = fileName.split('.').pop()?.toLowerCase() || ''
    return PREVIEW_MIME_TYPES[ext] || 'application/octet-stream'
}

const getSectionTitle = (sectionId: any) => {
    if (sectionId === -1 || sectionId === null || sectionId === undefined || sectionId === '') {
        return '-'
    }
    const section = props.course?.sections?.find(sec => sec.id.toString() === sectionId.toString())
    return section ? section.title : sectionId
}

// Fetch resources
const fetchResources = async () => {
  loading.value = true
  error.value = ''

  try {
    const response = await ResourceApi.getCourseResources(parseInt(props.courseId), {
      courseId: parseInt(props.courseId),
      userId: authStore.user?.id || 0,
      chapter: selectedChapter.value ? parseInt(selectedChapter.value) : undefined,
      type: selectedType.value || undefined,
      title: searchQuery.value || undefined,
      isTeacher: props.isTeacher
    })
    // 将每个资源的 title 替换为 file_name
    resources.value = response.data.map((item: any) => ({
      ...item,
      title: item.file_name
    }))
    console.log("resources: " + resources.value)
    // Extract unique types
    const typesSet = new Set(resources.value.map(r => r.type).filter(Boolean))
    resourceTypes.value = Array.from(typesSet) as string[]

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
        const response = await ResourceApi.getCourseResources(parseInt(props.courseId), {
            courseId: parseInt(props.courseId),
            userId: authStore.user?.id || 0,
            title: title,
            isTeacher: props.isTeacher
        })
        console.log("title: " + title)
        console.log()
        resourcesVer.value = response.data
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

// 新增：上传到知识库勾选项
const uploadToKnowledgeBase = ref(false)

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
  if(uploadForm.value.visibility === 'CLASS_ONLY' && !selectedClass.value) {
      uploadError.value = '请选择班级'
      return
  }

  uploadProgress.value = 0
  uploadError.value = ''
  kbUploadProgress.value = 0
  kbUploading.value = false

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
  if (uploadForm.value.visibility === 'CLASS_ONLY' && selectedClass.value) {
      formData.append('classId', selectedClass.value)
  }

  // 添加知识库上传标志
  formData.append('uploadToKnowledgeBase', uploadToKnowledgeBase.value.toString())

  try {
    await ResourceApi.uploadResource(props.courseId, formData, (progress) => {
      uploadProgress.value = progress
    })

    // 直接使用后端集成的知识库上传功能，不再需要单独调用
    // 如果后端已经处理了上传到知识库的逻辑，前端就不需要再单独调用

    // Reset form and refresh list
    showUploadForm.value = false
    isRenew.value = false
    resetUploadForm()
    fetchResources()

    // 通知提示
    if ($q) {
      if (uploadToKnowledgeBase.value) {
        $q.notify({ type: 'positive', message: '资源已上传并同步至知识库' })
      } else {
        $q.notify({ type: 'positive', message: '资源上传成功' })
      }
    }

  } catch (err: any) {
    uploadError.value = '上传资源失败，请稍后再试'
    console.error(err)
    if ($q) {
      $q.notify({ type: 'negative', message: '上传失败: ' + (err?.message || '未知错误') })
    }
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
        courseId: parseInt(props.courseId),
        sectionId: -1,
        uploaderId: authStore.user?.id || 0,
        title: '',
        type: '',
        file: null,
        visibility: ''
    }
    uploadProgress.value = 0
    uploadError.value = ''
}

// 修改预览方法
const previewResource = async (resource: any) => {
    try {
        // 1. 获取预览链接
        const response: any = await ResourceApi.previewResource(resource.id)
        const previewUrl = response.data.url
        const fileName = response.data.fileName || resource.title

        // 2. 如果是 Office 文档，使用 Office Online Viewer
        if (isOfficeFile(fileName)) {
            const encodedUrl = encodeURIComponent(previewUrl)
            window.open(OFFICE_PREVIEW_URL + encodedUrl, '_blank')
            return
        }

        // 3. 其他文件使用 Blob 预览
        const fileResponse = await fetch(previewUrl)
        const blob = await fileResponse.blob()

        // 4. 创建带正确 MIME type 的新 Blob
        const mimeType = getMimeType(fileName)
        const file = new Blob([blob], { type: mimeType })

        // 5. 创建预览 URL 并在新窗口打开
        const blobUrl = URL.createObjectURL(file)
        window.open(blobUrl, '_blank')

        // 6. 清理 Blob URL
        setTimeout(() => {
            URL.revokeObjectURL(blobUrl)
        }, 1000)
    } catch (err) {
        error.value = '预览资源失败，请稍后再试'
        console.error(err)
    }
}

// Download resource
const downloadResource = async (resource: any) => {
    try {
        // 1. 获取文件直链
        const response: any = await ResourceApi.downloadResource(resource.id)
        const fileUrl = response.data.url
        const fileName = response.data.fileName || resource.title

        // 2. 直接使用浏览器打开下载链接
        const link = document.createElement('a')
        link.href = fileUrl
        link.download = fileName
        document.body.appendChild(link)
        link.click()
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
  <!-- 知识库上传弹窗 -->
  <div v-if="showKbDialog" class="kb-dialog-mask">
    <div class="kb-dialog">
      <div class="kb-dialog-content">
        <div v-if="kbDialogMsg === '知识库上传中(๑•̀ㅂ•́)و✧'">
          <div class="kb-dialog-spinner"></div>
        </div>
        <div class="kb-dialog-msg">{{ kbDialogMsg }}</div>
      </div>
    </div>
  </div>

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
          <th v-if="role == 'tutor'">资料ID</th>
          <th>资料名称</th>
          <th>所属章节</th>
          <th>类型</th>
          <th>上传时间</th>
          <th v-if="role == 'tutor'">版本号</th>
          <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="resource in resourcesVer" :key="resource.id">
          <td v-if="role == 'tutor'">{{ resource.id }}</td>
          <td>{{ resource.title }}</td>
          <td>{{ getSectionTitle(resource.sectionId) }}</td>
          <td>{{ resource.type || '-' }}</td>
          <td>{{ resource.createdAt || '-' }}</td>
          <td v-if="role == 'tutor'">{{ resource.version }}</td>
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

      <!-- 新增：上传到知识库勾选项 -->
      <div class="form-group">
        <label>
          <input type="checkbox" v-model="uploadToKnowledgeBase" />
          上传到知识库
        </label>
      </div>

      <div class="form-row">
        <div class="form-group form-group-half">
          <label for="sectionId">所属章节</label>
          <select
            id="sectionId"
            v-model="uploadForm.sectionId"
            class="form-select"
          >
            <option value="-1">无章节</option>
            <option
              v-for="section in course?.sections"
              :key="section.id"
              :value="section.id"
            >
              {{ section.title }}
            </option>
          </select>
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

        <!-- 班级选择（当选择仅特定班级时显示） -->
        <div v-if="uploadForm.visibility === 'CLASS_ONLY' && course?.classes?.length" class="form-group form-group-half">
            <label>选择班级</label>
            <select v-model="selectedClass" class="form-select">
                <option value="">请选择班级</option>
                <option v-for="class_ in course.classes" :key="class_.id" :value="class_.id">
                    {{ class_.name }} ({{ class_.classCode }})
                </option>
            </select>
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
          class="form-select"
        >
          <option value="">所有章节</option>
          <option
            v-for="section in course?.sections"
            :key="section.id"
            :value="section.id"
          >
            {{ section.title }}
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
            <th v-if="role == 'tutor'">资料ID</th>
            <th>资料名称</th>
            <th>所属章节</th>
            <th>类型</th>
            <th>上传时间</th>
            <th v-if="role == 'tutor'">版本号</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="resource in resources" :key="resource.id">
            <td v-if="role == 'tutor'">{{ resource.id }}</td>
            <td>{{ resource.title }}</td>
            <td>{{ getSectionTitle(resource.sectionId) }}</td>
            <td>{{ resource.type || '-' }}</td>
            <td>{{ resource.createdAt || '-' }}</td>
            <td v-if="role == 'tutor'">{{ resource.version+1 }}</td>
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
    max-height: 480px;
    overflow-y: auto;
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
  max-height: 480px;
  overflow-y: auto;
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

.form-select {
    width: 100%;
    padding: 0.75rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 1rem;
    background-color: white;
}

.form-select:focus {
    outline: none;
    border-color: #2c6ecf;
    box-shadow: 0 0 0 2px rgba(44, 110, 207, 0.1);
}

/* 知识库上传弹窗样式 */
.kb-dialog-mask {
  position: fixed;
  z-index: 9999;
  left: 0; top: 0; right: 0; bottom: 0;
  background: rgba(0,0,0,0.18);
  display: flex;
  align-items: center;
  justify-content: center;
}
.kb-dialog {
  background: #fff;
  border-radius: 12px;
  min-width: 260px;
  min-height: 120px;
  box-shadow: 0 4px 24px rgba(44,110,207,0.13);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem 2.5rem;
  animation: fadein 0.2s;
}
.kb-dialog-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.kb-dialog-msg {
  font-size: 1.1rem;
  color: #512da8;
  margin-top: 1rem;
  text-align: center;
}
.kb-dialog-spinner {
  width: 36px;
  height: 36px;
  border: 4px solid #ede7f6;
  border-top: 4px solid #8e24aa;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 0.5rem;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}

@keyframes fadein {
  from { opacity: 0; }
  to { opacity: 1; }
}
</style>
