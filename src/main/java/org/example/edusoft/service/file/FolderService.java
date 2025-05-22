package org.example.edusoft.service.file;

import org.example.edusoft.entity.file.FileInfo;

public interface FolderService {
    
    /**
     * 找到班级下章节文件夹
     */
    public FileInfo findSectionFolder(Long parentId, Long sectionId);
    /**
     * 创建文件夹
     */
    public boolean createFolder(String name, Long sectiondirid, Long parentId, Long courseId, Long classId, Long uploaderId);
}

