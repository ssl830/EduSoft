package org.example.edusoft.service.practice.impl;

import org.example.edusoft.entity.practice.Practice;
import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.PracticeListDTO;
import org.example.edusoft.exception.practice.PracticeException;
import org.example.edusoft.mapper.practice.PracticeMapper;
import org.example.edusoft.mapper.practice.QuestionMapper;
import org.example.edusoft.service.practice.PracticeService;
import org.example.edusoft.utils.NotificationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.List;
import org.example.edusoft.mapper.record.PracticeRecordMapper;
import org.example.edusoft.mapper.practice.PracticeQuestionMapper;
import org.example.edusoft.mapper.practice.SubmissionMapper;
import org.example.edusoft.mapper.practice.AnswerMapper;
import org.example.edusoft.entity.practice.PracticeQuestion;
import org.example.edusoft.entity.practice.Submission;
import org.example.edusoft.entity.practice.Answer;

@Service
public class PracticeServiceImpl implements PracticeService {
    /**
     * 获取所有已截止且未统计得分率的练习ID（定时任务用）
     * 实现：查找end_time早于当前时间的所有练习ID
     */
    @Override
    public List<Long> getAllEndedPracticeIds() {
        // 伪代码：实际应根据业务查找所有已截止且未统计的练习ID
        // 这里只查end_time早于当前时间的练习
        return practiceMapper.findAllEndedPracticeIds(java.time.LocalDateTime.now());
    }

    @Autowired
    private PracticeMapper practiceMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private NotificationUtils notificationUtils;

    @Autowired
    private PracticeRecordMapper practiceRecordMapper;
    @Autowired
    private org.example.edusoft.mapper.practice.SubmissionMapper submissionMapper;
    @Autowired
    private org.example.edusoft.mapper.practice.AnswerMapper answerMapper;

    @Autowired
    private PracticeQuestionMapper practiceQuestionMapper;
    /**
     * 练习截止后统计并写入每题得分率
     */
    @Transactional
    public void updateScoreRateAfterDeadline(Long practiceId) {
        // 1. 获取练习下所有题目
        List<PracticeQuestion> pqList = practiceQuestionMapper.findpqByPracticeId(practiceId);
        if (pqList == null || pqList.isEmpty()) return;
        // 2. 获取所有提交（必须查所有，不加 is_judged 条件）
        List<Submission> submissions = submissionMapper.findByPracticeId(practiceId);
        if (submissions == null || submissions.isEmpty()) return;
        for (PracticeQuestion pq : pqList) {
            Long qid = pq.getQuestionId();
            int totalScore = 0;
            int maxScore = pq.getScore() != null ? pq.getScore() : 0;
            int count = 0;
            for (Submission sub : submissions) {
                List<Answer> answers = answerMapper.findByQuestionIdsAndSubmissionId(java.util.List.of(qid), sub.getId());
                if (answers != null && !answers.isEmpty()) {
                    totalScore += answers.get(0).getScore() != null ? answers.get(0).getScore() : 0;
                    count++;
                }
            }
            double scoreRate = (maxScore > 0 && count > 0) ? ((double) totalScore / (maxScore * count)) : 0.0;
            practiceQuestionMapper.updateScoreRate(practiceId, qid, scoreRate);
        }
    }

    @Override
    @Transactional
    public Practice createPractice(Practice practice) {
        // 验证练习时间
        if (practice.getStartTime() != null && practice.getEndTime() != null
                && practice.getStartTime().isAfter(practice.getEndTime())) {
            throw new PracticeException("PRACTICE_INVALID_TIME", "练习开始时间不能晚于结束时间");
        }

        // 验证必填字段
        if (practice.getTitle() == null || practice.getTitle().trim().isEmpty()) {
            throw new PracticeException("PRACTICE_TITLE_REQUIRED", "练习标题不能为空");
        }
        if (practice.getCourseId() == null) {
            throw new PracticeException("PRACTICE_COURSE_REQUIRED", "课程ID不能为空");
        }
        if (practice.getClassId() == null) {
            throw new PracticeException("PRACTICE_CLASS_REQUIRED", "班级ID不能为空");
        }
        if (practice.getCreatedBy() == null) {
            throw new PracticeException("PRACTICE_CREATOR_REQUIRED", "创建者ID不能为空");
        }

        // 设置创建时间
        practice.setCreatedAt(LocalDateTime.now());

        practiceMapper.createPractice(practice);

        // 获取班级中的所有学生ID
        List<Long> studentIds = practiceMapper.getClassStudentIds(practice.getClassId());

        // 自动创建练习通知：当老师发布新练习时，向班级中的所有学生发送通知
        notificationUtils.createPracticeNotification(
                practice.getId(),
                practice.getTitle(),
                practice.getCourseId(),
                practice.getClassId(),
                studentIds);

        return practice;
    }

