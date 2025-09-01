# 云主机配置指南

## 🏗️ 单主机配置（当前推荐）

如果您目前只使用一台云主机，配置如下GitHub Secrets：

```
SSH_PRIVATE_KEY    # SSH私钥
SERVER_HOST        # 您的云主机IP地址
ACR_USERNAME       # 阿里云镜像仓库用户名
ACR_PASSWORD       # 阿里云镜像仓库密码
```

### 在云主机上的必要配置

#### 1. 安装Docker
```bash
# Ubuntu/Debian
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# 启动Docker服务
sudo systemctl enable docker
sudo systemctl start docker
```

#### 2. 安装kubectl
```bash
# 下载kubectl
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl

# 验证安装
kubectl version --client
```

#### 3. 安装K8s集群（推荐使用k3s）
```bash
# 安装k3s（轻量级Kubernetes）
curl -sfL https://get.k3s.io | sh -

# 配置kubectl访问
mkdir -p ~/.kube
sudo cp /etc/rancher/k3s/k3s.yaml ~/.kube/config
sudo chown $(id -u):$(id -g) ~/.kube/config

# 验证集群状态
kubectl get nodes
```

#### 4. 创建部署目录
```bash
# GitHub Actions会自动创建所需目录，您不需要手动创建
# 但如果需要手动测试，可以创建以下目录结构：

# 云主机上的目录结构：
# ~/                           # 云主机用户目录
# ├── k8s/                     # GitHub Actions自动复制的K8s配置
# │   ├── user-service/
# │   ├── database/
# │   └── cluster/
# └── edusoft-k8s/            # 部署工作目录（GitHub Actions创建）

# GitHub Actions的工作流程：
# 1. 自动将项目中的 k8s/ 目录复制到云主机的 ~/k8s/
# 2. 在云主机上执行 kubectl apply -f ~/k8s/...
# 3. 检查部署状态并执行健康检查

# 如果需要手动准备：
mkdir -p ~/k8s
chmod 755 ~/k8s
```

#### 5. 配置阿里云镜像拉取
```bash
# 登录阿里云镜像仓库
docker login crpi-z38aw29its2zwb1p.cn-beijing.personal.cr.aliyuncs.com

# 在K8s中创建镜像拉取密钥
kubectl create secret docker-registry aliyun-registry-secret \
  --docker-server=crpi-z38aw29its2zwb1p.cn-beijing.personal.cr.aliyuncs.com \
  --docker-username=YOUR_USERNAME \
  --docker-password=YOUR_PASSWORD \
  --namespace=edusoft
```

---

## 🔄 多主机配置（未来扩展）

如果您计划使用多台云主机，配置如下GitHub Secrets：

```
SSH_PRIVATE_KEY    # SSH私钥（所有主机共用）
PROD_HOST_1        # 主生产环境IP
PROD_HOST_2        # 备用生产环境IP
PROD_HOST_3        # 第三生产环境IP（可选）
ACR_USERNAME       # 阿里云镜像仓库用户名
ACR_PASSWORD       # 阿里云镜像仓库密码
```

### 多主机架构设计

```
主机1 (PROD_HOST_1)     主机2 (PROD_HOST_2)     主机3 (PROD_HOST_3)
├── K8s Master         ├── K8s Worker          ├── K8s Worker
├── User Service       ├── User Service        ├── User Service
├── MySQL Primary     ├── MySQL Replica       ├── Load Balancer
└── Nginx LB           └── Backup Services     └── Monitoring
```

### 多主机配置步骤

#### 1. 所有主机的基础配置
在每台主机上重复单主机的配置步骤1-4。

#### 2. 配置K8s集群

**在主节点（PROD_HOST_1）:**
```bash
# 安装k3s作为server
curl -sfL https://get.k3s.io | sh -s - --write-kubeconfig-mode 644

# 获取token
sudo cat /var/lib/rancher/k3s/server/node-token
```

