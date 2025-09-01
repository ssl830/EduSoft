# AI功能微服务通信需求分析报告

## 1. 概述

本报告分析了AI功能在微服务架构下的通信需求，识别哪些AI接口需要跨微服务获取数据，以及如何优化服务间通信。

## 2. 当前AI功能分布

### 2.1 learning-service中的AI功能
所有AI功能目前都集中在learning-service中，作为AI请求的统一转发中心。

**优势：**
- 统一的AI接口管理
- 简化前端调用
- 便于AI调用日志管理

**挑战：**
- 需要频繁的跨微服务数据获取
- 可能成为性能瓶颈

### 2.2 需要跨微服务通信的数据类型

#### 课程相关数据（来源：course-service）
- course表：课程基本信息
- course_section表：章节信息
- course_outline表：课程大纲

#### 用户相关数据（来源：user-service）
- user表：用户基本信息
- class表：班级信息

#### 内容相关数据（来源：content-service）
- file_node表：文件信息
- video_summary_stage表：视频摘要相关

## 3. AI接口跨微服务通信需求分析

### 3.1 高频跨服务通信接口

#### `/rag/generate` - 生成教学内容
**需要的数据：**
- 课程信息（course-service）
- 用户信息（user-service）

**通信方案：**
```java
// 在AiAssistantService中添加
@Autowired
private CourseServiceClient courseServiceClient;

public Map<String, Object> generateTeachingContent(Map<String, Object> req) {
    // 获取课程信息
    if (req.containsKey("course_id")) {
        Long courseId = Long.parseLong(req.get("course_id").toString());
        CourseInfo courseInfo = courseServiceClient.getCourseInfo(courseId);
        req.put("course_name", courseInfo.getCourseName());
        req.put("course_description", courseInfo.getDescription());
    }
    
    return aiServiceClient.callAiService("/rag/generate", req);
}
```

#### `/rag/generate_exercise` - 生成练习题
**需要的数据：**
- 课程信息（course-service）
- 章节内容（course-service）

**通信方案：**
```java
public Map<String, Object> generateExercises(Map<String, Object> req) {
    // 获取课程和章节信息
    enrichWithCourseData(req);
    return aiServiceClient.callAiService("/rag/generate_exercise", req);
}

private void enrichWithCourseData(Map<String, Object> req) {
    if (req.containsKey("course_id")) {
        Long courseId = Long.parseLong(req.get("course_id").toString());
        CourseInfo courseInfo = courseServiceClient.getCourseInfo(courseId);
        req.put("course_name", courseInfo.getCourseName());
    }
    
    if (req.containsKey("section_id")) {
        Long sectionId = Long.parseLong(req.get("section_id").toString());
        SectionInfo sectionInfo = courseServiceClient.getSectionInfo(sectionId);
        req.put("lesson_content", sectionInfo.getContent());
    }
}
```

#### `/rag/generate_student_exercise` - 学生自测练习生成
**需要的数据：**
- 课程信息（course-service）
- 章节信息（course-service）
- 学生信息（user-service）

#### `/analyze-exercise` - 学情分析
**需要的数据：**
- 练习记录（本地learning_db）
- 课程信息（course-service）
- 学生信息（user-service）

### 3.2 中频跨服务通信接口

#### `/rag/optimize_course` - 课程优化
**需要的数据：**
- 完整课程信息（course-service）
- 学生反馈数据（可能分布在多个服务）

#### `/rag/assistant` - 在线学习助手
**需要的数据：**
- 当前学习上下文（course-service）
- 学习进度（本地或user-service）

### 3.3 低频跨服务通信接口

#### 存储管理接口
这些接口主要与AI微服务的存储管理相关，通常不需要业务数据。

## 4. 建议的微服务通信实现

### 4.1 创建微服务客户端

#### CourseServiceClient
```java
@Component
public class CourseServiceClient {
    
    @Value("${course.service.url:http://localhost:8081}")
    private String courseServiceUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public CourseInfo getCourseInfo(Long courseId) {
        String url = courseServiceUrl + "/api/course/info/" + courseId;
        return restTemplate.getForObject(url, CourseInfo.class);
    }
    
    public SectionInfo getSectionInfo(Long sectionId) {
        String url = courseServiceUrl + "/api/section/info/" + sectionId;
        return restTemplate.getForObject(url, SectionInfo.class);
    }
    
    public List<SectionInfo> getCourseSections(Long courseId) {
        String url = courseServiceUrl + "/api/course/" + courseId + "/sections";
        return restTemplate.getForObject(url, List.class);
    }
}
```

#### UserServiceClient
```java
@Component
public class UserServiceClient {
    
    @Value("${user.service.url:http://localhost:8082}")
    private String userServiceUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    public UserInfo getUserInfo(Long userId) {
        String url = userServiceUrl + "/api/user/info/" + userId;
        return restTemplate.getForObject(url, UserInfo.class);
    }
    
    public ClassInfo getClassInfo(Long classId) {
        String url = userServiceUrl + "/api/class/info/" + classId;
        return restTemplate.getForObject(url, ClassInfo.class);
    }
}
```

