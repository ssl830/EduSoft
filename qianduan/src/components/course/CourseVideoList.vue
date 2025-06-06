<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import ResourceApi from '../../api/resource'
import { useAuthStore } from "../../stores/auth.ts";

const props = defineProps<{
    courseId: string;
    isTeacher: boolean;
}>()

const videos = ref<any[]>([])
const loading = ref(true)
const error = ref('')

// Filters
const selectedChapter = ref('')
const searchQuery = ref('')

// Chapters (will be populated from videos)
const chapters = ref<Array<{id: number, name: string}>>([]) // 修改为对象数组

// Video player modal
const showVideoModal = ref(false)
const currentVideo = ref<any>(null)
const videoProgress = ref(0)
const watchedDuration = ref(0)
const playerRef = ref<HTMLVideoElement | null>(null)

// File upload
const showUploadForm = ref(false)
const uploadForm = ref({
    courseId: 0,
    chapterId: -1, // 默认改为-1 (无章节)
    createdBy: 0,
    title: '',
    description: '',
    file: null as File | null,
})
const uploadProgress = ref(0)
const uploadError = ref('')

const authStore = useAuthStore()

// Fetch videos
const fetchVideos = async () => {
    loading.value = true
    error.value = ''

    try {
        const response = await ResourceApi.getChapterResources(props.courseId, {
            studentId: authStore.user?.id.toString(),
            chapterId: selectedChapter.value || -1
        })

        videos.value = response.data.data.map((video: any) => {
            const progress = video.lastWatch && video.duration
                ? Math.min(100, Math.round((video.lastWatch / video.duration) * 100))
                : 0

            return {
                ...video,
                progress,
                formattedDuration: formatDuration(video.duration),
                formattedWatched: formatDuration(video.lastWatch || 0)
            }
        })

        // 提取章节信息 - 修改为对象数组
        const chapterMap = new Map<number, string>();
        videos.value.forEach(video => {
            if (video.chapterId && video.chapterName) {
                chapterMap.set(video.chapterId, video.chapterName);
            }
        });

        // 转换为数组并排序
        chapters.value = Array.from(chapterMap, ([id, name]) => ({ id, name }))
            .sort((a, b) => a.id - b.id);

    } catch (err) {
        error.value = '获取视频列表失败，请稍后再试'
        console.error(err)
    } finally {
        loading.value = false
    }
}


