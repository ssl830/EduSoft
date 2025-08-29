package org.example.edusoft.mapper.resource;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.resource.VideoSummary;
import org.springframework.stereotype.Repository;

/**
 * 视频摘要数据访问层
 */
@Repository
@Mapper
public interface VideoSummaryMapper {

    @Insert("INSERT INTO video_summary (resource_id, video_title, course_name, summary, transcript, duration) " +
            "VALUES (#{resourceId}, #{videoTitle}, #{courseName}, #{summary}, #{transcript}, #{duration})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(VideoSummary videoSummary);

    @Select("SELECT * FROM video_summary WHERE resource_id = #{resourceId}")
    VideoSummary selectByResourceId(Long resourceId);

    @Select("SELECT * FROM video_summary WHERE id = #{id}")
    VideoSummary selectById(Long id);

    @Update("UPDATE video_summary SET video_title = #{videoTitle}, course_name = #{courseName}, " +
            "summary = #{summary}, transcript = #{transcript}, duration = #{duration} WHERE id = #{id}")
    int update(VideoSummary videoSummary);

    @Delete("DELETE FROM video_summary WHERE id = #{id}")
    int delete(Long id);

    @Delete("DELETE FROM video_summary WHERE resource_id = #{resourceId}")
    int deleteByResourceId(Long resourceId);
}
