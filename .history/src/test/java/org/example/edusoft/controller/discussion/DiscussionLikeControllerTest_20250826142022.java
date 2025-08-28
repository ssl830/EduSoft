package org.example.edusoft.controller.discussion;

import org.example.edusoft.entity.discussion.DiscussionLike;
import org.example.edusoft.service.discussion.DiscussionLikeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
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

@WebMvcTest(DiscussionLikeController.class)
class DiscussionLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DiscussionLikeService discussionLikeService;

    // Utility: mock static StpUtil for login
    void mockLoginSuccess() {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(true);
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::getLoginIdAsLong).thenReturn(42L);
    }

    void mockLoginFail() {
        Mockito.mockStatic(cn.dev33.satoken.stp.StpUtil.class)
                .when(cn.dev33.satoken.stp.StpUtil::isLogin).thenReturn(false);
    }

    // 1. likeDiscussion
    @Test
    void likeDiscussion_success() throws Exception {
        mockLoginSuccess();
        DiscussionLike like = new DiscussionLike();
        like.setId(1L);
        Mockito.when(discussionLikeService.likeDiscussion(10L, 42L)).thenReturn(like);

        mockMvc.perform(post("/api/discussion-like/discussion/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void likeDiscussion_notLoggedIn() throws Exception {
        mockLoginFail();

        mockMvc.perform(post("/api/discussion-like/discussion/10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    @Test
    void likeDiscussion_illegalArgument() throws Exception {
        mockLoginSuccess();
        Mockito.when(discussionLikeService.likeDiscussion(10L, 42L))
                .thenThrow(new IllegalArgumentException("不能重复点赞"));

        mockMvc.perform(post("/api/discussion-like/discussion/10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("不能重复点赞"));
    }

    // 2. unlikeDiscussion
    @Test
    void unlikeDiscussion_success() throws Exception {
        mockLoginSuccess();

        mockMvc.perform(delete("/api/discussion-like/discussion/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("取消点赞成功"));
    }

    @Test
    void unlikeDiscussion_notLoggedIn() throws Exception {
        mockLoginFail();

        mockMvc.perform(delete("/api/discussion-like/discussion/10"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("请先登录"));
    }

    // 3. getLikesByDiscussion
    @Test
    void getLikesByDiscussion_success() throws Exception {
        List<DiscussionLike> likes = Arrays.asList(new DiscussionLike(), new DiscussionLike());
        Mockito.when(discussionLikeService.getLikesByDiscussion(10L)).thenReturn(likes);

        mockMvc.perform(get("/api/discussion-like/discussion/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getLikesByDiscussion_fail() throws Exception {
        Mockito.when(discussionLikeService.getLikesByDiscussion(10L)).thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/api/discussion-like/discussion/10"))
                .andExpect(status().is5xxServerError());
    }

    // 4. getLikesByUser
    @Test
    void getLikesByUser_success() throws Exception {
        List<DiscussionLike> likes = Arrays.asList(new DiscussionLike(), new DiscussionLike());
        Mockito.when(discussionLikeService.getLikesByUser(21L)).thenReturn(likes);

        mockMvc.perform(get("/api/discussion-like/user/21"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    void getLikesByUser_fail() throws Exception {
        Mockito.when(discussionLikeService.getLikesByUser(21L)).thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/api/discussion-like/user/21"))
                .andExpect(status().is5xxServerError());
    }

    // 5. countLikesByDiscussion
    @Test
    void countLikesByDiscussion_success() throws Exception {
        Mockito.when(discussionLikeService.countLikesByDiscussion(10L)).thenReturn(5);

        mockMvc.perform(get("/api/discussion-like/discussion/10/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("5"));
    }

    @Test
    void countLikesByDiscussion_fail() throws Exception {
        Mockito.when(discussionLikeService.countLikesByDiscussion(10L)).thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/api/discussion-like/discussion/10/count"))
                .andExpect(status().is5xxServerError());
    }

    // 6. countLikesByUser
    @Test
    void countLikesByUser_success() throws Exception {
        Mockito.when(discussionLikeService.countLikesByUser(21L)).thenReturn(7);

        mockMvc.perform(get("/api/discussion-like/user/21/count"))
                .andExpect(status().isOk())
                .andExpect(content().string("7"));
    }

    @Test
    void countLikesByUser_fail() throws Exception {
        Mockito.when(discussionLikeService.countLikesByUser(21L)).thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/api/discussion-like/user/21/count"))
                .andExpect(status().is5xxServerError());
    }

    // 7. hasLiked
    @Test
    void hasLiked_success_true() throws Exception {
        mockLoginSuccess();
        Mockito.when(discussionLikeService.hasLiked(10L, 42L)).thenReturn(true);

        mockMvc.perform(get("/api/discussion-like/discussion/10/check"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void hasLiked_success_false() throws Exception {
        mockLoginSuccess();
        Mockito.when(discussionLikeService.hasLiked(10L, 42L)).thenReturn(false);

        mockMvc.perform(get("/api/discussion-like/discussion/10/check"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void hasLiked_fail() throws Exception {
        mockLoginSuccess();
        Mockito.when(discussionLikeService.hasLiked(10L, 42L)).thenThrow(new RuntimeException("error"));

        mockMvc.perform(get("/api/discussion-like/discussion/10/check"))
                .andExpect(status().is5xxServerError());
    }
}