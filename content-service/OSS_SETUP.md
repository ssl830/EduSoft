# 阿里云OSS配置说明

## 概述
content-service 已经集成了阿里云对象存储（OSS）服务，用于处理文件上传、下载和管理。

## 当前状态
- ✅ 已添加阿里云OSS依赖
- ✅ 已创建OSS配置类
- ✅ 已实现文件上传服务接口
- ✅ 已集成到教学资源和作业服务中
- ⚠️ 当前使用模拟实现，避免启动失败

## 配置步骤

### 1. 获取阿里云OSS配置信息
1. 登录阿里云控制台
2. 进入对象存储OSS服务
3. 创建或选择已有的Bucket
4. 获取以下信息：
   - Endpoint（地域节点）
   - AccessKey ID
   - AccessKey Secret
   - Bucket名称

### 2. 修改配置文件
编辑 `src/main/resources/application-oss.yml` 文件：

```yaml
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com  # 替换为你的OSS endpoint
    accessKeyId: your-access-key-id          # 替换为你的AccessKey ID
    accessKeySecret: your-access-key-secret  # 替换为你的AccessKey Secret
    bucketName: your-bucket-name             # 替换为你的Bucket名称
```

### 3. 启用真实OSS实现
1. 取消注释 `OssConfig.java` 中的OSS客户端Bean
2. 取消注释 `FileUploadServiceImpl.java` 中的真实OSS实现
3. 添加必要的import语句

## 功能特性

### 文件上传
- 支持多种文件格式
- 自动生成唯一文件名
- 按业务类型分类存储
- 返回可访问的URL

### 文件管理
- 文件删除
- 签名URL生成（支持临时访问）
- 文件元数据管理

### 集成服务
- 教学资源上传
- 作业附件上传
- 作业提交文件上传

## 存储结构

```
bucket-name/
├── teaching-resources/          # 教学资源
│   ├── {courseId}/
│   │   └── {chapterId}/
│   │       └── {fileName}
├── homework/                    # 作业附件
│   └── {classId}/
│       └── {fileName}
└── homework-submission/         # 作业提交
    └── {homeworkId}/
        └── {studentId}/
            └── {fileName}
```

## 安全配置

### 1. 访问控制
- 建议使用RAM用户，而不是主账号
- 为OSS用户分配最小权限
- 定期轮换AccessKey

### 2. 网络安全
- 配置OSS Bucket的访问策略
- 设置IP白名单（如需要）
- 启用防盗链功能

### 3. 数据加密
- 启用服务端加密
- 配置传输加密（HTTPS）

## 测试接口

### 通用文件上传
```bash
POST /api/content/upload/file
Content-Type: multipart/form-data

file: [文件]
folder: [存储文件夹]
```

### 文件删除
```bash
DELETE /api/content/upload/file?fileUrl=[文件URL]
```

### 生成签名URL
```bash
GET /api/content/upload/signed-url?objectName=[对象名称]
```

## 注意事项

1. **文件大小限制**：当前设置为500MB
2. **文件类型**：支持常见文档、图片、视频等格式
3. **存储成本**：注意OSS的存储和流量费用
4. **备份策略**：建议定期备份重要文件
5. **监控告警**：配置OSS使用量监控

## 故障排除

### 常见问题
1. **配置错误**：检查AccessKey和Bucket名称
2. **权限不足**：确认RAM用户权限
3. **网络问题**：检查网络连接和防火墙设置
4. **存储空间**：确认Bucket有足够空间

### 日志查看
查看应用日志中的OSS相关错误信息：
```bash
tail -f logs/application.log | grep OSS
```

## 下一步计划

1. 实现真实的OSS文件上传
2. 添加文件压缩和格式转换
3. 集成CDN加速
4. 实现文件版本管理
5. 添加文件预览功能
