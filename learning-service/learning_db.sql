-- 学习服务数据库
drop database if exists learning_db;
CREATE DATABASE learning_db;
USE learning_db;

-- 作业表
CREATE TABLE homework (
                          id bigint NOT NULL AUTO_INCREMENT,
                          title varchar(255) NOT NULL,
                          description text,
                          class_id bigint NOT NULL,  -- 引用课程服务
                          created_by bigint NOT NULL,  -- 引用用户服务
                          attachment_url varchar(255) DEFAULT NULL,
                          object_name varchar(255) DEFAULT NULL,
                          deadline datetime NOT NULL,
                          created_at datetime DEFAULT CURRENT_TIMESTAMP,
                          updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          PRIMARY KEY (id)
);

-- 作业提交表
CREATE TABLE homework_submission (
                                     id bigint NOT NULL AUTO_INCREMENT,
                                     homework_id bigint NOT NULL,
                                     student_id bigint NOT NULL,  -- 引用用户服务
                                     file_url varchar(255) DEFAULT NULL,
                                     object_name varchar(255) DEFAULT NULL,
                                     submitted_at datetime DEFAULT CURRENT_TIMESTAMP,
                                     PRIMARY KEY (id),
                                     KEY homework_id (homework_id),
                                     CONSTRAINT homework_submission_ibfk_1 FOREIGN KEY (homework_id) REFERENCES homework (id) ON DELETE CASCADE
);

-- 练习表
CREATE TABLE practice (
                          id bigint NOT NULL AUTO_INCREMENT,
                          course_id bigint NOT NULL,  -- 引用课程服务
                          class_id bigint NOT NULL,  -- 引用课程服务
                          title varchar(200) NOT NULL,
                          start_time datetime DEFAULT NULL,
                          end_time datetime DEFAULT NULL,
                          allow_multiple_submission tinyint(1) DEFAULT '1',
                          created_by bigint DEFAULT NULL,  -- 引用用户服务
                          created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          KEY course_id (course_id),
                          KEY class_id (class_id)
);

-- 题目表
CREATE TABLE question (
                          id bigint NOT NULL AUTO_INCREMENT,
                          creator_id bigint NOT NULL,  -- 引用用户服务
                          type enum('singlechoice','program','fillblank','judge') NOT NULL,
                          content text NOT NULL,
                          analysis text,
                          options text,
                          answer text,
                          course_id bigint DEFAULT NULL,  -- 引用课程服务
                          section_id bigint DEFAULT NULL,  -- 引用课程服务
                          created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                          PRIMARY KEY (id),
                          KEY creator_id (creator_id)
);

-- 练习题目关联表
CREATE TABLE practice_question (
                                   practice_id bigint NOT NULL,
                                   question_id bigint NOT NULL,
                                   sort_order bigint DEFAULT NULL,
                                   score int NOT NULL,
                                   score_rate decimal(5,4) DEFAULT NULL,
                                   PRIMARY KEY (practice_id, question_id),
                                   KEY question_id (question_id),
                                   CONSTRAINT practice_question_ibfk_1 FOREIGN KEY (practice_id) REFERENCES practice (id) ON DELETE CASCADE,
                                   CONSTRAINT practice_question_ibfk_2 FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE
);

-- 练习提交表
CREATE TABLE submission (
                            id bigint NOT NULL AUTO_INCREMENT,
                            practice_id bigint NOT NULL,
                            student_id bigint NOT NULL,  -- 引用用户服务
                            submitted_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            score int DEFAULT '0',
                            is_judged int DEFAULT '0',
                            feedback text,
                            PRIMARY KEY (id),
                            KEY practice_id (practice_id),
                            KEY student_id (student_id),
                            CONSTRAINT submission_ibfk_1 FOREIGN KEY (practice_id) REFERENCES practice (id) ON DELETE CASCADE
);

