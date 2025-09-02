package org.example.edusoft.content.service.file;

import jakarta.servlet.http.HttpServletResponse;

public interface FilePreviewService {
    void previewFile(Long fileId, HttpServletResponse response);
}

