package org.example.edusoft.content.mapper;

import org.example.edusoft.content.entity.FileInfo;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface FileMapper {
    
    @Select("SELECT * FROM file_info WHERE id = #{id}")
    FileInfo findById(@Param("id") Long id);
    
    @Select("SELECT * FROM file_info WHERE uploader_id = #{uploaderId} AND status = 'active'")
    List<FileInfo> findByUploaderId(@Param("uploaderId") Long uploaderId);
    
    @Select("SELECT * FROM file_info WHERE category = #{category} AND status = 'active'")
    List<FileInfo> findByCategory(@Param("category") String category);
    
    @Select("SELECT * FROM file_info WHERE visibility = #{visibility} AND status = 'active'")
    List<FileInfo> findByVisibility(@Param("visibility") String visibility);
    
    @Select("SELECT * FROM file_info WHERE status = 'active' ORDER BY created_at DESC")
    List<FileInfo> findAllActive();
    
    @Insert("INSERT INTO file_info (file_name, original_file_name, file_path, file_type, file_size, " +
            "uploader_id, uploader_name, description, category, visibility, object_name, file_url, " +
            "status, created_at, updated_at) VALUES (#{fileName}, #{originalFileName}, #{filePath}, " +
            "#{fileType}, #{fileSize}, #{uploaderId}, #{uploaderName}, #{description}, #{category}, " +
            "#{visibility}, #{objectName}, #{fileUrl}, 'active', NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(FileInfo fileInfo);
    
    @Update("UPDATE file_info SET status = 'deleted', updated_at = NOW() WHERE id = #{id}")
    void deleteById(@Param("id") Long id);
    
    @Update("UPDATE file_info SET description = #{description}, updated_at = NOW() WHERE id = #{id}")
    void updateDescription(@Param("id") Long id, @Param("description") String description);
    
    @Update("UPDATE file_info SET visibility = #{visibility}, updated_at = NOW() WHERE id = #{id}")
    void updateVisibility(@Param("id") Long id, @Param("visibility") String visibility);
}
