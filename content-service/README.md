# Content Service (内容服务微服务)

## 概述
内容服务微服务负责管理教育平台的所有内容相关功能，包括文件管理、教学资源、通知管理和讨论区。

## 技术栈
- **框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **ORM**: MyBatis 3.0.2
- **认证**: Sa-Token
- **文件存储**: 本地存储 + 阿里云OSS
- **构建工具**: Maven

## 核心功能模块

### 1. 文件管理模块 (File Management)
- 文件上传/下载
- 文件分类管理
- 文件权限控制
- 文件预览
- 文件版本管理

### 2. 教学资源模块 (Teaching Resources)
- 课程资源管理
- 视频资源处理
- 文档资源管理
- 资源分类和标签
- 资源搜索

### 3. 通知管理模块 (Notification Management)
- 系统通知
- 课程通知
- 作业提醒
- 通知推送
- 通知历史

### 4. 讨论区模块 (Discussion Forum)
- 话题发布
- 回复管理
- 点赞功能
- 话题分类
- 内容审核

## 数据库设计

### 主要数据表
1. `file_info` - 文件信息表
2. `teaching_resource` - 教学资源表
3. `notification` - 通知表
4. `discussion` - 讨论话题表
5. `discussion_reply` - 讨论回复表
6. `discussion_like` - 讨论点赞表

## API接口设计

### 文件管理接口
- `POST /api/content/file/upload` - 文件上传
- `GET /api/content/file/download/{fileId}` - 文件下载
- `GET /api/content/file/list` - 文件列表
- `DELETE /api/content/file/{fileId}` - 删除文件

### 教学资源接口
- `POST /api/content/resource` - 创建资源
- `GET /api/content/resource/{resourceId}` - 获取资源详情
- `GET /api/content/resource/list` - 资源列表
- `PUT /api/content/resource/{resourceId}` - 更新资源

### 通知管理接口
- `POST /api/content/notification` - 发送通知
- `GET /api/content/notification/list` - 通知列表
- `PUT /api/content/notification/{id}/read` - 标记已读
- `DELETE /api/content/notification/{id}` - 删除通知

### 讨论区接口
- `POST /api/content/discussion` - 发布话题
- `GET /api/content/discussion/{id}` - 话题详情
- `POST /api/content/discussion/{id}/reply` - 回复话题
- `POST /api/content/discussion/{id}/like` - 点赞话题

## 微服务通信
- 与用户服务通信：获取用户信息、权限验证
- 与课程服务通信：获取课程信息、课程资源关联
- 与作业服务通信：作业相关通知

## 部署配置
- 端口：8082
- 数据库：content_db
- 文件存储路径：/data/content/files

