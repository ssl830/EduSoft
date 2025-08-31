# AI微服务架构重构说明

## 背景

在原本的拆分计划中，我们将AI功能拆分成了两个独立的微服务：
1. Java AI微服务（处理业务逻辑、调用记录等）
2. Python AI微服务（处理核心AI算法）

这种设计存在问题：
- AI功能被人为分裂，增加了复杂性
- 两个AI微服务之间存在强依赖关系
- 维护和部署复杂度增加

## 重构方案

我们采用了新的架构策略：

### 1. 保留Python AI微服务
- 专注于核心AI算法处理
- 提供RAG问答、练习生成、内容评估等核心AI功能
- 独立部署，提供HTTP API服务

### 2. 将Java AI代码集中到learning-service
- 将原本的AI相关Java代码迁移到learning-service
- learning-service负责AI功能的业务逻辑、权限控制、调用记录等
- learning-service通过HTTP调用Python AI微服务

### 3. 其他微服务通过CommonAIServiceClient调用
- 在common-lib中提供CommonAIServiceClient
- 各微服务需要AI功能时，通过CommonAIServiceClient调用learning-service
- learning-service作为AI功能的统一入口

## 架构图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   User Service  │    │ Course Service  │    │ Content Service │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          │                      │                      │
          │              CommonAIServiceClient          │
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                                 ▼
                    ┌─────────────────────┐
                    │  Learning Service   │
                    │   (含AI业务逻辑)      │
                    └─────────┬───────────┘
                              │ HTTP调用
                              ▼
                    ┌─────────────────────┐
                    │ Python AI Service   │
                    │   (核心AI算法)       │
                    └─────────────────────┘
```

## 代码迁移情况

### 已迁移到learning-service的文件：

#### AI核心组件
- `/ai/AIServiceClient.java` → `/learning/ai/AIServiceClient.java`
- `/ai/MultipartInputStreamFileResource.java` → `/learning/ai/MultipartInputStreamFileResource.java`

#### 实体类
- `/entity/ai/AiServiceCallLog.java` → `/learning/entity/ai/AiServiceCallLog.java`
- `/entity/chat/ChatSession.java` → `/learning/entity/chat/ChatSession.java`
- `/entity/chat/ChatMessage.java` → `/learning/entity/chat/ChatMessage.java`
- `/entity/chat/ChatMemorySummary.java` → `/learning/entity/chat/ChatMemorySummary.java`

#### Mapper
- `/mapper/ai/AiServiceCallLogMapper.java` → `/learning/mapper/ai/AiServiceCallLogMapper.java`
- `/mapper/chat/ChatSessionMapper.java` → `/learning/mapper/chat/ChatSessionMapper.java`
- `/mapper/chat/ChatMessageMapper.java` → `/learning/mapper/chat/ChatMessageMapper.java`
- `/mapper/chat/ChatMemorySummaryMapper.java` → `/learning/mapper/chat/ChatMemorySummaryMapper.java`

#### 服务类
- `/service/ai/AiAssistantService.java` → `/learning/service/ai/AiAssistantService.java`
- `/service/ai/AiServiceCaller.java` → `/learning/service/ai/AiServiceCaller.java`
- `/service/chat/ChatManagementService.java` → `/learning/service/chat/ChatManagementService.java`
- `/service/chat/ChatMemoryService.java` → `/learning/service/chat/ChatMemoryService.java`

#### 控制器
- `/controller/ai/AiAssistantController.java` → `/learning/controller/ai/AiAssistantController.java`
- `/controller/chat/ChatController.java` → `/learning/controller/chat/ChatController.java`

#### DTO
- `/dto/ai/AiAskRequest.java` → `/learning/dto/ai/AiAskRequest.java`
- `/dto/ai/AiAskResponse.java` → `/learning/dto/ai/AiAskResponse.java`

### 通用AI客户端
- 在common-lib中创建了`CommonAIServiceClient.java`
- 提供给其他微服务使用的统一AI调用接口

## 数据库更新

### learning_db.sql 新增表：
- `ai_service_call_log` - AI服务调用日志
- `chat_session` - 聊天会话
- `chat_message` - 聊天消息  
- `chat_memory_summary` - 聊天记忆摘要
- `wrong_question` - 错题记录
- `favorite_question` - 收藏题目

## API路径变更

### 原路径：
- `/api/ai/*` 

### 新路径：
- `/api/learning/ai/*` (learning-service提供)
- `/api/learning/chat/*` (learning-service提供)

### 其他微服务调用方式：
```java
@Autowired
private CommonAIServiceClient aiClient;

// 调用AI助手
Map<String, Object> result = aiClient.askAI(question, context);

// 生成练习
Map<String, Object> exercises = aiClient.generateExercise(courseContent);

// 评估主观题
Map<String, Object> evaluation = aiClient.evaluateSubjectiveAnswer(question, answer, standardAnswer);
```

## 优势

1. **架构清晰**：避免了AI功能分裂，保持了功能的内聚性
2. **维护简单**：AI相关的Java代码集中在learning-service，便于维护
3. **调用统一**：其他微服务通过统一的客户端调用AI功能
4. **扩展性好**：可以在learning-service中统一管理AI功能的权限、限流、监控等
5. **部署简化**：减少了一个独立的Java AI微服务

## 下一步工作

1. 完善learning-service中的AI功能测试
2. 在其他微服务中集成CommonAIServiceClient
3. 更新API文档和接口说明
4. 配置微服务间的网络通信
5. 添加AI调用的监控和日志
