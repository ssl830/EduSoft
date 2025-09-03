-- 用户微服务数据库脚本
-- 基于原始courseplatform数据库提取的用户相关表结构

-- 设置字符集和时区
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `user-db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `user-db`;

-- 删除已存在的表（如果存在）
DROP TABLE IF EXISTS `user`;

-- 创建用户表（核心表）
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID（主键）',
  `user_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户登录ID',
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户姓名',
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '密码哈希值',
  `role` enum('student','teacher','tutor') COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户角色',
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '邮箱地址',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  KEY `idx_role` (`role`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 插入初始用户数据
INSERT INTO `user` (`user_id`, `username`, `password_hash`, `role`, `email`, `created_at`, `updated_at`) VALUES
('T001', '张老师', 'hashed_pwd1', 'teacher', 'teacher@example.com', '2025-07-08 06:08:19', '2025-07-08 06:08:19'),
('S001', '小明', 'hashed_pwd2', 'student', 'xiaoming@example.com', '2025-07-08 06:08:19', '2025-07-08 06:08:19'),
('S002', '小红', 'hashed_pwd3', 'student', 'xiaohong@example.com', '2025-07-08 06:08:19', '2025-07-08 06:08:19'),
('T002', 'Teacher2', 'bc1785c9eda2973c1157cbbb90a8b1f9', 'teacher', 'pengsquare82@gmail.com', '2025-07-08 06:09:39', '2025-07-08 06:09:39'),
('S003', 'User', 'bc1785c9eda2973c1157cbbb90a8b1f9', 'student', 'buaavolunteers@sina.com', '2025-07-08 06:10:10', '2025-07-08 06:10:10'),
('Admin1', 'Admin', 'bc1785c9eda2973c1157cbbb90a8b1f9', 'tutor', '23371112@buaa.edu.cn', '2025-07-08 06:11:41', '2025-07-08 06:11:41'),
('T003', 'Teacher2', 'bc1785c9eda2973c1157cbbb90a8b1f9', 'teacher', 'pengsquare82@gmail.com', '2025-07-08 15:04:32', '2025-07-08 15:04:32'),
('S004', 'S004', 'bc1785c9eda2973c1157cbbb90a8b1f9', 'student', 'pengsquare82@gmail.com', '2025-08-16 08:30:33', '2025-08-16 08:30:33'),
('S005', 'S005', 'bc1785c9eda2973c1157cbbb90a8b1f9', 'student', 'pengsquare82@gmail.com', '2025-08-16 08:31:01', '2025-08-16 08:31:01'),
('T100', 'T100', 'bc1785c9eda2973c1157cbbb90a8b1f9', 'teacher', 'buaavolunteers@sina.com', '2025-08-21 09:50:31', '2025-08-21 09:50:31');

-- 重置外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 显示创建的表信息
SHOW TABLES LIKE 'user%';

-- 显示用户表结构
DESCRIBE `user`;

-- 显示用户数据统计
SELECT 
    role,
    COUNT(*) as user_count
FROM `user` 
GROUP BY role;
