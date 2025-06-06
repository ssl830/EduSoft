import axios from './axios'

const CourseApi = {
  // 获取所有课程列表
  getAllCourses() {
    return axios.get('/api/courses')
  },

  // Get all courses for current user
  getUserCourses(userId: string) {
    if (!userId) {
      console.error('userId is required')
      return Promise.reject(new Error('userId is required'))
    }
    return axios.get(`/api/courses/user/${userId}`)
  },

  // Get course by ID
  getCourseById(id: string) {
    return axios.get(`/api/courses/${id}`)
  },

  uploadSections(courseId: bigint, data: {sections: any[] }){
    return axios.post(`/api/courses/${courseId}/sections/`, data,
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
  },

  deleteSection(courseId: bigint, sectionId: bigint) {
    return axios.post(`/api/courses/${courseId}/sections/${sectionId}`)
  },

  // Create new course (teacher only)
  createCourse(data: {
    teacherId: bigint;
    name: string;
    code?: string;
    outline?: string;
    objective?: string;
    assessment?: string;
  }) {
    return axios.post('/api/courses', data)
  },

  // Update course (teacher only)
  updateCourse(id: string, data: {
    name?: string;
    code?: string;
    syllabus?: string;
    objectives?: string;
    assessment?: string;
  }) {
    return axios.put(`/courses/${id}`, data)
  },

  // Get classes for a course
  getCourseClasses(courseId: string) {
    return axios.get(`/courses/${courseId}/classes`)
  },

  // Create class for a course (teacher only)
  createClass(courseId: string, data: {
    name: string;
  }) {
    return axios.post(`/courses/${courseId}/classes`, data)
  },

  // Get class details
  getClassDetails(classId: string) {
    return axios.get(`/classes/${classId}`)
  },

  // Join class (student only)
  joinClass(classCode: string) {
    return axios.post('/classes/join', { class_code: classCode })
  },

  // Get students in a class (teacher/tutor only)
  getClassStudents(classId: string) {
    return axios.get(`/classes/${classId}/students`)
  },

  // Import students to a class (teacher only)
  importStudents(classId: string, file: File) {
    const formData = new FormData()
    formData.append('file', file)

    return axios.post(`/classes/${classId}/students/import`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
  },

  // 获取用户当前所在的班级
  getUserClasses() {
    return axios.get('/api/classes/user')
  },

  // 获取用户的默认班级（最近活跃的班级）
  getUserDefaultClass() {
    return axios.get('/api/user/default-class')
  },
}

export default CourseApi
