package org.example.edusoft.Mapper.file;

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
     * 查询所有文件信息
     */
    List<FileInfo> selectAll();

    /**
     * 插入新文件记录
     */
    void insert(FileInfo fileinfo);

    /**
     * 更新文件信息
     */
    void update(FileInfo fileinfo);

    /**
     * 根据 ID 删除文件
     */
    void deleteById(Long id);

    /**
     * 根据父级ID查找文件
     * 此方法用于查询特定父级目录中的所有文件信息
     * 主要用于实现文件系统的个性化视图，让用户可以看到自己在特定文件夹中的所有文件
     *
     * @param userId   用户ID，用于限定查询的用户范围
     * @param parentId 父级目录ID，用于限定查询的目录范围
     * @return 返回一个包含多个FileInfo对象的列表，这些对象属于指定用户且位于指定的父级目录下
     */
    List<FileInfo> selectByUserIdAndParentId(
        @Param("parentId") Long parentId
    );


    /**
     * 多条件筛选 + 排序查询
     * 
     */
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
    );
}
