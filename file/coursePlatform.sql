-- 一、创建数据库
DROP DATABASE IF EXISTS CoursePlatform;
CREATE DATABASE CoursePlatform DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE CoursePlatform;

-- 二、用户表
CREATE TABLE User (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(15) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('student', 'teacher', 'tutor') NOT NULL,
    email VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 三、课程与章节
CREATE TABLE Course (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    teacher_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(20) UNIQUE NOT NULL,   -- 课程代码（类似于ISBN）
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
    class_code VARCHAR(20) NOT NULL UNIQUE, -- 班级暗号，学生自己加入班级
    FOREIGN KEY (course_id) REFERENCES Course(id)
);

CREATE TABLE ClassUser (
    class_id BIGINT,
    user_id BIGINT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (class_id, user_id),
    FOREIGN KEY (class_id) REFERENCES Class(id),
    FOREIGN KEY (user_id) REFERENCES User(id)
);

CREATE TABLE CourseClass (
    course_id BIGINT,
    class_id BIGINT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (class_id, course_id),
    FOREIGN KEY (class_id) REFERENCES Class(id),
    FOREIGN KEY (course_id) REFERENCES Course(id)
);

-- 六、题库与练习
CREATE TABLE Question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    creator_id BIGINT NOT NULL,
    type ENUM('singlechoice', 'program', 'fillblank') NOT NULL,
    content TEXT NOT NULL,
    analysis TEXT,
    options TEXT,
    answer TEXT,
    course_id BIGINT,            -- 新增：关联课程
    section_id BIGINT,           -- 新增：关联章节
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (creator_id) REFERENCES User(id),
    FOREIGN KEY (course_id) REFERENCES Course(id),
    FOREIGN KEY (section_id) REFERENCES CourseSection(id)
);

CREATE TABLE Practice (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    course_id BIGINT NOT NULL,
    class_id BIGINT NOT NULL,
    title VARCHAR(200) NOT NULL,
    start_time DATETIME,
    end_time DATETIME,
    allow_multiple_submission BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES Course(id),
    FOREIGN KEY (created_by) REFERENCES User(id),
    FOREIGN KEY (class_id) REFERENCES Class(id)
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
    is_judged INT DEFAULT 0, 
    feedback TEXT,
    FOREIGN KEY (practice_id) REFERENCES Practice(id),
    FOREIGN KEY (student_id) REFERENCES User(id)
);

CREATE TABLE Answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    submission_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    answer_text TEXT,
    is_judged BOOLEAN DEFAULT FALSE,
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

-- 八、通知
CREATE TABLE Notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(200),
    message TEXT,
    read_flag BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES User(id)
);

-- 九、文件管理
CREATE TABLE file_node (
    id BIGINT PRIMARY KEY AUTO_INCREMENT, 
    file_name VARCHAR(255) NOT NULL,             -- 节点名称
    is_dir BOOLEAN NOT NULL DEFAULT FALSE,  -- 是否为文件夹
    parent_id BIGINT NULL,                  -- 父节点ID（根节点为NULL）
    course_id BIGINT NOT NULL,              -- 所属课程
    class_id BIGINT NULL,                   -- 所属班级（非必须，用于权限控制）
    uploader_id BIGINT NOT NULL,            -- 上传者
    sectiondir_id BIGINT default -1,            -- 是班级根文件夹中的章节文件夹时，这个字段等于它内部的文件的section_id
    file_type ENUM('VIDEO', 'PPT', 'CODE', 'PDF', 'OTHER') NOT NULL,
    section_id BIGINT default -1,     -- 所属章节，章节文件夹和所有的课程班级根文件夹中的文件的section_id都为-1
    last_file_version BIGINT NOT NULL DEFAULT 0,   -- 指向该文件的上一个版本的id，如果没有上一个版本默认为0
    is_current_version BOOLEAN NOT NULL DEFAULT TRUE, -- 是否是一个文件的最新版本，没有多个版本时默认为true
    file_size BIGINT NOT NULL,
    visibility ENUM('PUBLIC', 'PRIVATE', 'CLASS_ONLY') NOT NULL DEFAULT 'CLASS_ONLY',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    file_url VARCHAR(1024),      -- 文件存储路径，在云对象库中 
    file_version int,     -- 文件版本号           
    object_name VARCHAR(255),  -- 对象存储中的文件路径到文件名，如 /course/1/section/1/file.txt

    FOREIGN KEY (parent_id) REFERENCES file_node(id),
    FOREIGN KEY (course_id) REFERENCES Course(id),
    FOREIGN KEY (class_id) REFERENCES Class(id),
    FOREIGN KEY (uploader_id) REFERENCES User(id)
);

CREATE INDEX idx_parent_id ON file_node (parent_id);  -- 创建索引

-- 十、导入记录
CREATE TABLE import_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    class_id BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    file_name VARCHAR(255),
    total_count INT NOT NULL,
    success_count INT NOT NULL,
    fail_count INT NOT NULL,
    fail_reason TEXT,
    import_time DATETIME NOT NULL,
    import_type VARCHAR(20) NOT NULL,
    FOREIGN KEY (class_id) REFERENCES Class(id),
    FOREIGN KEY (operator_id) REFERENCES User(id)
);