-- 答案表
CREATE TABLE answer (
                        id bigint NOT NULL AUTO_INCREMENT,
                        submission_id bigint NOT NULL,
                        question_id bigint NOT NULL,
                        answer_text text,
                        is_judged tinyint(1) DEFAULT '0',
                        correct tinyint(1) DEFAULT NULL,
                        score int DEFAULT NULL,
                        sort_order bigint DEFAULT NULL,
                        PRIMARY KEY (id),
                        KEY submission_id (submission_id),
                        KEY question_id (question_id),
                        CONSTRAINT answer_ibfk_1 FOREIGN KEY (submission_id) REFERENCES submission (id) ON DELETE CASCADE,
                        CONSTRAINT answer_ibfk_2 FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE
);

-- 自主练习表
CREATE TABLE self_practice (
                               id bigint NOT NULL AUTO_INCREMENT,
                               student_id bigint NOT NULL,  -- 引用用户服务
                               title varchar(200) NOT NULL,
                               prompt text DEFAULT NULL,  -- AI生成提示词
                               created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (id),
                               KEY student_id (student_id)
);

-- 自主练习提交表
CREATE TABLE self_submission (
                                 id bigint NOT NULL AUTO_INCREMENT,
                                 self_practice_id bigint NOT NULL,
                                 student_id bigint NOT NULL,  -- 引用用户服务
                                 submitted_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                 score int DEFAULT '0',
                                 is_judged tinyint(1) DEFAULT '0',
                                 feedback json DEFAULT NULL,
                                 PRIMARY KEY (id),
                                 KEY self_practice_id (self_practice_id),
                                 KEY student_id (student_id),
                                 CONSTRAINT self_submission_ibfk_1 FOREIGN KEY (self_practice_id) REFERENCES self_practice (id) ON DELETE CASCADE
);

-- 自主练习答案表
CREATE TABLE self_answer (
                             id bigint NOT NULL AUTO_INCREMENT,
                             submission_id bigint NOT NULL,
                             question_id bigint NOT NULL,
                             answer_text text,
                             is_judged tinyint(1) DEFAULT '0',
                             correct tinyint(1) DEFAULT NULL,
                             score int DEFAULT NULL,
                             sort_order int DEFAULT NULL,
                             PRIMARY KEY (id),
                             KEY submission_id (submission_id),
                             KEY question_id (question_id),
                             CONSTRAINT self_answer_ibfk_1 FOREIGN KEY (submission_id) REFERENCES self_submission (id) ON DELETE CASCADE,
                             CONSTRAINT self_answer_ibfk_2 FOREIGN KEY (question_id) REFERENCES question (id) ON DELETE CASCADE
);

-- 学习记录表
CREATE TABLE learning_progress (
                                   id bigint NOT NULL AUTO_INCREMENT,
                                   resource_id bigint NOT NULL,  -- 引用内容服务
                                   student_id bigint NOT NULL,  -- 引用用户服务
                                   progress decimal(5,2) NOT NULL,
                                   last_position int NOT NULL,
                                   watch_count int DEFAULT '0',
                                   last_watch_time datetime DEFAULT NULL,
                                   created_at datetime DEFAULT CURRENT_TIMESTAMP,
                                   updated_at datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (id),
                                   UNIQUE KEY uk_resource_student (resource_id, student_id)
);

-- 课程进度表
CREATE TABLE progress (
                          id bigint NOT NULL AUTO_INCREMENT,
                          student_id bigint NOT NULL,  -- 引用用户服务
                          course_id bigint NOT NULL,  -- 引用课程服务
                          section_id bigint DEFAULT NULL,  -- 引用课程服务
                          completed tinyint(1) DEFAULT '0',
                          completed_at timestamp NULL DEFAULT NULL,
                          PRIMARY KEY (id),
                          KEY student_id (student_id),
                          KEY course_id (course_id),
                          KEY section_id (section_id)
);

-- 错题表
CREATE TABLE wrong_question (
                                id bigint NOT NULL AUTO_INCREMENT,
                                student_id bigint NOT NULL,  -- 引用用户服务
                                question_id bigint NOT NULL,
                                wrong_answer text,
                                correct_answer text,
                                wrong_count int DEFAULT '1',
                                last_wrong_time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                PRIMARY KEY (id),
                                KEY student_id (student_id),
                                KEY question_id (question_id)
);

