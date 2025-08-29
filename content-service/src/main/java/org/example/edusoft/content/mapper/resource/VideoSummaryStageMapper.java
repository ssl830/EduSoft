package org.example.edusoft.mapper.resource;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.resource.VideoSummaryStage;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 视频摘要阶段数据访问层
 */
@Repository
@Mapper
public interface VideoSummaryStageMapper {

    @Insert("INSERT INTO video_summary_stage (summary_id, title, start_time, end_time, description, sort_order) " +
            "VALUES (#{summaryId}, #{title}, #{startTime}, #{endTime}, #{description}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VideoSummaryStage stage);

    @Select("SELECT * FROM video_summary_stage WHERE summary_id = #{summaryId} ORDER BY sort_order ASC")
    List<VideoSummaryStage> selectBySummaryId(Long summaryId);

    @Delete("DELETE FROM video_summary_stage WHERE summary_id = #{summaryId}")
    int deleteBySummaryId(Long summaryId);
}
