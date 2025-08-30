package org.example.edusoft.learning.mapper.record;

import org.apache.ibatis.annotations.Mapper;
import org.example.edusoft.learning.entity.record.StudyRecord;
import org.example.edusoft.learning.entity.record.PracticeRecord;
import java.util.List;

@Mapper
public interface RecordMapper {
    List<StudyRecord> getStudyRecordsByUserId(Long userId);
    List<PracticeRecord> getPracticeRecordsByUserId(Long userId);
    // ... 其他方法 ...
}
