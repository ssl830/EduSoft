-- 内容服务数据库脚本
USE courseplatform;

-- 文件信息表
CREATE TABLE file_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL COMMENT '文件名',
    original_name VARCHAR(255) NOT NULL COMMENT '原始文件名',
    file_path VARCHAR(500) NOT NULL COMMENT '文件路径',
    file_size BIGINT NOT NULL COMMENT '文件大小(字节)',
    file_type VARCHAR(100) NOT NULL COMMENT '文件类型',
    mime_type VARCHAR(100) COMMENT 'MIME类型',
    file_hash VARCHAR(64) COMMENT '文件哈希值',
    upload_user_id VARCHAR(15) NOT NULL COMMENT '上传用户ID',
    upload_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    is_deleted TINYINT(1) DEFAULT 0 COMMENT '是否删除',
    delete_time DATETIME COMMENT '删除时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_file_type (file_type),
    INDEX idx_upload_time (upload_time)
) COMMENT '文件信息表';

-- 教学资源表
CREATE TABLE teaching_resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_name VARCHAR(255) NOT NULL COMMENT '资源名称',
    resource_type ENUM('video', 'document', 'image', 'audio', 'other') NOT NULL COMMENT '资源类型',
    resource_url VARCHAR(500) COMMENT '资源URL',
    file_id BIGINT COMMENT '关联文件ID',
    course_id BIGINT COMMENT '关联课程ID',
    description TEXT COMMENT '资源描述',
    tags VARCHAR(500) COMMENT '标签(JSON格式)',
    upload_user_id VARCHAR(15) NOT NULL COMMENT '上传用户ID',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    download_count INT DEFAULT 0 COMMENT '下载次数',
    is_public TINYINT(1) DEFAULT 1 COMMENT '是否公开',
    status ENUM('active', 'inactive', 'pending') DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (file_id) REFERENCES file_info(id) ON DELETE SET NULL,
    INDEX idx_course_id (course_id),
    INDEX idx_resource_type (resource_type),
    INDEX idx_upload_user (upload_user_id),
    INDEX idx_status (status)
) COMMENT '教学资源表';

-- 通知表
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    notification_type ENUM('system', 'course', 'homework', 'discussion', 'other') NOT NULL COMMENT '通知类型',
    sender_id VARCHAR(15) NOT NULL COMMENT '发送者ID',
    receiver_id VARCHAR(15) COMMENT '接收者ID(为空表示全体)',
    course_id BIGINT COMMENT '关联课程ID',
    related_id BIGINT COMMENT '关联业务ID',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    read_time DATETIME COMMENT '阅读时间',
    priority ENUM('low', 'normal', 'high', 'urgent') DEFAULT 'normal' COMMENT '优先级',
    status ENUM('sent', 'delivered', 'failed') DEFAULT 'sent' COMMENT '发送状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_receiver_id (receiver_id),
    INDEX idx_notification_type (notification_type),
    INDEX idx_course_id (course_id),
    INDEX idx_is_read (is_read),
    INDEX idx_created_at (created_at)
) COMMENT '通知表';

-- 讨论话题表
CREATE TABLE discussion (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL COMMENT '话题标题',
    content TEXT NOT NULL COMMENT '话题内容',
    author_id VARCHAR(15) NOT NULL COMMENT '作者ID',
    course_id BIGINT COMMENT '关联课程ID',
    category VARCHAR(50) COMMENT '话题分类',
    tags VARCHAR(500) COMMENT '标签(JSON格式)',
    view_count INT DEFAULT 0 COMMENT '查看次数',
    reply_count INT DEFAULT 0 COMMENT '回复次数',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    is_essence TINYINT(1) DEFAULT 0 COMMENT '是否精华',
    status ENUM('active', 'closed', 'deleted') DEFAULT 'active' COMMENT '状态',
    last_reply_time DATETIME COMMENT '最后回复时间',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_author_id (author_id),
    INDEX idx_course_id (course_id),
    INDEX idx_category (category),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) COMMENT '讨论话题表';

-- 讨论回复表
CREATE TABLE discussion_reply (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    discussion_id BIGINT NOT NULL COMMENT '话题ID',
    content TEXT NOT NULL COMMENT '回复内容',
    author_id VARCHAR(15) NOT NULL COMMENT '作者ID',
    parent_id BIGINT COMMENT '父回复ID(用于嵌套回复)',
    like_count INT DEFAULT 0 COMMENT '点赞次数',
    is_essence TINYINT(1) DEFAULT 0 COMMENT '是否精华回复',
    status ENUM('active', 'deleted') DEFAULT 'active' COMMENT '状态',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (discussion_id) REFERENCES discussion(id) ON DELETE CASCADE,
    FOREIGN KEY (parent_id) REFERENCES discussion_reply(id) ON DELETE CASCADE,
    INDEX idx_discussion_id (discussion_id),
    INDEX idx_author_id (author_id),
    INDEX idx_parent_id (parent_id),
    INDEX idx_created_at (created_at)
) COMMENT '讨论回复表';

-- 讨论点赞表
CREATE TABLE discussion_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(15) NOT NULL COMMENT '用户ID',
    target_type ENUM('discussion', 'reply') NOT NULL COMMENT '目标类型',
    target_id BIGINT NOT NULL COMMENT '目标ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_target (user_id, target_type, target_id),
    INDEX idx_target (target_type, target_id),
    INDEX idx_user_id (user_id)
) COMMENT '讨论点赞表';

-- 插入示例数据
INSERT INTO file_info (file_name, original_name, file_path, file_size, file_type, mime_type, upload_user_id) VALUES
('sample_doc.pdf', 'sample_doc.pdf', '/files/documents/sample_doc.pdf', 1024000, 'document', 'application/pdf', 'T001'),
('sample_video.mp4', 'sample_video.mp4', '/files/videos/sample_video.mp4', 52428800, 'video', 'video/mp4', 'T001');

INSERT INTO teaching_resource (resource_name, resource_type, resource_url, file_id, course_id, description, upload_user_id) VALUES
('Java编程基础', 'document', NULL, 1, 1, 'Java编程基础知识文档', 'T001'),
('Spring Boot教程', 'video', 'https://example.com/spring-boot-tutorial.mp4', NULL, 1, 'Spring Boot框架教程视频', 'T001');

INSERT INTO notification (title, content, notification_type, sender_id, receiver_id, course_id) VALUES
('系统维护通知', '系统将于今晚22:00-24:00进行维护，期间可能无法正常访问。', 'system', 'admin', NULL, NULL),
('新作业发布', '《Java编程基础》课程新作业已发布，请及时完成。', 'homework', 'T001', NULL, 1);

INSERT INTO discussion (title, content, author_id, course_id, category) VALUES
('Java学习心得分享', '分享一下学习Java的心得体会，欢迎大家交流讨论。', 'S001', 1, '学习交流'),
('Spring Boot问题求助', '在使用Spring Boot时遇到了一些问题，希望得到大家的帮助。', 'S002', 1, '问题求助');
