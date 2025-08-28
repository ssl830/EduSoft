# User Service (用户服务)
## 数据库表在sql\user.sql
## 概述
用户服务是EduSoft微服务架构中的核心服务之一，负责用户管理、认证授权等核心功能。

## 功能特性
- 用户注册与登录
- 用户信息管理（增删改查）
- 角色管理（学生、教师、导师）
- 密码加密与验证
- JWT Token认证
- 微服务注册与发现


### 配置数据库
1. 创建数据库 `courseplatform`
2. 修改 `application.yml` 中的数据库连接信息



### 服务端口
- 默认端口: 8081
- 健康检查: http://localhost:8081/actuator/health

## API接口

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

### 获取用户信息
```
GET /api/user/{userId}
Authorization: Bearer {token}
```

### 更新用户信息
```
PUT /api/user/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "新用户名",
  "email": "newemail@example.com"
}
```

### 删除用户
```
DELETE /api/user/{id}
Authorization: Bearer {token}
```

## 角色说明
- `student`: 学生用户
- `teacher`: 教师用户  
- `tutor`: 导师用户

## 安全配置
- 使用Sa-Token进行JWT认证
- 密码使用MD5+盐值加密
- 支持CSRF防护
- 接口权限控制

## 微服务集成
- 支持Eureka服务注册
- 提供OpenFeign客户端
- 支持服务间调用

## 部署说明
1. 确保Eureka Server已启动
2. 配置数据库连接
3. 启动用户服务
4. 验证服务注册状态

## 监控与健康检查
- 健康检查端点: `/actuator/health`
- 应用信息端点: `/actuator/info`
- 指标端点: `/actuator/metrics`

## 故障排除
- 检查数据库连接
- 验证Eureka Server状态
- 查看应用日志
- 检查端口占用情况

