-- 修复notification表结构
USE `content-db`;

-- 删除旧表
DROP TABLE IF EXISTS `notification`;

-- 创建完整的notification表
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '通知标题',
  `message` text COLLATE utf8mb4_unicode_ci COMMENT '通知内容',
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '通知类型',
  `read_flag` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `related_id` bigint DEFAULT NULL COMMENT '关联ID',
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '关联类型',
  `priority` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'normal' COMMENT '优先级',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'active' COMMENT '状态',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `sender_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '发送者姓名',
  `sender_id` bigint DEFAULT NULL COMMENT '发送者ID',
  `course_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程名称',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `class_name` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '班级名称',
  `class_id` bigint DEFAULT NULL COMMENT '班级ID',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `type` (`type`),
  KEY `read_flag` (`read_flag`),
  KEY `related_type` (`related_type`),
  KEY `related_id` (`related_id`),
  KEY `status` (`status`),
  KEY `priority` (`priority`),
  KEY `course_id` (`course_id`),
  KEY `class_id` (`class_id`),
  KEY `sender_id` (`sender_id`),
  KEY `created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='通知表';

-- 插入测试数据
INSERT INTO `notification` (`user_id`, `title`, `message`, `type`, `read_flag`, `related_id`, `related_type`, `priority`, `status`, `sender_name`, `sender_id`, `course_name`, `course_id`, `class_name`, `class_id`) VALUES
(17, '系统通知', '欢迎使用讨论区功能！', 'SYSTEM', 0, NULL, 'SYSTEM', 'normal', 'active', '系统管理员', 1, NULL, NULL, NULL, NULL),
(17, '课程通知', 'Java程序设计课程新增讨论区功能', 'COURSE_NOTICE', 0, 1, 'COURSE', 'normal', 'active', '张老师', 100, 'Java程序设计', 1, '计算机科学1班', 1),
(17, '讨论回复通知', '李四回复了你的讨论「关于Java多线程的疑问」', 'DISCUSSION_REPLY', 0, 1, 'DISCUSSION', 'normal', 'active', '李四', 18, 'Java程序设计', 1, '计算机科学1班', 1),
(17, '作业通知', '老师发布了新的作业：讨论区功能实现', 'HOMEWORK', 0, 1, 'HOMEWORK', 'high', 'active', '张老师', 100, 'Java程序设计', 1, '计算机科学1班', 1),
(17, '放假通知', '今年寒假1.19才放啊啊啊', 'SYSTEM', 0, NULL, 'SYSTEM', 'high', 'active', '教务处', 999, NULL, NULL, NULL, NULL);

-- 显示插入的数据
SELECT * FROM `notification` WHERE `user_id` = 17;
