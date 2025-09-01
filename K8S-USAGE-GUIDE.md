# K8s目录使用说明

## 📁 k8s目录结构说明

**项目中的目录结构：**
```
k8s/                        # 项目中的K8s配置目录
├── user-service/           # 用户服务K8s部署配置
│   └── deployment.yaml    # 包含Deployment、Service、ConfigMap等
├── database/               # 数据库K8s部署配置
│   └── mysql-user-service.yaml  # MySQL数据库部署配置
└── cluster/                # 集群级别配置（暂时不用）
    ├── ingress.yaml        # Ingress配置
    └── nginx-distributed.conf  # Nginx配置模板
```

**云主机上的目录结构（GitHub Actions自动创建）：**
```
~/                          # 云主机用户目录
├── k8s/                    # 从项目复制过来的K8s配置
│   ├── user-service/
│   ├── database/
│   └── cluster/
└── edusoft-k8s/           # GitHub Actions工作目录
```

**为什么叫"k8s"而不是"edusoft-k8s"？**
- 项目中使用简短的 `k8s/` 目录名，便于管理
- GitHub Actions会将 `k8s/` 复制到云主机的 `~/k8s/`
- 这样既保持项目结构简洁，又确保云主机上的配置完整

## 🤔 这些文件什么时候用？

### 方式1: GitHub Actions自动部署（推荐）

**您只需要推送代码到GitHub，其他都是自动的！**

1. **推送代码**到 `micro_user_service` 分支
2. **GitHub Actions自动触发**，执行以下步骤：
   - 构建Java项目
   - 创建Docker镜像
   - 推送到阿里云镜像仓库
   - **自动使用k8s配置文件部署到云主机**
3. **自动健康检查**确保部署成功

您**不需要手动执行**k8s命令！

### 方式2: 手动部署（备用方案）

如果GitHub Actions出现问题，您可以手动部署：

```bash
# 1. 先部署数据库
kubectl apply -f k8s/database/mysql-user-service.yaml

# 2. 再部署用户服务
kubectl apply -f k8s/user-service/deployment.yaml

# 3. 检查部署状态
kubectl get pods -n edusoft
```

或者使用我们提供的脚本：
```cmd
scripts\deploy-user-service.bat full
```

## 🎯 推荐的工作流程

### 日常开发
```
1. 修改用户服务代码
2. git add . && git commit -m "fix: 修复用户登录问题"
3. git push origin micro_user_service
4. ☕ 喝杯咖啡，等待自动部署完成
5. 检查部署结果：访问 http://您的IP/api/user/health
```

### 检查部署状态
```bash
# 查看GitHub Actions执行状态
# 在GitHub仓库的Actions标签页查看

# 或者直接在云主机上检查
kubectl get pods -n edusoft
kubectl logs -f deployment/edusoft-user-service -n edusoft
```

## 🔧 什么时候需要手动操作？

### 1. 首次部署
第一次部署时，您需要：
- 在云主机上安装K8s（见CLOUD-HOST-CONFIG.md）
- 在GitHub设置Secrets（SSH_PRIVATE_KEY, SERVER_HOST等）

### 2. 配置变更
如果需要修改K8s配置（如资源限制、副本数等）：
```bash
# 修改 k8s/user-service/deployment.yaml
# 然后推送代码，GitHub Actions会自动应用新配置
```

### 3. 紧急修复
如果GitHub Actions无法使用，可以手动部署：
```bash
# 登录云主机
ssh root@您的IP

# 手动部署
cd ~/edusoft-k8s
kubectl apply -f user-service-deployment.yaml
```

## ⚡ 快速验证部署

### 检查服务状态
```bash
# 在云主机上执行
kubectl get all -n edusoft

# 预期输出：
# NAME                                   READY   STATUS    RESTARTS   AGE
# pod/edusoft-user-service-xxx           1/1     Running   0          5m
# pod/mysql-user-service-xxx             1/1     Running   0          10m
```

### 测试服务接口
```bash
# 健康检查
curl http://您的IP:8081/actuator/health

# 或通过端口转发在本地测试
kubectl port-forward -n edusoft service/user-service 8081:8081
curl http://localhost:8081/actuator/health
```

## 📊 最佳实践

### 推荐：完全自动化流程
```
开发 → Git Push → GitHub Actions → 自动部署 → 健康检查
```

### 备用：半自动化流程
```
开发 → Git Push → 手动执行部署脚本
```

### 应急：完全手动流程
```
开发 → 手动构建 → 手动部署 → 手动验证
```

## ❓ 常见问题

**Q: 我是否需要学习K8s命令？**
A: 不需要！GitHub Actions会自动处理所有K8s操作。

**Q: 如果部署失败怎么办？**
A: 查看GitHub Actions日志，或在云主机上运行 `kubectl get events -n edusoft`

**Q: 可以修改K8s配置吗？**
A: 可以！修改k8s目录下的yaml文件，然后推送代码即可。

**Q: 如何回滚到上一版本？**
A: GitHub Actions会自动标记镜像版本，可以回滚到任意版本。

## 🎉 总结

**最简单的使用方式：**
1. 配置好云主机和GitHub Secrets
2. 只管写代码和推送
3. 其他都是自动的！

k8s目录的文件主要是给GitHub Actions使用的，您通常不需要手动执行这些配置文件。
