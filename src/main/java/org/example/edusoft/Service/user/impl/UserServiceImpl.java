package org.example.edusoft.service.user.impl;
import org.springframework.stereotype.Service;
import cn.dev33.satoken.secure.SaSecureUtil;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.mapper.user.UserMapper;
import org.example.edusoft.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public User findByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            userMapper.insert(user);
        } else {
            userMapper.update(user);
        }
    }
    
    @Override
    public void deactivateAccount(Long id) {
        // 直接执行删除
        userMapper.deleteById(id);
    }

    @Override
    public List<User> getAllTeachers() {
        return userMapper.getAllTeachers();
    }

    @Override
    public List<User> getAllStudents() {
        return userMapper.getAllStudents();
    }

    @Override
    public List<User> getAllTutors() {
        return userMapper.getAllTutors();
    }

    @Override
    public Boolean deleteUser(Long id) {
        return userMapper.deleteUser(id) > 0;
    }

}