-- 收藏题目表
CREATE TABLE favorite_question (
                                   student_id bigint NOT NULL,  -- 引用用户服务
                                   question_id bigint NOT NULL,
                                   PRIMARY KEY (student_id, question_id),
                                   KEY question_id (question_id)
);

-- 插入示例数据
-- 1. 插入作业数据
INSERT INTO homework (title, description, class_id, created_by, attachment_url, object_name, deadline) VALUES
                                                                                                           ('Java基础作业', '完成Java基础语法练习', 1, 101, 'https://example.com/files/java_basic.pdf', 'java_basic.pdf', '2025-09-10 23:59:59'),
                                                                                                           ('数据库设计作业', '设计一个学生信息管理系统数据库', 2, 102, 'https://example.com/files/db_design.pdf', 'db_design.pdf', '2025-09-15 23:59:59'),
                                                                                                           ('Web开发作业', '使用Spring Boot开发一个简单的REST API', 1, 101, 'https://example.com/files/web_dev.zip', 'web_dev.zip', '2025-09-20 23:59:59'),
                                                                                                           ('算法作业', '实现排序算法并分析时间复杂度', 3, 103, NULL, NULL, '2025-09-12 23:59:59');

-- 2. 插入作业提交数据
INSERT INTO homework_submission (homework_id, student_id, file_url, object_name, submitted_at) VALUES
                                                                                                   (1, 201, 'https://example.com/submissions/java_hw1.pdf', 'java_hw1.pdf', '2025-09-09 14:30:25'),
                                                                                                   (1, 202, 'https://example.com/submissions/java_hw2.zip', 'java_hw2.zip', '2025-09-10 10:15:40'),
                                                                                                   (2, 201, 'https://example.com/submissions/db_design.sql', 'db_design.sql', '2025-09-14 16:45:30'),
                                                                                                   (3, 203, 'https://example.com/submissions/web_api.zip', 'web_api.zip', '2025-09-19 20:30:15'),
                                                                                                   (4, 202, 'https://example.com/submissions/algorithms.java', 'algorithms.java', '2025-09-11 09:20:50');

-- 3. 插入练习数据
INSERT INTO practice (course_id, class_id, title, start_time, end_time, allow_multiple_submission, created_by) VALUES
                                                                                                                   (1, 101, 'Java基础测验', '2025-09-01 00:00:00', '2025-09-07 23:59:59', 1, 101),
                                                                                                                   (1, 101, '面向对象编程练习', '2025-09-08 00:00:00', '2025-09-14 23:59:59', 0, 101),
                                                                                                                   (2, 102, 'SQL查询练习', '2025-09-05 00:00:00', '2025-09-12 23:59:59', 1, 102),
                                                                                                                   (3, 103, '算法复杂度分析', '2025-09-10 00:00:00', '2025-09-17 23:59:59', 0, 103);

-- 4. 插入题目数据
INSERT INTO question (creator_id, type, content, analysis, options, answer, course_id, section_id) VALUES
                                                                                                       (101, 'singlechoice', 'Java中哪个关键字用于定义类？', 'class是Java中定义类的关键字', '["interface", "class", "struct", "object"]', 'class', 1, 1),
                                                                                                       (101, 'judge', 'Java中所有类都继承自Object类', '是的，Java中所有类都直接或间接继承自Object类', NULL, 'true', 1, 1),
                                                                                                       (102, 'fillblank', 'SQL中用于查询数据的关键字是____', 'SELECT是SQL中用于查询数据的关键字', NULL, 'SELECT', 2, 2),
                                                                                                       (103, 'singlechoice', '以下哪种排序算法的时间复杂度是O(n log n)？', '快速排序和归并排序的时间复杂度都是O(n log n)', '["冒泡排序", "选择排序", "快速排序", "插入排序"]', '快速排序', 3, 3),
                                                                                                       (101, 'program', '编写一个Java方法，计算两个整数的和', '这是一个简单的加法实现', NULL, 'public int add(int a, int b) { return a + b; }', 1, 1);

