package org.example.edusoft.service.record;
import java.util.List;
import java.util.Map;
import org.example.edusoft.entity.record.*;

public interface RecordService {
    List<StudyRecord> getStudyRecords(Long studentId);
    List<StudyRecord> getStudyRecordsByCourse(Long studentId, Long courseId);
    List<PracticeRecord> getPracticeRecords(Long studentId);
    List<PracticeRecord> getPracticeRecordsByCourse(Long studentId, Long courseId);
    byte[] exportRecordsToExcel(Long studentId);
    byte[] exportStudyRecordsByCourseToExcel(Long studentId, Long courseId);
    byte[] exportPracticeRecordsToExcel(Long studentId);
    byte[] exportPracticeRecordsByCourseToExcel(Long studentId, Long courseId);
    // 获取提交报告
    Map<String, Object> getSubmissionReport(Long submissionId, Long studentId);
    // 生成提交报告PDF
    byte[] generateSubmissionReportPdf(Map<String, Object> reportData);
}