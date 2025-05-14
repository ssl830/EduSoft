import axios from './axios'

const CourseApi = {
  // Get all courses for current user
  getUserCourses() {
    return axios.get('/courses')
  },

  // Get course by ID
  getCourseById(id: string) {
    return axios.get(`/courses/${id}`)
  },

  // Create new course (teacher only)
  createCourse(data: {
    name: string;
    code?: string;
    syllabus?: string;
    objectives?: string;
    assessment?: string;
  }) {
    return axios.post('/courses', data)
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

  // Get students in a class (teacher/assistant only)
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
  }
}

export default CourseApi
