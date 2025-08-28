package org.example.edusoft.common.storage;

/**
 * 文件上传对象工厂
 *
 * @Author: sjy
 * @Date: 2025/5/15 
 */
public interface IFileStorageProvider {
    IFileStorage getStorage();
}
