package org.example.edusoft.common.storage;

import java.io.OutputStream;

import org.example.edusoft.common.domain.FileBo;
import org.example.edusoft.entity.file.FileType;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传接口
 *
 * @Author: sjy
 * @Date: 2025/5/15 9:43
 */
public interface IFileStorage {

    /**
     * 判断存储桶是否在存在
     *
     * @param bucket 桶名称
     * @return true 存在 false 不存在
     */
    boolean bucketExists(String bucket);

    /**
     * 创建存储桶
     *
     * @param bucket 桶名称
     */
    void makeBucket(String bucket);

    /**
     * 上传文件
     *
     * @param file
     * @return 文件基本信息
     */
    FileBo upload(MultipartFile file, String uniqueName, FileType type);

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    void delete(String objectName);

    /**
     * 下载文件
     *
     * @param objectName 对象名称
     * @param response
     */
    void download(String objectName, HttpServletResponse response);
    void download(String url, OutputStream outputStream);

    /**
     * 获取文件访问地址
     *
     * @param objectName 对象名称
     * @return
     */ 
    String getUrl(String objectName);
}
