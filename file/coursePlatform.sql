-- 一、创建数据库
DROP DATABASE IF EXISTS CoursePlatform;
CREATE DATABASE CoursePlatform DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE CoursePlatform;

-- 二、用户表
CREATE TABLE User (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('student', 'teacher', 'ta') NOT NULL,
    name VARCHAR(100),
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 三、课程与章节
CREATE TABLE Course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,
    outline TEXT,
    objective TEXT,
    assessment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (teacher_id) REFERENCES User(id)
);

CREATE TABLE CourseSection (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    sort_order INT DEFAULT 0,
    FOREIGN KEY (course_id) REFERENCES Course(id)
);

-- 四、班级与成员
CREATE TABLE Class (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    class_code VARCHAR(20) NOT NULL UNIQUE,
    FOREIGN KEY (course_id) REFERENCES Course(id)
);

CREATE TABLE ClassStudent (
    class_id BIGINT,
    student_id BIGINT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (class_id, student_id),
    FOREIGN KEY (class_id) REFERENCES Class(id),
    FOREIGN KEY (student_id) REFERENCES User(id)
);

-- 五、教学资源及版本
CREATE TABLE Resource (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    section_id BIGINT,
    uploader_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    type ENUM('PPT', 'PDF', 'VIDEO', 'CODE', 'OTHER') NOT NULL,
    file_url TEXT NOT NULL,
    visibility ENUM('PUBLIC', 'PRIVATE', 'CLASS_ONLY') DEFAULT 'PUBLIC',
    version INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES Course(id),
    FOREIGN KEY (section_id) REFERENCES CourseSection(id),
    FOREIGN KEY (uploader_id) REFERENCES User(id)
);

CREATE TABLE ResourceVersion (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    resource_id BIGINT NOT NULL,
    version INT NOT NULL,
    file_url TEXT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (resource_id) REFERENCES Resource(id)
);

-- 六、题库与练习
CREATE TABLE Question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    creator_id BIGINT NOT NULL,
    type ENUM('choice', 'program') NOT NULL,
    content TEXT NOT NULL,
    options JSON,
    answer TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES User(id)
);

CREATE TABLE Practice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    allow_multiple_submission BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES Course(id),
    FOREIGN KEY (created_by) REFERENCES User(id)
);

CREATE TABLE PracticeQuestion (
    practice_id BIGINT,
    question_id BIGINT,
    score INT NOT NULL,
    PRIMARY KEY (practice_id, question_id),
    FOREIGN KEY (practice_id) REFERENCES Practice(id),
    FOREIGN KEY (question_id) REFERENCES Question(id)
);

CREATE TABLE Submission (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    practice_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    submitted_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    score INT DEFAULT 0,
    feedback TEXT,
    FOREIGN KEY (practice_id) REFERENCES Practice(id),
    FOREIGN KEY (student_id) REFERENCES User(id)
);

CREATE TABLE Answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    submission_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_text TEXT,
    correct BOOLEAN,
    score INT,
    FOREIGN KEY (submission_id) REFERENCES Submission(id),
    FOREIGN KEY (question_id) REFERENCES Question(id)
);

-- 七、学习记录
CREATE TABLE Progress (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    section_id BIGINT,
    completed BOOLEAN DEFAULT FALSE,
    completed_at TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES User(id),
    FOREIGN KEY (course_id) REFERENCES Course(id),
    FOREIGN KEY (section_id) REFERENCES CourseSection(id)
);

CREATE TABLE FavoriteQuestion (
    student_id BIGINT,
    question_id BIGINT,
    PRIMARY KEY (student_id, question_id),
    FOREIGN KEY (student_id) REFERENCES User(id),
    FOREIGN KEY (question_id) REFERENCES Question(id)
);

-- 八、通知（可选扩展）
CREATE TABLE Notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    message TEXT,
    read_flag BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- 九、插入测试数据
-- 用户
INSERT INTO User (username, password_hash, role, name, email) VALUES
('teacher01', 'hashed_pwd_123', 'teacher', '张老师', 'zhang@example.com'),
('student01', 'hashed_pwd_456', 'student', '李学生', 'li@example.com');

-- 课程
INSERT INTO Course (teacher_id, name, code, outline, objective, assessment) VALUES
(1, '软件工程基础', 'SE101', '介绍软件开发流程', '掌握软件工程基础知识', '作业+项目+考试');

-- 章节
INSERT INTO CourseSection (course_id, title, sort_order) VALUES
(1, '第一章：软件工程导论', 1),
(1, '第二章：需求分析', 2);

-- 班级
INSERT INTO Class (course_id, name, class_code) VALUES
(1, '软工A班', 'CLASS123');

-- 学生加入班级
INSERT INTO ClassStudent (class_id, student_id) VALUES
(1, 2);

-- 教学资源
INSERT INTO Resource (course_id, section_id, uploader_id, title, type, file_url, visibility) VALUES
(1, 1, 1, '第一章课件', 'PPT', '/uploads/ch1.pptx', 'CLASS_ONLY');

-- 题目
INSERT INTO Question (creator_id, type, content, options, answer) VALUES
(1, 'choice', '软件工程的第一步是什么？', '["需求分析","编码","测试","部署"]', '需求分析');

-- 练习任务
INSERT INTO Practice (course_id, title, start_time, end_time, allow_multiple_submission, created_by) VALUES
(1, '第一章练习', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), TRUE, 1);

-- 添加题目到练习
INSERT INTO PracticeQuestion (practice_id, question_id, score) VALUES
(1, 1, 5);

-- 学生提交
INSERT INTO Submission (practice_id, student_id, score, feedback) VALUES
(1, 2, 5, '回答正确');

-- 学生作答
INSERT INTO Answer (submission_id, question_id, answer_text, correct, score) VALUES
(1, 1, '需求分析', TRUE, 5);

-- 学习进度
INSERT INTO Progress (student_id, course_id, section_id, completed, completed_at) VALUES
(2, 1, 1, TRUE, NOW());

-- 收藏题目
INSERT INTO FavoriteQuestion (student_id, question_id) VALUES
(2, 1);

-- 通知
INSERT INTO Notification (user_id, title, message) VALUES
(2, '练习反馈已出', '第一章练习已批改，请查看得分');


-- 十、文件信息表
CREATE TABLE file_info (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    file_type ENUM('AUDIO_VIDEO', 'IMAGE', 'TEXT', 'PDF', 'OTHER') NOT NULL,
    file_size BIGINT NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    course_id BIGINT NOT NULL,
    section_id BIGINT,
    class_id BIGINT,
    file_version INT NOT NULL DEFAULT 1,
    uploader_id BIGINT NOT NULL,
    visibility ENUM('PUBLIC', 'PRIVATE', 'CLASS_ONLY') NOT NULL DEFAULT 'CLASS_ONLY',
    file_location ENUM('COURSE_FILE', 'SECTION_FILE', 'PRACTICE_FILE', 'OTHER') NOT NULL,
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (course_id) REFERENCES Course(id),
    FOREIGN KEY (section_id) REFERENCES CourseSection(id),
    FOREIGN KEY (class_id) REFERENCES Class(id),
    FOREIGN KEY (uploader_id) REFERENCES User(id)
);

