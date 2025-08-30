# Content Service - 作业功能模块

## 功能概述

作业功能模块提供了完整的作业管理功能，包括：
- 教师创建和管理作业
- 学生提交作业
- 查看作业列表和提交记录
- 文件上传和下载

## API 接口

### 1. 创建作业
```
POST /api/content/homework/create
Content-Type: multipart/form-data

参数：
- class_id: 班级ID（必选）
- title: 作业标题（必选）
- description: 作业描述（可选）
- end_time: 截止时间，格式：yyyy-MM-dd HH:mm:ss（可选）
- file: 附件文件（可选）
```

### 2. 获取作业详情
```
GET /api/content/homework/{id}

参数：
- id: 作业ID
```

### 3. 获取班级作业列表
```
GET /api/content/homework/list?classId={classId}

参数：
- classId: 班级ID
```

### 4. 提交作业
```
POST /api/content/homework/submit/{homeworkId}
Content-Type: multipart/form-data

参数：
- homeworkId: 作业ID（路径参数）
- student_id: 学生ID（必选）
- file: 提交的文件（必选）
```

### 5. 获取作业提交列表
```
GET /api/content/homework/submissions/{homeworkId}

参数：
- homeworkId: 作业ID
```

### 6. 获取学生提交记录
```
GET /api/content/homework/submission?homeworkId={homeworkId}&studentId={studentId}

参数：
- homeworkId: 作业ID
- studentId: 学生ID
```

### 7. 下载作业附件
```
GET /api/content/homework/file/{homeworkId}

参数：
- homeworkId: 作业ID
```

### 8. 下载提交的作业文件
```
GET /api/content/homework/submission/file/{submissionId}

参数：
- submissionId: 提交记录ID
```

### 9. 删除作业
```
DELETE /api/content/homework/{homeworkId}

参数：
- homeworkId: 作业ID
```

## 数据库表结构

### homework 表
- id: 作业ID（主键）
- title: 作业标题
- description: 作业描述
- course_id: 课程ID
- chapter_id: 章节ID
- chapter_name: 章节名称
- class_id: 班级ID
- created_by: 创建者ID
- created_by_name: 创建者姓名
- attachment_url: 附件URL
- object_name: 对象存储路径
- file_name: 文件名
- deadline: 截止时间
- status: 状态（draft/published/archived）
- submission_count: 提交数量
- created_at: 创建时间
- updated_at: 更新时间

### homeworksubmission 表
- id: 提交记录ID（主键）
- homework_id: 作业ID（外键）
- student_id: 学生ID
- student_name: 学生姓名
- file_url: 文件URL
- object_name: 对象存储路径
- submitted_at: 提交时间

## 部署说明

1. 确保数据库已创建并运行
2. 执行 `src/main/resources/sql/content.sql` 创建完整的数据库表结构（包含作业相关表）
3. 配置数据库连接信息
4. 启动 content-service 服务

## 注意事项

- 文件上传功能目前使用简化实现，生产环境需要集成实际的文件存储服务
- 学生姓名目前使用简化处理，实际使用时需要从用户服务获取
- 建议在生产环境中添加适当的权限控制和数据验证
