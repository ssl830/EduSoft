import axios from './axios'

const ResourceApi = {
  // Get resources for a course
  getCourseResources(courseId: string, params = {}) {
    return axios.get(`/courses/${courseId}/resources`, { params })
  },
  
  // Upload resource
  uploadResource(courseId: string, formData: FormData, onUploadProgress?: (progressEvent: number) => void) {
    return axios.post(`/courses/${courseId}/resources`, formData, {
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
  
  // Get resource details
  getResourceDetails(resourceId: string) {
    return axios.get(`/resources/${resourceId}`)
  },
  
  // Download resource
  downloadResource(resourceId: string) {
    return axios.get(`/resources/${resourceId}/download`, {
      responseType: 'blob'
    })
  },
  
  // Update resource (teacher/assistant only)
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
  
  // Delete resource (teacher/assistant only)
  deleteResource(resourceId: string) {
    return axios.delete(`/resources/${resourceId}`)
  }
}

export default ResourceApi