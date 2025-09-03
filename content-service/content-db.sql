-- 内容服务数据库脚本

DROP DATABASE IF EXISTS `content-db`;
CREATE DATABASE IF NOT EXISTS `content-db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `content-db`;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

DROP TABLE IF EXISTS `learning_progress`;
DROP TABLE IF EXISTS `homework_submission`;
DROP TABLE IF EXISTS `import_record`;
DROP TABLE IF EXISTS `file_node`;
DROP TABLE IF EXISTS `notification`;
DROP TABLE IF EXISTS `progress`;
DROP TABLE IF EXISTS `teaching_resource`;
DROP TABLE IF EXISTS `homework`;
--
-- Table structure for table `file_node`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `file_node` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_dir` tinyint(1) NOT NULL DEFAULT '0',
  `parent_id` bigint DEFAULT NULL,
  `course_id` bigint NOT NULL,
  `class_id` bigint DEFAULT NULL,
  `uploader_id` bigint NOT NULL,
  `sectiondir_id` bigint DEFAULT '-1',
  `file_type` enum('VIDEO','PPT','CODE','PDF','OTHER','WORD') COLLATE utf8mb4_unicode_ci NOT NULL,
  `section_id` bigint DEFAULT '-1',
  `last_file_version` bigint NOT NULL DEFAULT '0',
  `is_current_version` tinyint(1) NOT NULL DEFAULT '1',
  `file_size` bigint NOT NULL,
  `visibility` enum('PUBLIC','PRIVATE','CLASS_ONLY') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'CLASS_ONLY',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `file_url` varchar(1024) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `file_version` int DEFAULT NULL,
  `object_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  KEY `class_id` (`class_id`),
  KEY `uploader_id` (`uploader_id`),
  KEY `idx_parent_id` (`parent_id`),
  CONSTRAINT `file_node_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `file_node` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--
-- Table structure for table `homework`
--
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homework` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `class_id` bigint NOT NULL,
  `created_by` bigint NOT NULL,
  `attachment_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `object_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `deadline` datetime NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `homeworksubmission`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homework_submission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `homework_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `object_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `submitted_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `homework_id` (`homework_id`),
  CONSTRAINT `homework_submission_ibfk_1` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `import_record`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  KEY `operator_id` (`operator_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `teaching_resource`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teaching_resource` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `course_id` bigint NOT NULL,
  `chapter_id` bigint NOT NULL,
  `chapter_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `resource_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `object_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `duration` int DEFAULT NULL,
  `created_by` bigint NOT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;



--
-- Table structure for table `learning_progress`
-- 


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `learning_progress` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `resource_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `progress` decimal(5,2) NOT NULL,
  `last_position` int NOT NULL,
  `watch_count` int DEFAULT '0',
  `last_watch_time` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_student` (`resource_id`,`student_id`),
  CONSTRAINT `learning_progress_ibfk_1` FOREIGN KEY (`resource_id`) REFERENCES `teaching_resource` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `notification`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `message` text COLLATE utf8mb4_unicode_ci,
  `type` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `read_flag` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `related_id` bigint DEFAULT NULL,
  `related_type` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


--
-- Table structure for table `progress`
--


/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `progress` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `course_id` bigint NOT NULL,
  `section_id` bigint DEFAULT NULL,
  `completed` tinyint(1) DEFAULT '0',
  `completed_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `course_id` (`course_id`),
  KEY `section_id` (`section_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;



DROP TABLE IF EXISTS `discussion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `creator_id` bigint NOT NULL,
  `creator_num` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `is_pinned` tinyint(1) DEFAULT '0',
  `is_closed` tinyint(1) DEFAULT '0',
  `view_count` int DEFAULT '0',
  `reply_count` int DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  KEY `class_id` (`class_id`),
  KEY `creator_id` (`creator_id`),
  KEY `creator_num` (`creator_num`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


LOCK TABLES `discussion` WRITE;
/*!40000 ALTER TABLE `discussion` DISABLE KEYS */;
INSERT INTO `discussion` VALUES (1,1,1,4,'T002','dqhdiqhdip','uqhdiwudoqwhdfowd',0,0,0,0,'2025-07-17 08:31:43','2025-07-17 08:31:43'),(2,1,1,4,'T002','djwpiqjfdijwpf','diwphfwipqfhipqwhieqpfhiqepfie',0,0,0,0,'2025-07-17 08:32:02','2025-07-17 08:32:02'),(3,1,1,4,'T002','hello','hedoh😎\n````\n\ndihqwid\n\n````\n',0,0,0,0,'2025-08-11 03:39:10','2025-08-11 03:39:10'),(4,1,1,4,'T002','fwfqr21','wqew1e1ee2😅\n````\n\n代码内容\n\n````\n',0,0,4,1,'2025-08-11 03:53:57','2025-08-16 11:46:57'),(5,1,1,4,'T002','fqwfqefgqegqeqq','qqqqqqqqqqqq😊',0,0,10,2,'2025-08-11 04:12:23','2025-08-16 11:47:37'),(6,2,1,4,'T002','求助，这个问题啊巴拉巴拉','这里是求助的内容啊巴拉巴拉\n支持**md格式渲染**和表情包😁\n>真是太酷啦！！',0,0,16,2,'2025-08-16 12:41:03','2025-08-26 00:47:11'),(7,3,1,4,'T002','11111111111','🤣**粗体**\n*斜体*\n~~hello~~\n\n````C\n\nprintf(\"hello,world\");\n\n````\n',0,0,3,1,'2025-08-23 04:54:53','2025-08-23 04:55:25'),(8,2,1,4,'T002','1111111111111','😁··**11111111111111**',0,0,2,0,'2025-08-25 08:02:55','2025-08-25 08:03:09');
/*!40000 ALTER TABLE `discussion` ENABLE KEYS */;
UNLOCK TABLES;



DROP TABLE IF EXISTS `discussionreply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `discussionreply` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `discussion_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `user_num` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `parent_reply_id` bigint DEFAULT NULL,
  `is_teacher_reply` tinyint(1) DEFAULT '0',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `discussion_id` (`discussion_id`),
  KEY `user_id` (`user_id`),
  KEY `user_num` (`user_num`),
  KEY `parent_reply_id` (`parent_reply_id`),
  CONSTRAINT `discussionreply_ibfk_1` FOREIGN KEY (`discussion_id`) REFERENCES `discussion` (`id`) ON DELETE CASCADE,
  CONSTRAINT `discussionreply_ibfk_4` FOREIGN KEY (`parent_reply_id`) REFERENCES `discussionreply` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;


LOCK TABLES `discussionreply` WRITE;
/*!40000 ALTER TABLE `discussionreply` DISABLE KEYS */;
INSERT INTO `discussionreply` VALUES (1,4,4,'T002','byiguhi',NULL,1,'2025-08-11 04:11:52','2025-08-11 04:11:52'),(2,5,4,'T002','f2ygurhou32',NULL,1,'2025-08-11 04:19:28','2025-08-11 04:19:28'),(3,5,4,'T002','回复test1',NULL,1,'2025-08-11 04:19:58','2025-08-11 04:19:58'),(4,6,4,'T002','哇哇好厉害',NULL,1,'2025-08-16 12:41:29','2025-08-16 12:41:29'),(5,6,5,'S003','好棒好棒！！\n',NULL,0,'2025-08-16 12:51:14','2025-08-16 12:51:14'),(6,7,4,'T002','2222222',NULL,1,'2025-08-23 04:55:18','2025-08-23 04:55:18');
/*!40000 ALTER TABLE `discussionreply` ENABLE KEYS */;
UNLOCK TABLES;




