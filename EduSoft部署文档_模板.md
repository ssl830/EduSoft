# EduSoft 在线教育平台部署文档

## 部署说明
我们小组为EduSoft在线教育平台租赁了一台Ubuntu云服务器，故采用云端部署的形式运行项目。项目采用前后端分离架构，后端使用Spring Boot，前端使用Vue.js，数据库使用MySQL 8.0，并集成了阿里云OSS文件存储服务。访问 http://62.234.213.182 即可使用平台。

## 部署环境要求
我们项目对服务器性能有一定要求，需要支持Java 17环境和MySQL数据库。我们小组租赁的是Ubuntu 22.04 LTS云服务器，配置了完整的Spring Boot + Vue.js技术栈。

## 服务器配置
- **操作系统**：Ubuntu 22.04.3 LTS x86_64
- **CPU&内存**：2核(vCPU) 4 GiB  
- **处理器型号**：Intel(R) Xeon(R) CPU
- **带宽**：5Mbps带宽及以上网络适配器
- **磁盘容量**：50 GiB及以上
- **Java版本**：OpenJDK 17.0.7及以上
- **Nginx版本**：1.18.0及以上
- **数据库版本**：MySQL 8.0.34及以上
- **Node.js版本**：v18.20.8及以上
- **Maven版本**：3.6.3及以上

## 部署步骤

### 服务器设置
为了便于部署项目，我们配置了完整的Linux环境，首先更新系统并安装必要组件：

```bash
# 更新系统
sudo apt update && sudo apt upgrade -y

# 安装Java 17
sudo apt install openjdk-17-jdk -y

# 安装MySQL 8.0
sudo apt install mysql-server -y

# 安装Nginx
sudo apt install nginx -y
```

配置MySQL数据库，创建项目所需的数据库和用户：

```sql
# 登录MySQL
sudo mysql -u root -p

# 创建数据库
CREATE DATABASE courseplatform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 创建用户
CREATE USER 'edusoft'@'localhost' IDENTIFIED BY 'EduSoft@2025';

# 授权
GRANT ALL PRIVILEGES ON courseplatform.* TO 'edusoft'@'localhost';
FLUSH PRIVILEGES;
```

我们的项目需要阿里云OSS存储服务，需要配置相应的环境变量：

```bash
# 配置阿里云OSS环境变量
export ALIYUN_ACCESS_KEY_ID="your_access_key_id"
export ALIYUN_ACCESS_KEY_SECRET="your_access_key_secret"
export ALIYUN_OSS_BUCKET="your_bucket_name"
export ALIYUN_OSS_ENDPOINT="your_endpoint"
```

### 后端部署
我们项目后端基于Spring Boot框架，需要Java 17环境。部署过程如下：

首先，在本地构建项目：
```bash
# 构建后端项目
mvn clean package -DskipTests
```

![后端构建过程](https://via.placeholder.com/600x300?text=Maven+Build+Process)

接下来将构建好的JAR文件上传到服务器：
```bash
# 上传JAR文件到服务器
scp target/EduSoft-0.0.1-SNAPSHOT.jar ubuntu@62.234.213.182:/opt/edusoft/
```

在服务器上配置生产环境配置文件 `/opt/edusoft/application-prod.yml`：
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/courseplatform?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: edusoft
    password: EduSoft@2025
    driver-class-name: com.mysql.cj.jdbc.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
    database-platform: org.hibernate.dialect.MySQL8Dialect

logging:
  level:
    root: INFO
  file:
    name: /opt/edusoft/logs/backend.log
```

创建systemd服务配置 `/etc/systemd/system/edusoft-backend.service`：
```ini
[Unit]
Description=EduSoft Backend Service
After=network.target mysql.service

[Service]
Type=simple
User=ubuntu
Group=ubuntu
WorkingDirectory=/opt/edusoft
ExecStart=/usr/bin/java -jar /opt/edusoft/EduSoft-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
Restart=always
RestartSec=10

Environment="ALIYUN_ACCESS_KEY_ID=your_access_key_id"
Environment="ALIYUN_ACCESS_KEY_SECRET=your_access_key_secret"
Environment="ALIYUN_OSS_BUCKET=your_bucket_name"
Environment="ALIYUN_OSS_ENDPOINT=your_endpoint"

[Install]
WantedBy=multi-user.target
```

启动后端服务：
```bash
sudo systemctl daemon-reload
sudo systemctl enable edusoft-backend
sudo systemctl start edusoft-backend
```

![后端服务运行状态](https://via.placeholder.com/600x200?text=Backend+Service+Running)

### 前端部署
前端部署相对简单，我们首先需要在本地构建前端项目。

在本地项目目录下执行构建命令：
```bash
cd frontend/project
npm install
npm run build
```

这时会在目录下生成一个 `dist` 文件夹，包含所有编译后的静态文件。

![前端构建过程](https://via.placeholder.com/600x300?text=Frontend+Build+Process)

然后将该文件夹上传到服务器的 `/var/www/html/edusoft` 目录下：
```bash
# 上传前端文件
scp -r frontend/project/dist/* ubuntu@62.234.213.182:/var/www/html/edusoft/
```

配置Nginx虚拟主机 `/etc/nginx/sites-available/edusoft`：
```nginx
server {
    listen 80;
    server_name 62.234.213.182;
    
    # 静态文件根目录
    root /var/www/html/edusoft;
    index index.html;
    
    # 前端路由（SPA支持）
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    # API代理到后端
    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

启用Nginx配置：
```bash
sudo ln -sf /etc/nginx/sites-available/edusoft /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx
```

![Nginx配置界面](https://via.placeholder.com/600x400?text=Nginx+Configuration)

Nginx会默认将网站部署到80端口，访问服务器IP地址 http://62.234.213.182 即可看到我们的项目。

### 自动化部署脚本
为了简化部署流程，我们开发了自动化部署脚本 `deploy.ps1`：

```powershell
# 完整自动化部署
.\deploy.ps1

# 跳过构建直接部署
.\deploy.ps1 -SkipBuild

# 指定服务器IP
.\deploy.ps1 -ServerIP "62.234.213.182"
```

该脚本可以自动完成以下步骤：
1. 检查本地环境（Java、Maven、Node.js、SSH）
2. 构建后端和前端项目
3. 创建部署包并上传到服务器
4. 在服务器上自动配置和启动服务
5. 验证部署结果

![自动化部署过程](https://via.placeholder.com/600x350?text=Automated+Deployment+Process)

这样我们的部署就完成了！

## 功能验证
部署完成后，可以通过以下方式验证各个功能模块：

- **用户管理**：注册、登录、个人信息管理
- **课程管理**：创建课程、加入课程、课程内容管理
- **在线练习**：练习发布、在线答题、自动评分
- **讨论区**：发布讨论、回复评论、点赞功能
- **文件管理**：课件上传下载、作业提交

![系统功能界面](https://via.placeholder.com/600x400?text=System+Features+Overview)

## 权重分配

| 姓名 | 负责模块 | 权重 |
|------|----------|------|
| 开发者A | 后端开发、数据库设计 | 1 |
| 开发者B | 前端开发、UI设计 | 1 |
| 开发者C | 系统部署、运维配置 | 1 |
| 开发者D | 测试验证、文档编写 | 1 |
| 开发者E | 项目管理、整体协调 | 1 |

## 组员签字

**项目负责人**：________________  
**后端开发**：________________  
**前端开发**：________________  
**运维部署**：________________  
**测试文档**：________________  

**部署完成日期**：2024年12月8日  
**验收确认**：________________
