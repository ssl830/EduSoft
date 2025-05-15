package org.example.edusoft.Mapper;
import com.bugvictims.demo11.Pojo.*;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    //用户名查找
    @Select("select * from user where username=#{username}")
    User findByUserName(String username);

    //添加用户
    @Insert("INSERT INTO user(username, nick_name,password,email, phone, biology, status, create_time, update_time) " + "VALUES (#{username}, #{nickName},#{password},#{email} ,#{phone}, #{biology}, #{status}, NOW(), NOW())")
    void add(UserIgnorePassword user);

    //通过id获取用户
    @Select("select * from user where id=#{id}")
    User getUserById(int id);

    //更新用户信息
    @Update("update user set nick_name=#{nickName},email=#{email},phone=#{phone}," +
            "biology=#{biology},status=#{status}," +
            "update_time=#{updateTime},position=#{position}  where id=#{id}")
    void update(User user);

    //用户列表
    @Select("select * from user")
    List<User> getUsers();

    //用户队伍列表
    @Select("SELECT t.* FROM teams t " +
            "INNER JOIN team_user tu ON t.id = tu.team_id " +
            "WHERE tu.user_id = #{id}")
    List<Team> getTeams(int id);

    //当前用户所有邀请
    @Select("select * from invite_request  where user_id=#{id}")
    List<InviteRequest> getInviteRequests(int id);

    //用户所有申请
    @Select("select * from join_request  where user_id=#{id}")
    List<JoinRequest> getJoinRequests(int id);


}
