import axios from './axios'

// 练习报告导出格式
export type ExportFormat = 'excel' | 'pdf'

// 学习记录接口
export interface StudyRecord {
  id: number;
  studentId: number;
  courseId: number;
  sectionId: number;
  completed: boolean;
  completedAt: string;
  courseName: string;
  sectionTitle: string;
}

// 练习记录中的题目接口
export interface PracticeQuestion {
  id: number;
  sectionId: number;
  courseId: number;
  content: string;
  type: string;
  options: string;
  studentAnswer: string;
  correctAnswer: string;
  isCorrect: boolean;
  analysis: string;
  score: number;
}

// 练习记录接口
export interface PracticeRecord {
  id: number;
  practiceId: number;
  studentId: number;
  submittedAt: string;
  score: number;
  feedback: string;
  questions: PracticeQuestion[];
  practiceTitle: string;
  courseName: string;
  className: string;
}

// 练习提交报告接口 - 后端实际返回结构
export interface SubmissionReport {
  submissionInfo: {
    id: number;
    practiceId: number;
    studentId: number;
    submittedAt: string;
    score: number;
    feedback: string;
    practiceTitle: string;
    courseName: string;
    className: string;
  };
  questions: Array<{
    id: number;
    sectionId: number;
    courseId: number;
    content: string;
    type: string;
    options: string;
    studentAnswer: string;
    correctAnswer: string;
    isCorrect: boolean;
    analysis: string;
    score: number;
  }>;
  rank: number;
  totalStudents: number;
  percentile: number;
  scoreDistribution: Array<{
    score_range: string;
    count: number;
    percentage: number;
  }>;
}

// 练习报告接口
export interface ExerciseReport {
  exerciseId: string;
  exerciseTitle: string;
  studentId?: string;
  studentName?: string;
  summary: {
    totalScore: number;
    userScore: number;
    completedAt: string;
    timeSpent: number; // 以分钟为单位
    questions: Array<{
      questionId: string;
      questionType: string;
      content: string;
      studentAnswer: string;
      correctAnswer: string;
      isCorrect: boolean;
      analysis: string;
      score: number;
    }>;
    rank: number;
    totalStudents: number;
  };
}

