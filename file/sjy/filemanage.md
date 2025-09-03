# 文件资源管理文档

该控制器提供了与文件相关的基础功能，包括：**获取用户根目录文件夹、查询课程文件、上传、下载和预览文件**。所有接口均以 `/api` 为前缀。



---

## 2. 查询用户在某课程下的文件列表（支持过滤）

### 请求方式
```
POST /api/courses/{courseId}/filelist
```

### 请求参数
- **URL Path**
  - `courseId`: 所属课程 ID

- **Body (JSON)**
```json
{
  "userId": 123,
  "courseId": 101,
  "chapter": 5,
  "type": "PDF",
  "title": "Java编程"
}
```
 
### 参数说明
| 参数名     | 必填 | 类型   | 描述                     |
|------------|------|--------|--------------------------|
| userId     | 是   | Long   | 用户 ID                  |
| courseId   | 是   | Long   | 课程 ID                  |
| chapter    | 否   | Long   | 章节编号                 |
| type       | 否   | String | 文件类型（PDF/PPT/VIDEO等）|
| title      | 否   | String | 文件标题模糊匹配         |

### 响应格式
```json
{
  "code": 0,
  "message": "获取用户课程文件成功",
  "data": [
    {
      "id": 456,
      "name": "Java编程.pdf",
      "isDir": false,
      "parentId": 789,
      "courseId": 101,
      "classId": 201,
      "uploaderId": 123,
      "fileType": "PDF",
      "sectiondirId": null,
      "sectionId": 5,
      "lastVersionId": null,
      "isCurrentVersion": true,
      "fileSize": 2048,
      "visibility": "CLASS_ONLY",
      "url": "oss://...",
      "createdAt": "2023-01-01T00:00:00",
      "updatedAt": "2023-01-01T00:00:00",
      "version": 1,
      "objectName": "files/Java编程.pdf"
    }
  ]
}
```

---

## 3. 文件上传接口

### 请求方式
```
POST /api/courses/{courseId}/upload
```

### 请求参数
- **URL Path**
  - `courseId`: 所属课程 ID

- **Form Data**
  - `file`: 要上传的文件
  - `title`: 文件标题
  - `sectionId`: 所属章节 ID（可选）
  - `uploaderId`: 上传者 ID（可选）
  - `visibility`: 可见性（默认值：CLASS_ONLY）
  - `type`: 文件类型（可选）

### 参数说明
| 参数名       | 必填 | 类型          | 描述                           |
|--------------|------|---------------|--------------------------------|
| file         | 是   | MultipartFile | 上传的文件                     |
| title        | 是   | String        | 文件标题                       |
| sectionId    | 否   | Long          | 所属章节 ID                    |
| uploaderId   | 否   | Long          | 上传者 ID                      |
| visibility   | 否   | String        | 可见性（PUBLIC / CLASS_ONLY，默认 CLASS_ONLY）|
| type         | 否   | String        | 文件类型（如 PDF、PPT 等）     |

### 响应格式
```json
{
  "code": 0,
  "message": "上传成功",
  "data": {
    "id": 789,
    "name": "Java编程.pdf",
    "isDir": false,
    "parentId": 456,
    "courseId": 101,
    "classId": 201,
    "uploaderId": 123,
    "fileType": "PDF",
    "sectiondirId": null,
    "sectionId": 5,
    "lastVersionId": null,
    "isCurrentVersion": true,
    "fileSize": 2048,
    "visibility": "CLASS_ONLY",
    "url": "oss://...",
    "createdAt": "2023-01-01T00:00:00",
    "updatedAt": "2023-01-01T00:00:00",
    "version": 1,
    "objectName": "files/Java编程.pdf"
  }
}
```

---

## 4. 文件下载接口

### 请求方式
```
GET /api/resources/{resourceId}/download
```

### 请求参数
- **URL Path**
  - `resourceId`: 文件或文件夹 ID（字符串形式，后端会转换成Long）

### 响应格式
返回文件流（binary），用于浏览器下载。

---

## 5. 文件预览接口

### 请求方式
```
GET /api/resources/{resourceId}/preview
```

### 请求参数
- **URL Path**
  - `resourceId`: 文件或文件夹 ID（字符串形式，后端会转换成Long）

### 响应格式
返回文件流（binary），用于浏览器内联预览（如 PDF、图片等）。

---

## 通用响应结构

所有接口统一使用如下响应封装：

```java
Result<T>
```

其中：
- `code`: 状态码（0 表示成功）
- [message](file://d:\project_demo\EduSoft\src\main\java\org\example\edusoft\entity\Notification.java#L10-L10): 提示信息
- [data](file://d:\project_demo\EduSoft\src\main\java\org\example\edusoft\common\domain\Result.java#L16-L16): 返回数据（泛型 T）

---

## 错误处理

| HTTP Status Code | 描述                     |
|------------------|--------------------------|
| 400              | 参数错误（如 resourceId 格式错误）|
| 404              | 资源未找到               |
| 500              | 内部服务器错误           |

---

## 依赖服务说明

- [FileQueryService](file://d:\project_demo\EduSoft\src\main\java\org\example\edusoft\service\file\FileQueryService.java#L5-L30): 处理文件查询逻辑
- [FileUpload](file://d:\project_demo\EduSoft\src\main\java\org\example\edusoft\service\file\FileUpload.java#L8-L15): 处理文件上传逻辑
- [FileDownloadService](file://d:\project_demo\EduSoft\src\main\java\org\example\edusoft\service\file\FileDownloadService.java#L4-L9): 处理文件下载逻辑
- [FilePreviewService](file://d:\project_demo\EduSoft\src\main\java\org\example\edusoft\service\file\FilePreviewService.java#L4-L6): 处理文件预览逻辑
- [FolderService](file://d:\project_demo\EduSoft\src\main\java\org\example\edusoft\service\file\FolderService.java#L4-L14): 处理文件夹相关操作（当前未被使用）

---

## 注意事项

- 文件上传接口支持断点续传（根据业务实现）
- 文件下载和预览接口需确保文件存在且有访问权限
- 所有路径参数中涉及 ID 的字段均为 Long 类型，但接口接收为 String 以便兼容前端 URL 编码

--- 

以上即为 [FileController](file://d:\project_demo\EduSoft\src\main\java\org\example\edusoft\controller\file\FileController.java#L18-L154) 的完整接口说明文档。