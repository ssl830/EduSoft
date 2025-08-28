package org.example.edusoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.dto.TeacherClassDTO;
import java.util.List;

@Mapper
public interface TeacherClassMapper extends BaseMapper<TeacherClassDTO> {
    
    @Select("""
            SELECT 
                cl.id,
                cl.name,
                cl.class_code as classCode,
                (SELECT COUNT(DISTINCT cu.user_id) 
                 FROM classuser cu 
                 WHERE cu.class_id = cl.id) as studentCount
            FROM class cl
            JOIN course co ON cl.course_id = co.id
            WHERE co.teacher_id = #{teacherId}
            """)
    List<TeacherClassDTO> getClassesByTeacherId(Long teacherId);
}
