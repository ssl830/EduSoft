-- 内容服务数据库脚本
CREATE DATABASE IF NOT EXISTS `content-db` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `content-db`;

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET character_set_connection=utf8mb4;

-- 文件信息表 (完全匹配 FileInfo 实体类)
CREATE TABLE IF NOT EXISTS file_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    original_file_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_type VARCHAR(100) NOT NULL COMMENT '文件类型',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    uploader_id BIGINT NOT NULL COMMENT '上传用户ID',
    uploader_name VARCHAR(255) NOT NULL COMMENT '上传用户名',
    description TEXT COMMENT '文件描述',
    category VARCHAR(100) DEFAULT 'other' COMMENT '文件分类',
    visibility VARCHAR(50) DEFAULT 'CLASS_ONLY' COMMENT '可见性',
    object_name VARCHAR(255) COMMENT '对象名称',
    file_url VARCHAR(500) COMMENT '文件URL',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_uploader_id (uploader_id),
    INDEX idx_file_type (file_type),
    INDEX idx_category (category),
    INDEX idx_visibility (visibility),
    INDEX idx_status (status)
) COMMENT '文件信息表';

-- 教学资源表 (完全匹配 TeachingResource 实体类)
CREATE TABLE IF NOT EXISTS teaching_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '资源标题',
    description TEXT COMMENT '资源描述',
    content LONGTEXT COMMENT '资源内容',
    course_id BIGINT COMMENT '关联课程ID',
    chapter_id BIGINT COMMENT '关联章节ID',
    chapter_name VARCHAR(255) COMMENT '章节名称',
    resource_type VARCHAR(50) NOT NULL COMMENT '资源类型',
    file_url VARCHAR(500) COMMENT '文件URL',
    object_name VARCHAR(255) COMMENT '对象名称',
    duration INT DEFAULT 0 COMMENT '时长(秒)',
    author_id BIGINT NOT NULL COMMENT '作者ID',
    author_name VARCHAR(255) NOT NULL COMMENT '作者姓名',
    tags VARCHAR(500) COMMENT '标签',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    status VARCHAR(20) DEFAULT 'published' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_author_id (author_id),
    INDEX idx_course_id (course_id),
    INDEX idx_chapter_id (chapter_id),
    INDEX idx_resource_type (resource_type),
    INDEX idx_status (status)
) COMMENT '教学资源表';

-- 通知表 (完全匹配 Notification 实体类)
CREATE TABLE IF NOT EXISTS notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    message TEXT NOT NULL COMMENT '通知消息',
    type VARCHAR(50) NOT NULL COMMENT '通知类型',
    related_id BIGINT COMMENT '关联业务ID',
    related_type VARCHAR(50) COMMENT '关联业务类型',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    read_time DATETIME COMMENT '阅读时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_type (type),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) COMMENT '通知表';

-- 讨论话题表 (完全匹配 Discussion 实体类)
CREATE TABLE IF NOT EXISTS discussion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL COMMENT '话题标题',
    content TEXT NOT NULL COMMENT '话题内容',
    creator_id BIGINT NOT NULL COMMENT '创建者ID',
    creator_name VARCHAR(255) NOT NULL COMMENT '创建者姓名',
    course_id BIGINT COMMENT '关联课程ID',
    class_id BIGINT COMMENT '关联班级ID',
    type VARCHAR(50) DEFAULT 'general' COMMENT '讨论类型',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    reply_count INT DEFAULT 0 COMMENT '回复次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_creator_id (creator_id),
    INDEX idx_course_id (course_id),
    INDEX idx_class_id (class_id),
    INDEX idx_type (type),
    INDEX idx_status (status)
) COMMENT '讨论话题表';

-- 讨论回复表 (完全匹配 DiscussionReply 实体类)
CREATE TABLE IF NOT EXISTS discussion_reply (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    discussion_id BIGINT NOT NULL COMMENT '话题ID',
    content TEXT NOT NULL COMMENT '回复内容',
    replier_id BIGINT NOT NULL COMMENT '回复者ID',
    replier_name VARCHAR(255) NOT NULL COMMENT '回复者姓名',
    parent_id BIGINT COMMENT '父回复ID(用于嵌套回复)',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_discussion_id (discussion_id),
    INDEX idx_replier_id (replier_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_status (status)
) COMMENT '讨论回复表';

-- 学习进度表 (完全匹配 LearningProgress 实体类)
CREATE TABLE IF NOT EXISTS learning_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    student_id BIGINT NOT NULL COMMENT '学生ID',
    progress DOUBLE DEFAULT 0.0 COMMENT '学习进度(0-100)',
    last_position INT DEFAULT 0 COMMENT '最后观看位置(秒)',
    watch_count INT DEFAULT 0 COMMENT '观看次数',
    last_watch_time DATETIME COMMENT '最后观看时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_resource_student (resource_id, student_id),
    INDEX idx_resource_id (resource_id),
    INDEX idx_student_id (student_id)
) COMMENT '学习进度表';

-- 插入示例数据 (使用英文避免编码问题)
INSERT INTO file_info (file_name, original_file_name, file_path, file_size, file_type, uploader_id, uploader_name, description, category, visibility, object_name, file_url, status) VALUES
('sample_doc.pdf', 'sample_doc.pdf', '/uploads/documents/sample_doc.pdf', 1024000, 'PDF', 1, 'Test User', 'Sample Document', 'document', 'PUBLIC', 'sample_doc.pdf', '/uploads/documents/sample_doc.pdf', 'active'),
('sample_video.mp4', 'sample_video.mp4', '/uploads/videos/sample_video.mp4', 52428800, 'VIDEO', 1, 'Test User', 'Sample Video', 'video', 'CLASS_ONLY', 'sample_video.mp4', '/uploads/videos/sample_video.mp4', 'active');

INSERT INTO teaching_resource (title, description, course_id, chapter_id, chapter_name, resource_type, file_url, object_name, duration, author_id, author_name, tags, status) VALUES
('Java Programming Basics', 'Java programming basic knowledge document', 1, 1, 'Chapter 1', 'document', '/uploads/documents/java_basic.pdf', 'java_basic.pdf', 0, 1, 'Test Teacher', 'Java,Programming,Basic', 'published'),
('Spring Boot Tutorial', 'Spring Boot framework tutorial video', 1, 2, 'Chapter 2', 'video', '/uploads/videos/spring_boot_tutorial.mp4', 'spring_boot_tutorial.mp4', 3600, 1, 'Test Teacher', 'Spring Boot,Framework,Tutorial', 'published');

INSERT INTO notification (user_id, message, type, related_id, related_type) VALUES
(1, 'System maintenance notice: System will be maintained from 22:00-24:00 tonight.', 'system', NULL, NULL),
(1, 'New homework for Java Programming Basics course has been published.', 'homework', 1, 'course');

INSERT INTO discussion (title, content, creator_id, creator_name, course_id, class_id, type) VALUES
('Java Learning Experience', 'Share Java learning experience, welcome to discuss.', 1, 'Test Student', 1, 1, 'general'),
('Spring Boot Question', 'Encountered some problems when using Spring Boot, hope to get help.', 2, 'Test Student 2', 1, 1, 'question');

INSERT INTO discussion_reply (discussion_id, content, replier_id, replier_name) VALUES
(1, 'Great sharing! Thank you for the experience.', 2, 'Test Student 2'),
(1, 'I learned a lot from this discussion.', 3, 'Test Student 3');

INSERT INTO learning_progress (resource_id, student_id, progress, last_position, watch_count) VALUES
(1, 1, 50.0, 1800, 1),
(1, 2, 75.0, 2700, 2);
