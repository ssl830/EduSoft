package org.example.edusoft.mapper.resource;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.resource.VideoSummaryKeypoint;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 视频知识点数据访问层
 */
@Repository
@Mapper
public interface VideoSummaryKeypointMapper {

    @Insert("INSERT INTO video_summary_keypoint (summary_id, point, timestamp, importance) " +
            "VALUES (#{summaryId}, #{point}, #{timestamp}, #{importance})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VideoSummaryKeypoint keypoint);

    @Select("SELECT * FROM video_summary_keypoint WHERE summary_id = #{summaryId} ORDER BY timestamp ASC")
    List<VideoSummaryKeypoint> selectBySummaryId(Long summaryId);

    @Delete("DELETE FROM video_summary_keypoint WHERE summary_id = #{summaryId}")
    int deleteBySummaryId(Long summaryId);
}
