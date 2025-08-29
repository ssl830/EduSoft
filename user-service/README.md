# 用户服务 (User Service)

用户服务是微服务架构中的核心服务，负责用户管理、认证和授权等功能。

## 功能特性

- 用户注册和登录
- 用户信息管理
- 基于Sa-Token的JWT认证
- 角色权限管理
- 密码加密存储

## 技术栈

- Spring Boot 3.2.0
- Sa-Token (认证框架)
- MyBatis
- MySQL
- Maven

## 快速开始

### 环境要求

- Java 17+
- Maven 3.6+
- MySQL 8.0+

### 配置数据库

1. 创建数据库：
```sql
CREATE DATABASE user-db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `application.yml` 中的数据库连接信息

### 启动服务

```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8081` 启动

## API 接口文档

### 用户注册
```
POST /api/user/register
Content-Type: application/json

{
  "userId": "user123",
  "username": "张三",
  "passwordHash": "password123",
  "role": "student",
  "email": "user@example.com"
}
```

### 用户登录
```
POST /api/user/login
Content-Type: application/x-www-form-urlencoded

userId=user123&password=password123
```

**响应示例：**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "da60c9e2-60cf-4a4a-93f2-354ce0d87240",
    "userInfo": {
      "id": 11,
      "username": "张三",
      "userId": "user123",
      "email": "user@example.com",
      "role": "student"
    }
  }
}
```

### 获取用户信息
```
GET /api/user/{userId}?satoken={token}
```

**重要说明：** 认证token通过URL参数 `satoken` 传递，而不是Authorization头部！

**示例：**
```bash
# PowerShell
Invoke-RestMethod -Uri "http://localhost:8081/api/user/23371522?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" -Method GET

# curl
curl "http://localhost:8081/api/user/23371522?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240"
```

### 获取当前登录用户信息
```
GET /api/user/info?satoken={token}
```

**示例：**
```bash
Invoke-RestMethod -Uri "http://localhost:8081/api/user/info?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" -Method GET
```

### 更新用户信息
```
PUT /api/user/{userId}?satoken={token}
Content-Type: application/json

{
  "username": "新用户名",
  "email": "newemail@example.com"
}
```

**示例：**
```bash
Invoke-RestMethod -Uri "http://localhost:8081/api/user/11?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" `
  -Method PUT `
  -Headers @{"Content-Type"="application/json"} `
  -Body '{"username":"新用户名","email":"newemail@example.com"}'
```

### 修改密码
```
POST /api/user/changePassword?satoken={token}
Content-Type: application/x-www-form-urlencoded

oldPassword=oldpass&newPassword=newpass
```

**示例：**
```bash
Invoke-RestMethod -Uri "http://localhost:8081/api/user/changePassword?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" `
  -Method POST `
  -Headers @{"Content-Type"="application/x-www-form-urlencoded"} `
  -Body "oldPassword=999999&newPassword=123456"
```

### 用户登出
```
POST /api/user/logout?satoken={token}
```

**示例：**
```bash
Invoke-RestMethod -Uri "http://localhost:8081/api/user/logout?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" -Method POST
```

### 注销账号
```
POST /api/user/deactivate?satoken={token}
Content-Type: application/x-www-form-urlencoded

password=userpassword
```

**示例：**
```bash
Invoke-RestMethod -Uri "http://localhost:8081/api/user/deactivate?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" `
  -Method POST `
  -Headers @{"Content-Type"="application/x-www-form-urlencoded"} `
  -Body "password=999999"
```

### 删除用户
```
DELETE /api/user/{id}?satoken={token}
```

**示例：**
```bash
Invoke-RestMethod -Uri "http://localhost:8081/api/user/11?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" -Method DELETE
```

### 获取用户列表
```
GET /api/user/teachers?satoken={token}
GET /api/user/students?satoken={token}
GET /api/user/tutor?satoken={token}
```

**示例：**
```bash
# 获取所有教师
Invoke-RestMethod -Uri "http://localhost:8081/api/user/teachers?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" -Method GET

# 获取所有学生
Invoke-RestMethod -Uri "http://localhost:8081/api/user/students?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" -Method GET

# 获取所有导师
Invoke-RestMethod -Uri "http://localhost:8081/api/user/tutor?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240" -Method GET
```

## 认证说明

### Token传递方式
**重要：** 本服务使用Sa-Token框架，token通过URL参数 `satoken` 传递，而不是传统的 `Authorization: Bearer {token}` 头部！

**正确方式：**
```bash
# ✅ 正确 - 使用URL参数
GET /api/user/23371522?satoken=da60c9e2-60cf-4a4a-93f2-354ce0d87240

# ❌ 错误 - 使用Authorization头部
GET /api/user/23371522
Authorization: Bearer da60c9e2-60cf-4a4a-93f2-354ce0d87240
```

### 认证流程
1. 调用登录接口获取token
2. 在后续请求中通过 `satoken` 参数传递token
3. 除了注册和登录接口，其他接口都需要有效的token
4. Token有效期为30天

## 错误码说明

- 200: 成功
- 400: 请求参数错误
- 401: 未认证（token无效或过期）
- 403: 无权限
- 404: 资源不存在
- 500: 服务器内部错误

## 常见问题

### Q: 为什么使用satoken参数而不是Authorization头部？
A: 本服务使用Sa-Token框架，默认从URL参数中读取token。这是Sa-Token的设计特点，简化了token传递方式。

### Q: Token过期了怎么办？
A: 重新调用登录接口获取新的token。

### Q: 如何检查token是否有效？
A: 调用任意需要认证的接口，如果返回401错误说明token无效。

## 开发说明

### 项目结构
```
src/main/java/org/example/edusoft/user/
├── config/          # 配置类
├── controller/      # 控制器
├── dto/            # 数据传输对象
├── entity/         # 实体类
├── mapper/         # MyBatis映射器
├── service/        # 业务逻辑层
└── exception/      # 异常处理
```

### 添加新接口

1. 在 `UserController` 中添加新的映射方法
2. 在 `UserService` 中实现业务逻辑
3. 在 `UserMapper` 中添加数据库操作
4. 更新API文档

## 部署

### Docker部署

```bash
# 构建镜像
docker build -t user-service .

# 运行容器
docker run -p 8081:8081 user-service
```

### 传统部署

```bash
# 打包
mvn clean package

# 运行
java -jar target/user-service-1.0.0.jar
```

