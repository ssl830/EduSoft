package org.example.edusoft.mapper.classroom;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.edusoft.entity.classroom.ClassUser;
import java.util.List;

@Mapper
public interface ClassUserMapper extends BaseMapper<ClassUser> {
    
    @Select("SELECT cu.class_id, cu.user_id, cu.joined_at, " +
            "u.username as studentName, u.user_id as studentId " +
            "FROM classuser cu " +
            "LEFT JOIN user u ON cu.user_id = u.id " +
            "WHERE cu.class_id = #{classId}")
    List<ClassUser> getClassUsersByClassId(Long classId);
    
    @Select("SELECT COUNT(*) FROM classuser WHERE class_id = #{classId} AND user_id = #{userId}")
    int checkUserInClass(Long classId, Long userId);

    @Select("SELECT COUNT(*) FROM user WHERE id = #{userId}")
    boolean checkUserExists(Long userId);
} 