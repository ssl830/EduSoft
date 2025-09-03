package org.example.edusoft.content.mapper;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.content.entity.file.FileInfo;

/**
 * @Date: 2025/5/15 11:30
 * @Description: 资源文件表mapper接口，定义数据库操作方法
 */
@Mapper
public interface FileMapper {


    /**
     * 查询某个父节点下的所有子节点（包括文件夹和文件）
     */
    @Mapper
    List<FileInfo> getChildren(@Param("parentId") Long parentId);


    List<FileInfo> getChildrenWithFilter(
        @Param("parentId") Long parentId,
        @Param("title") String title,
        @Param("type") String type,
        @Param("chapter") Long chapter,
        @Param("regexTitle") String regexTitle
    );

    List<FileInfo> getFileWith(
        @Param("parentId") Long parentId,
        @Param("regexTitle") String regexTitle
    );

    /**
     * 获取某个用户所在的所有班级对应的根文件夹
     * SQL: SELECT * FROM file_node WHERE class_id IN (SELECT class_id FROM class_user WHERE user_id = #{userId}) AND parent_id IS NULL AND is_dir = true
     * @param userId 用户ID
     * @return 根文件夹列表
     */
    @Select("SELECT * FROM file_node WHERE class_id IN (SELECT class_id FROM class_user WHERE user_id = #{userId}) AND parent_id IS NULL AND is_dir = true")
    List<FileInfo> getRootFoldersByUserId(@Param("userId") Long userId);

    /**
     * 根据基础文件名获取所有版本的文件
     */
    List<FileInfo> getVersionsByBaseName(
        @Param("baseName") String baseName,
        @Param("parentId") Long parentId
    );

    /**
     * 获取某个班级对应的根文件夹
     * 对应SQL:
     * SELECT * FROM file_node WHERE class_id = #{classId} AND parent_id IS NULL AND is_dir = true
     */
    @Select("SELECT * FROM file_node WHERE class_id = #{classId} AND parent_id IS NULL AND is_dir = true")
    FileInfo getRootFolderByClassId(@Param("classId") Long classId);

    FileInfo getFolderBySection(
        @Param("parentId") Long parentId,
        @Param("sectiondirId") Long sectiondirId
    );

    /**
     * 获取某个节点及其所有子节点（递归获取整个树）
     */
    List<FileInfo> getAllNodesUnder(@Param("folderId") Long folderId);


    /**
     * 插入一个新节点
     * SQL: INSERT INTO file_node (...) VALUES (...)
     * @param node 文件节点对象
     */

    void insertNode(FileInfo node);

    FileInfo selectById(Long id);

    /**
     * 更新节点信息
     */
    void updateNode(FileInfo node);

    /**
     * 删除节点（物理删除）
     */
    void deleteNodeById(Long id);

    Long getLastVersionId(@Param("parentId") Long parentId, @Param("baseName") String baseName, @Param("currentName") String currentName);

    /**
     * 判断文件夹内是否存在同名文件
     * SQL: SELECT COUNT(*) FROM file_node WHERE parent_id = #{parentId} AND file_name = #{name}
     * @param name 文件名
     * @param parentId 父节点ID
     * @return 存在数量（>0表示存在）
     */
    @Select("SELECT COUNT(*) FROM file_node WHERE parent_id = #{parentId} AND file_name = #{name}")
    int existsByNameAndParent(@Param("name") String name, @Param("parentId") Long parentId);

    /**
     * 检查全局是否存在相同文件名（忽略目录）
     */
    boolean existsByNameGlobally(@Param("name") String name);

    boolean isDir(Long id);

    Long getClassIdByUserandCourse(
        @Param("userId") Long userId,
        @Param("courseId") Long courseId
    );

    List<Long> getAllClassIdsByCourseId(
        @Param("courseId") Long courseId
    );

    /**
     * 获取某课程下的所有文件（支持过滤）
     */
    @org.apache.ibatis.annotations.Select({
        "<script>",
        "SELECT * FROM file_node",
        "WHERE course_id = #{courseId}",
        "AND is_dir = false",
        "<if test='title == null or title == \"\"'>",
        "  AND is_current_version = true",
        "</if>",
        "<if test='title != null and title != \"\"'>",
        "  AND file_name REGEXP #{regexTitle}",
        "</if>",
        "<if test='type != null and type != \"\"'>",
        "  AND file_type = UPPER(#{type})",
        "</if>",
        "<if test='chapter != null and chapter != -1'>",
        "  AND section_id = #{chapter}",
        "</if>",
        "</script>"
    })
    List<FileInfo> getFilesByCourseId(
        @Param("courseId") Long courseId,
        @Param("title") String title,
        @Param("type") String type,
        @Param("chapter") Long chapter,
        @Param("regexTitle") String regexTitle
    );

    List<FileInfo> getFilesByClassIdandChapter(
        @Param("classId") Long classId,
        @Param("title") String title,
        @Param("type") String type,
        @Param("chapter") Long chapter,
        @Param("regexTitle") String regexTitle
    );

    List<FileInfo> getFilesByClassId(
        @Param("classId") Long classId,
        @Param("title") String title,
        @Param("type") String type,
        @Param("regexTitle") String regexTitle
    );
}
