-- 测试数据脚本 - 为user_id=17的同学创建完整的讨论区功能测试数据
USE `content-db`;

-- 1. 创建讨论区表
CREATE TABLE IF NOT EXISTS `discussion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL COMMENT '讨论标题',
  `content` text NOT NULL COMMENT '讨论内容',
  `creator_id` bigint NOT NULL COMMENT '创建者ID',
  `creator_name` varchar(255) NOT NULL COMMENT '创建者姓名',
  `course_id` bigint DEFAULT NULL COMMENT '关联课程ID',
  `class_id` bigint DEFAULT NULL COMMENT '关联班级ID',
  `type` varchar(50) DEFAULT 'general' COMMENT '讨论类型',
  `view_count` int DEFAULT '0' COMMENT '查看次数',
  `reply_count` int DEFAULT '0' COMMENT '回复次数',
  `like_count` int DEFAULT '0' COMMENT '点赞次数',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `creator_id` (`creator_id`),
  KEY `course_id` (`course_id`),
  KEY `class_id` (`class_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论话题表';

-- 2. 创建讨论回复表
CREATE TABLE IF NOT EXISTS `discussion_reply` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `discussion_id` bigint NOT NULL COMMENT '讨论ID',
  `content` text NOT NULL COMMENT '回复内容',
  `replier_id` bigint NOT NULL COMMENT '回复者ID',
  `replier_name` varchar(255) NOT NULL COMMENT '回复者姓名',
  `parent_reply_id` bigint DEFAULT NULL COMMENT '父回复ID',
  `like_count` int DEFAULT '0' COMMENT '点赞次数',
  `status` varchar(20) DEFAULT 'active' COMMENT '状态',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `discussion_id` (`discussion_id`),
  KEY `replier_id` (`replier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='讨论回复表';

-- 3. 插入测试数据
INSERT INTO `discussion` (`title`, `content`, `creator_id`, `creator_name`, `course_id`, `class_id`, `type`) VALUES
('关于Java多线程的疑问', '在学习Java多线程时遇到了一些问题，想请教一下大家。', 17, '张三', 1, 1, 'question'),
('Spring Boot项目结构分享', '分享一下我的Spring Boot项目结构，欢迎大家提出建议。', 17, '张三', 1, 1, 'share'),
('微服务架构设计讨论', '微服务拆分的原则和注意事项有哪些？', 17, '张三', 2, 2, 'discussion');

INSERT INTO `discussion_reply` (`discussion_id`, `content`, `replier_id`, `replier_name`) VALUES
(1, '线程池主要适用于需要频繁创建和销毁线程的场景。', 18, '李四'),
(1, '补充一下，要注意线程池的关闭，避免内存泄漏。', 19, '王五'),
(2, '项目结构很清晰，建议添加统一的异常处理机制。', 20, '赵六');

-- 4. 插入通知数据
INSERT INTO `notification` (`user_id`, `title`, `message`, `type`, `read_flag`, `created_at`, `related_id`, `related_type`) VALUES
(17, '讨论回复通知', '李四回复了你的讨论「关于Java多线程的疑问」', 'DISCUSSION_REPLY', 0, NOW(), 1, 'DISCUSSION'),
(17, '讨论回复通知', '王五回复了你的讨论「关于Java多线程的疑问」', 'DISCUSSION_REPLY', 0, NOW(), 1, 'DISCUSSION'),
(17, '系统通知', '欢迎使用讨论区功能！', 'SYSTEM', 0, NOW(), NULL, 'SYSTEM');

-- 5. 插入任务提醒数据
INSERT INTO `task_reminder` (`user_id`, `title`, `content`, `create_time`, `deadline`, `priority`, `completed`) VALUES
(17, '完成讨论区功能开发', '实现讨论区的增删改查功能', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), 'HIGH', 0),
(17, '学习Spring Boot', '深入学习Spring Boot框架', NOW(), DATE_ADD(NOW(), INTERVAL 14 DAY), 'MEDIUM', 0);

-- 6. 更新讨论表的回复数量
UPDATE `discussion` SET `reply_count` = 2 WHERE `id` = 1;
UPDATE `discussion` SET `reply_count` = 1 WHERE `id` = 2;
