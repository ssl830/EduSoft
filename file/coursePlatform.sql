-- 一、创建数据库
DROP DATABASE IF EXISTS CoursePlatform;
CREATE DATABASE CoursePlatform DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE CoursePlatform;

-- 二、用户表
CREATE TABLE User (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id VARCHAR(15) NOT NULL UNIQUE,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('student', 'teacher', 'tutor') NOT NULL,
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
    code VARCHAR(20) UNIQUE NOT NULL,   --  课程暗号
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

CREATE TABLE ClassUser (
    class_id BIGINT,
    user_id BIGINT,
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (class_id, user_id),
    FOREIGN KEY (class_id) REFERENCES Class(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
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

-- 1. 用户表 User
INSERT INTO User (user_id, username, password_hash, role, name, email)
VALUES 
('U001', 'teacher_zhang', 'hash123456', 'teacher', '张老师', 'zhang@example.com'),
('U002', 'student_li', 'hash789012', 'student', '李同学', 'li@example.com'),
('U003', 'student_wang', 'hash789013', 'student', '王同学', 'wang@example.com'),
('U004', 'tutor_liu', 'hash789014', 'tutor', '刘助教', 'liu@example.com'),
('U005', 'student_zhou', 'hash789015', 'student', '周同学', 'zhou@example.com');

-- 2. 课程表 Course
INSERT INTO Course (teacher_id, name, code, outline, objective, assessment)
VALUES 
(1, '软件工程基础', 'SE101', '介绍软件开发流程', '掌握基础知识', '作业+项目+考试'),
(1, '计算机网络', 'NET201', '介绍网络协议和架构', '理解OSI模型和TCP/IP协议栈', '期中+期末');

-- 3. 章节表 CourseSection
-- 课程 ID = 1 的章节
INSERT INTO CourseSection (course_id, title, sort_order)
VALUES 
(1, '第一章：软件工程导论', 1),
(1, '第二章：需求分析', 2),
(1, '第三章：设计模式', 3);

-- 课程 ID = 2 的章节
INSERT INTO CourseSection (course_id, title, sort_order)
VALUES 
(2, '第一章：网络基础', 1),
(2, '第二章：传输层', 2),
(2, '第三章：应用层', 3);

-- 4. 班级表 Class
-- 课程 ID = 1 的班级
INSERT INTO Class (course_id, name, class_code)
VALUES 
(1, '软工A班', 'CLASS_A_101'),
(1, '软工B班', 'CLASS_B_101');

-- 课程 ID = 2 的班级
INSERT INTO Class (course_id, name, class_code)
VALUES 
(2, '网络A班', 'CLASS_NET_A'),
(2, '网络B班', 'CLASS_NET_B');

-- 5. 班级成员 ClassUser + CourseClass
-- A班
INSERT INTO ClassUser (class_id, user_id) VALUES
(1, 2), (1, 3), (1, 5);
INSERT INTO CourseClass (course_id, class_id) VALUES
(1, 1);

-- B班
INSERT INTO ClassUser (class_id, user_id) VALUES
(2, 2), (2, 3);
INSERT INTO CourseClass (course_id, class_id) VALUES
(1, 2);

-- 网络课班级
INSERT INTO ClassUser (class_id, user_id) VALUES
(3, 2), (3, 5);
INSERT INTO CourseClass (course_id, class_id) VALUES
(2, 3);

-- 6. 文件表 file_node
-- 课程1的软工A班根目录文件
INSERT INTO file_node (file_name, is_dir, course_id, class_id, uploader_id, file_type, section_id, file_size, visibility, file_url, file_version, object_name)
VALUES 
('Java入门.pdf', false, 1, 1, 1, 'PDF', -1, 102400, 'CLASS_ONLY', '/courses/1/classes/1/files/java_intro_v1.pdf', 1, 'course/1/class/1/file1_v1.pdf'),
('Python教程.zip', false, 1, 1, 1, 'OTHER', -1, 512000, 'PUBLIC', '/courses/1/classes/1/files/python_tutorial.zip', 1, 'course/1/class/1/file2_v1.zip'),
('课程大纲.docx', false, 1, 1, 1, 'PPT', -1, 81920, 'COURSE_ONLY', '/courses/1/classes/1/files/course_outline_v1.docx', 1, 'course/1/class/1/file3_v1.docx');

-- 课程1的软工B班文件
INSERT INTO file_node (file_name, is_dir, course_id, class_id, uploader_id, file_type, section_id, file_size, visibility, file_url, file_version, object_name)
VALUES 
('Java进阶.pdf', false, 1, 2, 1, 'PDF', -1, 102400, 'CLASS_ONLY', '/courses/1/classes/2/files/java_adv_v1.pdf', 1, 'course/1/class/2/file1_v1.pdf'),
('Spring框架.pptx', false, 1, 2, 1, 'PPT', -1, 307200, 'CLASS_ONLY', '/courses/1/classes/2/files/spring_v1.pptx', 1, 'course/1/class/2/file2_v1.pptx'),
('课程资料.zip', false, 1, 2, 1, 'OTHER', -1, 1024000, 'CLASS_ONLY', '/courses/1/classes/2/files/materials.zip', 1, 'course/1/class/2/file3_v1.zip');

-- 课程2的网络A班文件
INSERT INTO file_node (file_name, is_dir, course_id, class_id, uploader_id, file_type, section_id, file_size, visibility, file_url, file_version, object_name)
VALUES 
('网络基础讲义.pdf', false, 2, 3, 1, 'PDF', -1, 102400, 'CLASS_ONLY', '/courses/2/classes/3/files/network_basics_v1.pdf', 1, 'course/2/class/3/file1_v1.pdf'),
('HTTP协议详解.pptx', false, 2, 3, 1, 'PPT', -1, 307200, 'CLASS_ONLY', '/courses/2/classes/3/files/http_protocol_v1.pptx', 1, 'course/2/class/3/file2_v1.pptx');

-- 7. 学习进度 Progress
INSERT INTO Progress (student_id, course_id, section_id, completed, completed_at)
VALUES 
(2, 1, 1, true, NOW()),
(2, 1, 2, false, NULL),
(3, 1, 1, true, NOW());

-- 8. 通知 Notification
INSERT INTO Notification (user_id, title, message)
VALUES 
(2, '新资源已上传', '课程《软件工程基础》新增了三个教学视频，请查看。'),
(2, '本周练习提醒', '请按时完成“第一章练习”'),
(3, '成绩更新', '你的“需求分析”练习获得满分！'),
(5, '新资源已上传', '《计算机网络》添加了最新实验指南');

-- 9. 题库 Question
INSERT INTO Question (creator_id, type, content, options, answer)
VALUES 
(1, 'singlechoice', '软件工程的第一步是什么？', '["需求分析","编码","测试","部署"]', '需求分析'),
(1, 'program', '编写一个打印“Hello World”的 Java 方法', '', 'System.out.println("Hello World");'),
(1, 'fillblank', 'Java中的main方法签名是______.', 'public static void main(String[] args)', 'public static void main(String[] args)');

-- 10. 练习 Practice
INSERT INTO Practice (course_id, title, start_time, end_time, allow_multiple_submission, created_by)
VALUES 
(1, '第一章练习', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), TRUE, 1),
(1, '第二章练习', NOW(), DATE_ADD(NOW(), INTERVAL 7 DAY), FALSE, 1);

-- 11. 练习题目关联 PracticeQuestion
INSERT INTO PracticeQuestion (practice_id, question_id, score)
VALUES 
(1, 1, 5),
(1, 2, 10),
(2, 3, 15);

-- 12. 提交 Submission
INSERT INTO Submission (practice_id, student_id, score, feedback)
VALUES 
(1, 2, 15, '全部正确'),
(1, 3, 10, '部分错误');

-- 13. 答案 Answer
INSERT INTO Answer (submission_id, question_id, answer_text, correct, score)
VALUES 
(1, 1, '需求分析', TRUE, 5),
(1, 2, 'System.out.println("Hello World");', TRUE, 10),
(2, 1, '编码', FALSE, 0);

-- 14. 收藏题库 FavoriteQuestion
INSERT INTO FavoriteQuestion (student_id, question_id)
VALUES 
(2, 1),
(2, 2),
(3, 1);