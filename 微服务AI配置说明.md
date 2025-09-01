# 各微服务AI配置示例

## learning-service配置 (application.yml)

```yaml
# learning-service 配置
server:
  port: 8084

spring:
  application:
    name: learning-service
  datasource:
    url: jdbc:mysql://localhost:3306/learning_db
    username: root
    password: password

# AI服务配置
ai:
  service:
    url: http://localhost:8000  # Python AI微服务地址

# 其他微服务地址（如果需要调用）
services:
  user-service:
    url: http://localhost:8081
  course-service:
    url: http://localhost:8082
  content-service:
    url: http://localhost:8083
```

## course-service配置 (application.yml)

```yaml
# course-service 配置
server:
  port: 8082

spring:
  application:
    name: course-service
  datasource:
    url: jdbc:mysql://localhost:3306/course_service_db
    username: root
    password: password

# 其他微服务地址
services:
  learning-service:
    url: http://localhost:8084  # 用于AI功能调用
  user-service:
    url: http://localhost:8081
  content-service:
    url: http://localhost:8083

# AI功能配置（通过learning-service）
learning:
  service:
    url: http://localhost:8084
```

## user-service配置 (application.yml)

```yaml
# user-service 配置
server:
  port: 8081

spring:
  application:
    name: user-service
  datasource:
    url: jdbc:mysql://localhost:3306/user_service_db
    username: root
    password: password

# 其他微服务地址
services:
  learning-service:
    url: http://localhost:8084  # 用于AI功能调用
  course-service:
    url: http://localhost:8082
  content-service:
    url: http://localhost:8083

# AI功能配置（通过learning-service）
learning:
  service:
    url: http://localhost:8084
```

## content-service配置 (application.yml)

```yaml
# content-service 配置
server:
  port: 8084

spring:
  application:
    name: content-service
  datasource:
    url: jdbc:mysql://localhost:3306/content_service_db
    username: root
    password: password

# 其他微服务地址
services:
  learning-service:
    url: http://localhost:8084  # 用于AI功能调用
  user-service:
    url: http://localhost:8081
  course-service:
    url: http://localhost:8082

# AI功能配置（通过learning-service）
learning:
  service:
    url: http://localhost:8084
```

## Python AI微服务保持不变

Python AI微服务继续在端口8000提供服务，不需要修改配置。

## 启动顺序建议

1. 首先启动Python AI微服务 (端口8000)
2. 启动user-service (端口8081)
3. 启动learning-service (端口8082) - 需要连接Python AI微服务
4. 启动course-service (端口8083)
5. 启动content-service (端口8084)
6. 启动api-gateway (如果有的话)

## 网络配置注意事项

1. 确保各微服务之间网络互通
2. 如果使用Docker，需要配置正确的网络
3. 在生产环境中，建议使用服务发现（如Eureka、Consul等）
4. 考虑添加负载均衡和熔断机制
