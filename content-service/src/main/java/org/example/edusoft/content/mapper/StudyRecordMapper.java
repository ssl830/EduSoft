package org.example.edusoft.content.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.content.entity.StudyRecord;

import java.util.List;

@Mapper
public interface StudyRecordMapper {
    @Select("""
                SELECT 
                    lp.id,
                    lp.resource_id,
                    lp.student_id,
                    lp.progress,
                    lp.last_position,
                    lp.watch_count,
                    lp.last_watch_time,
                    lp.created_at,
                    lp.updated_at
                FROM learning_progress lp
                WHERE lp.student_id = #{studentId}
                ORDER BY lp.last_watch_time DESC
            """)
    List<StudyRecord> findStudyRecords(@Param("studentId") Long studentId);
}
