# AI微服务接口格式修复报告

## 问题背景

在AI功能迁移过程中，发现Java端发送给Python AI微服务的JSON格式与Python微服务期望的格式不匹配，导致接口调用失败，返回422 Unprocessable Entity错误。

## 主要问题

### 1. 练习生成接口格式不匹配

**问题描述：**
- Java端发送: `{"prompt": "生成5道关于Java基础的单选题"}`
- Python微服务期望: `{"course_name": "string", "lesson_content": "string", "difficulty": "medium", "choose_count": 5, "fill_blank_count": 5, "question_count": 2}`

**修复位置：**
- `learning-service/src/main/java/org/example/edusoft/learning/ai/AIServiceClient.java`
- `learning-service/src/main/java/org/example/edusoft/learning/controller/ai/AiAssistantController.java`
- `common-lib/src/main/java/org/example/edusoft/common/ai/CommonAIServiceClient.java`

**修复方案：**
1. **AIServiceClient.generateExercise()** - 添加了`parsePromptToStandardRequest()`方法，将用户的自然语言prompt解析为AI微服务期望的格式
2. **AiAssistantController.generateExercises()** - 添加了格式检测和转换逻辑，支持两种格式
3. **CommonAIServiceClient.generateExercise()** - 修改为发送正确的JSON格式

### 2. 主观题评估接口格式不匹配

**问题描述：**
- Java端发送: `{"question": "题目", "student_answer": "学生答案", "standard_answer": "标准答案"}`
- Python微服务期望: `{"question": "题目", "student_answer": "学生答案", "reference_answer": "标准答案", "max_score": 10.0}`

**修复位置：**
- `common-lib/src/main/java/org/example/edusoft/common/ai/CommonAIServiceClient.java`
- `learning-service/src/main/java/org/example/edusoft/learning/ai/AIServiceClient.java`

**修复方案：**
1. 字段名修改：`standard_answer` → `reference_answer`
2. 添加缺失字段：`max_score`（默认值10.0）

### 3. 教学内容生成接口格式不匹配

**问题描述：**
- Java端发送: `{"courseOutline": "大纲", "courseName": "课程名", "expectedHours": 40}`
- Python微服务期望: `{"course_outline": "大纲", "course_name": "课程名", "expected_hours": 40}`

**修复位置：**
- `learning-service/src/main/java/org/example/edusoft/learning/ai/AIServiceClient.java`

**修复方案：**
- 字段名修改：camelCase → snake_case格式

## 修复后的接口映射

### 1. 练习生成接口 `/rag/generate_exercise`

```java
// 支持两种输入格式：

// 格式1：简单prompt（自动转换）
{
  "prompt": "生成5道关于Java基础的单选题，包含选项、答案和解析"
}

// 格式2：标准格式（直接转发）
{
  "course_name": "Java编程基础",
  "lesson_content": "Java基础知识",
  "difficulty": "medium",
  "choose_count": 5,
  "fill_blank_count": 3,
  "question_count": 2
}
```

### 2. 主观题评估接口 `/rag/evaluate_subjective`

```java
{
  "question": "题目内容",
  "student_answer": "学生答案",
  "reference_answer": "标准答案",
  "max_score": 10.0
}
```

### 3. 教学内容生成接口 `/rag/generate_teaching_content`

```java
{
  "course_outline": "课程大纲",
  "course_name": "课程名称",
  "expected_hours": 40
}
```

## 智能转换功能

### prompt解析器特性

在`AIServiceClient.parsePromptToStandardRequest()`和`AiAssistantController.convertPromptToStandardRequest()`中实现了智能解析：

1. **课程名称识别**：
   - "java" → "Java编程基础"
   - "python" → "Python编程"
   - "数据库" → "数据库原理"
   - "算法" → "数据结构与算法"

2. **难度识别**：
   - "简单"/"基础" → "easy"
   - "困难"/"高级" → "hard"
   - 默认 → "medium"

3. **题目数量解析**：
   - 正则表达式提取数字
   - 根据题目类型（选择/填空）智能分配
   - 默认：选择题5道，填空题3道，问答题2道

## 兼容性保证

修复后的接口既支持原有的标准格式调用，也支持简化的prompt格式调用，确保了：

1. **向后兼容**：现有的标准格式调用不受影响
2. **用户友好**：SelfPractice等模块可以继续使用简单的prompt格式
3. **智能转换**：自动将自然语言需求转换为结构化参数

## 测试验证

修复后可以通过以下测试验证：

```http
### 测试练习生成功能（prompt格式）
POST http://localhost:8084/api/learning/ai/rag/generate_exercise
Content-Type: application/json
X-User-Id: 201

{
  "prompt": "生成5道关于Java基础的单选题，包含选项、答案和解析"
}

### 测试练习生成功能（标准格式）
POST http://localhost:8084/api/learning/ai/rag/generate_exercise
Content-Type: application/json
X-User-Id: 201

{
  "course_name": "Java编程基础",
  "lesson_content": "Java面向对象编程",
  "difficulty": "medium",
  "choose_count": 5,
  "fill_blank_count": 0,
  "question_count": 0
}
```

## 影响范围

本次修复影响的组件：

1. **learning-service** - 核心AI功能调用
2. **common-lib** - 跨服务AI调用
3. **course-service** - 通过common-lib调用AI功能
4. **其他微服务** - 通过common-lib调用AI功能

## 总结

通过本次修复，解决了AI微服务调用中的格式不匹配问题，确保了：

1. ✅ 练习生成功能正常工作
2. ✅ 主观题AI评分功能正常工作  
3. ✅ 教学内容生成功能正常工作
4. ✅ 跨微服务AI调用功能正常工作
5. ✅ 向后兼容性保证
6. ✅ 用户体验优化（支持自然语言prompt）

所有AI相关接口现在都能正确地与Python AI微服务通信，不再出现422格式错误。
