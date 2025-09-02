package org.example.edusoft.content.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.content.entity.FileInfo;
import java.util.List;

@Mapper
public interface FileMapper {
    
    @Insert("INSERT INTO file_info (file_name, original_file_name, file_path, file_type, file_size, " +
            "uploader_id, uploader_name, description, category, visibility, object_name, file_url, " +
            "status, created_at, updated_at) " +
            "VALUES (#{fileName}, #{originalFileName}, #{filePath}, #{fileType}, #{fileSize}, " +
            "#{uploaderId}, #{uploaderName}, #{description}, #{category}, #{visibility}, " +
            "#{objectName}, #{fileUrl}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(FileInfo fileInfo);
    
    @Select("SELECT * FROM file_info WHERE id = #{id}")
    FileInfo findById(Long id);
    
    @Select("SELECT * FROM file_info WHERE uploader_id = #{uploaderId}")
    List<FileInfo> findByUploaderId(Long uploaderId);
    
    @Select("SELECT * FROM file_info WHERE category = #{category}")
    List<FileInfo> findByCategory(String category);
    
    @Select("SELECT * FROM file_info WHERE visibility = #{visibility}")
    List<FileInfo> findByVisibility(String visibility);
    
    @Select("SELECT * FROM file_info WHERE status = 'active'")
    List<FileInfo> findAllActive();
    
    @Delete("DELETE FROM file_info WHERE id = #{id}")
    int deleteById(Long id);
    
    @Update("UPDATE file_info SET status = 'deleted', updated_at = NOW() WHERE id = #{id}")
    int softDeleteById(Long id);
    
    @Update("UPDATE file_info SET description = #{description}, updated_at = NOW() WHERE id = #{id}")
    int updateDescription(@Param("id") Long id, @Param("description") String description);
    
    @Update("UPDATE file_info SET visibility = #{visibility}, updated_at = NOW() WHERE id = #{id}")
    int updateVisibility(@Param("id") Long id, @Param("visibility") String visibility);
}
