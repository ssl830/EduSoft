package org.example.edusoft.learning.service;

import org.example.edusoft.learning.entity.PracticeRecord;
import org.example.edusoft.learning.entity.StudyRecord;

import java.util.List;
import java.util.Map;

public interface RecordService {
    List<StudyRecord> getStudyRecords(Long studentId);
    List<StudyRecord> getStudyRecordsByCourse(Long studentId, Long courseId);
    List<PracticeRecord> getPracticeRecords(Long studentId);
    List<PracticeRecord> getPracticeRecordsByCourse(Long studentId, Long courseId);
    // byte[] exportRecordsToExcel(Long studentId);
    // byte[] exportStudyRecordsByCourseToExcel(Long studentId, Long courseId);
    // byte[] exportPracticeRecordsToExcel(Long studentId);
    // byte[] exportPracticeRecordsByCourseToExcel(Long studentId, Long courseId);
    Map<String, Object> getSubmissionReport(Long submissionId, Long studentId);
    // byte[] generateSubmissionReportPdf(Map<String, Object> reportData);
}
