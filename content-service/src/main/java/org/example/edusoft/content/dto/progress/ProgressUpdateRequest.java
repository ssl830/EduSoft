package org.example.edusoft.content.dto.progress;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressUpdateRequest {
    
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;
    
    @NotNull(message = "学生ID不能为空")
    private Long studentId;
    
    @NotNull(message = "进度不能为空")
    private Integer progress;
    
    @NotNull(message = "播放位置不能为空")
    private Integer position;
}
