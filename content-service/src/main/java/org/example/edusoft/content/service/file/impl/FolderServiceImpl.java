package org.example.edusoft.content.service.file.impl;

import org.example.edusoft.content.dto.file.FileResponseDTO;
import org.example.edusoft.content.service.file.FolderService;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class FolderServiceImpl implements FolderService {
    
    @Override
    public FileResponseDTO createFolder(String folderName, Long parentFolderId, Long courseId, Long creatorId) {
        // TODO: 实现文件夹创建逻辑
        FileResponseDTO folder = new FileResponseDTO();
        folder.setTitle(folderName);
        folder.setIsFolder(true);
        folder.setParentFolderId(parentFolderId);
        folder.setCourseId(courseId);
        return folder;
    }
    
    @Override
    public List<FileResponseDTO> getFolderContents(Long folderId) {
        // TODO: 实现获取文件夹内容的逻辑
        return new ArrayList<>();
    }
}
