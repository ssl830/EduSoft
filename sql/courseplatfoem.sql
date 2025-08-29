-- MySQL dump 10.13  Distrib 8.0.39, for Win64 (x86_64)
--
-- Host: localhost    Database: courseplatform
-- ------------------------------------------------------
-- Server version	8.0.39

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `aiservicecalllog`
--

DROP TABLE IF EXISTS `aiservicecalllog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `aiservicecalllog` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint DEFAULT NULL,
  `endpoint` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `duration_ms` bigint NOT NULL,
  `call_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `error_message` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `aiservicecalllog_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=154 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aiservicecalllog`
--

LOCK TABLES `aiservicecalllog` WRITE;
/*!40000 ALTER TABLE `aiservicecalllog` DISABLE KEYS */;
INSERT INTO `aiservicecalllog` VALUES (1,4,'/embedding/upload',190901,'2025-07-08 06:20:36','success',NULL),(2,NULL,'/embedding/base_path',0,'2025-07-08 09:39:52','fail',NULL),(3,NULL,'/embedding/base_path/reset',102,'2025-07-08 09:43:26','success',NULL),(4,NULL,'/embedding/base_path',0,'2025-07-08 09:44:21','fail',NULL),(5,NULL,'/embedding/base_path',0,'2025-07-08 09:49:08','fail',NULL),(6,NULL,'/embedding/base_path/reset',10,'2025-07-08 09:51:39','success',NULL),(7,NULL,'/embedding/base_path',37,'2025-07-08 09:51:56','success',NULL),(8,NULL,'/embedding/base_path/reset',11,'2025-07-08 10:04:10','success',NULL),(9,NULL,'/embedding/base_path',25,'2025-07-08 10:04:33','success',NULL),(10,NULL,'/embedding/base_path/reset',22,'2025-07-08 10:26:41','success',NULL),(11,NULL,'/embedding/base_path',17,'2025-07-08 10:26:59','success',NULL),(12,NULL,'/embedding/base_path',26,'2025-07-08 10:27:17','success',NULL),(13,NULL,'/embedding/base_path/reset',13,'2025-07-08 10:34:00','success',NULL),(14,NULL,'/embedding/base_path',188,'2025-07-08 13:19:27','success',NULL),(15,NULL,'/embedding/base_path/reset',38,'2025-07-08 13:47:42','success',NULL),(16,NULL,'/embedding/base_path',46,'2025-07-08 13:53:55','success',NULL),(17,NULL,'/embedding/base_path/reset',26,'2025-07-08 14:09:20','success',NULL),(18,NULL,'/embedding/base_path/reset',16,'2025-07-08 14:54:41','success',NULL),(19,NULL,'/embedding/base_path',74,'2025-07-08 14:54:55','success',NULL),(20,NULL,'/embedding/base_path/reset',23,'2025-07-08 14:55:29','success',NULL),(21,NULL,'/embedding/base_path/reset',37,'2025-07-08 15:08:29','success',NULL),(22,NULL,'/embedding/base_path/reset',7,'2025-07-08 15:10:45','success',NULL),(23,NULL,'/embedding/base_path',30,'2025-07-08 15:14:23','success',NULL),(24,NULL,'/embedding/base_path',32,'2025-07-08 15:14:27','success',NULL),(25,NULL,'/embedding/base_path/reset',15,'2025-07-08 15:14:33','success',NULL),(26,4,'/embedding/upload',9488,'2025-07-08 15:21:50','success',NULL),(27,4,'/embedding/upload',8308,'2025-07-09 04:40:05','success',NULL),(28,4,'/rag/assistant',443,'2025-07-09 04:42:07','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"list index out of range\"}\"'),(29,NULL,'/embedding/base_path/reset',30,'2025-07-09 04:43:00','success',NULL),(30,NULL,'/embedding/base_path/reset',15,'2025-07-09 04:43:10','success',NULL),(31,4,'/rag/assistant',287,'2025-07-09 04:43:52','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"list index out of range\"}\"'),(32,NULL,'/embedding/base_path/reset',16,'2025-07-09 04:54:38','success',NULL),(33,4,'/rag/assistant',1710,'2025-07-09 04:55:12','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"list index out of range\"}\"'),(34,4,'/rag/assistant',9338,'2025-07-09 04:58:04','success',NULL),(35,4,'/rag/assistant',12673,'2025-07-09 05:02:17','success',NULL),(36,4,'/rag/assistant',24642,'2025-07-09 05:04:20','success',NULL),(37,5,'/rag/generate_student_exercise',10183,'2025-07-09 05:08:39','success',NULL),(38,5,'/rag/assistant',12986,'2025-07-09 05:10:10','success',NULL),(39,5,'/rag/assistant',17477,'2025-07-09 05:21:30','success',NULL),(40,5,'/rag/assistant',18686,'2025-07-09 05:23:23','success',NULL),(41,4,'/rag/assistant',13326,'2025-07-09 05:28:21','success',NULL),(42,NULL,'/embedding/base_path',41,'2025-07-09 05:29:48','success',NULL),(43,NULL,'/embedding/base_path',29,'2025-07-09 05:30:04','success',NULL),(44,4,'/rag/assistant',18046,'2025-07-09 05:32:16','success',NULL),(45,4,'/rag/assistant',16304,'2025-07-09 05:39:31','success',NULL),(46,4,'/rag/assistant',23341,'2025-07-09 05:40:50','success',NULL),(47,4,'/rag/assistant',24066,'2025-07-09 05:42:20','success',NULL),(48,NULL,'/embedding/base_path',70,'2025-07-09 05:43:26','success',NULL),(49,4,'/rag/assistant',25910,'2025-07-09 05:44:02','success',NULL),(50,4,'/rag/assistant',22870,'2025-07-09 05:52:10','success',NULL),(51,4,'/rag/assistant',28923,'2025-07-09 05:53:56','success',NULL),(52,NULL,'/embedding/base_path/reset',7,'2025-07-09 07:11:54','success',NULL),(53,4,'/embedding/upload',8248,'2025-07-11 07:41:34','fail','500 Internal Server Error on POST request for \"http://localhost:8000/embedding/upload\": \"{\"detail\":\"Connection error.\"}\"'),(54,4,'/embedding/upload',85195,'2025-07-11 07:47:15','success',NULL),(55,4,'/rag/assistant',17530,'2025-07-11 08:05:05','success',NULL),(56,4,'/rag/assistant',22175,'2025-07-11 08:08:26','success',NULL),(57,4,'/rag/generate_exercise',45561,'2025-07-11 08:10:57','success',NULL),(58,4,'/rag/generate',156781,'2025-07-11 08:14:07','success',NULL),(59,4,'/rag/detail',38979,'2025-07-11 08:17:55','success',NULL),(60,5,'/rag/generate_student_exercise',13467,'2025-07-11 08:22:36','success',NULL),(61,5,'/rag/generate_student_exercise',9005,'2025-07-13 14:36:30','success',NULL),(62,5,'/rag/generate_student_exercise',20654,'2025-07-13 15:05:12','success',NULL),(63,5,'/rag/generate_student_exercise',16010,'2025-07-13 15:10:40','success',NULL),(64,5,'/rag/generate_student_exercise',11451,'2025-07-13 15:13:46','success',NULL),(65,5,'/rag/generate_student_exercise',10088,'2025-07-13 15:22:42','success',NULL),(66,5,'/rag/generate_student_exercise',9462,'2025-07-13 15:27:01','success',NULL),(67,5,'/rag/generate_student_exercise',13444,'2025-07-13 15:34:43','success',NULL),(68,5,'/rag/generate_student_exercise',29797,'2025-07-13 15:44:19','success',NULL),(69,5,'/rag/generate_student_exercise',29804,'2025-07-13 15:48:17','success',NULL),(70,5,'/rag/evaluate_subjective',16229,'2025-07-13 15:48:51','success',NULL),(71,5,'/rag/generate_selected_student_exercise',25533,'2025-07-13 15:52:07','success',NULL),(72,4,'/rag/generate',199835,'2025-07-14 15:03:42','success',NULL),(73,4,'/rag/detail',46382,'2025-07-14 15:08:10','success',NULL),(74,NULL,'/embedding/base_path',28,'2025-07-14 15:12:30','success',NULL),(75,NULL,'/embedding/base_path/reset',98,'2025-07-14 15:28:50','success',NULL),(76,NULL,'/embedding/base_path/reset',58,'2025-07-14 15:28:58','success',NULL),(77,4,'/rag/generate_exercise',46194,'2025-07-14 15:37:30','success',NULL),(78,4,'/rag/generate_exercise',45444,'2025-07-14 15:39:19','success',NULL),(79,4,'/embedding/upload',275329,'2025-07-15 09:06:20','success',NULL),(80,4,'/embedding/upload',153459,'2025-07-15 09:10:33','success',NULL),(81,4,'/embedding/upload',705445,'2025-07-15 09:30:16','success',NULL),(82,4,'/embedding/upload',107610,'2025-07-15 09:33:37','success',NULL),(83,4,'/embedding/upload',83094,'2025-07-15 09:38:28','fail','500 Internal Server Error on POST request for \"http://localhost:8000/embedding/upload\": \"{\"detail\":\"Connection error.\"}\"'),(84,4,'/embedding/upload',87114,'2025-07-15 09:41:08','success',NULL),(85,4,'/embedding/upload',128721,'2025-07-15 09:43:32','success',NULL),(86,4,'/rag/evaluate_subjective',9631,'2025-07-16 14:38:24','success',NULL),(87,4,'/rag/evaluate_subjective',17894,'2025-07-16 14:38:32','success',NULL),(88,4,'/rag/evaluate_subjective',21245,'2025-07-16 14:40:31','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/evaluate_subjective\": \"{\"detail\":\"JSON解析失败，原始内容：由于未提供学生的具体答案，我将给出一个评估模板，该模板可以根据学生的实际答案进行修改。\\n\\n```json\\n{\\n    \\\"score\\\": 0.0,  // 实际得分将根据学生答案填写\\n    \\\"analysis\\\": {\\n        \\\"correct_points\\\": [  // 学生答对的要点\\n            // 例如：\\\"Correctly imported TensorFlow.js... 错误信息: Expecting value: line 1 column 1 (char 0)\"}\"'),(89,4,'/rag/evaluate_subjective',47269,'2025-07-16 14:40:57','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/evaluate_subjective\": \"{\"detail\":\"JSON解析失败，原始内容：由于未提供学生的具体答案，我将提供一个评估模板，该模板可以根据学生的实际答案进行修改。\\n\\n```json\\n{\\n  \\\"score\\\": 0.0, \\n  \\\"analysis\\\": {\\n    \\\"correct_points\\\": [], \\n    \\\"incorrect_points\\\": [], \\n    \\\"knowledge_points\\\": [\\n      \\\"Chapter 3: Introduction... 错误信息: Expecting value: line 1 column 1 (char 0)\"}\"'),(90,4,'/rag/evaluate_subjective',8180,'2025-07-16 14:41:47','success',NULL),(91,4,'/rag/evaluate_subjective',21730,'2025-07-16 14:42:00','success',NULL),(92,5,'/rag/evaluate_subjective',10774,'2025-07-16 15:02:21','success',NULL),(93,5,'/rag/evaluate_subjective',21781,'2025-07-16 15:02:32','success',NULL),(94,5,'/rag/evaluate_subjective',11652,'2025-07-16 15:04:51','success',NULL),(95,5,'/rag/evaluate_subjective',21538,'2025-07-16 15:05:01','success',NULL),(96,5,'/rag/evaluate_subjective',5016,'2025-07-16 15:23:05','success',NULL),(97,5,'/rag/evaluate_subjective',13615,'2025-07-16 15:23:14','success',NULL),(98,5,'/rag/assistant',17997,'2025-08-11 06:05:24','success',NULL),(99,5,'/rag/assistant',17222,'2025-08-11 06:06:26','success',NULL),(100,5,'/rag/assistant',19651,'2025-08-11 06:13:42','success',NULL),(101,5,'/rag/assistant',16052,'2025-08-11 06:14:36','success',NULL),(102,5,'/rag/assistant',14546,'2025-08-11 06:16:50','success',NULL),(103,NULL,'/embedding/base_path/reset',469210,'2025-08-11 07:33:32','success',NULL),(104,4,'/rag/assistant',7495,'2025-08-16 08:17:11','success',NULL),(105,4,'/rag/assistant',12977,'2025-08-16 08:18:30','success',NULL),(106,NULL,'/embedding/base_path',77,'2025-08-16 08:42:22','success',NULL),(107,4,'/rag/assistant',33630,'2025-08-16 08:43:31','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"JSON解析失败，原始内容：```json\\n{\\n  \\\"answer\\\": \\\"根据您即将进入大三阶段的学习，以及北京航空航天大学软件学院软件工程专业的培养方案，以下是一些建议的暑期预习方向：\\n\\n1. 针对专业课，您可以提前预习分布式系统导论、并行程序设计、云计算技术基础等课程，这些课程将在大三秋季学期开设。\\n2. 对于实践性较强的课程，如嵌入式软件设计、数值计算与算法、工业大数据技术等，可以提前了解相关技术背景和基础知识。\\n3.... 错误信息: Expecting value: line 1 column 1 (char 0)\"}\"'),(108,4,'/rag/assistant',19297,'2025-08-16 09:11:31','success',NULL),(109,NULL,'/embedding/base_path/reset',63,'2025-08-16 09:13:49','success',NULL),(110,NULL,'/embedding/base_path',33,'2025-08-16 09:13:55','success',NULL),(111,4,'/rag/assistant',46450,'2025-08-16 09:15:00','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"JSON解析失败，原始内容：{\\n    \\\"answer\\\": \\\"根据您即将进入大三的阶段以及软件工程专业的培养方案，以下是一些建议的暑期预习方向：\\n\\n1. 核心专业课程：您可以考虑预习如「分布式系统导论」、「并行程序设计」、「云计算技术基础」等课程，这些课程将是您大三学习中的重要组成部分。\\n\\n2. 理论与实践结合的课程：针对「嵌入式软件设计」、「数值计算与算法」等课程，预习理论知识的同时，注意寻找实践机会，增强实际操作能力。\\n... 错误信息: Invalid control character at: line 2 column 57 (char 58)\"}\"'),(112,NULL,'/embedding/base_path/reset',84059,'2025-08-16 09:26:08','success',NULL),(113,NULL,'/embedding/base_path',8831,'2025-08-16 09:26:08','success',NULL),(114,4,'/rag/assistant',22529,'2025-08-16 09:26:52','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"JSON解析失败，原始内容：{\\n    \\\"answer\\\": \\\"根据您即将进入大三阶段的学习，以及北京航空航天大学软件学院软件工程专业的培养方案，以下是一些建议的暑期预习方向：\\n\\n1. 核心专业课程：关注如「分布式系统导论」、「并行程序设计」、「云计算技术基础」等课程，这些课程将在大三开设，预习这些可以帮助您提前掌握概念，为课堂学习打下坚实基础。\\n\\n2. 高级编程技能：鉴于「Java企业级开发」和「软件测试与交付」等课程的重要...\"}\"'),(115,4,'/rag/assistant',92538,'2025-08-16 09:30:58','success',NULL),(116,NULL,'/embedding/base_path',105295,'2025-08-16 09:30:58','success',NULL),(117,4,'/rag/assistant',24141,'2025-08-16 09:33:46','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"JSON解析失败，原始内容：{\\n    \\\"answer\\\": \\\"根据您即将进入大三阶段的学习，以及软件工程专业课程清单，以下是对您暑期预习的建议：首先，可以提前学习如「分布式系统导论」和「并行程序设计」等课程，这些课程对于理解软件工程的高级概念非常重要。其次，针对「云计算技术基础」和「工业互联网技术基础」，您可以进行初步的了解，特别是云计算在实际应用中的案例分析。另外，考虑到「智能计算系统」和「图像处理和计算机视觉」的前沿性，... 错误信息: Invalid control character at: line 2 column 311 (char 312)\"}\"'),(118,NULL,'/embedding/base_path/reset',80,'2025-08-16 09:34:24','success',NULL),(119,4,'/rag/assistant',18201,'2025-08-16 09:35:38','success',NULL),(120,4,'/rag/assistant',9758,'2025-08-16 09:36:22','success',NULL),(121,4,'/rag/assistant',8154,'2025-08-16 10:25:48','success',NULL),(122,NULL,'/embedding/base_path',74,'2025-08-16 10:29:51','success',NULL),(123,4,'/rag/assistant',42350,'2025-08-16 10:31:11','success',NULL),(124,4,'/rag/assistant',37811,'2025-08-16 10:32:41','success',NULL),(125,4,'/rag/assistant',69638,'2025-08-16 10:50:16','success',NULL),(126,4,'/rag/assistant',14974,'2025-08-16 10:58:06','success',NULL),(127,NULL,'/embedding/base_path/reset',143,'2025-08-16 10:58:44','success',NULL),(128,NULL,'/embedding/base_path/reset',73,'2025-08-16 10:58:50','success',NULL),(129,5,'/rag/assistant',9919,'2025-08-16 12:53:31','success',NULL),(130,5,'/rag/assistant',7330,'2025-08-16 12:54:40','success',NULL),(131,4,'/rag/generate_section',63050,'2025-08-16 13:00:17','success',NULL),(132,4,'/rag/detail',26415,'2025-08-16 13:11:31','success',NULL),(133,4,'/rag/regenerate',62,'2025-08-16 13:19:23','fail','422 Unprocessable Entity on POST request for \"http://localhost:8000/rag/regenerate\": \"{\"detail\":[{\"type\":\"string_type\",\"loc\":[\"body\",\"practiceContent\"],\"msg\":\"Input should be a valid string\",\"input\":{\"任务1\":\"准备线性回归的数据集\",\"目标1\":\"学生能够正确导入数据集，理解数据的结构\",\"评价标准1\":\"数据集是否符合线性回归的要求，数据结构是否清晰\",\"任务2\":\"构建线性回归模型\",\"目标2\":\"学生能够通过TensorFlow.js API创建模型\",\"评价标准2\":\"模型结构是否正确，是否使用了适当的变量和操作\",\"任务3\":\"训练并评估模型\",\"目标3\":\"学生能够训练模型，并使用适当的方法进行评估\",\"评价标准3\":\"模型的训练过程是否正确执行，评估结果是否合理\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"teachingGuidance\"],\"msg\":\"Input should be a valid string\",\"input\":{\"重难点\":\"理解张量、变量、操作的概念，以及如何使用TensorFlow.js构建模型\",\"易错点\":\"张量的形状和维度理解，变量与普通张量的区分，操作的选择和使用\",\"互动建议\":\"鼓励学生提问，通过具体例子讲解抽象概念，组织小组讨论\",\"分层教学策略\":\"针对不同学生的学习进度，提供不同难度的练习题，对理解困难的学生提供额外辅导\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"timePlan\",0,\"content\"],\"msg\":\"Input should be a valid string\",\"input\":{\"教学目标\":\"使学生理解张量、变量、操作等基本概念\",\"活动安排\":\"1. 张量的定义与性质；2. 变量的作用及声明方式；3. 操作的分类与使用方法\",\"师生活动\":\"教师通过PPT或板书展示概念，学生跟随讲解做笔记\",\"互动方式\":\"提问与解答，学生展示理解并通过例子加深认识\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"timePlan\",1,\"content\"],\"msg\":\"Input should be a valid string\",\"input\":{\"教学目标\":\"通过案例使学生直观感受TensorFlow.js在实际项目中的应用\",\"活动安排\":\"分析手写数字识别案例的模型结构、数据处理和训练过程\",\"师生活动\":\"教师逐步讲解案例，学生观看并思考每个环节的作用和意义\",\"互动方式\":\"小组讨论案例中的关键点，教师指导并解答疑问\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"timePlan\",2,\"content\"],\"msg\":\"Input should be a valid string\",\"input\":{\"教学目标\":\"学生能够独立使用TensorFlow.js构建简单的线性回归模型\",\"活动安排\":\"1. 数据准备；2. 模型构建；3. 模型训练与评估\",\"师生活动\":\"学生在教师指导下逐步完成练习，教师巡回指导\",\"互动方式\":\"同伴互助，分享构建过程遇到的问题和解决方法\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"timePlan\",3,\"content\"],\"msg\":\"Input should be a valid string\",\"input\":{\"教学目标\":\"巩固本课时学习的知识点\",\"活动安排\":\"总结张量、变量、操作的概念及模型构建流程\",\"师生活动\":\"教师带领回顾本课时内容，学生反馈学习难点\",\"互动方式\":\"学生提出问题，教师解答并给出进一步学习的建议\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"}]}\"'),(134,4,'/rag/generate',592624,'2025-08-16 13:29:35','success',NULL),(135,4,'/rag/generate',71134,'2025-08-17 04:40:04','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/generate\": \"{\"detail\":\"JSON解析失败，原始内容：以下是针对“Linux开发实践”课程的教学方案，以JSON格式返回：\\n\\n```json\\n{\\n    \\\"lessons\\\": [\\n        {\\n            \\\"title\\\": \\\"TensorFlow.js基本概念与原理\\\",\\n            \\\"timePlan\\\": [\\n                {\\\"step\\\": \\\"导入\\\", \\\"minutes\\\": 5, \\\"content\\\": ... 错误信息: Expecting value: line 1 column 1 (char 0)\"}\"'),(136,4,'/rag/generate',91994,'2025-08-17 04:43:46','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/generate\": \"{\"detail\":\"JSON解析失败，原始片段：以下是根据您提供的信息生成的教学方案：\\n\\n```json\\n{\\n    \\\"lessons\\\": [\\n        {\\n            \\\"title\\\": \\\"TensorFlow.js基础概念与原理\\\",\\n            \\\"timePlan\\\": [\\n                {\\\"step\\\": \\\"导入\\\", \\\"minutes\\\": 5, \\\"content\\\": \\\"介绍TensorFlow.j... e1=Expecting value: line 1 column 1 (char 0) e2= e3=Expecting value: line 59 column 44 (char 3342)\"}\"'),(137,4,'/rag/generate',145881,'2025-08-17 05:02:00','success',NULL),(138,4,'/rag/detail',27182,'2025-08-17 05:03:31','success',NULL),(139,4,'/rag/generate',106371,'2025-08-17 05:20:41','success',NULL),(140,4,'/rag/detail',48041,'2025-08-17 05:22:07','success',NULL),(141,4,'/rag/assistant',16600,'2025-08-17 05:37:58','success',NULL),(142,4,'/rag/assistant',28491,'2025-08-17 06:55:20','success',NULL),(143,4,'/rag/assistant',27618,'2025-08-17 07:01:25','success',NULL),(144,4,'/rag/assistant',53548,'2025-08-17 07:03:13','success',NULL),(145,4,'/rag/generate_section',139652,'2025-08-17 07:06:48','success',NULL),(146,4,'/rag/detail',50980,'2025-08-17 07:09:22','success',NULL),(147,NULL,'/embedding/base_path',20,'2025-08-17 07:10:17','success',NULL),(148,4,'/embedding/upload',73285,'2025-08-17 07:11:43','success',NULL),(149,NULL,'/embedding/base_path/reset',37,'2025-08-17 07:13:07','success',NULL),(150,4,'/rag/generate_exercise',56096,'2025-08-17 07:14:58','success',NULL),(151,4,'/rag/assistant',24225,'2025-08-18 10:59:32','success',NULL),(152,4,'/rag/assistant',35924,'2025-08-18 11:00:38','success',NULL),(153,4,'/rag/assistant',88502,'2025-08-18 11:02:29','success',NULL);
/*!40000 ALTER TABLE `aiservicecalllog` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `answer`
--

DROP TABLE IF EXISTS `answer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `answer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `submission_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `answer_text` text COLLATE utf8mb4_unicode_ci,
  `is_judged` tinyint(1) DEFAULT '0',
  `correct` tinyint(1) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `sort_order` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `submission_id` (`submission_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `answer_ibfk_1` FOREIGN KEY (`submission_id`) REFERENCES `submission` (`id`) ON DELETE CASCADE,
  CONSTRAINT `answer_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=44 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `answer`
--

LOCK TABLES `answer` WRITE;
/*!40000 ALTER TABLE `answer` DISABLE KEYS */;
INSERT INTO `answer` VALUES (1,1,1,'1NF|2NF|3NF',1,1,10,1),(2,2,29,'A',1,1,5,0),(3,2,30,'A',1,1,1,1),(4,2,31,'A',1,1,1,2),(5,2,32,'不会',1,0,0,3),(6,2,34,'不会',1,0,1,4),(7,2,35,'不会',1,0,1,5),(8,3,29,'A',1,1,5,0),(9,3,30,'A',1,1,1,1),(10,3,31,'B',1,0,0,2),(11,3,32,'',1,0,0,3),(12,3,34,'',0,0,0,4),(13,3,35,'',0,0,0,5),(14,4,29,'B',1,0,0,0),(15,4,30,'C',1,0,0,1),(16,4,31,'C',1,0,0,2),(17,4,32,'不知道',1,0,0,3),(18,4,34,'const model=tf.sequential();\nmodel.add(tf.layer.dense({units:1,inputShape:[1]}));\nmodel.compile({loss:\'meanSquaredError\',optimizer: \'sgd\']}\'\n',0,0,0,4),(19,4,35,'const model=tf.sequential();',0,0,0,5),(20,5,29,'B',1,0,0,0),(21,5,30,'B',1,0,0,1),(22,5,31,'B',1,0,0,2),(23,5,32,'不知道',1,0,0,3),(24,5,34,'求求你给我一分吧，老师最好了',0,0,0,4),(25,5,35,'先定义一个模型，',0,0,0,5),(26,6,29,'A',1,1,5,0),(27,6,30,'A',1,1,1,1),(28,6,31,'A',1,1,1,2),(29,6,32,'不会',1,0,0,3),(30,6,34,'先定义一个模型，定义节点为1，MSE为loss,通过梯度下降法得到loss极小值，训练稳定后得到模型参数',0,0,0,4),(31,6,35,'老师菜菜捞捞，嘤嘤嘤',0,0,0,5),(32,7,29,'A',1,1,5,0),(33,7,30,'A',1,1,1,1),(34,7,31,'A',1,1,1,2),(35,7,32,'不知道',1,0,0,3),(36,7,34,'定义一个模型',0,0,0,4),(37,7,35,'老师菜菜姥姥',0,0,0,5),(38,8,29,'A',1,1,5,0),(39,8,30,'A',1,1,1,1),(40,8,31,'A',1,1,1,2),(41,8,32,'不会',1,0,0,3),(42,8,34,'要定义一个模型，设置输入维度2，输出维度1的网络，loss使用MSE,利用梯度下降法得到loss最低点，loss稳定的参数值即为所求',0,0,0,4),(43,8,35,'老师菜菜捞捞',0,0,0,5);
/*!40000 ALTER TABLE `answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `class`
--

DROP TABLE IF EXISTS `class`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `class`
--

LOCK TABLES `class` WRITE;
/*!40000 ALTER TABLE `class` DISABLE KEYS */;
INSERT INTO `class` VALUES (1,1,'数据库-01班','DBCLASS01'),(2,2,'Linux开发实践周一 14:00-15:30','123456');
/*!40000 ALTER TABLE `class` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `classuser`
--

DROP TABLE IF EXISTS `classuser`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `classuser` (
  `class_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`class_id`,`user_id`),
  KEY `user_id` (`user_id`),
  CONSTRAINT `classuser_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE,
  CONSTRAINT `classuser_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `classuser`
--

LOCK TABLES `classuser` WRITE;
/*!40000 ALTER TABLE `classuser` DISABLE KEYS */;
INSERT INTO `classuser` VALUES (1,2,'2025-07-08 06:08:19'),(1,3,'2025-07-08 06:08:19'),(2,2,'2025-07-08 06:14:21'),(2,3,'2025-07-08 06:14:21'),(2,4,'2025-07-08 06:13:32'),(2,5,'2025-07-08 06:14:21'),(2,9,'2025-08-16 08:31:20'),(2,10,'2025-08-16 08:31:58');
/*!40000 ALTER TABLE `classuser` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `course`
--

DROP TABLE IF EXISTS `course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
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
  UNIQUE KEY `code` (`code`),
  KEY `teacher_id` (`teacher_id`),
  CONSTRAINT `course_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `course`
--

LOCK TABLES `course` WRITE;
/*!40000 ALTER TABLE `course` DISABLE KEYS */;
INSERT INTO `course` VALUES (1,1,'数据库原理','CS101','介绍关系数据库','掌握SQL','期末考试+作业','2025-07-08 06:08:19'),(2,4,'Linux开发实践','123456','教授TensorFlow.js应用开发，TensorFlow Lite,与嵌入式Python开发等内容','1','考试','2025-07-08 06:13:18'),(3,4,'测试课程','12345678','这里面是课程大纲，ai可以根据大纲直接生成整个课程的教案','这里是教学目标','这里是考核方式，比如考试/考查','2025-08-16 08:41:52');
/*!40000 ALTER TABLE `course` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `courseclass`
--

DROP TABLE IF EXISTS `courseclass`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courseclass` (
  `course_id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `joined_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`class_id`,`course_id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `courseclass_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE,
  CONSTRAINT `courseclass_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courseclass`
--

LOCK TABLES `courseclass` WRITE;
/*!40000 ALTER TABLE `courseclass` DISABLE KEYS */;
INSERT INTO `courseclass` VALUES (1,1,'2025-07-08 06:08:19'),(2,2,'2025-07-08 06:13:32');
/*!40000 ALTER TABLE `courseclass` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `coursesection`
--

DROP TABLE IF EXISTS `coursesection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `coursesection` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `sort_order` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  CONSTRAINT `coursesection_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `coursesection`
--

LOCK TABLES `coursesection` WRITE;
/*!40000 ALTER TABLE `coursesection` DISABLE KEYS */;
INSERT INTO `coursesection` VALUES (1,1,'第一章 数据库基础',1),(2,1,'第二章 SQL语言',2),(3,2,'cp07-TensorFlow.js应用开发',1),(4,2,'cp08-TensorFlow Lite',2),(5,2,'cp09-嵌入式Python开发',3);
/*!40000 ALTER TABLE `coursesection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussion`
--

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
  KEY `creator_num` (`creator_num`),
  CONSTRAINT `discussion_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `discussion_ibfk_2` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE,
  CONSTRAINT `discussion_ibfk_3` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `discussion_ibfk_4` FOREIGN KEY (`creator_num`) REFERENCES `user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion`
--

LOCK TABLES `discussion` WRITE;
/*!40000 ALTER TABLE `discussion` DISABLE KEYS */;
INSERT INTO `discussion` VALUES (1,1,1,4,'T002','dqhdiqhdip','uqhdiwudoqwhdfowd',0,0,0,0,'2025-07-17 08:31:43','2025-07-17 08:31:43'),(2,1,1,4,'T002','djwpiqjfdijwpf','diwphfwipqfhipqwhieqpfhiqepfie',0,0,0,0,'2025-07-17 08:32:02','2025-07-17 08:32:02'),(3,1,1,4,'T002','hello','hedoh😎\n````\n\ndihqwid\n\n````\n',0,0,0,0,'2025-08-11 03:39:10','2025-08-11 03:39:10'),(4,1,1,4,'T002','fwfqr21','wqew1e1ee2😅\n````\n\n代码内容\n\n````\n',0,0,4,1,'2025-08-11 03:53:57','2025-08-16 11:46:57'),(5,1,1,4,'T002','fqwfqefgqegqeqq','qqqqqqqqqqqq😊',0,0,10,2,'2025-08-11 04:12:23','2025-08-16 11:47:37'),(6,2,1,4,'T002','求助，这个问题啊巴拉巴拉','这里是求助的内容啊巴拉巴拉\n支持**md格式渲染**和表情包😁\n>真是太酷啦！！',0,0,9,2,'2025-08-16 12:41:03','2025-08-16 12:51:14');
/*!40000 ALTER TABLE `discussion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `discussionreply`
--

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
  CONSTRAINT `discussionreply_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `discussionreply_ibfk_3` FOREIGN KEY (`user_num`) REFERENCES `user` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `discussionreply_ibfk_4` FOREIGN KEY (`parent_reply_id`) REFERENCES `discussionreply` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussionreply`
--

LOCK TABLES `discussionreply` WRITE;
/*!40000 ALTER TABLE `discussionreply` DISABLE KEYS */;
INSERT INTO `discussionreply` VALUES (1,4,4,'T002','byiguhi',NULL,1,'2025-08-11 04:11:52','2025-08-11 04:11:52'),(2,5,4,'T002','f2ygurhou32',NULL,1,'2025-08-11 04:19:28','2025-08-11 04:19:28'),(3,5,4,'T002','回复test1',NULL,1,'2025-08-11 04:19:58','2025-08-11 04:19:58'),(4,6,4,'T002','哇哇好厉害',NULL,1,'2025-08-16 12:41:29','2025-08-16 12:41:29'),(5,6,5,'S003','好棒好棒！！\n',NULL,0,'2025-08-16 12:51:14','2025-08-16 12:51:14');
/*!40000 ALTER TABLE `discussionreply` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favoritequestion`
--

DROP TABLE IF EXISTS `favoritequestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `favoritequestion` (
  `student_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  PRIMARY KEY (`student_id`,`question_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `favoritequestion_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `favoritequestion_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favoritequestion`
--

LOCK TABLES `favoritequestion` WRITE;
/*!40000 ALTER TABLE `favoritequestion` DISABLE KEYS */;
INSERT INTO `favoritequestion` VALUES (2,1);
/*!40000 ALTER TABLE `favoritequestion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `file_node`
--

DROP TABLE IF EXISTS `file_node`;
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
  `file_type` enum('VIDEO','PPT','CODE','PDF','OTHER') COLLATE utf8mb4_unicode_ci NOT NULL,
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
  CONSTRAINT `file_node_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `file_node` (`id`) ON DELETE CASCADE,
  CONSTRAINT `file_node_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `file_node_ibfk_3` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE,
  CONSTRAINT `file_node_ibfk_4` FOREIGN KEY (`uploader_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_node`
--

LOCK TABLES `file_node` WRITE;
/*!40000 ALTER TABLE `file_node` DISABLE KEYS */;
INSERT INTO `file_node` VALUES (1,'第一章-视频.mp4',0,NULL,1,1,1,-1,'VIDEO',-1,0,1,102400,'CLASS_ONLY','2025-07-08 14:08:19','2025-07-08 14:08:19','/files/1.mp4',1,'/course/1/section/1/第一章-视频.mp4'),(2,'课程-2班级-2',1,NULL,2,2,4,-1,'OTHER',-1,0,1,0,'CLASS_ONLY','2025-07-08 14:17:21','2025-07-08 14:17:21','',0,''),(3,'章节-3',1,2,2,2,4,3,'OTHER',-1,0,1,0,'CLASS_ONLY','2025-07-08 14:17:21','2025-07-08 14:17:21','',0,''),(4,'cp07-样章示例-TensorFlow.js应用开发',0,3,2,2,4,3,'OTHER',3,0,1,1025232,'PUBLIC','2025-07-08 14:17:22','2025-07-08 14:17:22','https://edusoft-file.oss-cn-beijing.aliyuncs.com/89eeaf7aec06421bac972f1cc4807447.docx',0,'89eeaf7aec06421bac972f1cc4807447.docx'),(5,'章节-4',1,2,2,2,4,4,'OTHER',-1,0,1,0,'CLASS_ONLY','2025-07-11 15:41:24','2025-07-11 15:41:24','',0,''),(6,'TensorFlow Lite大纲',0,5,2,2,4,4,'OTHER',4,0,1,1621363,'CLASS_ONLY','2025-07-11 15:41:25','2025-07-11 15:41:25','https://edusoft-file.oss-cn-beijing.aliyuncs.com/d4b5d71843d146ca8f2a9299e3fcd07a.docx',0,'d4b5d71843d146ca8f2a9299e3fcd07a.docx'),(7,'章节-5',1,2,2,2,4,5,'OTHER',-1,0,1,0,'CLASS_ONLY','2025-07-13 21:53:11','2025-07-13 21:53:11','',0,''),(8,'cp09-样章示例-嵌入式Python开发',0,7,2,2,4,5,'OTHER',5,0,1,16101528,'PUBLIC','2025-07-13 21:53:11','2025-07-13 21:53:11','https://edusoft-file.oss-cn-beijing.aliyuncs.com/f6852f2742c94ff1a5ad93696e02617d.docx',0,'f6852f2742c94ff1a5ad93696e02617d.docx'),(9,'02 TensorFlow Lite体系结构.mp4',0,5,2,2,4,4,'VIDEO',4,0,1,82336382,'CLASS_ONLY','2025-07-13 22:10:01','2025-07-13 22:10:01','https://edusoft-file.oss-cn-beijing.aliyuncs.com/f7b6042033714a2b8abf18f7c756a44c.mp4',0,'f7b6042033714a2b8abf18f7c756a44c.mp4'),(10,'03 TensorFlow Lite开发工作流程',0,5,2,2,4,4,'VIDEO',4,0,1,68326935,'PUBLIC','2025-07-13 22:12:58','2025-07-13 22:12:58','https://edusoft-file.oss-cn-beijing.aliyuncs.com/a5afa79e663f4a639ef3a6b929984423.mp4',0,'a5afa79e663f4a639ef3a6b929984423.mp4'),(11,'04 TensorFlow Lite实现花卉识别-1.mp4',0,5,2,2,4,4,'VIDEO',4,0,1,80497520,'PUBLIC','2025-07-13 22:20:13','2025-07-13 22:20:13','https://edusoft-file.oss-cn-beijing.aliyuncs.com/2b9a3831369340369f67eed79ed31751.mp4',0,'2b9a3831369340369f67eed79ed31751.mp4'),(12,'05 TensorFlow Lite实现花卉识别-2',0,5,2,2,4,4,'VIDEO',4,0,1,52814766,'PUBLIC','2025-07-13 22:25:24','2025-07-13 22:25:24','https://edusoft-file.oss-cn-beijing.aliyuncs.com/3f9814391899438b81ee0cbbf9706481.mp4',0,'3f9814391899438b81ee0cbbf9706481.mp4');
/*!40000 ALTER TABLE `file_node` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homework`
--

DROP TABLE IF EXISTS `homework`;
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
-- Dumping data for table `homework`
--

LOCK TABLES `homework` WRITE;
/*!40000 ALTER TABLE `homework` DISABLE KEYS */;
INSERT INTO `homework` VALUES (1,'第一次作业','请完成ER图设计',1,1,'/attachments/hw1.pdf','/homework/hw1.pdf','2025-07-15 14:08:19','2025-07-08 14:08:19','2025-07-08 14:08:19'),(2,'作业1，请下载作业附件，根据文件指导完成MIPS编程入门操作','认真仔细，上传文件命名为“学号-姓名-作业1.zip“，包含编程文件与心得感悟（附图片）',2,1,'https://edusoft-file.oss-cn-beijing.aliyuncs.com/b53fc997c3b9463e878931af5c0f658c.pdf','b53fc997c3b9463e878931af5c0f658c.pdf','2025-09-01 16:38:00','2025-08-16 16:38:58','2025-08-16 16:38:58');
/*!40000 ALTER TABLE `homework` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `homeworksubmission`
--

DROP TABLE IF EXISTS `homeworksubmission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `homeworksubmission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `homework_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `object_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `submitted_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `homework_id` (`homework_id`),
  CONSTRAINT `homeworksubmission_ibfk_1` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homeworksubmission`
--

LOCK TABLES `homeworksubmission` WRITE;
/*!40000 ALTER TABLE `homeworksubmission` DISABLE KEYS */;
INSERT INTO `homeworksubmission` VALUES (1,1,3,'/attachments/hw1_s2.pdf','/homework_submissions/s2_hw1.pdf','2025-07-08 14:08:19');
/*!40000 ALTER TABLE `homeworksubmission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `import_record`
--

DROP TABLE IF EXISTS `import_record`;
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
  KEY `operator_id` (`operator_id`),
  CONSTRAINT `import_record_ibfk_1` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE,
  CONSTRAINT `import_record_ibfk_2` FOREIGN KEY (`operator_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `import_record`
--

LOCK TABLES `import_record` WRITE;
/*!40000 ALTER TABLE `import_record` DISABLE KEYS */;
INSERT INTO `import_record` VALUES (1,1,1,'student_list.xlsx',2,2,0,NULL,'2025-07-08 14:08:19','excel'),(2,2,4,NULL,3,3,0,NULL,'2025-07-08 14:14:21','MANUAL'),(3,2,9,NULL,1,1,0,NULL,'2025-08-16 16:31:20','CODE_JOIN'),(4,2,10,NULL,1,1,0,NULL,'2025-08-16 16:31:58','CODE_JOIN');
/*!40000 ALTER TABLE `import_record` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `learning_progress`
--

DROP TABLE IF EXISTS `learning_progress`;
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
-- Dumping data for table `learning_progress`
--

LOCK TABLES `learning_progress` WRITE;
/*!40000 ALTER TABLE `learning_progress` DISABLE KEYS */;
INSERT INTO `learning_progress` VALUES (1,1,2,300.00,300,1,NULL,'2025-07-08 14:08:19','2025-07-08 14:08:19'),(2,1,3,120.00,120,1,NULL,'2025-07-08 14:08:19','2025-07-08 14:08:19'),(3,2,5,100.00,360,1,'2025-08-16 16:28:11','2025-08-16 16:28:11','2025-08-16 16:28:11');
/*!40000 ALTER TABLE `learning_progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
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
  KEY `user_id` (`user_id`),
  CONSTRAINT `notification_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,2,'练习通知','你有一个新练习待完成','practice',0,'2025-07-08 06:08:19',1,'Practice'),(2,2,'新练习提醒','老师发布了新的在线练习：练习1','PRACTICE',0,'2025-07-08 06:14:49',2,'PRACTICE'),(3,3,'新练习提醒','老师发布了新的在线练习：练习1','PRACTICE',0,'2025-07-08 06:14:49',2,'PRACTICE'),(4,4,'新练习提醒','老师发布了新的在线练习：练习1','PRACTICE',0,'2025-07-08 06:14:49',2,'PRACTICE'),(5,5,'新练习提醒','老师发布了新的在线练习：练习1','PRACTICE',1,'2025-07-08 06:14:49',2,'PRACTICE'),(6,2,'新练习提醒','老师发布了新的在线练习：Tensor Flow 练习1','PRACTICE',0,'2025-07-14 15:41:15',3,'PRACTICE'),(7,3,'新练习提醒','老师发布了新的在线练习：Tensor Flow 练习1','PRACTICE',0,'2025-07-14 15:41:15',3,'PRACTICE'),(8,4,'新练习提醒','老师发布了新的在线练习：Tensor Flow 练习1','PRACTICE',0,'2025-07-14 15:41:15',3,'PRACTICE'),(9,5,'新练习提醒','老师发布了新的在线练习：Tensor Flow 练习1','PRACTICE',1,'2025-07-14 15:41:15',3,'PRACTICE'),(10,2,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(11,3,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(12,4,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(13,5,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(14,9,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(15,10,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE');
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `practice`
--

DROP TABLE IF EXISTS `practice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `practice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `course_id` bigint NOT NULL,
  `class_id` bigint NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_time` datetime DEFAULT NULL,
  `end_time` datetime DEFAULT NULL,
  `allow_multiple_submission` tinyint(1) DEFAULT '1',
  `created_by` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `course_id` (`course_id`),
  KEY `created_by` (`created_by`),
  KEY `class_id` (`class_id`),
  CONSTRAINT `practice_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `practice_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `practice_ibfk_3` FOREIGN KEY (`class_id`) REFERENCES `class` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `practice`
--

LOCK TABLES `practice` WRITE;
/*!40000 ALTER TABLE `practice` DISABLE KEYS */;
INSERT INTO `practice` VALUES (1,1,1,'第一章小测','2025-07-08 14:08:19','2025-07-09 14:08:19',1,1,'2025-07-08 06:08:19'),(2,2,2,'练习1','2025-07-08 14:14:00','2025-07-24 14:14:00',1,4,'2025-07-08 06:14:49'),(3,2,2,'Tensor Flow 练习1','2025-07-14 23:41:00','2025-07-31 23:41:00',1,4,'2025-07-14 15:41:14'),(4,2,2,'练习小测8.16','2025-08-16 16:33:00','2025-08-31 16:33:00',1,4,'2025-08-16 08:33:18');
/*!40000 ALTER TABLE `practice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `practicequestion`
--

DROP TABLE IF EXISTS `practicequestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `practicequestion` (
  `practice_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `sort_order` bigint DEFAULT NULL,
  `score` int NOT NULL,
  `score_rate` decimal(5,4) DEFAULT NULL,
  PRIMARY KEY (`practice_id`,`question_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `practicequestion_ibfk_1` FOREIGN KEY (`practice_id`) REFERENCES `practice` (`id`) ON DELETE CASCADE,
  CONSTRAINT `practicequestion_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `practicequestion`
--

LOCK TABLES `practicequestion` WRITE;
/*!40000 ALTER TABLE `practicequestion` DISABLE KEYS */;
INSERT INTO `practicequestion` VALUES (1,1,1,10,1.0000),(3,29,NULL,5,1.0000),(3,30,NULL,1,1.0000),(3,31,NULL,1,1.0000),(3,32,NULL,1,0.0000),(3,34,NULL,5,0.0000),(3,35,NULL,10,0.0000),(4,4,NULL,5,NULL),(4,6,NULL,5,NULL),(4,8,NULL,5,NULL),(4,34,NULL,10,NULL),(4,36,NULL,1,NULL);
/*!40000 ALTER TABLE `practicequestion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `progress`
--

DROP TABLE IF EXISTS `progress`;
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
  KEY `section_id` (`section_id`),
  CONSTRAINT `progress_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `progress_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE CASCADE,
  CONSTRAINT `progress_ibfk_3` FOREIGN KEY (`section_id`) REFERENCES `coursesection` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `progress`
--

LOCK TABLES `progress` WRITE;
/*!40000 ALTER TABLE `progress` DISABLE KEYS */;
INSERT INTO `progress` VALUES (1,2,1,1,1,'2025-07-08 06:08:19'),(2,3,1,1,0,NULL);
/*!40000 ALTER TABLE `progress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `question`
--

DROP TABLE IF EXISTS `question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `question` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `creator_id` bigint NOT NULL,
  `type` enum('singlechoice','program','fillblank','judge') COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `analysis` text COLLATE utf8mb4_unicode_ci,
  `options` text COLLATE utf8mb4_unicode_ci,
  `answer` text COLLATE utf8mb4_unicode_ci,
  `course_id` bigint DEFAULT NULL,
  `section_id` bigint DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `creator_id` (`creator_id`),
  KEY `course_id` (`course_id`),
  KEY `section_id` (`section_id`),
  CONSTRAINT `question_ibfk_1` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `question_ibfk_3` FOREIGN KEY (`section_id`) REFERENCES `coursesection` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=42 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1,1,'singlechoice','数据库的三大范式是什么？','详见教材第1章','1NF|2NF|3NF|BCNF','1NF|2NF|3NF',1,1,'2025-07-08 06:08:19'),(3,4,'singlechoice','在TensorFlow.js中，以下哪个API用于创建一个全连接层？','全连接层在TensorFlow.js中通过`tf.layers.dense`创建，其他选项分别对应卷积层、池化层和Dropout层。','tf.layers.dense|||tf.layers.conv2d|||tf.layers.maxPooling2d|||tf.layers.dropout','A',2,3,'2025-07-11 08:10:57'),(4,4,'singlechoice','TensorFlow.js中加载预训练模型通常使用的方法是？','`tf.loadLayersModel`是加载Keras格式的预训练模型的标准方法，其他选项为干扰项或旧版API。','tf.loadLayersModel|||tf.loadGraphModel|||tf.loadModel|||tf.importModel','A',2,3,'2025-07-11 08:10:57'),(5,4,'singlechoice','以下哪个操作可以用于TensorFlow.js张量的形状转换？','`tf.reshape`直接改变张量的形状而不改变数据，其他选项分别用于类型转换、连接和复制操作。','tf.reshape|||tf.cast|||tf.concat|||tf.tile','A',2,3,'2025-07-11 08:10:57'),(6,4,'singlechoice','在浏览器中使用TensorFlow.js进行实时摄像头目标检测时，需要优先调用的API是？','浏览器访问摄像头需通过`getUserMedia`获取权限，其他选项是后续处理步骤的API。','navigator.mediaDevices.getUserMedia|||tf.data.webcam|||tf.browser.fromPixels|||document.getElementById','A',2,3,'2025-07-11 08:10:57'),(7,4,'singlechoice','TensorFlow.js模型训练时监控损失值变化的回调函数是？','`onEpochEnd`在每个训练周期结束时触发，适合观察损失变化，其他选项回调时机不匹配。','onEpochEnd|||onBatchBegin|||onTrainBegin|||onYield','A',2,3,'2025-07-11 08:10:57'),(8,4,'fillblank','TensorFlow.js中，将普通JavaScript数组转换为张量的方法是______。','`tf.tensor()`是基础张量构造函数，接受JS数组作为输入参数。','','tf.tensor()',2,3,'2025-07-11 08:10:57'),(9,4,'fillblank','在Node.js环境下使用TensorFlow.js需要安装的包名是______。','官方提供的Node.js专用包包含本地绑定加速运算。','','@tensorflow/tfjs-node',2,3,'2025-07-11 08:10:57'),(10,4,'program','简述TensorFlow.js的WebGL后端与WASM后端的性能差异及适用场景。','WebGL通过显卡并行计算提升性能但兼容性受限，WASM作为轻量级方案保证跨平台一致性。','','WebGL后端利用GPU加速适合复杂模型计算，WASM后端在低端设备或没有WebGL支持的场景下提供更稳定的CPU计算。',2,3,'2025-07-11 08:10:57'),(20,5,'singlechoice','在TensorFlow中，以下哪个函数用于创建一个常量张量？','tf.constant()函数用于创建一个常量张量，其值在创建后不可更改。','tf.Variable()|||tf.constant()|||tf.placeholder()|||tf.Tensor()','B',-1,1,'2025-07-13 15:48:17'),(21,5,'fillblank','在TensorFlow中，用于执行计算图的操作的函数是______。','tf.Session().run()函数用于执行计算图中的操作并获取结果。','','tf.Session().run()',-1,1,'2025-07-13 15:48:17'),(22,5,'judge','TensorFlow 2.x版本默认启用了即时执行模式（Eager Execution）。','TensorFlow 2.x版本默认启用了即时执行模式，无需手动开启。','','true',-1,1,'2025-07-13 15:48:17'),(23,5,'program','使用TensorFlow构建一个简单的线性回归模型，并给出代码实现。','这是一个简单的线性回归模型实现，使用tf.keras.Sequential构建模型，包含一个全连接层，使用随机梯度下降优化器和均方误差损失函数进行训练。','','import tensorflow as tf\n\n# 定义模型\nmodel = tf.keras.Sequential([\n    tf.keras.layers.Dense(units=1, input_shape=[1])\n])\n\n# 编译模型\nmodel.compile(optimizer=\'sgd\', loss=\'mean_squared_error\')\n\n# 训练数据\nX = [1, 2, 3, 4]\ny = [2, 4, 6, 8]\n\n# 训练模型\nmodel.fit(X, y, epochs=100)',-1,1,'2025-07-13 15:48:17'),(24,5,'fillblank','在TensorFlow中，用于创建常量的函数是______。','tf.constant()函数用于在TensorFlow中创建常量张量。','','tf.constant()',-1,1,'2025-07-13 15:52:07'),(25,5,'fillblank','在TensorFlow中，用于初始化所有全局变量的函数是______。','tf.global_variables_initializer()函数用于初始化图中所有的全局变量。','','tf.global_variables_initializer()',-1,1,'2025-07-13 15:52:07'),(26,5,'fillblank','在TensorFlow中，用于定义占位符的函数是______。','tf.placeholder()函数用于定义占位符，可以在运行计算图时传入数据。','','tf.placeholder()',-1,1,'2025-07-13 15:52:07'),(27,5,'fillblank','在TensorFlow中，用于创建全零张量的函数是______。','tf.zeros()函数用于创建指定形状的全零张量。','','tf.zeros()',-1,1,'2025-07-13 15:52:07'),(28,5,'fillblank','在TensorFlow中，用于计算两个张量点积的函数是______。','tf.matmul()函数用于计算两个张量的矩阵乘法（点积）。','','tf.matmul()',-1,1,'2025-07-13 15:52:07'),(29,4,'singlechoice','在TensorFlow.js中，以下哪个方法用于加载预训练模型？','在TensorFlow.js中，加载预训练模型的标准方法是使用tf.loadLayersModel()，该方法会从指定的URL加载模型。其他选项中的方法在TensorFlow.js中并不存在。','tf.loadLayersModel()|||tf.loadModel()|||tf.loadPretrainedModel()|||tf.importModel()','A',2,3,'2025-07-14 15:37:30'),(30,4,'singlechoice','TensorFlow.js中，以下哪个操作可以用于创建一个全零的张量？','在TensorFlow.js中，tf.zeros()方法用于创建一个全零的张量。tf.ones()用于创建全一的张量，而tf.fill()可以填充任意值，但需要指定填充的值。tf.zero()并不是TensorFlow.js中的有效方法。','tf.zeros()|||tf.zero()|||tf.fill()|||tf.ones()','A',2,3,'2025-07-14 15:37:30'),(31,4,'singlechoice','在TensorFlow.js中，以下哪个API用于执行模型的训练？','在TensorFlow.js中，model.fit()是用于执行模型训练的标准API。它会根据提供的数据和标签进行模型的训练。其他选项中的API在TensorFlow.js中并不存在或用于其他目的。','model.fit()|||model.train()|||model.execute()|||model.run()','A',2,3,'2025-07-14 15:37:30'),(32,4,'fillblank','在TensorFlow.js中，用于将普通JavaScript数组转换为张量的方法是______。','tf.tensor()是TensorFlow.js中用于将普通JavaScript数组转换为张量的方法。它可以接受一个数组或嵌套数组，并生成对应的张量对象。','','tf.tensor()',2,3,'2025-07-14 15:37:30'),(33,4,'program','简述在TensorFlow.js中如何定义一个简单的神经网络模型，并说明每一层的作用。','定义一个神经网络模型通常使用tf.sequential()创建一个顺序模型，然后通过model.add()添加层。第一层需要指定inputShape，后续层会自动推断输入形状。每一层的units参数指定神经元的数量，activation参数指定激活函数。','','在TensorFlow.js中，可以通过以下代码定义一个简单的神经网络模型：\n\nconst model = tf.sequential();\nmodel.add(tf.layers.dense({units: 10, inputShape: [5], activation: \'relu\'}));\nmodel.add(tf.layers.dense({units: 1, activation: \'sigmoid\'}));\n\n第一层是一个具有10个神经元和ReLU激活函数的全连接层，输入形状为[5]。第二层是一个具有1个神经元和sigmoid激活函数的输出层，用于二分类任务。',2,3,'2025-07-14 15:37:30'),(34,4,'program','使用TensorFlow.js创建一个简单的线性回归模型，用于预测给定输入x的输出y。模型应包含一个具有1个神经元的全连接层，并使用均方误差作为损失函数。编写完整的代码，包括模型定义、编译和训练步骤。','解题思路：\n1. 使用tf.sequential()创建一个顺序模型\n2. 添加一个具有1个神经元和1维输入的全连接层\n3. 使用均方误差作为损失函数，随机梯度下降作为优化器\n4. 准备训练数据（这里使用了简单的线性关系y=2x-1）\n5. 使用fit方法训练模型，设置10个epoch','','// 定义模型\nconst model = tf.sequential();\nmodel.add(tf.layers.dense({units: 1, inputShape: [1]}));\n\n// 编译模型\nmodel.compile({loss: \'meanSquaredError\', optimizer: \'sgd\'});\n\n// 准备训练数据\nconst xs = tf.tensor2d([1, 2, 3, 4], [4, 1]);\nconst ys = tf.tensor2d([1, 3, 5, 7], [4, 1]);\n\n// 训练模型\nasync function trainModel() {\n  await model.fit(xs, ys, {epochs: 10});\n}\n\ntrainModel();',2,3,'2025-07-14 15:39:19'),(35,4,'program','使用TensorFlow.js加载预训练的MobileNet模型，并对给定的图像进行分类。编写完整的代码，包括模型加载、图像预处理和预测步骤。','解题思路：\n1. 使用tf.loadLayersModel加载预训练的MobileNet模型\n2. 创建图像预处理函数，将图像调整为224x224大小并归一化\n3. 使用模型进行预测，输出预测结果\n4. 注意使用tf.tidy()管理内存','','// 加载MobileNet模型\nasync function loadModel() {\n  const model = await tf.loadLayersModel(\'https://storage.googleapis.com/tfjs-models/tfjs/mobilenet_v1_0.25_224/model.json\');\n  return model;\n}\n\n// 图像预处理\nfunction preprocessImage(imageElement) {\n  return tf.tidy(() => {\n    // 将图像转换为张量\n    let tensor = tf.browser.fromPixels(imageElement)\n      .resizeNearestNeighbor([224, 224]) // 调整大小\n      .toFloat();\n    \n    // 归一化\n    const mean = tf.tensor1d([127.5, 127.5, 127.5]);\n    const std = tf.tensor1d([127.5, 127.5, 127.5]);\n    tensor = tensor.sub(mean).div(std);\n    \n    // 添加批次维度\n    return tensor.expandDims(0);\n  });\n}\n\n// 使用模型进行预测\nasync function predict() {\n  const model = await loadModel();\n  const imageElement = document.getElementById(\'image\');\n  const preprocessedImage = preprocessImage(imageElement);\n  const predictions = model.predict(preprocessedImage);\n  console.log(predictions.dataSync());\n}',2,3,'2025-07-14 15:39:19'),(36,4,'singlechoice','这道题目选A','选a','a|||b|||c|||d','A',2,3,'2025-08-16 08:34:53'),(37,4,'program','在嵌入式Linux系统中，如何将Python脚本打包成一个可执行文件？请简述步骤。','解题思路：在嵌入式Linux系统中，Python脚本通常需要打包成可执行文件以便于分发和运行。PyInstaller是一个常用的工具，可以将Python脚本打包成独立的可执行文件。通过安装PyInstaller并使用其命令行工具，可以轻松完成打包过程。','','1. 安装PyInstaller工具：`pip install pyinstaller`。2. 使用PyInstaller打包脚本：`pyinstaller --onefile your_script.py`。3. 将生成的可执行文件复制到嵌入式Linux系统中。',2,5,'2025-08-17 07:14:58'),(38,4,'program','在嵌入式Linux系统中，如何确保Python脚本在系统启动时自动运行？请提供一种实现方法。','解题思路：在嵌入式Linux系统中，可以通过systemd服务来管理Python脚本的自动启动。创建一个自定义的systemd服务文件，并配置脚本的路径和启动命令，然后启用和启动该服务，即可实现脚本在系统启动时自动运行。','','1. 创建一个systemd服务文件，例如`/etc/systemd/system/myscript.service`。2. 在服务文件中配置Python脚本的路径和启动命令。3. 启用并启动服务：`sudo systemctl enable myscript.service` 和 `sudo systemctl start myscript.service`。',2,5,'2025-08-17 07:14:59'),(39,4,'program','在嵌入式Python开发中，如何通过Python脚本读取GPIO引脚的状态？请提供一个示例代码片段。','解题思路：在嵌入式Python开发中，可以使用RPi.GPIO库来操作GPIO引脚。首先设置GPIO模式（如BCM或BOARD），然后配置引脚为输入模式，最后通过GPIO.input()函数读取引脚状态。','','示例代码：\n```python\nimport RPi.GPIO as GPIO\nGPIO.setmode(GPIO.BCM)\nGPIO.setup(17, GPIO.IN)\nstate = GPIO.input(17)\nprint(f\"GPIO 17 state: {state}\")\n```',2,5,'2025-08-17 07:14:59'),(40,4,'program','在嵌入式Linux系统中，如何通过Python脚本实现一个简单的串口通信？请提供一个示例代码片段。','解题思路：在嵌入式Linux系统中，可以使用Python的serial库来实现串口通信。首先创建一个Serial对象，指定串口设备（如/dev/ttyS0）和波特率，然后通过write()发送数据，通过readline()接收数据。','','示例代码：\n```python\nimport serial\nser = serial.Serial(\'/dev/ttyS0\', 9600, timeout=1)\nser.write(b\'Hello\')\nresponse = ser.readline()\nprint(f\"Response: {response}\")\nser.close()\n```',2,5,'2025-08-17 07:14:59'),(41,4,'program','在嵌入式Python开发中，如何通过Python脚本实现一个简单的HTTP服务器？请提供一个示例代码片段。','解题思路：在嵌入式Python开发中，可以使用Python内置的http.server模块来实现一个简单的HTTP服务器。通过继承BaseHTTPRequestHandler并实现do_GET方法，可以处理HTTP GET请求。然后创建一个HTTPServer实例并启动它。','','示例代码：\n```python\nfrom http.server import BaseHTTPRequestHandler, HTTPServer\n\nclass SimpleHandler(BaseHTTPRequestHandler):\n    def do_GET(self):\n        self.send_response(200)\n        self.send_header(\'Content-type\', \'text/html\')\n        self.end_headers()\n        self.wfile.write(b\'Hello, Embedded Linux!\')\n\nserver = HTTPServer((\'0.0.0.0\', 8080), SimpleHandler)\nserver.serve_forever()\n```',2,5,'2025-08-17 07:14:59');
/*!40000 ALTER TABLE `question` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `selfanswer`
--

DROP TABLE IF EXISTS `selfanswer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `selfanswer` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `submission_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `answer_text` text COLLATE utf8mb4_unicode_ci,
  `is_judged` tinyint(1) DEFAULT '0',
  `correct` tinyint(1) DEFAULT NULL,
  `score` int DEFAULT NULL,
  `sort_order` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `submission_id` (`submission_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `selfanswer_ibfk_1` FOREIGN KEY (`submission_id`) REFERENCES `selfsubmission` (`id`) ON DELETE CASCADE,
  CONSTRAINT `selfanswer_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selfanswer`
--

LOCK TABLES `selfanswer` WRITE;
/*!40000 ALTER TABLE `selfanswer` DISABLE KEYS */;
INSERT INTO `selfanswer` VALUES (2,23,20,'A',1,0,0,1),(3,23,21,'gk',1,0,0,2),(4,23,22,'正确',1,0,0,3),(5,23,23,'goihi',1,0,0,4),(6,24,24,'tge',1,0,0,1),(7,24,25,'rear',1,0,0,2),(8,24,26,'twt',1,0,0,3),(9,24,27,'wtt',1,0,0,4),(10,24,28,'wtew',1,0,0,5);
/*!40000 ALTER TABLE `selfanswer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `selfpractice`
--

DROP TABLE IF EXISTS `selfpractice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `selfpractice` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL COMMENT '发起学生 ID',
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '练习标题',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `selfpractice_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selfpractice`
--

LOCK TABLES `selfpractice` WRITE;
/*!40000 ALTER TABLE `selfpractice` DISABLE KEYS */;
INSERT INTO `selfpractice` VALUES (1,5,'AI自测 2025-07-09T13:08:39.237133700','2025-07-09 05:08:39'),(2,5,'AI自测 2025-07-11T16:22:36.987621600','2025-07-11 08:22:37'),(3,5,'AI自测 2025-07-13T22:36:30.208420400','2025-07-13 14:36:30'),(4,5,'AI自测 2025-07-13T23:05:12.254846800','2025-07-13 15:05:12'),(5,5,'AI自测 2025-07-13T23:10:40.866229300','2025-07-13 15:10:41'),(6,5,'AI自测 2025-07-13T23:13:46.943990400','2025-07-13 15:13:47'),(7,5,'AI自测 2025-07-13T23:22:42.427809900','2025-07-13 15:22:42'),(8,5,'AI自测 2025-07-13T23:27:01.733158800','2025-07-13 15:27:02'),(9,5,'AI自测 2025-07-13T23:34:43.615120500','2025-07-13 15:34:44'),(10,5,'AI自测 2025-07-13T23:44:19.351836400','2025-07-13 15:44:19'),(11,5,'AI自测 2025-07-13T23:48:17.070030300','2025-07-13 15:48:17'),(12,5,'AI自测 2025-07-13T23:52:07.385601600','2025-07-13 15:52:07');
/*!40000 ALTER TABLE `selfpractice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `selfpracticequestion`
--

DROP TABLE IF EXISTS `selfpracticequestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `selfpracticequestion` (
  `self_practice_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `sort_order` int DEFAULT '0',
  `score` int NOT NULL,
  PRIMARY KEY (`self_practice_id`,`question_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `selfpracticequestion_ibfk_1` FOREIGN KEY (`self_practice_id`) REFERENCES `selfpractice` (`id`) ON DELETE CASCADE,
  CONSTRAINT `selfpracticequestion_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selfpracticequestion`
--

LOCK TABLES `selfpracticequestion` WRITE;
/*!40000 ALTER TABLE `selfpracticequestion` DISABLE KEYS */;
INSERT INTO `selfpracticequestion` VALUES (11,20,1,10),(11,21,2,10),(11,22,3,10),(11,23,4,10),(12,24,1,10),(12,25,2,10),(12,26,3,10),(12,27,4,10),(12,28,5,10);
/*!40000 ALTER TABLE `selfpracticequestion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `selfprogress`
--

DROP TABLE IF EXISTS `selfprogress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `selfprogress` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `self_practice_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `progress_json` json NOT NULL COMMENT '题目作答进度(JSON)',
  `saved_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `self_practice_id` (`self_practice_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `selfprogress_ibfk_1` FOREIGN KEY (`self_practice_id`) REFERENCES `selfpractice` (`id`) ON DELETE CASCADE,
  CONSTRAINT `selfprogress_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selfprogress`
--

LOCK TABLES `selfprogress` WRITE;
/*!40000 ALTER TABLE `selfprogress` DISABLE KEYS */;
/*!40000 ALTER TABLE `selfprogress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `selfsubmission`
--

DROP TABLE IF EXISTS `selfsubmission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `selfsubmission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `self_practice_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `submitted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `score` int DEFAULT '0',
  `is_judged` tinyint(1) DEFAULT '0',
  `feedback` json DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `self_practice_id` (`self_practice_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `selfsubmission_ibfk_1` FOREIGN KEY (`self_practice_id`) REFERENCES `selfpractice` (`id`) ON DELETE CASCADE,
  CONSTRAINT `selfsubmission_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selfsubmission`
--

LOCK TABLES `selfsubmission` WRITE;
/*!40000 ALTER TABLE `selfsubmission` DISABLE KEYS */;
INSERT INTO `selfsubmission` VALUES (21,7,5,'2025-07-13 15:22:53',0,1,NULL),(23,11,5,'2025-07-13 15:48:52',0,1,NULL),(24,12,5,'2025-07-13 15:52:58',0,1,NULL);
/*!40000 ALTER TABLE `selfsubmission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `submission`
--

DROP TABLE IF EXISTS `submission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `submission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `practice_id` bigint NOT NULL,
  `student_id` bigint NOT NULL,
  `submitted_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `score` int DEFAULT '0',
  `is_judged` int DEFAULT '0',
  `feedback` text COLLATE utf8mb4_unicode_ci,
  PRIMARY KEY (`id`),
  KEY `practice_id` (`practice_id`),
  KEY `student_id` (`student_id`),
  CONSTRAINT `submission_ibfk_1` FOREIGN KEY (`practice_id`) REFERENCES `practice` (`id`) ON DELETE CASCADE,
  CONSTRAINT `submission_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submission`
--

LOCK TABLES `submission` WRITE;
/*!40000 ALTER TABLE `submission` DISABLE KEYS */;
INSERT INTO `submission` VALUES (1,1,2,'2025-07-08 06:08:19',10,1,'回答正确'),(2,3,5,'2025-07-14 15:48:23',9,1,NULL),(3,3,5,'2025-07-15 03:12:55',6,0,NULL),(4,3,5,'2025-07-15 03:18:14',0,0,NULL),(5,3,5,'2025-07-16 14:33:56',0,0,NULL),(6,3,5,'2025-07-16 14:52:47',7,0,NULL),(7,3,5,'2025-07-16 15:04:39',7,0,NULL),(8,3,5,'2025-07-16 15:23:00',7,0,NULL);
/*!40000 ALTER TABLE `submission` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teacherknowledgebase`
--

DROP TABLE IF EXISTS `teacherknowledgebase`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teacherknowledgebase` (
  `teacher_id` bigint NOT NULL,
  `paths_json` json NOT NULL,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`teacher_id`),
  CONSTRAINT `teacherknowledgebase_ibfk_1` FOREIGN KEY (`teacher_id`) REFERENCES `user` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teacherknowledgebase`
--

LOCK TABLES `teacherknowledgebase` WRITE;
/*!40000 ALTER TABLE `teacherknowledgebase` DISABLE KEYS */;
/*!40000 ALTER TABLE `teacherknowledgebase` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teaching_resource`
--

DROP TABLE IF EXISTS `teaching_resource`;
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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teaching_resource`
--

LOCK TABLES `teaching_resource` WRITE;
/*!40000 ALTER TABLE `teaching_resource` DISABLE KEYS */;
INSERT INTO `teaching_resource` VALUES (1,'第一章视频资源','视频讲解',1,1,'第一章 数据库基础','VIDEO','/resource/1.mp4','/resource/1.mp4',600,1,'2025-07-08 14:08:19','2025-07-08 14:08:19'),(2,'01 认识TensorFlow.js-1.mp4','学习视屏，请务必观看： 认识TensorFlow.js-1.mp4。',2,3,'cp07-TensorFlow.js应用开发','VIDEO','https://edusoft-file.oss-cn-beijing.aliyuncs.com/4bf05a31bb504e6e8ac01e70eae3291d.mp4','4bf05a31bb504e6e8ac01e70eae3291d.mp4',360,4,'2025-08-16 16:27:04','2025-08-16 16:27:46');
/*!40000 ALTER TABLE `teaching_resource` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` varchar(15) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password_hash` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('student','teacher','tutor') COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'T001','张老师','hashed_pwd1','teacher','teacher@example.com','2025-07-08 06:08:19','2025-07-08 06:08:19'),(2,'S001','小明','hashed_pwd2','student','xiaoming@example.com','2025-07-08 06:08:19','2025-07-08 06:08:19'),(3,'S002','小红','hashed_pwd3','student','xiaohong@example.com','2025-07-08 06:08:19','2025-07-08 06:08:19'),(4,'T002','Teacher2','bc1785c9eda2973c1157cbbb90a8b1f9','teacher','pengsquare82@gmail.com','2025-07-08 06:09:39','2025-07-08 06:09:39'),(5,'S003','User','bc1785c9eda2973c1157cbbb90a8b1f9','student','buaavolunteers@sina.com','2025-07-08 06:10:10','2025-07-08 06:10:10'),(7,'Admin1','Admin','bc1785c9eda2973c1157cbbb90a8b1f9','tutor','23371112@buaa.edu.cn','2025-07-08 06:11:41','2025-07-08 06:11:41'),(8,'T003','Teacher2','bc1785c9eda2973c1157cbbb90a8b1f9','teacher','pengsquare82@gmail.com','2025-07-08 15:04:32','2025-07-08 15:04:32'),(9,'S004','S004','bc1785c9eda2973c1157cbbb90a8b1f9','student','pengsquare82@gmail.com','2025-08-16 08:30:33','2025-08-16 08:30:33'),(10,'S005','S005','bc1785c9eda2973c1157cbbb90a8b1f9','student','pengsquare82@gmail.com','2025-08-16 08:31:01','2025-08-16 08:31:01'),(11,'T100','T100','bc1785c9eda2973c1157cbbb90a8b1f9','teacher','buaavolunteers@sina.com','2025-08-21 09:50:31','2025-08-21 09:50:31');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `wrongquestion`
--

DROP TABLE IF EXISTS `wrongquestion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `wrongquestion` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `student_id` bigint NOT NULL,
  `question_id` bigint NOT NULL,
  `wrong_answer` text COLLATE utf8mb4_unicode_ci,
  `correct_answer` text COLLATE utf8mb4_unicode_ci,
  `wrong_count` int DEFAULT '1',
  `last_wrong_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `student_id` (`student_id`),
  KEY `question_id` (`question_id`),
  CONSTRAINT `wrongquestion_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `wrongquestion_ibfk_2` FOREIGN KEY (`question_id`) REFERENCES `question` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wrongquestion`
--

LOCK TABLES `wrongquestion` WRITE;
/*!40000 ALTER TABLE `wrongquestion` DISABLE KEYS */;
INSERT INTO `wrongquestion` VALUES (1,5,20,'A','B',1,'2025-07-13 15:48:35','2025-07-13 15:48:35'),(2,5,21,'gk','tf.Session().run()',1,'2025-07-13 15:48:35','2025-07-13 15:48:35'),(3,5,22,'正确','true',1,'2025-07-13 15:48:35','2025-07-13 15:48:35'),(4,5,23,'goihi','import tensorflow as tf\n\n# 定义模型\nmodel = tf.keras.Sequential([\n    tf.keras.layers.Dense(units=1, input_shape=[1])\n])\n\n# 编译模型\nmodel.compile(optimizer=\'sgd\', loss=\'mean_squared_error\')\n\n# 训练数据\nX = [1, 2, 3, 4]\ny = [2, 4, 6, 8]\n\n# 训练模型\nmodel.fit(X, y, epochs=100)',1,'2025-07-13 15:48:51','2025-07-13 15:48:51'),(5,5,24,'tge','tf.constant()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(6,5,25,'rear','tf.global_variables_initializer()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(7,5,26,'twt','tf.placeholder()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(8,5,27,'wtt','tf.zeros()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(9,5,28,'wtew','tf.matmul()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57');
/*!40000 ALTER TABLE `wrongquestion` ENABLE KEYS */;
UNLOCK TABLES;
--
-- Table structure for table `chat_session`
--

DROP TABLE IF EXISTS `chat_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_session` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `session_title` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT 'New Chat',
  `course_id` bigint DEFAULT NULL,
  `course_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `course_id` (`course_id`),
  KEY `idx_user_active` (`user_id`, `is_active`),
  KEY `idx_updated_at` (`updated_at`),
  CONSTRAINT `chat_session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chat_session_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `role` enum('user','assistant') COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `references` json DEFAULT NULL,
  `knowledge_points` json DEFAULT NULL,
  `message_order` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `token_count` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  KEY `idx_session_order` (`session_id`, `message_order`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `chat_message_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `chat_memory_summary`
--

DROP TABLE IF EXISTS `chat_memory_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_memory_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `summary_content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `message_range_start` bigint NOT NULL,
  `message_range_end` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `token_count` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  KEY `idx_session_range` (`session_id`, `message_range_start`, `message_range_end`),
  CONSTRAINT `chat_memory_summary_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-21 22:18:12
