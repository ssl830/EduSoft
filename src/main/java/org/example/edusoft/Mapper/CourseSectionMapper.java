package org.example.edusoft.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.CourseSection;
import java.util.List;

/**
 * 课程章节数据访问接口
 * 提供课程章节相关的数据库操作
 */
@Mapper
public interface CourseSectionMapper {
    /**
     * 根据ID查询章节
     * @param id 章节ID
     * @return 章节信息
     */
    @Select("SELECT * FROM CourseSection WHERE id = #{id}")
    CourseSection findById(Long id);

    /**
     * 查询课程的所有章节，按排序号排序
     * @param courseId 课程ID
     * @return 章节列表
     */
    @Select("SELECT * FROM CourseSection WHERE course_id = #{courseId} ORDER BY sort_order")
    List<CourseSection> findByCourseId(Long courseId);

    /**
     * 查询课程的最大章节排序号
     * @param courseId 课程ID
     * @return 最大排序号
     */
    @Select("SELECT MAX(sort_order) FROM CourseSection WHERE course_id = #{courseId}")
    Integer findMaxSortOrderByCourseId(Long courseId);

    /**
     * 插入新章节
     * @param section 章节信息
     * @return 影响的行数
     */
    @Insert("INSERT INTO CourseSection (course_id, title, sort_order) " +
            "VALUES (#{courseId}, #{title}, #{sortOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(CourseSection section);

    /**
     * 更新章节信息
     * @param section 章节信息
     * @return 影响的行数
     */
    @Update("UPDATE CourseSection SET title = #{title}, sort_order = #{sortOrder} " +
            "WHERE id = #{id}")
    int update(CourseSection section);

    /**
     * 删除章节
     * @param id 章节ID
     * @return 影响的行数
     */
    @Delete("DELETE FROM CourseSection WHERE id = #{id}")
    int deleteById(Long id);

    @Delete("DELETE FROM CourseSection")
    int deleteAll();
} 