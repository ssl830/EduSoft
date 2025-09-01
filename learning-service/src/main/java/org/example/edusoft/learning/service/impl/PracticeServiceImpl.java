package org.example.edusoft.learning.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.edusoft.learning.entity.PracticeQuestion;
import org.example.edusoft.learning.client.CourseClient;
import org.example.edusoft.learning.client.ContentClient;
import org.example.edusoft.learning.entity.Answer;
import org.example.edusoft.learning.entity.PracticeSubmission;
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
import org.example.edusoft.learning.mapper.*;

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
        // 妤犲矁鐦夌紒鍐х瘎閺冨爼妫�
        if (practice.getStartTime() != null && practice.getEndTime() != null
                && practice.getStartTime().isAfter(practice.getEndTime())) {
            throw new PracticeException("PRACTICE_INVALID_TIME", "缂佸啩绡勫鈧慨瀣闂傜繝绗夐懗鑺ユ珓娴滃海绮ㄩ弶鐔告闂傦拷");
        }

        // 妤犲矁鐦夎箛鍛綖鐎涙顔�
        if (practice.getTitle() == null || practice.getTitle().trim().isEmpty()) {
            throw new PracticeException("PRACTICE_TITLE_REQUIRED", "缂佸啩绡勯弽鍥暯娑撳秷鍏樻稉铏光敄");
        }
        if (practice.getCourseId() == null) {
            throw new PracticeException("PRACTICE_COURSE_REQUIRED", "鐠囧墽鈻糏D娑撳秷鍏樻稉铏光敄");
        }
        if (practice.getClassId() == null) {
            throw new PracticeException("PRACTICE_CLASS_REQUIRED", "閻濐厾楠嘔D娑撳秷鍏樻稉铏光敄");
        }
        if (practice.getCreatedBy() == null) {
            throw new PracticeException("PRACTICE_CREATOR_REQUIRED", "閸掓稑缂撻懓鍖娑撳秷鍏樻稉铏光敄");
        }

        // 鐠佸墽鐤嗛崚娑樼紦閺冨爼妫�
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
        // 妤犲矁鐦夌紒鍐х瘎閺勵垰鎯佺€涙ê婀�
        Practice existingPractice = practiceMapper.getPracticeById(practice.getId());
        if (existingPractice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "缂佸啩绡勬稉宥呯摠閸︼拷");
        }

        // 妤犲矁鐦夌紒鍐х瘎閺冨爼妫�
        if (practice.getStartTime() != null && practice.getEndTime() != null
                && practice.getStartTime().isAfter(practice.getEndTime())) {
            throw new PracticeException("PRACTICE_INVALID_TIME", "缂佸啩绡勫鈧慨瀣闂傜繝绗夐懗鑺ユ珓娴滃海绮ㄩ弶鐔告闂傦拷");
        }

        // 閸欘亝娲块弬鐗堝絹娓氭稓娈戠€涙顔岄敍灞藉従娴犳牕鐡у▓鍏哥箽閹镐椒绗夐崣锟�
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
            throw new PracticeException("PRACTICE_CLASS_REQUIRED", "閻濐厾楠嘔D娑撳秷鍏樻稉铏光敄");
        }
        return practiceMapper.getPracticeList(classId);
    }

    @Override
    public Practice getPracticeDetail(Long id) {
        Practice practice = practiceMapper.getPracticeById(id);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "缂佸啩绡勬稉宥呯摠閸︼拷");
        }
        List<Question> questions = questionMapper.getQuestionsByPractice(id);
        // 鐏忓敄core鐎涙顔岀挧瀣偓鐓庡煂Question鐎电钖勯惃鍓哻ore鐏炵偞鈧拷
        for (Question q : questions) {
            try {
                java.lang.reflect.Field scoreField = q.getClass().getDeclaredField("score");
                scoreField.setAccessible(true);
                // 閻㈠彉绨琈yBatis鏉╂柨娲栭惃鍓勫鑼病閺堝』core鐎涙顔岄敍鍫ｎ潌SQL閿涘绱濋惄瀛樺复鐠у鈧厧宓嗛崣锟�
                // 婵″倹鐏夊▽鈩冩箒閸掓瑨鐑︽潻锟�
                // 鏉╂瑩鍣烽崑鍥啎MyBatis閼冲€熷殰閸斻劍妲х亸鍓哻ore閸掔殔.score
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
            throw new PracticeException("PRACTICE_NOT_FOUND", "缂佸啩绡勬稉宥呯摠閸︼拷");
        }

        // 閼惧嘲褰囨稉搴ｇ矊娑旂姷娴夐崗宕囨畱閹碘偓閺堝褰佹禍銈堫唶瑜帮拷
        List<Long> submissionIds = submissionMapper.findSubmissionIdsByPracticeId(id);

        // 閸掔娀娅庢稉搴ょ箹娴滄稒褰佹禍銈堫唶瑜版洜娴夐崗宕囨畱缁涙梹顢�
        if (!submissionIds.isEmpty()) {
            answerMapper.deleteAnswersBySubmissionIds(submissionIds);
        }

        // 閸掔娀娅庣紒鍐х瘎閸忓疇浠堥惃鍕暯閻╋拷
        questionMapper.removeAllQuestionsFromPractice(id);

        // 閸掔娀娅庣紒鍐х瘎閸忓疇浠堥惃鍕絹娴溿倛顔囪ぐ锟�
        submissionMapper.removeSubmissionsByPracticeId(id);

        // 閸掔娀娅庣紒鍐х瘎
        practiceMapper.deletePractice(id);
    }

    @Override
    public void addQuestionToPractice(Long practiceId, Long questionId, Integer score) {
        try {
            // 妤犲矁鐦夌紒鍐х瘎閺勵垰鎯佺€涙ê婀�
            Practice practice = practiceMapper.getPracticeById(practiceId);
            if (practice == null) {
                throw new PracticeException("PRACTICE_NOT_FOUND", "缂佸啩绡勬稉宥呯摠閸︼拷");
            }

            // 妤犲矁鐦夋０妯兼窗閺勵垰鎯佺€涙ê婀�
            Question question = questionMapper.getQuestionById(questionId);
            if (question == null) {
                throw new PracticeException("QUESTION_NOT_FOUND", "妫版娲版稉宥呯摠閸︼拷");
            }

            // 妤犲矁鐦夐崚鍡椻偓锟�
            if (score <= 0) {
                throw new PracticeException("PRACTICE_INVALID_SCORE", "妫版娲伴崚鍡椻偓鐓庣箑妞よ銇囨禍锟�0");
            }

            // 妤犲矁鐦夋０姗堟嫹閿熸枻鎷烽弰顖氭儊瀹歌尙绮￠崷銊х矊娑旂姳鑵�
            List<Question> existingQuestions = questionMapper.getQuestionsByPractice(practiceId);
            boolean questionExists = existingQuestions.stream()
                    .anyMatch(q -> q.getId().equals(questionId));
            if (questionExists) {
                throw new PracticeException("QUESTION_ALREADY_EXISTS", "鐠囥儵顣介惄顔煎嚒濞ｈ濮為崚鎵矊娑旂姳鑵�");
            }

            questionMapper.addQuestionToPractice(practiceId, questionId, score);
        } catch (PracticeException e) {
            throw e;
        } catch (Exception e) {
            if (e.getCause() instanceof java.sql.SQLIntegrityConstraintViolationException) {
                throw new PracticeException("PRACTICE_ADD_QUESTION_FAILED",
                        "濞ｈ濮炴０妯兼窗婢惰精瑙﹂敍姘辩矊娑旂嚐D " + practiceId + " 娑撳秴鐡ㄩ崷銊﹀灗瀹歌尪顫﹂崚鐘绘珟");
            }
            throw new PracticeException("PRACTICE_ADD_QUESTION_FAILED",
                    "濞ｈ濮炴０妯兼窗婢惰精瑙﹂敍锟�" + e.getMessage());
        }
    }

    @Override
    public void removeQuestionFromPractice(Long practiceId, Long questionId) {
        // 妤犲矁鐦夌紒鍐х瘎閺勵垰鎯佺€涙ê婀�
        Practice practice = practiceMapper.getPracticeById(practiceId);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "缂佸啩绡勬稉宥呯摠閸︼拷");
        }

        questionMapper.removeQuestionFromPractice(practiceId, questionId);
    }

    @Override
    public List<Question> getPracticeQuestions(Long practiceId) {
        // 妤犲矁鐦夌紒鍐х瘎閺勵垰鎯佺€涙ê婀�
        Practice practice = practiceMapper.getPracticeById(practiceId);
        if (practice == null) {
            throw new PracticeException("PRACTICE_NOT_FOUND", "缂佸啩绡勬稉宥呯摠閸︼拷");
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
        // 娴犲孩鏆熼幑顔肩氨閼惧嘲褰囨０妯兼窗娣団剝浼呴敍灞藉瘶閹奉剚顒滅涵顔剧摕濡楋拷
        Question question = questionMapper.findById(questionId);
        if (question == null) {
            throw new RuntimeException("妫版娲版稉宥呯摠閸︼拷");
        }

        // 濡偓閺屻儲妲搁崥锕€鍑＄€涙ê婀拠銉╂晩妫帮拷
        if (wrongQuestionMapper.existsWrongQuestion(studentId, questionId)) {
            // 婵″倹鐏夌€涙ê婀敍灞炬纯閺備即鏁婄拠顖涱偧閺佹澘鎷伴張鈧崥搴ㄦ晩鐠囶垱妞傞梻锟�
            wrongQuestionMapper.updateWrongQuestion(studentId, questionId, wrongAnswer, question.getAnswer());
        } else {
            // 婵″倹鐏夋稉宥呯摠閸︻煉绱濋弬鏉款杻闁挎瑩顣界拋鏉跨秿
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

    @Override
    public List<PracticeDTO> getStudentPracticeList(Long studentId, Long classId) {
        // This requires a custom query and DTO, which is not fully implemented in the original code.
        // Returning null for now.
        return null;
    }

    @Override
    public List<Map<String, Object>> getTeacherPractices(Long teacherId) {
        List<Map<String, Object>> practices = practiceMapper.getPracticesByTeacherId(teacherId);
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
