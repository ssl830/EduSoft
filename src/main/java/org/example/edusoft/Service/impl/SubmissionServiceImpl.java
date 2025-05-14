package org.example.edusoft.service.impl;

import org.example.edusoft.entity.Submission;
import org.example.edusoft.mapper.SubmissionMapper;
import org.example.edusoft.service.SubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SubmissionServiceImpl implements SubmissionService {
    @Autowired
    private SubmissionMapper submissionMapper;

    @Override
    public Submission findById(Long id) {
        return submissionMapper.findById(id);
    }

    @Override
    public List<Submission> findByPracticeId(Long practiceId) {
        return submissionMapper.findByPracticeId(practiceId);
    }

    @Override
    public List<Submission> findByStudentId(Long studentId) {
        return submissionMapper.findByStudentId(studentId);
    }

    @Override
    public List<Submission> findByPracticeAndStudent(Long practiceId, Long studentId) {
        return submissionMapper.findByPracticeAndStudent(practiceId, studentId);
    }

    @Override
    @Transactional
    public Submission createSubmission(Submission submission) {
        submissionMapper.insert(submission);
        return submission;
    }

    @Override
    @Transactional
    public Submission updateSubmission(Submission submission) {
        submissionMapper.update(submission);
        return submission;
    }

    @Override
    @Transactional
    public void deleteSubmission(Long id) {
        submissionMapper.deleteById(id);
    }
} 