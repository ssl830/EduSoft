package org.example.edusoft.service.file.impl;

import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FileDownloadService;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.utils.file.FileUtil;
import java.net.URLEncoder;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
                String fileName = URLEncoder.encode(fileInfo.getName(), StandardCharsets.UTF_8.toString());
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                storage.download(fileInfo.getObjectName(), response);
            } else {
                // 下载整个文件夹（打包为ZIP）
                List<FileInfo> files = fileMapper.getAllNodesUnder(fileId);
                //String zipName = fileInfo.getName() + ".zip";

                // 正确设置响应头
                response.reset();
                String zipName = URLEncoder.encode(fileInfo.getName() + ".zip", StandardCharsets.UTF_8.toString());
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/zip");
                response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + zipName);
                //response.setHeader("Content-Disposition", "attachment; filename=\"" + zipName + "\"");
                response.setHeader("Cache-Control", "no-store");
                response.setHeader("Pragma", "no-cache");
                
                try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                    for (FileInfo f : files) {
                        if (!f.getIsDir()) {
                            String relativePath = fileInfo.getName() + "/" + f.getName() + "." + FileUtil.getFileSuffix(f.getObjectName());
                            zos.putNextEntry(new ZipEntry(relativePath));
                            // 直接从存储中写入到 zos，避免内存中转（更高效）
                            storage.download(f.getObjectName(), zos);

                            zos.closeEntry();
                        }
                    }
                    zos.finish(); // 显式完成写入
                }
            }
        } catch (IOException e) {
            // 可记录日志
            throw new RuntimeException("文件下载失败", e);
        }
    }
}
