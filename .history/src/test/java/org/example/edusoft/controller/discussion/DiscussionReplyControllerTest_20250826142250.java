package org.example.edusoft.controller.discussion;

import org.example.edusoft.entity.discussion.DiscussionReply;
import org.example.edusoft.entity.discussion.Discussion;
import org.example.edusoft.entity.user.User;
import org.example.edusoft.service.discussion.DiscussionReplyService;
import org.example.edusoft.service.user.UserService;
import org.example.edusoft.service.discussion.DiscussionService;
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

@WebMvcTest(DiscussionReplyController.class)
class DiscussionReplyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscussionReplyService discussionReplyService;
    @MockBean
    private UserService userService;
    @MockBean
    private DiscussionService discussionService;

    void mockLoginTrue() {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(100L);
    }
    void mockLoginFalse() {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(false);
    }

    // createReply success
    @Test
    void createReply_success() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        user.setRole(User.UserRole.student);
        Mockito.when(userService.findById(100L)).thenReturn(user);

        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setIsClosed(false);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);

        DiscussionReply reply = new DiscussionReply();
        reply.setId(10L);
        reply.setDiscussionId(1L);
        reply.setUserId(100L);
        Mockito.when(discussionReplyService.createReply(any(DiscussionReply.class))).thenReturn(reply);

        mockMvc.perform(post("/api/discussion-reply/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    // createReply not logged in
    @Test
    void createReply_notLoggedIn() throws Exception {
        mockLoginFalse();
        mockMvc.perform(post("/api/discussion-reply/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    // createReply user not found
    @Test
    void createReply_userNotFound() throws Exception {
        mockLoginTrue();
        Mockito.when(userService.findById(100L)).thenReturn(null);
        mockMvc.perform(post("/api/discussion-reply/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));
    }

    // createReply discussion not found
    @Test
    void createReply_discussionNotFound() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(null);
        mockMvc.perform(post("/api/discussion-reply/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("讨论不存在"));
    }

    // createReply discussion closed
    @Test
    void createReply_discussionClosed() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setIsClosed(true);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);
        mockMvc.perform(post("/api/discussion-reply/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("该讨论已关闭，无法回复"));
    }

    // createReply parent reply not found
    @Test
    void createReply_parentReplyNotFound() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        user.setRole(User.UserRole.student);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setIsClosed(false);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);
        Mockito.when(discussionReplyService.getReply(999L)).thenReturn(null);

        mockMvc.perform(post("/api/discussion-reply/discussion/1?parentReplyId=999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("父回复不存在"));
    }

    // createReply parent reply not belong to discussion
    @Test
    void createReply_parentReplyNotBelong() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        user.setRole(User.UserRole.student);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setIsClosed(false);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);
        DiscussionReply parentReply = new DiscussionReply();
        parentReply.setId(999L);
        parentReply.setDiscussionId(2L);
        Mockito.when(discussionReplyService.getReply(999L)).thenReturn(parentReply);

        mockMvc.perform(post("/api/discussion-reply/discussion/1?parentReplyId=999")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("父回复不属于该讨论"));
    }

    // createReply service throws IllegalArgumentException
    @Test
    void createReply_serviceThrowsIllegalArgumentException() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        user.setRole(User.UserRole.student);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setIsClosed(false);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);
        Mockito.when(discussionReplyService.createReply(any(DiscussionReply.class)))
                .thenThrow(new IllegalArgumentException("Some error"));

        mockMvc.perform(post("/api/discussion-reply/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("创建回复失败: Some error"));
    }

    // createReply service throws Exception
    @Test
    void createReply_serviceThrowsException() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        user.setRole(User.UserRole.student);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        discussion.setIsClosed(false);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);
        Mockito.when(discussionReplyService.createReply(any(DiscussionReply.class)))
                .thenThrow(new RuntimeException("fail"));

        mockMvc.perform(post("/api/discussion-reply/discussion/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"hello\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.startsWith("创建回复失败:")));
    }

    // updateReply success
    @Test
    void updateReply_success() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        user.setRole(User.UserRole.student);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        DiscussionReply existing = new DiscussionReply();
        existing.setId(10L);
        existing.setUserId(100L);
        existing.setDiscussionId(1L);
        existing.setParentReplyId(null);
        existing.setIsTeacherReply(false);
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(existing);

        DiscussionReply updated = new DiscussionReply();
        updated.setId(10L);
        updated.setContent("updated");
        Mockito.when(discussionReplyService.updateReply(any(DiscussionReply.class))).thenReturn(updated);

        mockMvc.perform(put("/api/discussion-reply/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"updated\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.content").value("updated"));
    }

    // updateReply not logged in
    @Test
    void updateReply_notLoggedIn() throws Exception {
        mockLoginFalse();
        mockMvc.perform(put("/api/discussion-reply/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"updated\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    // updateReply user not found
    @Test
    void updateReply_userNotFound() throws Exception {
        mockLoginTrue();
        Mockito.when(userService.findById(100L)).thenReturn(null);
        mockMvc.perform(put("/api/discussion-reply/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"updated\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));
    }

    // updateReply reply not found
    @Test
    void updateReply_replyNotFound() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(null);
        mockMvc.perform(put("/api/discussion-reply/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"updated\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("回复不存在"));
    }

    // updateReply no permission
    @Test
    void updateReply_noPermission() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        Mockito.when(userService.findById(100L)).thenReturn(user);
        DiscussionReply existing = new DiscussionReply();
        existing.setId(10L);
        existing.setUserId(200L);
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(existing);
        mockMvc.perform(put("/api/discussion-reply/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"updated\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("您没有权限修改此回复"));
    }

    // updateReply service throws Exception
    @Test
    void updateReply_serviceThrowsException() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setUserId("20240001");
        Mockito.when(userService.findById(100L)).thenReturn(user);
        DiscussionReply existing = new DiscussionReply();
        existing.setId(10L);
        existing.setUserId(100L);
        existing.setDiscussionId(1L);
        existing.setParentReplyId(null);
        existing.setIsTeacherReply(false);
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(existing);

        Mockito.when(discussionReplyService.updateReply(any(DiscussionReply.class)))
                .thenThrow(new RuntimeException("fail"));

        mockMvc.perform(put("/api/discussion-reply/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"content\":\"updated\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.startsWith("更新回复失败:")));
    }

    // deleteReply success
    @Test
    void deleteReply_success() throws Exception {
        mockLoginTrue();
        DiscussionReply reply = new DiscussionReply();
        reply.setId(10L);
        reply.setUserId(100L);
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(reply);

        mockMvc.perform(delete("/api/discussion-reply/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("回复删除成功"));
    }

    // deleteReply not logged in
    @Test
    void deleteReply_notLoggedIn() throws Exception {
        mockLoginFalse();
        mockMvc.perform(delete("/api/discussion-reply/10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    // deleteReply not found
    @Test
    void deleteReply_notFound() throws Exception {
        mockLoginTrue();
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(null);
        mockMvc.perform(delete("/api/discussion-reply/10"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("回复不存在"));
    }

    // deleteReply no permission
    @Test
    void deleteReply_noPermission() throws Exception {
        mockLoginTrue();
        DiscussionReply reply = new DiscussionReply();
        reply.setId(10L);
        reply.setUserId(200L);
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(reply);
        mockMvc.perform(delete("/api/discussion-reply/10"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("您没有权限删除此回复"));
    }

    // deleteReply service throws Exception
    @Test
    void deleteReply_serviceThrowsException() throws Exception {
        mockLoginTrue();
        DiscussionReply reply = new DiscussionReply();
        reply.setId(10L);
        reply.setUserId(100L);
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(reply);

        Mockito.doThrow(new RuntimeException("fail")).when(discussionReplyService).deleteReply(10L);

        mockMvc.perform(delete("/api/discussion-reply/10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("删除回复失败"));
    }

    // getReply success
    @Test
    void getReply_success() throws Exception {
        DiscussionReply reply = new DiscussionReply();
        reply.setId(10L);
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(reply);
        mockMvc.perform(get("/api/discussion-reply/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    // getReply not found
    @Test
    void getReply_notFound() throws Exception {
        Mockito.when(discussionReplyService.getReply(10L)).thenReturn(null);
        mockMvc.perform(get("/api/discussion-reply/10"))
                .andExpect(status().isNotFound());
    }

    // getRepliesByDiscussion success/fail
    @Test
    void getRepliesByDiscussion_success() throws Exception {
        List<DiscussionReply> list = Arrays.asList(new DiscussionReply(), new DiscussionReply());
        Mockito.when(discussionReplyService.getRepliesByDiscussion(1L)).thenReturn(list);
        mockMvc.perform(get("/api/discussion-reply/discussion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getRepliesByDiscussion_fail() throws Exception {
        Mockito.when(discussionReplyService.getRepliesByDiscussion(1L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/discussion-reply/discussion/1"))
                .andExpect(status().is5xxServerError());
    }

    // getRepliesByUser success/fail
    @Test
    void getRepliesByUser_success() throws Exception {
        List<DiscussionReply> list = Arrays.asList(new DiscussionReply(), new DiscussionReply());
        Mockito.when(discussionReplyService.getRepliesByUser(100L)).thenReturn(list);
        mockMvc.perform(get("/api/discussion-reply/user/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }
    @Test
    void getRepliesByUser_fail() throws Exception {
        Mockito.when(discussionReplyService.getRepliesByUser(100L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/discussion-reply/user/100"))
                .andExpect(status().is5xxServerError());
    }

    // getTopLevelReplies success/fail
    @Test
    void getTopLevelReplies_success() throws Exception {
        List<DiscussionReply> list = Arrays.asList(new DiscussionReply(), new DiscussionReply());
        Mockito.when(discussionReplyService.getTopLevelReplies(1L)).thenReturn(list);
        mockMvc.perform(get("/api/discussion-reply/discussion/1/top-level"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }
    @Test
    void getTopLevelReplies_fail() throws Exception {
        Mockito.when(discussionReplyService.getTopLevelReplies(1L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/discussion-reply/discussion/1/top-level"))
                .andExpect(status().is5xxServerError());
    }

    // getRepliesByParent success/fail
    @Test
    void getRepliesByParent_success() throws Exception {
        List<DiscussionReply> list = Arrays.asList(new DiscussionReply(), new DiscussionReply());
        Mockito.when(discussionReplyService.getRepliesByParent(2L)).thenReturn(list);
        mockMvc.perform(get("/api/discussion-reply/parent/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }
    @Test
    void getRepliesByParent_fail() throws Exception {
        Mockito.when(discussionReplyService.getRepliesByParent(2L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/discussion-reply/parent/2"))
                .andExpect(status().is5xxServerError());
    }

    // getTeacherReplies success/fail
    @Test
    void getTeacherReplies_success() throws Exception {
        List<DiscussionReply> list = Arrays.asList(new DiscussionReply(), new DiscussionReply());
        Mockito.when(discussionReplyService.getTeacherReplies(1L)).thenReturn(list);
        mockMvc.perform(get("/api/discussion-reply/discussion/1/teacher"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }
    @Test
    void getTeacherReplies_fail() throws Exception {
        Mockito.when(discussionReplyService.getTeacherReplies(1L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/discussion-reply/discussion/1/teacher"))
                .andExpect(status().is5xxServerError());
    }

    // countRepliesByDiscussion success/fail
    @Test
    void countRepliesByDiscussion_success() throws Exception {
        Mockito.when(discussionReplyService.countRepliesByDiscussion(1L)).thenReturn(11);
        mockMvc.perform(get("/api/discussion-reply/discussion/1/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("11"));
    }
    @Test
    void countRepliesByDiscussion_fail() throws Exception {
        Mockito.when(discussionReplyService.countRepliesByDiscussion(1L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/discussion-reply/discussion/1/count"))
                .andExpect(status().is5xxServerError());
    }

    // countRepliesByUser success/fail
    @Test
    void countRepliesByUser_success() throws Exception {
        Mockito.when(discussionReplyService.countRepliesByUser(100L)).thenReturn(5);
        mockMvc.perform(get("/api/discussion-reply/user/100/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }
    @Test
    void countRepliesByUser_fail() throws Exception {
        Mockito.when(discussionReplyService.countRepliesByUser(100L)).thenThrow(new RuntimeException("fail"));
        mockMvc.perform(get("/api/discussion-reply/user/100/count"))
                .andExpect(status().is5xxServerError());
    }

    // deleteRepliesByDiscussion success
    @Test
    void deleteRepliesByDiscussion_success() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setRole(User.UserRole.teacher);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);

        mockMvc.perform(delete("/api/discussion-reply/discussion/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("讨论的所有回复已删除"));
    }

    // deleteRepliesByDiscussion not logged in
    @Test
    void deleteRepliesByDiscussion_notLoggedIn() throws Exception {
        mockLoginFalse();
        mockMvc.perform(delete("/api/discussion-reply/discussion/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    // deleteRepliesByDiscussion user not found
    @Test
    void deleteRepliesByDiscussion_userNotFound() throws Exception {
        mockLoginTrue();
        Mockito.when(userService.findById(100L)).thenReturn(null);
        mockMvc.perform(delete("/api/discussion-reply/discussion/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("用户不存在"));
    }

    // deleteRepliesByDiscussion not teacher
    @Test
    void deleteRepliesByDiscussion_notTeacher() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setRole(User.UserRole.student);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        mockMvc.perform(delete("/api/discussion-reply/discussion/1"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("只有教师才能执行此操作"));
    }

    // deleteRepliesByDiscussion discussion not found
    @Test
    void deleteRepliesByDiscussion_discussionNotFound() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setRole(User.UserRole.teacher);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(null);
        mockMvc.perform(delete("/api/discussion-reply/discussion/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("讨论不存在"));
    }

    // deleteRepliesByDiscussion service throws Exception
    @Test
    void deleteRepliesByDiscussion_serviceThrowsException() throws Exception {
        mockLoginTrue();
        User user = new User();
        user.setId(100L);
        user.setRole(User.UserRole.teacher);
        Mockito.when(userService.findById(100L)).thenReturn(user);
        Discussion discussion = new Discussion();
        discussion.setId(1L);
        Mockito.when(discussionService.getDiscussion(1L)).thenReturn(discussion);
        Mockito.doThrow(new RuntimeException("fail")).when(discussionReplyService).deleteRepliesByDiscussion(1L);
        mockMvc.perform(delete("/api/discussion-reply/discussion/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.startsWith("删除回复失败:")));
    }
}