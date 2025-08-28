package org.example.edusoft.user.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String userId;
    private String username;
    private String role;  // 使用String类型表示角色
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 不包含密码字段，用于安全传输
}