-- 十一、作业
-- Create Homework table
CREATE TABLE IF NOT EXISTS homework (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    class_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    attachment_url VARCHAR(255),
    object_name VARCHAR(255),
    deadline DATETIME NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create HomeworkSubmission table
CREATE TABLE IF NOT EXISTShomework_submission (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    homework_id BIGINT NOT NULL,
    student_id BIGINT NOT NULL,
    submission_type VARCHAR(50) NOT NULL,
    file_url VARCHAR(255),
    object_name VARCHAR(255),
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (homework_id) REFERENCES homework(id)
); 

-- 十二、教学资源
-- Create teaching resource table
CREATE TABLE IF NOT EXISTS teaching_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,           -- 资源标题
    description TEXT,                      -- 资源描述
    course_id BIGINT NOT NULL,             -- 所属课程ID
    chapter_id BIGINT NOT NULL,            -- 所属章节ID
    chapter_name VARCHAR(255) NOT NULL,     -- 章节名称
    resource_type VARCHAR(50) NOT NULL,    -- 资源类型（VIDEO/DOCUMENT等）
    file_url VARCHAR(255) NOT NULL,        -- 文件URL
    object_name VARCHAR(255) NOT NULL,     -- 对象存储中的文件名
    duration INTEGER,                      -- 视频时长（秒）
    created_by BIGINT NOT NULL,            -- 创建者ID
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create learning progress table
CREATE TABLE IF NOT EXISTS learning_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id BIGINT NOT NULL,           -- 教学资源ID
    student_id BIGINT NOT NULL,            -- 学生ID
    progress INTEGER NOT NULL,             -- 学习进度（秒）
    last_position INTEGER NOT NULL,        -- 最后观看位置（秒）
    watch_count INTEGER DEFAULT 0,         -- 观看次数
    last_watch_time DATETIME,              -- 最后观看时间
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_resource_student (resource_id, student_id),  -- 资源和学生的唯一约束
    FOREIGN KEY (resource_id) REFERENCES teaching_resource(id) ON DELETE CASCADE
); 



-- 1. 用户表 User
INSERT INTO User (user_id, username, password_hash, role, email) VALUES
('stu001', 'Alice',   'hash_pw_1', 'student', 'alice@example.com'),
('stu002', 'Bob',     'hash_pw_2', 'student', 'bob@example.com'),
('tea001', 'Prof.Chen', 'hash_pw_3','teacher','chen@example.edu'),
('tut001', 'TutorLi', 'hash_pw_4','tutor',  'li@example.com');

-- 2. 课程与章节
INSERT INTO Course (teacher_id, name, code, outline, objective, assessment) VALUES
(3, '操作系统原理',     'OS101', '操作系统基础...', '掌握进程与内存管理', '平时测验+实验+考试'),
(3, '数据结构与算法', 'DS102', '线性结构与树...', '熟练使用常见数据结构','平时作业+考试');

INSERT INTO CourseSection (course_id, title, sort_order) VALUES
(1, '第1章 操作系统概述', 1),
(1, '第2章 进程管理',     2),
(2, '第1章 线性结构',     1),
(2, '第2章 栈与队列',     2);

-- 3. 班级与成员
INSERT INTO Class (course_id, name, class_code) VALUES
(1, '2025级01班', 'OS01'),
(2, '2025级02班', 'DS02');

INSERT INTO ClassUser (class_id, user_id) VALUES
(1, 1),
(1, 2),
(2, 2);

INSERT INTO CourseClass (course_id, class_id) VALUES
(1, 1),
(2, 2);

-- 4. 题库与练习
INSERT INTO Question (creator_id, type, content, analysis, options, answer, course_id, section_id) VALUES
(3, 'singlechoice',
 '下面哪个属于操作系统的功能？',
 '进程管理、内存管理、文件管理都是操作系统功能。',
 '["进程管理","数据传输","电路设计","编译优化"]',
 '进程管理',
 1, 1),

(3, 'program',
 '请写一个函数，反转链表。',
 '可采用三指针法遍历原链表，逐个翻转指针。',
 NULL, NULL, 1, 2),

(3, 'fillblank',
 '在 Linux 中，用于查看当前目录下所有文件（包括隐藏文件）的命令是 ______。',
 '使用 ls -a 来显示所有文件。',
 NULL, 'ls -a', 1, 1);

INSERT INTO Practice (course_id, class_id, title, start_time, end_time, allow_multiple_submission, created_by) VALUES
(1, 1, '第1章在线练习', '2025-05-20 08:00:00', '2025-05-27 23:59:59', TRUE, 3);

-- 假设 Practice.id = 1
INSERT INTO PracticeQuestion (practice_id, question_id, score) VALUES
(1, 1, 10),
(1, 2, 20),
(1, 3, 5);

-- 5. 学生提交与答案
INSERT INTO Submission (practice_id, student_id, score, is_judged, feedback) VALUES
(1, 1, 0, 0, NULL),
(1, 2, 0, 0, NULL);

-- 假设 Submission.id = 1, 2
INSERT INTO Ans
