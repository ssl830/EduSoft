#!/bin/bash

# Content Service API 完整测试脚本 (curl版本)
# 测试所有接口功能

echo "=== Content Service API 完整测试 (curl版本) ==="
echo "开始时间: $(date)"
echo ""

BASE_URL="http://localhost:8083"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 测试函数
test_api() {
    local test_name="$1"
    local method="$2"
    local url="$3"
    local data="$4"
    
    echo -e "${CYAN}测试: $test_name${NC}"
    
    if [ -n "$data" ]; then
        response=$(curl -s -w "\n%{http_code}" -X $method "$url" -H "Content-Type: application/json" -d "$data")
    else
        response=$(curl -s -w "\n%{http_code}" -X $method "$url")
    fi
    
    http_code=$(echo "$response" | tail -n1)
    body=$(echo "$response" | head -n -1)
    
    if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
        echo -e "${GREEN}✓ 成功 (HTTP $http_code)${NC}"
        echo "$body" | jq '.' 2>/dev/null || echo "$body"
    else
        echo -e "${RED}✗ 失败 (HTTP $http_code)${NC}"
        echo "$body"
    fi
    echo ""
}

# ==================== 文件管理接口测试 ====================

echo -e "${YELLOW}=== 1. 文件管理接口测试 ===${NC}"

# 1.1 获取所有文件
test_api "获取所有文件" "GET" "$BASE_URL/api/content/file/all"

# 1.2 文件上传
echo -e "${CYAN}测试: 文件上传${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/content/file/upload" \
  -F "file=@README.md" \
  -F "uploaderId=12345" \
  -F "uploaderName=测试用户" \
  -F "visibility=public" \
  -F "objectName=test_readme.md" \
  -F "fileUrl=http://example.com/test_readme.md")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
    echo -e "${GREEN}✓ 成功 (HTTP $http_code)${NC}"
    UPLOADED_FILE_ID=$(echo "$body" | jq -r '.id' 2>/dev/null || echo "1")
    echo "上传的文件ID: $UPLOADED_FILE_ID"
else
    echo -e "${RED}✗ 失败 (HTTP $http_code)${NC}"
    UPLOADED_FILE_ID=1
fi
echo ""

# 1.3 获取文件信息
test_api "获取文件信息" "GET" "$BASE_URL/api/content/file/$UPLOADED_FILE_ID"

# 1.4 获取用户文件列表
test_api "获取用户文件列表" "GET" "$BASE_URL/api/content/file/uploader/12345"

# 1.5 获取可见性文件列表
test_api "获取可见性文件列表" "GET" "$BASE_URL/api/content/file/visibility/public"

# ==================== 教学资源接口测试 ====================

echo -e "${YELLOW}=== 2. 教学资源接口测试 ===${NC}"

# 2.1 创建教学资源
resource_data='{
  "title": "测试教学资源",
  "description": "这是一个测试教学资源",
  "authorId": 123,
  "courseId": 456,
  "chapterId": 789,
  "chapterName": "测试章节",
  "resourceType": "video",
  "fileUrl": "http://example.com/test_video.mp4",
  "objectName": "test_video.mp4",
  "duration": 3600,
  "authorName": "测试作者",
  "tags": "测试,视频",
  "status": "published"
}'

echo -e "${CYAN}测试: 创建教学资源${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/content/resource" \
  -H "Content-Type: application/json" \
  -d "$resource_data")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
    echo -e "${GREEN}✓ 成功 (HTTP $http_code)${NC}"
    RESOURCE_ID=$(echo "$body" | jq -r '.id' 2>/dev/null || echo "1")
    echo "创建的资源ID: $RESOURCE_ID"
else
    echo -e "${RED}✗ 失败 (HTTP $http_code)${NC}"
    RESOURCE_ID=1
fi
echo ""

# 2.2 获取资源详情
test_api "获取资源详情" "GET" "$BASE_URL/api/content/resource/$RESOURCE_ID"

# 2.3 获取作者资源列表
test_api "获取作者资源列表" "GET" "$BASE_URL/api/content/resource/author/123"

# 2.4 获取课程资源列表
test_api "获取课程资源列表" "GET" "$BASE_URL/api/content/resource/course/456"

# 2.5 获取章节资源列表
test_api "获取章节资源列表" "GET" "$BASE_URL/api/content/resource/chapter/789"

# 2.6 获取类型资源列表
test_api "获取类型资源列表" "GET" "$BASE_URL/api/content/resource/type/video"

# 2.7 获取所有已发布资源
test_api "获取所有已发布资源" "GET" "$BASE_URL/api/content/resource/all"

# 2.8 更新资源
update_data='{
  "title": "更新后的教学资源",
  "description": "这是更新后的描述"
}'
test_api "更新资源" "PUT" "$BASE_URL/api/content/resource/$RESOURCE_ID" "$update_data"

# 2.9 增加浏览次数
test_api "增加浏览次数" "PUT" "$BASE_URL/api/content/resource/$RESOURCE_ID/view"

# 2.10 增加下载次数
test_api "增加下载次数" "PUT" "$BASE_URL/api/content/resource/$RESOURCE_ID/download"

# ==================== 学习进度接口测试 ====================

echo -e "${YELLOW}=== 3. 学习进度接口测试 ===${NC}"

# 3.1 创建学习进度
progress_data="{
  \"resourceId\": $RESOURCE_ID,
  \"studentId\": 123,
  \"progress\": 50.0,
  \"lastPosition\": 1800,
  \"watchCount\": 1
}"
test_api "创建学习进度" "POST" "$BASE_URL/api/content/learning-progress" "$progress_data"

