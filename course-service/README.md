# 课程微服务 (Course Service)

**服务端口**: 8082  
**技术栈**: Spring Boot 3.4.5 + MyBatis + MySQL

## 功能模块

### 1. 课程管理
- 创建、查询、更新、删除课程
- 课程信息管理（课程名称、代码、大纲、目标、评估方式）
- 教师课程列表查询

### 2. 班级管理
- 班级创建和管理
- 班级成员管理（加入、离开班级）
- 班级代码管理
- 教师/学生班级列表查询

### 3. 课程章节管理
- 课程章节的增删改查
- 章节排序管理

### 4. 学生导入管理
- 批量导入学生到班级
- 导入记录管理
- 导入历史查询

## API接口文档

### 认证说明
所有接口都需要在请求头中包含 `satoken` 进行身份验证：
```http
satoken: your_token_here
```

### 课程管理接口

#### 1. 创建课程
```http
POST /api/courses
Content-Type: application/json
satoken: {token}

{
  "teacherId": 4,
  "name": "计算机科学导论",
  "code": "CS103",
  "outline": "计算机科学基础课程",
  "objective": "了解计算机科学的基本概念",
  "assessment": "期末考试 + 平时作业"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "课程创建成功",
  "data": {
    "id": 1,
    "teacherId": 4,
    "name": "计算机科学导论",
    "code": "CS103",
    "outline": "计算机科学基础课程",
    "objective": "了解计算机科学的基本概念",
    "assessment": "期末考试 + 平时作业",
    "createTime": "2024-01-01T10:00:00"
  }
}
```

#### 2. 获取所有课程列表
```http
GET /api/courses/list
satoken: {token}
```

#### 3. 获取用户课程列表
```http
GET /api/courses/user/{userId}
satoken: {token}
```

#### 4. 获取课程详情
```http
GET /api/courses/{courseId}
satoken: {token}
```

#### 5. 更新课程
```http
PUT /api/courses/{courseId}
Content-Type: application/json
satoken: {token}

{
  "teacherId": 4,
  "name": "计算机科学导论（更新版）",
  "code": "CS101",
  "outline": "计算机科学基础课程 - 更新版",
  "objective": "了解计算机科学的基本概念和最新发展",
  "assessment": "期末考试 + 平时作业 + 项目实践"
}
```

#### 6. 删除课程
```http
DELETE /api/courses/{courseId}
satoken: {token}
```

### 班级管理接口

#### 1. 创建班级
```http
POST /api/classes
Content-Type: application/json
satoken: {token}

{
  "courseId": 2,
  "name": "CS101-2024春季班",
  "classCode": "CS101_2024_SPRING"
}
```

#### 2. 获取教师班级列表
```http
GET /api/classes/teacher/{teacherId}
satoken: {token}
```

#### 3. 获取学生班级列表
```http
GET /api/classes/student/{studentId}
satoken: {token}
```

#### 4. 获取班级详情
```http
GET /api/classes/{classId}
satoken: {token}
```

#### 5. 更新班级
```http
PUT /api/classes/{classId}
Content-Type: application/json
satoken: {token}

{
  "courseId": 2,
  "name": "CS101-2024春季班（更新版）",
  "classCode": "CS101_2024_V2"
}
```

#### 6. 删除班级
```http
DELETE /api/classes/{classId}
satoken: {token}
```

### 班级成员管理接口

#### 1. 加入班级
```http
POST /api/classes/{classId}/join/{studentId}
satoken: {token}
```

#### 2. 离开班级
```http
DELETE /api/classes/{classId}/leave/{studentId}
satoken: {token}
```

#### 3. 获取班级成员
```http
GET /api/classes/{classId}/users
satoken: {token}
```

#### 4. 通过班级代码加入班级
```http
POST /api/classes/join?classCode={classCode}&studentId={studentId}
Content-Type: application/json
satoken: {token}
```

#### 5. 添加学生到班级
```http
POST /api/classes/{classId}/students?studentId={studentId}
satoken: {token}
```

#### 6. 从班级移除学生
```http
DELETE /api/classes/{classId}/students/{studentId}
satoken: {token}
```

#### 7. 获取班级学生数量
```http
GET /api/classes/{classId}/student-count
satoken: {token}
```

#### 8. 获取教师简化班级列表
```http
GET /api/classes/teacher/simple/{teacherId}
satoken: {token}
```

#### 9. 获取用户课程表
```http
GET /api/classes/schedule/user/{userId}
satoken: {token}
```

### 课程章节管理接口

#### 1. 创建课程章节
```http
POST /api/course-sections
Content-Type: application/json
satoken: {token}

{
  "courseId": 9,
  "title": "第一章：计算机基础",
  "sortOrder": 1
}
```

#### 2. 获取课程章节列表
```http
GET /api/course-sections/course/{courseId}
satoken: {token}
```

#### 3. 更新课程章节
```http
PUT /api/course-sections/{sectionId}
Content-Type: application/json
satoken: {token}

{
  "courseId": 9,
  "title": "第一章：计算机基础（更新版）",
  "sortOrder": 1
}
```

#### 4. 删除课程章节
```http
DELETE /api/course-sections/{sectionId}
satoken: {token}
```

### 学生导入管理接口

#### 1. 导入学生
```http
POST /api/imports/students
Content-Type: application/json
satoken: {token}

{
  "classId": 6,
  "operatorId": 4,
  "fileName": "students_import.csv",
  "importType": "BATCH_IMPORT",
  "studentData": [
    {
      "studentId": 10,
      "studentName": "王五"
    },
    {
      "studentId": 12,
      "studentName": "赵六"
    }
  ]
}
```

#### 2. 获取导入记录
```http
GET /api/imports/records/{classId}
satoken: {token}
```

#### 3. 获取特定导入记录
```http
GET /api/imports/record/{recordId}
satoken: {token}
```

## 测试接口

项目提供了完整的测试接口文件 `test-all-interfaces-fixed.http`，包含所有接口的测试用例。

### 测试步骤
1. 首先获取有效token（通过用户服务登录）
2. 使用token测试各个功能模块
3. 按照测试文件中的顺序执行测试用例


## 错误处理

### 常见错误码
- `200`: 请求成功
- `400`: 请求参数错误
- `401`: 未授权访问
- `403`: 权限不足
- `404`: 资源不存在
- `500`: 服务器内部错误

### 错误响应格式
```json
{
  "code": 400,
  "message": "错误描述",
  "data": null
}
```

## 开发说明

### 项目结构
```
src/main/java/
├── com.example.courseservice
│   ├── controller/    # 控制器层
│   ├── service/       # 服务层
│   ├── mapper/        # 数据访问层
│   ├── entity/        # 实体类
│   ├── dto/           # 数据传输对象
│   └── config/        # 配置类
```

### 开发规范
1. 遵循RESTful API设计规范
2. 使用统一的响应格式
3. 所有接口都需要进行权限验证
4. 数据库操作使用MyBatis
5. 异常处理统一在全局异常处理器中处理
