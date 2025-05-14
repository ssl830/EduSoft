import axios from './axios'

const ExerciseApi = {
  // Get exercises for a course
  getCourseExercises(courseId: string) {
    return axios.get(`/courses/${courseId}/practices`)
  },
  
  // Get teacher's courses (for exercise creation)
  getTeacherCourses() {
    return axios.get('/courses/teaching')
  },
  
  // Get classes for a course
  getCourseClasses(courseId: string) {
    return axios.get(`/courses/${courseId}/classes`)
  },
  
  // Create exercise (teacher/assistant only)
  createExercise(data: {
    title: string;
    courseId: string;
    classIds: string[];
    startTime?: string;
    endTime?: string;
    timeLimit?: number;
    allowedAttempts?: number;
    questions: Array<{
      type: string;
      content: string;
      options?: Array<{ key: string; text: string; }>;
      answer?: string | string[];
      points: number;
    }>;
  }) {
    return axios.post(`/courses/${data.courseId}/practices`, data)
  },
  
  // Get exercise details
  getExerciseDetails(exerciseId: string) {
    return axios.get(`/practices/${exerciseId}`)
  },
  
  // Start exercise attempt (student only)
  startExerciseAttempt(exerciseId: string) {
    return axios.post(`/practices/${exerciseId}/attempts`)
  },
  
  // Submit answer (student only)
  submitAnswer(attemptId: string, data: {
    question_id: string;
    answer_content: string | string[] | object;
  }) {
    return axios.post(`/attempts/${attemptId}/answers`, data)
  },
  
  // Finish exercise attempt (student only)
  finishAttempt(attemptId: string) {
    return axios.post(`/attempts/${attemptId}/finish`)
  },
  
  // Get exercise result (student)
  getExerciseResult(attemptId: string) {
    return axios.get(`/attempts/${attemptId}/result`)
  },
  
  // Get all results for an exercise (teacher/assistant)
  getExerciseResults(exerciseId: string) {
    return axios.get(`/practices/${exerciseId}/results`)
  },
  
  // Grade a subjective answer (teacher/assistant)
  gradeAnswer(answerId: string, data: {
    score: number;
    feedback?: string;
  }) {
    return axios.put(`/answers/${answerId}/grade`, data)
  }
}

export default ExerciseApi