package org.example.edusoft.service.file.impl;

import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FolderService;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.entity.file.FileType;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FolderServiceImpl implements FolderService {
    private final FileMapper fileMapper;
    /**
     * 查找指定文件夹下是否有对应 sectionId 的子文件夹
     */
    public FileInfo findSectionFolder(Long parentId, Long sectionId) {
        List<FileInfo> children = fileMapper.getChildren(parentId);
        for (FileInfo child : children) {
            if (child.getIsDir() && Objects.equals(child.getSectiondirId(), sectionId)) {
                return child;
            }
        }
        return null;
    }

    @Override
    public boolean createFolder(String name, Long sectiondirid, Long parentId, Long courseId, Long classId, Long uploaderId) {
        if (fileMapper.existsByNameAndParent(name, parentId)) {
            throw new RuntimeException("名称已存在");
        }

        FileInfo folder = new FileInfo();
        folder.setName(name);
        folder.setSectiondirId(sectiondirid);
        folder.setIsDir(true);
        folder.setParentId(parentId);
        folder.setCourseId(courseId);
        folder.setClassId(classId);
        folder.setUploaderId(uploaderId); 
        folder.setFileType(FileType.OTHER);
        folder.setSectionId(-1L);
        // 没有上一个版本，所以初始化为0
        folder.setLastVersionId(0L);
        folder.setIsCurrentVersion(true);  
        folder.setFileSize(0L);
        folder.setVisibility("CLASS_ONLY");
        folder.setCreatedAt(new Date());
        folder.setUpdatedAt(new Date());
        folder.setUrl(""); // 目录没有实际的URL
        folder.setVersion(0); 
        // 没有进行版本管理的文件的版本号都是0 
        folder.setObjectName("");
        fileMapper.insertNode(folder);
        return true;
    }
}
