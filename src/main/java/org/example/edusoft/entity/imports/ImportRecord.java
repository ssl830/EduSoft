package org.example.edusoft.entity.imports;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@TableName("import_record")
public class ImportRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @NotNull(message = "班级ID不能为空")
    private Long classId;
    
    @NotNull(message = "操作人ID不能为空")
    private Long operatorId;  // 操作人ID
    
    @Size(max = 255, message = "文件名长度不能超过255个字符")
    private String fileName;  // 导入文件名
    
    @NotNull(message = "总记录数不能为空")
    @Min(value = 0, message = "总记录数不能小于0")
    private Integer totalCount;  // 总记录数
    
    @NotNull(message = "成功导入数不能为空")
    @Min(value = 0, message = "成功导入数不能小于0")
    private Integer successCount;  // 成功导入数
    
    @NotNull(message = "失败数不能为空")
    @Min(value = 0, message = "失败数不能小于0")
    private Integer failCount;  // 失败数
    
    @Size(max = 1000, message = "失败原因长度不能超过1000个字符")
    private String failReason;  // 失败原因
    
    @NotNull(message = "导入时间不能为空")
    private LocalDateTime importTime;  // 导入时间
    
    @NotNull(message = "导入类型不能为空")
    @Size(min = 4, max = 6, message = "导入类型必须是FILE或MANUAL")
    private String importType;  // 导入类型：FILE（文件导入）, MANUAL（手动导入）
} 