    @Override
    @Transactional
    public Practice updatePractice(Practice practice) {
        // 验证练习是否存在
        Practice existingPractice = practiceMapper.getPracticeById(practice.getId());
        if (existingPractice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习不存在");
        }

        // 验证练习时间
        if (practice.getStartTime() != null && practice.getEndTime() != null
                && practice.getStartTime().isAfter(practice.getEndTime())) {
            throw new PracticeException("PRACTICE_INVALID_TIME", "练习开始时间不能晚于结束时间");
        }

        // 只更新提供的字段，其他字段保持不变
        if (practice.getTitle() != null) {
            existingPractice.setTitle(practice.getTitle());
        }
        if (practice.getStartTime() != null) {
            existingPractice.setStartTime(practice.getStartTime());
        }
        if (practice.getEndTime() != null) {
            existingPractice.setEndTime(practice.getEndTime());
        }
        if (practice.getAllowMultipleSubmission() != null) {
            existingPractice.setAllowMultipleSubmission(practice.getAllowMultipleSubmission());
        }

        practiceMapper.updatePractice(existingPractice);
        return existingPractice;
    }

    @Override
    public List<Practice> getPracticeList(Long classId) {
        if (classId == null) {
            throw new PracticeException("PRACTICE_CLASS_REQUIRED", "班级ID不能为空");
        }
        return practiceMapper.getPracticeList(classId);
    }