-- 5. 插入练习题目关联数据
INSERT INTO practice_question (practice_id, question_id, sort_order, score, score_rate) VALUES
                                                                                            (1, 1, 1, 10, 1.0),
                                                                                            (1, 2, 2, 10, 1.0),
                                                                                            (2, 5, 1, 20, 1.0),
                                                                                            (3, 3, 1, 15, 1.0),
                                                                                            (4, 4, 1, 15, 1.0);

-- 6. 插入练习提交数据
INSERT INTO submission (practice_id, student_id, submitted_at, score, is_judged, feedback) VALUES
                                                                                               (1, 201, '2025-09-05 14:30:25', 18, 1, '做得很好，但第二题需要更仔细阅读题目'),
                                                                                               (1, 202, '2025-09-06 10:15:40', 20, 1, '完美！所有答案都正确'),
                                                                                               (2, 201, '2025-09-10 16:45:30', 15, 1, '代码可以进一步优化'),
                                                                                               (3, 203, '2025-09-08 20:30:15', 12, 1, '基本正确，但有一个查询语句有误'),
                                                                                               (4, 202, '2025-09-15 09:20:50', 15, 1, '分析得很透彻');

-- 7. 插入答案数据
INSERT INTO answer (submission_id, question_id, answer_text, is_judged, correct, score, sort_order) VALUES
                                                                                                        (1, 1, 'class', 1, 1, 10, 1),
                                                                                                        (1, 2, 'true', 1, 1, 8, 2),
                                                                                                        (2, 1, 'class', 1, 1, 10, 1),
                                                                                                        (2, 2, 'true', 1, 1, 10, 2),
                                                                                                        (3, 5, 'public int add(int a, int b) { return a + b; }', 1, 1, 15, 1),
                                                                                                        (4, 3, 'SELECT', 1, 1, 12, 1),
                                                                                                        (5, 4, '快速排序', 1, 1, 15, 1);

-- 8. 插入自主练习数据
INSERT INTO self_practice (student_id, title, created_at) VALUES
                                                              (201, 'Java基础复习', '2025-09-01 10:30:25'),
                                                              (202, 'SQL查询练习', '2025-09-02 14:20:15'),
                                                              (203, '算法练习题', '2025-09-03 16:45:30'),
                                                              (201, 'Web开发练习', '2025-09-04 09:15:40');

-- 9. 插入自主练习提交数据
INSERT INTO self_submission (self_practice_id, student_id, submitted_at, score, is_judged, feedback) VALUES
                                                                                                         (1, 201, '2025-09-01 11:30:25', 45, 1, '{"correct": 3, "total": 5, "comment": "需要加强基础知识"}'),
                                                                                                         (2, 202, '2025-09-02 15:20:15', 40, 1, '{"correct": 4, "total": 5, "comment": "做得很好"}'),
                                                                                                         (3, 203, '2025-09-03 17:45:30', 35, 1, '{"correct": 3, "total": 5, "comment": "算法理解有待提高"}'),
                                                                                                         (4, 201, '2025-09-04 10:15:40', 50, 1, '{"correct": 5, "total": 5, "comment": "完美！"}');

-- 10. 插入自主练习答案数据
INSERT INTO self_answer (submission_id, question_id, answer_text, is_judged, correct, score, sort_order) VALUES
                                                                                                             (1, 1, 'class', 1, 1, 10, 1),
                                                                                                             (1, 2, 'true', 1, 1, 10, 2),
                                                                                                             (1, 4, '快速排序', 1, 1, 10, 3),
                                                                                                             (1, 3, 'SELECT', 1, 0, 5, 4),
                                                                                                             (1, 5, 'public int sum(int a, int b) { return a + b; }', 1, 1, 10, 5),
                                                                                                             (2, 1, 'class', 1, 1, 10, 1),
                                                                                                             (2, 3, 'SELECT', 1, 1, 10, 2),
                                                                                                             (2, 4, '快速排序', 1, 1, 10, 3),
                                                                                                             (2, 2, 'false', 1, 0, 0, 4),
                                                                                                             (2, 5, 'public int add(int a, int b) { return a + b; }', 1, 1, 10, 5);

