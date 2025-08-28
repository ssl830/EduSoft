package org.example.edusoft.service.file.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ResponseHeaderOverrides;

import org.example.edusoft.common.properties.FsServerProperties;
import org.example.edusoft.entity.file.FileAccessDTO;
import org.example.edusoft.entity.file.FileInfo;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.mapper.file.FileMapper;
import org.example.edusoft.service.file.FileAccessService;
import org.example.edusoft.utils.file.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
 
import java.net.URL;
import java.util.Date;
import java.net.URLEncoder;
import org.springframework.http.HttpMethod;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileAccessServiceImpl implements FileAccessService {

    private final FileMapper fileMapper;
    private final FsServerProperties fsServerProperties;

    @Override
    public FileAccessDTO getDownloadUrl(Long fileId) {
        FileInfo fileInfo = fileMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        if (fileInfo.getIsDir()) {
            throw new BusinessException("不支持获取文件夹的下载链接");
        }

        return getDownloadUrlByObjectName(fileInfo.getObjectName());
    }

    @Override
    public FileAccessDTO getPreviewUrl(Long fileId) {
        FileInfo fileInfo = fileMapper.selectById(fileId);
        if (fileInfo == null) {
            throw new BusinessException("文件不存在");
        }

        if (fileInfo.getIsDir()) {
            throw new BusinessException("不支持预览文件夹");
        }

        return getPreviewUrlByObjectName(fileInfo.getObjectName());
    }

    @Override
    public FileAccessDTO getDownloadUrlByObjectName(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new BusinessException("文件路径不能为空");
        }

        try {
            // 获取OSS配置
            FsServerProperties.AliyunOssProperties config = getOssConfig();
            
            // 创建OSS客户端
            OSS ossClient = createOssClient(config);

            try {
                // 设置URL过期时间为1小时
                Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
                
                // 构造带参数的预签名请求
                GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    config.getBucket(),
                    objectName
                );

                // 设置过期时间
                request.setExpiration(expiration);

                // 设置响应头：强制下载
                request.addQueryParameter("response-content-disposition", 
                    "attachment; filename=" + objectName);

                // 生成带签名的临时访问URL
                URL url = ossClient.generatePresignedUrl(request);

                return FileAccessDTO.builder()
                    .url(url.toString())
                    .fileName(getFileNameFromObjectName(objectName))
                    .expiresIn(3600)
                    .build();

            } finally {
                ossClient.shutdown();
            }
        } catch (Exception e) {
            log.error("生成下载签名URL失败: {}", e.getMessage());
            throw new BusinessException("生成下载链接失败：" + e.getMessage());
        }
    }

    @Override
    public FileAccessDTO getPreviewUrlByObjectName(String objectName) {
        if (objectName == null || objectName.trim().isEmpty()) {
            throw new BusinessException("文件路径不能为空");
        }

        try {
            // 获取OSS配置
            FsServerProperties.AliyunOssProperties config = getOssConfig();
            
            // 创建OSS客户端
            OSS ossClient = createOssClient(config);

            try {
                // 设置URL过期时间为1小时
                Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
                
                // 获取文件类型和MIME类型
                String suffix = FileUtil.getFileSuffix(objectName);
                String contentType = FileUtil.getContentType("." + suffix);
                String fileName = getFileNameFromObjectName(objectName);
                // 构造带参数的预签名请求
                GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    config.getBucket(),
                    objectName
                );

                request.setMethod(com.aliyun.oss.HttpMethod.GET);
                // 设置过期时间
                request.setExpiration(expiration);
                
                //request.addQueryParameter("response-content-type", contentType);
                //   让浏览器以内嵌方式显示，并带上文件名（UTF-8 编码防止乱码）
                request.addQueryParameter(
                    "response-content-disposition",
                    "inline;filename=" + URLEncoder.encode(fileName, "UTF-8")
                );

                // 生成带签名的临时访问URL
                URL url = ossClient.generatePresignedUrl(request);


                log.debug("Generated preview URL: {}", url.toString());

                return FileAccessDTO.builder()
                    .url(url.toString())
                    .fileName(fileName)
                    .fileType(suffix)
                    .expiresIn(3600)
                    .build();

            } finally {
                ossClient.shutdown();
            }
        } catch (Exception e) {
            log.error("生成预览签名URL失败: {}", e.getMessage());
            throw new BusinessException("生成预览链接失败：" + e.getMessage());
        }
    }

    /**
     * 获取OSS配置
     */
    private FsServerProperties.AliyunOssProperties getOssConfig() {
        FsServerProperties.AliyunOssProperties config = fsServerProperties.getAliyunOss();
        if (config == null) {
            throw new BusinessException("阿里云OSS配置未找到");
        }
        return config;
    }

    /**
     * 创建OSS客户端
     */
    private OSS createOssClient(FsServerProperties.AliyunOssProperties config) {
        return new OSSClientBuilder().build(
            config.getEndpoint(),
            config.getAccessKey(),
            config.getSecretKey()
        );
    }

    /**
     * 从对象名中提取文件名
     */
    private String getFileNameFromObjectName(String objectName) {
        int lastSlash = objectName.lastIndexOf('/');
        return lastSlash >= 0 ? objectName.substring(lastSlash + 1) : objectName;
    }
} 