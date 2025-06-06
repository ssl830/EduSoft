package com.bugvictims.demo11.Service.impl;

import com.bugvictims.demo11.Mapper.*;
import com.bugvictims.demo11.Pojo.*;
import com.bugvictims.demo11.Service.UserService;
import com.bugvictims.demo11.Utils.ThreadLocalUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private JoinRequestMapper joinRequestMapper;

    //寻找用户名
    @Override
    public User findByUserName(String username) {
        User u = userMapper.findByUserName(username);
        return u;
    }

    //注册新用户
    @Override
    public void register(UserIgnorePassword user) {
        //加密处理，此处暂无，后期再加
        //添加
        userMapper.add(user);
    }

    //获取当前登录用户
    @Override
    public User getLoginUser() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        //根据用户名查询用户
        return findByUserName(username);
    }

    @Override
    public User getUserById(int id) {
        return userMapper.getUserById(id);
    }

    //更新用户信息
    @Override
    public void update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
    }

    //用户申请加入队伍
    @Override
    public void userJoin(JoinRequest joinRequest) {
        joinRequest.setCreateTime(LocalDateTime.now());
        joinRequest.setUpdateTime(LocalDateTime.now());
        joinRequestMapper.insertJoinRequest(joinRequest);
    }

    //退出登录
    @Override
    public void logout(User loginUser) {
    }

    //用户列表
    @Override
    public PageInfo<User> getUsers(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); //分页查询...
        List<User> users = userMapper.getUsers();
        return new PageInfo<>(users);
    }

    //队伍列表
    @Override
    public PageInfo<Team> getTeams(int id, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); //分页查询...
        List<Team> teams = userMapper.getTeams(id);
        return new PageInfo<>(teams);
    }

    //用户当前收到邀请
    public PageInfo<InviteRequest> getInvites(int id, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); //分页查询...
        List<InviteRequest> inviteRequests = userMapper.getInviteRequests(id);
        return new PageInfo<>(inviteRequests);
    }

    //用户当前申请
    public PageInfo<JoinRequest> getJoins(int id, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize); //分页查询...
        List<JoinRequest> joinRequests = userMapper.getJoinRequests(id);
        return new PageInfo<>(joinRequests);
    }

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(ToEmail toEmail, Integer fromId, String from) {
        SimpleMailMessage message = new SimpleMailMessage();

        String fromUser = userMapper.getUserById(fromId).getUsername();
        //发件人
        message.setFrom(from);
        //收件人
        message.setTo(toEmail.getTos());
        //邮件标题
        message.setSubject(toEmail.getSubject());
        //邮件内容
        message.setText(fromUser + "：\n" + toEmail.getContent());
        try {
            mailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace();
        }
    }
}
