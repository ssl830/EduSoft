package org.example.edusoft.mapper.imports;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.imports.ImportRecord;
import java.util.List;

@Mapper
public interface ImportRecordMapper extends BaseMapper<ImportRecord> {
    
    @Select("SELECT * FROM import_record WHERE class_id = #{classId} ORDER BY import_time DESC")
    List<ImportRecord> getImportRecordsByClassId(Long classId);
} 