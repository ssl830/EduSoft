-- 创建 course_db 数据库并切换到该数据库
CREATE DATABASE IF NOT EXISTS course_db;
USE course_db;

DROP TABLE IF EXISTS `course`;
CREATE TABLE `course` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `teacher_id` bigint NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `outline` text COLLATE utf8mb4_unicode_ci,
  `objective` text COLLATE utf8mb4_unicode_ci,
  `assessment` text COLLATE utf8mb4_unicode_ci,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `class`;
CREATE TABLE `class` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `class_code` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `class_code` (`class_code`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `class_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `classuser`;
CREATE TABLE `classuser` (
  `class_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`class_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `classuser_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `courseclass`;
CREATE TABLE `courseclass` (
  `course_id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`class_id`,`course_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `courseclass_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE,
  CONSTRAINT `courseclass_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `coursesection`;
CREATE TABLE `coursesection` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `coursesection_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


DROP TABLE IF EXISTS `import_record`;
CREATE TABLE `import_record` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `class_id` bigint NOT NULL,
  `operator_id` bigint NOT NULL,
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `total_count` int NOT NULL,
  `success_count` int NOT NULL,
  `fail_count` int NOT NULL,
  `fail_reason` text COLLATE utf8mb4_unicode_ci,
  `import_time` datetime NOT NULL,
  `import_type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `class_id` (`class_id`),
  KEY `operator_id` (`operator_id`),
  CONSTRAINT `import_record_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

USE course_db;

-- 插入课程数据
INSERT INTO `course` (`id`,`teacher_id`, `name`, `code`, `outline`, `objective`, `assessment`) VALUES
(1,14, '计算机科学导论', 'CS101', '本课程介绍计算机科学的基本概念，包括编程、算法和数据结构', '使学生掌握基本的编程技能和计算思维', '平时作业30%，期中考试30%，期末考试40%'),
(2,14, '数据库系统', 'CS202', '学习关系数据库设计、SQL语言和数据库管理系统', '培养学生设计和管理数据库系统的能力', '实验报告40%，项目30%，期末考试30%'),
(3,14, 'Web开发技术', 'CS303', '学习前端和后端Web开发技术，包括HTML/CSS/JavaScript和服务器端编程', '使学生能够开发完整的Web应用程序', '平时作业30%，小组项目40%，期末展示30%');

-- 插入班级数据
INSERT INTO `class` (`course_id`, `name`, `class_code`) VALUES
(1, '计算机科学导论-2023秋季班', 'CS101-F23'),
(1, '计算机科学导论-2024春季班', 'CS101-S24'),
(2, '数据库系统-2023秋季班', 'CS202-F23'),
(3, 'Web开发技术-2024春季班', 'CS303-S24');

-- 插入课程章节数据
INSERT INTO `coursesection` (`course_id`, `title`, `sort_order`) VALUES
(1, '编程基础', 1),
(1, '控制结构', 2),
(1, '函数和模块', 3),
(2, '关系数据库概念', 1),
(2, 'SQL语言', 2),
(2, '数据库设计', 3),
(3, 'HTML和CSS基础', 1),
(3, 'JavaScript编程', 2),
(3, '服务器端开发', 3);