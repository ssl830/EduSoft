package org.example.edusoft.service.file.impl;

import org.example.edusoft.common.dto.file.FileResponseDTO;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FileQueryService;
import org.example.edusoft.entity.file.FileInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        Long chapter
    ) {
        Long classId = fileMapper.getClassIdByUserandCourse(userId, courseId);
        FileInfo root = fileMapper.getRootFolderByClassId(classId);
        if (root == null) return Collections.emptyList();

        List<FileInfo> children = fileMapper.getChildrenWithFilter(root.getId(), title, type, chapter);
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


