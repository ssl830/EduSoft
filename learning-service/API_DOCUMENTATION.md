# Learning Service - Record API 接口文档

## 概述
本文档描述了Learning Service中与学习记录（Record）相关的所有API接口，包括学习记录、练习记录、提交报告等功能的接口说明。

## 基础信息
- **服务地址**: `http://localhost:8084`
- **基础路径**: `/api/record`
- **认证方式**: 需要在请求头中包含 `satoken` 字段
- **响应格式**: JSON

## 通用响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {}
}
```

## 1. 学习记录相关接口

### 1.1 获取学习记录列表
**接口描述**: 获取指定学生的学习记录列表

**请求信息**:
- **URL**: `GET /api/record/study`
- **Headers**: 
  - `satoken`: 用户认证token
- **参数**: 无

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "resourceId": 101,
            "studentId": 1001,
            "progress": 85.5,
            "lastPosition": 300,
            "watchCount": 5,
            "lastWatchTime": "2024-01-15T10:30:00",
            "resourceTitle": "Java基础教程",
            "courseName": "Java编程基础",
            "sectionTitle": "第一章：Java简介"
        }
    ]
}
```

**PowerShell测试命令**:
```powershell
$baseUrl = "http://localhost:8084"
$testToken = "your_test_token_here"

Invoke-RestMethod -Uri "$baseUrl/api/record/study" -Method Get -Headers @{"satoken"=$testToken} -ContentType "application/json"
```

### 1.2 导出学习记录到Excel
**接口描述**: 将学习记录导出为Excel文件

**请求信息**:
- **URL**: `GET /api/record/study/export`
- **Headers**: 
  - `satoken`: 用户认证token
- **参数**: 无

**响应**: Excel文件下载

**PowerShell测试命令**:
```powershell
Invoke-RestMethod -Uri "$baseUrl/api/record/study/export" -Method Get -Headers @{"satoken"=$testToken} -OutFile "study_records.xlsx"
```

### 1.3 获取指定课程的学习记录
**接口描述**: 获取指定学生在指定课程下的学习记录

**请求信息**:
- **URL**: `GET /api/record/study/course/{courseId}`
- **Headers**: 
  - `satoken`: 用户认证token
- **路径参数**: 
  - `courseId`: 课程ID

**PowerShell测试命令**:
```powershell
$courseId = 1
Invoke-RestMethod -Uri "$baseUrl/api/record/study/course/$courseId" -Method Get -Headers @{"satoken"=$testToken} -ContentType "application/json"
```

### 1.4 导出指定课程的学习记录
**接口描述**: 将指定课程的学习记录导出为Excel文件

**请求信息**:
- **URL**: `GET /api/record/study/course/{courseId}/export`
- **Headers**: 
  - `satoken`: 用户认证token
- **路径参数**: 
  - `courseId`: 课程ID

**PowerShell测试命令**:
```powershell
Invoke-RestMethod -Uri "$baseUrl/api/record/study/course/$courseId/export" -Method Get -Headers @{"satoken"=$testToken} -OutFile "course_${courseId}_study_records.xlsx"
```

## 2. 练习记录相关接口

### 2.1 获取练习记录列表
**接口描述**: 获取指定学生的练习记录列表

**请求信息**:
- **URL**: `GET /api/record/practice`
- **Headers**: 
  - `satoken`: 用户认证token
- **参数**: 无

**PowerShell测试命令**:
```powershell
Invoke-RestMethod -Uri "$baseUrl/api/record/practice" -Method Get -Headers @{"satoken"=$testToken} -ContentType "application/json"
```

### 2.2 导出练习记录到Excel
**接口描述**: 将练习记录导出为Excel文件

**请求信息**:
- **URL**: `GET /api/record/practice/export`
- **Headers**: 
  - `satoken`: 用户认证token
- **参数**: 无

**PowerShell测试命令**:
```powershell
Invoke-RestMethod -Uri "$baseUrl/api/record/practice/export" -Method Get -Headers @{"satoken"=$testToken} -OutFile "practice_records.xlsx"
```

### 2.3 获取指定课程的练习记录
**接口描述**: 获取指定学生在指定课程下的练习记录

**请求信息**:
- **URL**: `GET /api/record/practice/course/{courseId}`
- **Headers**: 
  - `satoken`: 用户认证token
- **路径参数**: 
  - `courseId`: 课程ID

**PowerShell测试命令**:
```powershell
$courseId = 1
Invoke-RestMethod -Uri "$baseUrl/api/record/practice/course/$courseId" -Method Get -Headers @{"satoken"=$testToken} -ContentType "application/json"
```

### 2.4 导出指定课程的练习记录
**接口描述**: 将指定课程的练习记录导出为Excel文件

**请求信息**:
- **URL**: `GET /api/record/practice/course/{courseId}/export`
- **Headers**: 
  - `satoken`: 用户认证token
- **路径参数**: 
  - `courseId`: 课程ID

**PowerShell测试命令**:
```powershell
Invoke-RestMethod -Uri "$baseUrl/api/record/practice/course/$courseId/export" -Method Get -Headers @{"satoken"=$testToken} -OutFile "course_${courseId}_practice_records.xlsx"
```