// Format seconds to MM:SS
const formatDuration = (seconds: number) => {
    if (!seconds) return '00:00'
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// Open video player
const openVideoPlayer = (video: any) => {
    currentVideo.value = video
    showVideoModal.value = true
    // 使用 lastWatch 字段
    watchedDuration.value = video.lastWatch || 0
    videoProgress.value = video.progress || 0
}

// Close video player and save progress
const closeVideoPlayer = async () => {
    if (currentVideo.value && watchedDuration.value > 0) {
        try {
            // 使用新的进度记录接口
            await ResourceApi.recordWatchProgress({
                resourceId: currentVideo.value.id,
                studentId: authStore.user?.id,
                progress: watchedDuration.value, // 实际进度
                position: watchedDuration.value // 当前位置
            })
            fetchVideos()
        } catch (err) {
            console.error('保存观看进度失败:', err)
        }
    }
    showVideoModal.value = false
}

// Handle video time update
const handleTimeUpdate = () => {
    if (!playerRef.value) return
    const currentTime = playerRef.value.currentTime
    watchedDuration.value = currentTime
    if (currentVideo.value?.duration) {
        videoProgress.value = Math.min(100, Math.round((currentTime / currentVideo.value.duration) * 100))
    }
}

// Handle file selection
const handleFileChange = (event: Event) => {
    const input = event.target as HTMLInputElement
    if (input.files && input.files.length > 0) {
        uploadForm.value.file = input.files[0]
    }
}

// Upload video
const uploadVideo = async () => {
    if (!uploadForm.value.title) {
        uploadError.value = '请填写视频标题'
        return
    }
    if (!uploadForm.value.file) {
        uploadError.value = '请选择视频文件'
        return
    }

    uploadProgress.value = 0
    uploadError.value = ''
    console.log(props.courseId)

    const formData = new FormData()
    formData.append('file', uploadForm.value.file)
    formData.append('courseId', props.courseId) // 注意字段名
    formData.append('chapterId', uploadForm.value.chapterId.toString()) // 注意字段名
    formData.append('createdBy', uploadForm.value.createdBy.toString()) // 注意字段名
    formData.append('title', uploadForm.value.title)

    if (uploadForm.value.description) {
        formData.append('description', uploadForm.value.description)
    }

    try {
        await ResourceApi.uploadVideo(formData, (progress) => {
            uploadProgress.value = progress
        })

        // Reset form and refresh list
        showUploadForm.value = false
        resetUploadForm()
        fetchVideos()

    } catch (err) {
        uploadError.value = '上传视频失败，请稍后再试'
        console.error(err)
    }
}

// Reset upload form
const resetUploadForm = () => {
    uploadForm.value = {
        courseId: 0,
        chapterId: -1, // 默认改为-1 (无章节)
        createdBy: authStore.user?.id || 0,
        title: '',
        description: '',
        file: null
    }
    uploadProgress.value = 0
    uploadError.value = ''
}

// Filter videos when criteria change
watch([selectedChapter, searchQuery], () => {
    fetchVideos()
})

onMounted(() => {
    // 设置上传者ID
    uploadForm.value.createdBy = authStore.user?.id || 0
    fetchVideos()
})
</script>

<template>
    <div class="video-list-container">
        <div class="video-header">
            <h2>课程视频</h2>
            <!--                v-if="isTeacher"-->
            <button
                class="btn-primary"
                @click="showUploadForm = !showUploadForm"
            >
                {{ showUploadForm ? '取消上传' : '上传视频' }}
            </button>
        </div>

        <!-- Upload Form -->
        <div v-if="showUploadForm" class="upload-form">
            <h3>上传视频</h3>

            <div v-if="uploadError" class="error-message">{{ uploadError }}</div>

            <div class="form-group">
                <label for="title">视频标题</label>
                <input
                    id="title"
                    v-model="uploadForm.title"
                    type="text"
                    placeholder="输入视频标题"
                    required
                />
            </div>

            <!-- 新增描述字段 -->
            <div class="form-group">
                <label for="description">视频描述</label>
                <textarea
                    id="description"
                    v-model="uploadForm.description"
                    placeholder="输入视频描述"
                ></textarea>
            </div>

            <div class="form-row">
                <div class="form-group form-group-half">
                    <!-- 改为 chapterId -->
<!--                    <label for="chapterId">所属章节</label>-->
<!--                    <input-->
<!--                        id="chapterId"-->
<!--                        v-model="uploadForm.chapterId"-->
<!--                        type="number"-->
<!--                        placeholder="输入章节ID"-->
<!--                        required-->
<!--                    />-->
                    <!-- 修改为下拉框 -->
                    <label for="chapterId">所属章节</label>
                    <select
                        id="chapterId"
                        v-model="uploadForm.chapterId"
                        required
                    >
                        <option :value="-1">无章节属性</option>
                        <option
                            v-for="chapter in chapters"
                            :key="chapter.id"
                            :value="chapter.id"
                        >
                            {{ chapter.name }}
                        </option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label for="file">选择视频文件</label>
                <input
                    id="file"
                    type="file"
                    accept="video/*"
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
                    @click="uploadVideo"
                >
                    上传
                </button>
            </div>
        </div>

        <!-- Filter Section -->
        <div class="video-filters">
            <div class="filter-section">
                <label for="chapterFilter">按章节筛选:</label>
                <select
                    id="chapterFilter"
                    v-model="selectedChapter"
                >
                    <option value="">所有章节</option>
                    <!-- 修改为显示章节名称 -->
                    <option v-for="chapter in chapters" :key="chapter.id" :value="chapter.id">
                        {{ chapter.name }}
                    </option>
                </select>
            </div>
        </div>

        <!-- Video List -->
        <div v-if="loading" class="loading-container">加载中...</div>
        <div v-else-if="error" class="error-message">{{ error }}</div>
        <div v-else-if="videos.length === 0" class="empty-state">
            暂无课程视频
        </div>
        <div v-else class="video-table-wrapper">
            <table class="video-table">
                <thead>
                <tr>
                    <th>视频名称</th>
                    <th>所属章节</th>
                    <th>总时长</th>
                    <th>完成进度</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="video in videos" :key="video.id">
                    <td>{{ video.title }}</td>
                    <td>{{ video.chapterId }}.{{ video.chapterName  || '-' }}</td>
                    <td>{{ video.formattedDuration }}</td>
                    <td>
                        <div class="progress-container">
                            <div class="progress-bar">
                                <div
                                    class="progress-value"
                                    :style="{ width: `${video.progress}%` }"
                                ></div>
                            </div>
                            <span class="progress-text">{{ video.progress }}%</span>
                        </div>
                        <div class="duration-text">
                            已观看: {{ video.formattedWatched }} / {{ video.formattedDuration }}
                        </div>
                    </td>
                    <td class="actions">
                        <button
                            class="btn-action watch"
                            @click="openVideoPlayer(video)"
                            title="观看"
                        >
                            观看
                        </button>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <!-- Video Player Modal -->
        <div v-if="showVideoModal" class="video-modal">
            <div class="modal-content">
                <div class="modal-header">
                    <h3>{{ currentVideo.title }}</h3>
                    <button class="close-btn" @click="closeVideoPlayer">&times;</button>
                </div>

                <div class="video-container">
                    <video
                        ref="playerRef"
                        :src="currentVideo.fileUrl"
                        controls
                        autoplay
                        @timeupdate="handleTimeUpdate"
                    ></video>
                </div>

                <div class="progress-info">
                    <div class = "progress-container">
                        {{currentVideo.description}}
                    </div>
                    <div class="progress-container">
                        <div class="progress-bar">
                            <div
                                class="progress-value"
                                :style="{ width: `${videoProgress}%` }"
                            ></div>
                        </div>
                        <span class="progress-text">{{ videoProgress }}%</span>
                    </div>
                    <div class="duration-text">
                        已观看: {{ formatDuration(watchedDuration) }} / {{ currentVideo.formattedDuration }}
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<style scoped>
.video-list-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 1.5rem;
}

