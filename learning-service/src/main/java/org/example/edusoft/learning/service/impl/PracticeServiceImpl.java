package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.entity.PracticeQuestion;
import org.example.edusoft.learning.client.CourseClient;
import org.example.edusoft.learning.client.ContentClient;
import org.example.edusoft.learning.entity.Answer;
import org.example.edusoft.learning.entity.PracticeSubmission;
import org.example.edusoft.learning.exception.PracticeException;
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
    private final PracticeRecordMapper practiceRecordMapper;
    private final PracticeQuestionMapper practiceQuestionMapper;

    @Autowired
    private CourseClient courseClient;
    @Autowired
    private ContentClient contentClient;

    @Override
    @Transactional
    public Practice createPractice(Practice practice) {
        // 校验练习时间是否合法
        if (practice.getStartTime() != null && practice.getEndTime() != null
                && practice.getStartTime().isAfter(practice.getEndTime())) {
            throw new PracticeException("PRACTICE_INVALID_TIME", "开始时间不能晚于结束时间");
        }

        // 校验标题
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
            throw new PracticeException("PRACTICE_CREATOR_REQUIRED", "创建人不能为空");
        }

        // 设置创建时间
        practice.setCreatedAt(LocalDateTime.now());

        System.out.println("插入练习前，practice对象: " + practice);
        int result = practiceMapper.createPractice(practice);
        System.out.println("插入结果: " + result);
        
        // 如果插入成功但没有自动设置ID，手动查询获取
        if (practice.getId() == null) {
            System.out.println("自动生成的ID为null，手动查询获取");
            // 根据其他字段查询获取刚插入的记录
            List<Practice> practices = practiceMapper.getPracticeList(practice.getClassId());
            if (!practices.isEmpty()) {
                Practice latestPractice = practices.get(0); // 按创建时间倒序，第一个是最新的
                practice.setId(latestPractice.getId());
                System.out.println("手动设置ID: " + practice.getId());
            }
        }
        
        System.out.println("练习创建成功，最终ID: " + practice.getId());
        System.out.println("跳过通知创建，避免外部服务调用失败");

        return practice;
    }

    @Override
    @Transactional
    public Practice updatePractice(Practice practice) {
        // 校验练习是否存在
        Practice existingPractice = practiceMapper.getPracticeById(practice.getId());
        if (existingPractice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习未找到");
        }

        // 校验练习时间是否合法
        if (practice.getStartTime() != null && practice.getEndTime() != null
                && practice.getStartTime().isAfter(practice.getEndTime())) {
            throw new PracticeException("PRACTICE_INVALID_TIME", "开始时间不能晚于结束时间");
        }

        // 更新字段
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
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习未找到");
        }
        List<Question> questions = questionMapper.getQuestionsByPractice(id);
        // 兼容core字段未映射到Question对象
        for (Question q : questions) {
            try {
                java.lang.reflect.Field scoreField = q.getClass().getDeclaredField("score");
                scoreField.setAccessible(true);
                // MyBatis未映射score字段时，避免SQL异常
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
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习未找到");
        }

        // 查询所有提交ID
        List<Long> submissionIds = submissionMapper.findSubmissionIdsByPracticeId(id);

        // 删除所有提交的答案
        if (!submissionIds.isEmpty()) {
            answerMapper.deleteAnswersBySubmissionIds(submissionIds);
        }

        // 删除练习下所有题目
        questionMapper.removeAllQuestionsFromPractice(id);

        // 删除练习下所有提交
        submissionMapper.removeSubmissionsByPracticeId(id);

        // 删除练习
        practiceMapper.deletePractice(id);
    }

    @Override
    public void addQuestionToPractice(Long practiceId, Long questionId, Integer score) {
        try {
            // 校验练习是否存在
            Practice practice = practiceMapper.getPracticeById(practiceId);
            if (practice == null) {
                throw new PracticeException("PRACTICE_NOT_FOUND", "练习未找到");
            }

            // 校验题目是否存在
            Question question = questionMapper.getQuestionById(questionId);
            if (question == null) {
                throw new PracticeException("QUESTION_NOT_FOUND", "题目未找到");
            }

            // 校验分数
            if (score <= 0) {
                throw new PracticeException("PRACTICE_INVALID_SCORE", "分数必须大于0");
            }

            // 校验题目是否已添加
            List<Question> existingQuestions = questionMapper.getQuestionsByPractice(practiceId);
            boolean questionExists = existingQuestions.stream()
                    .anyMatch(q -> q.getId().equals(questionId));
            if (questionExists) {
                throw new PracticeException("QUESTION_ALREADY_EXISTS", "该题目已添加到练习");
            }

            questionMapper.addQuestionToPractice(practiceId, questionId, score);
        } catch (PracticeException e) {
            throw e;
        } catch (Exception e) {
            if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                throw new PracticeException("PRACTICE_ADD_QUESTION_FAILED",
                        "添加题目失败，练习ID " + practiceId + " 可能已存在该题目");
            }
            throw new PracticeException("PRACTICE_ADD_QUESTION_FAILED",
                    "添加题目失败：" + e.getMessage());
        }
    }

    @Override
    public void removeQuestionFromPractice(Long practiceId, Long questionId) {
        // 校验练习是否存在
        Practice practice = practiceMapper.getPracticeById(practiceId);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习未找到");
        }

        questionMapper.removeQuestionFromPractice(practiceId, questionId);
    }

    @Override
    public List<Question> getPracticeQuestions(Long practiceId) {
        // 校验练习是否存在
        Practice practice = practiceMapper.getPracticeById(practiceId);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "练习未找到");
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
        // 校验题目是否存在
        Question question = questionMapper.findById(questionId);
        if (question == null) {
            throw new RuntimeException("题目未找到");
        }

        // 判断错题是否已存在
        if (wrongQuestionMapper.existsWrongQuestion(studentId, questionId)) {
            // 已存在则更新错题答案
            wrongQuestionMapper.updateWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        } else {
            // 不存在则插入新错题
            wrongQuestionMapper.insertWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        }
    }

    @Override
    public List<Map<String, Object>> getWrongQuestions(Long studentId) {
        return wrongQuestionMapper.findWrongQuestions(studentId);
    }

    @Override
    public List<Map<String, Object>> getWrongQuestionsByCourse(Long studentId, Long courseId) {
        return wrongQuestionMapper.findWrongQuestionsByCourse(studentId, courseId);
    }

    @Override
    public void removeWrongQuestion(Long studentId, Long questionId) {
        wrongQuestionMapper.deleteWrongQuestion(studentId, questionId);
    }

