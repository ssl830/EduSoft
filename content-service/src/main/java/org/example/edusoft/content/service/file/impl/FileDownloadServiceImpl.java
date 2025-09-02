package org.example.edusoft.content.service.file.impl;

import org.example.edusoft.content.service.file.FileDownloadService;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class FileDownloadServiceImpl implements FileDownloadService {
    
    @Override
    public void downloadFile(Long fileId, HttpServletResponse response) {
        // TODO: 实现文件下载逻辑
    }
}