// API 实现
export const StudyRecordsApi = {
  // 获取所有学习记录
  getAllStudyRecords() {
    return axios.get('/api/record/study');
  },

  // 获取指定课程的学习记录
  getStudyRecordsByCourse(courseId: string) {
    return axios.get(`/api/record/study/course/${courseId}`);
  },

  // 获取所有练习记录
  getAllPracticeRecords() {
    return axios.get('/api/record/practice');
  },

  // 获取指定课程的练习记录
  getPracticeRecordsByCourse(courseId: string) {
    return axios.get(`/api/record/practice/course/${courseId}`);
  },

  // 获取学生的学习记录列表（包含筛选和分页）- 保留兼容性
  getStudentStudyRecords(params: {
    exerciseName?: string;
    status?: string;
    page?: number;
    pageSize?: number;
  }) {
    return axios.get('/api/record/student/list', { params });
  },

  // 学生获取单个练习的详细记录，包括题目、答案和反馈
  getStudentExerciseRecordDetail(exerciseId: string) {
    return axios.get(`/api/record/student/exercises/${exerciseId}`);
  },
  // 学生下载练习报告（Excel 或 PDF）
  downloadStudentExerciseReport(exerciseId: string, format: ExportFormat = 'pdf') {
    return axios.get(`/api/record/student/exercises/${exerciseId}/export`, {
      params: { format },
      responseType: 'blob',
      headers: {
        Accept: format === 'excel' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/pdf'
      }
    });
  },  // 导出练习提交报告（通过submissionId）
  exportSubmissionReport(submissionId: string) {
    return axios.get(`/api/record/submission/${submissionId}/export-report`, {
      responseType: 'blob',
      headers: {
        'Accept': '*/*'
      }
    });
  },

  // 获取练习提交详情报告（通过submissionId）
  getSubmissionReport(submissionId: string) {
    return axios.get(`/api/record/submission/${submissionId}/report`);
  },
  // 导出指定课程的学习记录（Excel 格式）
  exportStudyRecordsByCourse(courseId: string, params: {
    format?: ExportFormat;
    startDate?: string;
    endDate?: string;
  } = {}) {
    return axios.get(`/api/record/study/course/${courseId}/export`, {
      params: {
        format: params.format || 'excel',
        startDate: params.startDate,
        endDate: params.endDate
      },
      responseType: 'blob',
      headers: {
        Accept: params.format === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }
    });
  },

  // 直接导出课程学习记录（Excel 格式）- 简化调用
  exportCourseStudyRecords(courseId: string) {
    return axios.get(`/api/record/study/course/${courseId}/export`, {
      responseType: 'blob',
      headers: {
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }
    });
  },

  // 学生导出学习记录（Excel 格式）
  exportStudentStudyRecords(params: {
    exerciseName?: string;
    status?: string;
    startDate?: string;
    endDate?: string;
  } = {}) {
    return axios.get('/api/record/student/export', {
      params,
      responseType: 'blob',
      headers: {
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }
    });
  },

  // 教师/助教查看指定班级所有学生的练习完成情况（学习进度概览）
  getClassExerciseProgress(classId: string, params: {
    exerciseName?: string;
    studentName?: string;
    status?: string;
    page?: number;
    pageSize?: number;
  }) {
    return axios.get(`/api/record/teacher/classes/${classId}/progress`, { params });
  },

  // 教师/助教查看单个学生在某个练习上的详细记录
  getStudentExerciseAttemptForTeacher(exerciseId: string, studentId: string) {
    return axios.get(`/api/record/teacher/exercises/${exerciseId}/students/${studentId}`);
  },
  // 教师/助教导出班级练习报告
  exportClassExerciseReport(params: {
    classId: string;
    exerciseId?: string;
    format?: ExportFormat;
    startDate?: string;
    endDate?: string;
  }) {
    return axios.get(`/api/record/teacher/classes/${params.classId}/export`, {
      params: {
        exerciseId: params.exerciseId,
        format: params.format || 'excel',
        startDate: params.startDate,
        endDate: params.endDate
      },
      responseType: 'blob',
      headers: {
        Accept: params.format === 'pdf' ? 'application/pdf' : 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }
    });
  },

  // 教师/助教导出单个学生的练习报告
  exportStudentExerciseReport(params: {
    studentId: string;
    exerciseId: string;
    format?: ExportFormat;
  }) {
    return axios.get(`/api/record/teacher/students/${params.studentId}/exercises/${params.exerciseId}/export`, {
      params: {
        format: params.format || 'pdf'
      },
      responseType: 'blob',
      headers: {
        Accept: params.format === 'excel' ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/pdf'
      }
    });
  },
  // 教师/助教导出整体学习记录（Excel 格式）
  exportTeacherStudyRecords(params: {
    classId?: string;
    exerciseName?: string;
    studentName?: string;
    status?: string;
    startDate?: string;
    endDate?: string;
  } = {}) {
    return axios.get('/api/record/teacher/export', {
      params,
      responseType: 'blob',
      headers: {
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }
    });
  },

  // 导出所有练习记录（Excel 格式）
  exportAllPracticeRecords() {
    return axios.get('/api/record/practice/export', {
      responseType: 'blob',
      headers: {
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }
    });
  },

  // 导出指定课程的练习记录（Excel 格式）
  exportPracticeRecordsByCourse(courseId: string) {
    return axios.get(`/api/record/practice/course/${courseId}/export`, {
      responseType: 'blob',
      headers: {
        Accept: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
      }
    });
  }
}

export default StudyRecordsApi
