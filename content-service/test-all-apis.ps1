# Content Service API 完整测试脚本
# 测试所有接口功能

Write-Host "=== Content Service API 完整测试 ===" -ForegroundColor Green
Write-Host "开始时间: $(Get-Date)" -ForegroundColor Cyan

$baseUrl = "http://localhost:8083"

# ==================== 文件管理接口测试 ====================

Write-Host "`n=== 1. 文件管理接口测试 ===" -ForegroundColor Yellow

# 1.1 获取所有文件
Write-Host "`n1.1 测试获取所有文件" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/file/all" -Method GET
    Write-Host "✓ 成功: 获取到 $($response.Count) 个文件" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 1.2 文件上传
Write-Host "`n1.2 测试文件上传" -ForegroundColor Cyan
try {
    $form = @{
        file = Get-Item "README.md"
        uploaderId = "12345"
        uploaderName = "测试用户"
        visibility = "public"
        objectName = "test_readme.md"
        fileUrl = "http://example.com/test_readme.md"
    }
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/file/upload" -Method POST -Form $form
    Write-Host "✓ 成功: 文件上传成功，ID: $($response.id)" -ForegroundColor Green
    $uploadedFileId = $response.id
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
    $uploadedFileId = 1  # 使用默认ID继续测试
}

# 1.3 获取文件信息
Write-Host "`n1.3 测试获取文件信息" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/file/$uploadedFileId" -Method GET
    Write-Host "✓ 成功: 文件名称: $($response.fileName)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 1.4 获取用户文件列表
Write-Host "`n1.4 测试获取用户文件列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/file/uploader/12345" -Method GET
    Write-Host "✓ 成功: 用户文件数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 1.5 获取可见性文件列表
Write-Host "`n1.5 测试获取可见性文件列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/file/visibility/public" -Method GET
    Write-Host "✓ 成功: 公开文件数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 1.6 更新文件可见性
Write-Host "`n1.6 测试更新文件可见性" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/file/$uploadedFileId/visibility" -Method PUT -Body "private" -ContentType "text/plain"
    Write-Host "✓ 成功: 文件可见性更新成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# ==================== 教学资源接口测试 ====================

Write-Host "`n=== 2. 教学资源接口测试 ===" -ForegroundColor Yellow

