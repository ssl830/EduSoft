# Content Service - 内容管理微服务

## 服务概述

Content Service 是 EduSoft 微服务架构中的内容管理服务，负责处理教学资源、作业管理、讨论区、学习进度等核心功能。该服务需要与 User Service 和 Course Service 进行交互以获取用户信息和课程数据。

## 服务端口

- **服务端口**: 8083
- **服务名称**: content-service

## 与 User Service 的接口关系

### User Service 依赖接口 (端口: 8081)

Content Service 通过 Feign Client 调用 User Service 的以下接口：

#### 1. 用户身份验证
- **接口**: `GET /api/user/validate/{userId}`
- **用途**: 验证用户身份是否存在
- **调用场景**: 创建作业、上传资源时验证用户身份

#### 2. 获取用户信息
- **接口**: `GET /api/user/{userId}`
- **用途**: 获取用户详细信息
- **调用场景**: 显示资源作者信息、作业创建者信息

#### 3. 权限检查
- **接口**: `GET /api/user/{userId}/permission?permission={permission}`
- **用途**: 检查用户特定权限
- **调用场景**: 验证用户是否有权限访问或修改特定内容

#### 4. 批量获取用户信息
- **接口**: `POST /api/user/batch`
- **用途**: 批量获取多个用户信息
- **调用场景**: 显示作业提交者列表、讨论参与者列表

#### 5. 角色验证
- **接口**: `GET /api/user/{userId}/is-teacher`
- **接口**: `GET /api/user/{userId}/is-student`
- **用途**: 验证用户是否为教师或学生
- **调用场景**: 作业管理权限控制、资源访问权限控制

#### 6. 获取用户基本信息
- **接口**: `GET /api/user/{userId}/basic`
- **用途**: 获取用户基本信息（姓名、头像等）
- **调用场景**: 显示用户头像、姓名等基本信息

#### 7. 验证用户登录状态
- **接口**: `POST /api/user/verify-token`
- **用途**: 验证用户token是否有效
- **调用场景**: 接口访问权限验证

## 与 Course Service 的接口关系

### Course Service 依赖接口 (端口: 8082)

Content Service 通过 Feign Client 调用 Course Service 的以下接口：

#### 1. 课程信息获取
- **接口**: `GET /api/course/{courseId}`
- **用途**: 获取课程基本信息
- **调用场景**: 创建教学资源时验证课程存在性

#### 2. 课程权限检查
- **接口**: `GET /api/course/{courseId}/user/{userId}/permission`
- **用途**: 检查用户对特定课程的权限
- **调用场景**: 验证用户是否有权限访问课程内容

#### 3. 课程成员管理
- **接口**: `GET /api/course/{courseId}/members`
- **用途**: 获取课程成员列表
- **调用场景**: 显示课程参与者、权限控制

#### 4. 章节信息验证
- **接口**: `GET /api/course/{courseId}/chapter/{chapterId}`
- **用途**: 验证章节是否存在
- **调用场景**: 创建教学资源时验证章节有效性

#### 5. 教师身份验证
- **接口**: `GET /api/course/{courseId}/teacher/{userId}`
- **用途**: 验证用户是否为课程教师
- **调用场景**: 作业管理、资源管理权限控制

#### 6. 学生身份验证
- **接口**: `GET /api/course/{courseId}/student/{userId}`
- **用途**: 验证用户是否为课程学生
- **调用场景**: 作业提交、资源访问权限控制

#### 7. 班级信息获取
- **接口**: `GET /api/class/{classId}`
- **用途**: 获取班级信息
- **调用场景**: 作业管理、学习进度跟踪

#### 8. 班级成员验证
- **接口**: `GET /api/class/{classId}/member/{userId}`
- **用途**: 验证用户是否为班级成员
- **调用场景**: 作业访问权限控制

#### 9. 课程章节列表
- **接口**: `GET /api/course/{courseId}/chapters`
- **用途**: 获取课程的所有章节
- **调用场景**: 资源分类、导航菜单

#### 10. 课程状态验证
- **接口**: `GET /api/course/{courseId}/status`
- **用途**: 验证课程是否处于活跃状态
- **调用场景**: 资源访问控制、作业管理

#### 11. 用户课程关系
- **接口**: `GET /api/user/{userId}/courses`
- **接口**: `GET /api/user/{userId}/teaching-courses`
- **用途**: 获取用户参与的课程或教授的课程
- **调用场景**: 个人中心、权限控制

## 核心业务功能

### 1. 作业管理 (Homework Management)
- 作业创建、编辑、删除
- 作业提交管理
- 作业文件上传下载
- 作业状态跟踪

### 2. 教学资源管理 (Teaching Resources)
- 教学资源上传、编辑、删除
- 资源分类管理
- 资源访问统计
- 文件存储管理

### 3. 讨论区管理 (Discussion)
- 讨论主题创建、回复
- 讨论内容管理
- 用户互动记录

### 4. 学习进度跟踪 (Learning Progress)
- 学习进度记录
- 学习时长统计
- 学习行为分析

### 5. 通知管理 (Notification)
- 系统通知发送
- 通知状态管理
- 用户通知偏好

## 技术架构

### 依赖技术
- **Spring Boot 3.2.0**: 主框架
- **MyBatis**: 数据访问层
- **MySQL**: 数据存储
- **OpenFeign**: 服务间通信
- **Spring Cloud**: 微服务框架

### 配置说明
- **数据库**: content-db (MySQL)
- **文件存储**: 本地存储 (可扩展为OSS)
- **服务发现**: 硬编码配置 (可扩展为Eureka)

## 部署说明

### 环境要求
- Java 17+
- MySQL 8.0+
- Maven 3.6+

### 启动命令
```bash
cd content-service
mvn spring-boot:run
```

### 配置文件
- `application.yml`: 主配置文件
- `sql/content.sql`: 数据库初始化脚本

## 接口测试

详细的接口测试命令请参考项目中的测试脚本或API文档。

## 注意事项

1. **服务依赖**: 启动前确保 User Service (8081) 和 Course Service (8082) 已启动
2. **数据库**: 确保 content-db 数据库已创建并执行初始化脚本
3. **权限控制**: 所有业务操作都需要验证用户权限和课程权限
4. **文件上传**: 支持多种文件格式，注意文件大小限制 (100MB)