.video-table-wrapper {
    overflow-x: auto;
}

.video-table {
    width: 100%;
    border-collapse: collapse;
}

.video-table th,
.video-table td {
    padding: 1rem;
    text-align: left;
    border-bottom: 1px solid #e0e0e0;
}

.video-table th {
    background-color: #f5f5f5;
    font-weight: 600;
}

.video-table tr:hover {
    background-color: #f9f9f9;
}

.actions {
    display: flex;
    gap: 0.5rem;
}

.video-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1.5rem;
}

.video-header h2 {
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

.video-filters {
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

.filter-section select,
.filter-section input {
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

.btn-action {
    padding: 0.375rem 0.75rem;
    border-radius: 4px;
    border: none;
    font-size: 0.875rem;
    cursor: pointer;
    transition: background-color 0.2s;
}

.btn-action.watch {
    background-color: #e3f2fd;
    color: #1976d2;
}

.btn-action.watch:hover {
    background-color: #bbdefb;
}

/* Progress styles */
.progress-container {
    display: flex;
    align-items: center;
    gap: 0.5rem;
}

.progress-bar {
    flex-grow: 1;
    height: 8px;
    background-color: #e0e0e0;
    border-radius: 4px;
    overflow: hidden;
}

.progress-value {
    height: 100%;
    background-color: #4caf50;
    border-radius: 4px;
    transition: width 0.3s ease;
}

.progress-text {
    min-width: 40px;
    text-align: right;
    font-size: 0.875rem;
    color: #616161;
}

.duration-text {
    font-size: 0.75rem;
    color: #757575;
    margin-top: 0.25rem;
}

/* Video Modal */
.video-modal {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.7);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background-color: white;
    border-radius: 8px;
    width: 80%;
    max-width: 800px;
    overflow: hidden;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1rem;
    background-color: #f5f5f5;
    border-bottom: 1px solid #e0e0e0;
}

.modal-header h3 {
    margin: 0;
    font-size: 1.25rem;
}

.close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #616161;
}

.video-container {
    position: relative;
    padding-bottom: 56.25%; /* 16:9 aspect ratio */
    height: 0;
    background-color: #000;
}

.video-container video {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
}

.progress-info {
    padding: 1rem;
    background-color: #f9f9f9;
}

/* Radio group */
.radio-group {
    display: flex;
    gap: 1rem;
    margin-top: 0.5rem;
}

.radio-group label {
    align-items: center;
    cursor: pointer;
}

.radio-label {
    margin-left: 0.25rem;
}

input[type="radio"] {
    accent-color: #409eff;
}

@media (max-width: 768px) {
    .form-row {
        flex-direction: column;
        gap: 0;
    }

    .video-filters {
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

    .modal-content {
        width: 95%;
    }
}
</style>