# 2.1 创建教学资源
Write-Host "`n2.1 测试创建教学资源" -ForegroundColor Cyan
try {
    $resourceData = @{
        title = "测试教学资源"
        description = "这是一个测试教学资源"
        authorId = 123
        courseId = 456
        chapterId = 789
        chapterName = "测试章节"
        resourceType = "video"
        fileUrl = "http://example.com/test_video.mp4"
        objectName = "test_video.mp4"
        duration = 3600
        authorName = "测试作者"
        tags = "测试,视频"
        status = "published"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource" -Method POST -Body $resourceData -ContentType "application/json"
    Write-Host "✓ 成功: 教学资源创建成功，ID: $($response.id)" -ForegroundColor Green
    $resourceId = $response.id
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
    $resourceId = 1  # 使用默认ID继续测试
}

# 2.2 获取资源详情
Write-Host "`n2.2 测试获取资源详情" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/$resourceId" -Method GET
    Write-Host "✓ 成功: 资源标题: $($response.title)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.3 获取作者资源列表
Write-Host "`n2.3 测试获取作者资源列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/author/123" -Method GET
    Write-Host "✓ 成功: 作者资源数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.4 获取课程资源列表
Write-Host "`n2.4 测试获取课程资源列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/course/456" -Method GET
    Write-Host "✓ 成功: 课程资源数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.5 获取章节资源列表
Write-Host "`n2.5 测试获取章节资源列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/chapter/789" -Method GET
    Write-Host "✓ 成功: 章节资源数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.6 获取类型资源列表
Write-Host "`n2.6 测试获取类型资源列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/type/video" -Method GET
    Write-Host "✓ 成功: 视频资源数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.7 获取所有已发布资源
Write-Host "`n2.7 测试获取所有已发布资源" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/all" -Method GET
    Write-Host "✓ 成功: 已发布资源数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.8 更新资源
Write-Host "`n2.8 测试更新资源" -ForegroundColor Cyan
try {
    $updateData = @{
        title = "更新后的教学资源"
        description = "这是更新后的描述"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/$resourceId" -Method PUT -Body $updateData -ContentType "application/json"
    Write-Host "✓ 成功: 资源更新成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.9 增加浏览次数
Write-Host "`n2.9 测试增加浏览次数" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/$resourceId/view" -Method PUT
    Write-Host "✓ 成功: 浏览次数增加成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 2.10 增加下载次数
Write-Host "`n2.10 测试增加下载次数" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/resource/$resourceId/download" -Method PUT
    Write-Host "✓ 成功: 下载次数增加成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# ==================== 学习进度接口测试 ====================

Write-Host "`n=== 3. 学习进度接口测试 ===" -ForegroundColor Yellow

# 3.1 创建学习进度
Write-Host "`n3.1 测试创建学习进度" -ForegroundColor Cyan
try {
    $progressData = @{
        resourceId = $resourceId
        studentId = 123
        progress = 50.0
        lastPosition = 1800
        watchCount = 1
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/learning-progress" -Method POST -Body $progressData -ContentType "application/json"
    Write-Host "✓ 成功: 学习进度创建成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3.2 获取学习进度
Write-Host "`n3.2 测试获取学习进度" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/learning-progress/resource/$resourceId/student/123" -Method GET
    Write-Host "✓ 成功: 学习进度: $($response.progress)%" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3.3 更新学习进度
Write-Host "`n3.3 测试更新学习进度" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/learning-progress/resource/$resourceId/student/123?progress=75&lastPosition=2700" -Method PUT
    Write-Host "✓ 成功: 学习进度更新成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3.4 增加观看次数
Write-Host "`n3.4 测试增加观看次数" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/learning-progress/resource/$resourceId/student/123/watch" -Method PUT
    Write-Host "✓ 成功: 观看次数增加成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3.5 获取学生进度列表
Write-Host "`n3.5 测试获取学生进度列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/learning-progress/student/123" -Method GET
    Write-Host "✓ 成功: 学生进度记录数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 3.6 获取资源进度列表
Write-Host "`n3.6 测试获取资源进度列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/learning-progress/resource/$resourceId" -Method GET
    Write-Host "✓ 成功: 资源进度记录数量: $($response.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# ==================== 通知管理接口测试 ====================

Write-Host "`n=== 4. 通知管理接口测试 ===" -ForegroundColor Yellow

# 4.1 创建通知
Write-Host "`n4.1 测试创建通知" -ForegroundColor Cyan
try {
    $notificationData = @{
        userId = 123
        message = "这是一条测试通知"
        type = "system"
        relatedId = $resourceId
        relatedType = "resource"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/notification" -Method POST -Body $notificationData -ContentType "application/json"
    Write-Host "✓ 成功: 通知创建成功" -ForegroundColor Green
    $notificationId = $response.data.id
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
    $notificationId = 1
}

# 4.2 获取通知详情
Write-Host "`n4.2 测试获取通知详情" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/notification/$notificationId" -Method GET
    Write-Host "✓ 成功: 通知消息: $($response.data.message)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4.3 获取用户通知列表
Write-Host "`n4.3 测试获取用户通知列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/notification/user/123" -Method GET
    Write-Host "✓ 成功: 用户通知数量: $($response.data.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4.4 获取用户未读通知
Write-Host "`n4.4 测试获取用户未读通知" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/notification/user/123/unread" -Method GET
    Write-Host "✓ 成功: 未读通知数量: $($response.data.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 4.5 标记通知为已读
Write-Host "`n4.5 测试标记通知为已读" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/notification/$notificationId/read" -Method PUT
    Write-Host "✓ 成功: 通知标记为已读" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# ==================== 讨论区接口测试 ====================

Write-Host "`n=== 5. 讨论区接口测试 ===" -ForegroundColor Yellow

# 5.1 创建讨论
Write-Host "`n5.1 测试创建讨论" -ForegroundColor Cyan
try {
    $discussionData = @{
        title = "测试讨论主题"
        content = "这是一个测试讨论内容"
        creatorId = 123
        creatorName = "测试用户"
        courseId = 456
        classId = 789
        type = "general"
        status = "active"
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/discussion" -Method POST -Body $discussionData -ContentType "application/json"
    Write-Host "✓ 成功: 讨论创建成功" -ForegroundColor Green
    $discussionId = $response.data.id
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
    $discussionId = 1
}

# 5.2 获取讨论详情
Write-Host "`n5.2 测试获取讨论详情" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/discussion/$discussionId" -Method GET
    Write-Host "✓ 成功: 讨论标题: $($response.data.title)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 5.3 获取所有讨论
Write-Host "`n5.3 测试获取所有讨论" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/discussion" -Method GET
    Write-Host "✓ 成功: 讨论总数: $($response.data.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 5.4 增加浏览次数
Write-Host "`n5.4 测试增加浏览次数" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/discussion/$discussionId/view" -Method PUT
    Write-Host "✓ 成功: 浏览次数增加成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# ==================== 讨论回复接口测试 ====================

Write-Host "`n=== 6. 讨论回复接口测试 ===" -ForegroundColor Yellow

# 6.1 创建回复
Write-Host "`n6.1 测试创建回复" -ForegroundColor Cyan
try {
    $replyData = @{
        discussionId = $discussionId
        content = "这是一个测试回复"
        replierId = 456
        replierName = "回复用户"
        parentReplyId = 0
        isTeacherReply = false
    } | ConvertTo-Json
    
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/discussion-reply" -Method POST -Body $replyData -ContentType "application/json"
    Write-Host "✓ 成功: 回复创建成功" -ForegroundColor Green
    $replyId = $response.data.id
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
    $replyId = 1
}

# 6.2 获取讨论回复列表
Write-Host "`n6.2 测试获取讨论回复列表" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/discussion-reply/discussion/$discussionId" -Method GET
    Write-Host "✓ 成功: 回复数量: $($response.data.Count)" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# 6.3 标记为教师回复
Write-Host "`n6.3 测试标记为教师回复" -ForegroundColor Cyan
try {
    $response = Invoke-RestMethod -Uri "$baseUrl/api/content/discussion-reply/$replyId/teacher" -Method PUT
    Write-Host "✓ 成功: 标记为教师回复成功" -ForegroundColor Green
} catch {
    Write-Host "✗ 失败: $($_.Exception.Message)" -ForegroundColor Red
}

# ==================== 测试完成 ====================

Write-Host "`n=== 测试完成 ===" -ForegroundColor Green
Write-Host "结束时间: $(Get-Date)" -ForegroundColor Cyan
Write-Host "所有接口测试已完成！" -ForegroundColor Green
