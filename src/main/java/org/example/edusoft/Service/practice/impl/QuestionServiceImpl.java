package org.example.edusoft.service.practice.impl;

import org.example.edusoft.entity.practice.Question;
import org.example.edusoft.entity.practice.QuestionListDTO;
import org.example.edusoft.exception.practice.PracticeException;
import org.example.edusoft.mapper.practice.QuestionMapper;
import org.example.edusoft.service.practice.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class QuestionServiceImpl implements QuestionService {

    private static final String OPTION_SEPARATOR = "|||";

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    @Transactional
    public Question createQuestion(Question question) {
        // 验证必填字段
        if (question.getContent() == null || question.getContent().trim().isEmpty()) {
            throw new PracticeException("QUESTION_CONTENT_REQUIRED", "题目内容不能为空");
        }
        if (question.getType() == null) {
            throw new PracticeException("QUESTION_TYPE_REQUIRED", "题目类型不能为空");
        }
        if (question.getAnswer() == null || question.getAnswer().trim().isEmpty()) {
            throw new PracticeException("QUESTION_ANSWER_REQUIRED", "题目答案不能为空");
        }
        if (question.getCreatorId() == null) {
            throw new PracticeException("QUESTION_CREATOR_REQUIRED", "创建者ID不能为空");
        }
        if (question.getCourseId() == null) {
            throw new PracticeException("QUESTION_COURSE_REQUIRED", "课程ID不能为空");
        }
        if (question.getSectionId() == null) {
            throw new PracticeException("QUESTION_SECTION_REQUIRED", "章节ID不能为空");
        }

        // 处理选项
        if (question.getType() == Question.QuestionType.singlechoice || question.getType() == Question.QuestionType.multiplechoice) {
            if (question.getOptions() == null || question.getOptions().isEmpty()) {
                throw new PracticeException("QUESTION_OPTIONS_REQUIRED", "选择题必须提供选项");
            }
            // 将选项列表转换为字符串
            question.setOptions(String.join(OPTION_SEPARATOR, question.getOptions()));
        }

        // 设置创建时间
        question.setCreatedAt(LocalDateTime.now());

        questionMapper.createQuestion(question);
        return question;
    }

    @Override
    @Transactional
    public Question updateQuestion(Question question) {
        // 验证题目是否存在
        Question existingQuestion = questionMapper.getQuestionById(question.getId());
        if (existingQuestion == null) {
            throw new PracticeException("QUESTION_NOT_FOUND", "题目不存在");
        }

        // 只更新非空字段
        if (question.getContent() != null && !question.getContent().trim().isEmpty()) {
            existingQuestion.setContent(question.getContent());
        }
        if (question.getType() != null) {
            existingQuestion.setType(question.getType());
        }
        if (question.getAnswer() != null && !question.getAnswer().trim().isEmpty()) {
            existingQuestion.setAnswer(question.getAnswer());
        }
        if (question.getAnalysis() != null) {
            existingQuestion.setAnalysis(question.getAnalysis());
        }
        if (question.getSectionId() != null) {
            existingQuestion.setSectionId(question.getSectionId());
        }

        // 处理选项
        if (question.getType() != null && 
            (question.getType() == Question.QuestionType.singlechoice || question.getType() == Question.QuestionType.multiplechoice)) {
            if (question.getOptionsList() != null && !question.getOptionsList().isEmpty()) {
                existingQuestion.setOptionsList(question.getOptionsList());
            }
        }

        questionMapper.updateQuestion(existingQuestion);
        return existingQuestion;
    }

    @Override
    public List<Question> getQuestionList(Long courseId, Integer page, Integer size) {
        if (page < 1) {
            throw new PracticeException("QUESTION_INVALID_PAGE", "页码必须大于0");
        }
        if (size < 1) {
            throw new PracticeException("QUESTION_INVALID_SIZE", "每页大小必须大于0");
        }

        int offset = (page - 1) * size;
        List<Question> questions = questionMapper.getQuestionList(courseId, offset, size);
        // 处理选项
        for (Question question : questions) {
            processQuestionOptions(question);
        }
        return questions;
    }

    @Override
    public Question getQuestionDetail(Long id) {
        Question question = questionMapper.getQuestionById(id);
        if (question == null) {
            throw new PracticeException("QUESTION_NOT_FOUND", "题目不存在");
        }
        processQuestionOptions(question);
        return question;
    }

    @Override
    @Transactional
    public void deleteQuestion(Long id) {
        Question question = questionMapper.getQuestionById(id);
        if (question == null) {
            throw new PracticeException("QUESTION_NOT_FOUND", "题目不存在");
        }
        questionMapper.deleteQuestion(id);
    }

    @Override
    @Transactional
    public void importQuestionsToPractice(Long practiceId, List<Long> questionIds, List<Integer> scores) {
        if (questionIds == null || questionIds.isEmpty()) {
            throw new PracticeException("QUESTION_IDS_REQUIRED", "题目ID列表不能为空");
        }
        if (scores == null || scores.isEmpty()) {
            throw new PracticeException("QUESTION_SCORES_REQUIRED", "分值列表不能为空");
        }
        if (questionIds.size() != scores.size()) {
            throw new PracticeException("QUESTION_SCORES_MISMATCH", "题目ID列表和分值列表长度不匹配");
        }

        // 验证所有题目是否存在
        for (Long questionId : questionIds) {
            Question question = questionMapper.getQuestionById(questionId);
            if (question == null) {
                throw new PracticeException("QUESTION_NOT_FOUND", "题目ID " + questionId + " 不存在");
            }
        }

        // 验证分值是否合法
        for (Integer score : scores) {
            if (score <= 0) {
                throw new PracticeException("QUESTION_INVALID_SCORE", "题目分值必须大于0");
            }
        }

        // 查询当前练习已有的题目及分数
        List<Question> existingQuestions = questionMapper.getQuestionsByPractice(practiceId);
        Map<Long, Integer> existingMap = new java.util.HashMap<>();
        for (Question q : existingQuestions) {
            if (q.getId() != null) {
                existingMap.put(q.getId(), q.getScore());
            }
        }

        // 1. 先移除不在新列表中的题目
        for (Long oldQid : existingMap.keySet()) {
            if (!questionIds.contains(oldQid)) {
                questionMapper.removeQuestionFromPractice(practiceId, oldQid);
            }
        }

        // 2. 新增或更新题目
        for (int i = 0; i < questionIds.size(); i++) {
            Long qid = questionIds.get(i);
            Integer score = scores.get(i);
            if (existingMap.containsKey(qid)) {
                // 已存在，分数不同则更新
                if (!existingMap.get(qid).equals(score)) {
                    questionMapper.updatePracticeQuestionScore(practiceId, qid, score);
                }
            } else {
                // 不存在，插入
                questionMapper.addQuestionToPractice(practiceId, qid, score);
            }
        }
    }

    @Override
    public List<Question> getQuestionsBySection(Long courseId, Long sectionId) {
        if (courseId == null) {
            throw new PracticeException("COURSE_ID_REQUIRED", "课程ID不能为空");
        }
        if (sectionId == null) {
            throw new PracticeException("SECTION_ID_REQUIRED", "章节ID不能为空");
        }
        List<Question> questions = questionMapper.getQuestionsBySection(courseId, sectionId);
        // 处理选项
        for (Question question : questions) {
            processQuestionOptions(question);
        }
        return questions;
    }

    @Override
    @Transactional
    public List<Question> batchCreateQuestions(List<Question> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new PracticeException("QUESTIONS_REQUIRED", "题目列表不能为空");
        }

        // 验证所有题目的必填字段
        for (Question question : questions) {
            if (question.getContent() == null || question.getContent().trim().isEmpty()) {
                throw new PracticeException("QUESTION_CONTENT_REQUIRED", "题目内容不能为空");
            }
            if (question.getType() == null) {
                throw new PracticeException("QUESTION_TYPE_REQUIRED", "题目类型不能为空");
            }
            if (question.getAnswer() == null || question.getAnswer().trim().isEmpty()) {
                throw new PracticeException("QUESTION_ANSWER_REQUIRED", "题目答案不能为空");
            }
            if (question.getCreatorId() == null) {
                throw new PracticeException("QUESTION_CREATOR_REQUIRED", "创建者ID不能为空");
            }
            if (question.getCourseId() == null) {
                throw new PracticeException("QUESTION_COURSE_REQUIRED", "课程ID不能为空");
            }
            if (question.getSectionId() == null) {
                throw new PracticeException("QUESTION_SECTION_REQUIRED", "章节ID不能为空");
            }

            // 处理选项
            if (question.getType() == Question.QuestionType.singlechoice || question.getType() == Question.QuestionType.multiplechoice) {
                if (question.getOptions() == null || question.getOptions().isEmpty()) {
                    throw new PracticeException("QUESTION_OPTIONS_REQUIRED", "选择题必须提供选项");
                }
                // 将选项列表转换为字符串
                question.setOptions(String.join(OPTION_SEPARATOR, question.getOptions()));
            }

            // 设置创建时间
            question.setCreatedAt(LocalDateTime.now());
        }

        for (Question question : questions) {
            questionMapper.createQuestion(question);
        }
        return questions;
    }

    /**
     * 处理题目选项，将字符串转换为列表
     */
    private void processQuestionOptions(Question question) {
        if (question.getType() != null && 
            (question.getType() == Question.QuestionType.singlechoice || question.getType() == Question.QuestionType.multiplechoice) 
            && question.getOptions() != null) {
            question.setOptionsList(Arrays.asList(question.getOptions().split(OPTION_SEPARATOR)));
        }
    }

    @Override
    public List<Question> getQuestionListByTeacherAndSection(Long teacherId, Long courseId, Long sectionId) {
        if (teacherId == null) {
            throw new PracticeException("TEACHER_ID_REQUIRED", "教师ID不能为空");
        }
        if (courseId == null) {
            throw new PracticeException("COURSE_ID_REQUIRED", "课程ID不能为空");
        }
        if (sectionId == null) {
            throw new PracticeException("SECTION_ID_REQUIRED", "章节ID不能为空");
        }
        
        List<Question> questions = questionMapper.getQuestionsByTeacherAndSection(teacherId, courseId, sectionId);
        // 处理选项
        for (Question question : questions) {
            processQuestionOptions(question);
        }
        return questions;
    }

    @Override
    public List<QuestionListDTO> getQuestionListByCourse(Long courseId) {
        if (courseId == null) {
            throw new PracticeException("COURSE_ID_REQUIRED", "课程ID不能为空");
        }
        
        List<Map<String, Object>> questions = questionMapper.getQuestionListWithNames(courseId);
        return questions.stream().map(q -> {
            QuestionListDTO dto = new QuestionListDTO();
            dto.setId(((Number) q.get("id")).longValue());
            dto.setName((String) q.get("content"));
            dto.setCourseId(((Number) q.get("course_id")).longValue());
            dto.setCourseName((String) q.get("course_name"));
            dto.setSectionId(((Number) q.get("section_id")).longValue());
            dto.setSectionName((String) q.get("section_name"));
            dto.setTeacherId(String.valueOf(q.get("creator_id")));
            dto.setType(String.valueOf(q.get("type")));
            dto.setAnswer((String) q.get("answer"));
            
            // 处理选项
            if ("singlechoice".equals(q.get("type")) && q.get("options") != null) {
                String[] options = ((String) q.get("options")).split("\\|\\|\\|");
                List<Map<String, String>> formattedOptions = new ArrayList<>();
                for (int i = 0; i < options.length; i++) {
                    Map<String, String> option = new HashMap<>();
                    option.put("key", String.valueOf((char)('A' + i)));
                    option.put("text", options[i]);
                    formattedOptions.add(option);
                }
                dto.setOptions(formattedOptions);
            } else {
                dto.setOptions(new ArrayList<>());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public List<QuestionListDTO> getAllQuestions() {
        List<Map<String, Object>> questions = questionMapper.getAllQuestionsWithNames();
        return questions.stream().map(q -> {
            QuestionListDTO dto = new QuestionListDTO();
            dto.setId(((Number) q.get("id")).longValue());
            dto.setName((String) q.get("content"));
            dto.setCourseId(((Number) q.get("course_id")).longValue());
            dto.setCourseName((String) q.get("course_name"));
            dto.setSectionId(((Number) q.get("section_id")).longValue());
            dto.setSectionName((String) q.get("section_name"));
            dto.setTeacherId(String.valueOf(q.get("creator_id")));
            dto.setType(String.valueOf(q.get("type")));
            dto.setAnswer((String) q.get("answer"));
            
            // 处理选项
            if ("singlechoice".equals(q.get("type")) && q.get("options") != null) {
                String[] options = ((String) q.get("options")).split("\\|\\|\\|");
                List<Map<String, String>> formattedOptions = new ArrayList<>();
                for (int i = 0; i < options.length; i++) {
                    Map<String, String> option = new HashMap<>();
                    option.put("key", String.valueOf((char)('A' + i)));
                    option.put("text", options[i]);
                    formattedOptions.add(option);
                }
                dto.setOptions(formattedOptions);
            } else {
                dto.setOptions(new ArrayList<>());
            }
            
            return dto;
        }).collect(Collectors.toList());
    }
} 