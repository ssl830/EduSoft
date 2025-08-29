# 课程班级管理微服务

## 项目简介
课程班级管理微服务，负责处理课程、班级、学生等相关的业务逻辑。

## 主要功能
- 课程管理（增删改查）
- 班级管理（增删改查、学生加入/退出）
- 课程章节管理
- 学生导入管理
- 用户认证集成

## 技术栈
- Spring Boot 3.1.0
- MyBatis Plus 3.5.7
- MySQL 8.0
- Spring Cloud (已禁用)

## 快速开始

### 1. 环境要求
- Java 21
- MySQL 8.0+
- Maven 3.6+

### 2. 数据库配置
确保MySQL中已创建 `course_db` 数据库，并导入相应的表结构。

### 3. 配置文件
修改 `src/main/resources/application.yml` 中的数据库连接信息。

### 4. 启动服务
```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8082` 启动。

## 微服务通信

### 用户服务集成
本微服务与用户微服务集成，用于：
- 用户身份验证
- 获取用户信息
- 权限控制

### 认证方式
支持以下认证头格式：

1. **Authorization头（推荐）**：
   ```
   Authorization: Bearer {token}
   X-User-Id: {userId}
   ```

2. **satoken头**：
   ```
   satoken: {token}
   X-User-Id: {userId}
   ```

3. **Cookie方式**：
   ```
   Cookie: satoken={token}
   X-User-Id: {userId}
   ```

### 测试认证
在测试API时，请确保：

1. 先在用户微服务登录获取token
2. 在请求头中包含：
   - `Authorization: Bearer {你的token}`
   - `X-User-Id: {你的用户ID}`

### 绕过认证（仅用于测试）
如需跳过认证进行测试，可以：

1. **临时方式**：在请求头添加 `X-Bypass-Auth: true`
2. **全局方式**：在 `application.yml` 中设置 `auth.required: false`

## API文档

### 课程管理
- `POST /api/courses` - 创建课程
- `GET /api/courses/list` - 获取所有课程
- `GET /api/courses/{id}` - 获取课程详情
- `PUT /api/courses/{id}` - 更新课程
- `DELETE /api/courses/{id}` - 删除课程

### 班级管理
- `POST /api/classes` - 创建班级
- `GET /api/classes/{id}` - 获取班级详情
- `PUT /api/classes/{id}` - 更新班级
- `DELETE /api/classes/{id}` - 删除班级
- `POST /api/classes/{id}/join/{userId}` - 加入班级
- `DELETE /api/classes/{id}/leave/{userId}` - 离开班级

### 课程章节
- `POST /api/course-sections` - 创建章节
- `GET /api/course-sections/course/{courseId}` - 获取课程章节
- `PUT /api/course-sections/{id}` - 更新章节
- `DELETE /api/course-sections/{id}` - 删除章节

### 学生导入
- `POST /api/imports/students` - 批量导入学生
- `GET /api/imports/records/{classId}` - 获取导入记录

## 数据库表结构

### 主要表
- `course` - 课程信息
- `class` - 班级信息
- `courseclass` - 课程班级关联
- `coursesection` - 课程章节
- `classuser` - 班级用户关联
- `import_record` - 导入记录

## 部署

### Docker部署
```bash
docker build -t course-service .
docker run -p 8082:8082 course-service
```

### Kubernetes部署
参考 `kubernetes/course-service.yaml` 配置文件。

## 监控

### 健康检查
- `GET /actuator/health` - 服务健康状态

### 日志
日志级别可在 `application.yml` 中配置，支持DEBUG、INFO、WARN、ERROR级别。

## 故障排除

### 常见问题

1. **401 Unauthorized**
   - 检查认证头是否正确
   - 确认token是否有效
   - 验证用户ID是否存在

2. **数据库连接失败**
   - 检查MySQL服务是否启动
   - 验证数据库连接参数
   - 确认数据库是否存在

3. **用户服务通信失败**
   - 检查用户服务是否启动
   - 验证 `services.user.base-url` 配置
   - 确认网络连通性

### 调试模式
启用DEBUG日志：
```yaml
logging:
  level:
    org.example.edusoft: DEBUG
```

## 贡献
欢迎提交Issue和Pull Request。

## 许可证
本项目采用MIT许可证。
