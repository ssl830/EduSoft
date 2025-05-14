# 课程管理功能说明文档

## 功能概述
本模块实现了课程的管理功能，包括课程的创建、更新、章节管理和资源管理等功能。支持教师、学生和助教三种角色查看相关课程。

## API接口说明

### 1. 课程管理
#### 创建课程
- 请求方式：POST
- 接口路径：`/api/courses`
- 请求体：
```json
{
    "teacherId": "教师ID（字符串）",
    "name": "课程名称",
    "code": "课程代码",
    "outline": "课程大纲",
    "objective": "教学目标",
    "assessment": "考核方式"
}
```

#### 更新课程
- 请求方式：PUT
- 接口路径：`/api/courses/{courseId}`
- 请求体：同创建课程

#### 获取课程详情
- 请求方式：GET
- 接口路径：`/api/courses/{courseId}`

#### 获取用户的课程列表
- 请求方式：GET
- 接口路径：`/api/courses/user/{userId}`
- 请求参数：
  - role: 用户角色（teacher/student/ta）
- 说明：
  - 教师：返回其创建的所有课程
  - 学生：返回其所在班级的所有课程
  - 助教：返回其协助的所有课程

### 2. 课程章节管理
#### 添加章节
- 请求方式：POST
- 接口路径：`/api/courses/{courseId}/sections`
- 请求体：
```json
{
    "title": "章节标题"
}
```

#### 删除章节
- 请求方式：DELETE
- 接口路径：`/api/courses/{courseId}/sections/{sectionId}`

### 3. 课程资源管理
#### 上传资源
- 请求方式：POST
- 接口路径：`/api/courses/{courseId}/resources`
- 请求参数：
  - file: 文件（MultipartFile）
  - title: 资源标题
  - type: 资源类型（PPT/PDF/VIDEO/CODE/OTHER）
  - sectionId: 所属章节ID（可选）

### 4. 课程预览
#### 获取课程完整信息
- 请求方式：GET
- 接口路径：`/api/courses/{courseId}/preview`
- 返回数据：包含课程基本信息和所有章节信息

## 数据模型

### Course（课程）
```json
{
    "id": "课程ID（字符串）",
    "teacherId": "教师ID（字符串）",
    "name": "课程名称",
    "code": "课程代码",
    "outline": "课程大纲",
    "objective": "教学目标",
    "assessment": "考核方式",
    "createdAt": "创建时间",
    "sections": [
        {
            "id": "章节ID（字符串）",
            "title": "章节标题",
            "sortOrder": "排序号"
        }
    ]
}
```

### CourseSection（课程章节）
```json
{
    "id": "章节ID（字符串）",
    "courseId": "课程ID（字符串）",
    "title": "章节标题",
    "sortOrder": "排序号"
}
```

### Resource（课程资源）
```json
{
    "id": "资源ID（字符串）",
    "courseId": "课程ID（字符串）",
    "sectionId": "章节ID（字符串）",
    "uploaderId": "上传者ID（字符串）",
    "title": "资源标题",
    "type": "资源类型（PPT/PDF/VIDEO/CODE/OTHER）",
    "fileUrl": "文件URL",
    "visibility": "可见性（PUBLIC/PRIVATE/CLASS_ONLY）",
    "version": "版本号",
    "createdAt": "创建时间"
}
```

## 用户角色说明
1. 教师（teacher）：可以创建和管理课程，上传课程资源
2. 学生（student）：可以查看所在班级的课程和资源
3. 助教（ta）：可以协助教师管理课程，查看和上传课程资源

## 注意事项
1. 课程代码必须唯一
2. 章节会自动按添加顺序排序
3. 资源上传后默认对所有班级成员可见
4. 删除章节时会同时删除该章节下的所有资源
5. 课程预览功能会返回课程的完整信息，包括所有章节
6. 获取课程列表时需要提供正确的用户角色（teacher/student/ta）
7. 学生只能查看其所在班级的课程
8. 助教可以查看其协助的课程
9. 所有ID字段都使用字符串类型存储 