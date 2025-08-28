package org.example.edusoft.mapper.resource;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.resource.LearningProgress;
import java.util.List;

/** 
 * 学习进度数据访问接口
 */
@Mapper  
public interface LearningProgressMapper {
    
    /**
     * 插入或更新学习进度
     * @param progress 学习进度对象
     */
    @Insert({
        "<script>",
        "INSERT INTO learning_progress (resource_id, student_id, progress, last_position, watch_count, last_watch_time)",
        "VALUES (#{resourceId}, #{studentId}, #{progress}, #{lastPosition}, 1, #{lastWatchTime})",
        "ON DUPLICATE KEY UPDATE",
        "progress = #{progress},",
        "last_position = #{lastPosition},",
        "watch_count = watch_count + 1,",
        "last_watch_time = #{lastWatchTime}",
        "</script>"
    })
    void insertOrUpdate(LearningProgress progress);

    /**
     * 根据资源ID和学生ID查询学习进度
     * @param resourceId 资源ID
     * @param studentId 学生ID
     * @return 学习进度对象
     */
    @Select("SELECT * FROM learning_progress WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    LearningProgress selectByResourceAndStudent(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);

    /**
     * 根据资源ID查询所有学习进度
     * @param resourceId 资源ID
     * @return 学习进度列表
     */
    @Select("SELECT * FROM learning_progress WHERE resource_id = #{resourceId}")
    List<LearningProgress> selectByResourceId(Long resourceId);

    /**
     * 根据学生ID查询所有学习进度
     * @param studentId 学生ID
     * @return 学习进度列表
     */
    @Select("SELECT * FROM learning_progress WHERE student_id = #{studentId}")
    List<LearningProgress> selectByStudentId(Long studentId);

    /**
     * 更新学习进度
     * @param progress 学习进度对象
     * @return 影响的行数
     */
    @Update({
        "UPDATE learning_progress",
        "SET progress = #{progress},",
        "last_position = #{lastPosition},",
        "last_watch_time = #{lastWatchTime}",
        "WHERE resource_id = #{resourceId}",
        "AND student_id = #{studentId}"
    })
    int updateWithVersion(LearningProgress progress);

    /**
     * 删除学习进度
     * @param resourceId 资源ID
     * @param studentId 学生ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM learning_progress WHERE resource_id = #{resourceId} AND student_id = #{studentId}")
    int deleteByResourceAndStudent(@Param("resourceId") Long resourceId, @Param("studentId") Long studentId);

    /**
     * 删除资源的所有学习进度记录
     * @param resourceId 资源ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM learning_progress WHERE resource_id = #{resourceId}")
    int deleteByResourceId(@Param("resourceId") Long resourceId);
} 