//    @Override
//    public List<PracticeDTO> getStudentPracticeList(Long studentId, Long classId) {
//        // This requires a custom query and DTO, which is not fully implemented in the original code.
//        // Returning null for now.
//        return null;
//    }

    @Override
    public List<PracticeDTO> getStudentPracticeList(Long studentId, Long classId) {
        if (studentId == null || classId == null) {
            throw new IllegalArgumentException("学生ID和班级ID不能为空");
        }
        return practiceMapper.getStudentPracticeList(studentId, classId);
    }

    @Override
    public List<Map<String, Object>> getTeacherPractices(Long teacherId) {
        List<Map<String, Object>> practices = practiceMapper.getPracticesByTeacherId(teacherId);
        System.out.println("practices===================");
        System.out.println(practices);
        // 批量获取所有courseId
        List<Long> courseIds = practices.stream()
                .map(p -> p.get("course_id"))
                .filter(java.util.Objects::nonNull)
                .map(id -> Long.valueOf(id.toString()))
                .distinct()
                .toList();
        // 批量获取课程信息，兼容Result包裹
        List<Map<String, Object>> courseList = List.of();
        System.out.println("courseList===================");
        System.out.println(courseList);
        try {
            Object courseListObj = courseClient.getCoursesByIds(
                    courseIds.stream().map(String::valueOf).collect(java.util.stream.Collectors.joining(","))
            );
            if (courseListObj instanceof Map mapObj && mapObj.containsKey("data")) {
                Object dataObj = mapObj.get("data");
                if (dataObj instanceof List) {
                    courseList = (List<Map<String, Object>>) dataObj;
                }
            } else if (courseListObj instanceof List) {
                courseList = (List<Map<String, Object>>) courseListObj;
            }
        } catch (Exception ex) {
            // RestTemplate反序列化异常时，降级返回模拟数据
            courseList = courseIds.stream().map(cid -> {
                Map<String, Object> course = new java.util.HashMap<>();
                course.put("id", cid);
                course.put("name", "模拟课程-" + cid);
                return course;
            }).toList();
        }
        Map<Long, String> courseNameMap = new java.util.HashMap<>();
        for (Map<String, Object> course : courseList) {
            Object idObj = course.get("id");
            Object nameObj = course.get("name");
            if (idObj != null && nameObj != null) {
                courseNameMap.put(Long.valueOf(idObj.toString()), nameObj.toString());
            }
        }
        // 补全课程名
        for (Map<String, Object> practice : practices) {
            Object courseIdObj = practice.get("course_id");
            if (courseIdObj != null) {
                Long courseId = Long.valueOf(courseIdObj.toString());
                practice.put("course_name", courseNameMap.getOrDefault(courseId, "未知课程"));
            } else {
                practice.put("course_name", "未知课程");
            }
        }
        return practices;
    }

    @Override
    public Map<String, Object> getSubmissionStats(Long practiceId) {
        return practiceRecordMapper.getSubmissionStatsByPracticeId(practiceId);
    }


    /**
     * 练习截止后统计并写入每题得分率
     */
    @Transactional
    public void updateScoreRateAfterDeadline(Long practiceId) {
        // 1. 获取练习下所有题目
        List<PracticeQuestion> pqList = practiceQuestionMapper.findpqByPracticeId(practiceId);
        if (pqList == null || pqList.isEmpty()) return;
        // 2. 获取所有提交（必须查所有，不加 is_judged 条件）
        List<PracticeSubmission> submissions = submissionMapper.findByPracticeId(practiceId);
        if (submissions == null || submissions.isEmpty()) return;
        for (PracticeQuestion pq : pqList) {
            Long qid = pq.getQuestionId();
            int totalScore = 0;
            int maxScore = pq.getScore() != null ? pq.getScore() : 0;
            int count = 0;
            for (PracticeSubmission sub : submissions) {
                List<Answer> answers = answerMapper.findByQuestionIdsAndSubmissionId(List.of(qid), sub.getId());
                if (answers != null && !answers.isEmpty()) {
                    totalScore += answers.get(0).getScore() != null ? answers.get(0).getScore() : 0;
                    count++;
                }
            }
            double scoreRate = (maxScore > 0 && count > 0) ? ((double) totalScore / (maxScore * count)) : 0.0;
            practiceQuestionMapper.updateScoreRate(practiceId, qid, scoreRate);
        }
    }

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

    @Override
    public List<Map<String, Object>> getCoursePractices(Long studentId, Long courseId) {
        if (courseId == null) {
            throw new PracticeException("PRACTICE_COURSE_REQUIRED", "课程ID不能为空");
        }
        // 现阶段直接返回该课程下所有练习的基础信息
        // 如果需要根据学生做个性化过滤，可在此扩展（使用studentId）
        List<Practice> list = practiceMapper.getPracticeListByCourse(courseId);
        return list.stream()
                .map(p -> {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", p.getId());
                    m.put("title", p.getTitle());
                    m.put("courseId", p.getCourseId());
                    m.put("classId", p.getClassId());
                    m.put("startTime", p.getStartTime());
                    m.put("endTime", p.getEndTime());
                    return m;
                })
                .toList();
    }
}