**在工作节点（PROD_HOST_2, PROD_HOST_3）:**
```bash
# 使用主节点的token加入集群
curl -sfL https://get.k3s.io | K3S_URL=https://PROD_HOST_1:6443 K3S_TOKEN=<TOKEN> sh -
```

#### 3. 配置存储共享（NFS）

**在主节点配置NFS服务器:**
```bash
# 安装NFS服务器
sudo apt install nfs-kernel-server

# 创建共享目录
sudo mkdir -p /data/edusoft
sudo chown nobody:nogroup /data/edusoft
sudo chmod 777 /data/edusoft

# 配置NFS导出
echo "/data/edusoft *(rw,sync,no_subtree_check)" | sudo tee -a /etc/exports
sudo exportfs -a
sudo systemctl restart nfs-kernel-server
```

**在工作节点配置NFS客户端:**
```bash
# 安装NFS客户端
sudo apt install nfs-common

# 测试挂载
sudo mkdir -p /mnt/edusoft-data
sudo mount -t nfs PROD_HOST_1:/data/edusoft /mnt/edusoft-data
```

#### 4. 配置负载均衡

在多主机环境中，您需要配置负载均衡器（如Nginx）分发流量：

```nginx
upstream user_service_backend {
    server PROD_HOST_1:8081 weight=3;
    server PROD_HOST_2:8081 weight=2;
    server PROD_HOST_3:8081 weight=1;
}

server {
    listen 80;
    location /api/user/ {
        proxy_pass http://user_service_backend;
    }
}
```

---

## 📝 GitHub Actions配置

### 当前配置（自动兼容）

当前的GitHub Actions工作流会自动检测您的配置：

1. **如果设置了 `SERVER_HOST`**: 使用单主机模式
2. **如果设置了 `PROD_HOST_1/2/3`**: 使用多主机模式

### 必需的GitHub Secrets

#### 最小配置（单主机）:
```
SSH_PRIVATE_KEY=您的SSH私钥
SERVER_HOST=您的云主机IP
ACR_USERNAME=阿里云用户名
ACR_PASSWORD=阿里云密码
```

#### 完整配置（多主机）:
```
SSH_PRIVATE_KEY=您的SSH私钥
PROD_HOST_1=主生产环境IP
PROD_HOST_2=备用生产环境IP
PROD_HOST_3=第三环境IP（可选）
ACR_USERNAME=阿里云用户名
ACR_PASSWORD=阿里云密码
```

---

## 🚀 部署流程

### 单主机部署流程
1. 推送代码到 `micro_user_service` 分支
2. GitHub Actions自动触发
3. 构建Docker镜像并推送
4. 部署到单台云主机的K8s集群

### 多主机部署流程
1. 推送代码到 `main` 分支
2. GitHub Actions自动触发
3. 构建Docker镜像并推送
4. 依次部署到多台云主机
5. 执行健康检查确保所有节点正常

---

## 🔍 监控和调试

### 查看部署状态
```bash
# 检查Pod状态
kubectl get pods -n edusoft

# 查看服务日志
kubectl logs -f deployment/edusoft-user-service -n edusoft

# 检查服务健康状态
curl http://您的IP:8081/actuator/health
```

### 故障排除
```bash
# 查看K8s集群状态
kubectl cluster-info

# 查看节点状态
kubectl get nodes

# 查看事件
kubectl get events -n edusoft --sort-by='.lastTimestamp'
```

---

## 📈 扩展建议

### 从单主机迁移到多主机

1. **准备新主机**: 按照多主机配置步骤配置新主机
2. **数据迁移**: 使用MySQL备份/恢复迁移数据
3. **更新Secrets**: 将 `SERVER_HOST` 改为 `PROD_HOST_*`
4. **测试部署**: 先在非生产环境测试
5. **切换流量**: 逐步将流量切换到新集群

### 性能优化建议

- **主机1**: 部署核心服务和主数据库
- **主机2**: 部署副本服务和备用数据库
- **主机3**: 部署监控、日志收集和负载均衡

这样的配置能确保高可用性和负载分布。
