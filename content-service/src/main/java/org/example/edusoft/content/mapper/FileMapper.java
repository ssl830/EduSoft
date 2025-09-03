package org.example.edusoft.content.mapper;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.file.FileInfo;

/**
 * @Date: 2025/5/15 11:30
 * @Description: 资源文件表mapper接口，定义数据库操作方法
 */
@Mapper
public interface FileMapper {


    /**
     * 查询某个父节点下的所有子节点（包括文件夹和文件）
     * SQL: SELECT * FROM file_node WHERE parent_id = #{parentId}
     * @param parentId 父节点ID
     * @return 子节点列表
     */
    @Select("SELECT * FROM file_node WHERE parent_id = #{parentId}")
    @Results(id = "FileInfoMap", value = {
        @Result(column = "id", property = "id"),
        @Result(column = "file_name", property = "file_name"),
        @Result(column = "is_dir", property = "isDir"),
        @Result(column = "parent_id", property = "parentId"),
        @Result(column = "course_id", property = "courseId"),
        @Result(column = "class_id", property = "classId"),
        @Result(column = "uploader_id", property = "uploaderId"),
        @Result(column = "sectiondir_id", property = "sectiondirId"),
        @Result(column = "file_type", property = "fileType"),
        @Result(column = "section_id", property = "sectionId"),
        @Result(column = "last_file_version", property = "lastVersionId"),
        @Result(column = "is_current_version", property = "isCurrentVersion"),
        @Result(column = "file_size", property = "fileSize"),
        @Result(column = "visibility", property = "visibility"),
        @Result(column = "created_at", property = "createdAt"),
        @Result(column = "updated_at", property = "updatedAt"),
        @Result(column = "file_url", property = "url"),
        @Result(column = "file_version", property = "version"),
        @Result(column = "object_name", property = "objectName")
    })
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
    @ResultMap("FileInfoMap")
    List<FileInfo> getRootFoldersByUserId(@Param("userId") Long userId);

    /**
     * 根据基础文件名获取所有版本的文件
     * SQL:
     * SELECT * FROM file_node
     * WHERE parent_id = #{parentId}
     *   AND (
     *     file_name = #{baseName}
     *     OR file_name REGEXP CONCAT(
     *         '^',
     *         REGEXP_REPLACE(#{baseName}, '([\\$\\^\\*\\+\\?\\(\\)\\[\\]\\{\\}\\|\\\\\\.])', '\\\\$1'),
     *         '\\([0-9]+\\)$'
     *     )
     *   )
     * @param baseName 基础文件名
     * @param parentId 父节点ID
     * @return 版本文件列表
     */
    @Select({"SELECT * FROM file_node",
            " WHERE parent_id = #{parentId}",
            "AND (",
            "file_name = #{baseName}",
            "OR file_name REGEXP CONCAT(",
            "'^',",
            "REGEXP_REPLACE(#{baseName}, '([\\\\$\\\\^\\\\*\\\\+\\\\?\\\\(\\\\)\\\\[\\\\]\\\\{\\\\}\\\\|\\\\\\\\\\\\.])', '\\\\\\\\$1'),",
            "'\\\\([0-9]+\\\\)$'",
            ")",
            ")"})
    @ResultMap("FileInfoMap")
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
    @ResultMap("FileInfoMap")
    FileInfo getRootFolderByClassId(@Param("classId") Long classId);

    FileInfo getFolderBySection(
        @Param("parentId") Long parentId,
        @Param("sectiondirId") Long sectiondirId
    );

    /**
     * 获取某个节点及其所有子节点（递归获取整个树）
     * SQL:
     * WITH RECURSIVE node_tree AS (
     * SELECT * FROM file_node WHERE id = #{folderId}
     * UNION ALL     *   SELECT f.* FROM file_node f
     * INNER JOIN node_tree t ON f.parent_id = t.id
     * )
     * SELECT * FROM node_tree
     * @param folderId 根节点ID
     * @return 节点及所有子节点列表
     */
    @Select({"WITH RECURSIVE node_tree AS (",
            "SELECT * FROM file_node WHERE id = #{folderId}",
            "UNION ALL",
            "SELECT f.* FROM file_node f",
            "INNER JOIN node_tree t ON f.parent_id = t.id",
            ")",
            "SELECT * FROM node_tree"})
    @ResultMap("FileInfoMap")
    List<FileInfo> getAllNodesUnder(@Param("folderId") Long folderId);


    /**
     * 插入一个新节点
     * @param node 文件节点对象
     */

    @Insert({"INSERT INTO file_node ( file_name, is_dir, parent_id, course_id, class_id,",
            "uploader_id, sectiondir_id, file_type, section_id,",
            "last_file_version, is_current_version, file_size,",
            "visibility, created_at, updated_at, file_url, file_version,",
            "object_name",
            ") VALUES (",
            "#{name}, #{isDir}, #{parentId}, #{courseId}, #{classId},",
            "#{uploaderId}, #{sectiondirId}, #{fileType}, #{sectionId},",
            "#{lastVersionId}, #{isCurrentVersion}, #{fileSize},",
            "#{visibility}, #{createdAt}, #{updatedAt}, #{url}, #{version},",
            "#{objectName}",
            ")"})
    void insertNode(FileInfo node);

    @Select("SELECT * FROM file_node WHERE id = #{id}")
    @ResultMap("FileInfoMap")
    FileInfo selectById(Long id);

