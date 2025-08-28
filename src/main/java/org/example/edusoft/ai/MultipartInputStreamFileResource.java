package org.example.edusoft.ai;

import org.springframework.core.io.InputStreamResource;
import java.io.IOException;
import java.io.InputStream;

/**
 * 支持RestTemplate以流方式上传文件的工具类。
 */
public class MultipartInputStreamFileResource extends InputStreamResource {
    private final String filename;

    public MultipartInputStreamFileResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return this.filename;
    }

    @Override
    public long contentLength() throws IOException {
        return -1; // 不提前计算长度
    }
} 