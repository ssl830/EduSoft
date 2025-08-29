package org.example.edusoft.service.resource.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.example.edusoft.dto.resource.VideoSummaryDetailDTO;
import org.example.edusoft.entity.resource.*;
import org.example.edusoft.mapper.resource.*;
import org.example.edusoft.service.resource.VideoSummaryService;
import org.example.edusoft.common.properties.FsServerProperties;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;

/**
 * 视频摘要服务实现
 */
@Slf4j
@Service
public class VideoSummaryServiceImpl implements VideoSummaryService {

    @Autowired
    private VideoSummaryMapper videoSummaryMapper;

    @Autowired
    private VideoSummaryStageMapper stageMapper;

    @Autowired
    private VideoSummaryKeypointMapper keypointMapper;

    @Autowired
    private VideoSummaryTimestampMapper timestampMapper;

    @Autowired
    private TeachingResourceMapper teachingResourceMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FsServerProperties fsServerProperties;

    @Value("${ai.service.url:http://localhost:8000}")
    private String aiServiceUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public VideoSummaryDetailDTO generateSummaryForResource(Long resourceId) {
        try {
            // 1. 获取教学资源信息
            TeachingResource resource = teachingResourceMapper.selectById(resourceId);
            if (resource == null) {
                throw new RuntimeException("教学资源不存在: " + resourceId);
            }

            // 2. 检查是否为视频资源
            if (!"VIDEO".equalsIgnoreCase(resource.getResourceType())) {
                throw new RuntimeException("资源不是视频类型: " + resourceId);
            }

            // 3. 检查是否已存在摘要，如果存在则先删除
            VideoSummary existingSummary = videoSummaryMapper.selectByResourceId(resourceId);
            if (existingSummary != null) {
                deleteSummary(existingSummary.getId());
            }

            // 4. 下载视频文件到临时目录
            String tempVideoPath = downloadVideoToTemp(resource.getFileUrl());
            
            try {
                // 5. 调用AI服务生成摘要
                JsonNode summaryJson = callAiServiceForSummary(tempVideoPath, resource.getTitle(), "");

                // 6. 保存摘要到数据库
                VideoSummary videoSummary = createVideoSummary(resourceId, resource, summaryJson);
                videoSummaryMapper.insert(videoSummary);

                // 7. 保存阶段信息
                saveStages(videoSummary.getId(), summaryJson.get("stages"));

                // 8. 保存知识点信息
                saveKeyPoints(videoSummary.getId(), summaryJson.get("keyPoints"));

                // 9. 保存重要时间点信息
                saveKeyTimestamps(videoSummary.getId(), summaryJson.get("keyTimestamps"));

                // 10. 返回完整的摘要信息
                return getSummaryById(videoSummary.getId());

            } finally {
                // 清理临时文件
                try {
                    Files.deleteIfExists(Paths.get(tempVideoPath));
                } catch (Exception e) {
                    log.warn("清理临时文件失败: " + tempVideoPath, e);
                }
            }

        } catch (Exception e) {
            log.error("生成视频摘要失败, resourceId: " + resourceId, e);
            throw new RuntimeException("生成视频摘要失败: " + e.getMessage(), e);
        }
    }

    @Override
    public VideoSummaryDetailDTO getSummaryByResourceId(Long resourceId) {
        VideoSummary videoSummary = videoSummaryMapper.selectByResourceId(resourceId);
        if (videoSummary == null) {
            return null;
        }
        return buildSummaryDetailDTO(videoSummary);
    }

    @Override
    public VideoSummaryDetailDTO getSummaryById(Long summaryId) {
        VideoSummary videoSummary = videoSummaryMapper.selectById(summaryId);
        if (videoSummary == null) {
            return null;
        }
        return buildSummaryDetailDTO(videoSummary);
    }

    @Override
    @Transactional
    public boolean deleteSummary(Long summaryId) {
        try {
            // 删除关联的阶段、知识点、时间点信息
            stageMapper.deleteBySummaryId(summaryId);
            keypointMapper.deleteBySummaryId(summaryId);
            timestampMapper.deleteBySummaryId(summaryId);
            
            // 删除摘要记录
            int result = videoSummaryMapper.delete(summaryId);
            return result > 0;
        } catch (Exception e) {
            log.error("删除视频摘要失败, summaryId: " + summaryId, e);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean deleteSummaryByResourceId(Long resourceId) {
        VideoSummary videoSummary = videoSummaryMapper.selectByResourceId(resourceId);
        if (videoSummary != null) {
            return deleteSummary(videoSummary.getId());
        }
        return true;
    }

    @Override
    @Transactional
    public VideoSummaryDetailDTO regenerateSummary(Long resourceId) {
        // 先删除现有摘要
        deleteSummaryByResourceId(resourceId);
        // 重新生成
        return generateSummaryForResource(resourceId);
    }

    /**
     * 获取OSS资源的签名URL
     */
    private String getSignedUrl(String objectName) throws Exception {
        FsServerProperties.AliyunOssProperties config = fsServerProperties.getAliyunOss();
        if (config == null) {
            throw new RuntimeException("阿里云OSS配置未找到");
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
            
            // 构造带参数的预签名请求
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
                config.getBucket(),
                objectName
            );

            // 设置过期时间
            request.setExpiration(expiration);

            // 生成带签名的临时访问URL
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }

    /**
     * 下载视频文件到临时目录
     */
    private String downloadVideoToTemp(String fileUrl) throws Exception {
        String downloadUrl = fileUrl;
        
        // 如果是OSS URL，需要获取签名URL
        if (fileUrl.contains("aliyuncs.com")) {
            // 从URL中提取object name
            String objectName = extractObjectNameFromUrl(fileUrl);
            if (objectName != null) {
                downloadUrl = getSignedUrl(objectName);
                log.info("使用签名URL下载视频: {}", downloadUrl);
            }
        }
        
        // 创建临时文件
        String fileName = "video_" + System.currentTimeMillis() + ".mp4";
        String tempPath = System.getProperty("java.io.tmpdir") + File.separator + fileName;
        
        // 下载文件
        URI uri = URI.create(downloadUrl);
        Files.copy(uri.toURL().openStream(), Paths.get(tempPath), StandardCopyOption.REPLACE_EXISTING);
        
        return tempPath;
    }

    /**
     * 从OSS URL中提取对象名称
     */
    private String extractObjectNameFromUrl(String url) {
        try {
            // OSS URL格式: https://bucket.oss-cn-beijing.aliyuncs.com/objectname
            // 或: https://edusoft-file.oss-cn-beijing.aliyuncs.com/4bf05a31bb504e6e8ac01e70eae3291d.mp4
            URI uri = URI.create(url);
            String path = uri.getPath();
            // 移除开头的"/"
            return path.startsWith("/") ? path.substring(1) : path;
        } catch (Exception e) {
            log.error("提取对象名称失败: " + url, e);
            return null;
        }
    }

    /**
     * 调用AI服务生成视频摘要
     */
    private JsonNode callAiServiceForSummary(String videoPath, String videoTitle, String courseName) throws Exception {
        String url = aiServiceUrl + "/video/summary";
        
        // 准备多部分表单数据
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new org.springframework.core.io.FileSystemResource(videoPath));
        body.add("video_title", videoTitle);
        body.add("course_name", courseName);
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);
        
        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("AI服务调用失败: " + response.getStatusCode());
        }
        
