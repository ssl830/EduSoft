package org.example.edusoft.mapper.file;

import java.util.List;
import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.example.edusoft.entity.file.FileInfo;

/**
 * @Date: 2025/5/15 11:30
 * @Description: 资源文件表mapper接口，定义数据库操作方法
 */
public interface FileMapper {

    
    /**
     * 查询某个父节点下的所有子节点（包括文件夹和文件）
     */
    List<FileInfo> getChildren(@Param("parentId") Long parentId);

    /**
     * 获取某个用户所在的所有班级对应的根文件夹
     */
    List<FileInfo> getRootFoldersByUserId(@Param("userId") Long userId);

    /**
     * 获取某个班级对应的根文件夹
     */
    FileInfo getRootFolderByClassId(
        @Param("classId") Long classId
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

    boolean existsByNameAndParent(@Param("name") String name, @Param("parentId") Long parentId);
    boolean isDir(Long id);

    Long getClassIdByUserandCourse(
        @Param("userId") Long userId,
        @Param("courseId") Long courseId
    );

    /* 
    List<FileInfo> selectAll();

    
    void insert(FileInfo fileinfo);

    void update(FileInfo fileinfo);

    void deleteById(Long id);


    List<FileInfo> selectByUserIdAndParentId(
        @Param("parentId") Long parentId
    );


   
    List<Long> selectClassIdsByUserId(@Param("userId") Long userId);

  
    Long selectClassIdByUserIdAndCourseId(@Param("userId") Long userId, @Param("courseId") Long courseId);
    
    List<FileInfo> selectByConditions(
        @Param("id") Long id,
        @Param("courseId") Long courseId,
        @Param("classId") Long classId,
        @Param("fileType") String fileType,
        @Param("uploaderId") Long uploaderId,
        @Param("sectionId") Long sectionId,
        @Param("fileSize") Long fileSize,
        @Param("uploadTime") Date uploadTime,
        @Param("fileVersion") Integer fileVersion,
        @Param("visibility") String visibility,
        @Param("fileLocation") String fileLocation,      // 文件归属（课程文件/章节文件等）
        @Param("fileName") String fileName,      // 模糊匹配文件名
        @Param("sortBy") String sortBy,          // 排序字段（name / upload_time）
        @Param("order") String order             // 排序方式（asc / desc）
    );*/
}
