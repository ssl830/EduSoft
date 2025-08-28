package org.example.edusoft.service.file;

import org.example.edusoft.common.dto.file.FileResponseDTO;
import java.util.List;

public interface FileQueryService {
    /**
     * 获取用户所有可见的文件（按班级划分）
     */
    List<FileResponseDTO> getAllFilesByUserId(Long userId);

    /**
     * 获取用户在某个课程下的文件
     */
    List<FileResponseDTO> getFilesByUserandCourse(Long userId, Long courseId);


    List<FileResponseDTO> getFilesByUserandCourseWithFilter(
        Long userId,
        Long courseId,
        String title,
        String type,
        Long chapter,
        Boolean isTeacher
    );
    /**
     * 获取某个文件夹下的内容
     */
    List<FileResponseDTO>  getChildrenFiles(Long folderId);

    public List<FileResponseDTO> getFileVersions(Long userId, Long courseId, String baseName);
}


