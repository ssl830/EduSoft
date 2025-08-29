package org.example.edusoft.service.user;

import org.example.edusoft.entity.user.User;
import org.example.edusoft.mapper.user.UserMapper;
import org.example.edusoft.service.user.impl.UserServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("根据ID查找用户 - 找到用户")
    void testFindById_found() {
        User user = new User();
        user.setId(1L);
        when(userMapper.findById(1L)).thenReturn(user);

        User result = userService.findById(1L);

        assertEquals(user, result);
        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("根据ID查找用户 - 未找到用户")
    void testFindById_notFound() {
        when(userMapper.findById(2L)).thenReturn(null);

        User result = userService.findById(2L);

        assertNull(result);
    }

    @Test
    @DisplayName("根据ID查找用户 - 传入空ID")
    void testFindById_nullId() {
        when(userMapper.findById(null)).thenReturn(null);

        User result = userService.findById(null);

        assertNull(result);
    }

    @Test
    @DisplayName("根据UserId查找用户 - 找到用户")
    void testFindByUserId_found() {
        User user = new User();
        user.setUserId("u123");
        when(userMapper.findByUserId("u123")).thenReturn(user);

        User result = userService.findByUserId("u123");

        assertEquals(user, result);
        assertEquals("u123", result.getUserId());
    }

    @Test
    @DisplayName("根据UserId查找用户 - 未找到用户")
    void testFindByUserId_notFound() {
        when(userMapper.findByUserId("none")).thenReturn(null);

        User result = userService.findByUserId("none");

        assertNull(result);
    }

    @Test
    @DisplayName("保存用户 - 新增用户")
    void testSave_insert() {
        User user = new User();
        user.setId(null);

        userService.save(user);

        verify(userMapper, times(1)).insert(any(User.class));
        verify(userMapper, never()).update(any(User.class));
    }

    @Test
    @DisplayName("保存用户 - 更新用户")
    void testSave_update() {
        User user = new User();
        user.setId(10L);

        userService.save(user);

        verify(userMapper, times(1)).update(any(User.class));
        verify(userMapper, never()).insert(any(User.class));
    }

    @Test
    @DisplayName("注销账户 - 有效ID")
    void testDeactivateAccount_validId() {
        userService.deactivateAccount(5L);
        verify(userMapper, times(1)).deleteById(5L);
    }

    @Test
    @DisplayName("注销账户 - 空ID")
    void testDeactivateAccount_nullId() {
        doNothing().when(userMapper).deleteById(null);
        userService.deactivateAccount(null);
        verify(userMapper, times(1)).deleteById(null);
    }

    @Test
    @DisplayName("获取所有教师 - 非空列表")
    void testGetAllTeachers_nonEmpty() {
        List<User> teachers = Arrays.asList(new User(), new User());
        when(userMapper.getAllTeachers()).thenReturn(teachers);

        List<User> result = userService.getAllTeachers();

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("获取所有教师 - 空列表")
    void testGetAllTeachers_empty() {
        when(userMapper.getAllTeachers()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllTeachers();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取所有学生 - 非空列表")
    void testGetAllStudents_nonEmpty() {
        List<User> students = Arrays.asList(new User());
        when(userMapper.getAllStudents()).thenReturn(students);

        List<User> result = userService.getAllStudents();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取所有学生 - 空列表")
    void testGetAllStudents_empty() {
        when(userMapper.getAllStudents()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllStudents();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取所有辅导员 - 非空列表")
    void testGetAllTutors_nonEmpty() {
        List<User> tutors = Arrays.asList(new User());
        when(userMapper.getAllTutors()).thenReturn(tutors);

        List<User> result = userService.getAllTutors();

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("获取所有辅导员 - 空列表")
    void testGetAllTutors_empty() {
        when(userMapper.getAllTutors()).thenReturn(Collections.emptyList());

        List<User> result = userService.getAllTutors();

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("删除用户 - 删除成功")
    void testDeleteUser_success() {
        when(userMapper.deleteUser(1L)).thenReturn(1);

        boolean result = userService.deleteUser(1L);

        assertTrue(result);
    }

    @Test
    @DisplayName("删除用户 - 删除失败")
    void testDeleteUser_fail() {
        when(userMapper.deleteUser(2L)).thenReturn(0);

        boolean result = userService.deleteUser(2L);

        assertFalse(result);
    }
}
