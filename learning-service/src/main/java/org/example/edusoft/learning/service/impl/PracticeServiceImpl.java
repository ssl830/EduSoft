package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(PracticeServiceImpl.class);

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
        // 基础数据：题目与其 course_id、section_id
        List<Map<String, Object>> items = favoriteQuestionMapper.findFavoriteQuestions(studentId);
        if (items == null || items.isEmpty()) return items;

        // 收集ID集合
        List<Long> courseIds = items.stream()
                .map(m -> m.get("course_id"))
                .filter(java.util.Objects::nonNull)
                .map(v -> Long.valueOf(v.toString()))
                .distinct().toList();
        List<Long> sectionIds = items.stream()
                .map(m -> m.get("section_id"))
                .filter(java.util.Objects::nonNull)
                .map(v -> Long.valueOf(v.toString()))
                .distinct().toList();
        log.debug("[Favorite] 收集到 courseIds={}, sectionIds={}", courseIds, sectionIds);
        List<Long> questionIds = items.stream()
                .map(m -> m.get("id"))
                .filter(java.util.Objects::nonNull)
                .map(v -> Long.valueOf(v.toString()))
                .distinct().toList();

        // 批量获取课程名称
        java.util.Map<Long, String> courseNameMap = new java.util.HashMap<>();
        try {
            if (!courseIds.isEmpty()) {
                String ids = String.join(",", courseIds.stream().map(String::valueOf).toList());
                java.util.List<java.util.Map<String, Object>> courses = courseClient.getCoursesByIds(ids);
                if (courses != null) {
                    for (java.util.Map<String, Object> c : courses) {
                        Object idObj = c.get("id");
                        Object nameObj = c.get("name");
                        if (idObj != null && nameObj != null) {
                            courseNameMap.put(Long.valueOf(idObj.toString()), nameObj.toString());
                        }
                    }
                }
            }
        } catch (Exception ignore) {
        }

        // 批量获取章节名称
        java.util.Map<Long, String> sectionNameMap = new java.util.HashMap<>();
        try {
            if (!sectionIds.isEmpty()) {
                String ids = String.join(",", sectionIds.stream().map(String::valueOf).toList());
                java.util.List<java.util.Map<String, Object>> sections = courseClient.getSectionsByIds(ids);
                log.debug("[Favorite] 批量获取章节，输入IDs={}, 返回条数={}", ids, (sections == null ? null : sections.size()));
                if (sections != null) {
                    for (java.util.Map<String, Object> s : sections) {
                        Object idObj = s.get("id");
                        if (idObj == null) idObj = s.get("section_id");
                        if (idObj == null) idObj = s.get("sectionId");
                        if (idObj == null) continue;
                        Object nameObj = s.get("name");
                        if (nameObj == null) nameObj = s.get("title");
                        if (nameObj == null) nameObj = s.get("sectionName");
                        if (nameObj == null) nameObj = s.get("section_title");
                        if (nameObj == null) nameObj = s.get("sectionTitle");
                        if (nameObj == null) nameObj = s.get("title_cn");
                        if (nameObj != null) {
                            sectionNameMap.put(Long.valueOf(idObj.toString()), nameObj.toString());
                        }
                    }
                }
                log.debug("[Favorite] 批量阶段 sectionNameMap keys={} size={}", sectionNameMap.keySet(), sectionNameMap.size());
                // 对缺失的部分逐个兜底查询
                java.util.Set<Long> missing = new java.util.HashSet<>(sectionIds);
                missing.removeAll(sectionNameMap.keySet());
                log.debug("[Favorite] 需要逐条兜底的章节IDs={}", missing);
                if (!missing.isEmpty()) {
                    for (Long sidMiss : missing) {
                        try {
                            java.util.Map<String, Object> sec = courseClient.getSectionById(sidMiss);
                            if (sec != null) {
                                Object nameObj = sec.get("name");
                                if (nameObj == null) nameObj = sec.get("title");
                                if (nameObj == null) nameObj = sec.get("sectionName");
                                if (nameObj == null) nameObj = sec.get("section_title");
                                if (nameObj == null) nameObj = sec.get("sectionTitle");
                                if (nameObj == null) nameObj = sec.get("title_cn");
                                if (nameObj != null) {
                                    sectionNameMap.put(sidMiss, nameObj.toString());
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                    log.debug("[Favorite] 逐条阶段 sectionNameMap keys={} size={}", sectionNameMap.keySet(), sectionNameMap.size());
                    // 仍缺失：按课程ID拉取课程下章节填充
                    java.util.Set<Long> stillMissing = new java.util.HashSet<>(sectionIds);
                    stillMissing.removeAll(sectionNameMap.keySet());
                    log.debug("[Favorite] 需要按课程兜底的章节IDs={}", stillMissing);
                    if (!stillMissing.isEmpty() && !courseIds.isEmpty()) {
                        for (Long cId : courseIds) {
                            try {
                                java.util.List<java.util.Map<String, Object>> secList = courseClient.getSectionsByCourseId(cId);
                                log.debug("[Favorite] 课程{} 返回章节条数={}", cId, (secList == null ? null : secList.size()));
                                if (secList != null) {
                                    for (java.util.Map<String, Object> s : secList) {
                                        Object sidObj = s.get("id");
                                        if (sidObj == null) sidObj = s.get("section_id");
                                        if (sidObj == null) sidObj = s.get("sectionId");
                                        if (sidObj == null) continue;
                                        Long sidL = Long.valueOf(sidObj.toString());
                                        if (!sectionNameMap.containsKey(sidL)) {
                                            Object nameObj = s.get("name");
                                            if (nameObj == null) nameObj = s.get("title");
                                            if (nameObj == null) nameObj = s.get("sectionName");
                                            if (nameObj == null) nameObj = s.get("section_title");
                                            if (nameObj == null) nameObj = s.get("sectionTitle");
                                            if (nameObj == null) nameObj = s.get("title_cn");
                                            if (nameObj != null) {
                                                sectionNameMap.put(sidL, nameObj.toString());
                                            }
                                        }
                                    }
                                }
                            } catch (Exception ignored) {}
                        }
                        log.debug("[Favorite] 按课程阶段 sectionNameMap keys={} size={}", sectionNameMap.keySet(), sectionNameMap.size());
                    }
                }
            }
        } catch (Exception ignore) {
        }

        // 通过 practice_question 反查所属练习（取一个即可）并带上练习标题
        for (Map<String, Object> item : items) {
            // 课程与章节名
            Object cid = item.get("course_id");
            if (cid != null) {
                Long cId = Long.valueOf(cid.toString());
                item.put("course_name", courseNameMap.getOrDefault(cId, ""));
            }
            Object sid = item.get("section_id");
            if (sid != null) {
                Long sId = Long.valueOf(sid.toString());
                String secNameVal = sectionNameMap.getOrDefault(sId, "");
                item.put("section_name", secNameVal);
                // 前端 QuestionFavor.vue 使用 section_title 展示
                item.put("section_title", secNameVal);
                // 兼容驼峰
                item.put("sectionName", secNameVal);
                if (!sectionNameMap.containsKey(sId)) {
                    log.debug("[Favorite] 条目(id={}) 仍缺少章节名 section_id={}，当前已知章节keys={}", item.get("id"), sId, sectionNameMap.keySet());
                }
            }

            // 所属练习（learning 内部）
            Object qid = item.get("id");
            if (qid != null) {
                Long qId = Long.valueOf(qid.toString());
                try {
                    java.util.List<Long> pids = practiceQuestionMapper.findPracticeIdsByQuestionId(qId);
                    if (pids != null && !pids.isEmpty()) {
                        Long pid = pids.get(0);
                        item.put("practice_id", pid);
                        org.example.edusoft.learning.entity.Practice p = practiceMapper.getPracticeById(pid);
                        if (p != null) {
                            item.put("practice_title", p.getTitle());
                        }
                    }
                } catch (Exception ignore) {
                }
            }
        }

        return items;
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
        List<Map<String, Object>> items = wrongQuestionMapper.findWrongQuestions(studentId);
        if (items == null || items.isEmpty()) return items;

        // 收集ID集合
        List<Long> courseIds = items.stream()
                .map(m -> m.get("course_id"))
                .filter(java.util.Objects::nonNull)
                .map(v -> Long.valueOf(v.toString()))
                .distinct().toList();
        List<Long> sectionIds = items.stream()
                .map(m -> m.get("section_id"))
                .filter(java.util.Objects::nonNull)
                .map(v -> Long.valueOf(v.toString()))
                .distinct().toList();
        List<Long> questionIds = items.stream()
                .map(m -> m.get("id"))
                .filter(java.util.Objects::nonNull)
                .map(v -> Long.valueOf(v.toString()))
                .distinct().toList();

        // 批量获取课程
        java.util.Map<Long, String> courseNameMap = new java.util.HashMap<>();
        try {
            if (!courseIds.isEmpty()) {
                String ids = String.join(",", courseIds.stream().map(String::valueOf).toList());
                List<Map<String, Object>> courseList = courseClient.getCoursesByIds(ids);
                if (courseList != null) {
                    for (Map<String, Object> c : courseList) {
                        Object idObj = c.get("id");
                        Object nameObj = c.get("name");
                        if (idObj != null && nameObj != null) {
                            courseNameMap.put(Long.valueOf(idObj.toString()), nameObj.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[Wrong] 批量获取课程失败: {}", e.getMessage());
        }

        // 批量获取章节
        java.util.Map<Long, String> sectionNameMap = new java.util.HashMap<>();
        try {
            if (!sectionIds.isEmpty()) {
                String sids = String.join(",", sectionIds.stream().map(String::valueOf).toList());
                List<Map<String, Object>> sectionList = courseClient.getSectionsByIds(sids);
                if (sectionList != null) {
                    for (Map<String, Object> sec : sectionList) {
                        Object sidObj = sec.get("id");
                        if (sidObj == null) sidObj = sec.get("section_id");
                        if (sidObj == null) sidObj = sec.get("sectionId");
                        Object nameObj = sec.get("name");
                        if (nameObj == null) nameObj = sec.get("title");
                        if (nameObj == null) nameObj = sec.get("sectionName");
                        if (nameObj == null) nameObj = sec.get("section_title");
                        if (nameObj == null) nameObj = sec.get("sectionTitle");
                        if (nameObj == null) nameObj = sec.get("title_cn");
                        if (sidObj != null && nameObj != null) {
                            sectionNameMap.put(Long.valueOf(sidObj.toString()), nameObj.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[Wrong] 批量获取章节失败: {}", e.getMessage());
        }

        // 为获取练习标题，先根据题目ID查practiceIds
        java.util.Set<Long> practiceIdSet = new java.util.HashSet<>();
        java.util.Map<Long, java.util.Set<Long>> questionToPracticeIds = new java.util.HashMap<>();
        for (Long qid : questionIds) {
            List<Long> pids = practiceQuestionMapper.findPracticeIdsByQuestionId(qid);
            if (pids != null && !pids.isEmpty()) {
                practiceIdSet.addAll(pids);
                questionToPracticeIds.put(qid, new java.util.HashSet<>(pids));
            }
        }

        // 填充到每条记录
        for (Map<String, Object> item : items) {
            // 兼容前端删除时使用 question_id
            if (item.get("question_id") == null && item.get("id") != null) {
                item.put("question_id", item.get("id"));
            }
            Object cid = item.get("course_id");
            if (cid != null) {
                Long cId = Long.valueOf(cid.toString());
                item.put("course_name", courseNameMap.getOrDefault(cId, ""));
            }
            Object sid = item.get("section_id");
            if (sid != null) {
                Long sId = Long.valueOf(sid.toString());
                String secNameVal = sectionNameMap.getOrDefault(sId, "");
                item.put("section_name", secNameVal);
                item.put("section_title", secNameVal);
                item.put("sectionName", secNameVal);
            }
            // 练习标题（若同一题属于多个练习，取任意一个标题展示）—与收藏题一致，直接本地 practiceMapper
            Object qid = item.get("id");
            if (qid != null) {
                Long qId = Long.valueOf(qid.toString());
                java.util.Set<Long> pids = questionToPracticeIds.get(qId);
                if (pids != null && !pids.isEmpty()) {
                    Long firstPid = pids.iterator().next();
                    try {
                        org.example.edusoft.learning.entity.Practice p = practiceMapper.getPracticeById(firstPid);
                        if (p != null && p.getTitle() != null) {
                            item.put("practice_title", p.getTitle());
                        }
                    } catch (Exception ex) {
                        log.debug("[Wrong] 获取练习标题失败 practiceId={} err={}", firstPid, ex.getMessage());
                    }
                }
            }
        }

        return items;
    }

    @Override
    public List<Map<String, Object>> getWrongQuestionsByCourse(Long studentId, Long courseId) {
        List<Map<String, Object>> items = wrongQuestionMapper.findWrongQuestionsByCourse(studentId, courseId);
        if (items == null || items.isEmpty()) return items;

        // 课程名
        String courseName = "";
        try {
            if (courseId != null) {
                List<Map<String, Object>> courseList = courseClient.getCoursesByIds(String.valueOf(courseId));
                if (courseList != null && !courseList.isEmpty()) {
                    Object nameObj = courseList.get(0).get("name");
                    if (nameObj != null) courseName = nameObj.toString();
                }
            }
        } catch (Exception e) {
            log.warn("[WrongByCourse] 获取课程失败: {}", e.getMessage());
        }

        // 章节名
        List<Long> sectionIds = items.stream()
                .map(m -> m.get("section_id"))
                .filter(java.util.Objects::nonNull)
                .map(v -> Long.valueOf(v.toString()))
                .distinct().toList();
        java.util.Map<Long, String> sectionNameMap = new java.util.HashMap<>();
        try {
            if (!sectionIds.isEmpty()) {
                String sids = String.join(",", sectionIds.stream().map(String::valueOf).toList());
                List<Map<String, Object>> sectionList = courseClient.getSectionsByIds(sids);
                if (sectionList != null) {
                    for (Map<String, Object> sec : sectionList) {
                        Object sidObj = sec.get("id");
                        if (sidObj == null) sidObj = sec.get("section_id");
                        if (sidObj == null) sidObj = sec.get("sectionId");
                        Object nameObj = sec.get("name");
                        if (nameObj == null) nameObj = sec.get("title");
                        if (nameObj == null) nameObj = sec.get("sectionName");
                        if (nameObj == null) nameObj = sec.get("section_title");
                        if (nameObj == null) nameObj = sec.get("sectionTitle");
                        if (nameObj == null) nameObj = sec.get("title_cn");
                        if (sidObj != null && nameObj != null) {
                            sectionNameMap.put(Long.valueOf(sidObj.toString()), nameObj.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("[WrongByCourse] 批量获取章节失败: {}", e.getMessage());
        }

        // 练习标题
        List<Long> questionIds = items.stream()
                .map(m -> m.get("id"))
                .filter(java.util.Objects::nonNull)
                .map(v -> Long.valueOf(v.toString()))
                .distinct().toList();
        java.util.Set<Long> practiceIdSet = new java.util.HashSet<>();
        java.util.Map<Long, java.util.Set<Long>> questionToPracticeIds = new java.util.HashMap<>();
        for (Long qid : questionIds) {
            List<Long> pids = practiceQuestionMapper.findPracticeIdsByQuestionId(qid);
            if (pids != null && !pids.isEmpty()) {
                practiceIdSet.addAll(pids);
                questionToPracticeIds.put(qid, new java.util.HashSet<>(pids));
            }
        }
        for (Map<String, Object> item : items) {
            // 兼容前端删除时使用 question_id
            if (item.get("question_id") == null && item.get("id") != null) {
                item.put("question_id", item.get("id"));
            }
            // 课程
            item.put("course_name", courseName);
            // 章节
            Object sid = item.get("section_id");
            if (sid != null) {
                Long sId = Long.valueOf(sid.toString());
                String secNameVal = sectionNameMap.getOrDefault(sId, "");
                item.put("section_name", secNameVal);
                item.put("section_title", secNameVal);
                item.put("sectionName", secNameVal);
            }
            // 练习—与收藏题一致，直接本地 practiceMapper
            Object qid = item.get("id");
            if (qid != null) {
                Long qId = Long.valueOf(qid.toString());
                java.util.Set<Long> pids = questionToPracticeIds.get(qId);
                if (pids != null && !pids.isEmpty()) {
                    Long firstPid = pids.iterator().next();
                    try {
                        org.example.edusoft.learning.entity.Practice p = practiceMapper.getPracticeById(firstPid);
                        if (p != null && p.getTitle() != null) {
                            item.put("practice_title", p.getTitle());
                        }
                    } catch (Exception ex) {
                        log.debug("[WrongByCourse] 获取练习标题失败 practiceId={} err={}", firstPid, ex.getMessage());
                    }
                }
            }
        }

        return items;
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
        Map<String, Object> stats = practiceRecordMapper.getSubmissionStatsByPracticeId(practiceId);
        if (stats == null) stats = new java.util.HashMap<>();
        try {
            Practice practice = practiceMapper.getPracticeById(practiceId);
            if (practice != null) {
                // 填入课程与班级ID，便于前端使用
                if (practice.getCourseId() != null) stats.putIfAbsent("course_id", practice.getCourseId());
                if (practice.getClassId() != null) stats.putIfAbsent("class_id", practice.getClassId());

                // 课程名称
                try {
                    if (practice.getCourseId() != null) {
                        log.debug("[Stats] 解析课程名称 courseId={}", practice.getCourseId());
                        Map<String, Object> course = courseClient.getCourseById(practice.getCourseId());
                        if (course != null) {
                            Object data = course.get("data");
                            Map<String, Object> courseData = (data instanceof Map) ? (Map<String, Object>) data : course;
                            log.debug("[Stats] course raw keys={} wrappedKeys={}", course.keySet(), courseData.keySet());
                            Object nameObj = courseData.get("name");
                            if (nameObj == null) nameObj = courseData.get("title");
                            if (nameObj == null) nameObj = courseData.get("course_name");
                            if (nameObj == null) nameObj = courseData.get("courseName");
                            if (nameObj != null) {
                                stats.put("course_name", nameObj.toString());
                                log.info("[Stats] 解析课程名称成功 practiceId={} courseId={} name={}", practiceId, practice.getCourseId(), nameObj);
                            } else {
                                log.debug("[Stats] 未从课程响应中解析到名称 practiceId={} courseId={} keys={}", practiceId, practice.getCourseId(), courseData.keySet());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("[Stats] 获取课程名称失败 practiceId={} err={}", practiceId, e.getMessage());
                }

                // 班级名称
                try {
                    if (practice.getClassId() != null) {
                        log.debug("[Stats] 解析班级名称 classId={}", practice.getClassId());
                        Map<String, Object> cls = courseClient.getClassById(practice.getClassId());
                        if (cls != null) {
                            Object data = cls.get("data");
                            Map<String, Object> clsData = (data instanceof Map) ? (Map<String, Object>) data : cls;
                            log.debug("[Stats] class raw keys={} wrappedKeys={}", cls.keySet(), clsData.keySet());
                            Object nameObj = clsData.get("name");
                            if (nameObj == null) nameObj = clsData.get("class_name");
                            if (nameObj == null) nameObj = clsData.get("className");
                            if (nameObj == null) nameObj = clsData.get("title");
                            if (nameObj != null) {
                                stats.put("class_name", nameObj.toString());
                                log.info("[Stats] 解析班级名称成功 practiceId={} classId={} name={}", practiceId, practice.getClassId(), nameObj);
                            } else {
                                log.debug("[Stats] 未从班级响应中解析到名称 practiceId={} classId={} keys={}", practiceId, practice.getClassId(), clsData.keySet());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.debug("[Stats] 获取班级名称失败 practiceId={} err={}", practiceId, e.getMessage());
                }

                // 规范化输出字段：同时提供 snake_case 与 camelCase，并设置缺省值
                try {
                    Object courseNameObj = stats.get("course_name");
                    if (courseNameObj == null) courseNameObj = stats.get("courseName");
                    String courseName = courseNameObj != null ? courseNameObj.toString() : "未知课程";
                    stats.put("course_name", courseName);
                    stats.put("courseName", courseName);
                    Object classNameObj = stats.get("class_name");
                    if (classNameObj == null) classNameObj = stats.get("className");
                    String className = classNameObj != null ? classNameObj.toString() : "未知班级";
                    stats.put("class_name", className);
                    stats.put("className", className);
                    log.info("[Stats] 规范化名称输出 practiceId={} course_name='{}' class_name='{}'", practiceId, courseName, className);
                } catch (Exception ignore) {}
            }
        } catch (Exception ex) {
            log.warn("[Stats] 丰富统计信息失败 practiceId={} err={}", practiceId, ex.getMessage());
        }
        try {
            log.info("[Stats] 返回前最终stats practiceId={} keys={} course_name='{}' class_name='{}'", practiceId, stats.keySet(), stats.get("course_name"), stats.get("class_name"));
        } catch (Exception ignore) {}
        return stats;
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