### 4.2 数据传输对象（DTO）

```java
// CourseInfo.java
public class CourseInfo {
    private Long courseId;
    private String courseName;
    private String description;
    private String courseOutline;
    // getters and setters
}

// SectionInfo.java
public class SectionInfo {
    private Long sectionId;
    private String sectionTitle;
    private String content;
    private Integer orderNum;
    // getters and setters
}

// UserInfo.java
public class UserInfo {
    private Long userId;
    private String username;
    private String realName;
    private String role;
    // getters and setters
}
```

## 5. 性能优化建议

### 5.1 缓存策略
```java
@Service
public class AiServiceDataCache {
    
    @Cacheable(value = "courseInfo", key = "#courseId")
    public CourseInfo getCourseInfo(Long courseId) {
        return courseServiceClient.getCourseInfo(courseId);
    }
    
    @Cacheable(value = "sectionInfo", key = "#sectionId")
    public SectionInfo getSectionInfo(Long sectionId) {
        return courseServiceClient.getSectionInfo(sectionId);
    }
}
```

### 5.2 批量查询
对于需要多个相关数据的接口，实现批量查询以减少网络调用：

```java
public Map<String, Object> generateCourseContentBatch(List<Long> sectionIds) {
    // 批量获取章节信息
    List<SectionInfo> sections = courseServiceClient.getBatchSections(sectionIds);
    // 批量处理
}
```

### 5.3 异步处理
对于非实时性要求高的AI功能，可以考虑异步处理：

```java
@Async
public CompletableFuture<Map<String, Object>> generateContentAsync(Map<String, Object> req) {
    // 异步生成内容
    return CompletableFuture.completedFuture(result);
}
```

## 6. 特殊情况分析：视频摘要功能

### 6.1 当前状态
视频摘要功能目前在learning-service中，但是：
- 视频文件存储在content-service管理的file_node表中
- video_summary_stage表在content-service的数据库中

### 6.2 迁移建议
**建议将视频摘要功能迁移到content-service：**

**理由：**
1. 数据就近原则：video相关的数据都在content-service
2. 减少跨服务通信：避免频繁的文件信息查询
3. 职责明确：content-service负责所有内容相关的AI功能

**迁移步骤：**
1. 在content-service中创建VideoAiController
2. 移植video相关的AI接口
3. 更新前端调用路径
4. 从learning-service移除相关代码

```java
// content-service中的实现
@RestController
@RequestMapping("/api/content/ai")
public class VideoAiController {
    
    @PostMapping("/video/summary")
    public Map<String, Object> generateVideoSummary(@RequestBody Map<String, Object> req) {
        // 1. 从本地数据库获取视频信息
        // 2. 调用AI微服务生成摘要
        // 3. 保存结果到video_summary_stage表
    }
}
```

## 7. 实施优先级

### 高优先级
1. 实现CourseServiceClient - 支持课程和章节信息查询
2. 完善generateExercises方法的跨服务数据获取
3. 实现数据缓存机制

### 中优先级
1. 实现UserServiceClient - 支持用户和班级信息查询
2. 优化学情分析接口的数据聚合
3. 实现批量查询优化

### 低优先级
1. 视频摘要功能迁移到content-service
2. 实现异步处理机制
3. 性能监控和优化

## 8. 风险评估

### 8.1 网络延迟风险
**风险：** 跨服务调用增加响应时间
**缓解措施：** 缓存、批量查询、异步处理

### 8.2 服务依赖风险
**风险：** 依赖服务不可用影响AI功能
**缓解措施：** 熔断器、降级策略、默认值处理

### 8.3 数据一致性风险
**风险：** 跨服务数据可能不一致
**缓解措施：** 缓存失效策略、数据版本控制

## 9. 监控建议

### 9.1 指标监控
- 跨服务调用次数和耗时
- AI接口响应时间
- 缓存命中率

### 9.2 日志记录
```java
@Around("execution(* org.example.edusoft.learning.service.ai.*.*(..))")
public Object logAiServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {
    long startTime = System.currentTimeMillis();
    String methodName = joinPoint.getSignature().getName();
    
    try {
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - startTime;
        log.info("AI service call [{}] completed in {}ms", methodName, duration);
        return result;
    } catch (Exception e) {
        log.error("AI service call [{}] failed: {}", methodName, e.getMessage());
        throw e;
    }
}
```

## 10. 结论

AI功能的微服务化需要仔细规划跨服务通信策略。通过实现合适的服务客户端、缓存机制和性能优化，可以在保持功能完整性的同时，确保系统的性能和可靠性。

建议优先实现CourseServiceClient，因为大部分AI功能都需要课程相关数据。同时，考虑将视频摘要功能迁移到content-service以实现更好的数据局部性。
