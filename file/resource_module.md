# 教学资源模块说明文档

## 模块结构

该模块在以下目录中实现相关功能：

```
src/main/java/org/example/edusoft/
├── entity/resource/
│   ├── TeachingResource.java     # 教学资源实体类
│   └── LearningProgress.java     # 学习进度实体类
├── mapper/resource/
│   ├── TeachingResourceMapper.java    # 教学资源数据访问接口
│   └── LearningProgressMapper.java    # 学习进度数据访问接口
├── service/resource/
│   ├── TeachingResourceService.java   # 教学资源服务接口
│   └── impl/
│       └── TeachingResourceServiceImpl.java  # 教学资源服务实现类
└── controller/resource/
    └── TeachingResourceController.java      # 教学资源控制器
```

## 数据库表设计

### 1. 教学资源表 (teaching_resource)
```sql
CREATE TABLE teaching_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,           -- 资源标题
    description TEXT,                      -- 资源描述
    course_id BIGINT NOT NULL,             -- 所属课程ID
    chapter_id BIGINT NOT NULL,            -- 所属章节ID
    chapter_name VARCHAR(255) NOT NULL,     -- 章节名称
    resource_type VARCHAR(50) NOT NULL,    -- 资源类型（VIDEO/DOCUMENT等）
    file_url VARCHAR(255) NOT NULL,        -- 文件URL
    object_name VARCHAR(255) NOT NULL,     -- 对象存储中的文件名
    duration INTEGER,                      -- 视频时长（秒）
    created_by BIGINT NOT NULL,            -- 创建者ID
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 2. 学习进度表 (learning_progress)
```sql
CREATE TABLE learning_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    resource_id BIGINT NOT NULL,           -- 教学资源ID
    student_id BIGINT NOT NULL,            -- 学生ID
    progress INTEGER NOT NULL,             -- 学习进度（秒）
    last_position INTEGER NOT NULL,        -- 最后观看位置（秒）
    watch_count INTEGER DEFAULT 0,         -- 观看次数
    last_watch_time DATETIME,              -- 最后观看时间
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_resource_student (resource_id, student_id),  -- 资源和学生的唯一约束
    FOREIGN KEY (resource_id) REFERENCES teaching_resource(id) ON DELETE CASCADE
);
```

## API接口说明

### 1. 上传教学资源
- 请求方式：POST
- 路径：/api/resources/upload
- Content-Type: multipart/form-data
- 参数：
  - file: 视频文件
  - courseId: 课程ID
  - chapterId: 章节ID
  - chapterName: 章节名称
  - title: 资源标题
  - description: 资源描述
  - createdBy: 创建者ID
- 返回：资源详细信息

### 2. 获取课程资源列表
- 请求方式：GET
- 路径：/api/resources/list/{courseId}
- 返回：课程下的所有教学资源（按章节分组）

### 3. 获取章节资源列表
- 请求方式：GET
- 路径：/api/resources/chapter/{courseId}/{chapterId}
- 返回：指定章节下的所有教学资源

### 4. 获取资源详情
- 请求方式：GET
- 路径：/api/resources/{resourceId}
- 返回：资源详细信息

### 5. 更新学习进度
- 请求方式：POST
- 路径：/api/resources/progress
- Content-Type: application/json
- 请求体：
  ```json
  {
    "resourceId": "资源ID",
    "studentId": "学生ID",
    "progress": "当前进度（秒）",
    "position": "当前播放位置（秒）"
  }
  ```
- 返回：更新后的进度信息

### 6. 获取学习进度
- 请求方式：GET
- 路径：/api/resources/progress/{resourceId}/{studentId}
- 返回：学习进度信息

### 7. 删除教学资源
- 请求方式：DELETE
- 路径：/api/resources/{resourceId}
- 参数：
  - operatorId: 操作者ID（Query参数）
- 返回：删除操作结果
- 说明：
  - 删除资源时会同时删除阿里云OSS中的文件
  - 删除资源时会级联删除相关的学习进度记录
  - 需要验证请求者是否有权限删除该资源

### 8. 获取资源访问URL
- 请求方式：GET
- 路径：/api/resources/url/{resourceId}
- 返回：带签名的临时访问URL（有效期1小时）
- 说明：
  - 返回的URL包含签名信息，用于防盗链
  - URL有效期为1小时，过期后需要重新获取

## 前端要求

1. 视频播放器组件要求：
   - 支持进度条显示
   - 支持播放/暂停控制
   - 支持进度跳转
   - 记录当前播放位置

2. 进度记录要求：
   - 每10秒向后端发送一次进度更新
   - 在视频暂停时发送进度更新
   - 在页面关闭前发送最后的进度更新

3. 错误处理：
   - 网络异常时进行重试
   - 显示友好的错误提示
   - 保存本地进度记录，在网络恢复后同步

## 注意事项

1. 视频上传：
   - 支持的视频格式：MP4, WebM
   - 最大文件大小：500MB
   - 上传到阿里云OSS对象存储
   - 使用阿里云OSS SDK处理文件上传

2. 进度记录：
   - 使用唯一约束（resource_id, student_id）防止重复记录
   - 每10秒更新一次进度记录
   - 记录最后观看时间和观看位置
   - 支持断点续播功能

3. 安全性：
   - 使用阿里云OSS的签名URL机制防盗链
   - URL有效期为1小时，过期后需要重新获取
   - 删除操作需要提供操作者ID进行权限验证
   - 资源ID和学生ID的合法性验证

4. 存储管理：
   - 使用唯一的对象名在OSS中存储文件
   - 删除资源时自动清理OSS中的文件
   - 使用外键约束自动清理相关的学习进度记录
   - 保存文件URL和对象名便于管理和访问 