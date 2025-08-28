package org.example.edusoft.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.Map;

@Data
public class ImportRequest {
    @NotNull(message = "班级ID不能为空")
    private Long classId;
    
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;
    
    private String fileName;  // 可选，文件导入时的文件名
    
    @NotNull(message = "导入类型不能为空")
    private String importType;  // FILE, MANUAL, CODE_JOIN
    
    @NotEmpty(message = "学生数据不能为空")
    private List<Map<String, Object>> studentData;
}
