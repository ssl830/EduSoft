package org.example.edusoft.common.storage.platform;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectResult;
import org.example.edusoft.common.constant.CommonConstant;
import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.exception.StorageConfigException;
import org.example.edusoft.common.properties.FsServerProperties;
import org.example.edusoft.common.storage.IFileStorage;
import org.example.edusoft.entity.file.FileType;
import org.example.edusoft.utils.file.ResponseUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.OutputStream;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * 阿里云oss文件上传
 *
 * @Author: sjy
 * @Date: 2025/5/17 10:32
 */
@Slf4j
@Component
public class AliyunOssStorage implements IFileStorage {

    private final OSS client;
    private final String endPoint;
    private final String bucket;

    public AliyunOssStorage(FsServerProperties fsServerProperties) {
        try {
            FsServerProperties.AliyunOssProperties config = fsServerProperties.getAliyunOss();
            if (config == null) {
                throw new StorageConfigException("阿里云OSS配置未找到");
            }
            String accessKey = config.getAccessKey();
            String secretKey = config.getSecretKey();
            String endPoint = config.getEndpoint();
            String bucket = config.getBucket();
            this.client = new OSSClientBuilder().build(endPoint, accessKey, secretKey);
            this.endPoint = endPoint;
            this.bucket = bucket;
        } catch (Exception e) {
            log.error("[AliyunOSS] OSSClient build failed: {}", e.getMessage());
            throw new StorageConfigException("请检查阿里云OSS配置是否正确");
        }
    } 

    @Override
    public boolean bucketExists(String bucket) {
        try {
            boolean exists = client.doesBucketExist(bucket);
            System.out.println(exists);
            return exists;
        } catch (Exception e) {
            log.error("[AliyunOSS] bucketExists Exception:{}", e.getMessage());
        } finally {
            client.shutdown();
        }
        return false;
    }

    @Override
    public void makeBucket(String bucket) {
        try {
            if (!bucketExists(bucket)) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucket);
                // 设置存储空间读写权限为公共读，默认为私有。
                //createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                client.createBucket(createBucketRequest);
                log.info("[AliyunOSS] makeBucket success bucketName:{}", bucket);
            }
        } catch (Exception e) {
            log.error("[AliyunOSS] makeBucket Exception:{}", e.getMessage());
            throw new BusinessException("创建存储桶失败");
        } finally {
            client.shutdown();
        }
    }

    @Override
    public FileBo upload(MultipartFile file, String uniqueName, FileType type) {
        //需要开启对应ACL权限
        //makeBucket(bucket);
        try {
            FileBo fileBo = FileBo.build(file, uniqueName, type); 
            // 创建 PutObjectRequest 并设置 ACL 权限为公共读
            PutObjectResult result = client.putObject(bucket, fileBo.getFileName(), file.getInputStream());
            if (result == null) {
                throw new BusinessException("文件上传失败");
            }
            // 设置 ACL 权限为公共读，这样可以预览
            // client.setObjectAcl(bucket, fileBo.getFileName(), CannedAccessControlList.PublicRead);
            // 获取文件访问地址，用OSS库中独一无二的文件名来构建这个url，因此url是文件在对象库中的唯一标识
            // 存入mysql数据库中 
            String url = getUrl(fileBo.getFileName());
            fileBo.setUrl(url);
            return fileBo;
        } catch (Exception e) {
            e.printStackTrace(); // 重要：先打印异常
            log.error("OSS 上传失败，原因：" + e.getMessage());
            log.error("[AliyunOSS] file upload failed: {}", e.getMessage());
            throw new BusinessException("文件上传失败");
        } finally {
            client.shutdown();
        }
    }

    @Override
    public void delete(String objectName) {
        if (StringUtils.isEmpty(objectName)) {
            throw new BusinessException("文件删除失败");
        }
        try {
            client.deleteObject(bucket, objectName);
            log.info("[AliyunOSS] file delete success, object:{}", objectName);
        } catch (Exception e) {
            log.error("[AliyunOSS] file delete failed: {}", e.getMessage());
            throw new BusinessException("文件删除失败");
        } finally {
            client.shutdown();
        }
    } 

    @SneakyThrows
    @Override
    public void download(String objectName, OutputStream outputStream) {
        if (StringUtils.isEmpty(objectName)) {
            throw new BusinessException("文件下载失败，文件对象为空");
        }
        try (OSSObject ossObject = client.getObject(bucket, objectName)) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = ossObject.getObjectContent().read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            log.info("[AliyunOSS] 文件下载成功, object:{}", objectName);
        } catch (Exception e) {
            log.error("[AliyunOSS] 文件下载失败: {}", e.getMessage());
            throw new BusinessException("文件下载失败");
        } 
    }
    
    public void download(String objectName, HttpServletResponse response) {
        if (StringUtils.isEmpty(objectName)) {
            throw new BusinessException("文件下载失败, 文件对象为空");
        }
        try (OSSObject ossObject = client.getObject(bucket, objectName)) {
            ResponseUtil.write(ossObject.getObjectContent(), objectName, response);
            log.info("[Minio] file download success, object:{}", objectName);
        } catch (Exception e) {
            log.error("[Minio] file download failed: {}", e.getMessage());
            throw new BusinessException("文件下载失败");
        } 
    }
    
    @Override
    public String getUrl(String objectName) {
        return "https://" + bucket + CommonConstant.SUFFIX_SPLIT + endPoint + CommonConstant.DIR_SPLIT + objectName;
    }
}

