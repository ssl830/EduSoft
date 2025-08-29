# Content Service

内容管理微服务，负责文件管理、教学资源、通知管理和讨论区功能。

## 功能模块

### 1. 文件管理 (File Management)
- 文件上传下载
- 文件信息管理
- 文件可见性控制

### 2. 教学资源 (Teaching Resources)
- 教学资源创建和管理
- 资源分类和搜索
- 资源统计（浏览次数、下载次数）

### 3. 通知管理 (Notification Management)
- 系统通知创建
- 通知状态管理（已读/未读）
- 通知过期处理

### 4. 讨论区 (Discussion Forum)
- 讨论主题创建和管理
- 讨论回复功能
- 讨论置顶和关闭
- 教师回复标记

### 5. 学习进度 (Learning Progress)
- 学生学习进度跟踪
- 观看次数统计
- 进度更新

## API 接口

### 文件管理接口

#### 文件上传
```
POST /api/content/file/upload
Content-Type: multipart/form-data

参数:
- file: 文件
- uploaderId: 上传者ID (Long)
- visibility: 可见性 (String, 可选)
- objectName: 对象名称 (String, 可选)
- fileUrl: 文件URL (String, 可选)
```

#### 文件下载
```
GET /api/content/file/download/{fileId}
```

#### 获取文件信息
```
GET /api/content/file/{fileId}
```

#### 获取用户文件列表
```
GET /api/content/file/uploader/{uploaderId}
```

#### 获取可见性文件列表
```
GET /api/content/file/visibility/{visibility}
```

#### 更新文件可见性
```
PUT /api/content/file/{fileId}/visibility
Content-Type: application/json

{
  "visibility": "public"
}
```

### 教学资源接口

#### 创建教学资源
```
POST /api/content/resource
Content-Type: application/json

{
  "title": "资源标题",
  "description": "资源描述",
  "authorId": 123,
  "courseId": 456,
  "chapterId": 789,
  "chapterName": "章节名称",
  "type": "video",
  "fileUrl": "http://example.com/resource.mp4",
  "objectName": "resource.mp4",
  "duration": 3600
}
```

#### 获取资源详情
```
GET /api/content/resource/{id}
```

#### 获取作者资源列表
```
GET /api/content/resource/author/{authorId}
```

#### 获取课程资源列表
```
GET /api/content/resource/course/{courseId}
```

#### 获取章节资源列表
```
GET /api/content/resource/chapter/{chapterId}
```

#### 获取类型资源列表
```
GET /api/content/resource/type/{type}
```

#### 获取所有已发布资源
```
GET /api/content/resource/published
```

#### 更新资源
```
PUT /api/content/resource/{id}
Content-Type: application/json

{
  "title": "更新后的标题",
  "description": "更新后的描述"
}
```

#### 归档资源
```
PUT /api/content/resource/{id}/archive
```

#### 增加浏览次数
```
PUT /api/content/resource/{id}/view
```

#### 增加下载次数
```
PUT /api/content/resource/{id}/download
```

### 通知管理接口

#### 创建通知
```
POST /api/content/notification
Content-Type: application/json

{
  "userId": 123,
  "message": "通知内容",
  "type": "system",
  "relatedId": 456,
  "relatedType": "course"
}
```

#### 获取通知详情
```
GET /api/content/notification/{id}
```

#### 获取用户通知列表
```
GET /api/content/notification/user/{userId}
```

#### 获取用户未读通知
```
GET /api/content/notification/user/{userId}/unread
```

#### 获取类型通知列表
```
GET /api/content/notification/type/{type}
```

#### 获取关联对象通知列表
```
GET /api/content/notification/related?relatedType=course&relatedId=123
```

#### 标记通知为已读
```
PUT /api/content/notification/{id}/read
```

#### 标记用户所有通知为已读
```
PUT /api/content/notification/user/{userId}/read-all
```

#### 过期通知
```
PUT /api/content/notification/{id}/expire
```

### 讨论区接口

#### 创建讨论
```
POST /api/content/discussion
Content-Type: application/json

{
  "courseId": 123,
  "classId": 456,
  "creatorId": 789,
  "creatorNum": "2021001",
  "title": "讨论标题",
  "content": "讨论内容",
  "category": "general"
}
```

#### 获取讨论详情
```
GET /api/content/discussion/{id}
```

#### 获取创建者讨论列表
```
GET /api/content/discussion/creator/{creatorId}
```

#### 获取课程讨论列表
```
GET /api/content/discussion/course/{courseId}
```

#### 获取班级讨论列表
```
GET /api/content/discussion/class/{classId}
```

#### 获取类型讨论列表
```
GET /api/content/discussion/type/{type}
```

#### 获取所有讨论
```
GET /api/content/discussion
```

#### 更新讨论
```
PUT /api/content/discussion/{id}
Content-Type: application/json

{
  "title": "更新后的标题",
  "content": "更新后的内容"
}
```

