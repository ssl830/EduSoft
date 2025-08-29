# Content Service API 测试命令

## 1. 使用 PowerShell 测试

### 测试获取所有文件
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/content/file/all" -Method GET
```

### 测试创建学习进度
```powershell
$data = @{
    resourceId = 1
    studentId = 123
    progress = 50.0
    lastPosition = 1800
    watchCount = 1
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8083/api/content/learning-progress" -Method POST -Body $data -ContentType "application/json"
```

### 测试获取学习进度
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/content/learning-progress/resource/1/student/123" -Method GET
```

### 测试更新学习进度
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/content/learning-progress/resource/1/student/123?progress=75&lastPosition=2700" -Method PUT
```

### 测试增加观看次数
```powershell
Invoke-RestMethod -Uri "http://localhost:8083/api/content/learning-progress/resource/1/student/123/watch" -Method PUT
```

## 2. 使用 curl 测试（如果安装了 curl）

### 测试获取所有文件
```bash
curl -X GET "http://localhost:8083/api/content/file/all"
```

### 测试创建学习进度
```bash
curl -X POST "http://localhost:8083/api/content/learning-progress" \
  -H "Content-Type: application/json" \
  -d '{
    "resourceId": 1,
    "studentId": 123,
    "progress": 50.0,
    "lastPosition": 1800,
    "watchCount": 1
  }'
```

### 测试获取学习进度
```bash
curl -X GET "http://localhost:8083/api/content/learning-progress/resource/1/student/123"
```

### 测试更新学习进度
```bash
curl -X PUT "http://localhost:8083/api/content/learning-progress/resource/1/student/123?progress=75&lastPosition=2700"
```

### 测试增加观看次数
```bash
curl -X PUT "http://localhost:8083/api/content/learning-progress/resource/1/student/123/watch"
```

## 3. 使用 httpie 测试（如果安装了 httpie）

### 测试获取所有文件
```bash
http GET http://localhost:8083/api/content/file/all
```

### 测试创建学习进度
```bash
http POST http://localhost:8083/api/content/learning-progress \
  resourceId:=1 \
  studentId:=123 \
  progress:=50.0 \
  lastPosition:=1800 \
  watchCount:=1
```

### 测试获取学习进度
```bash
http GET http://localhost:8083/api/content/learning-progress/resource/1/student/123
```

### 测试更新学习进度
```bash
http PUT http://localhost:8083/api/content/learning-progress/resource/1/student/123 progress==75 lastPosition==2700
```

### 测试增加观看次数
```bash
http PUT http://localhost:8083/api/content/learning-progress/resource/1/student/123/watch
```

## 4. 文件上传测试

### 使用 PowerShell
```powershell
$form = @{
    file = Get-Item "README.md"
    uploaderId = "12345"
    visibility = "public"
    objectName = "myfile.txt"
    fileUrl = "http://example.com/a.txt"
}

Invoke-RestMethod -Uri "http://localhost:8083/api/content/file/upload" -Method POST -Form $form
```

### 使用 curl
```bash
curl -X POST "http://localhost:8083/api/content/file/upload" \
  -F "file=@README.md" \
  -F "uploaderId=12345" \
  -F "visibility=public" \
  -F "objectName=myfile.txt" \
  -F "fileUrl=http://example.com/a.txt"
```

### 使用 httpie
```bash
http -f POST http://localhost:8083/api/content/file/upload \
  file@README.md \
  uploaderId=12345 \
  visibility=public \
  objectName=myfile.txt \
  fileUrl=http://example.com/a.txt
```
