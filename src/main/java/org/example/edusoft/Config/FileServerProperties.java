package org.example.edusoft.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "fs.files-server")
public class FileServerProperties {

    private String type;
    private Map<String, Object> local;
    private Map<String, String> qiniu;
    private Map<String, String> aliyunOss;
    private Map<String, Object> minio;
    private Map<String, String> preview;

    // Getters and Setters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getLocal() {
        return local;
    }

    public void setLocal(Map<String, Object> local) {
        this.local = local;
    }

    public Map<String, String> getQiniu() {
        return qiniu;
    }

    public void setQiniu(Map<String, String> qiniu) {
        this.qiniu = qiniu;
    }

    public Map<String, String> getAliyunOss() {
        return aliyunOss;
    }

    public void setAliyunOss(Map<String, String> aliyunOss) {
        this.aliyunOss = aliyunOss;
    }

    public Map<String, Object> getMinio() {
        return minio;
    }

    public void setMinio(Map<String, Object> minio) {
        this.minio = minio;
    }

    public Map<String, String> getPreview() {
        return preview;
    }

    public void setPreview(Map<String, String> preview) {
        this.preview = preview;
    }
}