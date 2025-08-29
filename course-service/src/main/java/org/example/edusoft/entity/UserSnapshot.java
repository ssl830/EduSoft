package org.example.edusoft.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_snapshot")
public class UserSnapshot {
	@TableId(type = IdType.AUTO)
	private Long id;
	private Long userId;
	private String username;
	private String role;
	private String email;
}
