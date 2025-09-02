# Content Service API 文档

## 概述
Content Service 是教育软件系统的内容管理微服务，提供教学资源、作业、讨论、通知等核心功能。

## 基础信息
- **服务端口**: 8083
- **基础路径**: `/api/content`
- **健康检查**: `/actuator/health`

## 1. 教学资源管理 (TeachingResource)

### 1.1 上传教学资源
```http
POST /api/content/teaching-resources/upload
Content-Type: multipart/form-data

file: [文件]
courseId: [课程ID]
chapterId: [章节ID]
chapterName: [章节名称]
title: [资源标题]
description: [资源描述]
```

### 1.2 获取课程资源
```http
GET /api/content/teaching-resources/course/{courseId}
```

### 1.3 获取章节资源
```http
GET /api/content/teaching-resources/chapter/{courseId}/{chapterId}
```

### 1.4 更新学习进度
```http
PUT /api/content/teaching-resources/progress
Content-Type: application/json

{
  "resourceId": 1,
  "studentId": 1,
  "progress": 0.75,
  "position": 300
}
```

## 2. 作业管理 (Homework)

### 2.1 创建作业
```http
POST /api/content/homework
Content-Type: multipart/form-data

classId: [班级ID]
title: [作业标题]
description: [作业描述]
endTime: [截止时间]
file: [附件文件]
```

### 2.2 获取作业列表
```http
GET /api/content/homework/class/{classId}
```

### 2.3 提交作业
```http
POST /api/content/homework/{homeworkId}/submit
Content-Type: multipart/form-data

studentId: [学生ID]
content: [提交内容]
file: [提交文件]
```

### 2.4 获取提交列表
```http
GET /api/content/homework/{homeworkId}/submissions
```

## 3. 讨论管理 (Discussion)

### 3.1 创建讨论
```http
POST /api/content/discussions
Content-Type: application/json

{
  "title": "讨论标题",
  "content": "讨论内容",
  "courseId": 1,
  "chapterId": 1,
  "creatorId": 1
}
```

### 3.2 获取讨论列表
```http
GET /api/content/discussions/course/{courseId}
```

### 3.3 更新讨论状态
```http
PUT /api/content/discussions/{discussionId}/status
Content-Type: application/json

{
  "status": "closed"
}
```

## 4. 讨论回复 (DiscussionReply)

### 4.1 创建回复
```http
POST /api/content/discussion-replies
Content-Type: application/json

{
  "discussionId": 1,
  "parentReplyId": null,
  "content": "回复内容",
  "creatorId": 1
}
```

### 4.2 获取讨论回复
```http
GET /api/content/discussion-replies/discussion/{discussionId}
```

### 4.3 点赞回复
```http
POST /api/content/discussion-replies/{replyId}/like
```

## 5. 学习进度 (LearningProgress)

### 5.1 更新学习进度
```http
PUT /api/content/learning-progress
Content-Type: application/json

{
  "resourceId": 1,
  "studentId": 1,
  "progress": 0.8,
  "position": 400
}
```

### 5.2 获取学习进度
```http
GET /api/content/learning-progress/{resourceId}/{studentId}
```

### 5.3 获取课程进度统计
```http
GET /api/content/learning-progress/course/{courseId}/statistics
```

## 6. 通知管理 (Notification)

### 6.1 创建通知
```http
POST /api/content/notifications
Content-Type: application/json

{
  "title": "通知标题",
  "content": "通知内容",
  "type": "ANNOUNCEMENT",
  "targetType": "COURSE",
  "targetId": 1,
  "senderId": 1
}
```

### 6.2 获取用户通知
```http
GET /api/content/notifications/user/{userId}
```

### 6.3 标记通知为已读
```http
PUT /api/content/notifications/{notificationId}/read
```

## 7. 任务提醒 (TaskReminder)

### 7.1 创建任务提醒
```http
POST /api/content/task-reminders
Content-Type: application/json

{
  "title": "提醒标题",
  "description": "提醒描述",
  "dueDate": "2024-01-15T23:59:59",
  "priority": "HIGH",
  "userId": 1
}
```

### 7.2 获取用户提醒
```http
GET /api/content/task-reminders/user/{userId}
```

### 7.3 更新提醒状态
```http
PUT /api/content/task-reminders/{reminderId}/status
Content-Type: application/json

{
  "status": "COMPLETED"
}
```

## 8. 文件管理 (File)

### 8.1 上传文件
```http
POST /api/content/upload/file
Content-Type: multipart/form-data

file: [文件]
folder: [存储文件夹]
```

### 8.2 删除文件
```http
DELETE /api/content/upload/file?fileUrl=[文件URL]
```

### 8.3 生成签名URL
```http
GET /api/content/upload/signed-url?objectName=[对象名称]
```

## 9. 文件访问 (FileAccess)

### 9.1 获取文件信息
```http
GET /api/content/files/{fileId}
```

### 9.2 获取用户文件
```http
GET /api/content/files/uploader/{uploaderId}
```

### 9.3 更新文件描述
```http
PUT /api/content/files/{fileId}/description
Content-Type: application/json

{
  "description": "新的文件描述"
}
```

## 10. 健康检查和监控

### 10.1 健康状态
```http
GET /actuator/health
```

### 10.2 应用信息
```http
GET /actuator/info
```

### 10.3 指标数据
```http
GET /actuator/metrics
```

## 认证和权限

所有API都需要通过Sa-Token进行身份验证，在请求头中添加：
```http
satoken: [用户Token]
```

## 错误处理

### 标准错误响应格式
```json
{
  "success": false,
  "message": "错误描述",
  "code": 500,
  "data": null
}
```

### 常见HTTP状态码
- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

## 文件上传限制

- **最大文件大小**: 500MB
- **支持的文件类型**: 
  - 文档: PDF, DOC, DOCX, XLS, XLSX, PPT, PPTX, TXT
  - 图片: JPG, JPEG, PNG, GIF
  - 压缩包: ZIP, RAR
  - 视频: MP4, AVI, MOV, WMV

## 数据格式

### 时间格式
所有时间字段使用ISO 8601格式：`YYYY-MM-DDTHH:mm:ss`

### 分页参数
```http
GET /api/content/resource?page=1&size=20&sort=createdAt,desc
```

## 测试建议

1. 使用Postman或类似工具测试API
2. 先测试健康检查接口确认服务状态
3. 按照依赖关系顺序测试（先创建资源，再测试相关功能）
4. 注意文件上传接口的Content-Type设置
5. 验证错误处理机制

## 注意事项

1. 文件上传会存储到阿里云OSS（当前使用模拟实现）
2. 所有ID字段使用Long类型
3. 进度字段使用Double类型（0.0-1.0）
4. 状态字段使用枚举值
5. 软删除机制，数据不会物理删除
