package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.entity.PracticeRecord;
import org.example.edusoft.learning.entity.StudyRecord;
import org.example.edusoft.learning.mapper.RecordMapper;
import org.example.edusoft.learning.service.RecordService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordMapper recordMapper;

    @Override
    public List<StudyRecord> getStudyRecords(Long studentId) {
        return recordMapper.getStudyRecordsByStudentId(studentId);
    }

    @Override
    public List<StudyRecord> getStudyRecordsByCourse(Long studentId, Long courseId) {
        return recordMapper.getStudyRecordsByStudentAndCourse(studentId, courseId);
    }

    @Override
    public List<PracticeRecord> getPracticeRecords(Long studentId) {
        return recordMapper.getPracticeRecordsByStudentId(studentId);
    }

    @Override
    public List<PracticeRecord> getPracticeRecordsByCourse(Long studentId, Long courseId) {
        return recordMapper.getPracticeRecordsByStudentAndCourse(studentId, courseId);
    }

    @Override
    public Map<String, Object> getSubmissionReport(Long submissionId, Long studentId) {
        // This method requires more complex logic and possibly other service calls.
        // Returning an empty map for now.
        return new HashMap<>();
    }
}
