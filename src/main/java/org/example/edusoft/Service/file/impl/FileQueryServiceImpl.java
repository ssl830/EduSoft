package org.example.edusoft.service.file.impl;

import org.example.edusoft.common.dto.file.FileResponseDTO;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FileQueryService;
import org.example.edusoft.utils.file.FileUtil;
import org.example.edusoft.entity.file.FileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FileQueryServiceImpl implements FileQueryService {

    private final FileMapper fileMapper;

    private FileResponseDTO convertToResponseDTO(FileInfo fileInfo) {
        FileResponseDTO dto = new FileResponseDTO();
        dto.setId(fileInfo.getId());
        dto.setCourseId(fileInfo.getCourseId());
        dto.setSectionId(fileInfo.getSectionId());
        dto.setUploaderId(fileInfo.getUploaderId()); 
        dto.setTitle(fileInfo.getName()); 
        dto.setType(fileInfo.getFileType() != null ? fileInfo.getFileType().name() : null); // 枚举转字符串
        dto.setFileUrl(fileInfo.getUrl()); 
        dto.setVisibility(fileInfo.getVisibility());
        dto.setVersion(fileInfo.getVersion()); // 示例版本号格式  1
        dto.setCreatedAt(fileInfo.getCreatedAt());
        return dto;
    }

    // 1. 获取用户所有文件，以课程班级文件夹的形式展出 done
    @Override
    public List<FileResponseDTO> getAllFilesByUserId(Long userId) {
        //return fileMapper.getRootFoldersByUserId(userId);
        List<FileInfo> fileInfoList = fileMapper.getRootFoldersByUserId(userId);
        return fileInfoList.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
 
    // 2. 获取用户在某课程下的文件 
    @Override 
    public List<FileResponseDTO> getFilesByUserandCourse(Long userId, Long courseId) {
        Long classId = fileMapper.getClassIdByUserandCourse(userId, courseId);
        FileInfo root = fileMapper.getRootFolderByClassId(classId);
        if (root == null) return Collections.emptyList();
        return fileMapper.getChildren(root.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }


    @Override
    public List<FileResponseDTO> getFilesByUserandCourseWithFilter(
        Long userId,
        Long courseId,
        String title,
        String type, 
        Long chapter,
        Boolean isTeacher
    ) {
        System.out.println("Input title: " + title);
        
        // 获取基础文件名（去掉最后的版本号）
        String baseName = FileUtil.getFileBaseName(title);
        System.out.println("Base name: " + baseName);
        
        // 构建用于匹配所有版本的正则表达式
        String regexTitle = "";
        if (baseName == null || baseName.trim().isEmpty()) {
            regexTitle = ".*"; // 匹配所有文件
        } else {
            // 转义特殊字符
            String escapedBaseName = baseName.replaceAll(
                "([\\$\\^\\*\\+\\?\\(\\)\\[\\]\\{\\}\\|\\\\\\.])",
                "\\\\$1"
            );
            // 匹配基础名称完全相同的文件，可能后面跟着(数字)
            regexTitle = "^" + escapedBaseName + "(\\([0-9]+\\))?$";
        }
        System.out.println("Regex pattern: " + regexTitle);

        List<FileInfo> children;
        if (isTeacher) {
            // 如果是老师，直接获取课程下的所有文件
            children = fileMapper.getFilesByCourseId(courseId, baseName, type, chapter, regexTitle);
        } else {
            // 如果是学生，只获取自己所在班级的文件
            Long classId = fileMapper.getClassIdByUserandCourse(userId, courseId);
            FileInfo root = fileMapper.getRootFolderByClassId(classId);
            if (root == null) return Collections.emptyList();
            
            if (chapter != null) {
                children = fileMapper.getFilesByClassIdandChapter(classId, baseName, type, chapter, regexTitle);
            } else {
                children = fileMapper.getFilesByClassId(classId, baseName, type, regexTitle);
            }
        }

        return children.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // 3. 获取某个文件夹下的内容
    @Override
    public List<FileResponseDTO> getChildrenFiles(Long folderId) {
        FileInfo folder = fileMapper.selectById(folderId);
        if (folder == null || !folder.getIsDir()) {
            throw new IllegalArgumentException("无效的文件夹ID");
        }
        return fileMapper.getChildren(folderId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    // 4. 获取文件的版本列表
    @Override
    public List<FileResponseDTO> getFileVersions(Long userId, Long courseId, String baseName) {
        Long classId = fileMapper.getClassIdByUserandCourse(userId, courseId);
        FileInfo root = fileMapper.getRootFolderByClassId(classId);
        if (root == null) return Collections.emptyList();
        return fileMapper.getVersionsByBaseName(baseName, root.getId()).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
}


