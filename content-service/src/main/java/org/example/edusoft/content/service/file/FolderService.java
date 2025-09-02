package org.example.edusoft.content.service.file;

import org.example.edusoft.content.dto.file.FileResponseDTO;
import java.util.List;

public interface FolderService {
    
    /**
     * 创建文件夹
     */
    FileResponseDTO createFolder(String folderName, Long parentFolderId, Long courseId, Long creatorId);
    
    /**
     * 获取文件夹内容
     */
    List<FileResponseDTO> getFolderContents(Long folderId);
}
