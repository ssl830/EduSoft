package org.example.edusoft.service.file;

import jakarta.servlet.http.HttpServletResponse;

public interface FileDownloadService {
    /**
     * 下载文件或文件夹（如果是文件夹则打包成 ZIP）
     */
    void downloadFileOrFolder(Long fileId, HttpServletResponse response);
}