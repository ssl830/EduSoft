package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.client.CourseClient;
import org.example.edusoft.learning.client.ContentClient;
import org.example.edusoft.learning.exception.PracticeException;
import org.example.edusoft.learning.other.BusinessException;
import org.example.edusoft.learning.dto.PracticeDTO;
import org.example.edusoft.learning.entity.Practice;
import org.example.edusoft.learning.entity.Question;
import org.example.edusoft.learning.mapper.*;
import org.example.edusoft.learning.service.PracticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PracticeServiceImpl implements PracticeService {

    private final PracticeMapper practiceMapper;
    private final QuestionMapper questionMapper;
    private final SubmissionMapper submissionMapper;
    private final FavoriteQuestionMapper favoriteQuestionMapper;
    private final WrongQuestionMapper wrongQuestionMapper;
    private final AnswerMapper answerMapper;

    @Autowired
    private CourseClient courseClient;
    @Autowired
    private ContentClient contentClient;

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
        Object resp = courseClient.getStudentsByClassId(practice.getClassId());
        System.out.println("respppppppppppppppppppppppppppppppppppppppppppppppppppppppppp");
        System.out.println(resp);
        List<Map<String, Object>> studentList = List.of();
        if (resp instanceof Map) {
            Object dataObj = ((Map<?, ?>) resp).get("data");
            if (dataObj instanceof List) {
                studentList = (List<Map<String, Object>>) dataObj;
            }
        } else if (resp instanceof List) {
            studentList = (List<Map<String, Object>>) resp;
        }
        List<Long> studentIds = studentList.stream()
                .map(m -> {
                    Object userIdObj = m.get("userId");
                    return userIdObj == null ? null : Long.valueOf(userIdObj.toString());
                })
                .filter(id -> id != null)
                .toList();

        // 新建通知
        Map<String, Object> notification = Map.of(
            "type", "practice",
            "title", practice.getTitle(),
            "content", "您有新的练习：" + practice.getTitle(),
            "relatedType", "practice",
            "relatedId", practice.getId(),
            "userIds", studentIds
        );
        contentClient.createNotification(notification);

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

            // 验证题���是否已经在练习中
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
        if (!favoriteQuestionMapper.isQuestionFavorited(studentId, questionId)) {
            favoriteQuestionMapper.insertFavoriteQuestion(studentId, questionId);
        }
    }

    @Override
    public void unfavoriteQuestion(Long studentId, Long questionId) {
        favoriteQuestionMapper.deleteFavoriteQuestion(studentId, questionId);
    }

    @Override
    public List<Map<String, Object>> getFavoriteQuestions(Long studentId) {
        return favoriteQuestionMapper.findFavoriteQuestions(studentId);
    }

    @Override
    public void addWrongQuestion(Long studentId, Long questionId, String wrongAnswer) {
        // 从数据库获取题目信息，包括正确答案
        Question question = questionMapper.findById(questionId);
        if (question == null) {
            throw new RuntimeException("题目不存在");
        }

        // 检查是否已存在该错题
        if (wrongQuestionMapper.existsWrongQuestion(studentId, questionId)) {
            // 如果存在，更新错误次数和最后错误时间
            wrongQuestionMapper.updateWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        } else {
            // 如果不存在，新增错题记录
            wrongQuestionMapper.insertWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        }
    }

    @Override
    public List<Map<String, Object>> getWrongQuestions(Long studentId) {
        return wrongQuestionMapper.findWrongQuestions(studentId);
    }

//    wrongggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg
    @Override
    public List<Map<String, Object>> getWrongQuestionsByCourse(Long studentId, Long courseId) {
        // This method may require a more complex query joining with course table
        // For now, returning all wrong questions
        return wrongQuestionMapper.findWrongQuestions(studentId);
    }

    @Override
    public void removeWrongQuestion(Long studentId, Long questionId) {
        wrongQuestionMapper.deleteWrongQuestion(studentId, questionId);
    }

    @Override
    public List<PracticeDTO> getStudentPracticeList(Long studentId, Long classId) {
        // This requires a custom query and DTO, which is not fully implemented in the original code.
        // Returning null for now.
        return null;
    }
}
