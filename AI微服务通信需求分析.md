# AI功能微服务通信需求分析报告

## 1. 当前AI功能分布分析

根据对Python AI微服务接口和learning-service代码的分析，发现以下AI功能分布情况：

### 1.1 已正确迁移到learning-service的AI功能
- ✅ 知识库文件上传 (`/embedding/upload`)
- ✅ 生成教学内容 (`/rag/generate`)
- ✅ 生成练习题 (`/rag/generate_exercise`)
- ✅ 主观题评估 (`/rag/evaluate_subjective`)
- ✅ 在线学习助手 (`/rag/assistant`)
- ✅ 学生自测练习生成 (`/rag/generate_student_exercise`)
- ✅ 选定题目自测生成 (`/rag/generate_selected_student_exercise`)

### 1.2 需要补全到learning-service的AI功能
- ❌ 教学内容细节生成 (`/rag/detail`)
- ❌ 重新生成教学内容 (`/rag/regenerate`)
- ❌ 课程优化建议 (`/rag/optimize_course`)
- ❌ 教学内容反馈修改 (`/rag/feedback`)
- ❌ 课时环节细节生成 (`/rag/step_detail`)
- ❌ 章节教学内容生成 (`/rag/generate_section`)
- ❌ 练习学情分析 (`/rag/analyze_exercise`)
- ❌ 视频摘要生成 (`/video/summary`)
- ❌ 文本摘要生成 (`/video/summary/text`)
- ❌ 存储路径管理接口组

## 2. 需要跨微服务通信的AI功能

### 2.1 需要course-service数据的功能
这些功能需要获取课程名称、课程大纲、章节信息等：

1. **生成练习题** (`/rag/generate_exercise`)
   - 需要从course-service获取：课程名称、章节内容
   - 当前状态：⚠️ 需要添加微服务调用逻辑

2. **教学内容生成** (`/rag/generate`)
   - 需要从course-service获取：课程信息、大纲
   - 当前状态：⚠️ 需要添加微服务调用逻辑

3. **课程优化建议** (`/rag/optimize_course`)
   - 需要从course-service获取：完整课程结构
   - 当前状态：⚠️ 需要添加微服务调用逻辑

### 2.2 需要user-service数据的功能
这些功能需要获取用户信息：

1. **在线学习助手** (`/rag/assistant`)
   - 需要从user-service获取：用户学习历史、偏好
   - 当前状态：⚠️ 需要添加用户上下文

2. **学生自测练习生成** (`/rag/generate_student_exercise`)
   - 需要获取学生的错题记录、学习进度
   - 当前状态：✅ 已有保存逻辑，但需要完善获取学生历史数据

### 2.3 需要自身数据库+外部微服务的功能
1. **练习学情分析** (`/rag/analyze_exercise`)
   - 需要learning_db中的：练习题目、学生答题统计
   - 需要course-service的：课程信息、章节信息
   - 当前状态：⚠️ 需要实现完整的数据聚合逻辑

## 3. 适合放在其他微服务的AI功能

### 3.1 适合放在content-service的功能
**视频摘要生成** (`/video/summary`, `/video/summary/text`)
- 理由：
  - 视频资源存储在content-service
  - 摘要结果也应存储在content-service的资源表中
  - 避免跨服务的大文件传输
  - content-service更适合处理资源相关的AI功能

- 建议：将视频摘要功能迁移到content-service

### 3.2 保留在learning-service的功能
其他所有AI功能都适合保留在learning-service，因为：
- 大部分与学习活动（练习、作业、自测）相关
- learning-service是学习活动的中心
- 便于统一管理AI服务调用日志

## 4. 微服务通信实现计划

### 4.1 需要在learning-service中添加的Client调用
```java
// 获取课程信息
CourseInfo courseInfo = courseClient.getCourseById(courseId);
String courseName = courseInfo.getName();
String courseOutline = courseInfo.getOutline();

// 获取章节信息  
SectionInfo sectionInfo = courseClient.getSectionById(sectionId);
String sectionContent = sectionInfo.getContent();

// 获取班级信息
ClassInfo classInfo = courseClient.getClassById(classId);
```

### 4.2 需要在content-service中实现的视频摘要功能
```java
@RestController
@RequestMapping("/api/content/ai")
public class ContentAiController {
    
    @PostMapping("/video/summary")
    public Map<String, Object> generateVideoSummary(@RequestBody Map<String, Object> req) {
        // 1. 从content_db获取视频信息
        // 2. 调用Python AI微服务生成摘要
        // 3. 将摘要结果存储到content_db
        // 4. 返回摘要结果
    }
}
```

## 5. 实现优先级

### 高优先级（核心功能）
1. ✅ 完善learning-service中所有AI接口的实现
2. ⚠️ 添加练习学情分析的完整数据聚合逻辑
3. ⚠️ 在生成练习题功能中添加course-service数据获取

### 中优先级（功能增强）
1. ⚠️ 在教学内容生成中添加课程信息获取
2. ⚠️ 在在线助手中添加用户学习历史获取
3. ⚠️ 实现课程优化建议的完整数据聚合

### 低优先级（功能迁移）
1. ⚠️ 将视频摘要功能迁移到content-service
2. ⚠️ 优化AI服务调用的性能和错误处理

## 6. 当前存在的问题

### 6.1 数据获取问题
- learning-service中很多地方直接使用courseId但无法获取课程详细信息
- 需要通过CourseClient获取课程名称、大纲等信息
- 错题分析、学情分析需要聚合多个数据源

### 6.2 接口完整性问题
- learning-service缺少多个AI接口的实现
- 已添加controller但service层方法需要完善
- 需要实现与Python AI微服务的完整对接

### 6.3 错误处理问题
- 微服务调用失败时的降级策略
- AI服务超时处理
- 数据不一致时的处理方案

## 7. 下一步行动计划

1. **立即执行**：补全learning-service中缺失的AI接口实现
2. **本周完成**：实现关键AI功能的微服务数据获取逻辑
3. **下周完成**：测试所有AI接口的完整功能流程
4. **后期优化**：考虑将视频摘要功能迁移到content-service
