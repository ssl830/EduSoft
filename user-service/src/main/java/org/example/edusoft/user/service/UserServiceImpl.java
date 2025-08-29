package org.example.edusoft.user.service;

import org.example.edusoft.user.entity.User;
import org.example.edusoft.user.dto.UserDTO;
import org.example.edusoft.user.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User save(User user) {
        userMapper.insert(user);
        return user;
    }
    
    @Override
    public User findByUserId(String userId) {
        return userMapper.findByUserId(userId);
    }
    
    @Override
    public User findById(Long id) {
        return userMapper.findById(id);
    }
    
    @Override
    public User update(User user) {
        userMapper.update(user);
        return user;
    }
    
    @Override
    public void updatePassword(String userId, String passwordHash) {
        userMapper.updatePassword(userId, passwordHash);
    }
    
    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }
    
    @Override
    public void deleteByUserId(String userId) {
        userMapper.deleteByUserId(userId);
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
    public List<User> getAllUsers() {
        return userMapper.getAllUsers();
    }
    
    @Override
    public List<User> getUsersByRole(String role) {
        return userMapper.getUsersByRole(role);
    }
    
    @Override
    public boolean isUserIdExists(String userId) {
        return findByUserId(userId) != null;
    }
    
    @Override
    public boolean isEmailExists(String email) {
        return userMapper.findByEmail(email) != null;
    }
    
    @Override
    public boolean validatePassword(String rawPassword, String hashedPassword) {
        // 这里应该实现密码验证逻辑
        return rawPassword.equals(hashedPassword); // 临时实现
    }
    
    @Override
    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole().name());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        return dto;
    }
    
    @Override
    public List<UserDTO> convertToDTOList(List<User> users) {
        if (users == null) {
            return null;
        }
        
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void deactivateAccount(Long userId) {
        deleteById(userId);
    }
}