        return objectMapper.readTree(response.getBody());
    }

    /**
     * 创建视频摘要对象
     */
    private VideoSummary createVideoSummary(Long resourceId, TeachingResource resource, JsonNode summaryJson) {
        VideoSummary videoSummary = new VideoSummary();
        videoSummary.setResourceId(resourceId);
        videoSummary.setVideoTitle(resource.getTitle());
        videoSummary.setCourseName(""); // 可以从resource或其他地方获取课程名称
        videoSummary.setSummary(summaryJson.has("summary") ? summaryJson.get("summary").asText() : "");
        videoSummary.setTranscript(summaryJson.has("transcript") ? summaryJson.get("transcript").asText() : "");
        videoSummary.setDuration(summaryJson.has("duration") ? summaryJson.get("duration").asInt() : resource.getDuration());
        return videoSummary;
    }

    /**
     * 保存阶段信息
     */
    private void saveStages(Long summaryId, JsonNode stagesJson) {
        if (stagesJson == null || !stagesJson.isArray()) {
            return;
        }
        
        int sortOrder = 1;
        for (JsonNode stageJson : stagesJson) {
            VideoSummaryStage stage = new VideoSummaryStage();
            stage.setSummaryId(summaryId);
            stage.setTitle(stageJson.has("title") ? stageJson.get("title").asText() : "");
            stage.setStartTime(stageJson.has("start_time") ? stageJson.get("start_time").asInt() : 0);
            stage.setEndTime(stageJson.has("end_time") ? stageJson.get("end_time").asInt() : 0);
            stage.setDescription(stageJson.has("description") ? stageJson.get("description").asText() : "");
            stage.setSortOrder(sortOrder++);
            
            stageMapper.insert(stage);
        }
    }

    /**
     * 保存知识点信息
     */
    private void saveKeyPoints(Long summaryId, JsonNode keyPointsJson) {
        if (keyPointsJson == null || !keyPointsJson.isArray()) {
            return;
        }
        
        for (JsonNode pointJson : keyPointsJson) {
            VideoSummaryKeypoint keypoint = new VideoSummaryKeypoint();
            keypoint.setSummaryId(summaryId);
            keypoint.setPoint(pointJson.has("point") ? pointJson.get("point").asText() : "");
            keypoint.setTimestamp(pointJson.has("timestamp") ? pointJson.get("timestamp").asInt() : 0);
            keypoint.setImportance(pointJson.has("importance") ? pointJson.get("importance").asText() : "medium");
            
            keypointMapper.insert(keypoint);
        }
    }

    /**
     * 保存重要时间点信息
     */
    private void saveKeyTimestamps(Long summaryId, JsonNode timestampsJson) {
        if (timestampsJson == null || !timestampsJson.isArray()) {
            return;
        }
        
        for (JsonNode timestampJson : timestampsJson) {
            VideoSummaryTimestamp timestamp = new VideoSummaryTimestamp();
            timestamp.setSummaryId(summaryId);
            timestamp.setTime(timestampJson.has("time") ? timestampJson.get("time").asInt() : 0);
            timestamp.setEvent(timestampJson.has("event") ? timestampJson.get("event").asText() : "");
            timestamp.setType(timestampJson.has("type") ? timestampJson.get("type").asText() : "concept");
            
            timestampMapper.insert(timestamp);
        }
    }

    /**
     * 构建摘要详情DTO
     */
    private VideoSummaryDetailDTO buildSummaryDetailDTO(VideoSummary videoSummary) {
        VideoSummaryDetailDTO dto = new VideoSummaryDetailDTO();
        BeanUtils.copyProperties(videoSummary, dto);
        
        // 获取关联的阶段、知识点、时间点信息
        dto.setStages(stageMapper.selectBySummaryId(videoSummary.getId()));
        dto.setKeyPoints(keypointMapper.selectBySummaryId(videoSummary.getId()));
        dto.setKeyTimestamps(timestampMapper.selectBySummaryId(videoSummary.getId()));
        
        return dto;
    }
}
