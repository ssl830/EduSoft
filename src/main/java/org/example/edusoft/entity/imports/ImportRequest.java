package org.example.edusoft.entity.imports;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ImportRequest {
    private Long classId;
    private Long operatorId;
    private String importType;
    private String fileName;
    private List<Map<String, Object>> studentData;
} 