### 2.5 调试练习记录数据
**接口描述**: 调试接口，检查指定课程的练习记录数据情况

**请求信息**:
- **URL**: `GET /api/record/practice/{courseId}/debug`
- **Headers**: 
  - `satoken`: 用户认证token
- **路径参数**: 
  - `courseId`: 课程ID

**PowerShell测试命令**:
```powershell
Invoke-RestMethod -Uri "$baseUrl/api/record/practice/$courseId/debug" -Method Get -Headers @{"satoken"=$testToken} -ContentType "application/json"
```

## 3. 练习提交报告相关接口

### 3.1 获取练习提交报告
**接口描述**: 获取指定练习提交的详细报告信息

**请求信息**:
- **URL**: `GET /api/record/submission/{submissionId}/report`
- **Headers**: 
  - `satoken`: 用户认证token
- **路径参数**: 
  - `submissionId`: 提交记录ID

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "submissionInfo": {
            "id": 1,
            "practiceId": 101,
            "studentId": 1001,
            "submittedAt": "2024-01-15T14:30:00",
            "score": 85,
            "feedback": "表现良好",
            "practiceTitle": "Java基础测试",
            "courseName": "Java编程基础",
            "className": "计算机科学1班"
        },
        "questions": [
            {
                "id": 1,
                "content": "Java的特点是什么？",
                "type": "多选题",
                "studentAnswer": "A,B,C",
                "correctAnswer": "A,B,C,D",
                "score": 8,
                "isCorrect": false,
                "analysis": "Java具有跨平台、面向对象、安全性高等特点"
            }
        ],
        "rank": 3,
        "totalStudents": 25,
        "percentile": 88.0,
        "scoreDistribution": [
            {
                "score_range": "90-100",
                "count": 5,
                "percentage": 20.0
            }
        ]
    }
}
```

**PowerShell测试命令**:
```powershell
$submissionId = 1
Invoke-RestMethod -Uri "$baseUrl/api/record/submission/$submissionId/report" -Method Get -Headers @{"satoken"=$testToken} -ContentType "application/json"
```

### 3.2 导出练习提交报告
**接口描述**: 将练习提交报告导出为PDF文件

**请求信息**:
- **URL**: `GET /api/record/submission/{submissionId}/export-report`
- **Headers**: 
  - `satoken`: 用户认证token
- **路径参数**: 
  - `submissionId`: 提交记录ID

**响应**: PDF文件下载

**PowerShell测试命令**:
```powershell
Invoke-RestMethod -Uri "$baseUrl/api/record/submission/$submissionId/export-report" -Method Get -Headers @{"satoken"=$testToken} -OutFile "submission_report.pdf"
```

### 3.3 获取提交记录信息
**接口描述**: 获取指定提交记录的详细信息（包括调试信息）

**请求信息**:
- **URL**: `GET /api/record/submission/{submissionId}/info`
- **Headers**: 
  - `satoken`: 用户认证token
- **路径参数**: 
  - `submissionId`: 提交记录ID

**PowerShell测试命令**:
```powershell
Invoke-RestMethod -Uri "$baseUrl/api/record/submission/$submissionId/info" -Method Get -Headers @{"satoken"=$testToken} -ContentType "application/json"
```

## 4. 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误或业务逻辑错误 |
| 401 | 未认证或token无效 |
| 500 | 服务器内部错误 |

## 5. 常见错误及解决方案

### 5.1 认证相关错误
- **错误**: `请先登录`
- **原因**: 请求头中缺少`satoken`或token已过期
- **解决**: 重新登录获取有效的token

### 5.2 数据不存在错误
- **错误**: `没有找到练习记录`、`未找到该提交记录`
- **原因**: 数据库中确实没有对应的数据
- **解决**: 检查参数是否正确，或先创建相关数据

### 5.3 系统内部错误
- **错误**: `500 系统内部错误`
- **原因**: 服务器内部异常
- **解决**: 检查服务器日志，联系技术支持

## 6. 测试环境准备

### 6.1 环境变量设置
```powershell
# 设置测试环境变量
$baseUrl = "http://localhost:8084"
$testToken = "your_test_token_here"
$courseId = 1
$submissionId = 1
```

### 6.2 测试数据准备
在测试接口之前，请确保：
1. 数据库中有相应的测试数据
2. 用户已正确登录并获取有效token
3. 相关的微服务（user-service、course-service、content-service）已启动

## 7. 注意事项

1. **认证要求**: 所有接口都需要在请求头中包含有效的`satoken`
2. **数据权限**: 用户只能访问自己的学习记录
3. **文件下载**: 导出接口会直接下载文件，请确保有足够的磁盘空间
4. **错误处理**: 建议在调用接口时添加适当的错误处理逻辑
5. **性能考虑**: 大量数据导出可能需要较长时间，请耐心等待

## 8. 更新日志

- **2024-01-15**: 初始版本，包含所有基础接口
- **2024-01-15**: 添加调试接口，优化错误处理
- **2024-01-15**: 完善接口文档，添加测试命令
