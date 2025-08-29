package org.example.edusoft.service.file.impl;

import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FilePreviewService;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletOutputStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.example.edusoft.entity.file.FileInfo;
import java.net.URLEncoder;
import org.example.edusoft.utils.file.FileUtil;


@Service
@RequiredArgsConstructor
public class FilePreviewServiceImpl implements FilePreviewService {
    private final FileMapper fileMapper;
    private final IFileStorageProvider storageProvider;

    @Override
    public void previewFile(Long fileId, HttpServletResponse response) {
        try {
            FileInfo fileInfo = fileMapper.selectById(fileId);
            if (fileInfo == null || fileInfo.getIsDir()) {
                throw new IllegalArgumentException("仅支持预览单个文件");
            }

        // 获取文件类型（可以从扩展名判断）
        String fileName = fileInfo.getObjectName().toLowerCase();
        String contentType = FileUtil.getContentType("."+ FileUtil.getFileSuffix(fileName));
        System.out.println("文件名：" + fileName +"文件类型: " + contentType);
        response.setContentType(contentType);

        // 设置 disposition 为 inline，而不是 attachment（下载）
        String encodedName = URLEncoder.encode(fileInfo.getName(), StandardCharsets.UTF_8.toString());
        response.setHeader("Content-Disposition", "inline; filename=\"" + encodedName + "\"");

        IFileStorage storage = storageProvider.getStorage();
        storage.download(fileInfo.getObjectName(), response.getOutputStream());
    } catch (IOException e) {
        throw new RuntimeException("文件预览失败", e);
    }
    }
}
