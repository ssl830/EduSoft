package org.example.edusoft.user.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.util.SaResult;

import org.example.edusoft.user.entity.User;    
import org.example.edusoft.user.dto.UserUpdate;
import org.example.edusoft.user.service.UserService;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    // 使用 Sa-Token 提供的密码加密工具
    private static final String SALT = "edusoft"; // 加盐值，建议配置在配置文件中

    @SaIgnore
    @PostMapping("/register")
    public SaResult register(@Valid @RequestBody User user) {
        System.out.println("开始处理注册请求: " + user.getUsername());
        try {
            // 检查userid是否已存在
            if (userService.findByUserId(user.getUserId()) != null) {
               return SaResult.error("用户ID已存在，请选择其他用户ID");
            }
            // 密码加密
            String encryptedPassword = SaSecureUtil.md5BySalt(user.getPasswordHash(), SALT);
            user.setPasswordHash(encryptedPassword);
            // 设置创建时间和更新时间
            LocalDateTime now = LocalDateTime.now();
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            // 保存用户
            userService.save(user);
            return SaResult.ok("注册成功");
        } catch (Exception e) {
            System.out.println("注册失败: " + e.getMessage());
            if (e.getMessage().contains("Duplicate entry") && e.getMessage().contains("userid")) {
               return SaResult.error("用户ID已存在，请选择其他用户ID");
            }
            return SaResult.error("注册失败：" + e.getMessage());
        }
    }

    @SaIgnore
    @PostMapping("/login")
    public SaResult login(@RequestParam String userId, @RequestParam String password) {
        try {
            System.out.println("开始处理登录请求: username=" + userId); // 添加日志
            // 查找用户
            User user = userService.findByUserId(userId);
            System.out.println("查询用户结果: " + (user != null ? "找到用户" : "用户不存在")); // 添加日志
            if (user != null) {
                System.out.println("用户信息: " + user); // 打印用户信息
            }
            if (user == null) {
                return SaResult.error("用户名或密码错误");
            }
            // 打印密码哈希值，方便调试
            System.out.println("数据库中的密码哈希: " + user.getPasswordHash());
            String encryptedPassword = SaSecureUtil.md5BySalt(password, SALT);
            System.out.println("输入密码的哈希: " + encryptedPassword);
            if (!encryptedPassword.equals(user.getPasswordHash())) {
                return SaResult.error("用户名或密码错误");
            }
            // 登录成功
            StpUtil.login(user.getId());
            String token = StpUtil.getTokenValue();
            // 创建返回的用户信息，排除敏感字段
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", user.getId());
            userInfo.put("username", user.getUsername());
            userInfo.put("userid", user.getUserId());
            userInfo.put("email", user.getEmail());
            userInfo.put("role", user.getRole());
           // 构建返回数据
            Map<String, Object> data = new HashMap<>();
           data.put("token", token);
           data.put("userInfo", userInfo);
           return SaResult.ok("登录成功").setData(data);
        } catch (Exception e) {
            e.printStackTrace(); // 打印完整堆栈
            System.out.println("登录失败，详细错误: " + e.getMessage());
            return SaResult.error("登录失败：系统错误");
        }
    }

    // 用户登出
    @PostMapping("/logout")
    public SaResult logout() {
        try {
            // 判断是否已登录
            if (!StpUtil.isLogin()) {
                return SaResult.error("用户未登录");
            }
            // 执行登出
            StpUtil.logout();
            return SaResult.ok("退出登录成功");
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("退出登录失败：" + e.getMessage());
        }
    }

    // 根据userId获取用户信息 - 符合README规范
    @GetMapping("/{userId}")
    public SaResult getUserByUserId(@PathVariable String userId) {
        try {
            // 检查是否登录
            StpUtil.checkLogin();
            // 获取用户信息
            User user = userService.findByUserId(userId);
            if (user == null) {
                return SaResult.error("用户不存在");
            }
            // 返回用户信息，但不返回密码
            user.setPasswordHash(null);
            return SaResult.ok("获取成功").setData(user);
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("获取用户信息失败：" + e.getMessage());
        }
    }

    // 根据数据库ID获取用户信息 - 专门为课程微服务提供
    @GetMapping("/id/{id}")
    public SaResult getUserById(@PathVariable Long id) {
        try {
            // 检查是否登录
            StpUtil.checkLogin();
            // 获取用户信息
            User user = userService.findById(id);
            if (user == null) {
                return SaResult.error("用户不存在");
            }
            // 返回用户信息，但不返回密码
            user.setPasswordHash(null);
            return SaResult.ok("获取成功").setData(user);
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("获取用户信息失败：" + e.getMessage());
        }
    }

    // 获取当前登录用户信息
    @GetMapping("/info")
    public SaResult getUserInfo() {
        try {
            // 检查是否登录
            StpUtil.checkLogin();
            // 获取用户ID并转换为Long类型
            String loginIdStr = StpUtil.getLoginId().toString();
            Long userId = Long.parseLong(loginIdStr);
            // 获取用户信息
            User user = userService.findById(userId);
            if (user == null) {
                return SaResult.error("用户不存在");
            }
            // 返回用户信息，但不返回密码
            user.setPasswordHash(null);
            return SaResult.ok("获取成功").setData(user);
        } catch (NumberFormatException e) {
            return SaResult.error("用户ID格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("获取用户信息失败：" + e.getMessage());
        }
    }

    // 微服务间token验证接口 - 专门为其他微服务提供
    @SaIgnore
    @GetMapping("/validate")
    public SaResult validateToken(@RequestHeader("satoken") String token) {
        try {
            // 手动设置token到当前会话
            StpUtil.setTokenValue(token);
            
            // 检查是否登录
            if (!StpUtil.isLogin()) {
                return SaResult.error("token无效");
            }
            
            // 获取用户ID并转换为Long类型
            String loginIdStr = StpUtil.getLoginId().toString();
            Long userId = Long.parseLong(loginIdStr);
            
            // 获取用户信息
            User user = userService.findById(userId);
            if (user == null) {
                return SaResult.error("用户不存在");
            }
            
            // 返回用户信息，但不返回密码
            user.setPasswordHash(null);
            return SaResult.ok("验证成功").setData(user);
        } catch (NumberFormatException e) {
            return SaResult.error("用户ID格式错误");
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("token验证失败：" + e.getMessage());
        }
    }

    // 更新用户信息 - 修改为PUT方法，符合README规范
    @PutMapping("/{userId}")
    public SaResult updateUserInfo(@PathVariable String userId, @Valid @RequestBody UserUpdate updateDTO) {
        try {
            // 检查登录状态
            StpUtil.checkLogin();
            // 获取当前用户
            String loginIdStr = StpUtil.getLoginId().toString();
            Long currentUserId = Long.parseLong(loginIdStr);
            
            // 根据userId查找用户
            User currentUser = userService.findByUserId(userId);
            if (currentUser == null) {
                return SaResult.error("用户不存在");
            }
            
            // 检查权限：只能更新自己的信息
            if (!currentUserId.equals(currentUser.getId())) {
                return SaResult.error("无权限更新其他用户信息");
            }
            
            // 只更新允许修改的字段
            if (updateDTO.getUsername() != null) {
                currentUser.setUsername(updateDTO.getUsername());
            }
            if (updateDTO.getEmail() != null) {
                currentUser.setEmail(updateDTO.getEmail());
            }
            // 更新时间
            currentUser.setUpdatedAt(LocalDateTime.now());
            // 保存更新
            userService.update(currentUser);
            return SaResult.ok("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("更新失败：" + e.getMessage());
        }
    }

    // 修改密码接口
    @PostMapping("/changePassword")
    public SaResult changePassword(@RequestParam String oldPassword, @RequestParam String newPassword) {
        try {
            // 检查登录状态
            StpUtil.checkLogin();
            // 获取当前用户ID并正确转换类型
            String loginIdStr = StpUtil.getLoginId().toString();
            Long currentUserId = Long.parseLong(loginIdStr);
            // 获取当前用户
            User currentUser = userService.findById(currentUserId);
            if (currentUser == null) {
                return SaResult.error("用户不存在");
            }
            // 验证旧密码
            String oldPasswordHash = SaSecureUtil.md5BySalt(oldPassword, SALT);
            if (!oldPasswordHash.equals(currentUser.getPasswordHash())) {
                return SaResult.error("原密码错误");
            }
            // 更新新密码
            String newPasswordHash = SaSecureUtil.md5BySalt(newPassword, SALT);
            // 直接更新密码
            userService.updatePassword(currentUser.getUserId(), newPasswordHash);
            return SaResult.ok("密码修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("密码修改失败：" + e.getMessage());
        }
    }

    // 注销账号接口
    @PostMapping("/deactivate")
    public SaResult deactivateAccount(@RequestParam String password) {
        try {
            // 获取当前登录用户
            Long loginId = StpUtil.getLoginIdAsLong();
            User currentUser = userService.findById(loginId);
            if (currentUser == null) {
                return SaResult.error("用户不存在");
            }
            // 验证密码
            String encryptedPassword = SaSecureUtil.md5BySalt(password, SALT);
            if (!encryptedPassword.equals(currentUser.getPasswordHash())) {
                return SaResult.error("密码错误");
            }
            // 密码验证通过，执行注销
            userService.deactivateAccount(currentUser.getId());
            // 注销成功后，清除登录状态
            StpUtil.logout();
            return SaResult.ok("账号已注销");

        } catch (Exception e) {
            e.printStackTrace();
            return SaResult.error("注销失败：" + e.getMessage());
        }
    }

    // 获取全部老师信息
    @GetMapping("/teachers")
    public SaResult getAllTeachers() {
        try {
            List<User> teachers = userService.getAllTeachers();
            if (teachers == null || teachers.isEmpty()) {
                return SaResult.error("老师不存在");
            }
            return SaResult.ok("获取老师列表成功").setData(teachers);
        } catch (Exception e) {
            return SaResult.error("获取老师列表失败：" + e.getMessage());
        }
    }

    // 获取全部学生信息
    @GetMapping("/students")
    public SaResult getAllStudents() {
        try {
            List<User> students = userService.getAllStudents();
            if (students == null || students.isEmpty()) {
                return SaResult.error("学生不存在");
            }
            return SaResult.ok("获取学生列表成功").setData(students);
        } catch (Exception e) {
            return SaResult.error("获取学生列表失败：" + e.getMessage());
        }
    }

    @GetMapping("/tutor")
    public SaResult getAllTutors() {
        try {
            List<User> tutors = userService.getAllTutors();
            if (tutors == null || tutors.isEmpty()) {
                return SaResult.error("管理员不存在");
            }
            return SaResult.ok("获取管理员列表成功").setData(tutors);
        } catch (Exception e) {
            return SaResult.error("获取管理员列表失败：" + e.getMessage());
        }
    }

    // 删除用户 - 符合README规范
    @DeleteMapping("/{id}")
    public SaResult deleteUser(@PathVariable Long id) {
        try {
            // 检查登录状态
            StpUtil.checkLogin();
            // 获取当前用户ID
            String loginIdStr = StpUtil.getLoginId().toString();
            Long currentUserId = Long.parseLong(loginIdStr);
            
            // 检查权限：只能删除自己的账号
            if (!currentUserId.equals(id)) {
                return SaResult.error("无权限删除其他用户");
            }
            
            userService.deleteById(id);
            return SaResult.ok("删除用户成功");
        } catch (Exception e) {
            return SaResult.error("删除用户失败：" + e.getMessage());
        }
    }
}
