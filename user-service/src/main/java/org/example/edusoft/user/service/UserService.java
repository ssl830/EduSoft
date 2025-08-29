package org.example.edusoft.user.service;

import org.example.edusoft.user.entity.User;
import org.example.edusoft.user.dto.UserDTO;
import java.util.List;

public interface UserService {
    
    /**
     * 保存用户
     */
    User save(User user);
    
    /**
     * 根据用户ID查找用户
     */
    User findByUserId(String userId);
    
    /**
     * 根据ID查找用户
     */
    User findById(Long id);
    
    /**
     * 更新用户信息
     */
    User update(User user);
    
    /**
     * 更新用户密码
     */
    void updatePassword(String userId, String passwordHash);
    
    /**
     * 根据ID删除用户
     */
    void deleteById(Long id);
    
    /**
     * 根据用户ID删除用户
     */
    void deleteByUserId(String userId);
    
    /**
     * 获取所有教师
     */
    List<User> getAllTeachers();
    
    /**
     * 获取所有学生
     */
    List<User> getAllStudents();
    
    /**
     * 获取所有导师
     */
    List<User> getAllTutors();
    
    /**
     * 获取所有用户
     */
    List<User> getAllUsers();
    
    /**
     * 根据角色获取用户列表
     */
    List<User> getUsersByRole(String role);
    
    /**
     * 检查用户ID是否已存在
     */
    boolean isUserIdExists(String userId);
    
    /**
     * 检查邮箱是否已存在
     */
    boolean isEmailExists(String email);
    
    /**
     * 验证用户密码
     */
    boolean validatePassword(String rawPassword, String hashedPassword);
    
    /**
     * 将User实体转换为UserDTO
     */
    UserDTO convertToDTO(User user);
    
    /**
     * 将User实体列表转换为UserDTO列表
     */
    List<UserDTO> convertToDTOList(List<User> users);
    
    /**
     * 停用用户账户
     */
    void deactivateAccount(Long userId);
}
