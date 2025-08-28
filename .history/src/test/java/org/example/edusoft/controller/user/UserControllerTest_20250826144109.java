package org.example.edusoft.controller.user;

import org.example.edusoft.entity.user.User;
import org.example.edusoft.entity.user.UserUpdate;
import org.example.edusoft.service.user.UserService;
import org.example.edusoft.common.domain.Result;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.util.SaResult;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    // Utility: mock static for StpUtil
    void mockLoginTrue(Long id) {
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::getLoginId).thenReturn(id);
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::getLoginIdAsLong).thenReturn(id);
    }

    void mockLoginFalse() {
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::isLogin).thenReturn(false);
    }

    // register success
    @Test
    void register_success() throws Exception {
        User user = new User();
        user.setUserId("user1");
        user.setUsername("name");
        user.setPasswordHash("pw");
        Mockito.when(userService.findByUserId("user1")).thenReturn(null);
        Mockito.doNothing().when(userService).save(any(User.class));

        String json = "{\"userId\":\"user1\",\"username\":\"name\",\"passwordHash\":\"pw\",\"role\":\"student\"}";
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("注册成功"))
                .andExpect(jsonPath("$.code").value(200));
    }

    // register fail: userId exists
    @Test
    void register_fail_userIdExists() throws Exception {
        User user = new User();
        user.setUserId("user1");
        Mockito.when(userService.findByUserId("user1")).thenReturn(user);

        String json = "{\"userId\":\"user1\",\"username\":\"name\",\"passwordHash\":\"pw\",\"role\":\"student\"}";
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("用户ID已存在，请选择其他用户ID"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // register fail: save throws
    @Test
    void register_fail_saveThrows() throws Exception {
        Mockito.when(userService.findByUserId("user2")).thenReturn(null);
        Mockito.doThrow(new RuntimeException("fail!")).when(userService).save(any(User.class));

        String json = "{\"userId\":\"user2\",\"username\":\"name\",\"passwordHash\":\"pw\",\"role\":\"student\"}";
        mockMvc.perform(post("/api/user/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("注册失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }

    // login success
    @Test
    void login_success() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUserId("user1");
        user.setUsername("name");
        user.setPasswordHash(SaSecureUtil.md5BySalt("pw", "edusoft"));
        user.setRole("student");
        Mockito.when(userService.findByUserId("user1")).thenReturn(user);

        Mockito.doNothing().when(StpUtil.class);
        Mockito.mockStatic(StpUtil.class)
                .when(() -> StpUtil.login(1L));
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::getTokenValue).thenReturn("token123");

        mockMvc.perform(post("/api/user/login")
                .param("userId", "user1")
                .param("password", "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("登录成功"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").value("token123"));
    }

    // login fail: wrong password
    @Test
    void login_fail_wrongPassword() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setUserId("user1");
        user.setPasswordHash(SaSecureUtil.md5BySalt("pw", "edusoft"));
        Mockito.when(userService.findByUserId("user1")).thenReturn(user);

        mockMvc.perform(post("/api/user/login")
                .param("userId", "user1")
                .param("password", "wrongpw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // login fail: user not exist
    @Test
    void login_fail_userNotExist() throws Exception {
        Mockito.when(userService.findByUserId("user1")).thenReturn(null);
        mockMvc.perform(post("/api/user/login")
                .param("userId", "user1")
                .param("password", "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("用户名或密码错误"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // logout success
    @Test
    void logout_success() throws Exception {
        mockLoginTrue(1L);
        Mockito.doNothing().when(StpUtil.class);
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::logout);

        mockMvc.perform(post("/api/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("退出登录成功"))
                .andExpect(jsonPath("$.code").value(200));
    }

    // logout fail: not logged in
    @Test
    void logout_fail_notLoggedIn() throws Exception {
        mockLoginFalse();
        mockMvc.perform(post("/api/user/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("用户未登录"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // getUserInfo success
    @Test
    void getUserInfo_success() throws Exception {
        mockLoginTrue(2L);
        User user = new User();
        user.setId(2L);
        user.setUsername("n");
        user.setUserId("u2");
        user.setRole("student");
        Mockito.when(userService.findById(2L)).thenReturn(user);

        mockMvc.perform(get("/api/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取成功"))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(2));
    }

    // getUserInfo fail: not logged in/format error
    @Test
    void getUserInfo_fail_formatError() throws Exception {
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::checkLogin).thenThrow(new NumberFormatException("bad"));
        mockMvc.perform(get("/api/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("用户ID格式错误"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // getUserInfo fail: userService throws
    @Test
    void getUserInfo_fail_userServiceThrows() throws Exception {
        mockLoginTrue(2L);
        Mockito.when(userService.findById(2L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(get("/api/user/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取用户信息失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }

    // updateUserInfo success
    @Test
    void updateUserInfo_success() throws Exception {
        mockLoginTrue(3L);
        User user = new User();
        user.setId(3L);
        user.setUsername("n");
        user.setEmail("e");
        Mockito.when(userService.findById(3L)).thenReturn(user);
        Mockito.doNothing().when(userService).save(any(User.class));

        String json = "{\"username\":\"newname\",\"email\":\"newmail\"}";
        mockMvc.perform(post("/api/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("更新成功"))
                .andExpect(jsonPath("$.code").value(200));
    }

    // updateUserInfo fail: not logged in
    @Test
    void updateUserInfo_fail_notLoggedIn() throws Exception {
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::checkLogin).thenThrow(new RuntimeException("not logged in"));
        String json = "{\"username\":\"a\"}";
        mockMvc.perform(post("/api/user/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("更新失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }

    // changePassword success
    @Test
    void changePassword_success() throws Exception {
        mockLoginTrue(4L);
        User user = new User();
        user.setId(4L);
        user.setPasswordHash(SaSecureUtil.md5BySalt("old", "edusoft"));
        Mockito.when(userService.findById(4L)).thenReturn(user);
        Mockito.doNothing().when(userService).save(any(User.class));

        mockMvc.perform(post("/api/user/changePassword")
                .param("oldPassword", "old")
                .param("newPassword", "new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("密码修改成功"))
                .andExpect(jsonPath("$.code").value(200));
    }

    // changePassword fail: wrong old password
    @Test
    void changePassword_fail_wrongOld() throws Exception {
        mockLoginTrue(4L);
        User user = new User();
        user.setId(4L);
        user.setPasswordHash(SaSecureUtil.md5BySalt("old", "edusoft"));
        Mockito.when(userService.findById(4L)).thenReturn(user);

        mockMvc.perform(post("/api/user/changePassword")
                .param("oldPassword", "bad")
                .param("newPassword", "new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("原密码错误"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // changePassword fail: format error
    @Test
    void changePassword_fail_formatError() throws Exception {
        Mockito.mockStatic(StpUtil.class)
                .when(StpUtil::checkLogin).thenThrow(new NumberFormatException("bad"));
        mockMvc.perform(post("/api/user/changePassword")
                .param("oldPassword", "a")
                .param("newPassword", "b"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("用户ID格式错误"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // changePassword fail: save throws
    @Test
    void changePassword_fail_saveThrows() throws Exception {
        mockLoginTrue(4L);
        User user = new User();
        user.setId(4L);
        user.setPasswordHash(SaSecureUtil.md5BySalt("old", "edusoft"));
        Mockito.when(userService.findById(4L)).thenReturn(user);
        Mockito.doThrow(new RuntimeException("fail!")).when(userService).save(any(User.class));

        mockMvc.perform(post("/api/user/changePassword")
                .param("oldPassword", "old")
                .param("newPassword", "new"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("密码修改失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }

    // deactivateAccount success
    @Test
    void deactivateAccount_success() throws Exception {
        mockLoginTrue(5L);
        User user = new User();
        user.setId(5L);
        user.setPasswordHash(SaSecureUtil.md5BySalt("pw", "edusoft"));
        Mockito.when(userService.findById(5L)).thenReturn(user);
        Mockito.doNothing().when(userService).deactivateAccount(5L);
        Mockito.doNothing().when(StpUtil.class);
        Mockito.mockStatic(StpUtil.class).when(StpUtil::logout);

        mockMvc.perform(post("/api/user/deactivate")
                .param("password", "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("账号已注销"))
                .andExpect(jsonPath("$.code").value(200));
    }

    // deactivateAccount fail: wrong password
    @Test
    void deactivateAccount_fail_wrongPw() throws Exception {
        mockLoginTrue(5L);
        User user = new User();
        user.setId(5L);
        user.setPasswordHash(SaSecureUtil.md5BySalt("pw", "edusoft"));
        Mockito.when(userService.findById(5L)).thenReturn(user);

        mockMvc.perform(post("/api/user/deactivate")
                .param("password", "bad"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("密码错误"))
                .andExpect(jsonPath("$.code").value(500));
    }

    // deactivateAccount fail: service throws
    @Test
    void deactivateAccount_fail_serviceThrows() throws Exception {
        mockLoginTrue(5L);
        User user = new User();
        user.setId(5L);
        user.setPasswordHash(SaSecureUtil.md5BySalt("pw", "edusoft"));
        Mockito.when(userService.findById(5L)).thenReturn(user);
        Mockito.doThrow(new RuntimeException("fail!")).when(userService).deactivateAccount(5L);

        mockMvc.perform(post("/api/user/deactivate")
                .param("password", "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("注销失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }

    // getAllTeachers success/fail
    @Test
    void getAllTeachers_success() throws Exception {
        List<User> list = Arrays.asList(new User(), new User());
        Mockito.when(userService.getAllTeachers()).thenReturn(list);

        mockMvc.perform(get("/api/user/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取老师列表成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getAllTeachers_fail() throws Exception {
        Mockito.when(userService.getAllTeachers()).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/user/teachers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取老师列表失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }

    // getAllStudents success/fail
    @Test
    void getAllStudents_success() throws Exception {
        List<User> list = Arrays.asList(new User(), new User());
        Mockito.when(userService.getAllStudents()).thenReturn(list);

        mockMvc.perform(get("/api/user/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取学生列表成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getAllStudents_fail() throws Exception {
        Mockito.when(userService.getAllStudents()).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/user/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取学生列表失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }

    // getAllTutors success/fail
    @Test
    void getAllTutors_success() throws Exception {
        List<User> list = Arrays.asList(new User(), new User());
        Mockito.when(userService.getAllTutors()).thenReturn(list);

        mockMvc.perform(get("/api/user/tutor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("获取管理员列表成功"))
                .andExpect(jsonPath("$.data[0]").exists());
    }

    @Test
    void getAllTutors_fail() throws Exception {
        Mockito.when(userService.getAllTutors()).thenThrow(new RuntimeException("fail!"));

        mockMvc.perform(get("/api/user/tutor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("获取管理员列表失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }

    // deleteUser success/fail
    @Test
    void deleteUser_success() throws Exception {
        Mockito.when(userService.deleteUser(11L)).thenReturn(true);

        mockMvc.perform(delete("/api/user/delete/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("删除用户成功"))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void deleteUser_fail() throws Exception {
        Mockito.when(userService.deleteUser(11L)).thenThrow(new RuntimeException("fail!"));
        mockMvc.perform(delete("/api/user/delete/11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value(org.hamcrest.Matchers.startsWith("删除用户失败：")))
                .andExpect(jsonPath("$.code").value(500));
    }
}