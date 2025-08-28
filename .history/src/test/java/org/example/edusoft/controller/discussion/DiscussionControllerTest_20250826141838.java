package org.example.edusoft.controller.discussion;

import org.example.edusoft.entity.discussion.Discussion;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.service.discussion.DiscussionService;
import org.example.edusoft.service.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DiscussionController.class)
class DiscussionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscussionService discussionService;

    @MockBean
    private UserService userService;

    // For static StpUtil mock, see note below

    // Utility: Setup StpUtil (simulate login)
    @BeforeEach
    void setupStpUtil() {
        // You need to use a static mocking framework (e.g. Mockito.mockStatic)
        // Here is a pseudo-code comment for your actual environment:
        // Mockito.mockStatic(StpUtil.class).when(StpUtil::isLogin).thenReturn(true);
        // Mockito.mockStatic(StpUtil.class).when(StpUtil::getLoginIdAsLong).thenReturn(10L);
        // For negative tests, you can set thenReturn(false) as needed
    }

    // 1. createDiscussion
    @Test
    void createDiscussion_success() throws Exception {
        // mock login
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        User user = new User();
        user.setUserId("2025");
        user.setId(10L);
        Mockito.when(userService.findById(10L)).thenReturn(user);

        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setCourseId(2L);
        discussion.setClassId(3L);
        discussion.setCreatorId(10L);
        discussion.setCreatorNum("2025");
        Mockito.when(discussionService.createDiscussion(any(Discussion.class))).thenReturn(discussion);

        mockMvc.perform(post("/api/discussion/course/2/class/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"TestTitle\",\"content\":\"TestContent\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createDiscussion_notLoggedIn() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(false);

        mockMvc.perform(post("/api/discussion/course/2/class/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"TestTitle\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    @Test
    void createDiscussion_invalidCourseId() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);

        mockMvc.perform(post("/api/discussion/course/0/class/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"TestTitle\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("无效的课程ID"));
    }

    @Test
    void createDiscussion_invalidClassId() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);

        mockMvc.perform(post("/api/discussion/course/2/class/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"TestTitle\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("无效的班级ID"));
    }

    @Test
    void createDiscussion_userNotFound() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(null);

        mockMvc.perform(post("/api/discussion/course/2/class/3")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"TestTitle\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));
    }

    // 2. updateDiscussion
    @Test
    void updateDiscussion_success() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);

        Discussion existing = new Discussion();
        existing.setId(1L);
        existing.setCreatorId(10L);
        existing.setCourseId(2L);
        existing.setClassId(3L);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(existing);

        Discussion updated = new Discussion();
        updated.setId(1L);
        updated.setTitle("NewTitle");
        Mockito.when(discussionService.updateDiscussion(any(Discussion.class))).thenReturn(updated);

        mockMvc.perform(put("/api/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"NewTitle\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("NewTitle"));
    }

    @Test
    void updateDiscussion_notLoggedIn() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(false);

        mockMvc.perform(put("/api/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"NewTitle\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    @Test
    void updateDiscussion_notFound() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(null);

        mockMvc.perform(put("/api/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"NewTitle\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("讨论不存在"));
    }

    @Test
    void updateDiscussion_noPermission() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Discussion existing = new Discussion();
        existing.setId(1L);
        existing.setCreatorId(20L); // not login user
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(existing);

        mockMvc.perform(put("/api/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"NewTitle\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("您没有权限修改此讨论"));
    }

    // 3. deleteDiscussion
    @Test
    void deleteDiscussion_success() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);

        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setCreatorId(10L);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);

        mockMvc.perform(delete("/api/discussion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("讨论删除成功"));
    }

    @Test
    void deleteDiscussion_notLoggedIn() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(false);

        mockMvc.perform(delete("/api/discussion/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    @Test
    void deleteDiscussion_notFound() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(null);

        mockMvc.perform(delete("/api/discussion/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("讨论不存在"));
    }

    @Test
    void deleteDiscussion_noPermission() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setCreatorId(11L);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);

        mockMvc.perform(delete("/api/discussion/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("您没有权限删除此讨论"));
    }

    // 4. getDiscussion
    @Test
    void getDiscussion_success() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);

        Discussion discussion = new Discussion();
        discussion.setId(1L);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);

        mockMvc.perform(get("/api/discussion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getDiscussion_notLoggedIn() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(false);

        mockMvc.perform(get("/api/discussion/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    @Test
    void getDiscussion_notFound() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(null);

        mockMvc.perform(get("/api/discussion/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("讨论不存在"));
    }

    // 5. getDiscussionsByCourse
    @Test
    void getDiscussionsByCourse_success() throws Exception {
        List<Discussion> discussions = Arrays.asList(new Discussion(), new Discussion());
        Mockito.when(discussionService.getDiscussionsByCourse(2L)).thenReturn(discussions);

        mockMvc.perform(get("/api/discussion/course/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getDiscussionsByCourse_fail() throws Exception {
        Mockito.when(discussionService.getDiscussionsByCourse(2L)).thenThrow(new RuntimeException("error"));
        mockMvc.perform(get("/api/discussion/course/2"))
                .andExpect(status().is5xxServerError());
    }

    // 6. getDiscussionsByClass
    @Test
    void getDiscussionsByClass_success() throws Exception {
        List<Discussion> discussions = Arrays.asList(new Discussion(), new Discussion());
        Mockito.when(discussionService.getDiscussionsByClass(3L)).thenReturn(discussions);

        mockMvc.perform(get("/api/discussion/class/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getDiscussionsByClass_fail() throws Exception {
        Mockito.when(discussionService.getDiscussionsByClass(3L)).thenThrow(new RuntimeException("error"));
        mockMvc.perform(get("/api/discussion/class/3"))
                .andExpect(status().is5xxServerError());
    }

    // 7. getDiscussionsByCreator
    @Test
    void getDiscussionsByCreator_success() throws Exception {
        List<Discussion> discussions = Arrays.asList(new Discussion(), new Discussion());
        Mockito.when(discussionService.getDiscussionsByCreator(10L)).thenReturn(discussions);

        mockMvc.perform(get("/api/discussion/creator/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getDiscussionsByCreator_fail() throws Exception {
        Mockito.when(discussionService.getDiscussionsByCreator(10L)).thenThrow(new RuntimeException("error"));
        mockMvc.perform(get("/api/discussion/creator/10"))
                .andExpect(status().is5xxServerError());
    }

    // 8. getDiscussionsByCourseAndClass
    @Test
    void getDiscussionsByCourseAndClass_success() throws Exception {
        List<Discussion> discussions = Arrays.asList(new Discussion(), new Discussion());
        Mockito.when(discussionService.getDiscussionsByCourseAndClass(2L, 3L)).thenReturn(discussions);

        mockMvc.perform(get("/api/discussion/course/2/class/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getDiscussionsByCourseAndClass_fail() throws Exception {
        Mockito.when(discussionService.getDiscussionsByCourseAndClass(2L, 3L)).thenThrow(new RuntimeException("error"));
        mockMvc.perform(get("/api/discussion/course/2/class/3"))
                .andExpect(status().is5xxServerError());
    }

    // 9. updatePinnedStatus
    @Test
    void updatePinnedStatus_success() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setRole(User.UserRole.teacher);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(user);

        Discussion discussion = new Discussion();
        discussion.setId(1L);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);

        mockMvc.perform(put("/api/discussion/1/pin?isPinned=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("讨论已置顶"));
    }

    @Test
    void updatePinnedStatus_userNotFound() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(null);

        mockMvc.perform(put("/api/discussion/1/pin?isPinned=true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));
    }

    @Test
    void updatePinnedStatus_notTeacher() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setRole(User.UserRole.student);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(user);

        mockMvc.perform(put("/api/discussion/1/pin?isPinned=true"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("只有教师才能执行此操作"));
    }

    @Test
    void updatePinnedStatus_discussionNotFound() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setRole(User.UserRole.teacher);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(user);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(null);

        mockMvc.perform(put("/api/discussion/1/pin?isPinned=true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("讨论不存在"));
    }

    // 10. updateClosedStatus
    @Test
    void updateClosedStatus_success() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setRole(User.UserRole.teacher);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(user);

        Discussion discussion = new Discussion();
        discussion.setId(1L);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);

        mockMvc.perform(put("/api/discussion/1/close?isClosed=true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("讨论已关闭"));
    }

    @Test
    void updateClosedStatus_userNotFound() throws Exception {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(null);

        mockMvc.perform(put("/api/discussion/1/close?isClosed=true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));
    }

    @Test
    void updateClosedStatus_notTeacher() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setRole(User.UserRole.student);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(user);

        mockMvc.perform(put("/api/discussion/1/close?isClosed=true"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("只有教师才能执行此操作"));
    }

    @Test
    void updateClosedStatus_discussionNotFound() throws Exception {
        User user = new User();
        user.setId(10L);
        user.setRole(User.UserRole.teacher);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
            .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(10L);
        Mockito.when(userService.findById(10L)).thenReturn(user);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(null);

        mockMvc.perform(put("/api/discussion/1/close?isClosed=true"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("讨论不存在"));
    }

    // 11. countDiscussionsByCourse
    @Test
    void countDiscussionsByCourse_success() throws Exception {
        Mockito.when(discussionService.countDiscussionsByCourse(2L)).thenReturn(4);

        mockMvc.perform(get("/api/discussion/course/2/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("4"));
    }

    @Test
    void countDiscussionsByCourse_fail() throws Exception {
        Mockito.when(discussionService.countDiscussionsByCourse(2L)).thenThrow(new RuntimeException("error"));
        mockMvc.perform(get("/api/discussion/course/2/count"))
                .andExpect(status().is5xxServerError());
    }

    // 12. countDiscussionsByClass
    @Test
    void countDiscussionsByClass_success() throws Exception {
        Mockito.when(discussionService.countDiscussionsByClass(3L)).thenReturn(2);

        mockMvc.perform(get("/api/discussion/class/3/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void countDiscussionsByClass_fail() throws Exception {
        Mockito.when(discussionService.countDiscussionsByClass(3L)).thenThrow(new RuntimeException("error"));
        mockMvc.perform(get("/api/discussion/class/3/count"))
                .andExpect(status().is5xxServerError());
    }
}