    @Override
    public Practice getPracticeDetail(Long id) {
        Practice practice = practiceMapper.getPracticeById(id);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习不存在");
        }
        List<Question> questions = questionMapper.getQuestionsByPractice(id);
        // 将score字段赋值到Question对象的score属性
        for (Question q : questions) {
            try {
                java.lang.reflect.Field scoreField = q.getClass().getDeclaredField("score");
                scoreField.setAccessible(true);
                // 由于MyBatis返回的q已经有score字段（见SQL），直接赋值即可
                // 如果没有则跳过
                // 这里假设MyBatis能自动映射score到q.score
            } catch (Exception e) {
                // ignore
            }
        }
        practice.setQuestions(questions);
        return practice;
    }

    @Override
    @Transactional
    public void deletePractice(Long id) {
        Practice practice = practiceMapper.getPracticeById(id);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习不存在");
        }

        // 获取与练习相关的所有提交记录
        List<Long> submissionIds = submissionMapper.findSubmissionIdsByPracticeId(id);

        // 删除与这些提交记录相关的答案
        if (!submissionIds.isEmpty()) {
            answerMapper.deleteAnswersBySubmissionIds(submissionIds);
        }

        // 删除练习关联的题目
        questionMapper.removeAllQuestionsFromPractice(id);

        // 删除练习关联的提交记录
        submissionMapper.removeSubmissionsByPracticeId(id);

        // 删除练习
        practiceMapper.deletePractice(id);
    }

    @Override
    @Transactional
    public void addQuestionToPractice(Long practiceId, Long questionId, Integer score) {
        try {
            // 验证练习是否存在
            Practice practice = practiceMapper.getPracticeById(practiceId);
            if (practice == null) {
                throw new PracticeException("PRACTICE_NOT_FOUND", "练习不存在");
            }

            // 验证题目是否存在
            Question question = questionMapper.getQuestionById(questionId);
            if (question == null) {
                throw new PracticeException("QUESTION_NOT_FOUND", "题目不存在");
            }

            // 验证分值
            if (score <= 0) {
                throw new PracticeException("PRACTICE_INVALID_SCORE", "题目分值必须大于0");
            }

            // 验证题目是否已经在练习中
            List<Question> existingQuestions = questionMapper.getQuestionsByPractice(practiceId);
            boolean questionExists = existingQuestions.stream()
                    .anyMatch(q -> q.getId().equals(questionId));
            if (questionExists) {
                throw new PracticeException("QUESTION_ALREADY_EXISTS", "该题目已添加到练习中");
            }

            questionMapper.addQuestionToPractice(practiceId, questionId, score);
        } catch (PracticeException e) {
            throw e;
        } catch (Exception e) {
            if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                throw new PracticeException("PRACTICE_ADD_QUESTION_FAILED",
                        "添加题目失败：练习ID " + practiceId + " 不存在或已被删除");
            }
            throw new PracticeException("PRACTICE_ADD_QUESTION_FAILED",
                    "添加题目失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeQuestionFromPractice(Long practiceId, Long questionId) {
        // 验证练习是否存在
        Practice practice = practiceMapper.getPracticeById(practiceId);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习不存在");
        }

        questionMapper.removeQuestionFromPractice(practiceId, questionId);
    }

    @Override
    public List<Question> getPracticeQuestions(Long practiceId) {
        // 验证练习是否存在
        Practice practice = practiceMapper.getPracticeById(practiceId);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习不存在");
        }
        return questionMapper.getQuestionsByPractice(practiceId);
    }

    @Override
    public void favoriteQuestion(Long studentId, Long questionId) {
        // 检查题目是否已收藏
        if (!practiceMapper.isQuestionFavorited(studentId, questionId)) {
            practiceMapper.insertFavoriteQuestion(studentId, questionId);
        }
    }

    @Override
    public void unfavoriteQuestion(Long studentId, Long questionId) {
        practiceMapper.deleteFavoriteQuestion(studentId, questionId);
    }

    @Override
    public List<Map<String, Object>> getFavoriteQuestions(Long studentId) {
        return practiceMapper.findFavoriteQuestions(studentId);
    }

    @Override
    public void addWrongQuestion(Long studentId, Long questionId, String wrongAnswer) {
        // 从数据库获取题目信息，包括正确答案
        Question question = questionMapper.findById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 检查是否已存在该错题
        if (practiceMapper.existsWrongQuestion(studentId, questionId)) {
            // 如果存在，更新错误次数和最后错误时间
            practiceMapper.updateWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        } else {
            // 如果不存在，新增错题记录
            practiceMapper.insertWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        }
    }

    @Override
    public List<Map<String, Object>> getWrongQuestions(Long studentId) {
        return practiceMapper.findWrongQuestions(studentId);
    }

    @Override
    public List<Map<String, Object>> getWrongQuestionsByCourse(Long studentId, Long courseId) {
        return practiceMapper.findWrongQuestionsByCourse(studentId, courseId);
    }

    @Override
    public void removeWrongQuestion(Long studentId, Long questionId) {
        practiceMapper.deleteWrongQuestion(studentId, questionId);
    }

    @Override
    public List<Map<String, Object>> getCoursePractices(Long studentId, Long courseId) {
        // 先查询学生所在班级
        Long classId = practiceMapper.findClassIdByUserAndCourse(studentId, courseId);
        if (classId == null) {
            throw new RuntimeException("未找到学生所在班级");
        }

        // 根据班级ID和课程ID查询练习，同时传入studentId
        return practiceMapper.findCoursePractices(courseId, classId, studentId);
    }

    @Override
    public List<PracticeListDTO> getStudentPracticeList(Long studentId, Long classId) {
        if (studentId == null || classId == null) {
            throw new IllegalArgumentException("学生ID和班级ID不能为空");
        }
        return practiceMapper.getStudentPracticeList(studentId, classId);
    }

    @Override
    public List<Map<String, Object>> getTeacherPractices(Long teacherId) {
        return practiceMapper.getPracticesByTeacherId(teacherId);
    }

    @Override
    public Map<String, Object> getSubmissionStats(Long practiceId) {
        return practiceRecordMapper.getSubmissionStatsByPracticeId(practiceId);
    }
}

