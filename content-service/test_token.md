# TokenInterceptor 测试说明

## 测试步骤

1. **启动 content-service**
   ```bash
   mvn spring-boot:run
   ```

2. **查看后端日志**
   观察 TokenInterceptor 的 DEBUG 日志输出

3. **测试通知接口**
   使用 PowerShell 测试：
   ```powershell
   # 获取一个有效的 token（从 user-service 登录接口）
   $loginResponse = Invoke-RestMethod -Uri "http://localhost:8081/api/user/login" -Method POST -Body '{"username":"20200207","password":"123456"}' -ContentType "application/json"
   $token = $loginResponse.data.token
   
   # 测试通知接口
   $headers = @{
       "satoken" = $token
   }
   
   $response = Invoke-RestMethod -Uri "http://localhost:8083/api/content/notifications" -Method GET -Headers $headers
   Write-Host "Response: $($response | ConvertTo-Json -Depth 10)"
   ```

## 预期日志输出

TokenInterceptor 应该输出类似：
```
DEBUG Token拦截器处理请求: GET /api/content/notifications
DEBUG 从satoken头获取token: [UUID]
DEBUG 检测到UUID格式的token: [UUID]
DEBUG 通过用户服务验证token成功，获取到用户ID: 20200207
DEBUG 已解析到用户ID并写入请求属性: 20200207
```

## 可能的问题

1. **用户服务未启动** - 确保 user-service 在 8081 端口运行
2. **网络连接问题** - 检查 localhost:8081 是否可达
3. **token 格式问题** - 确保前端发送的是正确的 UUID 格式
4. **数据库查询问题** - 检查 notification 表中是否有 user_id = '20200207' 的数据

