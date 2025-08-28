package org.example.edusoft.mapper.resource;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.resource.VideoSummaryTimestamp;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 视频重要时间点数据访问层
 */
@Repository
@Mapper
public interface VideoSummaryTimestampMapper {

    @Insert("INSERT INTO video_summary_timestamp (summary_id, time, event, type) " +
            "VALUES (#{summaryId}, #{time}, #{event}, #{type})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VideoSummaryTimestamp timestamp);

    @Select("SELECT * FROM video_summary_timestamp WHERE summary_id = #{summaryId} ORDER BY time ASC")
    List<VideoSummaryTimestamp> selectBySummaryId(Long summaryId);

    @Delete("DELETE FROM video_summary_timestamp WHERE summary_id = #{summaryId}")
    int deleteBySummaryId(Long summaryId);
}
