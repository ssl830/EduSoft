# Notification Service - 通知微服务

## 概述

Notification Service 是 content-service 中的一个核心微服务模块，负责处理系统中的所有通知相关功能。该服务与 user-service 和 course-service 进行通信，为用户提供完整的通知管理功能。

## 功能特性

### 1. 通知类型支持
- **课程通知** (COURSE_NOTICE): 课程相关的通知
- **练习通知** (PRACTICE_NOTICE): 在线练习相关通知
- **作业通知** (HOMEWORK): 作业发布和提交通知
- **任务通知** (TASK): 任务相关通知
- **DDL提醒** (DDL_REMINDER): 截止时间提醒
- **资源上传通知** (RESOURCE_UPLOAD): 新教学资源通知
- **讨论回复通知** (DISCUSSION_REPLY): 讨论区回复通知
- **系统通知** (SYSTEM): 系统级别通知
- **成绩发布通知** (GRADE_PUBLISH): 成绩发布通知
- **公告通知** (ANNOUNCEMENT): 班级公告通知

### 2. 核心功能
- 通知的创建、查询、更新、删除
- 批量通知发送
- 通知状态管理（已读/未读）
- 通知优先级管理
- 通知搜索和过滤
- 分页查询支持
- 通知统计信息
- 过期通知清理

### 3. 微服务通信
- 与 user-service 通信获取用户信息
- 与 course-service 通信获取课程和班级信息
- 支持 Feign 客户端调用

## API 接口

### 基础通知接口

#### 获取用户通知
```
GET /api/notifications
GET /api/notifications/paged?page=1&size=10
```

#### 获取特定类型通知
```
GET /api/notifications/type/{type}
GET /api/notifications/course/{courseId}
GET /api/notifications/class/{classId}
```

#### 通知状态管理
```
PUT /api/notifications/{id}/read
PUT /api/notifications/read-all
```

#### 通知搜索
```
GET /api/notifications/search?keyword={keyword}
```

#### 通知统计
```
GET /api/notifications/stats
```

### 批量通知发送接口

#### 发送系统通知
```
POST /api/notifications/system
{
    "title": "系统维护通知",
    "message": "系统将于今晚进行维护",
    "userIds": [1, 2, 3]
}
```

#### 发送课程通知
```
POST /api/notifications/course
{
    "title": "课程安排变更",
    "message": "下周三的课程时间有调整",
    "courseId": 123,
    "userIds": [1, 2, 3]
}
```

#### 发送班级通知
```
POST /api/notifications/class
{
    "title": "班级活动通知",
    "message": "本周五有班级聚会活动",
    "classId": 456,
    "userIds": [1, 2, 3]
}
```

### 管理接口

#### 删除通知
```
DELETE /api/notifications/{id}
```

#### 清理过期通知
```
DELETE /api/notifications/cleanup?daysToKeep=30
```

## 数据库结构

### notification 表
```sql
CREATE TABLE notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(200) COMMENT '通知标题',
    message TEXT COMMENT '通知内容',
    type VARCHAR(20) NOT NULL COMMENT '通知类型',
    read_flag TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    related_id BIGINT COMMENT '关联ID',
    related_type VARCHAR(50) COMMENT '关联类型',
    priority VARCHAR(20) DEFAULT 'normal' COMMENT '优先级',
    status VARCHAR(20) DEFAULT 'active' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    sender_name VARCHAR(100) COMMENT '发送者姓名',
    sender_id BIGINT COMMENT '发送者ID',
    course_name VARCHAR(200) COMMENT '课程名称',
    course_id BIGINT COMMENT '课程ID',
    class_name VARCHAR(200) COMMENT '班级名称',
    class_id BIGINT COMMENT '班级ID'
);
```

## 使用示例

### 1. 在业务服务中使用 NotificationUtils

```java
@Autowired
private NotificationUtils notificationUtils;

// 创建作业通知
public void publishHomework(Homework homework) {
    // 获取班级学生列表
    List<Long> studentIds = getClassStudentIds(homework.getClassId());
    
    // 发送通知
    notificationUtils.createHomeworkNotification(
        homework.getId(),
        homework.getTitle(),
        homework.getCourseId(),
        homework.getClassId(),
        studentIds
    );
}

// 创建DDL提醒
public void sendDDLReminder(Task task) {
    List<Long> studentIds = getTaskStudentIds(task.getId());
    
    notificationUtils.createDDLReminderNotification(
        task.getId(),
        task.getTitle(),
        task.getCourseId(),
        task.getClassId(),
        studentIds,
        task.getDeadline().toString()
    );
}
```

### 2. 直接使用 NotificationService

```java
@Autowired
private NotificationService notificationService;

// 发送系统通知
public void sendSystemMaintenanceNotice() {
    List<Long> allUserIds = getAllUserIds();
    notificationService.sendSystemNotification(
        "系统维护通知",
        "系统将于今晚22:00-24:00进行维护升级",
        allUserIds
    );
}

// 发送课程通知
public void sendCourseScheduleChange(Long courseId) {
    List<Long> studentIds = getCourseStudentIds(courseId);
    notificationService.sendCourseNotification(
        "课程时间调整",
        "下周三的课程时间调整为14:00-16:00",
        courseId,
        studentIds
    );
}
```

## 配置说明

### 微服务通信配置
```yaml
# 微服务配置
user:
  service:
    url: http://localhost:8081

course:
  service:
    url: http://localhost:8082
```

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/content-db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: fkq200566
    driver-class-name: com.mysql.cj.jdbc.Driver
```

## 注意事项

1. **异常处理**: 微服务通信失败时，通知服务会记录日志但不抛出异常，避免影响主业务流程
2. **性能优化**: 支持批量通知创建，减少数据库操作次数
3. **数据一致性**: 使用事务确保通知数据的完整性
4. **扩展性**: 支持新的通知类型和业务场景
5. **监控**: 提供通知统计信息，便于监控和分析

## 依赖服务

- **user-service**: 用户信息验证和获取
- **course-service**: 课程和班级信息获取
- **MySQL**: 通知数据存储
- **Spring Boot**: 应用框架
- **MyBatis**: 数据访问层
- **Feign**: 微服务通信客户端

## 部署说明

1. 确保 MySQL 数据库已启动并创建了相应的表结构
2. 配置正确的微服务通信地址
3. 启动 content-service 应用
4. 验证通知服务接口是否正常响应

## 测试接口

可以使用以下 HTTP 文件进行接口测试：

```http
### 获取用户通知
GET http://localhost:8083/api/notifications
Authorization: Bearer {token}

### 发送系统通知
POST http://localhost:8083/api/notifications/system
Content-Type: application/json

{
    "title": "测试通知",
    "message": "这是一个测试通知",
    "userIds": [1, 2, 3]
}
```

