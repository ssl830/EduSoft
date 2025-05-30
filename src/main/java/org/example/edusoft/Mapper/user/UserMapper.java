package org.example.edusoft.mapper.user;
import org.example.edusoft.entity.*;
import org.example.edusoft.entity.user.User;
import org.apache.ibatis.annotations.*; 

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM user WHERE user_id = #{userId}")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "username", column = "username"),
        @Result(property = "passwordHash", column = "password_hash"),
        @Result(property = "role", column = "role"),
        @Result(property = "email", column = "email"),
        @Result(property = "userId", column = "user_id"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    User findByUserId(@Param("userId") String userId);

    @Select("SELECT * FROM User WHERE id = #{id}")
    User findById(@Param("id")Long id);

    @Insert("INSERT INTO User (username, user_id, password_hash, role, email, created_at, updated_at) " +
           "VALUES (#{username},#{userId}, #{passwordHash}, #{role},  #{email}, " +
           "CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(User user);

    @Update("UPDATE User SET username = #{username}, user_id=#{userId}, password_hash = #{passwordHash}, " +
            "role = #{role}, email = #{email} " +
            "WHERE id = #{id}")
    void update(User user);
    
    @Delete("DELETE FROM user WHERE id = #{id}")
    void deleteById(@Param("id") Long id);

    @Delete("DELETE FROM user WHERE user_id = #{userId}")
    void deleteByUserId(@Param("userId") String userId);

}