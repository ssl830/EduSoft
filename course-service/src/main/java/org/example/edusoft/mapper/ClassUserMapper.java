package org.example.edusoft.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.ClassUser;
import java.util.List;

@Mapper
public interface ClassUserMapper extends BaseMapper<ClassUser> {
    
    @Select("SELECT * FROM classuser WHERE class_id = #{classId}")
    List<ClassUser> getClassUsers(Long classId);
    
    @Insert("INSERT INTO classuser (class_id, user_id, joined_at) VALUES (#{classId}, #{userId}, NOW())")
    int joinClass(@Param("classId") Long classId, @Param("userId") Long userId);
    
    @Delete("DELETE FROM classuser WHERE class_id = #{classId} AND user_id = #{userId}")
    int leaveClass(@Param("classId") Long classId, @Param("userId") Long userId);
    
    @Select("SELECT COUNT(*) FROM classuser WHERE class_id = #{classId} AND user_id = #{userId}")
    int isUserInClass(@Param("classId") Long classId, @Param("userId") Long userId);
    
    @Select("SELECT * FROM class WHERE class_code = #{classCode}")
    org.example.edusoft.entity.Class getClassByCode(String classCode);
}
