<script setup lang="ts">
import { ref, onMounted, watch, nextTick } from 'vue'
import ResourceApi from '../../api/resource'
import { useAuthStore } from "../../stores/auth.ts";

interface Props {
    courseId: string;
    isTeacher: boolean;
    course?: {
        id: number;
        sections: Array<{
            id: string;
            title: string;
            sortOrder: string;
        }>;
        classes: Array<{
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

// 添加视频对象的类型定义
interface VideoResource {
    id: number;
    title: string;
    description?: string;
    fileUrl: string;
    duration?: number;
    chapterId: number;
    chapterName: string;
    progress: number;
    lastWatch?: number;
    formattedDuration: string;
    formattedWatched: string;
}

// 修改 ref 的类型
const videos = ref<VideoResource[]>([])
const loading = ref(true)
const error = ref('')

// Filters
const selectedChapter = ref('')
const searchQuery = ref('')

// Chapters (will be populated from videos)
const chapters = ref<Array<{id: number, name: string}>>([]) // 修改为对象数组

// Video player modal
const showVideoModal = ref(false)
const currentVideo = ref<VideoResource | null>(null)
const videoProgress = ref(0)
const watchedDuration = ref(0)
const playerRef = ref<HTMLVideoElement | null>(null)

// 视频摘要相关
const showSummaryModal = ref(false)
const currentSummary = ref<any>(null)
const summaryLoading = ref(false)
const summaryError = ref('')

// 上传教学资源视频
const showUploadForm = ref(false)
const uploadForm = ref({
    courseId: 0,
    title: '',
    chapterId: -1, // 默认为-1表示无章节属性
    chapterName: '',
    file: null as File | null,
    description: '',
    createdBy: 0
})
const uploadProgress = ref(0)
const uploadError = ref('')

const authStore = useAuthStore()

// 当 props.course.sections 更新时，同步到本地 chapters 列表
watch(() => props.course?.sections, sections => {
    if (sections) {
        chapters.value = [
            { id: -1, name: '无章节属性' },
            ...sections.map(s => ({ id: parseInt(s.id), name: s.title }))
        ]
    }
}, { immediate: true })

// 初始化创建者
onMounted(() => {
    uploadForm.value.createdBy = authStore.user?.id || 0
})

// Fetch videos
const fetchVideos = async () => {
    loading.value = true
    error.value = ''

    try {
        const formData = new FormData()
        formData.append('studentId', authStore.user?.id?.toString() || '')
        formData.append('chapterId', selectedChapter.value?.toString() || '-1')
        
        const response = await ResourceApi.getChapterResources(props.courseId, formData)
        console.log("responseaaaaaaa:", response);
        videos.value = response.data.map((video: any) => {
            // 直接使用后端返回的 progress 和 lastWatch/lastPosition
            const progress = video.progress || 0
            const lastWatch = video.lastPosition || 0
            console.log("==================")
            console.log(video.duration)
            console.log(formatDuration(video.duration))
            
            return {
                id: video.resourceId || video.id,
                title: video.title || '',
                description: video.description,
                fileUrl: video.fileUrl || '',
                duration: video.duration,
                chapterId: video.chapterId || -1,
                chapterName: video.chapterName || '',
                progress: progress,
                lastWatch: lastWatch,
                formattedDuration: formatDuration(video.duration),
                formattedWatched: formatDuration(lastWatch)
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
const formatDuration = (seconds: number | undefined): string => {
    if (!seconds) return '00:00'
    const mins = Math.floor(seconds / 60)
    const secs = Math.floor(seconds % 60)
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

// Open video player
const openVideoPlayer = async (video: VideoResource) => {
    try {
        // 每次打开视频时都重新获取最新的进度数据
        if (authStore.user?.id) {
            const formData = new FormData()
            formData.append('studentId', authStore.user.id.toString())
            formData.append('chapterId', selectedChapter.value?.toString() || '-1')
            
            // 先获取最新的视频资源数据
            const resourceResponse = await ResourceApi.getChapterResources(props.courseId, formData)
            const updatedVideo = resourceResponse.data.find((v: any) => v.resourceId === video.id)
            
            if (updatedVideo) {
                // 更新视频对象的进度信息
                video.progress = updatedVideo.progress || 0
                video.lastWatch = updatedVideo.lastPosition || 0
                video.formattedWatched = formatDuration(updatedVideo.lastPosition || 0)
            }

            // 再获取具体的播放进度记录
            const progressResponse = await ResourceApi.getVideoProgress(
                video.id.toString(),
                authStore.user.id.toString()
            )
            
            if (progressResponse.data.code === 200 && progressResponse.data.data) {
                const { progress, lastPosition } = progressResponse.data.data
                video.progress = progress
                video.lastWatch = lastPosition
                video.formattedWatched = formatDuration(lastPosition)
            }
        }

        // 设置视频播放器的状态
        currentVideo.value = video
        showVideoModal.value = true
        
        // 设置进度相关的值
        videoProgress.value = video.progress
        maxAllowedProgress.value = video.progress
        watchedDuration.value = video.lastWatch || 0

        // 等待视频加载完成后设置播放位置
        nextTick(() => {
            if (playerRef.value && video.lastWatch) {
                playerRef.value.currentTime = video.lastWatch
            }
        })
    } catch (err) {
        console.error('获取播放记录失败:', err)
        // 即使获取记录失败，也允许播放视频，使用视频对象中现有的进度
        currentVideo.value = video
        showVideoModal.value = true
        videoProgress.value = video.progress
        maxAllowedProgress.value = video.progress
        watchedDuration.value = video.lastWatch || 0
    }
}

// Add new ref for max allowed progress
const maxAllowedProgress = ref(0)

// Handle video metadata loaded (for duration)
const handleMetadataLoaded = async () => {
    if (!playerRef.value || !currentVideo.value?.id) return
    const duration = Math.floor(playerRef.value.duration)
    
    // Only update if duration has changed
    if (currentVideo.value.duration !== duration) {
        try {
            // Update the resource with duration
            await ResourceApi.updateResourceDuration(currentVideo.value.id.toString(), duration)
            currentVideo.value.duration = duration
        } catch (err) {
            console.error('更新视频时长失败:', err)
        }
    }
}

// Handle seeking (prevent seeking beyond allowed position)
const handleSeeking = () => {
    if (!playerRef.value || !currentVideo.value?.duration) return
    const seekTime = playerRef.value.currentTime
    const maxAllowedTime = (maxAllowedProgress.value / 100) * currentVideo.value.duration
    
    // If trying to seek beyond max allowed position, prevent it
    if (seekTime > maxAllowedTime) {
        playerRef.value.currentTime = maxAllowedTime
    }
}

// Handle video time update
const handleTimeUpdate = () => {
    if (!playerRef.value || !currentVideo.value?.duration) return
    const currentTime = Math.floor(playerRef.value.currentTime)
    
    // 更新当前观看时长
    watchedDuration.value = currentTime
    
    // 计算当前播放位置对应的进度百分比
    const currentProgress = Math.round((currentTime / currentVideo.value.duration) * 100)
    
    // 如果当前进度大于之前的进度，则更新进度条
    if (currentProgress > videoProgress.value) {
        videoProgress.value = currentProgress
        maxAllowedProgress.value = currentProgress // 同时更新最大允许进度
    }
}

// Close video player and save progress
const closeVideoPlayer = async () => {
    if (!currentVideo.value?.id || !authStore.user?.id || watchedDuration.value <= 0) {
        showVideoModal.value = false
        return
    }

    try {
        const currentProgress = Math.round((watchedDuration.value / (currentVideo.value.duration || 1)) * 100)
        // 只有当当前位置对应的进度超过历史进度时才更新progress
        const progressToSave = currentProgress > maxAllowedProgress.value ? currentProgress : maxAllowedProgress.value

        const formData = new FormData()
        formData.append('resourceId', currentVideo.value.id.toString())
        formData.append('studentId', authStore.user.id.toString())
        formData.append('progress', progressToSave.toString())
        formData.append('position', watchedDuration.value.toString())
        
        await ResourceApi.recordWatchProgress(formData)
        
        // 更新本地视频列表中的进度
        const updatedVideo = videos.value.find(v => v.id === currentVideo.value?.id)
        if (updatedVideo) {
            updatedVideo.progress = progressToSave
            updatedVideo.lastWatch = watchedDuration.value
            updatedVideo.formattedWatched = formatDuration(watchedDuration.value)
        }
    } catch (err) {
        console.error('保存观看进度失败:', err)
    }
    showVideoModal.value = false
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

    // 检查文件大小 (限制为500MB)
    const maxSize = 500 * 1024 * 1024 // 500MB
    if (uploadForm.value.file.size > maxSize) {
        uploadError.value = '视频文件大小不能超过500MB'
        return
    }

    // 检查文件类型
    const allowedTypes = ['video/mp4', 'video/avi', 'video/mov', 'video/wmv', 'video/flv', 'video/webm']
    if (!allowedTypes.includes(uploadForm.value.file.type)) {
        uploadError.value = '请选择支持的视频格式：MP4、AVI、MOV、WMV、FLV、WebM'
        return
    }

    uploadProgress.value = 0
    uploadError.value = ''
    console.log(props.courseId)

    const formData = new FormData()
    formData.append('file', uploadForm.value.file)
    formData.append('courseId', props.courseId)
    formData.append('chapterId', uploadForm.value.chapterId.toString())
    formData.append('chapterName', props.course?.sections.find(section => parseInt(section.id) === uploadForm.value.chapterId)?.title || '')
    formData.append('createdBy', uploadForm.value.createdBy.toString())
    formData.append('title', uploadForm.value.title)
    // description 必须有字段，即使为空
    formData.append('description', uploadForm.value.description || '')

    let retryCount = 0
    const maxRetries = 2

    const attemptUpload = async (): Promise<void> => {
        try {
            await ResourceApi.uploadVideo(formData, (progress) => {
                uploadProgress.value = progress
            })

            // Reset form and refresh list
            showUploadForm.value = false
            resetUploadForm()
            fetchVideos()

        } catch (err: any) {
            console.error('上传错误:', err)
            
            // 检查是否是网络错误且可以重试
            const isNetworkError = err.message?.includes('Network Error') || 
                                 err.code === 'ERR_CONNECTION_RESET' ||
                                 err.code === 'ERR_NETWORK'
            
            if (isNetworkError && retryCount < maxRetries) {
                retryCount++
                uploadError.value = `上传失败，正在重试 (${retryCount}/${maxRetries})...`
                // 等待2秒后重试
                await new Promise(resolve => setTimeout(resolve, 2000))
                return attemptUpload()
            } else {
                // 根据错误类型显示不同的错误信息
                if (isNetworkError) {
                    uploadError.value = '网络连接失败，请检查网络连接后重试'
                } else if (err.response?.status === 413) {
                    uploadError.value = '文件太大，服务器无法处理'
                } else if (err.response?.status === 500) {
                    uploadError.value = '服务器处理错误，请稍后再试'
                } else {
                    uploadError.value = '上传视频失败：' + (err.message || '未知错误')
                }
            }
        }
    }

    await attemptUpload()
}

// Reset upload form
const resetUploadForm = () => {
    uploadForm.value = {
        courseId: parseInt(props.courseId),
        title: '',
        chapterId: -1, // 默认为-1表示无章节属性
        chapterName: '',
        file: null,
        description: '',
        createdBy: authStore.user?.id || 0
    }
    uploadProgress.value = 0
    uploadError.value = ''
}

// 视频摘要相关方法
const openSummaryModal = async (video: VideoResource) => {
    currentVideo.value = video  // 设置当前视频
    summaryLoading.value = true
    summaryError.value = ''
    showSummaryModal.value = true
    currentSummary.value = null

    try {
        const response = await ResourceApi.getVideoSummary(video.id.toString())
        if (response && response.code === 200) {
            currentSummary.value = response.data
        } else {
            summaryError.value = '获取视频摘要失败'
        }
    } catch (err) {
        console.error('获取视频摘要失败:', err)
        summaryError.value = '获取视频摘要失败，可能尚未生成摘要'
    } finally {
        summaryLoading.value = false
    }
}

const generateSummary = async (video: VideoResource) => {
    summaryLoading.value = true
    summaryError.value = ''

    try {
        const response = await ResourceApi.generateVideoSummary(video.id.toString())
        if (response && response.code === 200) {
            currentSummary.value = response.data
        } else {
            summaryError.value = '生成视频摘要失败'
        }
    } catch (err) {
        console.error('生成视频摘要失败:', err)
        summaryError.value = '生成视频摘要失败，请稍后再试'
    } finally {
        summaryLoading.value = false
    }
}

const closeSummaryModal = () => {
    showSummaryModal.value = false
    currentSummary.value = null
    summaryError.value = ''
}

const formatTime = (seconds: number): string => {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
}

const formatFileSize = (bytes: number): string => {
    if (bytes === 0) return '0 Bytes'
    
    const k = 1024
    const sizes = ['Bytes', 'KB', 'MB', 'GB']
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    
    return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
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
            <h2>课程视频</h2>            <button
                v-if="isTeacher"
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

            <div class="form-group">
                <label for="chapterId">所属章节</label>
                <select
                    id="chapterId"
                    v-model="uploadForm.chapterId"
                    class="form-select"
                >
                    <option value="-1">无章节属性</option>
                    <option 
                        v-for="section in props.course?.sections" 
                        :key="section.id" 
                        :value="parseInt(section.id)"
                    >
                        {{ section.title }}
                    </option>
                </select>
            </div>

            <div class="form-group">
                <label for="description">视频描述</label>
                <textarea
                    id="description"
                    v-model="uploadForm.description"
                    placeholder="输入视频描述"
                    rows="3"
                ></textarea>
            </div>

            <div class="form-group">
                <label for="video">选择视频文件</label>
                <p class="file-hint">支持格式：MP4、AVI、MOV、WMV、FLV、WebM，文件大小不超过500MB</p>
                <input
                    id="video"
                    type="file"
                    accept="video/*"
                    @change="handleFileChange"
                    required
                />
                <div v-if="uploadForm.file" class="file-info">
                    <p><strong>文件名：</strong>{{ uploadForm.file.name }}</p>
                    <p><strong>文件大小：</strong>{{ formatFileSize(uploadForm.file.size) }}</p>
                    <p><strong>文件类型：</strong>{{ uploadForm.file.type }}</p>
                </div>
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
                <label for="chapterFilter">选择章节:</label>
                <select
                    id="chapterFilter"
                    v-model="selectedChapter"
                    class="chapter-select"
                >
                    <option value="">所有章节</option>
                    <option 
                        v-for="section in props.course?.sections" 
                        :key="section.id" 
                        :value="section.id"
                    >
                        {{ section.title }}
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
                    <th>视频时长</th>
                    <th>完成进度</th>
                    <th>操作</th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="video in videos" :key="video.id">
                    <td>{{ video.title }}</td>
                    <td>{{ video.chapterId }}.{{ video.chapterName || '-' }}</td>
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
                        <button
                            class="btn-action summary"
                            @click="openSummaryModal(video)"
                            title="查看摘要"
                        >
                            摘要
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
                    <h3>{{ currentVideo?.title || '' }}</h3>
                    <button class="close-btn" @click="closeVideoPlayer">&times;</button>
                </div>

                <div class="video-container">
                    <video
                        ref="playerRef"
                        :src="currentVideo?.fileUrl"
                        controls
                        autoplay
                        @timeupdate="handleTimeUpdate"
                        @loadedmetadata="handleMetadataLoaded"
                        @seeking="handleSeeking"
                    ></video>
                </div>

                <div class="progress-info">
                    <div class="progress-container">
                        {{ currentVideo?.description || '' }}
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
                        已观看: {{ formatDuration(watchedDuration) }} / {{ currentVideo?.formattedDuration || '00:00' }}
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 视频摘要模态框 -->
    <div v-if="showSummaryModal" class="modal-overlay" @click="closeSummaryModal">
        <div class="summary-modal" @click.stop>
            <div class="modal-header">
                <h3>视频摘要</h3>
                <button class="close-btn" @click="closeSummaryModal">&times;</button>
            </div>
            
            <div class="modal-content">
                <div v-if="summaryLoading" class="loading">
                    生成摘要中...
                </div>
                
                <div v-else-if="summaryError" class="error">
                    <p>{{ summaryError }}</p>
                    <button 
                        class="btn-primary" 
                        @click="generateSummary(currentVideo!)"
                        :disabled="summaryLoading"
                    >
                        生成摘要
                    </button>
                </div>
                
                <div v-else-if="currentSummary" class="summary-content">
                    <!-- 概述 -->
                    <div class="summary-section">
                        <h4>内容概述</h4>
                        <p>{{ currentSummary.summary }}</p>
                    </div>
                    
                    <!-- 阶段划分 -->
                    <div v-if="currentSummary.stages && currentSummary.stages.length > 0" class="summary-section">
                        <h4>阶段划分</h4>
                        <div class="stages-list">
                            <div 
                                v-for="(stage, index) in currentSummary.stages" 
                                :key="index"
                                class="stage-item"
                            >
                                <div class="stage-header">
                                    <span class="stage-title">{{ stage.title }}</span>
                                    <span class="stage-time">
                                        {{ formatTime(stage.startTime) }} - {{ formatTime(stage.endTime) }}
                                    </span>
                                </div>
                                <p class="stage-description">{{ stage.description }}</p>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 知识点 -->
                    <div v-if="currentSummary.keyPoints && currentSummary.keyPoints.length > 0" class="summary-section">
                        <h4>重要知识点</h4>
                        <div class="keypoints-list">
                            <div 
                                v-for="(point, index) in currentSummary.keyPoints" 
                                :key="index"
                                class="keypoint-item"
                                :class="`importance-${point.importance}`"
                            >
                                <div class="keypoint-header">
                                    <span class="keypoint-text">{{ point.point }}</span>
                                    <span class="keypoint-time">{{ formatTime(point.timestamp) }}</span>
                                </div>
                                <span class="importance-badge">{{ 
                                    point.importance === 'high' ? '高' : 
                                    point.importance === 'medium' ? '中' : '低' 
                                }}</span>
                            </div>
                        </div>
                    </div>
                    
                    <!-- 重要时间点 -->
                    <div v-if="currentSummary.keyTimestamps && currentSummary.keyTimestamps.length > 0" class="summary-section">
                        <h4>重要时间点</h4>
                        <div class="timestamps-list">
                            <div 
                                v-for="(timestamp, index) in currentSummary.keyTimestamps" 
                                :key="index"
                                class="timestamp-item"
                            >
                                <span class="timestamp-time">{{ formatTime(timestamp.time) }}</span>
                                <span class="timestamp-type">{{ 
                                    timestamp.type === 'concept' ? '概念' : 
                                    timestamp.type === 'example' ? '示例' : 
                                    timestamp.type === 'summary' ? '总结' : '问题' 
                                }}</span>
                                <span class="timestamp-event">{{ timestamp.event }}</span>
                            </div>
                        </div>
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

.chapter-select {
    padding: 0.5rem;
    border: 1px solid #ddd;
    border-radius: 4px;
    min-width: 200px;
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

.form-group textarea {
    resize: vertical;
}

/* 视频摘要模态框样式 */
.summary-modal {
    background: white;
    border-radius: 8px;
    max-width: 800px;
    width: 90%;
    max-height: 90vh;
    overflow-y: auto;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 1.5rem;
    border-bottom: 1px solid #e0e0e0;
}

.modal-header h3 {
    margin: 0;
    color: #333;
}

.close-btn {
    background: none;
    border: none;
    font-size: 2rem;
    cursor: pointer;
    color: #666;
    padding: 0;
    width: 2rem;
    height: 2rem;
    display: flex;
    align-items: center;
    justify-content: center;
}

.close-btn:hover {
    color: #333;
}

.modal-content {
    padding: 1.5rem;
}

.loading, .error {
    text-align: center;
    padding: 2rem;
}

.error {
    color: #d32f2f;
}

.summary-content {
    max-height: 60vh;
    overflow-y: auto;
}

.summary-section {
    margin-bottom: 2rem;
}

.summary-section h4 {
    color: #2c6ecf;
    margin-bottom: 1rem;
    font-size: 1.1rem;
    border-bottom: 2px solid #e3f2fd;
    padding-bottom: 0.5rem;
}

.summary-section p {
    color: #666;
    line-height: 1.6;
}

/* 阶段列表样式 */
.stages-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
}

.stage-item {
    background: #f8f9fa;
    padding: 1rem;
    border-radius: 6px;
    border-left: 4px solid #2c6ecf;
    margin-bottom: 1rem;
}

.stage-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 0.5rem;
}

.stage-title {
    font-weight: 600;
    color: #333;
}

.stage-time {
    color: #666;
    font-size: 0.9rem;
    background: #e3f2fd;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
}

.stage-description {
    color: #666;
    margin: 0;
    line-height: 1.5;
}

/* 知识点列表样式 */
.keypoints-list {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
}

.keypoint-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 0.75rem;
    background: #f8f9fa;
    border-radius: 6px;
    margin-bottom: 0.75rem;
}

.keypoint-header {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.keypoint-text {
    color: #333;
}

.keypoint-time {
    color: #666;
    font-size: 0.9rem;
}

.importance-badge {
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-size: 0.8rem;
    font-weight: 600;
    min-width: 2rem;
    text-align: center;
}

.importance-high .importance-badge {
    background: #ffebee;
    color: #d32f2f;
}

.importance-medium .importance-badge {
    background: #fff3e0;
    color: #f57c00;
}

.importance-low .importance-badge {
    background: #e8f5e8;
    color: #388e3c;
}

/* 时间点列表样式 */
.timestamps-list {
    display: flex;
    flex-direction: column;
    gap: 0.5rem;
}

.timestamp-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    padding: 0.5rem;
    border-bottom: 1px solid #e0e0e0;
    margin-bottom: 0.5rem;
}

.timestamp-time {
    color: #2c6ecf;
    font-weight: 600;
    min-width: 4rem;
}

.timestamp-type {
    background: #e3f2fd;
    color: #2c6ecf;
    padding: 0.25rem 0.5rem;
    border-radius: 4px;
    font-size: 0.8rem;
    min-width: 3rem;
    text-align: center;
}

.timestamp-event {
    color: #666;
    flex: 1;
}

/* 摘要按钮样式 */
.btn-action.summary {
    background-color: #4caf50;
    color: white;
}

.btn-action.summary:hover {
    background-color: #45a049;
}

/* 文件上传相关样式 */
.file-hint {
    font-size: 0.875rem;
    color: #757575;
    margin: 0.25rem 0;
}

.file-info {
    margin-top: 0.75rem;
    padding: 0.75rem;
    background-color: #f5f5f5;
    border-radius: 4px;
    border: 1px solid #e0e0e0;
}

.file-info p {
    margin: 0.25rem 0;
    font-size: 0.875rem;
}

.file-info strong {
    color: #424242;
}
</style>
