import axios from './axios'

const ClassApi = {
  // Get all classes for current user
  getUserClasses(id: bigint) {
    return axios.get(`/api/classes/user/${id}`)
  },

  uploadStudents(data: { classId: string, operatorId: string, students: any[] }) {
    return axios.post(`/api/imports/students`, data,
      {
        headers: {
          'Content-Type': 'application/json',
        },
      }
    );
  },

  // Get class by ID
  getClassById(id: bigint) {
    return axios.get(`/api/classes/${id}`)
  },

  // Create new class (teacher only)
  createClass(data: {
    courseId: bigint;
    name: string;
    code?: string;
  }) {
    return axios.post('/api/classes', data)
  },

  getHomeworkList(classId: bigint) {
    return axios.get(`/api/homework/list?classId=${classId}`)
  },

  deleteHomework(homeworkId: bigint) {
    return axios.delete(`/api/homework/${homeworkId}`)
  },

  fetchSubmissions(homeworkId: bigint) {
    return axios.get(`/api/homework/submissions/${homeworkId}`)
  },

  // Download resource
  downloadSubmissionFile(submissionId: bigint) {
    return axios.get(`/api/homework/submission/file/${submissionId}`)
  },

  // Upload resource
  uploadSubmissionFile(homeworkId: bigint, formData: FormData) {
    return axios.post(`/api/homework/submit/${homeworkId}`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
    })
  },

  createHomework(formData: FormData) {
    return axios.post('/api/homework/create', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      },
    })
  },

  downloadHomeworkFile(homeworkId: bigint) {
    return axios.get(`/api/homework/file/${homeworkId}`)
  },

  // Update class (teacher only)
  updateClass(id: string, data: {
    name?: string;
    code?: string;
    syllabus?: string;
    objectives?: string;
    assessment?: string;
  }) {
    return axios.put(`/classes/${id}`, data)
  },

  // Get classes for a class
  getClassClasses(classId: string) {
    return axios.get(`/classes/${classId}/classes`)
  },

  // Create class for a class (teacher only)
  // createClass(classId: string, data: {
  //   name: string;
  // }) {
  //   return axios.post(`/classes/${classId}/classes`, data)
  // },

  // Get class details
  getClassDetails(classId: string) {
    return axios.get(`/classes/${classId}`)
  },

  joinClass(studentId: string, classCode: string) {
    // 使用 URLSearchParams 自动处理编码
    const params = new URLSearchParams({
      studentId,
      classCode
    });

    return axios.post(`/api/classes/join?${params.toString()}`);
  },

  // Get students in a class (teacher/tutor only)
  getClassStudents(classId: string) {
    return axios.get(`/api/classes/${classId}/users`)
  },

  deleteStudents(classId: string, studentId: string){
    return axios.delete(`/api/classes/${classId}/students/${studentId}`)
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

export default ClassApi
