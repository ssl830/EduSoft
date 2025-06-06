package org.example.edusoft.mapper;

import org.apache.ibatis.annotations.*;
import org.example.edusoft.entity.ResourceVersion;
import java.util.List;

@Mapper
public interface ResourceVersionMapper {
    @Select("SELECT * FROM ResourceVersion WHERE id = #{id}")
    ResourceVersion findById(Long id);

    @Select("SELECT * FROM ResourceVersion WHERE resource_id = #{resourceId} ORDER BY version DESC")
    List<ResourceVersion> findByResourceId(Long resourceId);

    @Select("SELECT * FROM ResourceVersion WHERE resource_id = #{resourceId} AND version = #{version}")
    ResourceVersion findByResourceAndVersion(Long resourceId, Integer version);

    @Insert("INSERT INTO ResourceVersion (resource_id, version, file_url) " +
            "VALUES (#{resourceId}, #{version}, #{fileUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ResourceVersion version);

    @Delete("DELETE FROM ResourceVersion WHERE id = #{id}")
    int deleteById(Long id);
} 