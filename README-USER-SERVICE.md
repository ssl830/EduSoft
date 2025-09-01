# EduSoft 用户服务 (User Service)

这是EduSoft教育平台的用户服务微服务，负责用户认证、授权和用户信息管理。

## 📋 项目概述

**分支**: `micro_user_service`  
**服务**: 用户服务 (User Service)  
**端口**: 8081  
**数据库**: MySQL (user-db)  

## 🏗️ 项目结构

```
EduSoft/
├── user-service/              # 用户服务源码
│   ├── src/                  # Java源码
│   ├── Dockerfile            # Docker构建文件
│   ├── pom.xml              # Maven配置
│   └── test-api.http        # API测试文件
├── sql/                      # 数据库脚本
│   └── user.sql             # 用户数据库初始化脚本
├── k8s/                      # Kubernetes配置
│   └── user-service/        # 用户服务K8s部署配置
├── .github/workflows/        # GitHub Actions
│   └── user-service.yml     # 用户服务CI/CD流水线
└── scripts/                  # 部署脚本
    └── deploy-user-service.sh
```

## 🚀 快速开始

### 本地开发

1. **克隆项目**
   ```bash
   git clone https://github.com/ssl830/EduSoft.git
   cd EduSoft
   git checkout micro_user_service
   ```

2. **数据库准备**
   ```bash
   # 创建数据库
   mysql -u root -p < sql/user.sql
   ```

3. **启动服务**
   ```bash
   cd user-service
   mvn spring-boot:run
   ```

4. **访问服务**
   - 服务地址: http://localhost:8081
   - 健康检查: http://localhost:8081/actuator/health
   - API文档: http://localhost:8081/swagger-ui.html

### Docker部署

```bash
cd user-service
docker build -t edusoft-user-service .
docker run -p 8081:8081 edusoft-user-service
```

## 🛠️ 技术栈

- **后端框架**: Spring Boot 3.2.0
- **数据库**: MySQL 8.0
- **ORM**: MyBatis
- **认证**: Sa-Token
- **构建工具**: Maven 3.6+
- **Java版本**: JDK 17

## 📊 CI/CD 流水线

### GitHub Actions 工作流

`.github/workflows/user-service.yml` 包含完整的CI/CD流水线：

1. **代码质量检查** - 代码格式和质量检查
2. **单元测试** - 运行所有单元测试
3. **构建服务** - Maven打包
4. **Docker镜像** - 构建和推送Docker镜像
5. **安全扫描** - 漏洞扫描
6. **部署** - 自动部署到K8s集群

### 触发条件

- 推送到 `micro_user_service` 分支
- 创建Pull Request到 `micro_user_service` 分支

## ☸️ Kubernetes 部署

### 部署到K8s集群

```bash
# 应用K8s配置
kubectl apply -f k8s/user-service/

# 检查部署状态
kubectl get pods -n edusoft
kubectl get services -n edusoft
```

### 配置说明

- **Namespace**: `edusoft`
- **Service Type**: ClusterIP
- **Replicas**: 3个副本
- **Resources**: 512Mi内存，250m CPU请求
- **Health Check**: `/actuator/health`

## 🔧 环境配置

### 必需的环境变量

```bash
# 数据库配置
DB_USERNAME=root
DB_PASSWORD=your_password
DB_URL=jdbc:mysql://mysql-service:3306/user-db

# Sa-Token配置
SA_TOKEN_SECRET=your_jwt_secret
```

### GitHub Secrets 配置

在GitHub仓库中配置以下Secrets：

```
ACR_USERNAME          # 阿里云镜像仓库用户名
ACR_PASSWORD          # 阿里云镜像仓库密码
SSH_PRIVATE_KEY       # SSH私钥
DEV_HOST             # 开发环境主机地址
PROD_HOST_1          # 生产环境主机1
PROD_HOST_2          # 生产环境主机2
```

## 📚 API 文档

### 核心接口

| 接口 | 方法 | 路径 | 描述 |
|------|------|------|------|
| 用户注册 | POST | `/api/user/register` | 用户注册 |
| 用户登录 | POST | `/api/user/login` | 用户登录 |
| 获取用户信息 | GET | `/api/user/profile` | 获取当前用户信息 |
| 更新用户信息 | PUT | `/api/user/profile` | 更新用户信息 |
| 用户列表 | GET | `/api/user/list` | 获取用户列表 |
| 健康检查 | GET | `/actuator/health` | 服务健康检查 |

### 测试接口

使用 `user-service/test-api.http` 文件测试API接口。

## 🔍 监控和日志

### 健康检查

```bash
# 检查服务状态
curl http://localhost:8081/actuator/health

# 检查详细信息
curl http://localhost:8081/actuator/info
```

### 日志查看

```bash
# K8s环境日志
kubectl logs -f deployment/edusoft-user-service -n edusoft

# Docker环境日志
docker logs -f edusoft-user-service
```

## 🧪 测试

### 运行测试

```bash
cd user-service
mvn test
```

### 测试覆盖率

```bash
mvn test jacoco:report
```

## 🚀 部署指南

### 1. 开发环境部署

```bash
# 自动部署到开发环境
git push origin micro_user_service
```

### 2. 生产环境部署

```bash
# 创建Release触发生产部署
git tag v1.0.0
git push origin v1.0.0
```

### 3. 手动部署

```bash
# 使用部署脚本
./scripts/deploy-user-service.sh
```

## 🔒 安全配置

- JWT Token认证
- 数据库连接加密
- API访问限流
- Docker镜像安全扫描
- Network Policy网络隔离

## 📝 开发规范

1. 代码提交前运行 `mvn test`
2. 遵循Spring Boot最佳实践
3. API接口使用RESTful设计
4. 数据库操作使用事务管理
5. 敏感信息使用环境变量

## 🆘 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库连接配置
   - 确认数据库服务运行状态

2. **启动失败**
   - 检查端口占用情况
   - 查看应用日志

3. **认证失败**
   - 检查Sa-Token配置
   - 验证JWT密钥设置

## 🤝 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交更改
4. 创建Pull Request

## 📞 联系方式

- 项目维护者: psq2006
- 仓库地址: https://github.com/ssl830/EduSoft
- 分支: micro_user_service
