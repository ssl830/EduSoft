package org.example.edusoft.service.user.impl;
import org.springframework.stereotype.Service;
import cn.dev33.satoken.secure.SaSecureUtil;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.exception.BusinessException;
import org.example.edusoft.mapper.user.UserMapper;
import org.example.edusoft.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
}