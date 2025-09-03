package org.example.edusoft.content.service.resource.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.content.client.AIServiceClient;
import org.example.edusoft.content.common.properties.FsServerProperties;
import org.example.edusoft.content.common.domain.FileBo;
import org.example.edusoft.content.common.storage.IFileStorageProvider;
import org.example.edusoft.content.common.storage.IFileStorage;
import org.example.edusoft.content.entity.resource.TeachingResource;
import org.example.edusoft.content.entity.resource.LearningProgress;
import org.example.edusoft.content.dto.resource.ResourceProgressDTO;
import org.example.edusoft.content.mapper.resource.TeachingResourceMapper;
import org.example.edusoft.content.mapper.progress.LearningProgressMapper;
import org.example.edusoft.content.service.resource.TeachingResourceService;
import org.example.edusoft.content.service.file.FileUpload;
import org.example.edusoft.content.exception.BusinessException;
import org.example.edusoft.content.entity.file.FileType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.example.edusoft.content.entity.file.FileInfo;
import org.example.edusoft.content.common.Result;
import org.example.edusoft.content.mapper.StudyRecordMapper;
import org.example.edusoft.content.entity.StudyRecord;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class TeachingResourceServiceImpl implements TeachingResourceService {
    
    @Autowired
    private TeachingResourceMapper teachingResourceMapper;
    
    @Autowired
    private FileUpload fileUploadService;

    @Autowired
    private IFileStorage fileStorage;

    @Autowired
    private IFileStorageProvider storageProvider;

    @Autowired
    private LearningProgressMapper progressMapper;

    @Autowired
    private FsServerProperties fsServerProperties;

    @Autowired
    private AIServiceClient aiServiceClient;

    @Autowired
    private StudyRecordMapper studyRecordMapper;

    @Override
    public TeachingResource createResource(TeachingResource resource) {
        if (resource == null) {
            throw new BusinessException("教学资源不能为空");
        }
        
        resource.setCreatedAt(LocalDateTime.now());
        resource.setUpdatedAt(LocalDateTime.now());
        
        teachingResourceMapper.insert(resource);
        return resource;
    }
    
    @Override
    public TeachingResource uploadResource(MultipartFile file, Long courseId, Long chapterId, String chapterName, String title, String description, Long createdBy) {
        // 保证 title 带扩展名
        String ext = "";
        String originalFilename = file.getOriginalFilename();
        String contentType = file.getContentType();
        if (title != null && !title.contains(".")) {
            if (originalFilename != null && originalFilename.contains(".")) {
                ext = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else if (contentType != null && contentType.contains("/")) {
                ext = "." + contentType.substring(contentType.lastIndexOf("/") + 1);
            }
            title = title + ext;
        }
        // 生成唯一文件名，使用 title（带扩展名）
        String uniqueName = UUID.randomUUID().toString() + "_" + title;
        // 如果 uniqueName 没有扩展名，补上
        if (!uniqueName.contains(".") && !ext.isEmpty()) {
            uniqueName = uniqueName + ext;
        }
        fileStorage = storageProvider.getStorage();
        System.out.println("uniqueName:");
        System.out.println(uniqueName);
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
        teachingResourceMapper.insert(resource);

//        // 异步生成视频摘要
//        try {
//            videoSummaryService.generateSummaryForResource(resource.getId());
//        } catch (Exception e) {
//            log.warn("自动生成视频摘要失败, resourceId: " + resource.getId(), e);
//            // 不影响视频上传的成功，仅记录警告日志
//        }

        return resource;
    }
    
    @Override
    public TeachingResource getResource(Long resourceId) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        return teachingResourceMapper.selectById(resourceId);
    }
    
    @Override
    public Map<Long, List<TeachingResource>> getResourcesByCourse(Long courseId) {
        if (courseId == null) {
            throw new BusinessException("课程ID不能为空");
        }
        
        List<TeachingResource> resources = teachingResourceMapper.selectByCourseId(courseId);
        return resources.stream()
                .collect(Collectors.groupingBy(TeachingResource::getChapterId));
    }
    
    @Override
    public List<TeachingResource> getResourcesByChapter(Long courseId, Long chapterId) {
        if (courseId == null) {
            throw new BusinessException("课程ID不能为空");
        }
        if (chapterId == null) {
            throw new BusinessException("章节ID不能为空");
        }
        
        return teachingResourceMapper.selectByChapter(courseId, chapterId);
    }
    
    @Override
    public boolean deleteResource(Long resourceId, Long operatorId) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        
        TeachingResource resource = teachingResourceMapper.selectById(resourceId);
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
            return teachingResourceMapper.deleteById(resourceId) > 0;
        } catch (Exception e) {
            log.error("删除资源失败: {}", e.getMessage());
            throw new BusinessException("删除资源失败：" + e.getMessage());
        }
    }
    
    @Override
    public LearningProgress updateProgress(Long resourceId, Long studentId, Integer progress, Integer position) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        // 检查资源是否存在
        TeachingResource resource = teachingResourceMapper.selectById(resourceId);
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
    
    @Override
    public LearningProgress getProgress(Long resourceId, Long studentId) {
        if (resourceId == null) {
            throw new BusinessException("资源ID不能为空");
        }
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        
        return teachingResourceMapper.selectByResourceAndStudent(resourceId, studentId);
    }
    
    @Override
    public String getSignedResourceUrl(Long resourceId) {
        TeachingResource resource = teachingResourceMapper.selectById(resourceId);
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
    
    @Override
    public List<ResourceProgressDTO> getCourseResourcesWithProgress(Long courseId, Long studentId, Long chapterId) {
        // 1. 获取资源列表
        List<TeachingResource> resources = teachingResourceMapper.selectByCourseAndChapter(courseId, chapterId);

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
    
    @Override
    public TeachingResource updateResourceDuration(Long resourceId, Integer duration) {
        TeachingResource resource = teachingResourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException("教学资源不存在");
        }

        resource.setDuration(duration);
        teachingResourceMapper.update(resource);

        return resource;
    }
    
    @Override
    public void syncToAIKnowledgeBase(MultipartFile file, Long resourceId) {
        try {
            // 获取课件关联的课程ID
            TeachingResource resource = teachingResourceMapper.selectById(resourceId);
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

    /**
     * 统计指定课程的资源总数
     * @param courseId 课程ID
     * @return 资源总数
     */
    public int countResourcesByCourseId(Long courseId) {
        return teachingResourceMapper.countByCourseId(courseId);
    }

    @Override
    public List<StudyRecord> getStudyRecordsByStudentId(Long studentId) {
        if (studentId == null) {
            throw new BusinessException("学生ID不能为空");
        }
        return studyRecordMapper.findStudyRecords(studentId);
    }
}
