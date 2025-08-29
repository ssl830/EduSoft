-- 学习服务数据库
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