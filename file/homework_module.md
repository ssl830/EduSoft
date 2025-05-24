# 作业管理模块说明文档

## 1. 模块结构
### 1.1 目录结构
```
src/main/java/org/example/edusoft/
├── controller/homework/
│   └── HomeworkController.java      # 作业控制器，处理HTTP请求
├── service/homework/
│   └── HomeworkService.java         # 作业服务，处理业务逻辑
├── entity/homework/
│   ├── Homework.java                # 作业实体类
│   └── HomeworkSubmission.java      # 作业提交实体类
├── mapper/homework/
│   ├── HomeworkMapper.java          # 作业数据访问接口
│   └── HomeworkSubmissionMapper.java # 作业提交数据访问接口
```

## 2. 功能说明
### 2.1 教师功能
- 创建作业
  - 设置作业基本信息（名称、简介等）
  - 上传作业附件（可选）
  - 设置截止时间和其他参数
- 管理作业
  - 查看作业列表（按班级）
  - 查看作业详情
  - 下载作业附件
  - 删除作业（包括附件和提交记录）
- 管理提交
  - 查看所有学生的提交情况
  - 下载学生提交的作业文件
  - 按班级统计提交情况

### 2.2 学生功能
- 查看作业
  - 浏览作业列表
  - 查看作业详情
  - 下载作业附件
- 提交作业
  - 上传作业文件
  - 选择提交类型
  - 查看个人提交记录
  - 查看提交状态

## 3. API接口
### 3.1 作业管理接口
```
# 作业基本操作
POST   /api/homework/create              # 创建作业
GET    /api/homework/{id}               # 获取作业详情
GET    /api/homework/list?classId={id}  # 获取班级作业列表
DELETE /api/homework/{homeworkId}       # 删除作业

# 文件操作
GET    /api/homework/file/{homeworkId}  # 下载作业附件
```

### 3.2 作业提交接口
```
# 提交操作
POST   /api/homework/submit/{homeworkId}         # 提交作业
GET    /api/homework/submissions/{homeworkId}    # 获取作业提交列表
GET    /api/homework/submission                  # 获取学生的提交记录
       ?homeworkId={id}&studentId={id}
GET    /api/homework/submission/file/{id}        # 下载提交的作业文件
```

### 3.3 请求/响应格式
#### 创建作业
- 请求：
  ```
  Content-Type: multipart/form-data
  - homework: JSON格式的作业信息
  - file: 附件文件（可选）
  ```
- 响应：作业ID

#### 提交作业
- 请求：
  ```
  Content-Type: multipart/form-data
  - studentId: 学生ID
  - submissionType: 提交类型
  - file: 提交的文件
  ```
- 响应：提交记录ID

## 4. 文件存储
- 作业附件存储在阿里云OSS的 homework 文件夹下
- 学生提交的作业文件存储在阿里云OSS的 hwsubmit 文件夹下
- 文件命名规则：{homework_id}/{timestamp}_{filename}

## 5. 注意事项
1. 文件处理：
   - 支持常见文档格式（PDF、Word、Excel等）
   - 文件大小限制：50MB
   - 使用阿里云OSS进行文件存储

2. 安全性：
   - 验证文件类型和大小
   - 检查提交权限和截止时间
   - 防止重复提交
   - 文件访问需要权限验证

3. 错误处理：
   - 文件上传失败重试机制
   - 友好的错误提示
   - 事务管理确保数据一致性

4. 性能优化：
   - 使用OSS进行文件存储和下载
   - 合理的数据库索引
   - 分页加载提交列表 