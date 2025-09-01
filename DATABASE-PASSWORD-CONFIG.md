# 数据库密码配置说明

## 🔐 当前密码配置

### 用户服务数据库密码

**MySQL Root用户：**
- 用户名: `root`
- 密码: `EduSoft_RootPassword2025!`
- Base64编码: `RWR1U29mdF9Sb290UGFzc3dvcmQyMDI1IQ==`

**应用服务用户：**
- 用户名: `user_service`
- 密码: `EduSoft_UserServicePassword2025!`
- Base64编码: `RWR1U29mdF9Vc2VyU2VydmljZVBhc3N3b3JkMjAyNSE=`

**数据库名称：**
- 数据库: `user-db`

## 🔧 配置文件位置

### 1. Kubernetes Secret配置
文件：`k8s/database/mysql-user-service.yaml`
```yaml
data:
  mysql-root-password: RWR1U29mdF9Sb290UGFzc3dvcmQyMDI1IQ==
  mysql-user: dXNlcl9zZXJ2aWNl
  mysql-password: RWR1U29mdF9Vc2VyU2VydmljZVBhc3N3b3JkMjAyNSE=
```

### 2. Spring Boot应用配置
文件：`user-service/src/main/resources/application.yml`
```yaml
datasource:
  url: jdbc:mysql://${DB_HOST:mysql-user-service}:${DB_PORT:3306}/${DB_NAME:user-db}?...
  username: ${DB_USERNAME:user_service}
  password: ${DB_PASSWORD:EduSoft_UserServicePassword2025!}
```

## 🚨 安全建议

### 生产环境密码要求：
1. **复杂性**: 至少12位，包含大小写字母、数字、特殊字符
2. **唯一性**: 每个环境使用不同的密码
3. **定期更换**: 建议每6个月更换一次
4. **存储安全**: 使用Kubernetes Secrets，不要硬编码

### 修改密码的步骤：

#### 方法1: 修改Kubernetes Secret（推荐）
```bash
# 1. 生成新密码的base64编码
echo -n "your_new_password" | base64

# 2. 更新secret
kubectl patch secret mysql-user-secret -n edusoft -p='{"data":{"mysql-password":"new_base64_encoded_password"}}'

# 3. 重启数据库pod
kubectl rollout restart deployment/mysql-user-service -n edusoft
```

#### 方法2: 直接在MySQL中修改
```sql
-- 连接到MySQL
ALTER USER 'user_service'@'%' IDENTIFIED BY 'your_new_password';
FLUSH PRIVILEGES;
```

## 🔍 验证连接

### 从应用pod中测试连接：
```bash
# 进入应用pod
kubectl exec -it deployment/edusoft-user-service -n edusoft -- bash

# 测试数据库连接
mysql -h mysql-user-service -u user_service -p'EduSoft_UserServicePassword2025!' user-db
```

### 检查应用日志：
```bash
kubectl logs deployment/edusoft-user-service -n edusoft
```

## ⚠️ 注意事项

1. **密码同步**: 修改密码时要确保Kubernetes Secret和数据库中的密码一致
2. **环境变量**: 应用会优先使用环境变量，然后使用默认值
3. **备份**: 修改密码前请先备份数据库
4. **测试**: 在生产环境修改前，请在测试环境验证

## 🔄 Base64编码/解码

### 编码：
```bash
echo -n "your_password" | base64
```

### 解码：
```bash
echo "encoded_string" | base64 -d
```

### 当前配置解码验证：
```bash
# Root密码
echo "RWR1U29mdF9Sb290UGFzc3dvcmQyMDI1IQ==" | base64 -d
# 输出: EduSoft_RootPassword2025!

# 用户服务密码  
echo "RWR1U29mdF9Vc2VyU2VydmljZVBhc3N3b3JkMjAyNSE=" | base64 -d
# 输出: EduSoft_UserServicePassword2025!
```
