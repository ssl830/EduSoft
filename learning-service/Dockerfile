# 使用官方的 OpenJDK 21 作为基础镜像
FROM openjdk:21-jdk-slim

# 设置工作目录
WORKDIR /app

# 将应用的 jar 文件添加到容器中
COPY target/learning-service-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8084

# 运行应用的命令
ENTRYPOINT ["java","-jar","app.jar"]