    /**
     * 更新节点信息
     * SQL:
     * UPDATE file_node SET
     * file_name = #{name},
     * is_dir = #{isDir},
     * parent_id = #{parentId},
     * course_id = #{courseId},
     *class_id = #{classId},
     *  uploader_id = #{uploaderId},
     *  sectiondir_id = #{sectiondirId},
     *  file_type = #{fileType},
     * section_id = #{sectionId},
     * last_file_version = #{lastVersionId},
     * is_current_version = #{isCurrentVersion},
     * file_size = #{fileSize},
     * visibility = #{visibility},
     * created_at = #{createdAt},
     * updated_at = #{updatedAt},
     *file_url = #{url},
     *  file_version = #{version},
     *  object_name = #{objectName}
     *  WHERE id = #{id}
     *  @param node 文件节点对象
     */
    @Update({"UPDATE file_node SET",
            "file_name = #{name},",
            "is_dir = #{isDir},",
            "parent_id = #{parentId},",
            "course_id = #{courseId},",
            "class_id = #{classId},",
            "uploader_id = #{uploaderId},",
            "sectiondir_id = #{sectiondirId},",
            "file_type = #{fileType},",
            "section_id = #{sectionId},",
            "last_file_version = #{lastVersionId},",
            "is_current_version = #{isCurrentVersion},",
            "file_size = #{fileSize},",
            "visibility = #{visibility},",
            "created_at = #{createdAt},",
            "updated_at = #{updatedAt},",
            "file_url = #{url},",
            "file_version = #{version},",
            "object_name = #{objectName}",
            "WHERE id = #{id}"
    })
    void updateNode(FileInfo node);

    /**
     * 删除节点（物理删除）
     */
    void deleteNodeById(Long id);

    /**
     * 获取指定名称、父ID下最大编号小于当前版本的文件
     * SQL:
     * SELECT id FROM file_node     * WHERE parent_id = #{parentId}     *   AND (     *     file_name = #{baseName}     *     OR file_name LIKE CONCAT(#{baseName}, '(%)')     *   )     *   AND is_current_version = true     *   AND file_name != #{currentName}     * ORDER BY     *   CASE     *     WHEN file_name REGEXP '\\([0-9]+\\)$'     *     THEN CAST(REGEXP_SUBSTR(file_name, '[0-9]+') AS UNSIGNED)     *     ELSE 0     *   END DESC,     *   created_at DESC     * LIMIT 1     * @param parentId 父节点ID
     * @param baseName 基础文件名
     * @param currentName 当前文件名
     * @return 上一个版本的ID
     */
    @Select({"SELECT id FROM file_node",
            " WHERE parent_id = #{parentId}",
            " AND (",
            " file_name = #{baseName}",
            " OR file_name LIKE CONCAT(#{baseName}, '(%)')",
            " )",
            " AND is_current_version = true",
            " AND file_name != #{currentName}",
            " ORDER BY",
            " CASE",
            " WHEN file_name REGEXP '\\\\([0-9]+\\\\)$'",
            " THEN CAST(REGEXP_SUBSTR(file_name, '[0-9]+') AS UNSIGNED)",
            " ELSE 0",
            " END DESC,",
            " created_at DESC",
            " LIMIT 1"
    })
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

    @Select("SELECT is_dir FROM file_node WHERE id = #{id}")
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
    @Select({
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
    @ResultMap("FileInfoMap")
    List<FileInfo> getFilesByCourseId(
        @Param("courseId") Long courseId,
        @Param("title") String title,
        @Param("type") String type,
        @Param("chapter") Long chapter,
        @Param("regexTitle") String regexTitle
    );

    /**
     * 根据班级ID和章节获取文件
     * SQL:
     * SELECT * FROM file_node
     * WHERE class_id = #{classId}
     *   AND is_dir = false
     *   <if test="title = null or title = ''">
     *     AND is_current_version = true
     *   </if>
     *   <if test="title != null and title != ''">
     *     AND file_name REGEXP #{regexTitle}
     *   </if>
     *   <if test="type != null and type != ''">
     *     AND file_type = UPPER(#{type})
     *   </if>
     *   <if test="chapter != null and chapter != -1">
     *     AND section_id = #{chapter}
     *   </if>
     * @param classId 班级ID
     * @param title 文件名（可选，支持模糊）
     * @param type 文件类型（可选）
     * @param chapter 章节ID（可选）
     * @param regexTitle 文件名正则（可选）
     * @return 文件列表
     */
    @Select({
        "<script>",
        "SELECT * FROM file_node",
        "WHERE class_id = #{classId}",
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
    @ResultMap("FileInfoMap")
    List<FileInfo> getFilesByClassIdandChapter(
        @Param("classId") Long classId,
        @Param("title") String title,
        @Param("type") String type,
        @Param("chapter") Long chapter,
        @Param("regexTitle") String regexTitle
    );

    /**
     * 根据班级ID获取文件
     * SQL:
     * SELECT * FROM file_node
     * WHERE class_id = #{classId}
     *   AND is_dir = false
     *   <if test="title = null or title = ''">
     *     AND is_current_version = true
     *   </if>
     *   <if test="title != null and title != ''">
     *     AND file_name REGEXP #{regexTitle}
     *   </if>
     *   <if test="type != null and type != ''">
     *     AND file_type = UPPER(#{type})
     *   </if>
     * @param classId 班级ID
     * @param title 文件名（可选，支持模糊）
     * @param type 文件类型（可选）
     * @param regexTitle 文件名正则（可选）
     * @return 文件列表
     */
    @Select({
            "<script>",
            "SELECT * FROM file_node",
            "WHERE class_id = #{classId}",
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
            "</script>"
    })
    @ResultMap("FileInfoMap")
    List<FileInfo> getFilesByClassId(
        @Param("classId") Long classId,
        @Param("title") String title,
        @Param("type") String type,
        @Param("regexTitle") String regexTitle
    );
}
