package org.example.edusoft.mapper.practice;

import java.util.List; 
import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.practice.*;


@Mapper
public interface AnswerMapper {
    @Insert({
        "INSERT INTO answer(submission_id, question_id, answer_text, is_judged, correct, score, sort_order)",
        "VALUES(#{submissionId}, #{questionId}, #{answerText}, #{isJudged}, #{correct}, #{score}, #{sortOrder})"
    })
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Answer answer);

    @Update({
        "UPDATE answer SET answer_text=#{answerText}, is_judged=#{isJudged},",
        "correct=#{correct}, score=#{score} WHERE id=#{id}"
    })
    void update(Answer answer);

    @Select("SELECT * FROM answer WHERE submission_id = #{submissionId}")
    List<Answer> findBySubmissionId(Long submissionId);

    @Select("<script>" +
            "SELECT * FROM answer WHERE question_id IN " +
            "<foreach collection='questionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND submission_id = #{submissionId}" +
            "</script>")
    List<Answer> findByQuestionIdsAndSubmissionId(@Param("questionIds") List<Long> questionIds,
                                                  @Param("submissionId") Long submissionId);


    
    @Select("<script>" +
            "SELECT * FROM answer WHERE submission_id IN " +
            "<foreach collection='submissionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND question_id IN " +
            "<foreach collection='questionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<Answer> findByQuestionIdsAndSubmissionIds(@Param("questionIds") List<Long> questionIds,
                                                  @Param("submissionIds") List<Long> submissionIds);

    @Update({
        "UPDATE answer SET score=#{score}, is_judged=#{isJudged}, correct=#{correct} WHERE id=#{id}"
    })
    void updateScoreAndJudgment(Answer answer);

    @Select("<script>" +
            "SELECT * FROM answer WHERE question_id IN " +
            "<foreach collection='questionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND submission_id = #{submissionId}" +
            " AND is_judged = 0" +
            "</script>")
    List<Answer> findunjudgedByQuestionIdsAndSubmissionId(@Param("questionIds") List<Long> questionIds,
                                                  @Param("submissionId") Long submissionId);


    @Select("<script>" +
            "SELECT * FROM answer WHERE submission_id IN " +
            "<foreach collection='submissionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND question_id IN " +
            "<foreach collection='questionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            " AND is_judged = 0" +
            "</script>")
    List<Answer> findunjudgedByQuestionIdsAndSubmissionIds(@Param("questionIds") List<Long> questionIds,
                                                  @Param("submissionIds") List<Long> submissionIds);

    /**
     * 根据提交ID和题目顺序查找答案
     */
    @Select("SELECT * FROM answer WHERE submission_id = #{submissionId} AND sort_order = #{sortOrder}")
    Answer findBySubmissionIdAndSortOrder(@Param("submissionId") Long submissionId, @Param("sortOrder") Long sortOrder);

    @Delete("<script>" +
            "DELETE FROM answer WHERE submission_id IN " +
            "<foreach collection='submissionIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    void deleteAnswersBySubmissionIds(@Param("submissionIds") List<Long> submissionIds);

}