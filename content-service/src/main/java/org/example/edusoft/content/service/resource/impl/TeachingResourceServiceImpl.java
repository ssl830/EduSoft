package org.example.edusoft.service.resource.impl;

import org.example.edusoft.entity.resource.TeachingResource;
import org.example.edusoft.entity.resource.LearningProgress;
import org.example.edusoft.entity.resource.ResourceProgressDTO;
import org.example.edusoft.mapper.resource.TeachingResourceMapper;
import org.example.edusoft.mapper.resource.LearningProgressMapper;
import org.example.edusoft.service.resource.TeachingResourceService;
import org.example.edusoft.service.resource.VideoSummaryService;
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

import com.aliyun.oss.model.GeneratePresignedUrlRequest;
//import com.aliyun.oss.common.comm.HttpMethod;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Date;
import java.net.URL;
import org.example.edusoft.ai.AIServiceClient;

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

    @Autowired
    private AIServiceClient aiServiceClient;

    @Autowired
    private VideoSummaryService videoSummaryService;

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
        
        // 异步生成视频摘要
        try {
            videoSummaryService.generateSummaryForResource(resource.getId());
        } catch (Exception e) {
            log.warn("自动生成视频摘要失败, resourceId: " + resource.getId(), e);
            // 不影响视频上传的成功，仅记录警告日志
        }
        
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
            //log.info("AccessKey: {}", config.getAccessKey());
            //log.info("SecretKey: {}", config.getSecretKey());
            //log.info("Endpoint: {}", config.getEndpoint());
            //log.info("Bucket: {}", config.getBucket());
            try {
                // 设置URL过期时间为1小时
                Date expiration = new Date(System.currentTimeMillis() + 3600 * 1000);
                
                // 构造带参数的预签名请求
                GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                    config.getBucket(),
                    resource.getObjectName()
                    //HttpMethod.GET
                );

                // 设置过期时间
                request.setExpiration(expiration);

                // 设置响应头：使视频在浏览器中内嵌播放，而不是下载
                request.addQueryParameter("response-content-type", "video/mp4");
                request.addQueryParameter("response-content-disposition", "inline");

                // 生成带签名的临时访问URL
                URL url = ossClient.generatePresignedUrl(
                    config.getBucket(),
                    resource.getObjectName(),
                    expiration
                );
                //log.info("OSS 文件路径: {}", resource.getObjectName());
                return url.toString();
            } finally {
                ossClient.shutdown();
            }
        } catch (Exception e) {
            log.error("生成签名URL失败: {}", e.getMessage());
            throw new BusinessException("生成访问链接失败：" + e.getMessage());
        }
    }

    /** 
     * 获取课程资源及学习进度信息
     */
    @Override
    public List<ResourceProgressDTO> getCourseResourcesWithProgress(Long courseId, Long studentId, Long chapterId) {
        // 1. 获取资源列表
        List<TeachingResource> resources = resourceMapper.selectByCourseAndChapter(courseId, chapterId);
        
        // 2. 转换为DTO并填充学习进度信息
        return resources.stream().map(resource -> {
            ResourceProgressDTO dto = new ResourceProgressDTO();
            
            // 设置资源信息
            dto.setResourceId(resource.getId());
            dto.setTitle(resource.getTitle());
            dto.setDescription(resource.getDescription());
            dto.setCourseId(resource.getCourseId());
            dto.setChapterId(resource.getChapterId());
            dto.setChapterName(resource.getChapterName());
            dto.setDuration(resource.getDuration());
            
            // 获取并设置学习进度信息
            LearningProgress progress = progressMapper.selectByResourceAndStudent(resource.getId(), studentId);
            if (progress != null) {
                dto.setLearningrecordId(progress.getId());
                dto.setStudentId(progress.getStudentId());
                dto.setProgress(progress.getProgress());
                dto.setLastPosition(progress.getLastPosition());
                dto.setWatchCount(progress.getWatchCount());
                dto.setLastWatchTime(progress.getLastWatchTime() != null ? 
                    progress.getLastWatchTime().toString() : null);
                dto.setLastWatch(progress.getLastWatchTime() != null ? 
                    progress.getLastWatchTime().toString() : null);
                dto.setCreatedAt(progress.getCreatedAt() != null ? 
                    progress.getCreatedAt().toString() : null);
                dto.setUpdatedAt(progress.getUpdatedAt() != null ? 
                    progress.getUpdatedAt().toString() : null);
                dto.setVersion(progress.getVersion());
            }
            
            // 生成带签名的访问URL
            try {
                String signedUrl = this.getSignedResourceUrl(resource.getId());
                dto.setFileUrl(signedUrl);
            } catch (Exception e) {
                log.error("生成签名URL失败: resourceId={}, error={}", resource.getId(), e.getMessage());
                dto.setFileUrl(resource.getFileUrl()); // 如果生成签名URL失败，使用原始URL
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 更新资源时长
     */
    @Override
    @Transactional
    public TeachingResource updateResourceDuration(Long resourceId, Integer duration) {
        TeachingResource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("教学资源不存在");
        }

        resource.setDuration(duration);
        resourceMapper.update(resource);
        
        return resource;
    }

    /**
     * 课件上传后自动同步AI知识库（AI微服务）
     */
    @Override
    public void syncToAIKnowledgeBase(MultipartFile file, Long resourceId) {
        try {
            // 获取课件关联的课程ID
            TeachingResource resource = resourceMapper.selectById(resourceId);
            if (resource == null) {
                log.error("同步AI知识库失败: 资源ID {} 不存在", resourceId);
                return;
            }
            
            // 调用AI微服务，将课件文件上传并入库（传递课程ID以支持联合知识库）
            String courseIdStr = resource.getCourseId() != null ? String.valueOf(resource.getCourseId()) : null;
            String result = aiServiceClient.uploadMaterial(file, courseIdStr);
            log.info("AI知识库同步结果: {}", result);
        } catch (Exception e) {
            log.error("同步AI知识库失败: {}", e.getMessage());
        }
    }
} 