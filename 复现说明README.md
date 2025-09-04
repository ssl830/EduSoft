# EduSoft 平台复现说明（Kubernetes & Docker Compose & 源码容器化）

本说明文档帮助你在本地或服务器环境下完整复现 EduSoft 教育平台，包括源码、微服务、容器化部署及测试。

---

## 方法一：Kubernetes 部署（推荐生产环境）

### 1. 环境准备
- 推荐操作系统：Linux/Windows 10+（需支持 Docker 和 K8s）
- 需安装：
  - Docker
  - Kubernetes（本地推荐 minikube/kind，生产可用 K8s 集群）
  - kubectl 命令行工具

### 2. 镜像拉取
- 依次执行以下命令拉取所有服务镜像：
  ```bash
  docker pull fankeqing/edusoft-frontend:latest
  docker pull fankeqing/edusoft-user-service:latest
  docker pull fankeqing/edusoft-course-service:latest
  docker pull fankeqing/edusoft-content-service:latest
  docker pull fankeqing/edusoft-learning-service:latest
  docker pull fankeqing/edusoft-ai-service:latest
  docker pull fankeqing/edusoft-mysql:8.0
  ```

### 3. K8s 部署步骤
1. 创建命名空间（可选）：
   ```bash
   kubectl create namespace edusoft
   ```
2. 部署数据库和各微服务：
   ```bash
   kubectl apply -f k8s/mysql-deployment.yaml
   kubectl apply -f k8s/ai-deployment.yaml
   kubectl apply -f k8s/user-deployment.yaml
   kubectl apply -f k8s/course-deployment.yaml
   kubectl apply -f k8s/content-deployment.yaml
   kubectl apply -f k8s/learning-deployment.yaml
   kubectl apply -f k8s/frontend-deployment.yaml
   ```
3. 检查服务状态：
   ```bash
   kubectl get pods -n edusoft
   kubectl get svc -n edusoft
   ```

### 4. 自动扩缩容配置与命令
- 已在 k8s/hpa.yaml 文件中配置所有微服务的自动扩缩容（HPA），无需单独编写。
- 应用 HPA 配置：
  ```bash
  kubectl apply -f k8s/hpa.yaml
  ```
- 手动扩容/缩容命令（如需）：
  ```bash
  kubectl scale deployment <服务名> --replicas=3 -n edusoft
  ```

### 6. 测试用例运行
- 各微服务目录下有测试脚本或用例，参考 README 或直接运行：
  ```bash
  mvn test
  # 或
  npm run test
  ```

---

## 方法二：Docker 镜像手动部署（推荐本地开发/快速体验）

本方法适用于已从 Docker Hub 拉取镜像后，直接用 docker run 命令手动部署各服务，无需 docker-compose.yml 文件。

## 📦 前提条件
确保你已经从 Docker Hub 拉取了以下 7 个镜像：
```bash
docker pull fankeqing/edusoft:mysql
docker pull fankeqing/edusoft:user-service  
docker pull fankeqing/edusoft:course-service
docker pull fankeqing/edusoft:content-service
docker pull fankeqing/edusoft:learning-service
docker pull fankeqing/edusoft:ai-service
docker pull fankeqing/edusoft:frontend
```

## 🚀 容器创建和运行指南

### 第一步：创建网络
```bash
docker network create edusoft-network
```

### 第二步：按顺序启动容器

#### 1. 启动 MySQL 数据库（必须第一个启动）
```bash
docker run -d --name edusoft-mysql --network edusoft-network -p 3307:3306 -e MYSQL_ROOT_PASSWORD=fkq200566 -v edusoft-mysql-data:/var/lib/mysql --restart unless-stopped fankeqing/edusoft:mysql
sleep 60
```

#### 1.1 数据库建库
```bash
Get-Content ./user-service/user-db.sql | docker exec -i edusoft-mysql mysql -u root -pfkq200566
Get-Content ./course-service/course_db.sql | docker exec -i edusoft-mysql mysql -u root -pfkq200566
Get-Content ./content-service/content-db.sql | docker exec -i edusoft-mysql mysql -u root -pfkq200566
Get-Content ./learning-service/learning_db.sql | docker exec -i edusoft-mysql mysql -u root -pfkq200566
```

