# Course Service (课程班级管理微服务)

## 项目简介

课程班级管理微服务是EduSoft教育平台的核心服务之一，负责管理课程、班级、章节等相关功能。

## 功能特性

### 课程管理
- 创建、更新、删除课程
- 获取课程列表和详情
- 课程章节管理
- 课程统计信息

### 班级管理
- 创建、更新、删除班级
- 学生加入/退出班级
- 班级成员管理
- 通过班级代码加入班级
- 批量导入学生

### 章节管理
- 创建、更新、删除课程章节
- 章节排序管理

### 学生导入管理
- 批量导入学生到班级
- 手动添加单个学生
- 通过班级代码加入班级
- 导入记录管理和查询

## 技术栈

- Spring Boot 3.2.0
- Spring Cloud 2023.0.0
- MyBatis Plus 3.5.4
- MySQL 8.0
- Maven

## 快速开始

### 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 安装步骤

1. 克隆项目
```bash
git clone <repository-url>
cd course-service
```

2. 配置数据库
- 确保MySQL服务已启动
- 创建数据库：`courseplatform`
- 导入SQL文件：`courseplatfoem.sql`

3. 修改配置
编辑 `src/main/resources/application.yml`，修改数据库连接信息：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/courseplatform?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&useSSL=false&zeroDateTimeBehavior=convertToNull&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

4. 编译运行
```bash
mvn clean package
java -jar target/course-service-1.0.0.jar
```

5. 访问服务
服务启动后，访问地址：http://localhost:8082

## API文档

### 课程相关接口

#### 创建课程
```
POST /api/courses
Content-Type: application/json

{
    "teacherId": 1,
    "name": "数据库原理",
    "code": "CS101",
    "outline": "介绍关系数据库",
    "objective": "掌握SQL",
    "assessment": "期末考试+作业"
}
```

#### 获取用户课程列表
```
GET /api/courses/user/{userId}
```

#### 获取课程详情
```
GET /api/courses/{id}
```

#### 更新课程
```
PUT /api/courses/{id}
Content-Type: application/json

{
    "name": "新课程名称",
    "code": "NEW101",
    "outline": "新大纲",
    "objective": "新目标",
    "assessment": "新考核方式"
}
```

#### 删除课程
```
DELETE /api/courses/{id}
```

### 班级相关接口

#### 创建班级
```
POST /api/classes
Content-Type: application/json

{
    "courseId": 1,
    "name": "数据库-01班",
    "classCode": "DBCLASS01"
}
```

#### 获取用户班级列表
```
GET /api/classes/user/{userId}
```

#### 获取班级详情
```
GET /api/classes/{id}
```

#### 学生加入班级
```
POST /api/classes/{classId}/join/{userId}
```

#### 学生退出班级
```
DELETE /api/classes/{classId}/leave/{userId}
```

#### 通过班级代码加入班级
```
POST /api/classes/join?classCode=DBCLASS01&studentId=1
```

#### 获取班级成员列表
```
GET /api/classes/{classId}/users
```

#### 批量导入学生
```
POST /api/classes/{classId}/import
Content-Type: application/json

[1, 2, 3, 4, 5]
```

### 章节相关接口

#### 获取课程章节列表
```
GET /api/course-sections/course/{courseId}
```

#### 创建章节
```
POST /api/course-sections
Content-Type: application/json

{
    "courseId": 1,
    "title": "第一章 数据库基础",
    "sortOrder": 1
}
```

#### 更新章节
```
PUT /api/course-sections/{id}
Content-Type: application/json

{
    "title": "新章节标题",
    "sortOrder": 2
}
```

#### 删除章节
```
DELETE /api/course-sections/{id}
```

### 学生导入相关接口

#### 统一导入学生接口
```
POST /api/imports/students
Content-Type: application/json

{
    "classId": 1,
    "operatorId": 1,
    "importType": "MANUAL",
    "fileName": "students.csv",
    "studentData": [
        {
            "student_id": "1"
        },
        {
            "student_id": "2"
        }
    ]
}
```

#### 获取班级导入记录列表
```
GET /api/imports/records/{classId}
```

#### 获取导入记录详情
```
GET /api/imports/record/{id}
```

## 数据库表结构

### Course表
- id: 主键
- teacher_id: 教师ID
- name: 课程名称
- code: 课程代码
- outline: 课程大纲
- objective: 教学目标
- assessment: 考核方式
- created_at: 创建时间

### Class表
- id: 主键
- course_id: 课程ID
- name: 班级名称
- class_code: 班级代码

### ClassUser表
- class_id: 班级ID
- user_id: 用户ID
- joined_at: 加入时间

### CourseSection表
- id: 主键
- course_id: 课程ID
- title: 章节标题
- sort_order: 排序

### ImportRecord表
- id: 主键
- class_id: 班级ID
- operator_id: 操作人ID
- file_name: 导入文件名
- total_count: 总记录数
- success_count: 成功导入数
- fail_count: 失败数
- fail_reason: 失败原因
- import_time: 导入时间
- import_type: 导入类型

## 部署

### Docker部署
```bash
# 构建镜像
docker build -t course-service .

# 运行容器
docker run -d -p 8082:8082 --name course-service course-service
```

### Kubernetes部署
```bash
kubectl apply -f kubernetes/course-service.yaml
```

## 监控和日志

服务启动后，可以通过以下方式查看日志：
```bash
# 查看应用日志
tail -f logs/course-service.log

# 查看错误日志
tail -f logs/error.log
```

## 常见问题

### 1. 数据库连接失败
- 检查MySQL服务是否启动
- 确认数据库连接信息是否正确
- 检查防火墙设置

### 2. 端口被占用
- 修改 `application.yml` 中的端口配置
- 或者停止占用端口的进程

### 3. 依赖下载失败
- 检查网络连接
- 配置Maven镜像源
- 清理Maven缓存：`mvn clean`

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交更改
4. 推送到分支
5. 创建 Pull Request

## 许可证

本项目采用 MIT 许可证。
