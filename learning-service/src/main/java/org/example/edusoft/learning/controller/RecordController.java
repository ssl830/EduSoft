package org.example.edusoft.learning.controller;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.Result;
import org.example.edusoft.learning.entity.PracticeRecord;
import org.example.edusoft.learning.entity.StudyRecord;
import org.example.edusoft.learning.service.RecordService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/learning/record")
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;

    @GetMapping("/study")
    public Result<List<StudyRecord>> getStudyRecords(@RequestHeader("X-User-Id") Long studentId) {
        return Result.success(recordService.getStudyRecords(studentId));
    }

    @GetMapping("/study/course/{courseId}")
    public Result<List<StudyRecord>> getStudyRecordsByCourse(@PathVariable Long courseId, @RequestHeader("X-User-Id") Long studentId) {
        List<StudyRecord> records = recordService.getStudyRecordsByCourse(studentId, courseId);
        return Result.success(records);
    }

    @GetMapping("/practice")
    public Result<List<StudyRecord>> getPracticeRecords(@RequestHeader("X-User-Id") Long studentId) {
        return Result.success(recordService.getPracticeRecords(studentId));
    }

    @GetMapping("/practice/course/{courseId}")
    public Result<List<StudyRecord>> getPracticeRecordsByCourse(@PathVariable Long courseId, @RequestHeader("X-User-Id") Long studentId) {
        List<StudyRecord> records = recordService.getPracticeRecordsByCourse(studentId, courseId);
        return Result.success(records);
    }
}
