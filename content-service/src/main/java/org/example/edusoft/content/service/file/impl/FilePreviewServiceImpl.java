package org.example.edusoft.content.service.file.impl;

import org.example.edusoft.content.service.file.FilePreviewService;
import org.springframework.stereotype.Service;

@Service
public class FilePreviewServiceImpl implements FilePreviewService {
    
    @Override
    public String getPreviewUrl(Long fileId) {
        // TODO: 实现文件预览URL生成逻辑
        return "preview_url_" + fileId;
    }
}
