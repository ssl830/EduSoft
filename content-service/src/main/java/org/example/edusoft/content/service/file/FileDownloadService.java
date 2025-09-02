package org.example.edusoft.content.service.file;

import jakarta.servlet.http.HttpServletResponse;

public interface FileDownloadService {
    
    /**
     * 下载文件
     */
    void downloadFile(Long fileId, HttpServletResponse response);
}
