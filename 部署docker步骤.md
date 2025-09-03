# EduSoft 微服务容器化部署指南

这是一个基于 Spring Boot 和 Docker 的教育软件微服务系统，包含用户管理、课程管理、学习管理和内容管理四个核心微服务。

## 🚀 系统架构

#### 第一步：编译所有微服务
```bash
# 进入项目根目录
cd EduSoft

# 编译 user-service
cd user-service
mvn clean package -DskipTests
cd ..

# 编译 course-service  
cd course-service
mvn clean package -DskipTests
cd ..

# 编译 learning-service
cd learning-service
mvn clean package -DskipTests
cd ..

# 编译 content-service
cd content-service
mvn clean package -DskipTests
cd ..
```

#### 第二步：构建前端项目
```bash
cd frontend
npm install
npm run build
cd ..
```

#### 第三步：启动容器
```bash
# 清理旧数据（可选）
docker-compose down -v

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps
```

## 🔍 验证部署

### 检查服务状态
```bash
# 查看所有容器状态
docker-compose ps

# 查看服务日志
docker-compose logs -f

# 检查数据库
docker exec edusoft-mysql-1 mysql -u root -pfkq200566 -e "SHOW DATABASES;"
```

### 健康检查
```bash
# 检查各微服务健康状态
curl http://localhost:8081/actuator/health  # user-service
curl http://localhost:8082/actuator/health  # course-service  
curl http://localhost:8083/actuator/health  # content-service
curl http://localhost:8084/actuator/health  # learning-service
```

### 访问应用
打开浏览器访问：http://localhost:3000

## 📊 服务端口说明

| 服务 | 端口 | 描述 |
|------|------|------|
| Frontend | 3000 | Vue.js 前端应用 |
| User Service | 8081 | 用户管理微服务 |
| Course Service | 8082 | 课程管理微服务 |
| Content Service | 8083 | 内容管理微服务 |
| Learning Service | 8084 | 学习管理微服务 |
| MySQL | 3307 | 数据库服务 |

## 🗄️ 数据库说明

系统自动创建以下数据库：

| 数据库名 | 用途 | 初始化脚本 |
|----------|------|------------|
| `user-db` | 用户服务 | `user-service/user-db.sql` |
| `course_db` | 课程服务 | `course-service/course_db.sql` |
| `learning_db` | 学习服务 | `learning-service/learning_db.sql` |
| `content-db` | 内容服务 | `content-service/content-db.sql` |

**数据库连接信息:**
- **主机**: localhost:3307
- **用户名**: root
- **密码**: fkq200566

## 🔧 常用管理命令

### 容器管理
```bash
# 启动所有服务
docker-compose up -d

# 停止所有服务
docker-compose down

# 重启特定服务
docker-compose restart [service-name]

# 查看特定服务日志
docker-compose logs [service-name]

# 实时查看日志
docker-compose logs -f [service-name]

# 重新构建并启动
docker-compose up -d --build
```

### 数据管理
```bash
# 停止服务并删除数据卷（重置数据库）
docker-compose down -v

# 备份数据库
docker exec edusoft-mysql-1 mysqldump -u root -pfkq200566 --all-databases > backup.sql

# 进入 MySQL 容器
docker exec -it edusoft-mysql-1 mysql -u root -pfkq200566
```

### 系统清理
```bash
# 清理停止的容器
docker container prune

# 清理未使用的镜像
docker image prune

# 清理所有未使用资源（谨慎使用）
docker system prune -a
```

## 🚨 故障排除

### 常见问题及解决方案

#### 1. 编译失败
```bash
# 检查 Java 版本
java -version  # 需要 JDK 11 或以上

# 清理 Maven 缓存
mvn clean

# 跳过测试重新编译
mvn clean package -DskipTests
```

#### 2. 端口冲突
修改 `docker-compose.yml` 中的端口映射：
```yaml
ports:
  - "3001:80"  # 将前端端口改为 3001
```

#### 3. 数据库连接失败
```bash
# 检查数据库容器状态
docker-compose logs mysql

# 重新初始化数据库
docker-compose down -v
docker-compose up -d mysql
```

#### 4. 服务启动失败
```bash
# 查看详细日志
docker-compose logs [service-name]

# 检查配置文件
# 确保 application.yml 中的数据库连接配置正确
```

#### 5. 前端无法访问后端
- 检查 nginx 配置是否正确
- 确认所有微服务都已启动
- 检查防火墙设置

### 日志位置
- **应用日志**: `docker-compose logs [service-name]`
- **Nginx 日志**: `docker-compose logs frontend`
- **MySQL 日志**: `docker-compose logs mysql`

## 🔐 安全配置

### 生产环境建议
1. **修改默认密码**
   - 数据库 root 密码
   - 应用配置中的密钥

2. **网络安全**
   - 配置防火墙规则
   - 使用 HTTPS
   - 限制数据库外部访问

3. **资源限制**
   - 为容器设置内存限制
   - 设置 CPU 使用限制

## 📈 性能优化

### 生产环境优化
```yaml
# 在 docker-compose.yml 中添加资源限制
services:
  user-service:
    deploy:
      resources:
        limits:
          memory: 512M
        reservations:
          memory: 256M
```

### 监控建议
- 使用 Docker 内置监控：`docker stats`
- 集成 Prometheus + Grafana
- 配置日志聚合系统

## 📝 更新日志

- **v1.0.0** - 初始版本，包含四个微服务的容器化部署
- **v1.1.0** - 添加 OSS 支持和讨论功能
- **v1.2.0** - 优化 nginx 配置，添加文件上传支持

## 🤝 贡献指南

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 技术支持

如果在部署过程中遇到问题，请：

1. 查看本文档的故障排除部分
2. 检查项目的 GitHub Issues
3. 联系技术支持团队

---

**祝您部署顺利！** 🎉