# 3.2 获取学习进度
test_api "获取学习进度" "GET" "$BASE_URL/api/content/learning-progress/resource/$RESOURCE_ID/student/123"

# 3.3 更新学习进度
test_api "更新学习进度" "PUT" "$BASE_URL/api/content/learning-progress/resource/$RESOURCE_ID/student/123?progress=75&lastPosition=2700"

# 3.4 增加观看次数
test_api "增加观看次数" "PUT" "$BASE_URL/api/content/learning-progress/resource/$RESOURCE_ID/student/123/watch"

# 3.5 获取学生进度列表
test_api "获取学生进度列表" "GET" "$BASE_URL/api/content/learning-progress/student/123"

# 3.6 获取资源进度列表
test_api "获取资源进度列表" "GET" "$BASE_URL/api/content/learning-progress/resource/$RESOURCE_ID"

# ==================== 通知管理接口测试 ====================

echo -e "${YELLOW}=== 4. 通知管理接口测试 ===${NC}"

# 4.1 创建通知
notification_data="{
  \"userId\": 123,
  \"message\": \"这是一条测试通知\",
  \"type\": \"system\",
  \"relatedId\": $RESOURCE_ID,
  \"relatedType\": \"resource\"
}"
echo -e "${CYAN}测试: 创建通知${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/content/notification" \
  -H "Content-Type: application/json" \
  -d "$notification_data")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
    echo -e "${GREEN}✓ 成功 (HTTP $http_code)${NC}"
    NOTIFICATION_ID=$(echo "$body" | jq -r '.data.id' 2>/dev/null || echo "1")
    echo "创建的通知ID: $NOTIFICATION_ID"
else
    echo -e "${RED}✗ 失败 (HTTP $http_code)${NC}"
    NOTIFICATION_ID=1
fi
echo ""

# 4.2 获取通知详情
test_api "获取通知详情" "GET" "$BASE_URL/api/content/notification/$NOTIFICATION_ID"

# 4.3 获取用户通知列表
test_api "获取用户通知列表" "GET" "$BASE_URL/api/content/notification/user/123"

# 4.4 获取用户未读通知
test_api "获取用户未读通知" "GET" "$BASE_URL/api/content/notification/user/123/unread"

# 4.5 标记通知为已读
test_api "标记通知为已读" "PUT" "$BASE_URL/api/content/notification/$NOTIFICATION_ID/read"

# ==================== 讨论区接口测试 ====================

echo -e "${YELLOW}=== 5. 讨论区接口测试 ===${NC}"

# 5.1 创建讨论
discussion_data='{
  "title": "测试讨论主题",
  "content": "这是一个测试讨论内容",
  "creatorId": 123,
  "creatorName": "测试用户",
  "courseId": 456,
  "classId": 789,
  "type": "general",
  "status": "active"
}'
echo -e "${CYAN}测试: 创建讨论${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/content/discussion" \
  -H "Content-Type: application/json" \
  -d "$discussion_data")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
    echo -e "${GREEN}✓ 成功 (HTTP $http_code)${NC}"
    DISCUSSION_ID=$(echo "$body" | jq -r '.data.id' 2>/dev/null || echo "1")
    echo "创建的讨论ID: $DISCUSSION_ID"
else
    echo -e "${RED}✗ 失败 (HTTP $http_code)${NC}"
    DISCUSSION_ID=1
fi
echo ""

# 5.2 获取讨论详情
test_api "获取讨论详情" "GET" "$BASE_URL/api/content/discussion/$DISCUSSION_ID"

# 5.3 获取所有讨论
test_api "获取所有讨论" "GET" "$BASE_URL/api/content/discussion"

# 5.4 增加浏览次数
test_api "增加浏览次数" "PUT" "$BASE_URL/api/content/discussion/$DISCUSSION_ID/view"

# ==================== 讨论回复接口测试 ====================

echo -e "${YELLOW}=== 6. 讨论回复接口测试 ===${NC}"

# 6.1 创建回复
reply_data="{
  \"discussionId\": $DISCUSSION_ID,
  \"content\": \"这是一个测试回复\",
  \"replierId\": 456,
  \"replierName\": \"回复用户\",
  \"parentReplyId\": 0,
  \"isTeacherReply\": false
}"
echo -e "${CYAN}测试: 创建回复${NC}"
response=$(curl -s -w "\n%{http_code}" -X POST "$BASE_URL/api/content/discussion-reply" \
  -H "Content-Type: application/json" \
  -d "$reply_data")

http_code=$(echo "$response" | tail -n1)
body=$(echo "$response" | head -n -1)

if [ "$http_code" -ge 200 ] && [ "$http_code" -lt 300 ]; then
    echo -e "${GREEN}✓ 成功 (HTTP $http_code)${NC}"
    REPLY_ID=$(echo "$body" | jq -r '.data.id' 2>/dev/null || echo "1")
    echo "创建的回复ID: $REPLY_ID"
else
    echo -e "${RED}✗ 失败 (HTTP $http_code)${NC}"
    REPLY_ID=1
fi
echo ""

# 6.2 获取讨论回复列表
test_api "获取讨论回复列表" "GET" "$BASE_URL/api/content/discussion-reply/discussion/$DISCUSSION_ID"

# 6.3 标记为教师回复
test_api "标记为教师回复" "PUT" "$BASE_URL/api/content/discussion-reply/$REPLY_ID/teacher"

# ==================== 测试完成 ====================

echo -e "${GREEN}=== 测试完成 ===${NC}"
echo "结束时间: $(date)"
echo -e "${GREEN}所有接口测试已完成！${NC}"
