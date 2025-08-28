package org.example.edusoft.user.entity;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor  // 添加无参构造函数
@AllArgsConstructor // 添加全参构造函数
public class User {

    private Long id;

    @NotBlank(message = "用户ID不能为空")
    @Size(min = 3, max = 15, message = "用户ID长度必须在3-15个字符之间")
    private String userId;

    @NotBlank(message = "姓名不能为空")
    @Size(min = 2, max = 50, message = "姓名长度必须在1-50个字符之间")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6个字符")
    private String passwordHash;

    @NotNull(message = "角色不能为空")
    private UserRole role;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    
    public enum UserRole {
        student,    // 使用大写，符合Java枚举命名规范
        teacher,
        tutor
    }
}