#### 2. 启动用户服务
```bash
docker run -d --name user-service --network edusoft-network -p 8081:8081 -e SPRING_DATASOURCE_URL="jdbc:mysql://edusoft-mysql:3306/user-db?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=fkq200566 --restart unless-stopped fankeqing/edusoft:user-service
sleep 30
```

#### 3. 启动课程服务
```bash
docker run -d --name course-service --network edusoft-network -p 8082:8082 -e SPRING_DATASOURCE_URL="jdbc:mysql://edusoft-mysql:3306/course_db?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=fkq200566 -e SERVICES_USER_BASE_URL=http://user-service:8081 --restart unless-stopped fankeqing/edusoft:course-service
sleep 30
```

#### 4. 启动内容服务
```bash
docker run -d --name content-service --network edusoft-network -p 8083:8083 -e SPRING_DATASOURCE_URL="jdbc:mysql://edusoft-mysql:3306/content-db?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=fkq200566 -e SERVICES_USER_BASE_URL=http://user-service:8081 -e SERVICES_COURSE_URL=http://course-service:8082 --restart unless-stopped fankeqing/edusoft:content-service
sleep 30
```

#### 5. 启动学习服务
```bash
docker run -d --name learning-service --network edusoft-network -p 8084:8084 -e SPRING_DATASOURCE_URL="jdbc:mysql://edusoft-mysql:3306/learning_db?useUnicode=true&characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Shanghai" -e SPRING_DATASOURCE_USERNAME=root -e SPRING_DATASOURCE_PASSWORD=fkq200566 -e SERVICES_USER_BASE_URL=http://user-service:8081 -e SERVICES_COURSE_URL=http://course-service:8082 --restart unless-stopped fankeqing/edusoft:learning-service
sleep 30
```

#### 6. 启动 AI 服务
```bash
docker run -d --name ai-service --network edusoft-network -p 8085:8000 --restart unless-stopped fankeqing/edusoft:ai-service
sleep 20
```

#### 7. 启动前端服务（最后启动）
```bash
docker run -d --name frontend --network edusoft-network -p 3000:80 --restart unless-stopped fankeqing/edusoft:frontend
```

---

## 方法三：从源码编译并容器化部署（进阶开发者方案）

本方法适用于开发者从头编译源码、构建镜像，并用 docker-compose.yml 进行本地部署。

### 1. 克隆源码
   ```bash
   git clone https://github.com/ssl830/EduSoft.git
   cd EduSoft
   ```

### 2. 编译与构建
- 进入对应微服务目录，编译源码：
  - Java 微服务（如 user-service、course-service、content-service、learning-service）：
    ```bash
    cd user-service
    mvn clean package -DskipTests
    # 生成 target/*.jar 文件
    ```
    其他 Java 服务同理，分别进入各自目录编译。
  - Python/FastAPI 服务（如 ai_service）：
    ```bash
    cd ai_service
    pip install -r requirements.txt
    # 可用 python main.py 验证本地运行
    ```
- 前端项目编译（如 frontend）：
  ```bash
  cd frontend
  npm install
  npm run build
  # 生成 dist/ 目录用于容器化
  ```

### 3. 构建 Docker 镜像
- 每个服务目录下通常有 Dockerfile，执行：
  ```bash
  docker build -t fankeqing/edusoft-user-service:dev .
  docker build -t fankeqing/edusoft-ai-service:dev .
  # 其他服务同理
  ```

### 4. 使用 docker-compose.yml 部署自定义镜像
- 编辑 `docker-compose.yml`，将镜像名改为你本地构建的镜像（如 fankeqing/edusoft-user-service:dev）。
- 启动服务：
  ```bash
  docker compose up -d
  ```

### 5. 本地验证与推送
- 验证镜像：
  ```bash
  docker images
  docker run --rm -p 8080:8080 fankeqing/edusoft-user-service:dev
  ```
- 推送镜像到远程仓库（如需）：
  ```bash
  docker login
  docker tag fankeqing/edusoft-user-service:dev fankeqing/edusoft-user-service:latest
  docker push fankeqing/edusoft-user-service:latest
  ```

---

如有疑问请联系项目负责人。
