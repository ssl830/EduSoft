import axios from './axios'
import  http  from './axios'
import type { AxiosProgressEvent } from 'axios'
import type { ApiResponse } from './axios'

const ResourceApi = {
  // 获取课程视频列表
  getChapterResources(courseId: string, formData: FormData){
    return axios.post(`/api/resources/chapter/${courseId}`, formData)
  },
  // 记录视频播放进度
  recordWatchProgress(formData: FormData){
    return axios.post('/api/resources/progress', formData)
  },
  // 上传视频
  uploadVideo(formData: FormData, onUploadProgress?: (progressEvent: number) => void) {
    return axios.post(`/api/resources/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: progressEvent => {
        if (progressEvent.total && onUploadProgress) {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onUploadProgress(percentCompleted)
        }
      }
    })
  },

  // 视频摘要相关API
  // 获取视频摘要
  getVideoSummary(resourceId: string): Promise<ApiResponse<any>> {
    return axios.get(`/api/video-summary/resource/${resourceId}`)
  },
  // 生成视频摘要
  generateVideoSummary(resourceId: string): Promise<ApiResponse<any>> {
    return axios.post(`/api/video-summary/generate/${resourceId}`)
  },
  // 重新生成视频摘要
  regenerateVideoSummary(resourceId: string): Promise<ApiResponse<any>> {
    return axios.post(`/api/video-summary/regenerate/${resourceId}`)
  },
  // 删除视频摘要
  deleteVideoSummary(resourceId: string): Promise<ApiResponse<any>> {
    return axios.delete(`/api/video-summary/resource/${resourceId}`)
  },



  // Get resources for a course
  getCourseResources(courseId: number, data: {
    courseId: number;
    userId: number;
    chapter?: number;
    type?: string;
    title?: string;
    isTeacher: boolean;
  }) {
    return axios.post(`/api/resources/${courseId}/filelist`, {
      ...data,
      courseId: Number(courseId),
    })
  },

  // Upload resource
  uploadResource(courseId: string, formData: FormData, onUploadProgress?: (progressEvent: number) => void) {
    return axios.post(`/api/courses/${courseId}/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: progressEvent => {
        if (progressEvent.total && onUploadProgress) {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onUploadProgress(percentCompleted)
        }
      }
    })
  },

  // Download resource
  downloadResource(resourceId: string) {
    return axios.get<{
      code: number;
      message: string;
      data: {
        url: string;
        fileName: string;
        fileType?: string;
        expiresIn: number;
      }
    }>(`/api/resources/${resourceId}/download`)
  },

  // Preview resource
  previewResource(resourceId: string) {
    return axios.get<{
      code: number;
      message: string;
      data: {
        url: string;
        fileName: string;
        fileType?: string;
        expiresIn: number;
      }
    }>(`/api/resources/${resourceId}/preview`)
  },

  // Update resource (teacher/tutor only)
  updateResource(resourceId: string, data: {
    title?: string;
    chapter?: string;
    type?: string;
    visible_to_classes?: string[];
    new_file?: File;
  }) {
    const formData = new FormData()

    if (data.title) formData.append('title', data.title)
    if (data.chapter) formData.append('chapter', data.chapter)
    if (data.type) formData.append('type', data.type)

    if (data.visible_to_classes) {
      data.visible_to_classes.forEach((classId, index) => {
        formData.append(`visible_to_classes[${index}]`, classId)
      })
    }

    if (data.new_file) formData.append('new_file', data.new_file)

    return axios.put(`/resources/${resourceId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // Delete resource (teacher/tutor only)
  deleteResource(resourceId: string) {
    // 修正：operatorId作为查询参数
    return axios.delete(`/api/resources/deleteFile/${resourceId}`)
  },

  // Update resource duration
  updateResourceDuration(resourceId: string, duration: number) {
    return axios.put(`/api/resources/${resourceId}/duration`, { duration })
  },

  // Get video progress
  getVideoProgress(resourceId: string, studentId: string) {
    return axios.get<{
      code: number;
      message: string;
      data: {
        progress: number;
        lastPosition: number;
        watchCount: number;
      }
    }>(`/api/resources/progress/${resourceId}/${studentId}`)
  },

  // 修正：上传到知识库（增加重复文件检测，适配联合知识库）
  async uploadToKnowledgeBase(
    file: File,
    formData: FormData,
    courseId?: string,
    onUploadProgress?: (progress: number) => void
  ) {
    // 动态导入避免循环依赖
    const StorageApi = (await import('./storage')).default

    // 检查重复文件
    const { data } = await StorageApi.documentExists(file.name, courseId)
    if (data.exists) {
      // 抛出错误供调用方捕获
      throw new Error('文件已存在于知识库中')
    }

    // 获取当前激活的知识库列表，确保在多知识库环境下正确上传
    try {
      const kbResponse = await StorageApi.listKnowledgeBases()
      if (kbResponse.data && kbResponse.data.length > 0) {
        // 无需额外操作，后端已经处理多知识库环境
        console.log(`正在向${kbResponse.data.length}个激活知识库上传文件`)
      }
    } catch (e) {
      console.warn('获取知识库列表失败，将使用默认知识库', e)
    }

    return axios.post('/api/ai/embedding/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
      onUploadProgress: progressEvent => {
        if (progressEvent.total && onUploadProgress) {
          const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total)
          onUploadProgress(percentCompleted)
        }
      }
    })
  },
}

/**
 * 上传课程资源
 */
export function uploadResource(
  formData: FormData,
  onUploadProgress?: (progressEvent: AxiosProgressEvent) => void
) {
  return http.post('/api/resources/upload', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    onUploadProgress
  })
}

/**
 * 获取课程资源列表
 */
export function getResourceList(courseId: number) {
  return http.get(`/api/resources/list/${courseId}`)
}

/**
 * 删除课程资源
 */
export function deleteResource(resourceId: number) {
  return http.delete(`/api/resources/${resourceId}`)
}

/**
 * 更新资源信息
 */
export function updateResource(resourceId: number, data: {
  name: string
  description: string
}) {
  return http.put(`/api/resources/${resourceId}`, data)
}

export default ResourceApi
