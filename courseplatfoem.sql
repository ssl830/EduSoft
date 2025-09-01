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
) ENGINE=InnoDB AUTO_INCREMENT=314 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aiservicecalllog`
--

LOCK TABLES `aiservicecalllog` WRITE;
/*!40000 ALTER TABLE `aiservicecalllog` DISABLE KEYS */;
INSERT INTO `aiservicecalllog` VALUES (1,4,'/embedding/upload',190901,'2025-07-08 06:20:36','success',NULL),(2,NULL,'/embedding/base_path',0,'2025-07-08 09:39:52','fail',NULL),(3,NULL,'/embedding/base_path/reset',102,'2025-07-08 09:43:26','success',NULL),(4,NULL,'/embedding/base_path',0,'2025-07-08 09:44:21','fail',NULL),(5,NULL,'/embedding/base_path',0,'2025-07-08 09:49:08','fail',NULL),(6,NULL,'/embedding/base_path/reset',10,'2025-07-08 09:51:39','success',NULL),(7,NULL,'/embedding/base_path',37,'2025-07-08 09:51:56','success',NULL),(8,NULL,'/embedding/base_path/reset',11,'2025-07-08 10:04:10','success',NULL),(9,NULL,'/embedding/base_path',25,'2025-07-08 10:04:33','success',NULL),(10,NULL,'/embedding/base_path/reset',22,'2025-07-08 10:26:41','success',NULL),(11,NULL,'/embedding/base_path',17,'2025-07-08 10:26:59','success',NULL),(12,NULL,'/embedding/base_path',26,'2025-07-08 10:27:17','success',NULL),(13,NULL,'/embedding/base_path/reset',13,'2025-07-08 10:34:00','success',NULL),(14,NULL,'/embedding/base_path',188,'2025-07-08 13:19:27','success',NULL),(15,NULL,'/embedding/base_path/reset',38,'2025-07-08 13:47:42','success',NULL),(16,NULL,'/embedding/base_path',46,'2025-07-08 13:53:55','success',NULL),(17,NULL,'/embedding/base_path/reset',26,'2025-07-08 14:09:20','success',NULL),(18,NULL,'/embedding/base_path/reset',16,'2025-07-08 14:54:41','success',NULL),(19,NULL,'/embedding/base_path',74,'2025-07-08 14:54:55','success',NULL),(20,NULL,'/embedding/base_path/reset',23,'2025-07-08 14:55:29','success',NULL),(21,NULL,'/embedding/base_path/reset',37,'2025-07-08 15:08:29','success',NULL),(22,NULL,'/embedding/base_path/reset',7,'2025-07-08 15:10:45','success',NULL),(23,NULL,'/embedding/base_path',30,'2025-07-08 15:14:23','success',NULL),(24,NULL,'/embedding/base_path',32,'2025-07-08 15:14:27','success',NULL),(25,NULL,'/embedding/base_path/reset',15,'2025-07-08 15:14:33','success',NULL),(26,4,'/embedding/upload',9488,'2025-07-08 15:21:50','success',NULL),(27,4,'/embedding/upload',8308,'2025-07-09 04:40:05','success',NULL),(28,4,'/rag/assistant',443,'2025-07-09 04:42:07','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"list index out of range\"}\"'),(29,NULL,'/embedding/base_path/reset',30,'2025-07-09 04:43:00','success',NULL),(30,NULL,'/embedding/base_path/reset',15,'2025-07-09 04:43:10','success',NULL),(31,4,'/rag/assistant',287,'2025-07-09 04:43:52','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"list index out of range\"}\"'),(32,NULL,'/embedding/base_path/reset',16,'2025-07-09 04:54:38','success',NULL),(33,4,'/rag/assistant',1710,'2025-07-09 04:55:12','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"list index out of range\"}\"'),(34,4,'/rag/assistant',9338,'2025-07-09 04:58:04','success',NULL),(35,4,'/rag/assistant',12673,'2025-07-09 05:02:17','success',NULL),(36,4,'/rag/assistant',24642,'2025-07-09 05:04:20','success',NULL),(37,5,'/rag/generate_student_exercise',10183,'2025-07-09 05:08:39','success',NULL),(38,5,'/rag/assistant',12986,'2025-07-09 05:10:10','success',NULL),(39,5,'/rag/assistant',17477,'2025-07-09 05:21:30','success',NULL),(40,5,'/rag/assistant',18686,'2025-07-09 05:23:23','success',NULL),(41,4,'/rag/assistant',13326,'2025-07-09 05:28:21','success',NULL),(42,NULL,'/embedding/base_path',41,'2025-07-09 05:29:48','success',NULL),(43,NULL,'/embedding/base_path',29,'2025-07-09 05:30:04','success',NULL),(44,4,'/rag/assistant',18046,'2025-07-09 05:32:16','success',NULL),(45,4,'/rag/assistant',16304,'2025-07-09 05:39:31','success',NULL),(46,4,'/rag/assistant',23341,'2025-07-09 05:40:50','success',NULL),(47,4,'/rag/assistant',24066,'2025-07-09 05:42:20','success',NULL),(48,NULL,'/embedding/base_path',70,'2025-07-09 05:43:26','success',NULL),(49,4,'/rag/assistant',25910,'2025-07-09 05:44:02','success',NULL),(50,4,'/rag/assistant',22870,'2025-07-09 05:52:10','success',NULL),(51,4,'/rag/assistant',28923,'2025-07-09 05:53:56','success',NULL),(52,NULL,'/embedding/base_path/reset',7,'2025-07-09 07:11:54','success',NULL),(53,4,'/embedding/upload',8248,'2025-07-11 07:41:34','fail','500 Internal Server Error on POST request for \"http://localhost:8000/embedding/upload\": \"{\"detail\":\"Connection error.\"}\"'),(54,4,'/embedding/upload',85195,'2025-07-11 07:47:15','success',NULL),(55,4,'/rag/assistant',17530,'2025-07-11 08:05:05','success',NULL),(56,4,'/rag/assistant',22175,'2025-07-11 08:08:26','success',NULL),(57,4,'/rag/generate_exercise',45561,'2025-07-11 08:10:57','success',NULL),(58,4,'/rag/generate',156781,'2025-07-11 08:14:07','success',NULL),(59,4,'/rag/detail',38979,'2025-07-11 08:17:55','success',NULL),(60,5,'/rag/generate_student_exercise',13467,'2025-07-11 08:22:36','success',NULL),(61,5,'/rag/generate_student_exercise',9005,'2025-07-13 14:36:30','success',NULL),(62,5,'/rag/generate_student_exercise',20654,'2025-07-13 15:05:12','success',NULL),(63,5,'/rag/generate_student_exercise',16010,'2025-07-13 15:10:40','success',NULL),(64,5,'/rag/generate_student_exercise',11451,'2025-07-13 15:13:46','success',NULL),(65,5,'/rag/generate_student_exercise',10088,'2025-07-13 15:22:42','success',NULL),(66,5,'/rag/generate_student_exercise',9462,'2025-07-13 15:27:01','success',NULL),(67,5,'/rag/generate_student_exercise',13444,'2025-07-13 15:34:43','success',NULL),(68,5,'/rag/generate_student_exercise',29797,'2025-07-13 15:44:19','success',NULL),(69,5,'/rag/generate_student_exercise',29804,'2025-07-13 15:48:17','success',NULL),(70,5,'/rag/evaluate_subjective',16229,'2025-07-13 15:48:51','success',NULL),(71,5,'/rag/generate_selected_student_exercise',25533,'2025-07-13 15:52:07','success',NULL),(72,4,'/rag/generate',199835,'2025-07-14 15:03:42','success',NULL),(73,4,'/rag/detail',46382,'2025-07-14 15:08:10','success',NULL),(74,NULL,'/embedding/base_path',28,'2025-07-14 15:12:30','success',NULL),(75,NULL,'/embedding/base_path/reset',98,'2025-07-14 15:28:50','success',NULL),(76,NULL,'/embedding/base_path/reset',58,'2025-07-14 15:28:58','success',NULL),(77,4,'/rag/generate_exercise',46194,'2025-07-14 15:37:30','success',NULL),(78,4,'/rag/generate_exercise',45444,'2025-07-14 15:39:19','success',NULL),(79,4,'/embedding/upload',275329,'2025-07-15 09:06:20','success',NULL),(80,4,'/embedding/upload',153459,'2025-07-15 09:10:33','success',NULL),(81,4,'/embedding/upload',705445,'2025-07-15 09:30:16','success',NULL),(82,4,'/embedding/upload',107610,'2025-07-15 09:33:37','success',NULL),(83,4,'/embedding/upload',83094,'2025-07-15 09:38:28','fail','500 Internal Server Error on POST request for \"http://localhost:8000/embedding/upload\": \"{\"detail\":\"Connection error.\"}\"'),(84,4,'/embedding/upload',87114,'2025-07-15 09:41:08','success',NULL),(85,4,'/embedding/upload',128721,'2025-07-15 09:43:32','success',NULL),(86,4,'/rag/evaluate_subjective',9631,'2025-07-16 14:38:24','success',NULL),(87,4,'/rag/evaluate_subjective',17894,'2025-07-16 14:38:32','success',NULL),(88,4,'/rag/evaluate_subjective',21245,'2025-07-16 14:40:31','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/evaluate_subjective\": \"{\"detail\":\"JSON解析失败，原始内容：由于未提供学生的具体答案，我将给出一个评估模板，该模板可以根据学生的实际答案进行修改。\\n\\n```json\\n{\\n    \\\"score\\\": 0.0,  // 实际得分将根据学生答案填写\\n    \\\"analysis\\\": {\\n        \\\"correct_points\\\": [  // 学生答对的要点\\n            // 例如：\\\"Correctly imported TensorFlow.js... 错误信息: Expecting value: line 1 column 1 (char 0)\"}\"'),(89,4,'/rag/evaluate_subjective',47269,'2025-07-16 14:40:57','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/evaluate_subjective\": \"{\"detail\":\"JSON解析失败，原始内容：由于未提供学生的具体答案，我将提供一个评估模板，该模板可以根据学生的实际答案进行修改。\\n\\n```json\\n{\\n  \\\"score\\\": 0.0, \\n  \\\"analysis\\\": {\\n    \\\"correct_points\\\": [], \\n    \\\"incorrect_points\\\": [], \\n    \\\"knowledge_points\\\": [\\n      \\\"Chapter 3: Introduction... 错误信息: Expecting value: line 1 column 1 (char 0)\"}\"'),(90,4,'/rag/evaluate_subjective',8180,'2025-07-16 14:41:47','success',NULL),(91,4,'/rag/evaluate_subjective',21730,'2025-07-16 14:42:00','success',NULL),(92,5,'/rag/evaluate_subjective',10774,'2025-07-16 15:02:21','success',NULL),(93,5,'/rag/evaluate_subjective',21781,'2025-07-16 15:02:32','success',NULL),(94,5,'/rag/evaluate_subjective',11652,'2025-07-16 15:04:51','success',NULL),(95,5,'/rag/evaluate_subjective',21538,'2025-07-16 15:05:01','success',NULL),(96,5,'/rag/evaluate_subjective',5016,'2025-07-16 15:23:05','success',NULL),(97,5,'/rag/evaluate_subjective',13615,'2025-07-16 15:23:14','success',NULL),(98,5,'/rag/assistant',17997,'2025-08-11 06:05:24','success',NULL),(99,5,'/rag/assistant',17222,'2025-08-11 06:06:26','success',NULL),(100,5,'/rag/assistant',19651,'2025-08-11 06:13:42','success',NULL),(101,5,'/rag/assistant',16052,'2025-08-11 06:14:36','success',NULL),(102,5,'/rag/assistant',14546,'2025-08-11 06:16:50','success',NULL),(103,NULL,'/embedding/base_path/reset',469210,'2025-08-11 07:33:32','success',NULL),(104,4,'/rag/assistant',7495,'2025-08-16 08:17:11','success',NULL),(105,4,'/rag/assistant',12977,'2025-08-16 08:18:30','success',NULL),(106,NULL,'/embedding/base_path',77,'2025-08-16 08:42:22','success',NULL),(107,4,'/rag/assistant',33630,'2025-08-16 08:43:31','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"JSON解析失败，原始内容：```json\\n{\\n  \\\"answer\\\": \\\"根据您即将进入大三阶段的学习，以及北京航空航天大学软件学院软件工程专业的培养方案，以下是一些建议的暑期预习方向：\\n\\n1. 针对专业课，您可以提前预习分布式系统导论、并行程序设计、云计算技术基础等课程，这些课程将在大三秋季学期开设。\\n2. 对于实践性较强的课程，如嵌入式软件设计、数值计算与算法、工业大数据技术等，可以提前了解相关技术背景和基础知识。\\n3.... 错误信息: Expecting value: line 1 column 1 (char 0)\"}\"'),(108,4,'/rag/assistant',19297,'2025-08-16 09:11:31','success',NULL),(109,NULL,'/embedding/base_path/reset',63,'2025-08-16 09:13:49','success',NULL),(110,NULL,'/embedding/base_path',33,'2025-08-16 09:13:55','success',NULL),(111,4,'/rag/assistant',46450,'2025-08-16 09:15:00','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"JSON解析失败，原始内容：{\\n    \\\"answer\\\": \\\"根据您即将进入大三的阶段以及软件工程专业的培养方案，以下是一些建议的暑期预习方向：\\n\\n1. 核心专业课程：您可以考虑预习如「分布式系统导论」、「并行程序设计」、「云计算技术基础」等课程，这些课程将是您大三学习中的重要组成部分。\\n\\n2. 理论与实践结合的课程：针对「嵌入式软件设计」、「数值计算与算法」等课程，预习理论知识的同时，注意寻找实践机会，增强实际操作能力。\\n... 错误信息: Invalid control character at: line 2 column 57 (char 58)\"}\"'),(112,NULL,'/embedding/base_path/reset',84059,'2025-08-16 09:26:08','success',NULL),(113,NULL,'/embedding/base_path',8831,'2025-08-16 09:26:08','success',NULL),(114,4,'/rag/assistant',22529,'2025-08-16 09:26:52','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"JSON解析失败，原始内容：{\\n    \\\"answer\\\": \\\"根据您即将进入大三阶段的学习，以及北京航空航天大学软件学院软件工程专业的培养方案，以下是一些建议的暑期预习方向：\\n\\n1. 核心专业课程：关注如「分布式系统导论」、「并行程序设计」、「云计算技术基础」等课程，这些课程将在大三开设，预习这些可以帮助您提前掌握概念，为课堂学习打下坚实基础。\\n\\n2. 高级编程技能：鉴于「Java企业级开发」和「软件测试与交付」等课程的重要...\"}\"'),(115,4,'/rag/assistant',92538,'2025-08-16 09:30:58','success',NULL),(116,NULL,'/embedding/base_path',105295,'2025-08-16 09:30:58','success',NULL),(117,4,'/rag/assistant',24141,'2025-08-16 09:33:46','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"JSON解析失败，原始内容：{\\n    \\\"answer\\\": \\\"根据您即将进入大三阶段的学习，以及软件工程专业课程清单，以下是对您暑期预习的建议：首先，可以提前学习如「分布式系统导论」和「并行程序设计」等课程，这些课程对于理解软件工程的高级概念非常重要。其次，针对「云计算技术基础」和「工业互联网技术基础」，您可以进行初步的了解，特别是云计算在实际应用中的案例分析。另外，考虑到「智能计算系统」和「图像处理和计算机视觉」的前沿性，... 错误信息: Invalid control character at: line 2 column 311 (char 312)\"}\"'),(118,NULL,'/embedding/base_path/reset',80,'2025-08-16 09:34:24','success',NULL),(119,4,'/rag/assistant',18201,'2025-08-16 09:35:38','success',NULL),(120,4,'/rag/assistant',9758,'2025-08-16 09:36:22','success',NULL),(121,4,'/rag/assistant',8154,'2025-08-16 10:25:48','success',NULL),(122,NULL,'/embedding/base_path',74,'2025-08-16 10:29:51','success',NULL),(123,4,'/rag/assistant',42350,'2025-08-16 10:31:11','success',NULL),(124,4,'/rag/assistant',37811,'2025-08-16 10:32:41','success',NULL),(125,4,'/rag/assistant',69638,'2025-08-16 10:50:16','success',NULL),(126,4,'/rag/assistant',14974,'2025-08-16 10:58:06','success',NULL),(127,NULL,'/embedding/base_path/reset',143,'2025-08-16 10:58:44','success',NULL),(128,NULL,'/embedding/base_path/reset',73,'2025-08-16 10:58:50','success',NULL),(129,5,'/rag/assistant',9919,'2025-08-16 12:53:31','success',NULL),(130,5,'/rag/assistant',7330,'2025-08-16 12:54:40','success',NULL),(131,4,'/rag/generate_section',63050,'2025-08-16 13:00:17','success',NULL),(132,4,'/rag/detail',26415,'2025-08-16 13:11:31','success',NULL),(133,4,'/rag/regenerate',62,'2025-08-16 13:19:23','fail','422 Unprocessable Entity on POST request for \"http://localhost:8000/rag/regenerate\": \"{\"detail\":[{\"type\":\"string_type\",\"loc\":[\"body\",\"practiceContent\"],\"msg\":\"Input should be a valid string\",\"input\":{\"任务1\":\"准备线性回归的数据集\",\"目标1\":\"学生能够正确导入数据集，理解数据的结构\",\"评价标准1\":\"数据集是否符合线性回归的要求，数据结构是否清晰\",\"任务2\":\"构建线性回归模型\",\"目标2\":\"学生能够通过TensorFlow.js API创建模型\",\"评价标准2\":\"模型结构是否正确，是否使用了适当的变量和操作\",\"任务3\":\"训练并评估模型\",\"目标3\":\"学生能够训练模型，并使用适当的方法进行评估\",\"评价标准3\":\"模型的训练过程是否正确执行，评估结果是否合理\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"teachingGuidance\"],\"msg\":\"Input should be a valid string\",\"input\":{\"重难点\":\"理解张量、变量、操作的概念，以及如何使用TensorFlow.js构建模型\",\"易错点\":\"张量的形状和维度理解，变量与普通张量的区分，操作的选择和使用\",\"互动建议\":\"鼓励学生提问，通过具体例子讲解抽象概念，组织小组讨论\",\"分层教学策略\":\"针对不同学生的学习进度，提供不同难度的练习题，对理解困难的学生提供额外辅导\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"timePlan\",0,\"content\"],\"msg\":\"Input should be a valid string\",\"input\":{\"教学目标\":\"使学生理解张量、变量、操作等基本概念\",\"活动安排\":\"1. 张量的定义与性质；2. 变量的作用及声明方式；3. 操作的分类与使用方法\",\"师生活动\":\"教师通过PPT或板书展示概念，学生跟随讲解做笔记\",\"互动方式\":\"提问与解答，学生展示理解并通过例子加深认识\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"timePlan\",1,\"content\"],\"msg\":\"Input should be a valid string\",\"input\":{\"教学目标\":\"通过案例使学生直观感受TensorFlow.js在实际项目中的应用\",\"活动安排\":\"分析手写数字识别案例的模型结构、数据处理和训练过程\",\"师生活动\":\"教师逐步讲解案例，学生观看并思考每个环节的作用和意义\",\"互动方式\":\"小组讨论案例中的关键点，教师指导并解答疑问\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"timePlan\",2,\"content\"],\"msg\":\"Input should be a valid string\",\"input\":{\"教学目标\":\"学生能够独立使用TensorFlow.js构建简单的线性回归模型\",\"活动安排\":\"1. 数据准备；2. 模型构建；3. 模型训练与评估\",\"师生活动\":\"学生在教师指导下逐步完成练习，教师巡回指导\",\"互动方式\":\"同伴互助，分享构建过程遇到的问题和解决方法\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"},{\"type\":\"string_type\",\"loc\":[\"body\",\"timePlan\",3,\"content\"],\"msg\":\"Input should be a valid string\",\"input\":{\"教学目标\":\"巩固本课时学习的知识点\",\"活动安排\":\"总结张量、变量、操作的概念及模型构建流程\",\"师生活动\":\"教师带领回顾本课时内容，学生反馈学习难点\",\"互动方式\":\"学生提出问题，教师解答并给出进一步学习的建议\"},\"url\":\"https://errors.pydantic.dev/2.11/v/string_type\"}]}\"'),(134,4,'/rag/generate',592624,'2025-08-16 13:29:35','success',NULL),(135,4,'/rag/generate',71134,'2025-08-17 04:40:04','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/generate\": \"{\"detail\":\"JSON解析失败，原始内容：以下是针对“Linux开发实践”课程的教学方案，以JSON格式返回：\\n\\n```json\\n{\\n    \\\"lessons\\\": [\\n        {\\n            \\\"title\\\": \\\"TensorFlow.js基本概念与原理\\\",\\n            \\\"timePlan\\\": [\\n                {\\\"step\\\": \\\"导入\\\", \\\"minutes\\\": 5, \\\"content\\\": ... 错误信息: Expecting value: line 1 column 1 (char 0)\"}\"'),(136,4,'/rag/generate',91994,'2025-08-17 04:43:46','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/generate\": \"{\"detail\":\"JSON解析失败，原始片段：以下是根据您提供的信息生成的教学方案：\\n\\n```json\\n{\\n    \\\"lessons\\\": [\\n        {\\n            \\\"title\\\": \\\"TensorFlow.js基础概念与原理\\\",\\n            \\\"timePlan\\\": [\\n                {\\\"step\\\": \\\"导入\\\", \\\"minutes\\\": 5, \\\"content\\\": \\\"介绍TensorFlow.j... e1=Expecting value: line 1 column 1 (char 0) e2= e3=Expecting value: line 59 column 44 (char 3342)\"}\"'),(137,4,'/rag/generate',145881,'2025-08-17 05:02:00','success',NULL),(138,4,'/rag/detail',27182,'2025-08-17 05:03:31','success',NULL),(139,4,'/rag/generate',106371,'2025-08-17 05:20:41','success',NULL),(140,4,'/rag/detail',48041,'2025-08-17 05:22:07','success',NULL),(141,4,'/rag/assistant',16600,'2025-08-17 05:37:58','success',NULL),(142,4,'/rag/assistant',28491,'2025-08-17 06:55:20','success',NULL),(143,4,'/rag/assistant',27618,'2025-08-17 07:01:25','success',NULL),(144,4,'/rag/assistant',53548,'2025-08-17 07:03:13','success',NULL),(145,4,'/rag/generate_section',139652,'2025-08-17 07:06:48','success',NULL),(146,4,'/rag/detail',50980,'2025-08-17 07:09:22','success',NULL),(147,NULL,'/embedding/base_path',20,'2025-08-17 07:10:17','success',NULL),(148,4,'/embedding/upload',73285,'2025-08-17 07:11:43','success',NULL),(149,NULL,'/embedding/base_path/reset',37,'2025-08-17 07:13:07','success',NULL),(150,4,'/rag/generate_exercise',56096,'2025-08-17 07:14:58','success',NULL),(151,4,'/rag/assistant',24225,'2025-08-18 10:59:32','success',NULL),(152,4,'/rag/assistant',35924,'2025-08-18 11:00:38','success',NULL),(153,4,'/rag/assistant',88502,'2025-08-18 11:02:29','success',NULL),(154,4,'/rag/assistant',74584,'2025-08-22 06:45:27','success',NULL),(155,NULL,'/embedding/base_path/reset',100,'2025-08-22 06:46:00','success',NULL),(156,5,'/rag/assistant',517545,'2025-08-22 07:27:04','fail','I/O error on POST request for \"http://localhost:8000/rag/assistant\": Connection reset'),(157,5,'/rag/assistant',10410,'2025-08-22 08:12:27','success',NULL),(158,5,'/rag/assistant',12494,'2025-08-22 08:15:57','success',NULL),(159,4,'/rag/generate',72803,'2025-08-22 08:20:25','success',NULL),(160,4,'/rag/detail',33866,'2025-08-22 08:23:16','success',NULL),(161,4,'/rag/regenerate',10145,'2025-08-22 08:24:14','success',NULL),(162,4,'/rag/generate_section',991,'2025-08-22 08:25:27','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/generate_section\": \"{\"detail\":\"Package not found at \'D:\\\\桌面\\\\EduSoft\\\\ai_service\\\\storage\\\\temp\\\\section-outline-4527716796462151466-cp09-样章示例-嵌入式Python开发.docx\'\"}\"'),(163,4,'/rag/generate_section',834,'2025-08-22 08:25:54','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/generate_section\": \"{\"detail\":\"Package not found at \'D:\\\\桌面\\\\EduSoft\\\\ai_service\\\\storage\\\\temp\\\\section-outline-11954351406530536428-cp09-样章示例-嵌入式Python开发.docx\'\"}\"'),(164,4,'/rag/generate_section',91539,'2025-08-22 08:27:57','success',NULL),(165,4,'/rag/detail',87025,'2025-08-22 08:29:47','success',NULL),(166,4,'/rag/generate_exercise',45429,'2025-08-22 09:14:37','success',NULL),(167,5,'/rag/assistant',5400,'2025-08-22 11:27:12','fail','Failed to save chat message'),(168,5,'/rag/assistant',17911,'2025-08-22 11:29:54','fail','Failed to save chat message'),(169,5,'/rag/assistant',14822,'2025-08-22 11:53:22','fail','Failed to save chat message'),(170,5,'/rag/assistant',4498,'2025-08-22 12:14:05','success',NULL),(171,5,'/rag/assistant',4400,'2025-08-22 12:16:01','success',NULL),(172,5,'/rag/assistant',11161,'2025-08-22 12:24:35','success',NULL),(173,5,'/rag/assistant',6648,'2025-08-22 12:24:50','success',NULL),(174,5,'/rag/assistant',10152,'2025-08-22 12:39:03','success',NULL),(175,5,'/rag/assistant',18584,'2025-08-22 12:39:46','success',NULL),(176,4,'/rag/assistant',19845,'2025-08-22 13:03:32','success',NULL),(177,NULL,'/embedding/base_path',131,'2025-08-22 13:04:28','success',NULL),(178,NULL,'/embedding/base_path',44,'2025-08-22 13:04:35','success',NULL),(179,4,'/rag/assistant',30752,'2025-08-22 13:05:32','success',NULL),(180,4,'/rag/assistant',26041,'2025-08-22 13:06:42','success',NULL),(181,4,'/rag/assistant',25069,'2025-08-22 13:07:50','success',NULL),(182,4,'/rag/assistant',27384,'2025-08-22 13:09:27','success',NULL),(183,4,'/rag/assistant',15058,'2025-08-22 13:10:40','success',NULL),(184,4,'/rag/assistant',28892,'2025-08-22 13:12:06','success',NULL),(185,4,'/rag/assistant',12459,'2025-08-23 03:35:58','success',NULL),(186,NULL,'/embedding/base_path/reset',74,'2025-08-23 03:37:09','success',NULL),(187,5,'/rag/generate_student_exercise',9800,'2025-08-23 04:42:12','success',NULL),(188,4,'/rag/evaluate_subjective',5329,'2025-08-23 04:52:33','success',NULL),(189,4,'/rag/evaluate_subjective',9899,'2025-08-23 04:52:37','success',NULL),(190,4,'/rag/generate',58126,'2025-08-23 04:57:11','success',NULL),(191,4,'/rag/detail',25098,'2025-08-23 04:58:15','success',NULL),(192,12,'/rag/evaluate_subjective',17887,'2025-08-23 05:25:14','success',NULL),(193,4,'/rag/evaluate_subjective',13589,'2025-08-23 05:27:15','success',NULL),(194,4,'/rag/evaluate_subjective',9947,'2025-08-23 14:17:18','success',NULL),(195,4,'/rag/evaluate_subjective',23866,'2025-08-23 14:17:32','success',NULL),(196,5,'/rag/evaluate_subjective',8038,'2025-08-23 14:23:54','success',NULL),(197,4,'/rag/evaluate_subjective',14400,'2025-08-23 14:26:27','success',NULL),(198,4,'/rag/evaluate_subjective',21876,'2025-08-23 14:26:35','success',NULL),(199,4,'/embedding/upload',1274,'2025-08-24 05:32:37','success',NULL),(200,4,'/embedding/upload',5205,'2025-08-24 05:42:07','success',NULL),(201,4,'/rag/assistant',9808,'2025-08-24 05:43:07','success',NULL),(202,4,'/rag/assistant',8083,'2025-08-24 05:45:11','success',NULL),(203,4,'/rag/assistant',8969,'2025-08-24 05:45:56','success',NULL),(204,8,'/embedding/upload',4520,'2025-08-24 05:47:28','success',NULL),(205,8,'/rag/assistant',3323,'2025-08-24 05:48:01','success',NULL),(206,8,'/rag/assistant',7770,'2025-08-24 05:48:46','success',NULL),(207,8,'/rag/assistant',10573,'2025-08-24 05:49:56','success',NULL),(208,8,'/rag/assistant',5934,'2025-08-24 05:51:40','success',NULL),(209,8,'/rag/assistant',3836,'2025-08-24 05:59:25','success',NULL),(210,8,'/rag/assistant',5514,'2025-08-24 06:19:55','success',NULL),(211,14,'/rag/assistant',2832,'2025-08-24 06:21:39','success',NULL),(212,14,'/embedding/upload',4510,'2025-08-24 06:22:22','success',NULL),(213,14,'/rag/assistant',7547,'2025-08-24 06:22:55','success',NULL),(214,14,'/rag/assistant',6755,'2025-08-24 06:24:00','success',NULL),(215,14,'/rag/assistant',11147,'2025-08-24 06:38:09','success',NULL),(216,14,'/rag/assistant',6124,'2025-08-24 06:39:08','success',NULL),(217,14,'/rag/assistant',9878,'2025-08-24 06:39:45','success',NULL),(218,14,'/rag/assistant',6490,'2025-08-24 06:40:16','success',NULL),(219,5,'/rag/assistant',5381,'2025-08-24 06:42:13','success',NULL),(220,5,'/rag/assistant',8988,'2025-08-24 06:44:49','success',NULL),(221,14,'/rag/assistant',8011,'2025-08-24 06:50:43','success',NULL),(222,5,'/rag/assistant',3506,'2025-08-24 06:51:06','success',NULL),(223,5,'/rag/assistant',10331,'2025-08-24 06:51:47','success',NULL),(224,4,'/rag/assistant',14134,'2025-08-24 13:33:47','success',NULL),(225,4,'/rag/assistant',3079,'2025-08-24 23:36:01','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"Connection error.\"}\"'),(226,4,'/rag/assistant',1366,'2025-08-24 23:38:51','fail','500 Internal Server Error on POST request for \"http://localhost:8000/rag/assistant\": \"{\"detail\":\"Connection error.\"}\"'),(227,4,'/rag/assistant',15979,'2025-08-24 23:41:30','success',NULL),(228,4,'/embedding/upload',930,'2025-08-24 23:41:53','success',NULL),(229,4,'/rag/assistant',10029,'2025-08-24 23:42:24','success',NULL),(230,4,'/rag/assistant',8692,'2025-08-24 23:43:25','success',NULL),(231,4,'/rag/assistant',6788,'2025-08-24 23:44:01','success',NULL),(232,4,'/rag/generate',42212,'2025-08-24 23:48:05','success',NULL),(233,4,'/rag/detail',27729,'2025-08-24 23:49:20','success',NULL),(234,4,'/rag/evaluate_subjective',15237,'2025-08-24 23:53:50','success',NULL),(235,4,'/rag/evaluate_subjective',15237,'2025-08-24 23:53:50','success',NULL),(236,4,'/rag/evaluate_subjective',5977,'2025-08-24 23:54:21','success',NULL),(237,4,'/rag/evaluate_subjective',15689,'2025-08-24 23:54:31','success',NULL),(238,5,'/rag/generate_selected_student_exercise',6969,'2025-08-25 00:26:52','success',NULL),(239,5,'/rag/generate_student_exercise',5122,'2025-08-25 00:27:11','success',NULL),(240,5,'/rag/generate_selected_student_exercise',6450,'2025-08-25 00:29:20','success',NULL),(241,5,'/rag/generate_student_exercise',7639,'2025-08-25 00:30:19','success',NULL),(242,4,'/rag/generate_exercise',42136,'2025-08-25 00:42:17','success',NULL),(243,4,'/rag/generate',63316,'2025-08-25 07:51:35','success',NULL),(244,4,'/rag/detail',36319,'2025-08-25 07:52:53','success',NULL),(245,4,'/rag/generate_exercise',42,'2025-08-25 07:57:55','fail','422 Unprocessable Entity on POST request for \"http://localhost:8000/rag/generate_exercise\": \"{\"detail\":[{\"type\":\"int_parsing\",\"loc\":[\"body\",\"choose_count\"],\"msg\":\"Input should be a valid integer, unable to parse string as an integer\",\"input\":\"\",\"url\":\"https://errors.pydantic.dev/2.11/v/int_parsing\"}]}\"'),(246,4,'/rag/generate_exercise',15757,'2025-08-25 07:58:23','success',NULL),(247,4,'/rag/evaluate_subjective',29966,'2025-08-25 08:01:18','success',NULL),(248,4,'/rag/evaluate_subjective',29971,'2025-08-25 08:01:18','success',NULL),(249,4,'/rag/assistant',6802,'2025-08-25 08:06:47','success',NULL),(250,4,'/rag/assistant',11950,'2025-08-25 08:09:19','success',NULL),(251,4,'/rag/assistant',7367,'2025-08-25 08:09:58','success',NULL),(252,8,'/rag/assistant',3224,'2025-08-25 08:11:41','success',NULL),(253,8,'/embedding/upload',643,'2025-08-25 08:18:07','success',NULL),(254,8,'/rag/assistant',10191,'2025-08-25 08:18:41','success',NULL),(255,4,'/rag/assistant',14086,'2025-08-25 08:31:14','success',NULL),(256,8,'/embedding/upload',200337,'2025-08-25 08:35:49','fail','I/O error on POST request for \"http://localhost:8000/embedding/upload\": Connection reset'),(257,15,'/embedding/upload',2524,'2025-08-25 08:42:45','success',NULL),(258,15,'/embedding/upload',303057,'2025-08-25 08:48:38','success',NULL),(259,15,'/embedding/upload',1841923,'2025-08-25 09:20:00','fail','500 Internal Server Error on POST request for \"http://localhost:8000/embedding/upload\": \"{\"detail\":\"Connection error.\"}\"'),(260,15,'/embedding/upload',169984,'2025-08-25 10:24:54','fail','500 Internal Server Error on POST request for \"http://localhost:8000/embedding/upload\": \"{\"detail\":\"Request timed out.\"}\"'),(261,15,'/embedding/upload',226154,'2025-08-25 10:34:32','fail','500 Internal Server Error on POST request for \"http://localhost:8000/embedding/upload\": \"{\"detail\":\"Connection error.\"}\"'),(262,15,'/embedding/upload',75329,'2025-08-25 11:19:54','success',NULL),(263,4,'/rag/generate',54757,'2025-08-25 12:28:22','success',NULL),(264,4,'/rag/detail',26523,'2025-08-25 12:29:48','success',NULL),(265,4,'/rag/detail',31401,'2025-08-25 12:30:46','success',NULL),(266,4,'/rag/generate_exercise',18121,'2025-08-25 12:34:30','success',NULL),(267,4,'/rag/assistant',10207,'2025-08-25 12:38:26','success',NULL),(268,4,'/embedding/upload',653,'2025-08-25 12:39:14','success',NULL),(269,4,'/rag/assistant',4229,'2025-08-25 12:39:34','success',NULL),(270,4,'/rag/assistant',15464,'2025-08-25 12:42:45','success',NULL),(271,5,'/rag/evaluate_subjective',7806,'2025-08-25 12:44:59','success',NULL),(272,5,'/rag/evaluate_subjective',13157,'2025-08-25 12:45:04','success',NULL),(273,5,'/rag/evaluate_subjective',19680,'2025-08-25 12:45:11','success',NULL),(274,5,'/rag/generate_selected_student_exercise',10205,'2025-08-25 12:47:25','success',NULL),(275,4,'/rag/assistant',10281,'2025-08-25 12:50:30','success',NULL),(276,4,'/rag/assistant',3842,'2025-08-25 12:50:52','success',NULL),(277,8,'/embedding/upload',554,'2025-08-25 12:52:45','success',NULL),(278,8,'/rag/assistant',4597,'2025-08-25 12:53:02','success',NULL),(279,8,'/embedding/upload',320288,'2025-08-25 12:59:23','success',NULL),(280,4,'/rag/generate',40970,'2025-08-26 00:29:55','success',NULL),(281,4,'/rag/detail',48979,'2025-08-26 00:31:12','success',NULL),(282,4,'/rag/evaluate_subjective',16026,'2025-08-26 00:35:42','success',NULL),(283,4,'/rag/evaluate_subjective',23120,'2025-08-26 00:35:50','success',NULL),(284,4,'/rag/generate_exercise',21204,'2025-08-26 00:37:07','success',NULL),(285,4,'/rag/assistant',5508,'2025-08-26 00:40:19','success',NULL),(286,8,'/embedding/upload',570,'2025-08-26 00:41:18','success',NULL),(287,8,'/rag/assistant',4895,'2025-08-26 00:41:33','success',NULL),(288,16,'/rag/evaluate_subjective',10946,'2025-08-26 00:46:11','success',NULL),(289,5,'/rag/generate_student_exercise',8108,'2025-08-26 00:48:11','success',NULL),(290,4,'/rag/generate',44097,'2025-08-26 05:28:07','success',NULL),(291,4,'/rag/detail',38334,'2025-08-26 05:29:10','success',NULL),(292,4,'/embedding/upload',8886,'2025-08-26 05:31:19','success',NULL),(293,4,'/rag/assistant',9995,'2025-08-26 05:32:15','success',NULL),(294,4,'/rag/assistant',7696,'2025-08-26 05:34:17','success',NULL),(295,4,'/rag/generate',73581,'2025-08-26 05:58:14','success',NULL),(296,4,'/rag/detail',28825,'2025-08-26 05:59:15','success',NULL),(297,4,'/rag/evaluate_subjective',9298,'2025-08-26 06:02:22','success',NULL),(298,4,'/rag/generate_exercise',39987,'2025-08-26 06:04:04','success',NULL),(299,4,'/rag/assistant',15029,'2025-08-26 06:06:11','success',NULL),(300,4,'/rag/assistant',7811,'2025-08-26 06:07:20','success',NULL),(301,5,'/rag/evaluate_subjective',12503,'2025-08-26 06:08:48','success',NULL),(302,5,'/rag/evaluate_subjective',21567,'2025-08-26 06:08:57','success',NULL),(303,5,'/rag/evaluate_subjective',26942,'2025-08-26 06:09:03','success',NULL),(304,14,'/rag/assistant',8534,'2025-08-26 06:25:18','success',NULL),(305,14,'/rag/assistant',10959,'2025-08-26 06:25:47','success',NULL),(306,14,'/rag/assistant',9572,'2025-08-26 06:26:31','success',NULL),(307,14,'/rag/assistant',9113,'2025-08-26 06:27:24','success',NULL),(308,4,'/rag/generate',52245,'2025-08-26 06:46:42','success',NULL),(309,4,'/rag/detail',34574,'2025-08-26 06:47:39','success',NULL),(310,4,'/rag/evaluate_subjective',8201,'2025-08-26 06:48:30','success',NULL),(311,5,'/rag/evaluate_subjective',5764,'2025-08-26 06:49:42','success',NULL),(312,5,'/rag/evaluate_subjective',9621,'2025-08-26 06:49:45','success',NULL),(313,5,'/rag/evaluate_subjective',17900,'2025-08-26 06:49:54','success',NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `answer`
--

LOCK TABLES `answer` WRITE;
/*!40000 ALTER TABLE `answer` DISABLE KEYS */;
INSERT INTO `answer` VALUES (1,1,1,'1NF|2NF|3NF',1,1,10,1),(2,2,29,'A',1,1,5,0),(3,2,30,'A',1,1,1,1),(4,2,31,'A',1,1,1,2),(5,2,32,'不会',1,0,0,3),(6,2,34,'不会',1,0,1,4),(7,2,35,'不会',1,0,1,5),(8,3,29,'A',1,1,5,0),(9,3,30,'A',1,1,1,1),(10,3,31,'B',1,0,0,2),(11,3,32,'',1,0,0,3),(12,3,34,'',0,0,0,4),(13,3,35,'',0,0,0,5),(14,4,29,'B',1,0,0,0),(15,4,30,'C',1,0,0,1),(16,4,31,'C',1,0,0,2),(17,4,32,'不知道',1,0,0,3),(18,4,34,'const model=tf.sequential();\nmodel.add(tf.layer.dense({units:1,inputShape:[1]}));\nmodel.compile({loss:\'meanSquaredError\',optimizer: \'sgd\']}\'\n',0,0,0,4),(19,4,35,'const model=tf.sequential();',0,0,0,5),(20,5,29,'B',1,0,0,0),(21,5,30,'B',1,0,0,1),(22,5,31,'B',1,0,0,2),(23,5,32,'不知道',1,0,0,3),(24,5,34,'求求你给我一分吧，老师最好了',1,0,0,4),(25,5,35,'先定义一个模型，',1,0,0,5),(26,6,29,'A',1,1,5,0),(27,6,30,'A',1,1,1,1),(28,6,31,'A',1,1,1,2),(29,6,32,'不会',1,0,0,3),(30,6,34,'先定义一个模型，定义节点为1，MSE为loss,通过梯度下降法得到loss极小值，训练稳定后得到模型参数',0,0,0,4),(31,6,35,'老师菜菜捞捞，嘤嘤嘤',0,0,0,5),(32,7,29,'A',1,1,5,0),(33,7,30,'A',1,1,1,1),(34,7,31,'A',1,1,1,2),(35,7,32,'不知道',1,0,0,3),(36,7,34,'定义一个模型',0,0,0,4),(37,7,35,'老师菜菜姥姥',0,0,0,5),(38,8,29,'A',1,1,5,0),(39,8,30,'A',1,1,1,1),(40,8,31,'A',1,1,1,2),(41,8,32,'不会',1,0,0,3),(42,8,34,'要定义一个模型，设置输入维度2，输出维度1的网络，loss使用MSE,利用梯度下降法得到loss最低点，loss稳定的参数值即为所求',0,0,0,4),(43,8,35,'老师菜菜捞捞',0,0,0,5),(44,9,4,'A',1,1,5,0),(45,9,6,'C',1,0,0,1),(46,9,8,'不知道',1,0,0,2),(47,9,34,'const xs= tf.tensor1d([1,2,3,4,5]);\nconst ys=tf.tensor1d([2,4,6,8,10]);//(y=2x)\n//定义模型结构\nconst model=tf.sequential();\nmodel.add(tf.layers.dense({\nunits:1, //一个神经元\ninputShape: [1] //输入形状1维\n}));\n//编译模型\nmodel.compile({\noptimizer: tf.train.sgd(0,1),\nloss: \'meanSquaredError’//均方误差\n});\n//训练模型\nasync function trainModel(){\nconst history =await model.fit(xs,ys,{\nepochs:100,\nbatchsize:5,\ncallbacks:{\nonEpochEnd:(epoch,logs)=>{\nif(epoch%10 ===0){\nconsole.log(\'Epoch$(epoch):loss=${logs.loss}\');\n}\n}\n}\n});',1,0,8,3),(48,9,36,'A',1,1,1,4),(49,10,4,'A',1,1,5,0),(50,10,6,'C',1,0,0,1),(51,10,8,'tf.tensor()',1,1,5,2),(52,10,34,'const xs,ys;\nconst model=tf.sequential();\nmodel.add(tf.layers.dense({\nunits:1,\ninputShape:[1]\n}));\nmodel.compile({\noptimizer:tf.train.sgd(0.9),\nloss: \"meanSquaredError\"\n});\n',1,0,1,3),(53,10,36,'A',1,1,1,4),(54,11,53,'111',0,0,0,0),(55,11,76,'22111',0,0,0,1),(56,11,77,'11',0,0,0,2),(57,12,4,'A',1,1,5,0),(58,12,6,'A',1,1,5,1),(59,12,8,'11',1,0,0,2),(60,12,34,'22',0,0,0,3),(61,12,36,'A',1,1,1,4),(62,13,53,'1111',0,0,0,0),(63,13,76,'1111111',0,0,0,1),(64,13,77,'111',0,0,0,2),(65,14,53,'1',0,0,0,0),(66,14,76,'1',0,0,0,1),(67,14,77,'1',0,0,0,2);
/*!40000 ALTER TABLE `answer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_memory_summary`
--

DROP TABLE IF EXISTS `chat_memory_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_memory_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `summary_content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `message_range_start` bigint NOT NULL,
  `message_range_end` bigint NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `token_count` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  KEY `idx_session_range` (`session_id`,`message_range_start`,`message_range_end`),
  CONSTRAINT `chat_memory_summary_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_memory_summary`
--

LOCK TABLES `chat_memory_summary` WRITE;
/*!40000 ALTER TABLE `chat_memory_summary` DISABLE KEYS */;
/*!40000 ALTER TABLE `chat_memory_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `chat_message`
--

DROP TABLE IF EXISTS `chat_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `chat_message` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `session_id` bigint NOT NULL,
  `role` enum('user','assistant') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `references` json DEFAULT NULL,
  `knowledge_points` json DEFAULT NULL,
  `message_order` int NOT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `token_count` int DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `session_id` (`session_id`),
  KEY `idx_session_order` (`session_id`,`message_order`),
  KEY `idx_created_at` (`created_at`),
  CONSTRAINT `chat_message_ibfk_1` FOREIGN KEY (`session_id`) REFERENCES `chat_session` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=130 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_message`
--

LOCK TABLES `chat_message` WRITE;
/*!40000 ALTER TABLE `chat_message` DISABLE KEYS */;
INSERT INTO `chat_message` VALUES (1,12,'user','hello',NULL,NULL,1,'2025-08-22 11:27:07',1),(2,12,'user','你是什么模型？',NULL,NULL,2,'2025-08-22 11:29:36',10),(3,12,'user','你好啊',NULL,NULL,3,'2025-08-22 11:53:08',5),(4,12,'user','1+1=',NULL,NULL,4,'2025-08-22 12:14:01',1),(5,12,'assistant','在数学中，1+1的结果是2。这是基本的算术运算，表示将一个单位与另一个相同的单位相加，总共得到两个单位的和。','[{\"source\": \"upload-1262541090359221833-02 TensorFlow.js框架_170151.mp4 440.0-442.0s\", \"content\": \"提到相加的概念，即主元素相加\"}]','[\"算术运算\", \"数值加法\", \"单位相加\"]',5,'2025-08-22 12:14:05',68),(6,12,'user','你还记得我都问过什么问题吗',NULL,NULL,6,'2025-08-22 12:15:57',20),(7,12,'assistant','根据我的记录，我无法准确记得每位学生具体问过的问题，因为每次对话都是独立的，我无法回溯过去的具体细节。我能做的，是基于当前对话为学生们提供即时的帮助和支持。','[{\"source\": \"10.2 qtopia_180857.mp4 860.0-863.0s\", \"content\": \"你可能不知道是哪里出了问题了\"}]','[\"对话系统的局限性\", \"基于记录的问答机制\", \"无法追溯过往对话的限制\"]',7,'2025-08-22 12:16:02',109),(8,13,'user','下面请称呼我为小彭，你好！',NULL,NULL,1,'2025-08-22 12:24:24',17),(9,13,'assistant','你好，小彭！欢迎参与学习交流，如果你有任何问题或需要帮助的地方，请随时告诉我。','[{\"source\": \"10.4 hello_182349.mp4 15.5-16.5s\", \"content\": \"课程开场白中提到了\'大家好\'，这是教师与学生进行互动的常用方式。\"}, {\"source\": \"10.5 singal_182823.mp4 16.0-17.3s\", \"content\": \"课程内容中出现了\'同学们好\'，体现了师生之间的友好交流。\"}]','[\"师生交流规范\", \"课程中的开场白使用\"]',2,'2025-08-22 12:24:36',52),(10,13,'user','我叫什么名字？',NULL,NULL,3,'2025-08-22 12:24:44',10),(11,13,'assistant','根据对话记录，你之前介绍过自己叫小彭。如果你现在想更改称呼或者有其他名字需要确认，可以告诉我，我会按照你的要求来称呼你。','[{\"source\": \"历史对话\", \"content\": \"学生1称自己为小彭，助教2欢迎小彭参与学习交流\"}]','[\"学生身份确认\", \"学习交流中的称呼规范\"]',4,'2025-08-22 12:24:51',83),(12,14,'user','我想我们这门课程会学些什么',NULL,NULL,1,'2025-08-22 12:38:54',20),(13,14,'assistant','在这门Linux开发实践课程中，你将学习关于Linux操作系统的基础知识及其在开发中的实际应用。具体包括如何在Linux环境下编写和运行程序，初步涉及最简单的程序开发，例如编写Hello World程序，以及如何使用QT进行开发。此外，课程还会简要介绍QT框架的基本情况，并引导同学们安装QT开发环境，例如使用QTCREATOR进行开发。除了应用层面的开发，课程也会提到Linux与微控制器的关联以及如何嵌入式开发中使用Linux。','[{\"source\": \"10.3 qmake_181344.mp4 511.1-514.1s\", \"content\": \"提到了Linux在MBA领域的应用\"}, {\"source\": \"10.1 qt_181927.mp4 686.5-695.5s\", \"content\": \"介绍了QT的基本情况，并建议安装QTCREATE进行开发\"}, {\"source\": \"1112_222613.mp4 273.2-277.4s\", \"content\": \"涉及Linux与微控制器的结合\"}, {\"source\": \"10.4 hello_182349.mp4 25.5-29.3s\", \"content\": \"讲解了如何编写一个最简单的程序\"}, {\"source\": \"10.4 hello_182349.mp4 819.9-823.9s\", \"content\": \"介绍了最简单的QT程序开发\"}]','[\"Linux开发基础\", \"编写简单的Linux程序\", \"QT开发环境配置\", \"QT程序开发入门\", \"Linux与微控制器的结合\"]',2,'2025-08-22 12:39:04',232),(14,14,'user','TensorFlow是什么？',NULL,NULL,3,'2025-08-22 12:39:28',5),(15,14,'assistant','在Linux开发实践的上下文中，TensorFlow指的是TensorFlow Lite，它是TensorFlow生态系统的一部分，专为移动设备、嵌入式设备和IoT设备设计。TensorFlow Lite提供了一组工具，帮助开发者在这些资源受限的设备上运行机器学习模型。它具有轻量级、快速推理和较小的二进制文件等特点，支持跨平台运行，包括基于Linux的IoT设备和微控制器。这使得在Linux开发实践中，可以将机器学习技术应用到边缘计算场景。','[{\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第39段\", \"content\": \"TensorFlow Lite 是一组工具，可帮助开发者在移动设备、嵌入式设备和 IoT 设备上运行 TensorFlow 模型。它支持设备端机器学习推断，延迟较低，并且二进制文件很小。\"}, {\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第22段\", \"content\": \"2015 年底Google 开源了端到端的机器学习开源框架 TensorFlow，它既支持大规模的模型训练，也支持各种环境的部署，包括服务器和移动端的部署，支持各种语言，包括 Python，C++，Java，Swift 甚至 Javascript。而近年来移动化浪潮和交互方式的改变，使得机器学习技术开发也在朝着轻量化的端侧发展，TensorFlow 团队又在 2017 年底上线了 TensorFlow Lite，一个轻量、快速、兼容度高的专门针对移动式应用场景的深度学习工具，把移动端及 IoT 设备端的深度学习技术的门槛再次大大降低。\"}, {\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第24段\", \"content\": \"TFLite是在边缘设备上运行TensorFlow模型推理的官方框架，它跨平台运行，包括Android、iOS以及基于Linux的IoT设备和微控制器。\"}]','[\"TensorFlow Lite的定义\", \"TensorFlow Lite在移动设备和嵌入式设备中的应用\", \"TensorFlow Lite对Linux设备的支持\", \"TensorFlow生态系统的多样性\"]',4,'2025-08-22 12:39:47',210),(16,15,'user','你觉得这门课在大学应该放在哪个学期来开？学科的性质（学分、学时、考核方式）是怎样的？并给我一份合适的教学大纲',NULL,NULL,1,'2025-08-22 13:03:13',73),(17,15,'assistant','Linux开发实践这门课适合在大学的第二学年下学期或第三学年开始，因为学生需要具备一定的编程基础和对操作系统的基本理解。这门课程通常为选修课，建议设置为3个学分，周学时为4课时，其中包含2课时实验实践。考核方式建议以实验和项目实践为主（占比60%），辅以理论考试（占比40%）。\n\n教学大纲建议如下：\n1. 课程介绍与Linux基础：介绍Linux系统特点、开发环境搭建以及常用命令。\n2. Shell脚本编程：讲解Shell脚本的编写与调试，掌握自动化任务处理。\n3. Linux系统编程基础：包括文件操作、进程管理、线程与多线程编程。\n4. Linux网络编程：讲解Socket编程、TCP/UDP通信等。\n5. 嵌入式Linux开发简介：结合微控制器，介绍嵌入式系统的开发流程。\n6. 开发工具与调试：介绍qmake、gdb等工具的使用，提高开发效率。\n7. GUI开发实践（可选）：介绍Qt框架，使用Qt Creator进行图形界面开发。\n8. 综合项目实践：完成一个基于Linux的完整开发项目，如简易服务器或嵌入式应用。\n\n通过本课程的学习，学生将具备在Linux环境下进行系统级开发的能力，并能应用于实际项目中。','[{\"source\": \"10.3 qmake_181344.mp4\", \"content\": \"介绍了qmake的使用，适用于Linux开发中的项目构建。\"}, {\"source\": \"1112_222613.mp4\", \"content\": \"提到了Linux与微控制器的关系，适用于嵌入式Linux开发部分的教学内容。\"}, {\"source\": \"10.1 qt_181927.mp4\", \"content\": \"介绍了Qt框架及其开发工具Qt Creator，可用于Linux下的GUI开发教学模块。\"}]','[\"Linux开发实践课程设置与学期安排\", \"课程学分与学时设计\", \"考核方式建议\", \"教学大纲内容与模块划分\", \"qmake在Linux开发中的应用\", \"Qt框架与GUI开发\", \"嵌入式Linux与微控制器结合\"]',2,'2025-08-22 13:03:33',533),(18,16,'user','你觉得这门课在大学应该放在哪个学期来开？学科的性质（学分、学时、考核方式）是怎样的？并给我一份合适的教学大纲',NULL,NULL,1,'2025-08-22 13:05:02',73),(19,16,'assistant','对于《Linux开发实践》这门课，考虑到它属于软件工程专业的实践类课程，最适合开在大学三年级的秋季或春季学期。这是因为在学习Linux开发实践之前，学生需要具备一定的基础，如操作系统、程序设计、数据结构等。根据软件工程专业的课程安排，到二年级结束时，学生已经掌握了这些基础知识，从而能够更好地理解和应用Linux开发的相关技能。\n\n关于课程性质，这门课程应该设置为专业选修课，总学分建议为2.5学分，总学时为48学时（其中理论课32学时，实验课16学时）。课程以考查为主要考核方式，授课语言为全汉语。\n\n以下是《Linux开发实践》的教学大纲示例：\n\n1. 课程简介\n   - Linux系统概述\n   - Linux在软件开发中的重要性\n\n2. Linux基础\n   - 安装与配置Linux环境\n   - 常用命令与Shell脚本编写\n\n3. Linux文件系统与权限管理\n   - 文件系统结构\n   - 文件权限与用户管理\n\n4. Linux程序开发环境\n   - 使用GCC编译器\n   - 调试工具GDB的使用\n   - Makefile编写与自动化编译\n\n5. Linux多任务与进程管理\n   - 进程概念与生命周期\n   - 进程调度与资源管理\n\n6. Linux网络编程\n   - 网络基础与Socket编程\n   - TCP/IP通信实践\n\n7. Linux系统编程\n   - 文件IO操作\n   - 内存管理与多线程编程\n\n8. Linux内核基础\n   - 内核模块开发简介\n   - 系统调用机制\n\n9. Linux安全与优化\n   - 系统日志与监控\n   - 常见安全加固措施\n\n10. 项目实践\n    - Linux平台软件开发项目实战\n    - 团队协作与版本控制（Git）','[{\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第7页\", \"content\": \"软件学院·软件工程专业课程信息：课程性质、学分、学时、理论学时、实验学时、实践学时、学年、学期、考核方式等信息。\"}, {\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第5页\", \"content\": \"提供了软件工程专业核心课程的信息，包括必修课程的学分、学时分布以及考核方式，为课程性质安排提供了参考。\"}]','[\"Linux开发实践课程适合开设的学期\", \"课程性质：学分、学时、理论与实验比例\", \"课程考核方式\", \"教学大纲设计原则\", \"Linux开发实践课程内容模块\"]',2,'2025-08-22 13:05:32',716),(20,17,'user','你觉得这门课在大学应该放在哪个学期来开？学科的性质（学分、学时、考核方式）是怎样的？并给我一份合适的教学大纲',NULL,NULL,1,'2025-08-22 13:06:17',73),(21,17,'assistant','Linux开发实践课程作为一门具有较强实践性的专业课程，适合在大学三年级秋季学期开设。这门课程建议设置为2学分，总学时为32学时，其中理论学时16学时，实验学时16学时。考核方式建议为考查，以注重过程性评价。以下是教学大纲示例：\n1. 课程目标：掌握Linux系统的基本开发环境与工具，熟悉Linux系统编程、Shell脚本编写和调试方法，能够使用C/C++语言进行Linux环境下的应用程序开发。\n2. 课程内容：\n   - Linux系统概述与基本操作\n   - Linux开发环境搭建（如GCC、GDB等）\n   - Shell脚本编程基础\n   - Linux文件系统与进程管理\n   - Linux系统编程（如系统调用、多进程/多线程编程）\n   - Linux网络编程基础\n   - 项目实践与综合实验\n3. 教学安排：课程采用理论+实验结合的方式，理论教学占50%，实验教学占50%。\n4. 考核方式：平时成绩（课堂互动、实验作业）占70%，期末项目实践占30%。','[{\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第5页\", \"content\": \"操作系统课程为必修课，4.5学分，96总学时，理论学时48，实验学时48，二年级春季学期开设，考核方式为考试，体现了理论与实验并重的特点。\"}, {\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第7页\", \"content\": \"选修课程如数据挖掘导论和计算机网络安全技术等多设置为2-2.5学分，总学时在32-48之间，考核方式包括考试与考查，课程性质注重理论结合实践。\"}]','[\"Linux开发实践课程的开课学期安排建议\", \"课程学分与学时分配（总学时、理论学时、实验学时）\", \"课程考核方式的选择与设计\", \"教学大纲的结构与内容构成\", \"Linux开发实践课程的实践教学占比\"]',2,'2025-08-22 13:06:43',424),(22,18,'user','你觉得Linux开发实践这门课在大学应该放在哪个学期来开？学科的性质（学分、学时、考核方式）是怎样的？并给我一份合适的教学大纲',NULL,NULL,1,'2025-08-22 13:07:26',79),(23,18,'assistant','Linux开发实践这门课程在大学的开课时间应当安排在第二学年的春学期或第三学年的秋学期较为合适。这是因为Linux开发实践涉及操作系统、程序设计基础以及软件工程相关知识，学生需要一定的计算机基础和编程能力作为支撑。该课程的性质可以设置为专业必修课，建议设置为4学分，总学时约96学时，其中理论学时48，实验学时48，以确保学生能够掌握理论知识并结合实践应用。\n\n考核方式可以采用考试与实践相结合的形式，既注重学生对理论知识的掌握，也强调实践能力。例如，可以将期末考试与课程项目结合，其中考试占总成绩的50%，项目实践占30%，平时作业和实验占20%。\n\n教学大纲可以分为以下几个模块：\n1. Linux系统基础：介绍Linux的历史、特点、安装和基本命令。\n2. Shell脚本编程：学习编写Shell脚本进行自动化任务处理。\n3. Linux系统管理：包括文件系统管理、用户和权限管理、网络配置、软件包管理等。\n4. Linux内核基础：学习Linux内核架构、模块管理、系统调用等。\n5. Linux开发环境：介绍GCC编译器、GDB调试工具、Makefile编写、版本控制工具Git等。\n6. Linux应用开发：基于C/C++进行Linux环境下的应用开发，涉及进程控制、线程管理、网络编程等。\n7. Linux驱动开发基础：介绍设备驱动的基本概念和编写方法。\n8. 课程项目实践：通过开发一个完整的Linux应用或系统工具，巩固所学内容。','[{\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第5页\", \"content\": \"软件学院课程列表中操作系统课程为4.5学分，96总学时，理论和实验各48学时，开课时间为第二学年春季学期，为必修考试课程。\"}, {\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第7页\", \"content\": \"专业限选课程设置及学分分布，强调实践与理论结合，并建议考核方式多样化。\"}, {\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第3页\", \"content\": \"关于实验实践学时的安排，如军事技能课程包含112实践学时，体现大学对实践教学的重视。\"}]','[\"Linux开发实践课程适合的开课学期\", \"课程性质设置（学分、学时、考核方式）\", \"Linux开发实践教学大纲模块设计\", \"操作系统课程的设置参考\", \"实践教学在课程设计中的重要性\"]',2,'2025-08-22 13:07:51',671),(24,19,'user','根据你了解的这门课程的信息，结合培养方案，制定出一版课程大纲',NULL,NULL,1,'2025-08-22 13:09:00',43),(25,19,'assistant','根据课程资料和培养方案，制定的《Linux开发实践》课程大纲如下：\n\n1. 课程简介\n   - 介绍Linux系统的基本概念及其在软件工程和开发实践中的重要性。\n   - 课程目标包括帮助学生掌握Linux开发环境的搭建、使用以及常见开发工具的应用。\n\n2. 理论教学内容\n   - Linux操作系统原理概述：包括进程管理、内存管理、文件系统等基本知识。\n   - Shell脚本编程：学习Shell编程语言，掌握自动化脚本编写。\n   - Linux开发工具：介绍GCC、GDB、Makefile、CMake等工具的基本使用。\n   - 开源软件开发模式：理解Linux开源生态及其协作开发模式。\n\n3. 实践教学内容\n   - Linux系统环境配置：包括安装Linux操作系统、配置网络、管理用户和权限。\n   - 基于Linux的程序开发：完成C/C++程序在Linux环境下的编译、调试与运行。\n   - Linux内核模块开发：学习编写简单的内核模块，了解内核开发的基本流程。\n   - 软件项目实践：通过实际项目，实现一个完整的Linux应用或工具开发。\n\n4. 学时分配\n   - 总学时：32学时\n   - 理论教学：24学时\n   - 实验教学：8学时\n   - 课程考核方式：考试+实践项目评估\n\n5. 课程目标\n   - 培养学生在Linux环境下进行软件开发的能力，强化实践操作技能。\n   - 为学生未来从事嵌入式系统开发、云计算、大数据、网络安全等相关方向的学习和研究打下基础。\n\n6. 与培养方案的衔接\n   - 本课程与软件工程专业课程中的《操作系统》《并行程序设计》等课程相衔接，为后续学习提供操作系统开发和调试能力支持。\n   - 为《分布式系统导论》《嵌入式软件设计》等方向提供底层开发环境和工具支持。','[{\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第5页\", \"content\": \"核心课程包括操作系统、软件工程基础、计算机网络与应用等，提供理论与实践结合的教学内容。\"}, {\"source\": \"upload-7255503534525479779-021-软件学院-软件工程-2023级课程清单_123957.pdf 第6页\", \"content\": \"选修课程包括嵌入式软件设计、工业互联网技术基础等，强调Linux系统在软件工程实践中的应用。\"}]','[\"Linux操作系统原理\", \"Shell脚本编程\", \"GCC/GDB工具链使用\", \"Linux内核模块开发\", \"开源软件开发模式\", \"Linux开发环境搭建与配置\"]',2,'2025-08-22 13:09:28',774),(26,19,'user','大纲应该结合课上的内容',NULL,NULL,3,'2025-08-22 13:10:26',17),(27,19,'assistant','根据课程资料和实际授课内容，我们可以进一步优化《Linux开发实践》课程大纲，使其更贴合课堂实践和教学需求。课程应重点结合实际开发场景，涵盖Linux开发工具的使用、嵌入式环境开发、项目实践等内容。比如在课堂上已经提到的与ARM平台结合的Linux开发、项目代码从Git获取等实践内容，都应当在大纲中具体体现。此外，对于有经验的学生，可以增加大型程序开发和开源协作项目的模块，以提升其综合开发能力。','[{\"source\": \"10.2 qtopia_180857.mp4 324.0-327.0s\", \"content\": \"提到了Linux与ARM平台的结合，表明课程涉及嵌入式Linux开发相关内容。\"}, {\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 110.4-119.6s\", \"content\": \"说明课程项目中涉及到从GitHub获取项目资源，强调了实际项目开发中资源获取的实践能力。\"}, {\"source\": \"upload-1262541090359221833-02 TensorFlow.js框架_170151.mp4 132.0-137.0s\", \"content\": \"提到对有经验学生可扩展开发更大程序，表明课程内容具有分层教学和进阶开发的引导性。\"}]','[\"Linux与ARM平台的结合开发\", \"从GitHub获取项目资源的实践\", \"分层教学：支持不同经验水平的项目开发\", \"嵌入式Linux开发\", \"大型程序开发与开源协作\"]',4,'2025-08-22 13:10:41',244),(28,19,'user','对的，根据课程中的TensorFlow\\嵌入式Linux开发等内容生成一般课程大纲',NULL,NULL,5,'2025-08-22 13:11:38',37),(29,19,'assistant','根据课程内容中提到的TensorFlow Lite与嵌入式Linux开发相关内容，下面是一版针对性的《Linux开发实践》课程大纲建议：\n\n1. 课程简介\n   - 介绍Linux系统的基本概念及其在开发实践中的重要性。\n   - 课程目标包括掌握Linux开发环境的搭建与使用、开发工具的应用，以及针对嵌入式Linux系统的TensorFlow Lite部署开发。\n\n2. 理论教学内容\n   - Linux操作系统基础：涵盖进程管理、内存管理、文件系统等核心内容。\n   - Shell脚本编程：学习Shell语言，编写自动化脚本。\n   - Linux开发工具应用：详细讲解GCC、GDB、Makefile和CMake工具的使用。\n   - 嵌入式Linux开发概述：了解嵌入式系统的基本特点及其与Linux的关系。\n   - TensorFlow Lite原理与应用场景：学习TensorFlow Lite框架，了解其在边缘计算中的应用。\n\n3. 实践教学内容\n   - Linux系统环境配置：包括安装、网络配置、用户管理与权限控制。\n   - Linux程序开发实践：在Linux环境中进行C/C++程序的编译、调试与运行。\n   - TensorFlow Lite开发工作流程：熟悉从模型准备到部署的全过程。\n   - 在嵌入式Linux系统上部署TensorFlow Lite：以Raspberry Pi或ARM64主板为例，实现模型在嵌入式平台的运行。\n   - 开源协作与项目开发：完成一个与TensorFlow Lite相关的实际项目，例如实现一个机器学习应用。\n\n4. 学时分配\n   - 理论课程：20学时\n   - 实践课程：30学时\n   - 项目实践：10学时\n\n本课程将紧密结合实际开发需求，强调Linux开发工具的使用以及TensorFlow Lite在嵌入式Linux系统中的应用，帮助学生提升综合开发能力。','[{\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第92段\", \"content\": \"8.3 任务1：TensorFlow Lite开发工作流程\"}, {\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第199段\", \"content\": \"TensorFlow Lite解释器可以部署在Raspberry Pi和基于Arm64的嵌入式Linux系统上\"}, {\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第549段\", \"content\": \"TensorFlow官网提供了许多TensorFlow Lite示例应用，并支持Raspberry Pi上的应用实现\"}, {\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第9段\", \"content\": \"了解TensorFlow Lite的应用\"}, {\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第31段\", \"content\": \"8.1.2 TensorFlow Lite的应用\"}]','[\"Linux操作系统原理概述\", \"Shell脚本编程\", \"Linux开发工具使用（如GCC、GDB、Makefile、CMake）\", \"嵌入式Linux开发基础\", \"TensorFlow Lite开发工作流程\", \"TensorFlow Lite在Raspberry Pi或ARM64平台的部署\", \"机器学习应用的实际项目开发\"]',6,'2025-08-22 13:12:06',684),(30,20,'user','请告诉我如何进行手写数字识别？',NULL,NULL,1,'2025-08-23 03:35:46',22),(31,20,'assistant','在Linux开发实践中进行手写数字识别，通常会结合机器学习框架如TensorFlow.js或TensorFlow Lite来实现。具体步骤包括：1）准备手写数字数据集，例如MNIST数据集；2）使用TensorFlow.js训练一个卷积神经网络（CNN）模型来识别手写数字；3）将训练好的模型部署到Linux环境中，可能使用TensorFlow Lite进行模型优化和嵌入式部署。这一过程涉及到Linux系统下的模型开发、测试和部署实践。','[{\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 0.0-15.6s\", \"content\": \"介绍使用TensorFlow.js模型，并通过卷积神经网络识别手写数字\"}, {\"source\": \"upload-5827127634749586247-01 认识TensorFlow.js-1_170810.mp4 67.7-78.8s\", \"content\": \"演示了使用TensorFlow.js进行模型开发、训练和部署的过程\"}, {\"source\": \"upload-6617113664522196356-cp08-样章示例-TensorFlow Lite_154550.docx 第92段\", \"content\": \"介绍了TensorFlow Lite开发工作流程，适用于Linux环境下的模型部署\"}]','[\"TensorFlow.js的使用\", \"卷积神经网络（CNN）模型训练\", \"手写数字识别数据集（如MNIST）\", \"Linux环境下的模型部署\", \"TensorFlow Lite开发流程\"]',2,'2025-08-23 03:35:59',186),(32,21,'user','你知道彭思奇为什么请假吗？',NULL,NULL,1,'2025-08-24 05:42:58',19),(33,21,'assistant','根据课程资料，软件学院大三学生彭思奇（学号：23371112）因为需要在2025年8月25日至2025年8月28日期间前往苏州大学参加中国软件杯决赛的展示和答辩，因此无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程。彭思奇已经与小组成员沟通了情况，并计划在课程开始前完善网站并完成单体网站的部署，同时在比赛结束后承担更多的小学期工作任务，以平衡小组内的工作分配。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"软件学院大三学生彭思奇（学号：23371112）因于2025年8月25日至2025年8月28日需要前往苏州大学参加中国软件杯决赛展示、答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程，小组成员均已知晓情况。学生本人将通过在课程开始前完善网站、完成单体网站部署，在比赛结束后承担更多小学期工作等方式来完平衡小组内的工作分配。\"}]','[\"请假原因\", \"中国软件杯赛事参与\", \"课程协调安排\", \"小组工作分配调整\"]',2,'2025-08-24 05:43:08',221),(34,21,'user','彭思奇请假的课有多少学分？',NULL,NULL,3,'2025-08-24 05:45:03',19),(35,21,'assistant','根据课程资料，《软件工程基础实践》这门课程中，彭思奇因参加中国软件杯决赛而请假，缺席了2025年8月26日、27日、28日的课程。请假情况说明中并未具体提及请假课程的学分信息。通常，学分是根据整门课程的设置来决定的，而非按具体某几次课计算。如果《软件工程基础实践》课程的总学分为已知，则每节课的学分可按课程安排进行比例计算。建议进一步咨询课程负责人以获取具体学分分配细节。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"彭思奇因需要前往苏州大学参加中国软件杯决赛展示和答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程。\"}]','[\"课程请假与学分的关系\", \"学分分配机制\", \"课程安排与学分比例\"]',4,'2025-08-24 05:45:11',238),(36,21,'user','对啊，你看看培养方案里面这门课程有多少学分',NULL,NULL,5,'2025-08-24 05:45:47',31),(37,21,'assistant','根据课程资料，《软件工程基础实践》是软件学院培养方案中的一门实践性课程，但请假情况说明文件中并未明确提及这门课程的具体学分信息。通常情况下，课程的学分会在培养方案或课程大纲中详细说明。为了获取准确的学分信息，建议查阅软件学院的官方培养方案或者联系课程负责人进行确认。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"软件学院大三学生彭思奇（学号：23371112）因需要前往苏州大学参加中国软件杯决赛的展示和答辩，无法正常参与《软件工程基础实践》课程。\"}, {\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第1段\", \"content\": \"《软件工程基础实践》课程请假情况说明。\"}]','[\"《软件工程基础实践》课程的请假原因与安排\", \"课程学分通常由培养方案或课程大纲规定\", \"学分的分配通常基于整门课程而非具体某几次课\"]',6,'2025-08-24 05:45:56',187),(38,22,'user','软件工程基础这门课多少学分？',NULL,NULL,1,'2025-08-24 05:47:58',20),(39,22,'assistant','根据目前提供的课程资料，并未明确提到《软件工程基础》课程的具体学分信息。建议学生进一步咨询所在学院或查阅官方课程大纲以获取准确的学分信息。','[{\"source\": \"《软件工程基础实践》课程请假情况说明_133236.docx 第1段\", \"content\": \"文档提到了《软件工程基础实践》课程，但未说明该课程的学分信息。\"}]','[\"课程学分查询\", \"软件工程基础实践课程安排\"]',2,'2025-08-24 05:48:01',97),(40,23,'user','软件工程基础这门课多少学分？',NULL,NULL,1,'2025-08-24 05:48:39',20),(41,23,'assistant','根据目前提供的信息，无法确定《软件工程基础》这门课程的具体学分。上述资料仅提及了《软件工程基础实践》课程的相关情况，但并未说明课程的学分信息。建议查阅学校教务系统或相关课程大纲以获取准确的学分信息。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第1段\", \"content\": \"资料提到了《软件工程基础实践》课程，但未提及学分信息。\"}]','[\"课程学分\", \"《软件工程基础实践》课程安排\"]',2,'2025-08-24 05:48:46',136),(42,24,'user','手写数字识别这门课讲的什么？',NULL,NULL,1,'2025-08-24 05:49:46',20),(43,24,'assistant','手写数字识别这门课程主要介绍如何利用TensorFlow.js构建一个卷积神经网络模型，以实现对数千张手写数字图片进行分类。通过训练分类器观察这些图片及其机器标签，使模型能够自动学习识别手写数字的规律。当提供特定尺寸的黑白图片时，模型会预测并分类图片中出现的数字。课程中还可能涉及数据预处理、模型构建、训练与评估的相关知识。','[{\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 0.0-15.6s\", \"content\": \"在这个项目中，我们将构建一个TensorFlow.js模型，使用卷积神经网络识别手写数字。\"}, {\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 15.6-25.1s\", \"content\": \"首先，我们将通过让分类器观察数千个手写数字图片和机器标签来训练分类器。\"}, {\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 76.2-84.4s\", \"content\": \"在我们提供特定尺寸的黑白图片后，该模型将对出现在图片中的数字进行分类。\"}]','[\"TensorFlow.js模型构建\", \"卷积神经网络（CNN）\", \"手写数字识别\", \"训练分类器\", \"图像分类\"]',2,'2025-08-24 05:49:57',211),(44,25,'user','软件工程基础这门课程多少学分',NULL,NULL,1,'2025-08-24 05:51:34',22),(45,25,'assistant','根据目前提供的信息，无法确定《软件工程基础》课程的具体学分。课程学分通常与课程的教学安排和所在学校的具体设置有关，而提供的课程资料片段中并未明确提及学分信息。建议查阅学校官方发布的课程大纲或相关教务通知以获取准确的学分信息。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第1段\", \"content\": \"提到了《软件工程基础实践》课程，但未说明课程的学分\"}, {\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"描述了学生因比赛请假情况，未涉及课程学分信息\"}]','[\"课程学分\", \"软件工程基础\"]',2,'2025-08-24 05:51:40',158),(46,26,'user','软件工程基础这门课程多少学分',NULL,NULL,1,'2025-08-24 05:59:21',22),(47,26,'assistant','根据目前提供的信息，未明确提及《软件工程基础》课程的具体学分信息。通常课程学分会根据学校教学安排有所不同，建议查阅学校教务系统或课程大纲以获取准确信息。',NULL,'[\"课程学分查询途径\", \"软件工程基础课程设置\"]',2,'2025-08-24 05:59:25',106),(48,27,'user','软件工程基础课程多少学分？',NULL,NULL,1,'2025-08-24 06:19:50',19),(49,27,'assistant','根据现有资料，并未明确提到《软件工程基础》课程的具体学分信息。建议学生进一步咨询学院教务或查看课程手册，以获取权威信息。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第1段\", \"content\": \"提到了《软件工程基础实践》课程的请假情况，但未提及课程学分\"}, {\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"说明了学生因比赛请假的具体情况，也未涉及课程学分信息\"}]','[\"课程学分信息查询\", \"课程管理与请假流程\"]',2,'2025-08-24 06:19:55',82),(50,28,'user','hello',NULL,NULL,1,'2025-08-24 06:21:36',1),(51,28,'assistant','你好！如果你有任何问题或需要关于课程内容的帮助，请随时告诉我。我在这里为你提供详细且易于理解的支持。','[{\"source\": \"10.5 singal_182823.mp4 271.8-272.9s\", \"content\": \"connected\"}]','[\"交流与沟通\", \"在线学习支持\"]',2,'2025-08-24 06:21:39',70),(52,29,'user','软件工程基础课程多少学分？',NULL,NULL,1,'2025-08-24 06:22:48',19),(53,29,'assistant','根据现有资料，并未直接提及《软件工程基础》课程的具体学分信息。然而，从请假情况说明中可以推测，该课程涉及实践内容，并可能以小组形式进行学习任务分配和考核。建议进一步查阅课程大纲或咨询课程负责人以获取准确学分信息。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第1段\", \"content\": \"提到了《软件工程基础实践》课程，但未说明学分\"}, {\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"描述了学生因比赛请假并采取补救措施完成课程任务的情况，侧面反映出课程具有实践性与小组协作特点\"}]','[\"《软件工程基础实践》课程内容与组织形式\", \"课程实践性任务安排\", \"小组协作在课程中的重要性\"]',2,'2025-08-24 06:22:56',146),(54,30,'user','软件专业学生的培养方案中大三有哪些课程/',NULL,NULL,1,'2025-08-24 06:23:54',29),(55,30,'assistant','根据现有资料，软件专业大三的培养方案中明确提及了一门课程：《软件工程基础实践》。这是一门注重实践能力培养的课程，学生需要在课程中完成网站的开发和单体网站的部署等相关任务。此外，课程可能涉及小组合作，学生需在团队中承担相应责任，如平衡工作分配、协同完成项目等。其他大三课程由于资料有限，未能提供具体信息。','[{\"source\": \"《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"学生需在《软件工程基础实践》课程中完成网站开发和部署任务，并通过课程前的准备工作和赛后承担更多工作来平衡小组内的分工。\"}, {\"source\": \"《软件工程基础实践》课程请假情况说明_133236.docx 第1段\", \"content\": \"该文档描述了与《软件工程基础实践》课程相关的请假情况说明。\"}]','[\"软件工程基础实践课程的任务要求\", \"小组合作与工作分配机制\", \"网站开发与单体网站部署\"]',2,'2025-08-24 06:24:00',206),(56,31,'user','软件专业学生的培养方案中大三有哪些课程/',NULL,NULL,1,'2025-08-24 06:37:58',29),(57,31,'assistant','根据软件学院软件工程专业2023级的课程清单，大三学生在培养方案中主要涉及的课程分为秋季和春季学期。秋季学期的课程包括科学计算与可视化、形式语言与自动机、计算机图形学、数据挖掘导论、分布式系统导论、嵌入式软件设计、数值计算与算法、计算机辅助设计与制造、智能计算系统、开源软件开发导论、网络存储、数字电路分析设计等。春季学期的课程则包括数据可视化分析技术、计算机网络安全技术、软件项目管理、人工智能安全导论、并行程序设计、云计算技术基础、工业互联网技术基础、工业大数据技术、物联网技术基础、图像处理和计算机视觉、智能软件工程等。这些课程多为限选课，学生需要根据自己的专业方向和兴趣选择满足学分要求的课程。','[{\"source\": \"upload-16780028357380500198-021-软件学院-软件工程-2023级课程清单_142218.pdf 第7页\", \"content\": \"大三秋季和春季学期开设的课程，包括科学计算与可视化、形式语言与自动机、计算机图形学、数据挖掘导论、数据可视化分析技术、计算机网络安全技术、软件项目管理、人工智能安全导论等。\"}, {\"source\": \"upload-16780028357380500198-021-软件学院-软件工程-2023级课程清单_142218.pdf 第6页\", \"content\": \"大三秋季和春季学期开设的课程，包括分布式系统导论、并行程序设计、云计算技术基础、嵌入式软件设计、数值计算与算法、计算机辅助设计与制造、工业互联网技术基础、工业大数据技术、物联网技术基础、智能计算系统、图像处理和计算机视觉、智能软件工程、开源软件开发导论、网络存储、数字电路分析设计等。\"}]','[\"大三秋季学期开设的课程\", \"大三春季学期开设的课程\", \"专业限选课的要求\", \"软件工程专业的课程分布\"]',2,'2025-08-24 06:38:09',406),(58,32,'user','彭思奇为什么请假？',NULL,NULL,1,'2025-08-24 06:39:02',13),(59,32,'assistant','彭思奇请假是因为需要前往苏州大学参加2025年8月25日至28日的中国软件杯决赛展示和答辩。因此，他无法正常参与8月26日、27日和28日的《软件工程基础实践》课程。根据请假情况说明，他已与小组成员沟通，同时通过提前完善网站内容、完成单体网站部署以及在比赛结束后承担更多工作来平衡小组内的任务。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"软件学院大三学生彭思奇（学号：23371112）因于2025年8月25日至2025年8月28日前往苏州大学参加中国软件杯决赛展示、答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程，小组成员均已知晓情况。学生本人将通过在课程开始前完善网站、完成单体网站部署，在比赛结束后承担更多小学期工作等方式来平衡小组内的工作分配。\"}]','[\"学生请假原因说明\", \"课程请假对学习任务的影响与补救措施\", \"团队协作中的沟通与分工\"]',2,'2025-08-24 06:39:09',182),(60,32,'user','彭思奇请假的课程占据了多少学分，课程性质是怎么样的？',NULL,NULL,3,'2025-08-24 06:39:36',37),(61,32,'assistant','根据《软件工程基础实践》课程请假情况说明文件，彭思奇请假的课程是《软件工程基础实践》。该课程属于软件工程专业实践类课程，主要围绕软件开发的实际操作与工程管理进行设计。课程的性质为必修实践课程，旨在通过团队协作完成实际项目开发，提升学生的软件工程能力和实践水平。关于具体学分，2023级软件工程专业课程清单中并未直接列出《软件工程基础实践》课程的具体学分。建议向学校教务部门或课程负责人确认具体学分信息。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"彭思奇因参加中国软件杯决赛展示、答辩无法正常参与2025年8月的《软件工程基础实践》课程\"}, {\"source\": \"upload-16780028357380500198-021-软件学院-软件工程-2023级课程清单_142218.pdf 第3页\", \"content\": \"软件学院2023级部分课程清单显示，课程性质包括必修、选修等，而实践类课程属于专业必修内容\"}]','[\"《软件工程基础实践》课程的性质为专业必修实践课程\", \"课程涉及软件开发实际操作与团队协作\", \"课程学分需通过学校课程清单进一步确认\"]',4,'2025-08-24 06:39:46',272),(62,32,'user','《软件工程基础实践》课程具体学分多少？',NULL,NULL,5,'2025-08-24 06:40:10',25),(63,32,'assistant','根据2023级软件工程专业课程清单，《软件工程基础实践》课程的学分为2学分。该课程属于必修实践课程，总学时为80学时，全部为实践学时，授课学期为二年级夏季学期。','[{\"source\": \"upload-16780028357380500198-021-软件学院-软件工程-2023级课程清单_142218.pdf 第5页\", \"content\": \"《软件工程基础实践》课程信息显示该课程学分为2分，属于必修实践课程，总学时为80学时，全部为实践学时，授课学期为二年级夏季学期。\"}]','[\"《软件工程基础实践》课程学分为2学分\", \"课程性质为必修实践课程\", \"总学时为80学时，全为实践学时\", \"授课学期为二年级夏季学期\"]',6,'2025-08-24 06:40:17',98),(64,33,'user','hello，我们学的预测汽车油耗的课主要讲的啥',NULL,NULL,1,'2025-08-24 06:42:08',26),(65,33,'assistant','预测汽车油耗的课程主要围绕如何利用数据分析和建模技术来预测汽车在不同条件下的燃油消耗情况。课程可能会介绍影响油耗的主要因素，例如汽车发动机性能、车重、驾驶习惯、路况等。同时，还会讲解一些常用的预测方法，例如线性回归、时间序列分析以及机器学习算法在油耗预测中的应用。此外，课程可能还涵盖实际案例研究，以帮助学生更好地理解和应用预测模型。',NULL,'[\"影响油耗的因素\", \"预测建模方法\", \"线性回归\", \"时间序列分析\", \"机器学习在油耗预测中的应用\"]',2,'2025-08-24 06:42:13',232),(66,34,'user','hello，我们学的预测汽车油耗的课主要讲的啥',NULL,NULL,1,'2025-08-24 06:44:40',26),(67,34,'assistant','在《Linux开发实践》课程中，关于预测汽车油耗的内容主要围绕如何利用机器学习技术，特别是使用TensorFlow.js来构建预测模型。课程讲解了通过分析汽车相关数据（例如发动机大小、车辆重量等）来预测汽车的油耗效率（MPG，Miles Per Gallon）。此外，还介绍了具体的预测流程，包括数据处理、模型训练和使用模型进行预测的步骤。','[{\"source\": \"upload-5827127634749586247-01 认识TensorFlow.js-1_170810.mp4 62.9-67.7s\", \"content\": \"在这个项目中我们将通过预测汽车油耗效率\"}, {\"source\": \"01 认识TensorFlow.js-1_162707.mp4 62.9-67.7s\", \"content\": \"在这个项目中我们将通过预测汽车油耗效率\"}, {\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 88.1-93.6s\", \"content\": \"使用模型预测汽车油耗具体流程一下\"}, {\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 57.0-62.4s\", \"content\": \"用来预测汽车的油耗效率MPG\"}]','[\"Linux开发环境搭建与实践\", \"TensorFlow.js的基本使用\", \"预测模型的构建与训练\", \"数据预处理与特征提取\", \"使用机器学习预测汽车油耗效率（MPG）\"]',2,'2025-08-24 06:44:49',179),(68,35,'user','《软件工程基础实践》课程具体学分多少？',NULL,NULL,1,'2025-08-24 06:50:36',25),(69,35,'assistant','根据提供的课程资料，《软件工程基础实践》课程的学分为2学分。','[{\"source\": \"upload-16780028357380500198-021-软件学院-软件工程-2023级课程清单_142218.pdf 第5页\", \"content\": \"在课程清单中明确提到《软件工程基础实践》的学分为2学分，总学时为80学时，其中全部为实践学时。\"}]','[\"《软件工程基础实践》的课程学分为2学分\", \"该课程的总学时为80学时\", \"课程性质为必修，考核方式为考查\"]',2,'2025-08-24 06:50:44',38),(70,36,'user','《软件工程基础实践》课程具体学分多少？',NULL,NULL,1,'2025-08-24 06:51:03',25),(71,36,'assistant','根据目前提供的资料，并未明确提到《软件工程基础实践》课程的具体学分信息。建议学生查阅课程目录、教学大纲或联系授课教师以获取更准确的信息。',NULL,'[\"《软件工程基础实践》课程内容\", \"课程请假情况说明\", \"QT开发环境介绍\"]',2,'2025-08-24 06:51:06',94),(72,37,'user','手写数字识别利用的什么原理？',NULL,NULL,1,'2025-08-24 06:51:37',20),(73,37,'assistant','手写数字识别主要基于机器学习中的深度学习技术，尤其是卷积神经网络（CNN）。在训练过程中，分类器会通过观察大量手写数字的图片及其对应的标签来学习不同数字的特征。这些图片通常是特定尺寸的黑白图像，模型通过分析图片中像素的分布规律，提取特征并进行分类。最终，经过训练的模型能够对输入的手写数字进行准确的识别和分类。','[{\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 0.0-15.6s\", \"content\": \"在这个项目中，我们将部署一个TensorFlow.js模型，使用卷积神经网络识别手写数字。\"}, {\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 15.6-25.1s\", \"content\": \"首先，我们将通过让分类器观察数千个手写数字图片和对应的标签来训练分类器。\"}, {\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 76.2-84.4s\", \"content\": \"在我们提供特定尺寸的黑白图片后，该模型将对出现在图片中的数字进行分类。\"}]','[\"手写数字识别\", \"卷积神经网络（CNN）\", \"深度学习模型训练\", \"图像分类\", \"特征提取\"]',2,'2025-08-24 06:51:48',212),(74,38,'user','hello',NULL,NULL,1,'2025-08-24 13:33:33',1),(75,38,'assistant','你好！如果你有任何问题或需要帮助，请随时告诉我。我可以为你解答关于课程、学习内容或其他相关事项的问题。如果有具体的疑问，请尽量详细描述，我会尽力为你提供详细且易于理解的解答。',NULL,'[\"基本问候与沟通\"]',2,'2025-08-24 13:33:47',119),(76,39,'user','彭思奇为什么请假？',NULL,NULL,1,'2025-08-24 23:35:59',13),(77,40,'user','彭思奇为什么请假？',NULL,NULL,1,'2025-08-24 23:38:50',13),(78,41,'user','彭思奇为什么请假？',NULL,NULL,1,'2025-08-24 23:41:14',13),(79,41,'assistant','根据现有课程资料，并未提及彭思奇请假的具体原因。如果这是课堂上提到的信息，请参考课堂记录或直接向授课老师询问详细情况。',NULL,NULL,2,'2025-08-24 23:41:30',83),(80,42,'user','彭思奇为什么请假？',NULL,NULL,1,'2025-08-24 23:42:15',13),(81,42,'assistant','彭思奇同学请假的原因是需要前往苏州大学参加2025年8月25日至8月28日举行的中国软件杯决赛展示和答辩活动。因此，他无法正常参与2025年8月26日、27日和28日的《软件工程基础实践》课程。请假人已向小组成员说明情况，并计划通过在课程开始前完善网站、完成单体网站部署，以及在比赛结束后承担更多小学期工作的方式，来平衡小组内的工作分配。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"软件学院大三学生彭思奇（学号：23371112）因于 2025年8月25日至2025年8月28日需要前往苏州大学参加中国软件杯决赛展示、答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程，小组成员均已知晓情况。学生本人将通过在课程开始前完善网站、完成单体网站部署，在比赛结束后承担更多小学期工作等方式来完平衡小组内的工作分配。\"}, {\"source\": \"upload-8621092309914837266-《软件工程基础实践》课程请假情况说明_074152.docx 第2段\", \"content\": \"软件学院大三学生彭思奇（学号：23371112）因于 2025年8月25日至2025年8月28日需要前往苏州大学参加中国软件杯决赛展示、答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程，小组成员均已知晓情况。学生本人将通过在课程开始前完善网站、完成单体网站部署，在比赛结束后承担更多小学期工作等方式来完平衡小组内的工作分配。\"}]','[\"学生请假的具体原因\", \"请假期间的活动安排\", \"课程缺席对小组协作的影响处理方案\", \"竞赛与课程时间冲突的协调方式\"]',2,'2025-08-24 23:42:25',206),(82,43,'user','这门课程讲的什么？彭思奇请假了这门课程吗？',NULL,NULL,1,'2025-08-24 23:43:17',29),(83,43,'assistant','《Linux开发实践》是一门专注于Linux系统开发技能的课程，旨在帮助学生掌握Linux环境下的软件开发、部署和相关实践操作。至于彭思奇同学，根据相关课程请假说明文档记录，彭思奇（学号：23371112）因需要前往苏州大学参加2025年8月25日至28日的中国软件杯决赛展示和答辩，无法正常参与《软件工程基础实践》课程的部分教学活动，并非《Linux开发实践》课程的请假情况。文档提到他将通过其他方式完成相关任务以平衡小组工作分配。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"彭思奇因于2025年8月25日至8月28日前往苏州大学参加中国软件杯决赛展示、答辩，无法参与8月26日、27日、28日的《软件工程基础实践》课程，已说明其将通过其他方式完成工作分配。\"}, {\"source\": \"upload-8621092309914837266-《软件工程基础实践》课程请假情况说明_074152.docx 第2段\", \"content\": \"彭思奇因参加中国软件杯决赛展示和答辩，无法参与《软件工程基础实践》课程，并计划通过完善网站和承担更多小学期任务来弥补小组工作分配。\"}]','[\"Linux开发实践课程的目标和内容\", \"学生请假情况与课程安排的调整\", \"彭思奇因比赛请假的具体时间与课程影响\"]',2,'2025-08-24 23:43:26',244),(84,43,'user','手写数字识别这里重点是什么？',NULL,NULL,3,'2025-08-24 23:43:55',20),(85,43,'assistant','在《Linux开发实践》课程的手写数字识别项目中，重点是使用TensorFlow.js构建一个卷积神经网络模型，以实现对手写数字的识别。这一部分的学习不仅涉及模型的搭建，还包括理解如何在Linux环境下进行相关开发任务，例如代码的部署和模型的应用。通过该项目，学生可以掌握Linux开发中与机器学习结合的实际应用技能。','[{\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 0.0-15.6s\", \"content\": \"项目中将使用TensorFlow.js模型，通过卷积神经网络识别手写数字。\"}]','[\"TensorFlow.js模型的使用\", \"卷积神经网络的基础应用\", \"Linux环境下的机器学习开发\", \"手写数字识别项目的部署与实践\"]',4,'2025-08-24 23:44:02',182),(86,44,'user','彭思奇在软件杯的队伍名字叫什么？',NULL,NULL,1,'2025-08-25 08:06:41',23),(87,44,'assistant','根据提供的资料，并未提及彭思奇在软件杯中的队伍名字。资料中仅提到在2018年3月，Dip Learning.gts团队与ThanThanFlow团队合并，但未涉及彭思奇及其参与的具体队伍信息。','[{\"source\": \"upload-5827127634749586247-01 认识TensorFlow.js-1_170810.mp4 234.4-240.4s\", \"content\": \"在2018年3月，Dip Learning.gts团队与ThanThanFlow团队合并\"}, {\"source\": \"01 认识TensorFlow.js-1_162707.mp4 234.4-240.4s\", \"content\": \"在2018年3月，Dip Learning.gts团队与ThanThanFlow团队合并\"}]','[\"团队合并历史\", \"软件杯相关队伍信息\"]',2,'2025-08-25 08:06:48',89),(88,45,'user','彭思奇在软件杯的队伍名字叫什么？',NULL,NULL,1,'2025-08-25 08:09:08',23),(89,45,'assistant','根据现有的资料片段，并未提及彭思奇在软件杯中的具体队伍名字。资料中仅提到在2018年3月，Dip Learning.gts团队与ThanThanFlow团队进行了合并，但没有关于彭思奇个人或其所属队伍的详细信息。','[{\"source\": \"upload-5827127634749586247-01 认识TensorFlow.js-1_170810.mp4 234.4-240.4s\", \"content\": \"在2018年3月，Dip Learning.gts团队与ThanThanFlow团队合并\"}, {\"source\": \"01 认识TensorFlow.js-1_162707.mp4 234.4-240.4s\", \"content\": \"在2018年3月，Dip Learning.gts团队与ThanThanFlow团队合并\"}]','[\"团队合并信息\", \"软件杯相关背景\"]',2,'2025-08-25 08:09:19',104),(90,45,'user','三核驱动AI队伍里面都有谁?',NULL,NULL,3,'2025-08-25 08:09:51',17),(91,45,'assistant','根据现有资料，并未提及名为\'三核驱动AI\'的具体队伍信息。资料中仅提到两个团队：Dip Learning.gts和ThanThanFlow，它们曾在2018年3月合并。但此信息与\'三核驱动AI\'是否有关联，资料中并未说明。','[{\"source\": \"upload-5827127634749586247-01 认识TensorFlow.js-1_170810.mp4 234.4-240.4s\", \"content\": \"在2018年3月，Dip Learning.gts团队与ThanThanFlow团队进行了合并\"}, {\"source\": \"01 认识TensorFlow.js-1_162707.mp4 234.4-240.4s\", \"content\": \"在2018年3月，Dip Learning.gts团队与ThanThanFlow团队进行了合并\"}]','[\"团队合并历史\", \"软件杯相关队伍信息\"]',4,'2025-08-25 08:09:59',97),(92,46,'user','彭思奇软件杯的队伍叫什么？',NULL,NULL,1,'2025-08-25 08:11:38',19),(93,46,'assistant','根据提供的课程资料，并未提及任何关于‘彭思奇软件杯’及其相关队伍名称的信息。该问题可能与课程内容无关。',NULL,NULL,2,'2025-08-25 08:11:42',70),(94,47,'user','彭思奇软件杯的队伍叫什么名字？',NULL,NULL,1,'2025-08-25 08:18:31',22),(95,47,'assistant','根据已有资料，彭思奇软件杯的队伍名称为“三核驱动AI”。这一名称来源于相关文件中对参赛队伍的具体命名。','[{\"source\": \"upload-10139940166133966472-测试文件_161807.docx 第1段\", \"content\": \"彭思奇软件杯的队伍名称叫做三核驱动AI\"}]','[\"彭思奇软件杯的队伍名称\"]',2,'2025-08-25 08:18:41',67),(96,48,'user','彭思奇为什么请假？',NULL,NULL,1,'2025-08-25 08:31:00',13),(97,48,'assistant','彭思奇同学请假的原因是他需要在2025年8月25日至2025年8月28日期间前往苏州大学参加‘中国软件杯’决赛的展示和答辩活动，因此无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程。为弥补课程缺席带来的影响，彭思奇计划在课程开始前完成网站的完善和单体网站的部署，并在比赛结束后承担更多小学期的工作，以平衡小组内的工作分配。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"软件学院大三学生 彭思奇（学号：23371112）因于 2025年8月25日至2025年8月28日需要前往苏州大学参加中国软件杯决赛展示、答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程，小组成员均已知晓情况。学生本人将通过在课程开始前完善网站、完成单体网站部署，在比赛结束后承担更多小学期工作等方式来完平衡小组内的工作分配。\"}, {\"source\": \"upload-8621092309914837266-《软件工程基础实践》课程请假情况说明_074152.docx 第2段\", \"content\": \"软件学院大三学生 彭思奇（学号：23371112）因于 2025年8月25日至2025年8月28日需要前往苏州大学参加中国软件杯决赛展示、答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程，小组成员均已知晓情况。学生本人将通过在课程开始前完善网站、完成单体网站部署，在比赛结束后承担更多小学期工作等方式来完平衡小组内的工作分配。\"}]','[\"请假原因说明\", \"课程缺席的替代补偿措施\", \"团队协作与责任分配\", \"学术与竞赛的协调管理\"]',2,'2025-08-25 08:31:14',206),(98,49,'user','彭思奇软件杯队伍名字是什么？',NULL,NULL,1,'2025-08-25 12:38:16',20),(99,49,'assistant','根据提供的资料，并未提及彭思奇所在队伍的具体名称。资料仅说明彭思奇是软件学院大三学生，参加了中国软件杯决赛展示和答辩，并未涉及比赛队伍名称的相关信息。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"软件学院大三学生 彭思奇（学号：23371112）因于 2025年8月25日至2025年8月28日需要前往苏州大学参加中国软件杯决赛展示、答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程，小组成员均已知晓情况。\"}, {\"source\": \"upload-8621092309914837266-《软件工程基础实践》课程请假情况说明_074152.docx 第2段\", \"content\": \"软件学院大三学生 彭思奇（学号：23371112）因于 2025年8月25日至2025年8月28日需要前往苏州大学参加中国软件杯决赛展示、答辩，无法正常参与2025年8月26日、27日、28日的《软件工程基础实践》课程，小组成员均已知晓情况。\"}]','[\"彭思奇的身份信息（软件学院大三学生，学号：23371112）\", \"彭思奇前往苏州大学参加中国软件杯决赛展示、答辩\", \"彭思奇因比赛请假，影响了《软件工程基础实践》课程的出勤\"]',2,'2025-08-25 12:38:27',106),(100,50,'user','彭思奇软件杯队伍叫什么名字？',NULL,NULL,1,'2025-08-25 12:39:31',20),(101,50,'assistant','根据课程资料，彭思奇参加的彭思奇软件杯队伍名称是“三核驱动AI”。这是在相关文档中明确提到的信息。','[{\"source\": \"upload-2934725788535794765-测试文件_203913.docx 第1段\", \"content\": \"彭思奇软件杯的队伍名称叫做三核驱动AI\"}]','[\"彭思奇软件杯队伍名称\", \"三核驱动AI\"]',2,'2025-08-25 12:39:35',64),(102,51,'user','预测汽车油耗怎么预测',NULL,NULL,1,'2025-08-25 12:42:30',16),(103,51,'assistant','在Linux开发实践中预测汽车油耗，通常涉及使用机器学习模型来分析和预测汽车的油耗效率，例如以MPG（每加仑行驶英里数）作为衡量指标。具体流程包括以下几个步骤：\n\n1. **数据收集与预处理**：需要收集与油耗效率相关的数据，包括车辆的气缸数、加速度、马力、排量、重量等参数。这些数据可能需要进行清洗和格式化，以便后续建模使用。\n2. **模型构建**：可以使用TensorFlow.js等工具构建机器学习模型。模型的训练基于已有的数据集，以油耗效率为目标变量进行训练。\n3. **模型训练与优化**：通过训练数据集对模型进行训练，并不断调整模型参数以优化预测精度。\n4. **预测与验证**：将测试数据输入训练好的模型中，进行预测并验证模型的准确性。若模型表现良好，则可用于新的未知数据的油耗预测。\n\n整个流程结合了Linux开发环境的数据处理能力以及机器学习框架的建模功能，实现对汽车油耗效率的精准预测。','[{\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 88.1-93.6s\", \"content\": \"描述了使用模型预测汽车油耗的具体流程\"}, {\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 57.0-62.4s\", \"content\": \"指出使用数据建模来预测汽车的油耗效率MPG\"}, {\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 27.5-36.6s\", \"content\": \"列举了影响汽车MPG的数据建模因素，包括气缸数、加速度、马力、排量、重量等\"}, {\"source\": \"upload-5827127634749586247-01 认识TensorFlow.js-1_170810.mp4 62.9-67.7s\", \"content\": \"提到通过TensorFlow.js项目实现预测汽车油耗效率的方法\"}]','[\"使用机器学习模型预测油耗效率\", \"基于MPG作为油耗效率衡量指标\", \"数据收集与预处理\", \"利用TensorFlow.js构建和训练模型\", \"影响油耗效率的因素（气缸数、马力、重量等）\"]',2,'2025-08-25 12:42:45',484),(104,52,'user','彭思奇为什么请假？',NULL,NULL,1,'2025-08-25 12:50:20',13),(105,52,'assistant','彭思奇请假是因为需要参加2025年8月25日至8月28日在苏州大学举办的中国软件杯决赛展示和答辩活动。因此，他无法正常参与《软件工程基础实践》课程的相关安排。同时，彭思奇已经与小组成员沟通，并计划通过课程开始前完成网站部署等工作以及比赛后承担更多任务的方式来平衡小组内的工作分配。','[{\"source\": \"upload-2082284774473666327-《软件工程基础实践》课程请假情况说明_133236.docx 第2段\", \"content\": \"彭思奇因参加中国软件杯决赛展示、答辩而请假，时间覆盖2025年8月26日、27日、28日\"}, {\"source\": \"upload-8621092309914837266-《软件工程基础实践》课程请假情况说明_074152.docx 第2段\", \"content\": \"彭思奇因2025年8月25日至28日前往苏州大学参加中国软件杯决赛而无法参与课程\"}]','[\"学生请假原因\", \"中国软件杯决赛活动\", \"课程安排与请假协调\", \"小组工作分配平衡\"]',2,'2025-08-25 12:50:30',184),(106,52,'user','彭思奇的软件杯队伍名称是什么？',NULL,NULL,3,'2025-08-25 12:50:49',22),(107,52,'assistant','彭思奇参加中国软件杯时所属的队伍名称是‘三核驱动AI’。这一信息在多个文件中得到了确认。','[{\"source\": \"测试文件_160836.docx 第1段\", \"content\": \"彭思奇软件杯的队伍名称叫做三核驱动AI\"}, {\"source\": \"upload-2934725788535794765-测试文件_203913.docx 第1段\", \"content\": \"彭思奇软件杯的队伍名称叫做三核驱动AI\"}]','[\"中国软件杯比赛信息\", \"团队名称\", \"彭思奇的课程安排\"]',4,'2025-08-25 12:50:53',58),(108,53,'user','彭思奇今天早上吃了什么？',NULL,NULL,1,'2025-08-25 12:52:58',17),(109,53,'assistant','根据现有的资料，彭思奇在2025年8月26号早上吃了桔子酒店的自助餐。该信息来源于相关文件记录。','[{\"source\": \"upload-8589483377839837048-测试文件_205244.docx 第1段\", \"content\": \"彭思奇在2025年8月26号早上吃了桔子酒店的自助餐\"}]','[\"彭思奇的早餐信息\", \"具体日期的饮食记录\"]',2,'2025-08-25 12:53:03',58),(110,54,'user','彭思奇早上吃了什么？',NULL,NULL,1,'2025-08-26 00:40:14',14),(111,54,'assistant','根据现有资料，并未提及彭思奇早上具体吃了什么，仅有一处提到‘再吃点东西’，但无法确认是否与其早餐有关。','[{\"source\": \"10.5 singal_182823.mp4 544.0-546.0s\", \"content\": \"提及‘再吃点东西’\"}]','[\"未明确记录彭思奇早餐内容\"]',2,'2025-08-26 00:40:19',68),(112,55,'user','彭思奇早上吃了什么？',NULL,NULL,1,'2025-08-26 00:41:28',14),(113,55,'assistant','根据现有资料，彭思奇在2025年8月26号早上吃了桔子酒店的自助餐。','[{\"source\": \"upload-8589483377839837048-测试文件_205244.docx 第1段\", \"content\": \"彭思奇在2025年8月26号早上吃了桔子酒店的自助餐\"}, {\"source\": \"upload-4976869717996366888-测试文件_084118.docx 第1段\", \"content\": \"彭思奇在2025年8月26号早上吃了桔子酒店的自助餐\"}]','[\"彭思奇的早餐信息\", \"具体日期与地点的饮食记录\"]',2,'2025-08-26 00:41:33',38),(114,56,'user','能为我介绍一下中国软件杯吗',NULL,NULL,1,'2025-08-26 05:32:06',20),(115,56,'assistant','中国软件杯是一项面向大学生的软件设计竞赛，旨在激发学生在软件开发领域的创新能力。第14届赛事吸引了来自全国各地的210支队伍晋级全国总决赛，这些队伍于8月25日齐聚比赛现场进行签到，为他们的努力迎来了收获的时刻。比赛不仅考验选手的技术能力，也鼓励团队协作和实践能力的提升。参赛队伍还会以独特且具有创意的队名展示自己的特色，例如“三核驱动AI”等。通过这样的赛事，学生们有机会在实践中提升自己的技能，并与同行交流学习。','[{\"source\": \"upload-8733416478257090075-测试图片——软件杯推送_133110.jpg 第3段\", \"content\": \"中 国 软 件 杯 赛 事 快 报 | 210 支 赛 队 “石马”上决高下\"}, {\"source\": \"upload-2934725788535794765-测试文件_203913.docx 第1段\", \"content\": \"彭思奇软件杯的队伍名称叫做三核驱动AI\"}, {\"source\": \"upload-8733416478257090075-测试图片——软件杯推送_133110.jpg 第5段\", \"content\": \"8 月 25 日 , 共 有 210 支 晋 级 决 赛 的 队 伍 来 到 第 14 届 中 国 软 件 杯 “ 大 学 生 软 件 设 计 大 赛 的 现 场 进 行 签 到 。\"}]','[\"中国软件杯是一项大学生软件设计竞赛\", \"第14届中国软件杯吸引了210支队伍进入全国总决赛\", \"参赛队伍拥有独特队名展示团队特色\", \"赛事考验技术能力与团队协作\"]',2,'2025-08-26 05:32:16',277),(116,57,'user','能为我介绍一下中国软件杯吗',NULL,NULL,1,'2025-08-26 05:34:10',20),(117,57,'assistant','中国软件杯（China Software Cup）是一项面向高校学生的软件设计与开发竞赛，旨在提升大学生在软件工程、创新设计和技术实践方面的能力。比赛通常由各大高校和知名科技企业联合举办，参赛者需根据赛事主题完成创意设计、项目开发与答辩展示。团队可以自由组队，围绕人工智能、数据库应用、用户界面设计等软件相关领域展开探索。例如，有参赛队伍名为“三核驱动AI”，展示了与AI相关的项目。比赛不仅注重技术实现，还重视创新思维和实际应用价值。','[{\"source\": \"测试文件_160836.docx 第1段\", \"content\": \"彭思奇软件杯的队伍名称叫做三核驱动AI\"}]','[\"中国软件杯是一项面向高校学生的软件设计竞赛\", \"比赛目的是提升大学生的创新设计和技术实践能力\", \"参赛队伍通常由高校学生自由组队构成\", \"比赛内容涵盖人工智能、数据库应用、用户界面设计等领域\", \"比赛流程包括创意设计、项目开发与答辩展示\"]',2,'2025-08-26 05:34:18',269),(118,58,'user','你能帮我介绍一下中国软件杯吗？',NULL,NULL,1,'2025-08-26 06:05:57',22),(119,58,'assistant','“中国软件杯”是指中国软件杯大学生软件设计大赛，是一项面向高校学生的软件设计竞赛。第14届中国软件杯的比赛现场迎来了210支晋级决赛的队伍进行全国总决赛，这些队伍在比赛过程中经历了多轮角逐，最终有机会展示自己的实力。比赛旨在为大学生提供一个展示创新能力的平台，同时也鼓励他们将所学知识应用于实际项目中。例如，有一支队伍的名字是“三核驱动AI”，他们也是参赛队伍之一。','[{\"source\": \"upload-8733416478257090075-测试图片——软件杯推送_133110.jpg 第3段\", \"content\": \"中国软件杯赛事快报提到共有210支队伍晋级决赛。\"}, {\"source\": \"upload-2934725788535794765-测试文件_203913.docx 第1段\", \"content\": \"彭思奇所在的软件杯队伍名称是“三核驱动AI”。\"}, {\"source\": \"upload-8733416478257090075-测试图片——软件杯推送_133110.jpg 第5段\", \"content\": \"8月25日，210支晋级决赛的队伍参加了全国总决赛的现场签到。\"}]','[\"中国软件杯是面向大学生的软件设计大赛\", \"比赛为高校学生提供了展示创新能力的平台\", \"第14届比赛共有210支队伍晋级全国总决赛\", \"参赛队伍如“三核驱动AI”展示了软件设计的实践成果\"]',2,'2025-08-26 06:06:12',244),(120,59,'user','手写数字识别和中国软件杯有什么关系？',NULL,NULL,1,'2025-08-26 06:07:12',26),(121,59,'assistant','手写数字识别和‘中国软件杯’的关系主要体现在技术应用和赛事主题上。‘中国软件杯’是一个聚焦软件开发和技术应用的赛事平台，参赛队伍常常会结合人工智能、图像识别等前沿技术进行项目开发。在提供的资料中，提到了彭思奇的队伍名称为‘三核驱动AI’，表明该队伍可能与人工智能方向相关。同时，手写数字识别是一项典型的图像识别任务，可以作为比赛项目中的技术应用之一，展示参赛者在模式识别与机器学习方面的实力。','[{\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 32.7-36.5s\", \"content\": \"我们前面已经了解了手写数字数据级\"}, {\"source\": \"彭思奇软件杯的队伍名称叫做三核驱动AI\", \"content\": \"彭思奇参与‘中国软件杯’的队伍名称为‘三核驱动AI’\"}]','[\"手写数字识别是一种典型的图像识别技术\", \"‘中国软件杯’是一项聚焦软件开发与技术创新的赛事\", \"人工智能和模式识别是比赛中的常见技术应用方向\"]',2,'2025-08-26 06:07:20',266),(122,60,'user','预测汽车油耗的教学视频里面老师主要讲了什么？',NULL,NULL,1,'2025-08-26 06:25:10',32),(123,60,'assistant','预测汽车油耗的教学视频主要围绕如何使用模型来预测汽车的油耗效率（MPG，Miles Per Gallon）。视频中提到，每个人都希望购买一辆能够行驶更远且油耗更少的汽车，因此通过模型预测油耗效率，可以帮助消费者更好地了解汽车性能。此外，视频还简要介绍了使用模型预测汽车油耗效率的具体流程，从而为实际应用提供指导。','[{\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 88.1-93.6s\", \"content\": \"介绍了使用模型预测汽车油耗的具体流程\"}, {\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 57.0-62.4s\", \"content\": \"提到预测汽车的油耗效率MPG\"}, {\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 48.0-53.5s\", \"content\": \"提到每个人都希望购买行驶更远、耗油更少的车\"}]','[\"预测汽车油耗效率（MPG）\", \"使用模型预测油耗的具体流程\", \"汽车油耗效率的实际意义\"]',2,'2025-08-26 06:25:19',192),(124,60,'user','能为我讲解一些技术细节吗',NULL,NULL,3,'2025-08-26 06:25:36',19),(125,60,'assistant','预测汽车油耗的技术实现中，通常会涉及几个关键步骤和工具。首先，需要使用到一些工具和框架来构建模型，例如TensorFlow Lite，它可以帮助我们训练模型并将其部署到实际应用中。在模型构建过程中，前几层网络通常会学习简单而通用的功能，这些功能对于后续的复杂识别或预测任务至关重要。此外，在开发过程中，也可以通过编写代码来实现复杂的逻辑和功能，比如使用预处理器来优化数据的输入，或者利用回掉机制处理异步操作。这些方法和技术共同构成了预测汽车油耗的技术基础。','[{\"source\": \"04 TensorFlow Lite实现花卉识别-1_222124.mp4 418.0-422.0s\", \"content\": \"前几层学习非常简单和通用的功能\"}, {\"source\": \"10.4 hello_182349.mp4 68.1-71.1s\", \"content\": \"如何使用预处理器等等\"}, {\"source\": \"10.5 singal_182823.mp4 54.1-57.2s\", \"content\": \"一些工具它使用了回掉机制\"}, {\"source\": \"10.4 hello_182349.mp4 855.9-860.9s\", \"content\": \"我们就通过存代码也可以编达一个非常复杂的一个\"}]','[\"模型的前几层学习简单和通用的功能\", \"使用预处理器优化数据输入\", \"利用回掉机制处理异步操作\", \"通过编码实现复杂逻辑\"]',4,'2025-08-26 06:25:47',299),(126,60,'user','能为我讲解一些预测汽车油耗的细节吗',NULL,NULL,5,'2025-08-26 06:26:22',26),(127,60,'assistant','预测汽车油耗效率（MPG）的流程通常包括以下几个步骤：首先，收集影响汽车油耗的相关数据，例如汽车的气缸数、加速度、马力、排量和重量等。这些数据是建模和预测的基础。其次，选择合适的工具和框架来构建模型，例如使用TensorFlow.js或TensorFlow Lite，这些框架能够帮助开发者训练模型并部署到实际应用中。接下来，利用收集的数据训练模型，使其能够学习并建立输入参数与油耗效率之间的关系。最后，通过训练好的模型对新的汽车数据进行预测，从而估算其油耗效率。这一流程不仅适用于学术研究，还能为消费者提供更直观的汽车性能评估。','[{\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 88.1-93.6s\", \"content\": \"介绍了使用模型预测汽车油耗的具体流程\"}, {\"source\": \"upload-17896897878885126814-04 预测汽车油耗效率-1_173153.mp4 27.5-36.6s\", \"content\": \"提到影响汽车MPG的因素包括气缸数、加速度、马力、排量、重量等\"}, {\"source\": \"upload-5827127634749586247-01 认识TensorFlow.js-1_170810.mp4 62.9-67.7s\", \"content\": \"提到将通过预测汽车油耗效率来展示TensorFlow.js的应用\"}]','[\"预测汽车油耗效率（MPG）的流程\", \"影响汽车油耗效率的关键因素\", \"使用TensorFlow.js等工具构建预测模型\", \"训练模型并应用于新数据预测\"]',6,'2025-08-26 06:26:31',320),(128,61,'user','什么是手写数字识别？手写数字识别依赖的技术原理是什么？',NULL,NULL,1,'2025-08-26 06:27:16',38),(129,61,'assistant','手写数字识别是一种图像识别技术，其目标是让计算机能够准确识别手写形式的数字（0-9）。这项技术广泛应用于各种场景，如银行支票识别、表格数据录入等。手写数字识别依赖的核心技术通常是基于深度学习的卷积神经网络（CNN）。具体来说，我们会用数千张手写数字图像以及对应的标签来训练分类模型。通过训练，模型能够学习到不同数字的特征，从而在面对新的手写数字时做出准确预测。','[{\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 0.0-15.6s\", \"content\": \"在这个项目中，我们将训练一个Tensorflow.js模型，使用卷积神经网络识别手写数字。\"}, {\"source\": \"upload-17308567376682724494-06 手写数字识别-1_174128.mp4 15.6-25.1s\", \"content\": \"首先，我们将通过让分类器观察数千个手写数字图片以及机器标签来训练分类器。\"}]','[\"手写数字识别的定义\", \"卷积神经网络（CNN）\", \"深度学习在图像识别中的应用\", \"模型训练与分类\"]',2,'2025-08-26 06:27:25',239);
/*!40000 ALTER TABLE `chat_message` ENABLE KEYS */;
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
  `session_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'New Chat',
  `course_id` bigint DEFAULT NULL,
  `course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `user_id` (`user_id`),
  KEY `course_id` (`course_id`),
  KEY `idx_user_active` (`user_id`,`is_active`),
  KEY `idx_updated_at` (`updated_at`),
  CONSTRAINT `chat_session_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chat_session_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `course` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `chat_session`
--

LOCK TABLES `chat_session` WRITE;
/*!40000 ALTER TABLE `chat_session` DISABLE KEYS */;
INSERT INTO `chat_session` VALUES (1,4,'New Chat',NULL,'','2025-08-22 10:48:43','2025-08-22 10:48:43',1),(2,4,'New Chat',NULL,'','2025-08-22 10:48:45','2025-08-22 10:48:45',1),(3,5,'New Chat',NULL,'','2025-08-22 11:02:59','2025-08-22 11:02:59',1),(4,5,'New Chat',NULL,'','2025-08-22 11:03:17','2025-08-22 11:03:17',1),(5,5,'New Chat',NULL,'','2025-08-22 11:03:18','2025-08-22 11:03:18',1),(6,5,'New Chat',NULL,'','2025-08-22 11:10:42','2025-08-22 11:10:42',1),(7,5,'New Chat',NULL,'','2025-08-22 11:20:23','2025-08-22 11:20:23',1),(8,5,'New Chat',NULL,'','2025-08-22 11:20:26','2025-08-22 11:20:26',0),(9,5,'New Chat',NULL,'','2025-08-22 11:23:40','2025-08-22 11:23:40',1),(10,5,'New Chat',NULL,'','2025-08-22 11:24:42','2025-08-22 11:24:42',0),(11,5,'New Chat',NULL,'','2025-08-22 11:24:46','2025-08-22 11:24:46',0),(12,5,'hello',NULL,'','2025-08-22 11:24:48','2025-08-22 12:16:01',1),(13,5,'下面请称呼我为小彭，你好！',NULL,'','2025-08-22 12:23:57','2025-08-22 12:24:50',1),(14,5,'我想我们这门课程会学些什么',NULL,'','2025-08-22 12:38:28','2025-08-22 12:39:46',1),(15,4,'你觉得这门课在大学应该放在哪个学期来开？学科的性质（学分、学...',2,'Linux开发实践','2025-08-22 13:03:13','2025-08-22 13:03:32',1),(16,4,'你觉得这门课在大学应该放在哪个学期来开？学科的性质（学分、学...',2,'Linux开发实践','2025-08-22 13:03:46','2025-08-22 13:05:32',1),(17,4,'你觉得这门课在大学应该放在哪个学期来开？学科的性质（学分、学...',2,'Linux开发实践','2025-08-22 13:05:57','2025-08-22 13:06:42',0),(18,4,'你觉得Linux开发实践这门课在大学应该放在哪个学期来开？学...',2,'Linux开发实践','2025-08-22 13:06:58','2025-08-22 13:07:50',0),(19,4,'根据你了解的这门课程的信息，结合培养方案，制定出一版课程大纲',2,'Linux开发实践','2025-08-22 13:09:00','2025-08-22 13:12:06',1),(20,4,'请告诉我如何进行手写数字识别？',2,'Linux开发实践','2025-08-23 03:35:27','2025-08-23 03:35:58',1),(21,4,'你知道彭思奇为什么请假吗？',NULL,'','2025-08-24 05:42:58','2025-08-24 05:45:56',0),(22,8,'软件工程基础这门课多少学分？',NULL,'','2025-08-24 05:47:58','2025-08-24 05:48:01',1),(23,8,'软件工程基础这门课多少学分？',NULL,'','2025-08-24 05:48:31','2025-08-24 05:48:46',1),(24,8,'手写数字识别这门课讲的什么？',NULL,'','2025-08-24 05:49:19','2025-08-24 05:49:56',1),(25,8,'软件工程基础这门课程多少学分',NULL,'','2025-08-24 05:51:34','2025-08-24 05:51:40',1),(26,8,'软件工程基础这门课程多少学分',NULL,'','2025-08-24 05:59:19','2025-08-24 05:59:25',1),(27,8,'软件工程基础课程多少学分？',NULL,'','2025-08-24 06:19:49','2025-08-24 06:19:55',1),(28,14,'hello',NULL,'','2025-08-24 06:21:36','2025-08-24 06:21:39',1),(29,14,'软件工程基础课程多少学分？',NULL,'','2025-08-24 06:22:48','2025-08-24 06:22:55',1),(30,14,'软件专业学生的培养方案中大三有哪些课程/',NULL,'','2025-08-24 06:23:54','2025-08-24 06:24:00',1),(31,14,'软件专业学生的培养方案中大三有哪些课程/',NULL,'','2025-08-24 06:37:58','2025-08-24 06:38:09',1),(32,14,'彭思奇为什么请假？',NULL,'','2025-08-24 06:38:50','2025-08-24 06:40:16',0),(33,5,'hello，我们学的预测汽车油耗的课主要讲的啥',NULL,'','2025-08-24 06:42:08','2025-08-24 06:42:13',1),(34,5,'hello，我们学的预测汽车油耗的课主要讲的啥',NULL,'','2025-08-24 06:44:31','2025-08-24 06:44:49',1),(35,14,'《软件工程基础实践》课程具体学分多少？',NULL,'','2025-08-24 06:50:34','2025-08-24 06:50:43',1),(36,5,'《软件工程基础实践》课程具体学分多少？',NULL,'','2025-08-24 06:51:03','2025-08-24 06:51:06',1),(37,5,'手写数字识别利用的什么原理？',NULL,'','2025-08-24 06:51:25','2025-08-24 06:51:47',1),(38,4,'hello',NULL,'','2025-08-24 13:33:33','2025-08-24 13:33:47',1),(39,4,'彭思奇为什么请假？',NULL,'','2025-08-24 23:35:58','2025-08-24 23:35:58',0),(40,4,'彭思奇为什么请假？',NULL,'','2025-08-24 23:38:50','2025-08-24 23:38:49',0),(41,4,'彭思奇为什么请假？',NULL,'','2025-08-24 23:40:45','2025-08-24 23:41:30',1),(42,4,'彭思奇为什么请假？',NULL,'','2025-08-24 23:42:15','2025-08-24 23:42:24',0),(43,4,'这门课程讲的什么？彭思奇请假了这门课程吗？',2,'Linux开发实践','2025-08-24 23:43:17','2025-08-24 23:44:01',1),(44,4,'彭思奇在软件杯的队伍名字叫什么？',NULL,'','2025-08-25 08:06:41','2025-08-25 08:06:47',0),(45,4,'彭思奇在软件杯的队伍名字叫什么？',NULL,'','2025-08-25 08:09:06','2025-08-25 08:09:58',0),(46,8,'彭思奇软件杯的队伍叫什么？',NULL,'','2025-08-25 08:11:38','2025-08-25 08:11:41',1),(47,8,'彭思奇软件杯的队伍叫什么名字？',NULL,'','2025-08-25 08:18:31','2025-08-25 08:18:41',1),(48,4,'彭思奇为什么请假？',NULL,'','2025-08-25 08:30:51','2025-08-25 08:31:14',1),(49,4,'彭思奇软件杯队伍名字是什么？',NULL,'','2025-08-25 12:38:03','2025-08-25 12:38:26',0),(50,4,'彭思奇软件杯队伍叫什么名字？',NULL,'','2025-08-25 12:39:31','2025-08-25 12:39:34',0),(51,4,'预测汽车油耗怎么预测',2,'Linux开发实践','2025-08-25 12:42:30','2025-08-25 12:42:45',0),(52,4,'彭思奇为什么请假？',NULL,'','2025-08-25 12:50:20','2025-08-25 12:50:52',1),(53,8,'彭思奇今天早上吃了什么？',NULL,'','2025-08-25 12:52:58','2025-08-25 12:53:02',1),(54,4,'彭思奇早上吃了什么？',NULL,'','2025-08-26 00:40:13','2025-08-26 00:40:19',1),(55,8,'彭思奇早上吃了什么？',NULL,'','2025-08-26 00:41:28','2025-08-26 00:41:33',1),(56,4,'能为我介绍一下中国软件杯吗',NULL,'','2025-08-26 05:32:06','2025-08-26 05:32:15',0),(57,4,'能为我介绍一下中国软件杯吗',NULL,'','2025-08-26 05:34:10','2025-08-26 05:34:17',0),(58,4,'你能帮我介绍一下中国软件杯吗？',NULL,'','2025-08-26 06:05:57','2025-08-26 06:06:11',0),(59,4,'手写数字识别和中国软件杯有什么关系？',NULL,'','2025-08-26 06:07:12','2025-08-26 06:07:20',0),(60,14,'预测汽车油耗的教学视频里面老师主要讲了什么？',NULL,'','2025-08-26 06:24:50','2025-08-26 06:26:31',1),(61,14,'什么是手写数字识别？手写数字识别依赖的技术原理是什么？',NULL,'','2025-08-26 06:26:51','2025-08-26 06:27:24',1);
/*!40000 ALTER TABLE `chat_session` ENABLE KEYS */;
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
INSERT INTO `classuser` VALUES (1,2,'2025-07-08 06:08:19'),(1,3,'2025-07-08 06:08:19'),(2,2,'2025-07-08 06:14:21'),(2,3,'2025-07-08 06:14:21'),(2,4,'2025-07-08 06:13:32'),(2,5,'2025-07-08 06:14:21'),(2,9,'2025-08-16 08:31:20'),(2,10,'2025-08-16 08:31:58'),(2,12,'2025-08-23 05:11:53'),(2,16,'2025-08-26 00:45:39');
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
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussion`
--

LOCK TABLES `discussion` WRITE;
/*!40000 ALTER TABLE `discussion` DISABLE KEYS */;
INSERT INTO `discussion` VALUES (1,1,1,4,'T002','dqhdiqhdip','uqhdiwudoqwhdfowd',0,0,0,0,'2025-07-17 08:31:43','2025-07-17 08:31:43'),(2,1,1,4,'T002','djwpiqjfdijwpf','diwphfwipqfhipqwhieqpfhiqepfie',0,0,0,0,'2025-07-17 08:32:02','2025-07-17 08:32:02'),(3,1,1,4,'T002','hello','hedoh😎\n````\n\ndihqwid\n\n````\n',0,0,0,0,'2025-08-11 03:39:10','2025-08-11 03:39:10'),(4,1,1,4,'T002','fwfqr21','wqew1e1ee2😅\n````\n\n代码内容\n\n````\n',0,0,4,1,'2025-08-11 03:53:57','2025-08-16 11:46:57'),(5,1,1,4,'T002','fqwfqefgqegqeqq','qqqqqqqqqqqq😊',0,0,10,2,'2025-08-11 04:12:23','2025-08-16 11:47:37'),(6,2,1,4,'T002','求助，这个问题啊巴拉巴拉','这里是求助的内容啊巴拉巴拉\n支持**md格式渲染**和表情包😁\n>真是太酷啦！！',0,0,16,2,'2025-08-16 12:41:03','2025-08-26 00:47:11'),(7,3,1,4,'T002','11111111111','🤣**粗体**\n*斜体*\n~~hello~~\n\n````C\n\nprintf(\"hello,world\");\n\n````\n',0,0,3,1,'2025-08-23 04:54:53','2025-08-23 04:55:25'),(8,2,1,4,'T002','1111111111111','😁··**11111111111111**',0,0,2,0,'2025-08-25 08:02:55','2025-08-25 08:03:09');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `discussionreply`
--

LOCK TABLES `discussionreply` WRITE;
/*!40000 ALTER TABLE `discussionreply` DISABLE KEYS */;
INSERT INTO `discussionreply` VALUES (1,4,4,'T002','byiguhi',NULL,1,'2025-08-11 04:11:52','2025-08-11 04:11:52'),(2,5,4,'T002','f2ygurhou32',NULL,1,'2025-08-11 04:19:28','2025-08-11 04:19:28'),(3,5,4,'T002','回复test1',NULL,1,'2025-08-11 04:19:58','2025-08-11 04:19:58'),(4,6,4,'T002','哇哇好厉害',NULL,1,'2025-08-16 12:41:29','2025-08-16 12:41:29'),(5,6,5,'S003','好棒好棒！！\n',NULL,0,'2025-08-16 12:51:14','2025-08-16 12:51:14'),(6,7,4,'T002','2222222',NULL,1,'2025-08-23 04:55:18','2025-08-23 04:55:18');
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
INSERT INTO `favoritequestion` VALUES (2,1),(5,6),(5,8),(5,34);
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `file_node`
--

LOCK TABLES `file_node` WRITE;
/*!40000 ALTER TABLE `file_node` DISABLE KEYS */;
INSERT INTO `file_node` VALUES (1,'第一章-视频.mp4',0,NULL,1,1,1,-1,'VIDEO',-1,0,1,102400,'CLASS_ONLY','2025-07-08 14:08:19','2025-07-08 14:08:19','/files/1.mp4',1,'/course/1/section/1/第一章-视频.mp4'),(2,'课程-2班级-2',1,NULL,2,2,4,-1,'OTHER',-1,0,1,0,'CLASS_ONLY','2025-07-08 14:17:21','2025-07-08 14:17:21','',0,''),(3,'章节-3',1,2,2,2,4,3,'OTHER',-1,0,1,0,'CLASS_ONLY','2025-07-08 14:17:21','2025-07-08 14:17:21','',0,''),(4,'cp07-样章示例-TensorFlow.js应用开发',0,3,2,2,4,3,'OTHER',3,0,1,1025232,'PUBLIC','2025-07-08 14:17:22','2025-07-08 14:17:22','https://edusoft-file.oss-cn-beijing.aliyuncs.com/89eeaf7aec06421bac972f1cc4807447.docx',0,'89eeaf7aec06421bac972f1cc4807447.docx'),(5,'章节-4',1,2,2,2,4,4,'OTHER',-1,0,1,0,'CLASS_ONLY','2025-07-11 15:41:24','2025-07-11 15:41:24','',0,''),(6,'TensorFlow Lite大纲',0,5,2,2,4,4,'OTHER',4,0,0,1621363,'CLASS_ONLY','2025-07-11 15:41:25','2025-07-11 15:41:25','https://edusoft-file.oss-cn-beijing.aliyuncs.com/d4b5d71843d146ca8f2a9299e3fcd07a.docx',0,'d4b5d71843d146ca8f2a9299e3fcd07a.docx'),(7,'章节-5',1,2,2,2,4,5,'OTHER',-1,0,1,0,'CLASS_ONLY','2025-07-13 21:53:11','2025-07-13 21:53:11','',0,''),(8,'cp09-样章示例-嵌入式Python开发',0,7,2,2,4,5,'OTHER',5,0,1,16101528,'PUBLIC','2025-07-13 21:53:11','2025-07-13 21:53:11','https://edusoft-file.oss-cn-beijing.aliyuncs.com/f6852f2742c94ff1a5ad93696e02617d.docx',0,'f6852f2742c94ff1a5ad93696e02617d.docx'),(9,'02 TensorFlow Lite体系结构.mp4',0,5,2,2,4,4,'VIDEO',4,0,1,82336382,'CLASS_ONLY','2025-07-13 22:10:01','2025-07-13 22:10:01','https://edusoft-file.oss-cn-beijing.aliyuncs.com/f7b6042033714a2b8abf18f7c756a44c.mp4',0,'f7b6042033714a2b8abf18f7c756a44c.mp4'),(10,'03 TensorFlow Lite开发工作流程',0,5,2,2,4,4,'VIDEO',4,0,1,68326935,'PUBLIC','2025-07-13 22:12:58','2025-07-13 22:12:58','https://edusoft-file.oss-cn-beijing.aliyuncs.com/a5afa79e663f4a639ef3a6b929984423.mp4',0,'a5afa79e663f4a639ef3a6b929984423.mp4'),(11,'04 TensorFlow Lite实现花卉识别-1.mp4',0,5,2,2,4,4,'VIDEO',4,0,1,80497520,'PUBLIC','2025-07-13 22:20:13','2025-07-13 22:20:13','https://edusoft-file.oss-cn-beijing.aliyuncs.com/2b9a3831369340369f67eed79ed31751.mp4',0,'2b9a3831369340369f67eed79ed31751.mp4'),(12,'05 TensorFlow Lite实现花卉识别-2',0,5,2,2,4,4,'VIDEO',4,0,1,52814766,'PUBLIC','2025-07-13 22:25:24','2025-07-13 22:25:24','https://edusoft-file.oss-cn-beijing.aliyuncs.com/3f9814391899438b81ee0cbbf9706481.mp4',0,'3f9814391899438b81ee0cbbf9706481.mp4'),(13,'TensorFlow Lite大纲(1)',0,5,2,2,4,4,'OTHER',4,6,1,22314,'CLASS_ONLY','2025-08-25 15:55:05','2025-08-25 15:55:05','https://edusoft-file.oss-cn-beijing.aliyuncs.com/5eb43891b73a4a8e8fd08a18b9bd54dc.docx',1,'5eb43891b73a4a8e8fd08a18b9bd54dc.docx'),(14,'测试文件',0,7,2,2,4,5,'OTHER',5,0,0,10552,'PUBLIC','2025-08-25 16:08:36','2025-08-25 16:08:36','https://edusoft-file.oss-cn-beijing.aliyuncs.com/d708e5c21e944131982598d8f6a7c6f0.docx',0,'d708e5c21e944131982598d8f6a7c6f0.docx'),(15,'测试文件(1)',0,7,2,2,4,5,'OTHER',5,14,1,10552,'PUBLIC','2025-08-25 16:29:43','2025-08-25 16:29:43','https://edusoft-file.oss-cn-beijing.aliyuncs.com/34a69cb7465048d6a31c3f74ab765476.docx',1,'34a69cb7465048d6a31c3f74ab765476.docx'),(16,'测试文件',0,3,2,2,4,3,'OTHER',3,0,1,114879321,'PUBLIC','2025-08-26 08:32:35','2025-08-26 08:32:35','https://edusoft-file.oss-cn-beijing.aliyuncs.com/6a9bf3d42eec48a0b856336615beb165.mp4',0,'6a9bf3d42eec48a0b856336615beb165.mp4');
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `homeworksubmission`
--

LOCK TABLES `homeworksubmission` WRITE;
/*!40000 ALTER TABLE `homeworksubmission` DISABLE KEYS */;
INSERT INTO `homeworksubmission` VALUES (1,1,3,'/attachments/hw1_s2.pdf','/homework_submissions/s2_hw1.pdf','2025-07-08 14:08:19'),(2,2,12,'https://edusoft-file.oss-cn-beijing.aliyuncs.com/bae874fcd89b4c6fb723bec378fc8ef4.docx','bae874fcd89b4c6fb723bec378fc8ef4.docx','2025-08-23 13:12:35'),(3,2,5,'https://edusoft-file.oss-cn-beijing.aliyuncs.com/0079251899f34f46b1e749b573b9772e.docx','0079251899f34f46b1e749b573b9772e.docx','2025-08-25 16:13:22');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `import_record`
--

LOCK TABLES `import_record` WRITE;
/*!40000 ALTER TABLE `import_record` DISABLE KEYS */;
INSERT INTO `import_record` VALUES (1,1,1,'student_list.xlsx',2,2,0,NULL,'2025-07-08 14:08:19','excel'),(2,2,4,NULL,3,3,0,NULL,'2025-07-08 14:14:21','MANUAL'),(3,2,9,NULL,1,1,0,NULL,'2025-08-16 16:31:20','CODE_JOIN'),(4,2,10,NULL,1,1,0,NULL,'2025-08-16 16:31:58','CODE_JOIN'),(5,2,12,NULL,1,1,0,NULL,'2025-08-23 13:11:53','CODE_JOIN'),(6,2,16,NULL,1,1,0,NULL,'2025-08-26 08:45:39','CODE_JOIN');
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `learning_progress`
--

LOCK TABLES `learning_progress` WRITE;
/*!40000 ALTER TABLE `learning_progress` DISABLE KEYS */;
INSERT INTO `learning_progress` VALUES (1,1,2,300.00,300,1,NULL,'2025-07-08 14:08:19','2025-07-08 14:08:19'),(2,1,3,120.00,120,1,NULL,'2025-07-08 14:08:19','2025-07-08 14:08:19'),(3,2,5,100.00,360,1,'2025-08-16 16:28:11','2025-08-16 16:28:11','2025-08-16 16:28:11'),(4,4,4,0.00,1,1,'2025-08-23 12:36:51','2025-08-23 12:36:51','2025-08-23 12:36:51'),(5,4,5,16.00,84,3,'2025-08-26 08:43:01','2025-08-23 12:39:33','2025-08-26 08:43:00'),(6,5,4,0.00,1,1,'2025-08-23 13:06:55','2025-08-23 13:06:55','2025-08-23 13:06:55'),(9,5,5,0.00,2,1,'2025-08-26 08:43:15','2025-08-26 08:43:15','2025-08-26 08:43:15');
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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (1,2,'练习通知','你有一个新练习待完成','practice',0,'2025-07-08 06:08:19',1,'Practice'),(2,2,'新练习提醒','老师发布了新的在线练习：练习1','PRACTICE',0,'2025-07-08 06:14:49',2,'PRACTICE'),(3,3,'新练习提醒','老师发布了新的在线练习：练习1','PRACTICE',0,'2025-07-08 06:14:49',2,'PRACTICE'),(4,4,'新练习提醒','老师发布了新的在线练习：练习1','PRACTICE',0,'2025-07-08 06:14:49',2,'PRACTICE'),(5,5,'新练习提醒','老师发布了新的在线练习：练习1','PRACTICE',1,'2025-07-08 06:14:49',2,'PRACTICE'),(6,2,'新练习提醒','老师发布了新的在线练习：Tensor Flow 练习1','PRACTICE',0,'2025-07-14 15:41:15',3,'PRACTICE'),(7,3,'新练习提醒','老师发布了新的在线练习：Tensor Flow 练习1','PRACTICE',0,'2025-07-14 15:41:15',3,'PRACTICE'),(8,4,'新练习提醒','老师发布了新的在线练习：Tensor Flow 练习1','PRACTICE',0,'2025-07-14 15:41:15',3,'PRACTICE'),(9,5,'新练习提醒','老师发布了新的在线练习：Tensor Flow 练习1','PRACTICE',1,'2025-07-14 15:41:15',3,'PRACTICE'),(10,2,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(11,3,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(12,4,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(13,5,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',1,'2025-08-16 08:33:18',4,'PRACTICE'),(14,9,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(15,10,'新练习提醒','老师发布了新的在线练习：练习小测8.16','PRACTICE',0,'2025-08-16 08:33:18',4,'PRACTICE'),(16,2,'新练习提醒','老师发布了新的在线练习：04 TensorFlow Lite实现花卉识别-1.mp4','PRACTICE',0,'2025-08-25 07:57:09',5,'PRACTICE'),(17,3,'新练习提醒','老师发布了新的在线练习：04 TensorFlow Lite实现花卉识别-1.mp4','PRACTICE',0,'2025-08-25 07:57:09',5,'PRACTICE'),(18,4,'新练习提醒','老师发布了新的在线练习：04 TensorFlow Lite实现花卉识别-1.mp4','PRACTICE',0,'2025-08-25 07:57:09',5,'PRACTICE'),(19,5,'新练习提醒','老师发布了新的在线练习：04 TensorFlow Lite实现花卉识别-1.mp4','PRACTICE',0,'2025-08-25 07:57:09',5,'PRACTICE'),(20,9,'新练习提醒','老师发布了新的在线练习：04 TensorFlow Lite实现花卉识别-1.mp4','PRACTICE',0,'2025-08-25 07:57:09',5,'PRACTICE'),(21,10,'新练习提醒','老师发布了新的在线练习：04 TensorFlow Lite实现花卉识别-1.mp4','PRACTICE',0,'2025-08-25 07:57:09',5,'PRACTICE'),(22,12,'新练习提醒','老师发布了新的在线练习：04 TensorFlow Lite实现花卉识别-1.mp4','PRACTICE',0,'2025-08-25 07:57:09',5,'PRACTICE'),(23,2,'新练习提醒','老师发布了新的在线练习：测试文件','PRACTICE',0,'2025-08-25 12:35:02',6,'PRACTICE'),(24,3,'新练习提醒','老师发布了新的在线练习：测试文件','PRACTICE',0,'2025-08-25 12:35:02',6,'PRACTICE'),(25,4,'新练习提醒','老师发布了新的在线练习：测试文件','PRACTICE',0,'2025-08-25 12:35:02',6,'PRACTICE'),(26,5,'新练习提醒','老师发布了新的在线练习：测试文件','PRACTICE',1,'2025-08-25 12:35:02',6,'PRACTICE'),(27,9,'新练习提醒','老师发布了新的在线练习：测试文件','PRACTICE',0,'2025-08-25 12:35:02',6,'PRACTICE'),(28,10,'新练习提醒','老师发布了新的在线练习：测试文件','PRACTICE',0,'2025-08-25 12:35:02',6,'PRACTICE'),(29,12,'新练习提醒','老师发布了新的在线练习：测试文件','PRACTICE',0,'2025-08-25 12:35:02',6,'PRACTICE');
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
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `practice`
--

LOCK TABLES `practice` WRITE;
/*!40000 ALTER TABLE `practice` DISABLE KEYS */;
INSERT INTO `practice` VALUES (1,1,1,'第一章小测','2025-07-08 14:08:19','2025-07-09 14:08:19',1,1,'2025-07-08 06:08:19'),(2,2,2,'练习1','2025-07-08 14:14:00','2025-07-24 14:14:00',1,4,'2025-07-08 06:14:49'),(3,2,2,'Tensor Flow 练习1','2025-07-14 23:41:00','2025-07-31 23:41:00',1,4,'2025-07-14 15:41:14'),(4,2,2,'练习小测8.16','2025-08-16 16:33:00','2025-08-31 16:33:00',1,4,'2025-08-16 08:33:18'),(5,2,2,'04 TensorFlow Lite实现花卉识别-1.mp4','2025-08-25 15:57:00','2025-08-30 15:57:00',1,4,'2025-08-25 07:57:09'),(6,2,2,'测试文件','2025-08-25 20:34:00','2025-08-27 20:34:00',1,4,'2025-08-25 12:35:02');
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
INSERT INTO `practicequestion` VALUES (1,1,1,10,1.0000),(3,29,NULL,5,0.7143),(3,30,NULL,1,0.7143),(3,31,NULL,1,0.5714),(3,32,NULL,1,0.0000),(3,34,NULL,5,0.0286),(3,35,NULL,10,0.0143),(4,4,NULL,5,1.0000),(4,6,NULL,5,0.3333),(4,8,NULL,5,0.3333),(4,34,NULL,10,0.2667),(4,36,NULL,1,1.0000),(6,53,NULL,1,NULL),(6,76,NULL,10,NULL),(6,77,NULL,10,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=89 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `question`
--

LOCK TABLES `question` WRITE;
/*!40000 ALTER TABLE `question` DISABLE KEYS */;
INSERT INTO `question` VALUES (1,1,'singlechoice','数据库的三大范式是什么？','详见教材第1章','1NF|2NF|3NF|BCNF','1NF|2NF|3NF',1,1,'2025-07-08 06:08:19'),(3,4,'singlechoice','在TensorFlow.js中，以下哪个API用于创建一个全连接层？','全连接层在TensorFlow.js中通过`tf.layers.dense`创建，其他选项分别对应卷积层、池化层和Dropout层。','tf.layers.dense|||tf.layers.conv2d|||tf.layers.maxPooling2d|||tf.layers.dropout','A',2,3,'2025-07-11 08:10:57'),(4,4,'singlechoice','TensorFlow.js中加载预训练模型通常使用的方法是？','`tf.loadLayersModel`是加载Keras格式的预训练模型的标准方法，其他选项为干扰项或旧版API。','tf.loadLayersModel|||tf.loadGraphModel|||tf.loadModel|||tf.importModel','A',2,3,'2025-07-11 08:10:57'),(5,4,'singlechoice','以下哪个操作可以用于TensorFlow.js张量的形状转换？','`tf.reshape`直接改变张量的形状而不改变数据，其他选项分别用于类型转换、连接和复制操作。','tf.reshape|||tf.cast|||tf.concat|||tf.tile','A',2,3,'2025-07-11 08:10:57'),(6,4,'singlechoice','在浏览器中使用TensorFlow.js进行实时摄像头目标检测时，需要优先调用的API是？','浏览器访问摄像头需通过`getUserMedia`获取权限，其他选项是后续处理步骤的API。','navigator.mediaDevices.getUserMedia|||tf.data.webcam|||tf.browser.fromPixels|||document.getElementById','A',2,3,'2025-07-11 08:10:57'),(7,4,'singlechoice','TensorFlow.js模型训练时监控损失值变化的回调函数是？','`onEpochEnd`在每个训练周期结束时触发，适合观察损失变化，其他选项回调时机不匹配。','onEpochEnd|||onBatchBegin|||onTrainBegin|||onYield','A',2,3,'2025-07-11 08:10:57'),(8,4,'fillblank','TensorFlow.js中，将普通JavaScript数组转换为张量的方法是______。','`tf.tensor()`是基础张量构造函数，接受JS数组作为输入参数。','','tf.tensor()',2,3,'2025-07-11 08:10:57'),(9,4,'fillblank','在Node.js环境下使用TensorFlow.js需要安装的包名是______。','官方提供的Node.js专用包包含本地绑定加速运算。','','@tensorflow/tfjs-node',2,3,'2025-07-11 08:10:57'),(10,4,'program','简述TensorFlow.js的WebGL后端与WASM后端的性能差异及适用场景。','WebGL通过显卡并行计算提升性能但兼容性受限，WASM作为轻量级方案保证跨平台一致性。','','WebGL后端利用GPU加速适合复杂模型计算，WASM后端在低端设备或没有WebGL支持的场景下提供更稳定的CPU计算。',2,3,'2025-07-11 08:10:57'),(20,5,'singlechoice','在TensorFlow中，以下哪个函数用于创建一个常量张量？','tf.constant()函数用于创建一个常量张量，其值在创建后不可更改。','tf.Variable()|||tf.constant()|||tf.placeholder()|||tf.Tensor()','B',-1,1,'2025-07-13 15:48:17'),(21,5,'fillblank','在TensorFlow中，用于执行计算图的操作的函数是______。','tf.Session().run()函数用于执行计算图中的操作并获取结果。','','tf.Session().run()',-1,1,'2025-07-13 15:48:17'),(22,5,'judge','TensorFlow 2.x版本默认启用了即时执行模式（Eager Execution）。','TensorFlow 2.x版本默认启用了即时执行模式，无需手动开启。','','true',-1,1,'2025-07-13 15:48:17'),(23,5,'program','使用TensorFlow构建一个简单的线性回归模型，并给出代码实现。','这是一个简单的线性回归模型实现，使用tf.keras.Sequential构建模型，包含一个全连接层，使用随机梯度下降优化器和均方误差损失函数进行训练。','','import tensorflow as tf\n\n# 定义模型\nmodel = tf.keras.Sequential([\n    tf.keras.layers.Dense(units=1, input_shape=[1])\n])\n\n# 编译模型\nmodel.compile(optimizer=\'sgd\', loss=\'mean_squared_error\')\n\n# 训练数据\nX = [1, 2, 3, 4]\ny = [2, 4, 6, 8]\n\n# 训练模型\nmodel.fit(X, y, epochs=100)',-1,1,'2025-07-13 15:48:17'),(24,5,'fillblank','在TensorFlow中，用于创建常量的函数是______。','tf.constant()函数用于在TensorFlow中创建常量张量。','','tf.constant()',-1,1,'2025-07-13 15:52:07'),(25,5,'fillblank','在TensorFlow中，用于初始化所有全局变量的函数是______。','tf.global_variables_initializer()函数用于初始化图中所有的全局变量。','','tf.global_variables_initializer()',-1,1,'2025-07-13 15:52:07'),(26,5,'fillblank','在TensorFlow中，用于定义占位符的函数是______。','tf.placeholder()函数用于定义占位符，可以在运行计算图时传入数据。','','tf.placeholder()',-1,1,'2025-07-13 15:52:07'),(27,5,'fillblank','在TensorFlow中，用于创建全零张量的函数是______。','tf.zeros()函数用于创建指定形状的全零张量。','','tf.zeros()',-1,1,'2025-07-13 15:52:07'),(28,5,'fillblank','在TensorFlow中，用于计算两个张量点积的函数是______。','tf.matmul()函数用于计算两个张量的矩阵乘法（点积）。','','tf.matmul()',-1,1,'2025-07-13 15:52:07'),(29,4,'singlechoice','在TensorFlow.js中，以下哪个方法用于加载预训练模型？','在TensorFlow.js中，加载预训练模型的标准方法是使用tf.loadLayersModel()，该方法会从指定的URL加载模型。其他选项中的方法在TensorFlow.js中并不存在。','tf.loadLayersModel()|||tf.loadModel()|||tf.loadPretrainedModel()|||tf.importModel()','A',2,3,'2025-07-14 15:37:30'),(30,4,'singlechoice','TensorFlow.js中，以下哪个操作可以用于创建一个全零的张量？','在TensorFlow.js中，tf.zeros()方法用于创建一个全零的张量。tf.ones()用于创建全一的张量，而tf.fill()可以填充任意值，但需要指定填充的值。tf.zero()并不是TensorFlow.js中的有效方法。','tf.zeros()|||tf.zero()|||tf.fill()|||tf.ones()','A',2,3,'2025-07-14 15:37:30'),(31,4,'singlechoice','在TensorFlow.js中，以下哪个API用于执行模型的训练？','在TensorFlow.js中，model.fit()是用于执行模型训练的标准API。它会根据提供的数据和标签进行模型的训练。其他选项中的API在TensorFlow.js中并不存在或用于其他目的。','model.fit()|||model.train()|||model.execute()|||model.run()','A',2,3,'2025-07-14 15:37:30'),(32,4,'fillblank','在TensorFlow.js中，用于将普通JavaScript数组转换为张量的方法是______。','tf.tensor()是TensorFlow.js中用于将普通JavaScript数组转换为张量的方法。它可以接受一个数组或嵌套数组，并生成对应的张量对象。','','tf.tensor()',2,3,'2025-07-14 15:37:30'),(33,4,'program','简述在TensorFlow.js中如何定义一个简单的神经网络模型，并说明每一层的作用。','定义一个神经网络模型通常使用tf.sequential()创建一个顺序模型，然后通过model.add()添加层。第一层需要指定inputShape，后续层会自动推断输入形状。每一层的units参数指定神经元的数量，activation参数指定激活函数。','','在TensorFlow.js中，可以通过以下代码定义一个简单的神经网络模型：\n\nconst model = tf.sequential();\nmodel.add(tf.layers.dense({units: 10, inputShape: [5], activation: \'relu\'}));\nmodel.add(tf.layers.dense({units: 1, activation: \'sigmoid\'}));\n\n第一层是一个具有10个神经元和ReLU激活函数的全连接层，输入形状为[5]。第二层是一个具有1个神经元和sigmoid激活函数的输出层，用于二分类任务。',2,3,'2025-07-14 15:37:30'),(34,4,'program','使用TensorFlow.js创建一个简单的线性回归模型，用于预测给定输入x的输出y。模型应包含一个具有1个神经元的全连接层，并使用均方误差作为损失函数。编写完整的代码，包括模型定义、编译和训练步骤。','解题思路：\n1. 使用tf.sequential()创建一个顺序模型\n2. 添加一个具有1个神经元和1维输入的全连接层\n3. 使用均方误差作为损失函数，随机梯度下降作为优化器\n4. 准备训练数据（这里使用了简单的线性关系y=2x-1）\n5. 使用fit方法训练模型，设置10个epoch','','// 定义模型\nconst model = tf.sequential();\nmodel.add(tf.layers.dense({units: 1, inputShape: [1]}));\n\n// 编译模型\nmodel.compile({loss: \'meanSquaredError\', optimizer: \'sgd\'});\n\n// 准备训练数据\nconst xs = tf.tensor2d([1, 2, 3, 4], [4, 1]);\nconst ys = tf.tensor2d([1, 3, 5, 7], [4, 1]);\n\n// 训练模型\nasync function trainModel() {\n  await model.fit(xs, ys, {epochs: 10});\n}\n\ntrainModel();',2,3,'2025-07-14 15:39:19'),(35,4,'program','使用TensorFlow.js加载预训练的MobileNet模型，并对给定的图像进行分类。编写完整的代码，包括模型加载、图像预处理和预测步骤。','解题思路：\n1. 使用tf.loadLayersModel加载预训练的MobileNet模型\n2. 创建图像预处理函数，将图像调整为224x224大小并归一化\n3. 使用模型进行预测，输出预测结果\n4. 注意使用tf.tidy()管理内存','','// 加载MobileNet模型\nasync function loadModel() {\n  const model = await tf.loadLayersModel(\'https://storage.googleapis.com/tfjs-models/tfjs/mobilenet_v1_0.25_224/model.json\');\n  return model;\n}\n\n// 图像预处理\nfunction preprocessImage(imageElement) {\n  return tf.tidy(() => {\n    // 将图像转换为张量\n    let tensor = tf.browser.fromPixels(imageElement)\n      .resizeNearestNeighbor([224, 224]) // 调整大小\n      .toFloat();\n    \n    // 归一化\n    const mean = tf.tensor1d([127.5, 127.5, 127.5]);\n    const std = tf.tensor1d([127.5, 127.5, 127.5]);\n    tensor = tensor.sub(mean).div(std);\n    \n    // 添加批次维度\n    return tensor.expandDims(0);\n  });\n}\n\n// 使用模型进行预测\nasync function predict() {\n  const model = await loadModel();\n  const imageElement = document.getElementById(\'image\');\n  const preprocessedImage = preprocessImage(imageElement);\n  const predictions = model.predict(preprocessedImage);\n  console.log(predictions.dataSync());\n}',2,3,'2025-07-14 15:39:19'),(36,4,'singlechoice','这道题目选A','选a','a|||b|||c|||d','A',2,3,'2025-08-16 08:34:53'),(37,4,'program','在嵌入式Linux系统中，如何将Python脚本打包成一个可执行文件？请简述步骤。','解题思路：在嵌入式Linux系统中，Python脚本通常需要打包成可执行文件以便于分发和运行。PyInstaller是一个常用的工具，可以将Python脚本打包成独立的可执行文件。通过安装PyInstaller并使用其命令行工具，可以轻松完成打包过程。','','1. 安装PyInstaller工具：`pip install pyinstaller`。2. 使用PyInstaller打包脚本：`pyinstaller --onefile your_script.py`。3. 将生成的可执行文件复制到嵌入式Linux系统中。',2,5,'2025-08-17 07:14:58'),(38,4,'program','在嵌入式Linux系统中，如何确保Python脚本在系统启动时自动运行？请提供一种实现方法。','解题思路：在嵌入式Linux系统中，可以通过systemd服务来管理Python脚本的自动启动。创建一个自定义的systemd服务文件，并配置脚本的路径和启动命令，然后启用和启动该服务，即可实现脚本在系统启动时自动运行。','','1. 创建一个systemd服务文件，例如`/etc/systemd/system/myscript.service`。2. 在服务文件中配置Python脚本的路径和启动命令。3. 启用并启动服务：`sudo systemctl enable myscript.service` 和 `sudo systemctl start myscript.service`。',2,5,'2025-08-17 07:14:59'),(39,4,'program','在嵌入式Python开发中，如何通过Python脚本读取GPIO引脚的状态？请提供一个示例代码片段。','解题思路：在嵌入式Python开发中，可以使用RPi.GPIO库来操作GPIO引脚。首先设置GPIO模式（如BCM或BOARD），然后配置引脚为输入模式，最后通过GPIO.input()函数读取引脚状态。','','示例代码：\n```python\nimport RPi.GPIO as GPIO\nGPIO.setmode(GPIO.BCM)\nGPIO.setup(17, GPIO.IN)\nstate = GPIO.input(17)\nprint(f\"GPIO 17 state: {state}\")\n```',2,5,'2025-08-17 07:14:59'),(40,4,'program','在嵌入式Linux系统中，如何通过Python脚本实现一个简单的串口通信？请提供一个示例代码片段。','解题思路：在嵌入式Linux系统中，可以使用Python的serial库来实现串口通信。首先创建一个Serial对象，指定串口设备（如/dev/ttyS0）和波特率，然后通过write()发送数据，通过readline()接收数据。','','示例代码：\n```python\nimport serial\nser = serial.Serial(\'/dev/ttyS0\', 9600, timeout=1)\nser.write(b\'Hello\')\nresponse = ser.readline()\nprint(f\"Response: {response}\")\nser.close()\n```',2,5,'2025-08-17 07:14:59'),(41,4,'program','在嵌入式Python开发中，如何通过Python脚本实现一个简单的HTTP服务器？请提供一个示例代码片段。','解题思路：在嵌入式Python开发中，可以使用Python内置的http.server模块来实现一个简单的HTTP服务器。通过继承BaseHTTPRequestHandler并实现do_GET方法，可以处理HTTP GET请求。然后创建一个HTTPServer实例并启动它。','','示例代码：\n```python\nfrom http.server import BaseHTTPRequestHandler, HTTPServer\n\nclass SimpleHandler(BaseHTTPRequestHandler):\n    def do_GET(self):\n        self.send_response(200)\n        self.send_header(\'Content-type\', \'text/html\')\n        self.end_headers()\n        self.wfile.write(b\'Hello, Embedded Linux!\')\n\nserver = HTTPServer((\'0.0.0.0\', 8080), SimpleHandler)\nserver.serve_forever()\n```',2,5,'2025-08-17 07:14:59'),(42,4,'singlechoice','在Linux环境下，以下哪个命令可以用于将TensorFlow模型转换为TensorFlow Lite格式？','在Linux系统中，可以使用`tflite_convert`命令行工具将TensorFlow模型转换为TensorFlow Lite支持的`.tflite`格式。','tflite_convert|||tensorflow_lite_convert|||convert_tflite|||lite_convert','A',2,4,'2025-08-22 09:14:37'),(43,4,'singlechoice','TensorFlow Lite运行时（Runtime）在Linux平台上主要通过哪种方式执行模型推理？','TensorFlow Lite提供了解释器接口，可以通过C++或Python API加载模型并执行推理。','通过Python的TensorFlow库直接执行|||通过C++或Python API调用解释器|||通过浏览器JavaScript引擎执行|||通过系统内核模块执行','B',2,4,'2025-08-22 09:14:37'),(44,4,'singlechoice','在使用TensorFlow Lite进行模型优化时，以下哪项技术可以用于减小模型大小并提升推理速度？','模型量化是TensorFlow Lite中常用的优化技术，通过降低模型权重的精度（例如从32位浮点数转为8位整数）来减小模型体积并提升推理效率。','模型量化|||数据增强|||模型剪枝|||模型蒸馏','A',2,4,'2025-08-22 09:14:37'),(45,4,'singlechoice','在Linux环境下，使用TensorFlow Lite进行模型部署时，下列哪项是主要依赖的库文件？','TensorFlow Lite的Linux部署依赖`libtensorflow-lite.so`动态链接库，该库包含了运行TensorFlow Lite模型所需的核心功能。','libtensorflow-core.so|||libtensorflow-lite.so|||libtensorflow-cpu.so|||libtensorflow-gpu.so','B',2,4,'2025-08-22 09:14:37'),(46,4,'singlechoice','以下哪种设备最适合使用TensorFlow Lite进行模型部署？','TensorFlow Lite是专为移动和嵌入式设备设计的轻量级推理框架，适合资源受限的环境。','高性能服务器|||嵌入式设备|||GPU工作站|||大型机','B',2,4,'2025-08-22 09:14:37'),(47,4,'fillblank','在Linux系统中，使用命令`tflite_convert`时，输入模型通常为______格式。','TensorFlow Lite的`tflite_convert`工具通常接受TensorFlow的SavedModel作为输入格式进行模型转换。','','SavedModel',2,4,'2025-08-22 09:14:37'),(48,4,'fillblank','TensorFlow Lite的核心组件中，负责解析和执行模型的部分称为______。','TensorFlow Lite中的解释器（Interpreter）负责加载模型并执行推理操作。','','Interpreter',2,4,'2025-08-22 09:14:37'),(49,4,'fillblank','在Linux环境中，TensorFlow Lite模型的文件扩展名通常是______。','TensorFlow Lite专用的模型文件格式通常以`.tflite`为扩展名。','','.tflite',2,4,'2025-08-22 09:14:37'),(50,4,'fillblank','在Linux系统中，TensorFlow Lite支持通过______实现模型的加速推理。','TensorFlow Lite允许通过Delegates机制将部分或全部推理任务委托给特定硬件（如GPU或NPU）以提高性能。','','Delegates',2,4,'2025-08-22 09:14:37'),(51,4,'fillblank','TensorFlow Lite的______模式允许在运行时根据设备条件动态选择最佳的推理方式。','TensorFlow Lite的Flex模式允许使用TensorFlow的完整操作集，从而在运行时动态选择最合适的执行路径。','','Flex',2,4,'2025-08-22 09:14:37'),(52,4,'program','简述在Linux系统中，如何使用`tflite_convert`命令将一个SavedModel格式的模型转换为TensorFlow Lite格式。','使用`tflite_convert`命令时，需要指定`--saved_model_dir`参数指向保存的模型路径，以及`--output_file`参数指定输出文件路径。','','tflite_convert --saved_model_dir=/path/to/saved_model --output_file=/path/to/output.tflite',2,4,'2025-08-22 09:14:37'),(53,4,'program','列举TensorFlow Lite支持的三种优化技术，并简要说明其作用。','TensorFlow Lite支持多种模型优化技术，包括量化、剪枝和操作融合，这些技术可以提升模型在移动和嵌入式设备上的性能。','','1. 量化：降低权重精度以减小模型大小和提升推理速度。\n2. 剪枝：移除模型中不重要的连接以减少计算量。\n3. 操作融合：将多个操作合并为一个以减少推理时间。',2,4,'2025-08-22 09:14:37'),(54,5,'singlechoice','在嵌入式Python环境中，以下哪个模块通常用于处理GPIO引脚？','RPi.GPIO是用于树莓派上控制GPIO引脚的常用模块，其他选项是Python的标准库模块，不用于控制硬件。','os|||sys|||RPi.GPIO|||math','C',-1,1,'2025-08-23 04:42:12'),(55,5,'singlechoice','以下哪个设备通信协议最常用于与传感器进行短距离、低速的串行通信？','I2C是一种常用的短距离、低速串行通信协议，非常适合嵌入式设备和传感器之间的数据传输。UART和SPI也是嵌入式通信协议，但I2C更为常见于传感器通信。TCP/IP是网络协议栈，不适用于设备内部通信。','UART|||SPI|||I2C|||TCP/IP','C',-1,1,'2025-08-23 04:42:12'),(56,5,'singlechoice','在嵌入式系统中，以下哪个选项是Python相比C/C++的主要劣势？','Python是一种解释型语言，相比C/C++的编译型特性，其运行效率较低，这在资源受限的嵌入式环境中尤为明显。','可移植性差|||运行效率低|||开发周期长|||语法复杂','B',-1,1,'2025-08-23 04:42:12'),(57,5,'singlechoice','在使用TensorFlow.js进行实时摄像头目标检测时，以下哪个方法可用于从视频帧中提取像素数据？','在TensorFlow.js中，`tf.browser.fromPixels`方法用于将视频帧的像素数据转换为张量，以便后续进行目标检测。','tf.browser.fromPixels|||tf.data.webcam|||tf.tensor2d|||tf.image.resizeBilinear','A',-1,1,'2025-08-25 00:26:52'),(58,5,'fillblank','在浏览器中使用TensorFlow.js加载预训练模型时，通常调用________方法来从指定URL加载模型。','加载预训练模型通常使用`tf.loadGraphModel`方法，它可以从指定的URL异步加载模型文件。',NULL,'tf.loadGraphModel',-1,1,'2025-08-25 00:26:52'),(59,5,'singlechoice','在巴黎和会上，以下哪个国家未被邀请参与主要谈判？','巴黎和会于1919年召开，主要战胜国（英国、法国、美国等）主导了会议，而战败国如德国并未被邀请参与主要谈判。','德国|||英国|||美国|||法国','A',-1,1,'2025-08-25 00:27:11'),(60,5,'fillblank','中国第一个五年计划的时间跨度是从______年至______年。','中国第一个五年计划从1953年开始，到1957年结束，重点发展重工业，旨在奠定国家工业化基础。','','1953,1957',-1,1,'2025-08-25 00:27:11'),(61,5,'singlechoice','在使用TensorFlow.js进行浏览器端模型推理时，若需将图像数据转换为模型可处理的张量格式，应优先调用以下哪个方法？','`tf.browser.fromPixels`可以直接从HTML元素（如<canvas>或<img>）中读取像素并生成张量，是处理图像输入最直接的API。','tf.browser.fromPixels|||tf.tensor2d|||tf.image.resizeBilinear|||tf.data.decodeImage','A',-1,1,'2025-08-25 00:29:20'),(62,5,'fillblank','在TensorFlow.js中，使用`model.predict()`进行推理前，通常需要先调用______方法对输入数据进行标准化处理。','标准化（normalize）用于将像素值缩放到[0,1]或其他模型所需的范围，以适配模型训练时的数据分布。','normalize|||resize|||expandDims|||toFloat','normalize',-1,1,'2025-08-25 00:29:20'),(63,5,'singlechoice','在TensorFlow.js中，以下哪个方法用于创建一个形状为[2, 3]的张量，并用数值2填充？','所有列出的方法都可以用于创建一个2x3的张量并用数值2填充。tf.tensor2d直接构造；tf.fill生成指定值的张量；tf.ones生成全1张量后通过mul(2)变为全2。','tf.tensor2d([[2, 2, 2], [2, 2, 2]])|||tf.fill([2, 3], 2)|||tf.ones([2, 3]).mul(2)|||以上都可以','D',-1,1,'2025-08-25 00:30:19'),(64,5,'fillblank','在TensorFlow.js中，用于将张量转换为JavaScript数组的方法是______。','在TensorFlow.js中，调用张量的array()方法可以将其异步转换为普通的JavaScript多维数组。','','array()',-1,1,'2025-08-25 00:30:19'),(65,4,'singlechoice','在TensorFlow Lite中，用于在移动设备上进行模型推理的运行时环境是哪一个？','TensorFlow Lite Interpreter 是 TensorFlow Lite 的核心组件，专门用于在移动设备或嵌入式设备上加载和运行经过优化的TFLite模型。','TensorFlow Serving|||TensorFlow Lite Interpreter|||TensorFlow.js|||TensorFlow Hub','B',2,4,'2025-08-25 00:42:17'),(66,4,'singlechoice','在使用TensorFlow Lite时，以下哪一项是将模型转换为TFLite格式的标准工具？','tflite_convert 是 TensorFlow 提供的命令行工具，用于将 SavedModel、Keras 模型或其他格式转换为 TensorFlow Lite 模型（.tflite）。','tflite_convert|||tf.lite.convert|||tflite_optimize|||tf.model.convert','A',2,4,'2025-08-25 00:42:17'),(67,4,'singlechoice','TensorFlow Lite支持以下哪种量化方式以优化模型性能？','TensorFlow Lite支持多种量化方式，包括动态范围量化、静态范围量化和完全量化，用于减少模型大小并提升推理速度。','动态范围量化|||静态范围量化|||完全量化|||以上全部','D',2,4,'2025-08-25 00:42:17'),(68,4,'singlechoice','在Linux环境下部署TensorFlow Lite模型时，下列哪个库是必须安装的？','在Linux平台上进行TensorFlow Lite开发，需要安装TFLite提供的C++或者Python库，以支持模型的加载与推理。','TensorFlow|||TensorFlow Lite C++库|||OpenCV|||NumPy','B',2,4,'2025-08-25 00:42:17'),(69,4,'fillblank','在TensorFlow Lite中，用于表示模型输入输出数据结构的类是 ________。','TfLiteTensor 是 TensorFlow Lite 中用来表示张量的结构体，用于描述输入和输出的数据类型、形状等信息。','','TfLiteTensor',2,4,'2025-08-25 00:42:17'),(70,4,'fillblank','TensorFlow Lite模型文件通常以 ________ 扩展名保存。','TensorFlow Lite模型经过转换后保存为 .tflite 文件格式，这是一种轻量化的FlatBuffer格式。','','.tflite',2,4,'2025-08-25 00:42:17'),(71,4,'fillblank','在Linux环境下使用TensorFlow Lite进行推理时，加载模型的核心函数是 ________。','TfLiteModelCreateFromFile 函数用于从文件中加载TensorFlow Lite模型，是C API中的标准方法。','','TfLiteModelCreateFromFile',2,4,'2025-08-25 00:42:17'),(72,4,'fillblank','为了减少TensorFlow Lite模型的大小和推理延迟，通常会采用 ________ 技术。','量化是一种常用的模型优化技术，通过减少模型中数值的精度（如从浮点转为整型）来减小模型体积和提升推理效率。','','量化',2,4,'2025-08-25 00:42:18'),(73,4,'fillblank','TensorFlow Lite支持 ________ 操作以在推理过程中进行硬件加速。','TensorFlow Lite支持委托机制（Delegate），允许开发者将部分计算任务委托给GPU、NNAPI或其它加速器。','','委托',2,4,'2025-08-25 00:42:18'),(74,4,'program','请简述在Linux环境下，如何使用TensorFlow Lite部署一个图像分类模型的基本流程。','部署过程需要涵盖模型转换、环境配置、数据预处理、模型推理和结果解析等关键步骤，每个环节都需要适配Linux平台的API和库。','','基本流程包括：1. 准备训练好的模型；2. 使用tflite_convert工具将其转换为.tflite格式；3. 安装必要的TFLite运行时库；4. 编写代码加载模型、预处理输入图像；5. 调用Interpreter进行推理；6. 解析输出结果。',2,4,'2025-08-25 00:42:18'),(75,4,'program','阅读以下使用TensorFlow.js在浏览器中进行简单模型训练的代码，分析并回答代码中每一步的作用，并指出可能存在的问题。\n\nconst model = tf.sequential();\nmodel.add(tf.layers.dense({inputShape: [10], units: 1}));\nmodel.compile({optimizer: \'sgd\', loss: \'meanSquaredError\'});\n\nconst xs = tf.randomUniform([100, 10]);\nconst ys = tf.randomUniform([100, 1]);\n\nmodel.fit(xs, ys, {\n  epochs: 1,\n  validationSplit: 0.2\n});','该代码实现了一个最简单的TensorFlow.js模型训练流程，包括模型定义、编译、数据生成和训练。其中关键步骤包括模型构建、损失函数和优化器配置、数据准备以及模型训练。问题主要集中在数据随机性导致无学习意义、训练轮次过少，以及优化器参数未指定。','','作用分析：\n1. 创建一个顺序模型。\n2. 添加一个全连接层，输入维度为10，输出一个值。\n3. 配置优化器和损失函数。\n4. 生成随机输入数据和目标数据。\n5. 使用fit方法训练模型，训练1个epoch，并保留20%数据用于验证。\n\n可能存在的问题：\n- 使用随机生成的数据进行训练，没有实际意义。\n- epoch数量太少，模型无法有效学习。\n- 使用SGD优化器但未指定学习率，可能影响训练效果。',2,3,'2025-08-25 07:58:23'),(76,4,'program','请阅读以下Python代码片段，并回答问题。\n\n在嵌入式Linux系统中运行以下Python脚本：\n\n```python\nimport os\nimport time\n\npid = os.fork()\n\nif pid == 0:\n    # 子进程\n    print(\'Child process is running with PID:\', os.getpid())\n    time.sleep(3)\n    print(\'Child process is exiting\')\nelse:\n    # 父进程\n    print(\'Parent process is waiting for child...\')\n    os.wait()\n    print(\'Child process has exited, parent is done.\')\n```\n\n问题：这段代码的功能是什么？请说明`os.fork()`和`os.wait()`的作用，并解释父子进程的执行顺序。','代码首先调用`os.fork()`，这将创建一个子进程。返回值pid为0表示当前是子进程，否则是父进程。子进程执行打印语句后休眠3秒然后退出；父进程调用`os.wait()`进入阻塞状态，直到子进程执行完毕，然后继续打印完成信息。此机制常用于进程管理与同步。','','这段代码演示了在Python中使用`os.fork()`创建子进程，并利用`os.wait()`使父进程等待子进程结束的典型进程控制流程。`os.fork()`用于创建一个新进程（子进程），`os.wait()`使父进程等待子进程终止。',2,5,'2025-08-25 12:34:30'),(77,4,'program','阅读以下Python与C混合编程的代码片段，并回答问题。\n\n在一个嵌入式Linux环境中，使用Python的`ctypes`模块调用如下C函数：\n\n```c\n// libsample.so\n#include <stdio.h>\n\nvoid greet(char *name) {\n    printf(\"Hello, %s from C!\\n\", name);\n}\n```\n\n对应的Python代码如下：\n\n```python\nfrom ctypes import CDLL, c_char_p\n\nlib = CDLL(\"./libsample.so\")\nlib.greet(b\"Embedded Linux\")\n```\n\n问题：请解释这段Python代码的功能。`ctypes.CDLL`和`b\"Embedded Linux\"`的作用分别是什么？','`ctypes.CDLL`加载了名为`libsample.so`的共享库（C语言编译生成），使得Python可以调用其中的函数。`greet()`函数接受一个`char*`类型的参数，因此Python中传入的字符串需要是字节类型（前缀`b`表示字节字符串），以便与C语言兼容。这种混合编程方式常用于嵌入式系统中实现性能敏感部分的优化。','','该段代码的功能是通过Python的`ctypes`模块调用C语言编写的动态库函数`greet()`，实现从Python中调用C语言函数。`ctypes.CDLL`用于加载动态链接库，`b\"Embedded Linux\"`表示将字符串以字节形式传入C函数。',2,5,'2025-08-25 12:34:30'),(78,5,'singlechoice','在嵌入式系统开发中，以下哪一项是使用Python的主要优势？','Python具有丰富的库和简洁的语法，能够显著提升开发效率，加快迭代周期，尽管其运行效率不如C/C++，但在原型设计和功能验证中具有明显优势。','更高的运行效率|||更小的内存占用|||更快的开发迭代速度|||直接访问硬件寄存器','C',-1,1,'2025-08-25 12:47:25'),(79,5,'singlechoice','在资源受限的嵌入式设备中，选择C语言而非Python进行开发的主要原因是？','C语言是编译型语言，生成的可执行文件效率更高，占用资源更少，适合嵌入式系统这种资源受限的环境。Python作为解释型语言，在运行效率和资源占用方面相对劣势。','C语言具有更丰富的图形界面库|||C语言程序运行效率更高|||C语言更容易学习和使用|||C语言支持跨平台自动编译','B',-1,1,'2025-08-25 12:47:25'),(80,4,'program','阅读以下使用TensorFlow.js在浏览器中进行图像分类的代码片段，回答问题。\n\n```javascript\n// 加载预训练模型\nconst model = await tf.loadLayersModel(\'model.json\');\n\n// 图像预处理函数\nfunction preprocessImage(imageElement) {\n  const tensor = tf.browser.fromPixels(imageElement)\n    .resizeNearestNeighbor([224, 224])\n    .toFloat()\n    .div(tf.scalar(255))\n    .expandDims();\n  return tensor;\n}\n\n// 预测函数\nasync function predict() {\n  const imageTensor = preprocessImage(document.getElementById(\'image\'));\n  const prediction = await model.predict(imageTensor).argMax(1).data();\n  console.log(`预测类别索引为：${prediction}`);\n}\n```\n\n问题：请简要说明以下内容：\n1. `tf.browser.fromPixels()` 和 `resizeNearestNeighbor()` 的作用。\n2. 图像张量在传入模型前为什么要进行 `toFloat()` 和 `div(tf.scalar(255))` 操作？','1. `tf.browser.fromPixels()` 用于将 HTML 中的图片或 canvas 元素转换为张量；由于模型输入大小为 224x224，需用 `resizeNearestNeighbor()` 调整图像尺寸。\n2. 图像数据通常为 0-255 的整数，深度学习模型通常接受 0-1 范围的浮点数输入，因此需要进行类型转换和归一化。','','1. `tf.browser.fromPixels()` 从图像元素中读取像素生成张量；`resizeNearestNeighbor()` 调整图像尺寸为模型输入要求的大小。\n2. `toFloat()` 将像素值转为浮点型，`div(tf.scalar(255))` 对图像进行归一化处理，将像素值范围从[0, 255]变为[0, 1]，以便适配模型输入。',2,3,'2025-08-26 00:37:07'),(81,4,'program','阅读以下使用 TensorFlow.js 构建简单线性回归模型的代码片段，回答问题。\n\n```javascript\nconst model = tf.sequential();\nmodel.add(tf.layers.dense({inputShape: [1], units: 1, useBias: true}));\n\nmodel.compile({\n  optimizer: \'sgd\',\n  loss: \'meanSquaredError\'\n});\n\nconst xs = tf.tensor1d([1, 2, 3, 4]);\nconst ys = tf.tensor1d([2, 4, 6, 8]);\n\nawait model.fit(xs, ys, {epochs: 10});\n\nconst prediction = model.predict(tf.tensor1d([5]));\nprediction.print();\n```\n\n问题：\n1. 该模型使用的优化器和损失函数分别是什么？\n2. 在 `model.predict(tf.tensor1d([5]))` 运行后，你期望输出什么数值？为什么？','1. 代码中 `model.compile` 设置了优化器为 `\'sgd\'`，损失函数为 `\'meanSquaredError\'`，适用于回归任务。\n2. 由于训练数据 x 与 y 呈线性关系 y=2x，模型经过训练后应能学习到该关系，输入 x=5 时，预测 y=2*5=10。','','1. 使用的优化器是 `sgd`（随机梯度下降），损失函数是 `meanSquaredError`（均方误差）。\n2. 预期输出接近 10。因为训练数据为 y = 2x，模型训练后应能学习到斜率为 2 的线性关系，因此输入 x=5 输出 y=10。',2,3,'2025-08-26 00:37:08'),(82,5,'singlechoice','在嵌入式Python环境中，以下哪种方式最适合用于在资源受限的系统中减小内存占用？','MicroPython是CPython的精简版本，专为微控制器和资源受限环境设计，能够显著减小内存占用，而完整CPython或PyQt在嵌入式场景中通常过于庞大。','使用完整的CPython解释器|||启用subprocess模块并运行外部脚本|||使用轻量级实现如MicroPython|||使用PyQt进行界面开发','C',-1,1,'2025-08-26 00:48:11'),(83,5,'singlechoice','在嵌入式Python中，若需通过底层寄存器直接与硬件通信，最可能使用以下哪种模块？','mmap模块允许将文件或设备内存映射到进程地址空间，适合在嵌入式Python中直接访问硬件寄存器。os和sys模块主要用于系统级操作，但不直接支持寄存器级别的访问。','os模块|||sys模块|||mmap模块|||threading模块','C',-1,1,'2025-08-26 00:48:11'),(84,4,'singlechoice','在Linux环境下，使用TensorFlow Lite进行模型推理时，以下哪项是加载.tflite模型文件的正确方法？','在TensorFlow Lite中，TFLiteInterpreter类提供了from_file()方法来加载本地的.tflite模型文件。其他选项分别是用于TensorFlow SavedModel、OpenCV模型加载和PyTorch模型加载的方法，不适用于TensorFlow Lite的模型加载。','使用TensorFlow的tf.saved_model.load()函数|||使用TFLiteInterpreter的from_file()方法|||使用OpenCV的readNetFromTensorflow()函数|||使用PyTorch的torch.load()函数','B',2,4,'2025-08-26 06:04:04'),(85,4,'singlechoice','在Linux系统中使用TensorFlow Lite时，若需要对模型输入进行量化处理，通常采用以下哪种数据类型？','TensorFlow Lite为了提升推理效率，支持量化推理，通常将输入数据从float32量化为uint8，以减少内存占用和计算资源消耗。','float32|||int8|||uint8|||float16','C',2,4,'2025-08-26 06:04:04'),(86,4,'singlechoice','在TensorFlow Lite中，用于将TensorFlow模型转换为.tflite格式的工具是？','TensorFlow提供了tflite_convert命令行工具用于将训练好的TensorFlow模型（如SavedModel或Keras模型）转换为TensorFlow Lite支持的.tflite格式。','tflite_convert|||convert_tflite|||tflite_run|||tf_to_lite','A',2,4,'2025-08-26 06:04:04'),(87,4,'singlechoice','在Linux系统中使用TensorFlow Lite部署模型时，以下哪种方式可以提高推理速度？','在TensorFlow Lite中，利用多线程处理（如使用Interpreter的SetNumThreads方法）可以有效提升推理速度。使用float32会降低性能，增加层数和输入尺寸也会增加计算负担。','使用多线程处理|||使用浮点数输入(float32)|||增加模型层数|||使用更大的输入尺寸','A',2,4,'2025-08-26 06:04:04'),(88,4,'fillblank','在TensorFlow Lite中，______ API用于在Linux平台上进行硬件加速推理（如使用GPU或NPU）。','TfLiteGpuDelegate是TensorFlow Lite提供的一个API，用于在支持的硬件（如GPU或NPU）上进行加速推理，提升模型运行效率。','','TfLiteGpuDelegate',2,4,'2025-08-26 06:04:04');
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
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selfanswer`
--

LOCK TABLES `selfanswer` WRITE;
/*!40000 ALTER TABLE `selfanswer` DISABLE KEYS */;
INSERT INTO `selfanswer` VALUES (2,23,20,'A',1,0,0,1),(3,23,21,'gk',1,0,0,2),(4,23,22,'正确',1,0,0,3),(5,23,23,'goihi',1,0,0,4),(6,24,24,'tge',1,0,0,1),(7,24,25,'rear',1,0,0,2),(8,24,26,'twt',1,0,0,3),(9,24,27,'wtt',1,0,0,4),(10,24,28,'wtew',1,0,0,5),(11,25,54,'A',1,0,0,1),(12,25,55,'A',1,0,0,2),(13,25,56,'A',1,0,0,3),(14,26,59,'A',1,1,10,1),(15,26,60,'2023',1,0,0,2),(16,27,61,'A',1,1,10,1),(17,27,62,'normalize',1,1,10,2),(18,28,78,'A',1,0,0,1),(19,28,79,'A',1,0,0,2),(20,29,82,'A',1,0,0,1),(21,29,83,'A',1,0,0,2);
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selfpractice`
--

LOCK TABLES `selfpractice` WRITE;
/*!40000 ALTER TABLE `selfpractice` DISABLE KEYS */;
INSERT INTO `selfpractice` VALUES (1,5,'AI自测 2025-07-09T13:08:39.237133700','2025-07-09 05:08:39'),(2,5,'AI自测 2025-07-11T16:22:36.987621600','2025-07-11 08:22:37'),(3,5,'AI自测 2025-07-13T22:36:30.208420400','2025-07-13 14:36:30'),(4,5,'AI自测 2025-07-13T23:05:12.254846800','2025-07-13 15:05:12'),(5,5,'AI自测 2025-07-13T23:10:40.866229300','2025-07-13 15:10:41'),(6,5,'AI自测 2025-07-13T23:13:46.943990400','2025-07-13 15:13:47'),(7,5,'AI自测 2025-07-13T23:22:42.427809900','2025-07-13 15:22:42'),(8,5,'AI自测 2025-07-13T23:27:01.733158800','2025-07-13 15:27:02'),(9,5,'AI自测 2025-07-13T23:34:43.615120500','2025-07-13 15:34:44'),(10,5,'AI自测 2025-07-13T23:44:19.351836400','2025-07-13 15:44:19'),(11,5,'AI自测 2025-07-13T23:48:17.070030300','2025-07-13 15:48:17'),(12,5,'AI自测 2025-07-13T23:52:07.385601600','2025-07-13 15:52:07'),(13,5,'AI自测 2025-08-23T12:42:12.699661800','2025-08-23 04:42:13'),(14,5,'AI自测 2025-08-25T08:26:52.906628300','2025-08-25 00:26:53'),(15,5,'AI自测 2025-08-25T08:27:11.542856500','2025-08-25 00:27:12'),(16,5,'AI自测 2025-08-25T08:29:20.276568400','2025-08-25 00:29:20'),(17,5,'AI自测 2025-08-25T08:30:19.698189400','2025-08-25 00:30:20'),(18,5,'AI自测 2025-08-25T20:47:25.439913400','2025-08-25 12:47:25'),(19,5,'AI自测 2025-08-26T08:48:11.298020400','2025-08-26 00:48:11');
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
INSERT INTO `selfpracticequestion` VALUES (11,20,1,10),(11,21,2,10),(11,22,3,10),(11,23,4,10),(12,24,1,10),(12,25,2,10),(12,26,3,10),(12,27,4,10),(12,28,5,10),(13,54,1,10),(13,55,2,10),(13,56,3,10),(14,57,1,10),(14,58,2,10),(15,59,1,10),(15,60,2,10),(16,61,1,10),(16,62,2,10),(17,63,1,10),(17,64,2,10),(18,78,1,10),(18,79,2,10),(19,82,1,10),(19,83,2,10);
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
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `selfsubmission`
--

LOCK TABLES `selfsubmission` WRITE;
/*!40000 ALTER TABLE `selfsubmission` DISABLE KEYS */;
INSERT INTO `selfsubmission` VALUES (21,7,5,'2025-07-13 15:22:53',0,1,NULL),(23,11,5,'2025-07-13 15:48:52',0,1,NULL),(24,12,5,'2025-07-13 15:52:58',0,1,NULL),(25,13,5,'2025-08-23 04:42:25',0,1,NULL),(26,15,5,'2025-08-25 00:27:22',10,1,NULL),(27,16,5,'2025-08-25 00:29:36',20,1,NULL),(28,18,5,'2025-08-25 12:47:41',0,1,NULL),(29,19,5,'2025-08-26 00:48:19',0,1,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `submission`
--

LOCK TABLES `submission` WRITE;
/*!40000 ALTER TABLE `submission` DISABLE KEYS */;
INSERT INTO `submission` VALUES (1,1,2,'2025-07-08 06:08:19',10,1,'回答正确'),(2,3,5,'2025-07-14 15:48:23',9,1,NULL),(3,3,5,'2025-07-15 03:12:55',6,0,NULL),(4,3,5,'2025-07-15 03:18:14',0,0,NULL),(5,3,5,'2025-07-16 14:33:56',0,1,NULL),(6,3,5,'2025-07-16 14:52:47',7,0,NULL),(7,3,5,'2025-07-16 15:04:39',7,0,NULL),(8,3,5,'2025-07-16 15:23:00',7,0,NULL),(9,4,12,'2025-08-23 05:24:56',14,1,NULL),(10,4,5,'2025-08-23 14:23:45',12,1,NULL),(11,6,5,'2025-08-25 12:44:50',0,0,NULL),(12,4,16,'2025-08-26 00:46:00',11,0,NULL),(13,6,5,'2025-08-26 06:08:35',0,0,NULL),(14,6,5,'2025-08-26 06:49:36',0,0,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teaching_resource`
--

LOCK TABLES `teaching_resource` WRITE;
/*!40000 ALTER TABLE `teaching_resource` DISABLE KEYS */;
INSERT INTO `teaching_resource` VALUES (1,'第一章视频资源','视频讲解',1,1,'第一章 数据库基础','VIDEO','/resource/1.mp4','/resource/1.mp4',600,1,'2025-07-08 14:08:19','2025-07-08 14:08:19'),(2,'01 认识TensorFlow.js-1.mp4','学习视屏，请务必观看： 认识TensorFlow.js-1.mp4。',2,3,'cp07-TensorFlow.js应用开发','VIDEO','https://edusoft-file.oss-cn-beijing.aliyuncs.com/4bf05a31bb504e6e8ac01e70eae3291d.mp4','4bf05a31bb504e6e8ac01e70eae3291d.mp4',360,4,'2025-08-16 16:27:04','2025-08-16 16:27:46'),(4,'04 TensorFlow Lite实现花卉识别-1.mp4','04 TensorFlow Lite实现花卉识别-1.mp4',2,4,'cp08-TensorFlow Lite','VIDEO','https://edusoft-file.oss-cn-beijing.aliyuncs.com/77f1799b137c48a09b221fa185bc57f5.mp4','77f1799b137c48a09b221fa185bc57f5.mp4',521,4,'2025-08-23 12:26:23','2025-08-23 12:36:49'),(5,'10.1 qt.mp4','10.1 qt.mp4',2,5,'cp09-嵌入式Python开发','VIDEO','https://edusoft-file.oss-cn-beijing.aliyuncs.com/8914d687add54f43aa90a1a2f48d8f9e.mp4','8914d687add54f43aa90a1a2f48d8f9e.mp4',723,4,'2025-08-23 13:01:24','2025-08-23 13:06:52');
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
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'T001','张老师','hashed_pwd1','teacher','teacher@example.com','2025-07-08 06:08:19','2025-07-08 06:08:19'),(2,'S001','小明','hashed_pwd2','student','xiaoming@example.com','2025-07-08 06:08:19','2025-07-08 06:08:19'),(3,'S002','小红','hashed_pwd3','student','xiaohong@example.com','2025-07-08 06:08:19','2025-07-08 06:08:19'),(4,'T002','Teacher2','bc1785c9eda2973c1157cbbb90a8b1f9','teacher','pengsquare82@gmail.com','2025-07-08 06:09:39','2025-07-08 06:09:39'),(5,'S003','User','bc1785c9eda2973c1157cbbb90a8b1f9','student','buaavolunteers@sina.com','2025-07-08 06:10:10','2025-07-08 06:10:10'),(7,'Admin1','Admin','bc1785c9eda2973c1157cbbb90a8b1f9','tutor','23371112@buaa.edu.cn','2025-07-08 06:11:41','2025-07-08 06:11:41'),(8,'T003','Teacher2','bc1785c9eda2973c1157cbbb90a8b1f9','teacher','pengsquare82@gmail.com','2025-07-08 15:04:32','2025-07-08 15:04:32'),(9,'S004','S004','bc1785c9eda2973c1157cbbb90a8b1f9','student','pengsquare82@gmail.com','2025-08-16 08:30:33','2025-08-16 08:30:33'),(10,'S005','S005','bc1785c9eda2973c1157cbbb90a8b1f9','student','pengsquare82@gmail.com','2025-08-16 08:31:01','2025-08-16 08:31:01'),(11,'T100','T100','bc1785c9eda2973c1157cbbb90a8b1f9','teacher','buaavolunteers@sina.com','2025-08-21 09:50:31','2025-08-21 09:50:31'),(12,'S100','S100','bc1785c9eda2973c1157cbbb90a8b1f9','student','3418482772@qq.com','2025-08-23 05:11:35','2025-08-23 05:11:35'),(13,'A001','A001','bc1785c9eda2973c1157cbbb90a8b1f9','tutor','3418482772@qq.com','2025-08-23 05:29:54','2025-08-23 05:29:54'),(14,'T004','T004','bc1785c9eda2973c1157cbbb90a8b1f9','teacher','3418482772@qq.com','2025-08-24 06:21:15','2025-08-24 06:21:15'),(15,'T111','T111','bc1785c9eda2973c1157cbbb90a8b1f9','teacher','3418482772@qq.com','2025-08-25 08:41:03','2025-08-25 08:41:03'),(16,'S222','S222','bc1785c9eda2973c1157cbbb90a8b1f9','student','peterthree54@gmail.com','2025-08-26 00:45:25','2025-08-26 00:45:25');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_summary`
--

DROP TABLE IF EXISTS `video_summary`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_summary` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '摘要ID',
  `resource_id` bigint NOT NULL COMMENT '关联的教学资源ID',
  `video_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '视频标题',
  `course_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '课程名称',
  `summary` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '视频内容概述',
  `transcript` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '视频转写文本',
  `duration` int DEFAULT '0' COMMENT '视频时长(秒)',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_resource_id` (`resource_id`),
  KEY `idx_course_name` (`course_name`),
  CONSTRAINT `fk_video_summary_resource` FOREIGN KEY (`resource_id`) REFERENCES `teaching_resource` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频摘要表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_summary`
--

LOCK TABLES `video_summary` WRITE;
/*!40000 ALTER TABLE `video_summary` DISABLE KEYS */;
INSERT INTO `video_summary` VALUES (1,2,'01 认识TensorFlow.js-1.mp4','','该视频介绍了TensorFlow.js的基本概念、应用场景、支持的平台和主要功能。内容涵盖TensorFlow.js的部署环境（如浏览器、Node.js、小程序）、模型加载方式、预训练模型类型、开发流程及优势，并简要回顾了其发展历史和实际应用案例。','ThanThanFlow.gts是一个用于使用加娃Squript进行机器学习开发的库用于在流览器和Note.gts讯论和部署机器学习模型ThanThanFlow.gts支持使用加娃Squript在流览器端部署也与微信小程序有很好的机程ThanThanFlow.gts支持所有Pyson可以加载的模型在Note.gts环境中直接通过调言API即可在流览器环境中需要转换为流览器支持的JSON格式ThanThanFlow.gts提供了一系列预讯论好的模型包括图像识别 余音识别 人体姿态识别 物体识别 文字分类等在这个项目中我们将通过预测汽车油耗效率首写数字识别 两个实力演示如何使用ThanThanFlow.gts进行ThanThanFlow模型的开发讯认和部署体验ThanThanFlow.gts直接在流览器加载ThanThanFlow通过本地CPU或者GPU资源进行所需的机器学习运算灵活的进行各种AI应用的开发我们将学习ThanThanFlow.gts的优点ThanThanFlow.gts的体系价格以及相关概念学习ThanThanFlow.gts环境配置在流览器中使用ThanThanFlow.gts能在Note.gts中使用ThanThanFlow.gts能通过Layer\'s API, call API,创建模型能熟练模型部署开发相应AI应用ThanThanFlow.gts是一个开源机器学习库可运行在加娃Scrept可以运行的任何位置它基于使用Pyson编写的原始ThanThanFlow.gts直在为加娃Scrept生态系统打造这种开发者体验和移系列API建于加娃Scrept的可移植性我们可以使用一种语言编写并在以下所有平台上轻松直行机器学习使用原版加娃Scrept的网络流览器中的客户端使用Note.gts进行服务器端甚至是RESPERBELRY, PII等IOT设备使用Electron的装面应用使用React Native的原生移动应用ThanThanFlow.gts还支持在每个环境中使用多个后端例如可以在CPU或WebGL中直行的实际基于硬件的环境使出的后端并不意味着服务器端环境以确保肩容性并保持运行速度在2017年,一个叫Dip Learning.gts的项目诞生了这是一款基于WebGL加速的开放原带马加娃Scrept机器学习库该库可以直接在流览器中运行而无需进行安装也无需借助后端运行在2018年3月,Dip Learning.gts团队与ThanThanFlow团队合并从命名为ThanThanFlow.gtsThanThanFlow.gts可以让我们直接在流览器中加载ThanThanFlow让用户立即通过本地的CPU或GPU资源进行我们所需要的机器学习运算更灵活的进行AI应用的开发流览器中进行机器学习相比于服务计端来讲由以下施打优势一,不需要安装软件或驱动打开流览器即可使用二,可以通过流览器进行更加方便的人机交互三,可以通过手机流览器调用手机硬件的各种传感器例如GPS,身上头等等似,用户的数据可以无需上传的服务器在本地即可完成所需操作官网有很多ThanThanFlow.gts是构建的势力和债线演示例如在网页端讯练一个模型来识别图片或语音讯练一个模型以新影的方式玩游戏我构建一个能创造钢琴音乐的神经网络的谷歌推出的AI小程序参画小歌在朋友圈风黄刷屏现在谷歌AI又有了更有趣的打开方式WooMeghr可以将你的姿势与同一姿势的其他人的图像相匹配利用机器来捕捉人类的动作然后跟8万多幅图像进行比对收索找出与人类动作一致的图像',360,'2025-08-23 04:16:02','2025-08-23 04:16:02'),(2,4,'04 TensorFlow Lite实现花卉识别-1.mp4','','该视频介绍了如何使用TensorFlow Lite在安卓设备上实现花卉识别应用。内容涵盖：使用MobileNet V2进行迁移学习训练花卉识别模型，使用TFLite转换器将模型转换为适用于移动端的格式，在安卓应用中使用TFLite解释器运行模型，并利用TFLite支持库对输入输出进行预处理和后处理。训练过程中还介绍了数据增强、模型微调等关键技术。','这个项目我们将会在安卓上使用TenzeFulloLight 时别花朵基于看花十名APP应用讲解TenzeFullo模型如何应用于安卓系统在服务器端讯练TenzeFullo模型并把模型文件迁移到智能中端以及TenzeFullo安卓开发环境够简以及应用开发APP在安卓设备上运行图像识别模型MobileNet V2来识别花会第一步通过天意学习实现花会识别模型第二步使用TFLight转换器转换模型第三步在安卓应用中使用TFLight解释器运行它第四步使用TenzeFulloLight 支持库预处理模型输入和后处理模型输出我们选择MobileNet V2进行千亿学习实现实别花会模型MobileNet V2是基于一个流线型的价格它使用深度可分离的卷机来构建轻量级的深层神经网我们将使用带有标注的花会数据集该设法会载入在ImageNet刚1000上的预讯联模型载花会数据集上做天意学习使用小型数据集时通常会利用在同意预中的较大数据集上训练的模型所学习的特征预训练的模型被动节并且仅在训练期间更新分类器的权重花会数据集可以载TenzeFullo官网下载每个紫文件夹都存储了一种类别的花的图片紫文件夹的名称就是花的类别的名称平均每一种花有734张图片图片都是RGB色彩模式的接下来我们将数据集划分为训练集和验证集训练前需要手动夹载图像数据完成包括变力数据集的目楼结构夹载图像数据以及返回输入和输出Kirons提供了Image Data Generator类它是一个图片声成器负责声成多个批词的图片以声成器的形式给模型训练Image Data Generator的构造罕述包含许多参数用于指定夹载后如何操作图像数据包括像素缩放和数据增强接着需要一个迭代器来逐步夹载单个数据集的图像这需要调用Flow From Directors的含数并指定该数据集目楼如Tream Validation目楼含数还允许配置于夹载图像相关的更多细节它给的Size参数允许将所有图像夹载到一个模型需要的特定的大小数值大小为224、224的正方形图像BatchSize 默认为32意思是许念时从数据集中的不同内中随机选出的32个图像该值设置为64在评估模型时可能还希望以确定性设许返回P处理就可以通过将Safe参数设置为FOSS我们选择MobileNetVR进行千亿学习首先实力化一个预加载了EmitNight 信念全中的MobileNetVR模型MobileNetVR模型默认是将图片分类到1000类每一类都有各自的标注我们项目中花贵分类只有5类所以购件模型的时候增加Include, Top等于FOSS参数表示不需要远有模型中最后的神经网络层即分类到1000类由于是通过千亿学习改造模型所以不改变基础模型的各项参数变量因为这样才能保留原来大规模训练的优势使用Model.trinable等于FOSS设置在训练中基础模型的各项参数变量不会被新的训练修改数据专讯通常的做法在展平操作之前依赖于最后一层该成称为平景层与最终或顶层相比平景层保持了很多通用性随后在原有模型的后面增加一个实化层对数据降围最后是一个五个节点的输出层因为需要的结果只有五类模型优化算法使用Odam损失韩束使用类别交叉商使用准确率作为判断训练结果的参数经过10个周期的训练后我们应该在验证极上看到约94%的准确率刚才我们紧在MobileNetwork2基础模型的顶部训练了一些层预训练网络的权重在训练过程中并为更新需要进一步提高性能就需要在训练预训练模型顶层的权重的同时另外训练我们添加的分类器应该尝试微调式的方式使用少量顶层而不是整个MobileNet模型前几层学习非常简单和通用的功能这些功能可以推广到几乎所有类别的图像随着层越来越高这些功能越来越多的针对训练模型的数据级微调的目的是使这些专用功能适应新数据级而不是覆盖通用学习首先取消洞捷模型的顶层取消洞捷被small的MobileNet V2模型网络一共155层前100层任设置为无法训练然后重新编一模型并恢复训练经过微调后模型精度几乎达到了98%当微调MobileNet V2基础模型的最后几层并在习上训练分类气时验证损失远远高于训练损失模型可能有一些过渡你合我们使用TF.save的model.save模型我们使用TF.save的model.save然后将模型保存为TFlight肩容格式save的model包含一个完整的分色flow程序不仅包含全重纸还包含计算它不需要原始模型购件代码就可以运行',521,'2025-08-23 04:32:34','2025-08-23 04:32:34'),(3,5,'10.1 qt.mp4','','本视频介绍了Qt跨平台开发框架的基础知识，包括其在嵌入式GUI开发中的重要性、发展历程、版本分类，以及Qt Creator开发环境的主要界面和功能。内容旨在帮助开发者快速了解Qt的核心模块、开发流程，并熟悉开发工具的使用。','从这节课开始,我们给大家介绍一下签论式的 GUI冰程,就是图形用户界面的冰程。在签论式系统中, GUI的地位也越来越重要,但是不同于桌面系统。签论式系统的硬件资源是有效,因此签论式的用户界面要求简单直观,可靠,再用资源小冰淇淋反映迅速。感谢齐聚公司为我们提供了QT,无论是专业的程序开发人员,还是编程爱号者,他们都希望自己编写的程序可以流畅的运行与各种平坦。QT在这一方面出种的表现,令我们印象深刻。利用QT提供的西加加英雄程序开发框架,可以轻松的实现,一次编写随处编绎的跨平台解决方案。使得我们的英雄程序能够完美的运行与各种平台之上。下面我们看一下QT的发展历史。在1991年的时候,Howver的开始开发QT,他提出了信号跟朝的概念并且开发出了第一个图形的核心。在94年的时候,成立了齐聚公司。2008年,齐聚公司被诺家公司收购,QT因此成为诺家旗下的冰层员工具。在2009年12月1日,诺家发布了QT4.6。2012年,QT又被迪迪来收购。2014年,跨平台去升开发环境QT Create 3.1证实发布,并且实现了对IOA4的完全支持。2014年,5月20日,QT开发团队宣布QT5.3证实发布。QT现在有四种版本。第一个就是文山奥的,它是用于文路士平台。另外是X11版,它是用于X系统的各种Linux和Unix平台。第三个是Mac板,它是用于苹果的IOA4系统。最后一个就是X版本,Inbadi的版,它是用于X系统。QT是一个庞大的跨平台开发框架,它提供了非常丰富的习家家力。我们看一下这个框架图,最底下是QT Core,它提供了核心的飞机外功能,所有模块都需要它。这个模块它提供了动画框架,定是器,各种容器类,时间日期类,事件,参见机制,图形,线程,XMNL等。在左边两个,第一个是QT Test,它提供了QT程序的单元测试功能。另外一个是QT Circle,它提供了使用社口访问数据库。在右边最上面QT WebCate,这个是Web流冷器引擎,它提供了显示和编辑Web内容。这就是QT Multimedia,这个是对Dormity的支持。QT QML提供了公QML使用的C家家Appi,QML是一种脚本语言。QT Quick是一种基于QT的高度动画的用户界面。适合与移动平台开发。QT Network它提供了跨平台的网络功能。QT Great,这个是其公Gi程序的基本功能,包括与窗口系统的集成,事件处理等等,就是跟窗体相关的。QT还提供了QT Great,QT Great是一个跨平台的完整的QT级程开发环境。包括了私家家的代码冰机器,项目和生成管理工具,集成了像下文相关的帮助系统,图形化的调试器,代码管理和流冷工具等。QT Great的主界面,下面我们简单的介绍一下QT Great的界面,还有它的一些功能,给大家做一下简单的介绍。QT打开以后主界面就是这样的,在上面是菜单,菜单项。在它左边,这个位置是一个叫模式选择器,在这里我们看有几种模式,这个是我们可以选的。这边是固件套件的选择器。这个我们叫定力器。另外还有输出窗口,这边是用于输出的各种输出信息的。首先看一下模式选择器,第一个是欢迎介面。在这边我们看有几个,第一个是入门。这里面主要是一些文档,我们可以打开看一下,因为这里面文档都是英文的,QT文档很全,全都是英文的。这个主要是第一个创建宵母,这个是打开宵母,打开右的宵母,这边是我们已经打开过了的宵母。第三个是例子,这里边的例子大家可以看一下很多。第四个是教程,有一些简单的教程,大家也可以看一下。第四个是我们最主要的一个工作区,就是编写代码的地方,这边是我们的项目,我们同时可以打开几个项目,可以打开多个项目,每一个项目我们看都有投文件,原文件,其他文件。如果有界面的话,我们就还会有一个界面文件。这是我们最主要的一个工作区,就是编写代码的地方。第三个设计,这个是用于设计我们界面文件的。第1个项目因为我们没有界面文件,所以这边是灰的,有的同学怎么觉得我的实际界面没有不能用,不是不能用,是因为你没有界面。如果我有界面文件的话,我们打开界面文件,这个时候就转到我们是实际界面了。实际界面是用于我们界面设计的。在这里边我们可以进行部件属性的设置,比如我放了一个按钮罢屏,在这边是我的属性,属性窗口。另外我们还可以进行信号跟草的设置,还可以进行布局设置等等。加去一个调试窗口,当我们调试的时候,我们可以用到。还有是项目,这个是进行项目配置的。项目配置的,我们要进行构建的设置,运行的设置,并计器的设置,代码风格的设置等等。并计器代码风格依赖关系。还有一个是分析模式,分析模式在这里边我们看有一个是QML的分析器,另外还有内存分析器。最后一个是帮助,帮助。大家也应该习惯看这些帮助,因为QT的类优很多,我们不可能每个类都非常熟悉。如果有不会的,我们就可以在这里边查一下。在固件套件选择器里边有4个按钮,第1个按钮是误标选择器,第2个按钮是运行按钮,第3个是调试按钮。最后一个是固件按钮,固件按钮我们就可以把它理解为是编移按钮。按了以后它就开始编移了。运行按钮就是运行程序的,我们运行点了运行以后我们的程序就开始运行了。第1个目标选择器,目标选择器里边我们可以选择第8个还是Release,就是调试版本还是发布版本。如果我们多个QT项目的话在这边我们还可以选择多个项目,我们要调试哪一个项目。并且我们每一个项目都会有两个版本第1个发布的第2个是调试的。如果我们有多个QT版本的话我们还可以选择QT的版本。这集课我们简单介绍了一下QT的情况,同学们回去以后可以打安装QTCREATE,安装了试一下,我们可以下载里边的例子,QTC它自带了很多的例子,这些例子也非常的有用。我们把它下载下载然后便宜运行一下,主要是要熟悉一下QTCREATE的机程开发环境,以后我们会一直会使用到它。这集课我们就上到这边,同学们再见。',723,'2025-08-23 05:04:28','2025-08-23 05:04:28');
/*!40000 ALTER TABLE `video_summary` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_summary_keypoint`
--

DROP TABLE IF EXISTS `video_summary_keypoint`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_summary_keypoint` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
  `summary_id` bigint NOT NULL COMMENT '关联的摘要ID',
  `point` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '知识点内容',
  `timestamp` int DEFAULT '0' COMMENT '相关时间点(秒)',
  `importance` enum('high','medium','low') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'medium' COMMENT '重要程度',
  PRIMARY KEY (`id`),
  KEY `idx_summary_id` (`summary_id`),
  KEY `idx_timestamp` (`timestamp`),
  KEY `idx_importance` (`importance`),
  CONSTRAINT `fk_keypoint_summary` FOREIGN KEY (`summary_id`) REFERENCES `video_summary` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频知识点表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_summary_keypoint`
--

LOCK TABLES `video_summary_keypoint` WRITE;
/*!40000 ALTER TABLE `video_summary_keypoint` DISABLE KEYS */;
INSERT INTO `video_summary_keypoint` VALUES (1,1,'TensorFlow.js 是一个用于 JavaScript 的机器学习库，可在浏览器和 Node.js 中运行。',0,'high'),(2,1,'支持将 Python 训练的模型加载到 TensorFlow.js，浏览器中需转换为 JSON 格式。',30,'high'),(3,1,'提供预训练模型如图像识别、语音识别、姿态识别、文字分类等。',53,'medium'),(4,1,'可运行在多种平台，包括浏览器、Node.js、IoT、Electron、React Native 等。',108,'high'),(5,1,'支持多种后端计算，如 CPU、WebGL，提升运行效率。',117,'medium'),(6,1,'浏览器端机器学习的优势：无需安装、本地数据处理、人机交互方便。',155,'high'),(7,2,'使用MobileNet V2进行迁移学习，适配5类花卉分类任务。',72,'high'),(8,2,'ImageDataGenerator用于图像数据增强和批处理加载。',110,'high'),(9,2,'冻结基础模型参数，在迁移学习中只训练顶层分类器。',125,'high'),(10,2,'微调策略：逐步解冻MobileNet V2的部分高层以提升模型性能。',140,'high'),(11,2,'使用Adam优化器、交叉熵损失函数进行模型训练。',130,'medium'),(12,2,'模型保存为TFLite格式，适用于移动端部署。',175,'high'),(13,2,'在安卓应用中使用TFLite解释器加载模型并进行推理。',205,'high'),(14,2,'TFLite支持库用于预处理输入图像和后处理输出结果。',215,'medium'),(15,2,'数据集结构：每个子文件夹代表一个花卉类别，平均734张图像。',98,'medium'),(16,2,'最终模型在验证集上达到约98%的准确率。',245,'high'),(17,3,'Qt是嵌入式系统中图形用户界面开发的重要工具，具有资源占用小、响应快、跨平台等特点。',26,'high'),(18,3,'Qt由Howver于1991年开发，提出信号与槽机制，后由齐聚公司发布，并经历多次并购和技术迭代。',94,'high'),(19,3,'Qt目前主要有四个版本，分别适用于Windows、Linux/Unix、macOS和X系统。',156,'medium'),(20,3,'Qt框架核心模块包括Qt Core、Qt Network、Qt Quick、Qt WebKit等，提供全面的开发支持。',187,'high'),(21,3,'Qt Creator是Qt官方开发环境，集成了代码编辑器、调试器、项目管理、帮助系统等功能。',250,'high'),(22,3,'Qt Creator提供多种工作模式，包括欢迎界面、编辑模式、设计模式、调试模式、分析模式和帮助模式。',450,'medium'),(23,3,'建议学生下载并安装Qt Creator，运行官方示例以熟悉开发环境。',600,'medium');
/*!40000 ALTER TABLE `video_summary_keypoint` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_summary_stage`
--

DROP TABLE IF EXISTS `video_summary_stage`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_summary_stage` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '阶段ID',
  `summary_id` bigint NOT NULL COMMENT '关联的摘要ID',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '阶段标题',
  `start_time` int NOT NULL DEFAULT '0' COMMENT '开始时间(秒)',
  `end_time` int NOT NULL DEFAULT '0' COMMENT '结束时间(秒)',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci COMMENT '阶段描述',
  `sort_order` int DEFAULT '1' COMMENT '排序序号',
  PRIMARY KEY (`id`),
  KEY `idx_summary_id` (`summary_id`),
  KEY `idx_start_time` (`start_time`),
  CONSTRAINT `fk_stage_summary` FOREIGN KEY (`summary_id`) REFERENCES `video_summary` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频摘要阶段表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_summary_stage`
--

LOCK TABLES `video_summary_stage` WRITE;
/*!40000 ALTER TABLE `video_summary_stage` DISABLE KEYS */;
INSERT INTO `video_summary_stage` VALUES (1,1,'TensorFlow.js 简介',0,20,'介绍TensorFlow.js是一个用于JavaScript进行机器学习开发的库，支持在浏览器和Node.js中训练和部署机器学习模型。',1),(2,1,'TensorFlow.js 支持的部署环境',20,30,'讲解TensorFlow.js可以在浏览器端使用JavaScript进行部署，并与微信小程序有良好的集成。',2),(3,1,'模型加载与支持格式',30,42,'说明TensorFlow.js支持加载Python训练的模型，在Node.js环境中通过API直接调用，而在浏览器中需转换为JSON格式。',3),(4,1,'预训练模型与应用示例',42,62,'介绍TensorFlow.js提供图像识别、语音识别、人体姿态识别、物体识别和文字分类等预训练模型。',4),(5,1,'项目目标与学习内容',62,91,'概述本项目将通过预测汽车油耗效率和手写数字识别两个示例演示TensorFlow.js模型开发、训练与部署流程，并体验浏览器端直接加载和本地资源调用。',5),(6,1,'TensorFlow.js 的优势与适用平台',91,117,'总结TensorFlow.js的优点，支持在浏览器、Node.js、IoT设备、Electron和React Native等多种平台运行。',6),(7,1,'TensorFlow.js 的后端支持与发展历程',117,140,'讲解TensorFlow.js支持CPU/WebGL等多种后端，回顾其发展历史，从最初的Deeplearn.js项目到与TensorFlow合并成为TensorFlow.js的过程。',7),(8,1,'浏览器端机器学习的优势',140,155,'总结在浏览器端运行机器学习的优势，包括无需安装、便于交互、可调用传感器和本地数据处理等。',8),(9,1,'实际应用案例展示',155,180,'展示基于TensorFlow.js构建的多个实际案例，如图像识别、AI游戏、音乐生成、Google的AI小程序以及姿势识别应用WooMe等。',9),(10,2,'项目介绍与目标',0,34,'视频开头介绍了项目目标，即在安卓设备上使用TensorFlow Lite实现花卉识别应用。同时概述了实现的主要步骤，包括模型训练、模型转换、安卓端部署及输入输出处理，并提到了基于‘看花识名’APP的应用场景。',1),(11,2,'模型训练准备与迁移学习',36,90,'讲解了使用MobileNet V2进行迁移学习训练花卉识别模型的过程。介绍了数据集结构、训练集与验证集划分、ImageDataGenerator的使用方法，以及如何利用预训练模型进行特征迁移，构建适用于5类花卉分类的模型结构。',2),(12,2,'模型训练与优化策略',91,170,'详细描述了模型训练过程，包括优化器选择、损失函数、评估指标设置，以及在初步训练后引入微调策略以提升准确率。重点讲解了冻结基础模型层、逐步解冻并重新训练部分层的方法，以及对模型过拟合的分析和应对措施。',3),(13,2,'模型保存与转换为TFLite',170,200,'介绍如何使用TensorFlow模型保存功能，将训练好的模型保存为TFLite兼容格式。强调了保存模型的完整性及独立运行能力，无需原始模型构建代码。',4),(14,2,'TensorFlow Lite在安卓中的集成',200,240,'讲解了将TFLite模型部署到安卓应用中的流程，包括使用TFLite转换器进行模型转换、在安卓应用中调用TFLite解释器运行模型，以及使用TFLite支持库进行输入预处理和输出后处理的步骤。',5),(15,2,'项目总结与预期效果',240,521,'总结项目实现的整体流程，回顾了模型训练、优化、保存与安卓部署的全过程，并指出最终模型在验证集上可达到约98%的准确率，说明整个迁移学习和微调策略的有效性。',6),(16,3,'Qt框架与嵌入式GUI介绍',0,88,'介绍Qt作为嵌入式系统中图形用户界面开发工具的重要性，指出其资源占用小、响应快、跨平台等优势，并感谢齐聚公司提供该框架。',1),(17,3,'Qt的发展历程',88,156,'回顾Qt从1991年开始开发到2014年的发展历程，包括其创始人、公司并购情况（齐聚→诺基亚→迪迪来）以及关键版本发布时间。',2),(18,3,'Qt的版本分类',156,187,'介绍Qt目前的四个主要版本，分别是Windows版、X11版（用于Linux/Unix）、Mac版和X版本（Inbadi平台使用）。',3),(19,3,'Qt核心模块介绍',187,250,'详细讲解Qt框架的各个核心模块，包括Qt Core、Qt Test、Qt Network、Qt Quick、Qt WebKit等，强调其跨平台和模块化特点。',4),(20,3,'Qt Creator开发环境简介',250,450,'介绍Qt Creator的主界面布局，包括模式选择器、固件套件选择器、输出窗口等，讲解其作为跨平台开发环境所包含的代码编辑器、调试器、项目管理工具等核心功能。',5),(21,3,'Qt Creator功能详解',450,600,'深入讲解Qt Creator的不同模式（如欢迎界面、编辑模式、设计模式、调试模式、分析模式和帮助模式），以及如何进行项目配置、构建、运行和调试等操作。',6),(22,3,'课程总结与学习建议',600,723,'总结Qt在跨平台开发中的优势，建议学生安装并尝试使用Qt Creator，通过运行官方示例加深理解，并为后续课程打下基础。',7);
/*!40000 ALTER TABLE `video_summary_stage` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `video_summary_timestamp`
--

DROP TABLE IF EXISTS `video_summary_timestamp`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `video_summary_timestamp` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '时间点ID',
  `summary_id` bigint NOT NULL COMMENT '关联的摘要ID',
  `time` int NOT NULL COMMENT '时间点(秒)',
  `event` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '重要事件或内容描述',
  `type` enum('concept','example','summary','question') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'concept' COMMENT '事件类型',
  PRIMARY KEY (`id`),
  KEY `idx_summary_id` (`summary_id`),
  KEY `idx_time` (`time`),
  KEY `idx_type` (`type`),
  CONSTRAINT `fk_timestamp_summary` FOREIGN KEY (`summary_id`) REFERENCES `video_summary` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='视频重要时间点表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `video_summary_timestamp`
--

LOCK TABLES `video_summary_timestamp` WRITE;
/*!40000 ALTER TABLE `video_summary_timestamp` DISABLE KEYS */;
INSERT INTO `video_summary_timestamp` VALUES (1,1,0,'介绍 TensorFlow.js 核心功能','concept'),(2,1,30,'讲解 TensorFlow.js 模型加载格式','concept'),(3,1,53,'列出 TensorFlow.js 提供的预训练模型','example'),(4,1,62,'项目目标：预测汽车油耗与手写数字识别','example'),(5,1,117,'TensorFlow.js 后端支持与历史演变','concept'),(6,1,140,'浏览器端机器学习的核心优势','summary'),(7,1,155,'展示 TensorFlow.js 实际应用案例','example'),(8,2,0,'项目目标介绍：在安卓上使用TensorFlow Lite识别花卉','summary'),(9,2,36,'开始讲解使用MobileNet V2进行迁移学习','concept'),(10,2,91,'数据集结构介绍与划分训练/验证集','concept'),(11,2,110,'ImageDataGenerator的使用方法讲解','concept'),(12,2,125,'冻结MobileNet V2基础层进行迁移学习','concept'),(13,2,140,'引入微调策略，解冻部分高层以提升性能','concept'),(14,2,175,'保存模型为TFLite格式','example'),(15,2,205,'讲解在安卓中使用TFLite解释器运行模型','concept'),(16,2,215,'TFLite支持库进行输入预处理与输出后处理','concept'),(17,2,245,'总结训练效果与准确率','summary'),(18,3,0,'课程开始，介绍Qt与嵌入式GUI开发的重要性','summary'),(19,3,88,'开始讲解Qt的发展历史','concept'),(20,3,156,'介绍Qt的四个版本分类','concept'),(21,3,187,'讲解Qt框架的模块组成','concept'),(22,3,250,'展示Qt Creator主界面及其功能布局','example'),(23,3,450,'介绍Qt Creator不同工作模式的用途','example'),(24,3,600,'总结课程内容，建议学生动手实践','summary');
/*!40000 ALTER TABLE `video_summary_timestamp` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `wrongquestion`
--

LOCK TABLES `wrongquestion` WRITE;
/*!40000 ALTER TABLE `wrongquestion` DISABLE KEYS */;
INSERT INTO `wrongquestion` VALUES (1,5,20,'A','B',1,'2025-07-13 15:48:35','2025-07-13 15:48:35'),(2,5,21,'gk','tf.Session().run()',1,'2025-07-13 15:48:35','2025-07-13 15:48:35'),(3,5,22,'正确','true',1,'2025-07-13 15:48:35','2025-07-13 15:48:35'),(4,5,23,'goihi','import tensorflow as tf\n\n# 定义模型\nmodel = tf.keras.Sequential([\n    tf.keras.layers.Dense(units=1, input_shape=[1])\n])\n\n# 编译模型\nmodel.compile(optimizer=\'sgd\', loss=\'mean_squared_error\')\n\n# 训练数据\nX = [1, 2, 3, 4]\ny = [2, 4, 6, 8]\n\n# 训练模型\nmodel.fit(X, y, epochs=100)',1,'2025-07-13 15:48:51','2025-07-13 15:48:51'),(5,5,24,'tge','tf.constant()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(6,5,25,'rear','tf.global_variables_initializer()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(7,5,26,'twt','tf.placeholder()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(8,5,27,'wtt','tf.zeros()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(9,5,28,'wtew','tf.matmul()',1,'2025-07-13 15:52:57','2025-07-13 15:52:57'),(10,5,54,'A','C',1,'2025-08-23 04:42:25','2025-08-23 04:42:25'),(11,5,55,'A','C',1,'2025-08-23 04:42:25','2025-08-23 04:42:25'),(12,5,56,'A','B',1,'2025-08-23 04:42:25','2025-08-23 04:42:25'),(13,5,34,'const xs,ys;\nconst model=tf.sequential();\nmodel.add(tf.layers.dense({\nunits:1,\ninputShape:[1]\n}));\nmodel.compile({\noptimizer:tf.train.sgd(0.9),\nloss: \"meanSquaredError\"\n});\n','// 定义模型\nconst model = tf.sequential();\nmodel.add(tf.layers.dense({units: 1, inputShape: [1]}));\n\n// 编译模型\nmodel.compile({loss: \'meanSquaredError\', optimizer: \'sgd\'});\n\n// 准备训练数据\nconst xs = tf.tensor2d([1, 2, 3, 4], [4, 1]);\nconst ys = tf.tensor2d([1, 3, 5, 7], [4, 1]);\n\n// 训练模型\nasync function trainModel() {\n  await model.fit(xs, ys, {epochs: 10});\n}\n\ntrainModel();',1,'2025-08-23 14:24:31','2025-08-23 14:24:31'),(14,5,6,'C','A',1,'2025-08-23 14:24:43','2025-08-23 14:24:43');
/*!40000 ALTER TABLE `wrongquestion` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-08-30 16:50:43
