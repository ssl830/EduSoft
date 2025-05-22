package org.example.edusoft.common.dto.file;
// 用于返回文件信息的DTO

import lombok.Data;
import java.util.Date;

@Data
public class FileResponseDTO {
    private Long id;
    private Long courseId;
    private Long sectionId;
    private Long uploaderId; 
    private String title;
    private String type;
    private String fileUrl;
    private String visibility;
    private int version; // 版本号：是怎么确定的，应该是string还是int？后端暂且先设定为int
    private Date createdAt;
}