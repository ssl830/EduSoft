package org.example.edusoft.content.service.file;

import org.example.edusoft.content.dto.file.FileResponseDTO;
import java.util.List;

public interface FileQueryService {
    
    /**
     * 获取用户的所有文件
     */
    List<FileResponseDTO> getAllFilesByUserId(Long userId);
    
    /**
     * 根据用户和课程获取文件列表（支持过滤）
     */
    List<FileResponseDTO> getFilesByUserandCourseWithFilter(
        Long userId, 
        Long courseId, 
        String title, 
        String type, 
        Long chapter, 
        Boolean isTeacher
    );
}
