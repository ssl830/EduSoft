-- 数据库迁移脚本：从courseplatform迁移到user-db
-- 执行前请确保已备份数据

-- 1. 创建新的user-db数据库
CREATE DATABASE IF NOT EXISTS `user-db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 2. 切换到新数据库
USE `user-db`;

-- 3. 创建用户表结构
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
  UNIQUE KEY `uk_email` (`email`),
  KEY `idx_role` (`role`),
  KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 4. 迁移数据（如果原数据库存在）
-- 注意：执行前请确保courseplatform数据库中的user表存在
INSERT INTO `user-db`.`user` 
SELECT * FROM `courseplatform`.`user`;

-- 5. 验证迁移结果
SELECT 
    'user-db' as database_name,
    COUNT(*) as user_count
FROM `user-db`.`user`
UNION ALL
SELECT 
    'courseplatform' as database_name,
    COUNT(*) as user_count
FROM `courseplatform`.`user`;

-- 6. 显示迁移后的用户统计
SELECT 
    role,
    COUNT(*) as user_count
FROM `user-db`.`user` 
GROUP BY role;

-- 迁移完成后，可以删除原数据库中的user表（可选）
-- DROP TABLE IF EXISTS `courseplatform`.`user`;




