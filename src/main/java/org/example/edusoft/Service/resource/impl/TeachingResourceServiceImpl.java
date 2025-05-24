package org.example.edusoft.service.resource.impl;

import org.example.edusoft.entity.resource.TeachingResource;
import org.example.edusoft.entity.resource.LearningProgress;
import org.example.edusoft.mapper.resource.TeachingResourceMapper;
import org.example.edusoft.mapper.resource.LearningProgressMapper;
import org.example.edusoft.service.resource.TeachingResourceService;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.common.storage.IFileStorageProvider;
import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.example.edusoft.common.properties.FsServerProperties;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Date;
import java.net.URL;

/**
 * 教学资源服务实现类
 */
@Slf4j
@Service
public class TeachingResourceServiceImpl implements TeachingResourceService {

    @Autowired
    private TeachingResourceMapper resourceMapper;

    @Autowired
    private LearningProgressMapper progressMapper;

    @Autowired
    private IFileStorage fileStorage;

    @Autowired
    private IFileStorageProvider storageProvider;

    @Autowired
    private FsServerProperties fsServerProperties;

    /**
     * 上传教学资源
     */
    @Override
    @Transactional
    public TeachingResource uploadResource(MultipartFile file, Long courseId, Long chapterId,
            String chapterName, String title, String description, Long createdBy) {
        // 生成唯一文件名
        String uniqueName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        fileStorage = storageProvider.getStorage();
        // 上传文件到存储系统
        FileBo fileBo = fileStorage.upload(file, uniqueName, FileType.VIDEO);
        
        // 创建资源记录
        TeachingResource resource = new TeachingResource();
        resource.setTitle(title);
        resource.setDescription(description);
        resource.setCourseId(courseId);
        resource.setChapterId(chapterId);
        resource.setChapterName(chapterName);
        resource.setResourceType("VIDEO");
        resource.setFileUrl(fileBo.getUrl());
        resource.setObjectName(fileBo.getFileName());
        resource.setCreatedBy(createdBy);
        resource.setCreatedAt(LocalDateTime.now());
        
        // 保存到数据库
        resourceMapper.insert(resource);
        
        return resource;
    }

    /**
     * 获取教学资源详情
     */
    @Override
    public TeachingResource getResource(Long resourceId) {
        return resourceMapper.selectById(resourceId);
    }

    /**
     * 获取课程的所有教学资源（按章节分组）
     */
    @Override
    public Map<Long, List<TeachingResource>> getResourcesByCourse(Long courseId) {
        List<TeachingResource> resources = resourceMapper.selectByCourseId(courseId);
        return resources.stream()
                .collect(Collectors.groupingBy(TeachingResource::getChapterId));
    }

    /**
     * 获取章节的教学资源
     */
    @Override
    public List<TeachingResource> getResourcesByChapter(Long courseId, Long chapterId) {
        return resourceMapper.selectByChapter(courseId, chapterId);
    }

    /**
     * 删除教学资源
     */
    @Override
    @Transactional
    public boolean deleteResource(Long resourceId, Long operatorId) {
        TeachingResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            return false;
        }
        
        try {
            // 获取文件存储实例
            fileStorage = storageProvider.getStorage();
            
            // 删除存储系统中的文件
            fileStorage.delete(resource.getObjectName());
            
            // 删除学习进度记录
            progressMapper.deleteByResourceId(resourceId);
            
            // 删除数据库记录
            return resourceMapper.deleteById(resourceId) > 0;
        } catch (Exception e) {
            log.error("删除资源失败: {}", e.getMessage());
            throw new BusinessException("删除资源失败：" + e.getMessage());
        }
    }

    /**
     * 更新学习进度
     */
    @Override
    @Transactional
    public LearningProgress updateProgress(Long resourceId, Long studentId, Integer progress, Integer position) {
        // 检查资源是否存在
        TeachingResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("教学资源不存在");
        }

        LearningProgress learningProgress = new LearningProgress();
        learningProgress.setResourceId(resourceId);
        learningProgress.setStudentId(studentId);
        learningProgress.setProgress(progress);
        learningProgress.setLastPosition(position);
        learningProgress.setLastWatchTime(LocalDateTime.now());
        
        progressMapper.insertOrUpdate(learningProgress);
        
        return progressMapper.selectByResourceAndStudent(resourceId, studentId);
    }

    /**
     * 获取学习进度
     */
    @Override
    public LearningProgress getProgress(Long resourceId, Long studentId) {
        return progressMapper.selectByResourceAndStudent(resourceId, studentId);
    }

    /**
     * 获取资源的基础访问URL
     */
    @Override
    public String getResourceUrl(Long resourceId) {
        TeachingResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            return null;
        }
        return fileStorage.getUrl(resource.getObjectName());
    }

    /**
     * 获取资源的临时访问URL（带签名，有效期1小时）
     */
    @Override
    public String getSignedResourceUrl(Long resourceId) {
        TeachingResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            return null;
        }

        try {
            // 获取OSS配置
            FsServerProperties.AliyunOssProperties config = fsServerProperties.getAliyunOss();
            if (config == null) {
                throw new BusinessException("阿里云OSS配置未找到");
            }

            // 创建OSS客户端
            OSS ossClient = new OSSClientBuilder().build(
                config.getEndpoint(),
                config.getAccessKey(),
                config.getSecretKey()
            );

            try {
                // 设置URL过期时间为1小时
                Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
                // 生成带签名的临时访问URL
                URL url = ossClient.generatePresignedUrl(
                    config.getBucket(),
                    resource.getObjectName(),
                    expiration
                );
                return url.toString();
            } finally {
                ossClient.shutdown();
            }
        } catch (Exception e) {
            log.error("生成签名URL失败: {}", e.getMessage());
            throw new BusinessException("生成访问链接失败：" + e.getMessage());
        }
    }
} 