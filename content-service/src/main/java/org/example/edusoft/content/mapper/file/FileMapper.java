package org.example.edusoft.mapper.file;

import java.util.List; 

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.edusoft.entity.file.FileInfo;

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
     */
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
     */
    FileInfo getRootFolderByClassId(
        @Param("classId") Long classId
    );

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

    boolean existsByNameAndParent(@Param("name") String name, @Param("parentId") Long parentId);

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