#### 关闭讨论
```
PUT /api/content/discussion/{id}/close
```

#### 置顶讨论
```
PUT /api/content/discussion/{id}/pin
```

#### 取消置顶讨论
```
PUT /api/content/discussion/{id}/unpin
```

#### 增加浏览次数
```
PUT /api/content/discussion/{id}/view
```

#### 增加回复次数
```
PUT /api/content/discussion/{id}/reply
```

### 讨论回复接口

#### 创建回复
```
POST /api/content/discussion-reply
Content-Type: application/json

{
  "discussionId": 123,
  "userId": 456,
  "userNum": "2021001",
  "content": "回复内容",
  "parentReplyId": 789,
  "isTeacherReply": false
}
```

#### 获取回复详情
```
GET /api/content/discussion-reply/{id}
```

#### 获取讨论回复列表
```
GET /api/content/discussion-reply/discussion/{discussionId}
```

#### 获取用户回复列表
```
GET /api/content/discussion-reply/user/{userId}
```

#### 获取父回复列表
```
GET /api/content/discussion-reply/parent/{parentReplyId}
```

#### 更新回复
```
PUT /api/content/discussion-reply/{id}
Content-Type: application/json

{
  "content": "更新后的回复内容"
}
```

#### 删除回复
```
DELETE /api/content/discussion-reply/{id}
```

#### 标记为教师回复
```
PUT /api/content/discussion-reply/{id}/teacher
```

### 学习进度接口

#### 创建学习进度
```
POST /api/content/learning-progress
Content-Type: application/json

{
  "resourceId": 123,
  "studentId": 456,
  "progress": 50,
  "lastPosition": 1800,
  "watchCount": 1
}
```

#### 获取进度详情
```
GET /api/content/learning-progress/{id}
```

#### 获取资源学生进度
```
GET /api/content/learning-progress/resource/{resourceId}/student/{studentId}
```

#### 获取学生进度列表
```
GET /api/content/learning-progress/student/{studentId}
```

#### 获取资源进度列表
```
GET /api/content/learning-progress/resource/{resourceId}
```

#### 更新进度
```
PUT /api/content/learning-progress/{id}
Content-Type: application/json

{
  "progress": 75,
  "lastPosition": 2700,
  "watchCount": 2
}
```

#### 更新进度（通过资源ID和学生ID）
```
PUT /api/content/learning-progress/resource/{resourceId}/student/{studentId}?progress=75&lastPosition=2700
```

#### 增加观看次数
```
PUT /api/content/learning-progress/resource/{resourceId}/student/{studentId}/watch
```

## 数据库表结构

### file_info 表
- id: 主键
- file_name: 文件名
- file_path: 文件路径
- file_size: 文件大小
- file_type: 文件类型
- uploader_id: 上传者ID
- visibility: 可见性
- object_name: 对象名称
- file_url: 文件URL
- upload_time: 上传时间
- status: 状态

### teaching_resource 表
- id: 主键
- title: 标题
- description: 描述
- author_id: 作者ID
- course_id: 课程ID
- chapter_id: 章节ID
- chapter_name: 章节名称
- type: 类型
- file_url: 文件URL
- object_name: 对象名称
- duration: 时长
- view_count: 浏览次数
- download_count: 下载次数
- status: 状态
- created_at: 创建时间
- updated_at: 更新时间

### notification 表
- id: 主键
- user_id: 用户ID
- message: 消息内容
- type: 类型
- related_id: 关联ID
- related_type: 关联类型
- read_flag: 已读标志
- expire_time: 过期时间
- created_at: 创建时间
- updated_at: 更新时间

### discussion 表
- id: 主键
- course_id: 课程ID
- class_id: 班级ID
- creator_id: 创建者ID
- creator_num: 创建者学号
- title: 标题
- content: 内容
- category: 分类
- is_pinned: 是否置顶
- is_closed: 是否关闭
- view_count: 浏览次数
- reply_count: 回复次数
- like_count: 点赞次数
- status: 状态
- created_at: 创建时间
- updated_at: 更新时间

### discussion_reply 表
- id: 主键
- discussion_id: 讨论ID
- user_id: 用户ID
- user_num: 用户学号
- content: 内容
- parent_reply_id: 父回复ID
- is_teacher_reply: 是否教师回复
- like_count: 点赞次数
- status: 状态
- created_at: 创建时间
- updated_at: 更新时间

### learning_progress 表
- id: 主键
- resource_id: 资源ID
- student_id: 学生ID
- progress: 进度百分比
- last_position: 最后观看位置
- watch_count: 观看次数
- last_watch_time: 最后观看时间
- created_at: 创建时间
- updated_at: 更新时间

## 技术栈

- Spring Boot 3.2.0
- MyBatis
- MySQL 8.0
- Maven

## 启动方式

```bash
mvn spring-boot:run
```

服务将在 8082 端口启动。

## 配置说明

主要配置在 `application.yml` 中：

- 数据库连接配置
- 文件上传配置
- 日志配置

