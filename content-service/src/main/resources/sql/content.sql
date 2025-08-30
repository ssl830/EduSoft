-- 创建内容服务数据库
CREATE DATABASE IF NOT EXISTS `content-db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `content-db`;

-- 文件信息表 (基于现有的file_node表结构)
CREATE TABLE IF NOT EXISTS file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    original_file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type ENUM('VIDEO','PPT','CODE','PDF','OTHER') NOT NULL COMMENT '文件类型',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    uploader_name VARCHAR(100) NOT NULL COMMENT '上传者姓名',
    description TEXT COMMENT '文件描述',
    category VARCHAR(50) DEFAULT 'other' COMMENT '文件分类',
    visibility ENUM('PUBLIC','PRIVATE','CLASS_ONLY') DEFAULT 'CLASS_ONLY' COMMENT '可见性',
    object_name VARCHAR(255) DEFAULT NULL COMMENT '对象存储名称',
    file_url VARCHAR(1024) DEFAULT NULL COMMENT '文件URL',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_uploader (uploader_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_visibility (visibility)
) COMMENT '文件信息表';

-- 教学资源表 (基于现有的teaching_resource表结构)
CREATE TABLE IF NOT EXISTS teaching_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '资源标题',
    description TEXT COMMENT '资源描述',
    content LONGTEXT COMMENT '资源内容',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    chapter_id BIGINT NOT NULL COMMENT '章节ID',
    chapter_name VARCHAR(255) NOT NULL COMMENT '章节名称',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型',
    file_url VARCHAR(255) NOT NULL COMMENT '文件URL',
    object_name VARCHAR(255) NOT NULL COMMENT '对象存储名称',
    duration INT DEFAULT NULL COMMENT '时长(秒)',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    author_name VARCHAR(100) NOT NULL COMMENT '作者姓名',
    tags VARCHAR(500) COMMENT '标签',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    status VARCHAR(20) DEFAULT 'published' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course (course_id),
    INDEX idx_chapter (chapter_id),
    INDEX idx_author (author_id),
    INDEX idx_type (resource_type),
    INDEX idx_status (status)
) COMMENT '教学资源表';

-- 通知表 (基于现有的notification表结构)
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) DEFAULT NULL COMMENT '通知标题',
    message TEXT COMMENT '通知内容',
    type VARCHAR(20) NOT NULL COMMENT '通知类型',
    read_flag TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    related_id BIGINT DEFAULT NULL COMMENT '关联ID',
    related_type VARCHAR(50) DEFAULT NULL COMMENT '关联类型',
    priority VARCHAR(20) DEFAULT 'normal' COMMENT '优先级',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_user (user_id),
    INDEX idx_type (type),
    INDEX idx_read_flag (read_flag),
    INDEX idx_related (related_type, related_id),
    INDEX idx_status (status)
) COMMENT '通知表';

-- 讨论表 (基于现有的discussion表结构)
CREATE TABLE IF NOT EXISTS discussion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_id BIGINT NOT NULL COMMENT '课程ID',
    class_id BIGINT NOT NULL COMMENT '班级ID',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    creator_num VARCHAR(15) NOT NULL COMMENT '创建者学号/工号',
    title VARCHAR(200) NOT NULL COMMENT '讨论标题',
    content TEXT NOT NULL COMMENT '讨论内容',
    category VARCHAR(50) DEFAULT 'general' COMMENT '分类',
    is_pinned TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    is_closed TINYINT(1) DEFAULT 0 COMMENT '是否关闭',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    reply_count INT DEFAULT 0 COMMENT '回复次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course (course_id),
    INDEX idx_class (class_id),
    INDEX idx_creator (creator_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_pinned (is_pinned)
) COMMENT '讨论表';

-- 讨论回复表 (基于现有的discussionreply表结构)
CREATE TABLE IF NOT EXISTS discussion_reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    discussion_id BIGINT NOT NULL COMMENT '讨论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    user_num VARCHAR(15) NOT NULL COMMENT '用户学号/工号',
    content TEXT NOT NULL COMMENT '回复内容',
    parent_reply_id BIGINT DEFAULT NULL COMMENT '父回复ID',
    is_teacher_reply TINYINT(1) DEFAULT 0 COMMENT '是否教师回复',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_discussion (discussion_id),
    INDEX idx_user (user_id),
    INDEX idx_parent (parent_reply_id),
    INDEX idx_status (status),
    FOREIGN KEY (discussion_id) REFERENCES discussion(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_reply_id) REFERENCES discussion_reply(id) ON DELETE CASCADE
) COMMENT '讨论回复表';

-- 学习进度表 (基于现有的learning_progress表结构)
CREATE TABLE IF NOT EXISTS learning_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    progress DECIMAL(5,2) NOT NULL COMMENT '学习进度(秒)',
    last_position INT NOT NULL COMMENT '最后观看位置(秒)',
    watch_count INT DEFAULT 0 COMMENT '观看次数',
    last_watch_time DATETIME DEFAULT NULL COMMENT '最后观看时间',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_resource_student (resource_id, student_id),
    INDEX idx_student (student_id),
    INDEX idx_resource (resource_id),
    FOREIGN KEY (resource_id) REFERENCES teaching_resource(id) ON DELETE CASCADE
) COMMENT '学习进度表';

-- 文件节点表 (基于现有的file_node表结构，用于文件树管理)
CREATE TABLE IF NOT EXISTS file_node (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    is_dir TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否为目录',
    parent_id BIGINT DEFAULT NULL COMMENT '父节点ID',
    course_id BIGINT NOT NULL COMMENT '课程ID',
    class_id BIGINT DEFAULT NULL COMMENT '班级ID',
    uploader_id BIGINT NOT NULL COMMENT '上传者ID',
    sectiondir_id BIGINT DEFAULT -1 COMMENT '章节目录ID',
    file_type ENUM('VIDEO','PPT','CODE','PDF','OTHER') NOT NULL COMMENT '文件类型',
    section_id BIGINT DEFAULT -1 COMMENT '章节ID',
    last_file_version BIGINT NOT NULL DEFAULT 0 COMMENT '最后文件版本',
    is_current_version TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否为当前版本',
    file_size BIGINT NOT NULL COMMENT '文件大小',
    visibility ENUM('PUBLIC','PRIVATE','CLASS_ONLY') NOT NULL DEFAULT 'CLASS_ONLY' COMMENT '可见性',
    file_url VARCHAR(1024) DEFAULT NULL COMMENT '文件URL',
    file_version INT DEFAULT NULL COMMENT '文件版本',
    object_name VARCHAR(255) DEFAULT NULL COMMENT '对象存储名称',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_course (course_id),
    INDEX idx_class (class_id),
    INDEX idx_uploader (uploader_id),
    INDEX idx_parent (parent_id),
    INDEX idx_section (section_id),
    FOREIGN KEY (parent_id) REFERENCES file_node(id) ON DELETE CASCADE
) COMMENT '文件节点表';

-- 插入一些示例数据
INSERT INTO file_info (file_name, original_file_name, file_path, file_type, file_size, uploader_id, uploader_name, description, category, visibility) VALUES
('示例文档.pdf', '示例文档.pdf', '/uploads/documents/', 'PDF', 1024000, 1, '张老师', '这是一个示例文档', 'document', 'PUBLIC'),
('教学视频.mp4', '教学视频.mp4', '/uploads/videos/', 'VIDEO', 52428800, 1, '张老师', '这是一个教学视频', 'video', 'CLASS_ONLY');

INSERT INTO teaching_resource (title, description, course_id, chapter_id, chapter_name, resource_type, file_url, object_name, duration, author_id, author_name, tags, status) VALUES
('第一章教学视频', '数据库基础概念讲解', 1, 1, '第一章 数据库基础', 'VIDEO', '/resource/1.mp4', '/resource/1.mp4', 600, 1, '张老师', '数据库,基础', 'published'),
('TensorFlow.js入门', 'TensorFlow.js基础概念介绍', 2, 3, 'cp07-TensorFlow.js应用开发', 'VIDEO', '/resource/tfjs_intro.mp4', '/resource/tfjs_intro.mp4', 1200, 4, 'Teacher2', 'TensorFlow.js,JavaScript', 'published');

INSERT INTO notification (user_id, title, message, type, related_id, related_type) VALUES
(2, '新资源通知', '老师上传了新的教学资源', 'RESOURCE', 1, 'TEACHING_RESOURCE'),
(3, '讨论回复通知', '有人回复了你的讨论', 'DISCUSSION', 1, 'DISCUSSION_REPLY');

INSERT INTO discussion (course_id, class_id, creator_id, creator_num, title, content, category) VALUES
(1, 1, 4, 'T002', '数据库设计问题', '请问ER图设计有什么注意事项？', 'question'),
(2, 2, 5, 'S003', 'TensorFlow.js学习心得', '分享一下学习TensorFlow.js的心得体会', 'share');

INSERT INTO discussion_reply (discussion_id, user_id, user_num, content, is_teacher_reply) VALUES
(1, 4, 'T002', 'ER图设计需要注意实体间的关系和属性的完整性', 1),
(1, 5, 'S003', '谢谢老师的解答！', 0);

INSERT INTO learning_progress (resource_id, student_id, progress, last_position, watch_count) VALUES
(1, 2, 300.00, 300, 1),
(1, 3, 120.00, 120, 1);

-- 作业表
CREATE TABLE IF NOT EXISTS `homework` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '作业标题',
  `description` text COLLATE utf8mb4_unicode_ci COMMENT '作业描述',
  `course_id` bigint DEFAULT NULL COMMENT '课程ID',
  `chapter_id` bigint DEFAULT NULL COMMENT '章节ID',
  `chapter_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '章节名称',
  `class_id` bigint NOT NULL COMMENT '班级ID',
  `created_by` bigint NOT NULL COMMENT '创建者ID',
  `created_by_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建者姓名',
  `attachment_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '附件URL',
  `object_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对象存储路径',
  `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件名',
  `deadline` datetime NOT NULL COMMENT '截止时间',
  `status` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT 'published' COMMENT '状态',
  `submission_count` int DEFAULT 0 COMMENT '提交数量',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_class_id` (`class_id`),
  KEY `idx_course_id` (`course_id`),
  KEY `idx_created_by` (`created_by`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '作业表';

-- 作业提交表
CREATE TABLE IF NOT EXISTS `homeworksubmission` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `homework_id` bigint NOT NULL COMMENT '作业ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `student_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '学生姓名',
  `file_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件URL',
  `object_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '对象存储路径',
  `submitted_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
  PRIMARY KEY (`id`),
  KEY `idx_homework_id` (`homework_id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_homework_student` (`homework_id`, `student_id`),
  CONSTRAINT `homeworksubmission_ibfk_1` FOREIGN KEY (`homework_id`) REFERENCES `homework` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT '作业提交表';

-- 插入作业相关测试数据
INSERT INTO homework (title, description, course_id, chapter_id, chapter_name, class_id, created_by, created_by_name, attachment_url, object_name, file_name, deadline, status, submission_count) VALUES
('第一次作业 - ER图设计', '请完成ER图设计，要求包含至少3个实体，2个关系，每个实体至少3个属性', 1, 1, '第一章 数据库基础', 1, 1, '张老师', '/uploads/homework/hw1.pdf', 'homework/1/hw1.pdf', 'hw1.pdf', '2025-12-31 23:59:59', 'published', 2),
('TensorFlow.js实践作业', '使用TensorFlow.js实现简单的线性回归模型，并提交代码和运行结果截图', 2, 3, 'cp07-TensorFlow.js应用开发', 2, 4, 'Teacher2', '/uploads/homework/tfjs_hw.pdf', 'homework/2/tfjs_hw.pdf', 'tfjs_hw.pdf', '2025-12-25 23:59:59', 'published', 1),
('数据库查询练习', '完成以下SQL查询练习：1. 基本查询 2. 连接查询 3. 聚合查询', 1, 2, '第二章 SQL基础', 1, 1, '张老师', '/uploads/homework/sql_practice.pdf', 'homework/1/sql_practice.pdf', 'sql_practice.pdf', '2025-12-20 23:59:59', 'published', 0);

INSERT INTO homeworksubmission (homework_id, student_id, student_name, file_url, object_name, submitted_at) VALUES
(1, 2, '学生张三', '/uploads/submission/hw1_student2.pdf', 'homework/submission/1/2_hw1_student2.pdf', '2025-12-15 14:30:00'),
(1, 3, '学生李四', '/uploads/submission/hw1_student3.pdf', 'homework/submission/1/3_hw1_student3.pdf', '2025-12-16 16:45:00'),
(2, 5, '学生王五', '/uploads/submission/tfjs_hw_student5.zip', 'homework/submission/2/5_tfjs_hw_student5.zip', '2025-12-20 10:15:00');
