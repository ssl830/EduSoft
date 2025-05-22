package org.example.edusoft.service.file.impl;

import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FileDownloadService;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.entity.file.FileInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import java.io.ByteArrayOutputStream;


@Service
@RequiredArgsConstructor
public class FileDownloadServiceImpl implements FileDownloadService {
    private final FileMapper fileMapper;
    private final IFileStorageProvider storageProvider;

    @Override
    public void downloadFileOrFolder(Long fileId, HttpServletResponse response) {
        try {
            FileInfo fileInfo = fileMapper.selectById(fileId);
            if (fileInfo == null) {
                throw new IllegalArgumentException("文件不存在");
            }

            IFileStorage storage = storageProvider.getStorage();

            
            if (!fileInfo.getIsDir()) {
                // 下载单个文件
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition", "attachment; filename=" + fileInfo.getName());
                storage.download(fileInfo.getObjectName(), response);
            } else { 
                // 下载整个文件夹（打包为ZIP）
                List<FileInfo> files = fileMapper.getAllNodesUnder(fileId);
                String zipName = fileInfo.getName() + ".zip";
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");

                try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                    for (FileInfo f : files) {
                        if (!f.getIsDir()) {
                            String relativePath = f.getUrl().replaceFirst("/" + fileId, "") + "/" + f.getName();
                            zos.putNextEntry(new ZipEntry(relativePath));

                            // 使用 ByteArrayOutputStream 缓冲下载内容
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            storage.download(f.getUrl(), baos); // 假设 IFileStorage 支持 OutputStream
                            byte[] bytes = baos.toByteArray();
                            zos.write(bytes, 0, bytes.length);

                            zos.closeEntry();
                        }
                    }
                }
            }
        } catch (IOException e) {
            // 可记录日志
            throw new RuntimeException("文件下载失败", e);
        }
    }
}