-- 11. 插入学习记录数据
INSERT INTO learning_progress (resource_id, student_id, progress, last_position, watch_count, last_watch_time) VALUES
                                                                                                                   (1001, 201, 85.50, 1200, 3, '2025-09-05 14:30:25'),
                                                                                                                   (1002, 201, 45.25, 850, 2, '2025-09-04 10:15:40'),
                                                                                                                   (1001, 202, 92.75, 1350, 4, '2025-09-06 16:45:30'),
                                                                                                                   (1003, 203, 78.00, 1100, 3, '2025-09-07 20:30:15'),
                                                                                                                   (1002, 202, 65.50, 950, 2, '2025-09-05 09:20:50');

-- 12. 插入课程进度数据
INSERT INTO progress (student_id, course_id, section_id, completed, completed_at) VALUES
                                                                                      (201, 1, 1, 1, '2025-09-05 14:30:25'),
                                                                                      (201, 1, 2, 1, '2025-09-06 10:15:40'),
                                                                                      (202, 1, 1, 1, '2025-09-04 16:45:30'),
                                                                                      (203, 2, 1, 1, '2025-09-07 20:30:15'),
                                                                                      (202, 2, 1, 0, NULL);

-- 13. 插入错题数据
INSERT INTO wrong_question (student_id, question_id, wrong_answer, correct_answer, wrong_count, last_wrong_time) VALUES
                                                                                                                     (201, 3, 'FROM', 'SELECT', 2, '2025-09-05 14:30:25'),
                                                                                                                     (202, 2, 'false', 'true', 1, '2025-09-06 10:15:40'),
                                                                                                                     (203, 4, '冒泡排序', '快速排序', 3, '2025-09-07 20:30:15'),
                                                                                                                     (201, 5, 'public void add(int a, int b) { a + b; }', 'public int add(int a, int b) { return a + b; }', 1, '2025-09-08 09:20:50');

-- 14. 插入收藏题目数据
INSERT INTO favorite_question (student_id, question_id) VALUES
                                                            (201, 1),
                                                            (201, 4),
                                                            (202, 3),
                                                            (203, 5),
                                                            (202, 1);

-- AI相关表结构

-- AI服务调用日志表
CREATE TABLE ai_service_call_log (
    id bigint NOT NULL AUTO_INCREMENT,
    user_id bigint NOT NULL,
    endpoint varchar(255) NOT NULL,
    request_data json DEFAULT NULL,
    response_data json DEFAULT NULL,
    status varchar(50) DEFAULT 'SUCCESS',
    error_message text DEFAULT NULL,
    execution_time_ms int DEFAULT NULL,
    created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_endpoint (endpoint),
    KEY idx_created_at (created_at)
);

-- 聊天会话表
CREATE TABLE chat_session (
    id bigint NOT NULL AUTO_INCREMENT,
    user_id bigint NOT NULL,
    session_name varchar(255) DEFAULT '新对话',
    created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
);

-- 聊天记录表
CREATE TABLE chat_memory (
    id bigint NOT NULL AUTO_INCREMENT,
    session_id bigint NOT NULL,
    user_id bigint NOT NULL,
    role enum('user','assistant') NOT NULL,
    content text NOT NULL,
    created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_session_id (session_id),
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at),
    CONSTRAINT chat_memory_ibfk_1 FOREIGN KEY (session_id) REFERENCES chat_session (id) ON DELETE CASCADE
);

-- 视频摘要表
CREATE TABLE video_summary (
    id bigint NOT NULL AUTO_INCREMENT,
    video_id bigint NOT NULL,
    user_id bigint NOT NULL,
    summary_content text NOT NULL,
    key_points json DEFAULT NULL,
    summary_type varchar(50) DEFAULT 'AUTO',
    created_at timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_video_id (video_id),
    KEY idx_user_id (user_id),
    KEY idx_created_at (created_at)
);
